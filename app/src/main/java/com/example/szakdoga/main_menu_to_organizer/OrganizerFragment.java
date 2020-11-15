package com.example.szakdoga.main_menu_to_organizer;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.szakdoga.R;
import com.example.szakdoga.main_menu_to_participants.RecycleAdapter;
import com.example.szakdoga.signin_up.SignIn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class OrganizerFragment extends Fragment {

    TextView name,email;
    EditText addEmail;
    Button logoutButton,activateButton,phoneBookButton,calculatorButton,emailsButton;
    RecyclerView recyclerView;
    ImageView info;
    private FirebaseAuth fAuth;
    private FirebaseFirestore firestore;
    private ArrayList<UserModel> organizers;
    private ArrayList<UserModel> users;
    private OrganizerAdapter adapter;
    public static final String CALCULATOR_PACKAGE ="com.android.calculator2";
    public static final String CALCULATOR_CLASS ="com.android.calculator2.Calculator";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v;
        v=inflater.inflate(R.layout.fragment_organizer, container, false);
        fAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();
        name=v.findViewById(R.id.organizer_name);
        email=v.findViewById(R.id.organizer_email);
        addEmail=v.findViewById(R.id.add_email);
        logoutButton=v.findViewById(R.id.logoutorg);
        activateButton=v.findViewById(R.id.activate_button);
        phoneBookButton=v.findViewById(R.id.phone_book);
        calculatorButton=v.findViewById(R.id.calculator);
        emailsButton=v.findViewById(R.id.myemails);
        recyclerView=v.findViewById(R.id.recyclerOrganizers);
        info=v.findViewById(R.id.infoToast);
        organizers=new ArrayList<>();
        users=new ArrayList<>();

        final DocumentReference docRef = firestore.collection("users").document(fAuth.getCurrentUser().getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    name.setText("Welcome "+documentSnapshot.getString("firstName")+"!");
                    email.setText(documentSnapshot.getString("email"));

                } else {
                    Log.d(TAG, "No such document");
                }

            }
        });

        final CollectionReference db=firestore.collection("users");
        db.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                users.add(new UserModel(document.getId(),document.getString("email")));
                                if (document.getBoolean("organizer")){
                                    organizers.add(new UserModel(document.getId(),
                                            document.getString("firstName"),
                                            document.getString("lastName"),
                                            document.getString("email"),
                                            document.getBoolean("organizer")));
                                    adapter.notifyDataSetChanged();
                                }}
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        adapter=new OrganizerAdapter(v.getContext(),organizers);
        recyclerView.setAdapter(adapter);

        activateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=addEmail.getText().toString().trim();
                if (TextUtils.isEmpty(email)){
                    addEmail.setError("Email is required.");
                }
                else {
                    String ID=null;
                    for (int i=0;i<users.size();i++){
                        if (users.get(i).getEmail().equals(email))
                            ID=users.get(i).getID();
                    }
                    if (ID==null)
                    {
                        addEmail.setError("There is no user with this email");
                    }
                    else {
                        DocumentReference reference=firestore.collection("users").document(ID);
                        reference.update("organizer",true);
                    }
                }
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth.signOut();
                startActivity(new Intent(v.getContext(), SignIn.class));
                getActivity().finish();
            }
        });
        phoneBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL));
            }
        });
        calculatorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.setComponent(new ComponentName(
                        CALCULATOR_PACKAGE,
                        CALCULATOR_CLASS));
                startActivity(intent);
            }
        });
        emailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                startActivity(intent);
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"You can give someone permission if he or she is registered.",Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }
}
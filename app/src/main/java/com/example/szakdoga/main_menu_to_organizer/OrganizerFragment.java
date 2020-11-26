package com.example.szakdoga.main_menu_to_organizer;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.szakdoga.R;
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
import java.util.Objects;

import static android.content.ContentValues.TAG;

/**
 * Profil fragment, ahol a szervező neve, email címe jelenik meg.
 * Kijelentkezés gomb, különböző segítségek, mint a telefonkönyv, számológép,
 * emailek megnyitása lehetséges innen.
 * Lehetőség van szervezői státuszt adni már regisztrált felhasználóknak,
 * illetve vissza is lehet vonni az engedélyt.
 */
public class OrganizerFragment extends Fragment {
    //Változók
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
        organizers=new ArrayList<>();
        users=new ArrayList<>();

        //Layout elemek hozzárendelése
        setType(v);

        //Lekérjük a Firestore-ból a felhasználó adatait(név és email).
        final DocumentReference docRef = firestore.collection("users").document(Objects.requireNonNull(fAuth.getCurrentUser()).getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
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

        //Lekérjük a szervezők és felhasználók listáját, előbbit pedig megjeleníti majd egy recyclerView
        final CollectionReference db=firestore.collection("users");
        db.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                users.add(new UserModel(document.getId(),document.getString("firstName"),document.getString("lastName"),document.getString("email")));
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

        //RecyclerView adapterének beállítása
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        adapter=new OrganizerAdapter(v.getContext(),organizers);
        recyclerView.setAdapter(adapter);

        //Szervezői engedélyt adhatunk valakinek, a beírt email cím alapján, ha van ilyen regisztrált felhasználó
        activateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Ellenőrzés: létezik-e a megadott email címmel regisztrált felhasználó
                final String email=addEmail.getText().toString().trim();
                if (TextUtils.isEmpty(email)){
                    addEmail.setError(getString(R.string.email_is_required));
                }
                else {
                    String ID=null;
                    for (int i=0;i<users.size();i++){
                        if (users.get(i).getEmail().equals(email))
                            ID=users.get(i).getID();
                    }
                    if (ID==null)
                    {
                        addEmail.setError(getString(R.string.no_user));
                    }
                    else {
                        DocumentReference reference=firestore.collection("users").document(ID);
                        final String id=ID;
                        reference.update("organizer",true).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                for (int i=0;i<users.size();i++){
                                    if (users.get(i).getID().equals(id)){
                                        organizers.add(new UserModel(users.get(i).getID(),users.get(i).getFirstName(),users.get(i).getLastName(),users.get(i).getEmail(),true));
                                        adapter.notifyDataSetChanged();
                                    } } }});
                    } } }});

        //Kijelentkező gomb
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth.signOut();
                startActivity(new Intent(v.getContext(), SignIn.class));
                Objects.requireNonNull(getActivity()).finish(); }
        });

        //Telefonkönyv
        phoneBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL));
            }
        });

        //Számológép
        calculatorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.setComponent(new ComponentName(CALCULATOR_PACKAGE, CALCULATOR_CLASS));
                startActivity(intent);
            }
        });

        //Emailek
        emailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                startActivity(intent);
            }
        });

        //Információ
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),getString(R.string.give_permission),Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }

    //Layout elemek hozzárendelése a változókhoz
    private void setType(View v) {
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
    }

    @Override
    public void onDestroy() {
        organizers.clear();
        users.clear();
        super.onDestroy();
        System.gc();
    }
}
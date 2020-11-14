package com.example.szakdoga.main_menu_to_participants;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.szakdoga.R;
import com.example.szakdoga.main_menu_to_organizer.EventModel;
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

public class ProfileFragment extends Fragment {

    Button logout;
    TextView userText;
    TextView emailText;
    private FirebaseAuth fAuth;
    private FirebaseFirestore firestore;
    private ArrayList<String> arrList;
    private ArrayList<EventModel> events;
    private RecyclerView recyclerView1;
    private RecycleAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v;
        v=inflater.inflate(R.layout.fragment_profile, container, false);
        recyclerView1=v.findViewById(R.id.recyclerViewForEvent11);
        logout=v.findViewById(R.id.logoutbtn);
        userText=v.findViewById(R.id.textViewUserName);
        emailText=v.findViewById(R.id.user_email);

        fAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();
        arrList= new ArrayList<>();
        events=new ArrayList<>();

        final DocumentReference docRef = firestore.collection("users").document(fAuth.getCurrentUser().getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    arrList = (ArrayList) documentSnapshot.get("likes");
                    userText.setText("Welcome "+documentSnapshot.getString("firstName")+"!");
                    emailText.setText(documentSnapshot.getString("email"));

                } else {
                    Log.d(TAG, "No such document");
                }

            }
        });

        final CollectionReference db=firestore.collection("events");
        db.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (arrList.contains(document.getId())){
                                    events.add(new EventModel(document.getId(),
                                            document.getString("Image"),
                                            document.getString("Title"),
                                            document.getString("Time"),
                                            document.getGeoPoint("GeoPoint"),
                                            document.getString("Description")));
                                    adapter.notifyDataSetChanged();
                                }}
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });



        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        recyclerView1.setLayoutManager(layoutManager);
        adapter=new RecycleAdapter(v.getContext(),events);
        recyclerView1.setAdapter(adapter);



        //Kijelentkezés
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth.signOut();
                startActivity(new Intent(v.getContext(),SignIn.class));
                getActivity().finish();
            }
        });

        return v;
    }
}
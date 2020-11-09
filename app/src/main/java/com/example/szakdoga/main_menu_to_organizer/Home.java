package com.example.szakdoga.main_menu_to_organizer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.szakdoga.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Home extends AppCompatActivity {

    private TextView textName,textEmail;
    private Button btnLogout;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);

        textName=findViewById(R.id.textNameWelcome);
        textEmail=findViewById(R.id.textEmailWelcome);
        btnLogout=findViewById(R.id.buttonLogOut);

        fAuth=FirebaseAuth.getInstance();
        /*fStore=FirebaseFirestore.getInstance();
        userID=fAuth.getCurrentUser().getUid();

        DocumentReference documentReference=fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(Home.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null) {
                    textEmail.setText(value.getString("email"));
                }
                if (value != null) {
                    textName.setText(value.getString("firstName"));
                }

            }
        });*/
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        finish();

    }
}
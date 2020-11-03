package com.example.szakdoga.signin_up;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.szakdoga.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;

/**
 * Regisztrációs felület:
 * Név,email,jelszó
 * A Firebase elmenti a felhasználót és adatait elmenti a Firestore adatbázisában
 */
public class SignUp extends AppCompatActivity {

    private Button signUpBtn;
    private EditText nameFirstText,nameLastText,emailText,passwordText,confirmPasswordText;
    private TextView signInText;
    private ProgressBar progressBar;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up);

        //Vezérlők beállítása
        setType();

        //Firebase és Firestore
        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();

        // "Already have an account? Sign in!" szövegre kattintva visszaugrunk a bejelentkezéshez.
        signInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              finish();
            }
        });

        // Ha kitöltöttük helyesen a mezőket akkor regisztrál minket az app.
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String nameFirst=nameFirstText.getText().toString().trim();
                final String nameLast=nameLastText.getText().toString().trim();
                final String email=emailText.getText().toString().trim();
                final String password=passwordText.getText().toString().trim();
                final String confirmPassword=confirmPasswordText.getText().toString().trim();

                // Ellenőrzés :
                if (TextUtils.isEmpty(nameFirst)){
                    nameFirstText.setError("First name is required.");
                    return;
                }
                if (TextUtils.isEmpty(nameLast)){
                    nameLastText.setError("Last name is required.");
                    return;
                }
                if (TextUtils.isEmpty(email)){
                    emailText.setError("Email is required.");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    passwordText.setError("Password is required.");
                    return;
                }
                if (TextUtils.isEmpty(confirmPassword)){
                    confirmPasswordText.setError("Password is required.");
                    return;
                }
                if (password.length()<6){
                    passwordText.setError("Password must be >= 6 characters.");
                    return;
                }
                if (!confirmPassword.equals(password)){
                    confirmPasswordText.setError("Confirm password and password do not match");
                }
                if (!TextUtils.isEmpty(nameFirst) & !TextUtils.isEmpty(nameLast) & !TextUtils.isEmpty(email) & !TextUtils.isEmpty(password) & !TextUtils.isEmpty(confirmPassword) &  password.length()>6 & confirmPassword.equals(password)){
                progressBar.setVisibility(View.VISIBLE);

                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            final FirebaseUser user=fAuth.getCurrentUser();
                            user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(SignUp.this,"Verification email has been sent.",Toast.LENGTH_SHORT).show();
                                    userID=user.getUid();
                                    DocumentReference documentReference=fStore.collection("users").document(userID);
                                    Map<String,Object> user=new HashMap<>();
                                    user.put("firstName",nameFirst);
                                    user.put("lastName",nameLast);
                                    user.put("email",email);
                                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("TAG","onSuccess: user profile is created "+ userID);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("TAG","Error!"+e.toString());
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("TAG","onFailure: Email not sent"+e.getMessage());
                                }
                            });

                            Toast.makeText(SignUp.this,"User created. Verify your email!",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUp.this, SignIn.class));
                        }
                        else{
                            Toast.makeText(SignUp.this,"Error !"+ task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }}
        });
    }

    //vezérlők beállítása
    private void setType() {
        nameFirstText=findViewById(R.id.nameFirst);
        nameLastText=findViewById(R.id.nameLast);
        emailText=findViewById(R.id.emailEdit);
        passwordText=findViewById(R.id.passwordEdit);
        confirmPasswordText=findViewById(R.id.editTextConfirmPass);
        signInText=findViewById(R.id.textSignIn);
        signUpBtn=findViewById(R.id.btnSignIn);
        progressBar=findViewById(R.id.progressBar);
    }
}
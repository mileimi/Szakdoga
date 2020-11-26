package com.example.szakdoga.signin_up;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Regisztrációs felület:
 * Név,email,jelszó
 * A Firebase elmenti a felhasználót és adatait elmenti a Firestore adatbázisában
 */
public class SignUp extends AppCompatActivity {

    //Változók
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
                //Név
                if (TextUtils.isEmpty(nameFirst)){
                    nameFirstText.setError(getString(R.string.firsNameReq));
                    return;
                }
                //Név
                if (TextUtils.isEmpty(nameLast)){
                    nameLastText.setError(getString(R.string.lastNameReq));
                    return;
                }
                //Email
                if (TextUtils.isEmpty(email)){
                    emailText.setError(getString(R.string.email_is_required));
                    return;
                }
                //Jelszó
                if (TextUtils.isEmpty(password)){
                    passwordText.setError(getString(R.string.password_is_required));
                    return;
                }
                //Jelszó újra
                if (TextUtils.isEmpty(confirmPassword)){
                    confirmPasswordText.setError(getString(R.string.password_is_required));
                    return;
                }
                //A jelszó 6-nál több karakterből kell álljon
                if (password.length()<6){
                    passwordText.setError(getString(R.string.password_must_be));
                    return;
                }
                //Ha nem egyezik a 2 jelszó mező tartalma
                if (!confirmPassword.equals(password)){
                    confirmPasswordText.setError(getString(R.string.two_pass_not_eq));
                }
                //Ha minden helyesen van kitöltve:
                if (!TextUtils.isEmpty(nameFirst) & !TextUtils.isEmpty(nameLast) & !TextUtils.isEmpty(email) & !TextUtils.isEmpty(password) & !TextUtils.isEmpty(confirmPassword) &  password.length()>6 & confirmPassword.equals(password)){
                progressBar.setVisibility(View.VISIBLE);

                //Regisztráció
                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            final FirebaseUser user=fAuth.getCurrentUser();
                            //Email megerősítő küldése a felhasználónak
                            assert user != null;
                            user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(SignUp.this,getString(R.string.verification_sent),Toast.LENGTH_SHORT).show();
                                    //Eltároljuk a felhasználó adatait a Firestore-ba
                                    userID=user.getUid();
                                    DocumentReference documentReference=fStore.collection("users").document(userID);
                                    Map<String,Object> user=new HashMap<>();
                                    user.put("firstName",nameFirst);
                                    user.put("lastName",nameLast);
                                    user.put("email",email);
                                    user.put("likes", Collections.emptyList());
                                    user.put("organizer",false);
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
                                    Toast.makeText(SignUp.this,getString(R.string.verification_not_sent),Toast.LENGTH_SHORT).show();
                                }
                            });

                            Toast.makeText(SignUp.this,getString(R.string.user_created),Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUp.this, SignIn.class));
                        }
                        else{
                            Toast.makeText(SignUp.this,getString(R.string.errorLog)+ Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_SHORT).show();
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
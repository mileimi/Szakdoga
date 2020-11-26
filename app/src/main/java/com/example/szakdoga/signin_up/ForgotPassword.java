package com.example.szakdoga.signin_up;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.szakdoga.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Elfelejtett jelszó esetén meg kell adnunk a regisztrált email címünket,
 * amire kapunk egy emailt, és megváltoztathatjuk a jelszavunkat
 */
public class ForgotPassword extends AppCompatActivity {

    //Változók
    Button sendBtn,backBtn;
    EditText emailText;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_forgot_password);

        sendBtn=findViewById(R.id.btnSend);
        backBtn=findViewById(R.id.backBTN);
        emailText=findViewById(R.id.emailEditText12);
        fAuth=FirebaseAuth.getInstance();

        //Visszalépés
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Ha a küldés gombra kattintunk akkor a megadott email címre kapunk egy jelszó megváltoztató üzenetet
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=emailText.getText().toString().trim();
                //Megadott email ellenőrzése
                if (TextUtils.isEmpty(email)) {
                    emailText.setError(getString(R.string.email_is_required));
                }
                else{
                fAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ForgotPassword.this,getString(R.string.reset_link),Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ForgotPassword.this,getString(R.string.reset_link_not)+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }}
        });
    }
}
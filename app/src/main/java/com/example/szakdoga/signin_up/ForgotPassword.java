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

    Button sendBtn;
    EditText emailText;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_forgot_password);

        sendBtn=findViewById(R.id.btnSend);
        emailText=findViewById(R.id.emailEditText12);
        fAuth=FirebaseAuth.getInstance();

        // Ha a küldés gombra kattintunk
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=emailText.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    emailText.setError("Email is required.");
                }
                else{
                fAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ForgotPassword.this,"Reset link sent to your email.",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ForgotPassword.this,"Error! Reset link is NOT sent."+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }}
        });
    }
}
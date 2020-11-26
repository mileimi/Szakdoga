package com.example.szakdoga.signin_up;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.szakdoga.R;
import com.example.szakdoga.main_menu_to_organizer.ChooseFestival;
import com.example.szakdoga.main_menu_to_participants.Festivals;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

/**
 * Bejelentkező felület
 * Regisztrált email címmel és jeészóval, illetve Facebook fiókkal való belépés
 * Ha még nem regisztrál tovább léphet a regisztrációs felületre
 * Elfelejtett jelszó esetén pedig jelszó visszaállító emailt küld a regisztrált email címre
 */
public class SignIn extends AppCompatActivity {

    //Változók
    private Button signInBtn;
    private EditText emailText,passwordText;
    private TextView forgotPasswordText, createAccountText;
    private ProgressBar progressBar;
    private ImageView background;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fireStore;
    private String UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.sign_in_activity);

        //Vezérlők beállítása
        setType();

        //Firebase elérése
        fAuth=FirebaseAuth.getInstance();
        fireStore=FirebaseFirestore.getInstance();

        //Háttéranimáció beállítása
        Animation backgroundAnim = AnimationUtils.loadAnimation(this, R.anim.background_anim);
        background.setAnimation(backgroundAnim);

        //Elfelejtett jelszó
        forgotPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToForgotPassword=new Intent(SignIn.this, ForgotPassword.class);
                startActivity(intentToForgotPassword);
            }
        });

        //Regisztráció
        createAccountText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToSignUp=new Intent(SignIn.this, SignUp.class);
                startActivity(intentToSignUp);
            }
        });

        //Bejelentkezés és a kitöltött mezők(email, jelszó) ellenőrzése
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=emailText.getText().toString().trim();
                String password=passwordText.getText().toString().trim();

                //Ha az email mező üres
                if (TextUtils.isEmpty(email)){
                    emailText.setError(getString(R.string.email_is_required));
                    return;
                }
                //Ha a jelszó mező üres
                if (TextUtils.isEmpty(password)){
                    passwordText.setError(getString(R.string.password_is_required));
                    return;
                }
                //Ha a megadott mezők ki vannak töltve
                if (!TextUtils.isEmpty(email) & !TextUtils.isEmpty(password)){
                progressBar.setVisibility(View.VISIBLE);
                //Bejelentkezés(ha pontos adatokat adott meg és megerősítette az email címét)
                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            if(Objects.requireNonNull(fAuth.getCurrentUser()).isEmailVerified()){
                            Toast.makeText(SignIn.this,getString(R.string.logged),Toast.LENGTH_SHORT).show();
                            UID= fAuth.getCurrentUser().getUid();
                            //Azonosítjuk hogy a felhasználó egy szervező-e
                             fireStore.collection("users").document(UID)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()){
                                                if (Objects.requireNonNull(task.getResult()).contains("organizer")){
                                                    if ( task.getResult().getBoolean("organizer")){
                                                        startActivity(new Intent(SignIn.this, ChooseFestival.class));
                                                    }else{
                                                        startActivity(new Intent(SignIn.this, Festivals.class));
                                                    }
                                                }else{
                                                    startActivity(new Intent(SignIn.this, Festivals.class));
                                                }
                                                finish();
                                            }
                                        }
                                    });
                            progressBar.setVisibility(View.GONE);
                        }else{
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(SignIn.this,getString(R.string.email_verification),Toast.LENGTH_SHORT).show();
                            }}
                        else{
                            Toast.makeText(SignIn.this,getString(R.string.errorLog)+ Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }}
        });
    }

    //Vezérlők beállítása
    private void setType() {
        emailText=findViewById(R.id.editTextTextEmailAddress);
        passwordText=findViewById(R.id.editTextTextPassword);
        signInBtn = findViewById(R.id.btnSignIn);
        forgotPasswordText = findViewById(R.id.textForgotPass);
        createAccountText = findViewById(R.id.textCreateAcc);
        progressBar=findViewById(R.id.progressBar2);
        background = findViewById(R.id.imageView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
    }
}

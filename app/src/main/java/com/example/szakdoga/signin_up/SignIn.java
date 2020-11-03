package com.example.szakdoga.signin_up;

import android.content.Intent;
import android.os.Bundle;
import android.telecom.Call;
import android.text.TextUtils;
import android.util.Log;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.szakdoga.main_menu.Home;
import com.example.szakdoga.main_menu.NavigationBar;
import com.example.szakdoga.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.util.Arrays;


/**
 * Bejelentkező felület
 * Regisztrált email címmel és jeészóval, illetve Facebook fiókkal való belépés
 * Ha még nem regisztrál tovább léphet a regisztrációs felületre
 * Elfelejtett jelszó esetén pedig jelszó visszaállító emailt küld a regisztrált email címre
 */
public class SignIn extends AppCompatActivity {

    private Button signInBtn;
    private EditText emailText,passwordText;
    private TextView forgotPasswordText, createAccountText;
    private ProgressBar progressBar;
    private ImageView background;
    private FirebaseAuth fAuth;

   /* private CallbackManager callbackManager;
    private LoginButton loginButton;
    private static final String TAG="FacebookAuthentication";
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.sign_in_activity);
        //Vezérlők beállítása
        setType();

        //Firebase elérése
        fAuth=FirebaseAuth.getInstance();

        //Háttéranimáció beállítása
        Animation backgroundAnim = AnimationUtils.loadAnimation(this, R.anim.background_anim);
        background.setAnimation(backgroundAnim);


        /*//Facebook
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        loginButton=findViewById(R.id.login_button);
        callbackManager=CallbackManager.Factory.create();
        loginButton.setReadPermissions(Arrays.asList("email","public_profile"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG,"onSuccess"+loginResult);
                handleFacebookToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG,"onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG,"onError"+error);
            }
        });
*/


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

                if (TextUtils.isEmpty(email)){
                    emailText.setError("Email is required.");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    passwordText.setError("Password is required.");
                    return;
                }

                if (!TextUtils.isEmpty(email) & !TextUtils.isEmpty(password)){
                    progressBar.setVisibility(View.VISIBLE);
                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            if(fAuth.getCurrentUser().isEmailVerified()){
                            Toast.makeText(SignIn.this,"Logged in successfully!",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignIn.this, NavigationBar.class));
                            progressBar.setVisibility(View.GONE);
                            finish();
                        }else{
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(SignIn.this,"Please verify your email.",Toast.LENGTH_SHORT).show();
                            }}
                        else{
                            Toast.makeText(SignIn.this,"Error !"+ task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }}
        });


    }

  /* private void handleFacebookToken(AccessToken accessToken) {
        Log.d(TAG,"handleFacebookToken"+accessToken);
        AuthCredential credential= FacebookAuthProvider.getCredential(accessToken.getToken());
        fAuth.signInWithCredential(credential).addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Log.d(TAG,"sign in with credential: Successful");

                }
                else{
                    Log.d(TAG,"sign in with credential: Failure",task.getException());
                    Toast.makeText(SignIn.this,"Authentication Failed",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    public void goMainScreen(){
        Intent intent=new Intent(SignIn.this, Home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
    }
*/
    //Vezérlők beállítása
    private void setType() {
        emailText=findViewById(R.id.editTextTextEmailAddress);
        passwordText=findViewById(R.id.editTextTextPassword);
        signInBtn = findViewById(R.id.btnSignIn);
        forgotPasswordText = findViewById(R.id.textForgotPass);
        createAccountText = findViewById(R.id.textCreateAcc);
        background = findViewById(R.id.imageView);
        progressBar=findViewById(R.id.progressBar2);
    }
}
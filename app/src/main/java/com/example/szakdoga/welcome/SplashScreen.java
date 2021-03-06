package com.example.szakdoga.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.szakdoga.R;
import com.example.szakdoga.signin_up.SignIn;
/**
 * Nyitó képernyő ami 3,2 másodpercig látható.
 * Animált TextView-t és ImageView-t használ.
 * Az animációk a resource/anim mappában találhatóak.
 */
public class SplashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        //Változók
        TextView text1;
        Animation anim1,anim2;
        ImageView image;
        int DELAY=3200;

        //Layout elemek hozzárendelése
        text1=findViewById(R.id.text_view_splash);
        image=findViewById(R.id.imageView222);

        //Animációk betöltése
        anim1= AnimationUtils.loadAnimation(this,R.anim.welcome_image_anim);
        anim2=AnimationUtils.loadAnimation(this,R.anim.welcome_text_anim);

        //Animáció hozzárendelés a kiválasztott elemekhez
        image.setAnimation(anim1);
        text1.setAnimation(anim2);

        //Késleltetés beállítás után ugrik a bejelentkezéshez az app
       new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intentToSignIn=new Intent(SplashScreen.this, SignIn.class);
                startActivity(intentToSignIn);
                overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                finish();
            }
            }, DELAY);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
    }
}
package com.example.szakdoga.main_menu_to_organizer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.szakdoga.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
/**
 * Esemény hátterének a beállítása.
 * Lehetőség van a már feltöltött képekből választani,
 * valamint tölthetünk fel a készülékünkről is képet a Storage-ba
 */

public class EventPhotoSetting extends AppCompatActivity {

    //Változók
    private Button uploadButton;
    private Button cancelButton;
    private Button setButton;
    private ViewPager viewPager;
    private WormDotsIndicator dot3;
    private MyPagerAdapter pagerAdapter;
    private ArrayList<String> images;
    private FirebaseStorage storage;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_photo_setting);
        storage=FirebaseStorage.getInstance();
        firestore=FirebaseFirestore.getInstance();

        //Layout elemek hozzárendelése
        uploadButton=findViewById(R.id.uploadButton);
        cancelButton=findViewById(R.id.cancelbtn);
        setButton=findViewById(R.id.set_background);
        viewPager=findViewById(R.id.viewPager);
        dot3=findViewById(R.id.wormdot);
        images=new ArrayList<>();

        //Megkapjuk a kiválasztott esemény ID-ját, ez alapján fogja update-elni
        Intent intent=getIntent();
        final String EventID=intent.getStringExtra("EventID");
        final String festID=intent.getStringExtra("FestiID");

        //Lekérjük a már feltöltött képeket a Storage-ból
        final StorageReference imagesRef=storage.getReferenceFromUrl("gs://szakdoga-11e95.appspot.com/");
        imagesRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference item : listResult.getItems()) {
                    item.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()){
                                if (!images.contains(Objects.requireNonNull(task.getResult()).toString())) {
                                    images.add(task.getResult().toString());
                                }pagerAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),getString(R.string.photo_downloading),Toast.LENGTH_SHORT).show();
            }

        });

         //ViewPager segítségével lehet lapozgatni a képek között
         viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
             //Ha az nem lapozunk akkor az első helyet állítjuk be pozíciónak
             boolean first=true;
             @Override
             public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                 if (first && positionOffset == 0 && positionOffsetPixels == 0){
                     onPageSelected(0);
                     first = false;
             }}
             //Ha kiválasztottunk egy képet és rákattintunk a Set gombra, akkor az elmetésre kerül az adatbázisban
             @Override
             public void onPageSelected(final int position) {
                 setButton.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         DocumentReference reference=firestore.collection("festivals").document(festID).collection("events").document(EventID);
                         reference.update("Image",images.get(position)).addOnSuccessListener(new OnSuccessListener<Void>() {
                             @Override
                             public void onSuccess(Void aVoid) {
                                 finish();
                             }
                         }).addOnFailureListener(new OnFailureListener() {
                             @Override
                             public void onFailure(@NonNull Exception e) {
                                 Toast.makeText(EventPhotoSetting.this,getString(R.string.errorLog),Toast.LENGTH_SHORT).show();
                             }
                         });
                     }
                 });
             }
             @Override
             public void onPageScrollStateChanged(int state) {

             }
         });

         //Saját kép feltöltése
         uploadButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 choosePicture();
             }
         });

         //Kilépés
         cancelButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                finish();
             }
         });

         //Pager adapter beállítása
         pagerAdapter=new MyPagerAdapter(getApplicationContext(),images);
         viewPager.setAdapter(pagerAdapter);
         dot3.setViewPager(viewPager);
    }

    //Megnyitja a képfájlok mappát az eszközön
    private void choosePicture() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,231);
    }

    //Az eszközön lévő kép megkapása
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 231 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            Log.d("Fileupload12", "File: " + data.getData().toString());
            uploadPicture(imageUri);
            images.add(imageUri.toString());
            pagerAdapter.notifyDataSetChanged();
        }

    }

    //kÉP FELTÖLTÉSE
    private void uploadPicture(Uri imageUri) {
        final String randomKey= UUID.randomUUID().toString();
        StorageReference reference=storage.getReference(randomKey);
        reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Toast.makeText(context,"Picture uploaded",Toast.LENGTH_SHORT).show();
                Log.d("Fileupload","File uploading");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(context,"Something is wrong while uploading the picture",Toast.LENGTH_SHORT).show();
                Log.d("Fileupload1", "File not uploading " + e.getMessage());
            }
        });}
    }

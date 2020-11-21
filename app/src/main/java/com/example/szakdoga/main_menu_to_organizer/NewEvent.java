package com.example.szakdoga.main_menu_to_organizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.szakdoga.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.baidu.mapapi.BMapManager.getContext;

public class NewEvent extends AppCompatActivity {

    private EditText newTitle, newDescription;
    private TextView newDate;
    private Button newCreateBtn, newBackBtn;
    private MapView newMap;
    private GoogleMap mGoogleMap;
    private FirebaseFirestore firestore;
    private CollectionReference collectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        firestore=FirebaseFirestore.getInstance();
        collectionReference=firestore.collection("events");

        newTitle=findViewById(R.id.TitleEvent);
        newDescription=findViewById(R.id.DescriptionEvent);
        newDate=findViewById(R.id.DateEvent);
        newCreateBtn=findViewById(R.id.EventCreate);
        newBackBtn=findViewById(R.id.EventBack);
        newMap=findViewById(R.id.MapEvent);
        final GeoPoint[] geoPoint = new GeoPoint[1];

        newMap.onCreate(null);
        newMap.onResume();
        newMap.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                MapsInitializer.initialize(getApplicationContext());

                mGoogleMap=googleMap;
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                CameraPosition camera= CameraPosition.builder().target(new LatLng(47.52,19.042)).zoom(11).bearing(0).tilt(45).build();
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(camera));

                mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        mGoogleMap.clear();
                        mGoogleMap.addMarker(new MarkerOptions().position(latLng));
                        geoPoint[0] =new GeoPoint(latLng.latitude,latLng.longitude);

                    }
                });

            }
        });


        newBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        newDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v,newDate);
            }
        });
        newCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeoPoint geo=new GeoPoint(geoPoint[0].getLatitude(),geoPoint[0].getLongitude());
                Map<String,Object> event=new HashMap<>();
                event.put("Title",newTitle.getText().toString());
                event.put("Description",newDescription.getText().toString());
                event.put("Time",newDate.getText().toString());
                event.put("GeoPoint", geo);
                event.put("Image","");
                collectionReference.add(event).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("TAG","onSuccess: new event is created ");
                        startActivity(new Intent(NewEvent.this,NavigationBar.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG","onFailure: new event is not created "+e.getMessage());
                    }
                });
            }
        });
    }


    private void showDatePickerDialog(final View v, final TextView t) {
        final Calendar calendar=Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

                TimePickerDialog.OnTimeSetListener timeSetListener=new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        calendar.set(Calendar.MINUTE,minute);

                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yy-MM-dd HH:mm");
                        t.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                };
                new TimePickerDialog(v.getContext(),timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false).show();
            }
        };
        new DatePickerDialog(v.getContext(),dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
}
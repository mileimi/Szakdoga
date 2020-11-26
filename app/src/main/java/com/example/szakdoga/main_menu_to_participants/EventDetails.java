package com.example.szakdoga.main_menu_to_participants;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.szakdoga.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
/**
 * A listából ha rákattintunk egy eseményre, akkor az külön activityn jelenik meg,
 * a hozzá tartozó részletekkel, térképen jelölve a pontos helyét.
 */
public class EventDetails extends AppCompatActivity {

    private TextView titleE;
    private TextView date;
    private TextView descriptionE;
    private MapView map;
    private GoogleMap mGoogleMap;
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        //Layout elemek beállítása
        setType();

        //Megkapja a kiválasztott esemény adatait majd megjeleníti azokat
        Intent intent=getIntent();
        final String title=intent.getStringExtra("EventTitle");
        String description=intent.getStringExtra("EventDescription");
        String time=intent.getStringExtra("EventTime");
        final Double latitude = intent.getDoubleExtra("latitude", 34.34);
        final Double longitude = intent.getDoubleExtra("longitude", 34.34);


        titleE.setText(title);
        date.setText(time);
        descriptionE.setText(description);
        descriptionE.setMovementMethod(new ScrollingMovementMethod());

        map.onCreate(null);
        map.onResume();
        map.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                MapsInitializer.initialize(getApplicationContext());

                mGoogleMap=googleMap;
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).title(title));

                CameraPosition camera= CameraPosition.builder().target(new LatLng(latitude,longitude)).zoom(14).bearing(0).tilt(45).build();
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(camera));

            }
        });

        //Visszalépés
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    //Layout elemek beállítása
    private void setType() {
        titleE=findViewById(R.id.event_title);
        date=findViewById(R.id.event_date);
        descriptionE=findViewById(R.id.event_description);
        map=findViewById(R.id.mapView11);
        back=findViewById(R.id.back_button);
    }
}
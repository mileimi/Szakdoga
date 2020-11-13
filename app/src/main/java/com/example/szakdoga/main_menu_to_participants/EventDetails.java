package com.example.szakdoga.main_menu_to_participants;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.szakdoga.R;
import com.example.szakdoga.main_menu_to_organizer.EventModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class EventDetails extends AppCompatActivity {

    private TextView title;
    private TextView date;
    private TextView description;
    private MapView map;
    private GoogleMap mGoogleMap;
    private Double latitude;
    private Double longitude;
    private Button back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        title=findViewById(R.id.event_title);
        date=findViewById(R.id.event_date);
        description=findViewById(R.id.event_description);
        map=findViewById(R.id.mapView11);
        back=findViewById(R.id.back_button);

        Intent intent=getIntent();
        EventModel actualEvent=intent.getParcelableExtra("Event");
        latitude=intent.getDoubleExtra("latitude",34.34);
        longitude=intent.getDoubleExtra("longitude",34.34);
        setDetails(title,date,description,map,actualEvent,latitude,longitude);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void setDetails(TextView title, TextView date, TextView description, MapView map, final EventModel actualEvent, final Double latitude, final Double longitude) {
        title.setText(actualEvent.getTitle());
        date.setText(actualEvent.getTime());
        description.setText(actualEvent.getDescription());
        description.setMovementMethod(new ScrollingMovementMethod());

        map.onCreate(null);
        map.onResume();
        map.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                MapsInitializer.initialize(getApplicationContext());

                mGoogleMap=googleMap;
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).title(actualEvent.getTitle()));

                CameraPosition camera= CameraPosition.builder().target(new LatLng(latitude,longitude)).zoom(14).bearing(0).tilt(45).build();
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(camera));

            }
        });

    }
}
package com.example.szakdoga.main_menu_to_organizer;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.szakdoga.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.util.AsyncQueue;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;


public class MapFragment extends Fragment {

    GoogleMap mGoogleMap;
    MapView mapView;
    View mView;
    ArrayList<EventModel> events;
    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db=FirebaseFirestore.getInstance();
        collectionReference=db.collection("events");

        events=new ArrayList<>();
        db.collection("events")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("FIRESTOREdata", document.getId() + " => " + document.getData());
                                events.add(new EventModel(document.getString("Title"),document.getString("Time"),document.getGeoPoint("GeoPoint")));

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView=inflater.inflate(R.layout.fragment_map, container, false);
        mapView=mView.findViewById(R.id.id_mapView);
            mapView.onCreate(null);
            mapView.onResume();
            mapView.refreshDrawableState();
            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    MapsInitializer.initialize(getContext());

                    mGoogleMap=googleMap;
                    mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                    for(int i=0;i<events.size();i++)
                    {
                        mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(events.get(i).getGeoPoint().getLatitude(),events.get(i).getGeoPoint().getLongitude())).title(events.get(i).getTitle()));
                    }

                    CameraPosition camera= CameraPosition.builder().target(new LatLng(47.524698,19.044044)).zoom(10).bearing(0).tilt(45).build();
                    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(camera));

                    mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            return false;
                        }
                    });

                }
            });

    return mView;}

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
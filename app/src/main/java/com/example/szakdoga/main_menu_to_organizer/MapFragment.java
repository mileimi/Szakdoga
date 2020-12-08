package com.example.szakdoga.main_menu_to_organizer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.szakdoga.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

import static android.content.ContentValues.TAG;

/**
 * Az események térképen való megjelenítése
 */

public class MapFragment extends Fragment {
    GoogleMap mGoogleMap;
    MapView mapView;
    View mView;
    ArrayList<EventModel> events;
    private FirebaseFirestore db;
    private String festID;

    public MapFragment(String festID) {
        db = FirebaseFirestore.getInstance();
        events=new ArrayList<>();
        this.festID=festID;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = mView.findViewById(R.id.id_mapView);
        MyAsyncTask myAsyncTask=new MyAsyncTask();
        myAsyncTask.execute();
        return mView;
    }


    public class MyAsyncTask extends AsyncTask<Void,Void,ArrayList<EventModel>>{

        @Override
        protected ArrayList<EventModel> doInBackground(Void... voids) {
            db.collection("festivals").document(festID).collection("events")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                    events.add(new EventModel(document.getString("Title"), document.getGeoPoint("GeoPoint")));
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
            return events;
        }

        @Override
        protected void onPostExecute(ArrayList<EventModel> eventModels) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mGoogleMap=googleMap;
                    mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    if(events!=null) {
                        for (int i = 0; i < events.size(); i++) {
                            mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(events.get(i).getGeoPoint().getLatitude(), events.get(i).getGeoPoint().getLongitude())).title(events.get(i).getTitle()));
                        }
                    }
                    CameraPosition camera= CameraPosition.builder().target(new LatLng(47.524698,19.044044)).zoom(10).bearing(0).tilt(45).build();
                    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(camera));
                }
            });
        }
    }}
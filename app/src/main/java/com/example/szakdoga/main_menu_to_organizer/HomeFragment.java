package com.example.szakdoga.main_menu_to_organizer;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.szakdoga.R;
import com.firebase.ui.firestore.SnapshotParser;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
/**
 * Ezen a fragmenten jelennek meg az események, szerkeszthetjük őket,
 * valamint újakat adhatunk a meglévőkhöz
 */
public class HomeFragment extends Fragment {

    //Változók
    private Button newEventBtn;
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private FirebaseFirestore db;
    String festID;
    private GoogleMap mGoogleMap;

    //A fragment szintén megkapja a kiválasztott fesztivál ID-ját
    public HomeFragment(String id) {
        this.festID=id;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db=FirebaseFirestore.getInstance();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (eventAdapter!=null){ eventAdapter.refresh();}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v;
        v=inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView=v.findViewById(R.id.recyclerViewForEvent);
        newEventBtn=v.findViewById(R.id.new_event_btn);

        //Adatbázis lekérés: Paging használatával: dátum szerint rendezve
        Query query=db.collection("festivals").document(festID).collection("events").orderBy("Time");

        PagedList.Config config=new PagedList.Config.Builder()
                .setInitialLoadSizeHint(3)
                .setPageSize(3)
                .build();

        FirestorePagingOptions<EventModel> options=new FirestorePagingOptions.Builder<EventModel>()
                .setQuery(query, config, new SnapshotParser<EventModel>() {
                    @NonNull
                    @Override
                    public EventModel parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        EventModel eventModel=snapshot.toObject(EventModel.class);
                        String itemId=snapshot.getId();
                        assert eventModel != null;
                        eventModel.setID(itemId);
                        return eventModel;
                    }
                })
                .setLifecycleOwner(this)
                .build();

        //RecyclerView adapter beállítása
        eventAdapter=new EventAdapter(options,festID);
        recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));
        recyclerView.setAdapter(eventAdapter);


        //Új esemény hozzáadása gomb, ami a NewEvent activityre ugrik
        newEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //dialógus indítása
                final Dialog dialog=new Dialog(v.getContext());
                dialog.setContentView(R.layout.activity_new_event);
                //Ablak beállítások
                int width= WindowManager.LayoutParams.MATCH_PARENT;
                int height=WindowManager.LayoutParams.WRAP_CONTENT;
                dialog.getWindow().setLayout(width,height);
                dialog.setCancelable(false);
                dialog.show();
                //dialóguson lévő elemek beállítása
                final Button backButton=dialog.findViewById(R.id.EventBack);
                final Button createButton=dialog.findViewById(R.id.EventCreate);
                final TextView eventDate=dialog.findViewById(R.id.DateEvent);
                final EditText eventTitle=dialog.findViewById(R.id.TitleEvent);
                final EditText eventDescription=dialog.findViewById(R.id.DescriptionEvent);
                final MapView mapView=dialog.findViewById(R.id.MapEvent);

                final GeoPoint[] geoPoint = new GeoPoint[1];

                //Térkép létrehozása
                mapView.onCreate(null);
                mapView.onResume();
                mapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        MapsInitializer.initialize(v.getContext());

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
                //Idő beállítás
                eventDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDatePickerDialog(v,eventDate);
                    }
                });
                //Létrehozás
                createButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        GeoPoint geo;
                        if (geoPoint[0] != null){
                            geo=new GeoPoint(geoPoint[0].getLatitude(),geoPoint[0].getLongitude());
                            Map<String,Object> event=new HashMap<>();
                            event.put("Title",eventTitle.getText().toString());
                            event.put("Description",eventDescription.getText().toString());
                            event.put("Time",eventDate.getText().toString());
                            event.put("GeoPoint", geo);
                            event.put("Image","");
                            db.collection("festivals").document(festID).collection("events").add(event).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    eventAdapter.refresh();
                                    Toast.makeText(v.getContext(),getString(R.string.event_created),Toast.LENGTH_LONG).show();
                                    dialog.cancel();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(v.getContext(),getString(R.string.event_not_created),Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                        else{
                            Toast.makeText(v.getContext(),getString(R.string.add_location),Toast.LENGTH_LONG).show();
                        }}
                });
                //Vissza gomb
                backButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
            }
        });
        return v;
            }
    //Dátum
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

                        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yy-MM-dd HH:mm");
                        t.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                };
                new TimePickerDialog(v.getContext(),timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false).show();
            }
        };
        new DatePickerDialog(v.getContext(),dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
    }




package com.example.szakdoga.main_menu_to_organizer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.szakdoga.R;
import com.example.szakdoga.signin_up.SignIn;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
/**
 * Az események RecyclerView-ban való megjelenítéséhez, szükség van adapterre
 */
public class EventAdapter extends FirestorePagingAdapter<EventModel,EventAdapter.EventViewHolder> {
    private final FirebaseFirestore firestore;
    private final String festivalID;
    private GoogleMap mGoogleMap;

    public EventAdapter(@NonNull FirestorePagingOptions<EventModel> options, String festivalID) {
        super(options);
        firestore=FirebaseFirestore.getInstance();
        this.festivalID=festivalID;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_simple_item,parent,false);
        return new EventViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull final EventViewHolder holder, int position, @NonNull final EventModel model) {
        //A beállított cardView-on megjelenő elemek beállítása, valamint funkcióinak implementálása
        //Cím és dátum megjelenítése
        holder.textView.setText(model.getTitle());
        holder.textViewTime.setText(model.getTime());

        //Háttér beszúrása
        Glide.with(holder.itemView.getContext())
                .load(model.getImage())
                .into(holder.background);

        //Szerkesztő gomb, ahol a címet és a leírást változtathatjuk
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String id=model.getID();
                //Edit-dialógus indítása
                final Dialog dialog=new Dialog(v.getContext());
                dialog.setContentView(R.layout.edit_event_dialog);
                //Ablak beállítások
                int width= WindowManager.LayoutParams.MATCH_PARENT;
                int height=WindowManager.LayoutParams.WRAP_CONTENT;
                dialog.getWindow().setLayout(width,height);
                dialog.show();
                //dialóguson lévő editTextek beállítása
                final EditText editTitle=dialog.findViewById(R.id.editTextTitle);
                final EditText editDescription=dialog.findViewById(R.id.editTextDescription);
                final Button cancelButton=dialog.findViewById(R.id.cancelbtn);
                final Button updateButton=dialog.findViewById(R.id.updatebtn);
                //Kiválasztott esemény adatainak megjelenítése
                editTitle.setText(model.getTitle());
                editDescription.setText(model.getDescription());

                //Kilépés
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

                //Update gombra kattintva elmenti a változtatásokat az adatbázisba
                updateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        firestore.collection("festivals").document(festivalID).collection("events").document(id).update("Title",editTitle.getText().toString(),"Description",editDescription.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                refresh();
                                dialog.cancel();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(v.getContext(),R.string.event_not_updated,Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        });
                    }
                });
            }
        });

        //Dátum beállítása
        holder.textViewTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ID=model.getID();
                showDatePickerDialog(v,ID);
            }
        });

        //Esemény helyszínének megváltoztatása
        holder.locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //Edit-dialógus indítása
                final Dialog dialog=new Dialog(v.getContext());
                dialog.setContentView(R.layout.edit_event_location_dialog);
                //Ablak beállítások
                int width= WindowManager.LayoutParams.MATCH_PARENT;
                int height=WindowManager.LayoutParams.WRAP_CONTENT;
                dialog.getWindow().setLayout(width,height);
                dialog.show();
                //dialóguson lévő editTextek beállítása
                final Button cancelButton=dialog.findViewById(R.id.cancelButton12);
                final Button updateButton=dialog.findViewById(R.id.updateButton12);
                final MapView map=dialog.findViewById(R.id.locationMap);
                final double latitude=model.getGeoPoint().getLatitude();
                final double longitude=model.getGeoPoint().getLongitude();
                final String titleE=model.getTitle();
                final String id=model.getID();
                final GeoPoint[] geoPoint = new GeoPoint[1];
                //Kiválasztott esemény adatainak megjelenítése
                map.onCreate(null);
                map.onResume();
                map.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        MapsInitializer.initialize(v.getContext());

                        mGoogleMap=googleMap;
                        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                        mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).title(titleE));

                        CameraPosition camera= CameraPosition.builder().target(new LatLng(latitude,longitude)).zoom(14).bearing(0).tilt(45).build();
                        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(camera));

                        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                            @Override
                            public void onMapClick(LatLng latLng) {
                                mGoogleMap.clear();
                                mGoogleMap.addMarker(new MarkerOptions().position(latLng).title(titleE));
                                geoPoint[0] =new GeoPoint(latLng.latitude,latLng.longitude);

                            }
                        });

                    }
                });

                //Kilépés a dialógusból
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

                //Változtatások mentése
                updateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {

                        firestore.collection("festivals").document(festivalID).collection("events").document(id).update("GeoPoint",geoPoint[0]).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                refresh();
                                Toast.makeText(v.getContext(),R.string.event_updated,Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(v.getContext(),R.string.event_not_updated,Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
            }
        });

        //Háttérkép megváltoztatása
        holder.photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Ehhez átirányít az EventPhotoSetting Activity-re
                Intent intent=new Intent(v.getContext(),EventPhotoSetting.class);
                intent.putExtra("EventID",model.getID());
                intent.putExtra("FestiID",festivalID);
                v.getContext().startActivity(intent);
            }
        });

        //Esemény törlése teljesen
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog(v,model.getID());
            }
        });


    }

    //Paging adatbázis letöltés állapotainak figyelése
    @Override
    protected void onLoadingStateChanged(@NonNull LoadingState state) {
        super.onLoadingStateChanged(state);

        switch (state){
            case ERROR:
                Log.d("Paging Log","Error");
                break;
            case LOADED:
                Log.d("Paging Log","Loaded"+getItemCount());
                break;
            case FINISHED:
                Log.d("Paging Log","Finished");
                break;
            case LOADING_MORE:
                Log.d("Paging Log","Loading more");
                break;
            case LOADING_INITIAL:
                Log.d("Paging Log","Loading initial");
                break;
        }

    }

    //Dátum beállítása DatePickerDialog segítségével
    private void showDatePickerDialog(final View v, final String ID) {
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
                        firestore.collection("festivals").document(festivalID).collection("events").document(ID).update("Time",simpleDateFormat.format((calendar.getTime()))).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                refresh();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(v.getContext(),R.string.date_not_updated,Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                };
                new TimePickerDialog(v.getContext(),timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false).show();
            }
        };
        new DatePickerDialog(v.getContext(),dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    //Esemény törlése dialógus
    private void deleteDialog( final View v, final String iD) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(v.getContext());
        builder1.setMessage(R.string.want_delete_event);
        builder1.setCancelable(false);

        //Igen-töröljük az adatbázisból az eseményt
        builder1.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                firestore.collection("festivals").document(festivalID).collection("events").document(iD).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        refresh();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(v.getContext(),R.string.event_deleted,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        //Kilépés a dialógusból
        builder1.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    //ViewHolder
    public class EventViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        TextView textViewTime;
        ImageView background,deleteButton, editButton,locationButton,photoButton;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.EventTitle1);
            textViewTime=itemView.findViewById(R.id.EventTime1);
            background=itemView.findViewById(R.id.background_image1);
            deleteButton=itemView.findViewById(R.id.delete_event);
            editButton=itemView.findViewById(R.id.edit_event);
            locationButton=itemView.findViewById(R.id.locationChange);
            photoButton=itemView.findViewById(R.id.photoChange);
        }
    }
}

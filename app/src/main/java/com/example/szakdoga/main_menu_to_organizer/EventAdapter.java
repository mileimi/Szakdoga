package com.example.szakdoga.main_menu_to_organizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.szakdoga.R;
import com.example.szakdoga.room_database.NoteModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder>  {
    ArrayList<EventModel> events;
    Activity context;
    private FirebaseFirestore datab;
    private CollectionReference collectionRef;
    private GoogleMap mGoogleMap;
    FirebaseStorage storage;


    public EventAdapter(Activity context,ArrayList<EventModel> events){
        this.context=context;
        this.events=events;
        datab=FirebaseFirestore.getInstance();
        collectionRef=datab.collection("events");
        storage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_list_simple_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final EventAdapter.ViewHolder holder, final int position) {
        holder.textView.setText(events.get(position).getTitle());
        holder.textViewTime.setText(events.get(position).getTime());
        Glide.with(context)
                .load(events.get(position).getImagePath())
                .into(holder.background);

        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EventModel event1=events.get(holder.getAdapterPosition());
                final String id=event1.getID();
                //Edit-dialógus indítása
                final Dialog dialog=new Dialog(context);
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
                editTitle.setText(event1.getTitle());
                editDescription.setText(event1.getDescription());
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                updateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        collectionRef.document(id).update("Title",editTitle.getText().toString());
                        collectionRef.document(id).update("Description",editDescription.getText().toString());
                        events.get(position).setTitle(editTitle.getText().toString());
                        events.get(position).setDescription(editDescription.getText().toString());
                        notifyItemChanged(position);
                    }
                });

            }
        });
        holder.textViewTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ID=events.get(position).getID();
                showDatePickerDialog(v,position,ID);
            }
        });

        holder.locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //Edit-dialógus indítása
                final Dialog dialog=new Dialog(context);
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
                final double latitude=events.get(position).getGeoPoint().getLatitude();
                final double longitude=events.get(position).getGeoPoint().getLongitude();
                final String titleE=events.get(position).getTitle();
                final String id=events.get(position).getID();
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
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                updateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        collectionRef.document(id).update("GeoPoint",geoPoint[0]);
                        events.get(position).setGeoPoint(geoPoint[0]);
                        notifyItemChanged(position);
                    }
                });
            }
        });

        holder.photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(context,EventPhotoSetting.class);
                intent.putExtra("EventID",events.get(position).getID());
                context.startActivity(intent);
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog(holder.getAdapterPosition());
            }
        });

    }

    private void showDatePickerDialog(final View v, final int position, final String ID) {
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
                        collectionRef.document(ID).update("Time",simpleDateFormat.format((calendar.getTime())));
                        events.get(position).setTime(simpleDateFormat.format(calendar.getTime()));
                        notifyItemChanged(position);
                    }
                };
                new TimePickerDialog(v.getContext(),timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false).show();
            }
        };
        new DatePickerDialog(v.getContext(),dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void deleteDialog(final int position) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage("Are you sure you want to delete this event?");
        builder1.setCancelable(true);

        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        collectionRef.document(events.get(position).getID()).delete();
                        events.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position,events.size());

                    }
                });

        builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }


    @Override
    public int getItemCount() {
        return events.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        TextView textViewTime;
        ImageView background,deleteButton, editButton,locationButton,photoButton;

        public ViewHolder(@NonNull View itemView) {
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

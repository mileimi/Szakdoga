package com.example.szakdoga.main_menu_to_participants;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.szakdoga.R;
import com.example.szakdoga.main_menu_to_organizer.EventModel;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.internal.$Gson$Preconditions;

import java.util.ArrayList;
import java.util.Objects;
/**
 * Adapter a RecyclerView-hoz, ami az eseményeket jeleníti meg
 */
public class RecycleAdapter extends FirestorePagingAdapter<EventModel, RecycleAdapter.ViewHolder32> {
    private FirebaseFirestore firestore;
    private String festivalID;
    private FirebaseAuth firebaseAuth;
    private DocumentReference documentReference;

    public RecycleAdapter(@NonNull FirestorePagingOptions<EventModel> options, String festivalID) {
        super(options);
        this.festivalID = festivalID;
        firestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        documentReference= firestore.collection("users").document(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());
    }

    @NonNull
    @Override
    public ViewHolder32 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.eventcard, parent, false);
        return new RecycleAdapter.ViewHolder32(view);
    }
    @Override
    protected void onBindViewHolder(@NonNull final RecycleAdapter.ViewHolder32 holder, final int position, @NonNull final EventModel model) {
        //Kártyán megjelenő adatok beállítása
        holder.textView.setText(model.getTitle());
        holder.textViewTime.setText(model.getTime());
        if (!model.getImage().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(model.getImage())
                    .into(holder.image);
        }

        //Ha a felhasználó már kedvelte az eseményt akkor ehhez igazítja a toggle button helyzetét
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    ArrayList<String> arrList;
                    arrList = (ArrayList) documentSnapshot.get("likes");

                    assert arrList != null;
                    if (arrList.contains(model.getID()))
                    {holder.toggleButton.setChecked(true);}
                }}
        });

        //Kedvelhetjük az eseményeket
        holder.toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    documentReference.update("likes", FieldValue.arrayUnion(model.getID())).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("Like","Liked");
                        }
                    });
                }else{
                    documentReference.update("likes",FieldValue.arrayRemove(model.getID())).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("Like","UnLiked");
                        }
                        });
                }
            }
        });

        //Részletesebb leírást kapunk ha rákattintunk az eseményre
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   Intent intent=new Intent(v.getContext(),EventDetails.class);
                    intent.putExtra("EventTitle",model.getTitle());
                    intent.putExtra("EventDescription",model.getDescription());
                    intent.putExtra("EventTime",model.getTime());
                    intent.putExtra("latitude",model.getGeoPoint().getLatitude());
                    intent.putExtra("longitude",model.getGeoPoint().getLongitude());
                    v.getContext().startActivity(intent);
                }
            });
    }
    //ViewHolder
    public class ViewHolder32 extends RecyclerView.ViewHolder {
        TextView textView;
        TextView textViewTime;
        ImageView image;
        ToggleButton toggleButton;
        public ViewHolder32(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.EventTitle);
            textViewTime = itemView.findViewById(R.id.EventTime);
            image = itemView.findViewById(R.id.background_image);
            toggleButton = itemView.findViewById(R.id.like_event);
        }

    }

}


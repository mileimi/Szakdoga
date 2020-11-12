package com.example.szakdoga.main_menu_to_participants;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.szakdoga.R;
import com.example.szakdoga.main_menu_to_organizer.EventModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {
        ArrayList<EventModel> events;
        Context context;
        private FirebaseAuth firebaseAuth;
        private FirebaseFirestore firestore;
        private DocumentReference docRef;

public RecycleAdapter(Context context,ArrayList<EventModel> events){
        this.context=context;
        this.events=events;
        this.firebaseAuth=FirebaseAuth.getInstance();
        this.firestore=FirebaseFirestore.getInstance();
        this.docRef=firestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
        }

@NonNull
@Override
public RecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
        .inflate(R.layout.eventcard,parent,false);
         return new ViewHolder(view);
        }

@Override
public void onBindViewHolder(@NonNull final RecycleAdapter.ViewHolder holder, final int position) {

        holder.textView.setText(events.get(position).getTitle());
        holder.textViewTime.setText(events.get(position).getTime());
        if (!events.get(position).getImagePath().isEmpty()) {
            Glide.with(context)
                    .load(events.get(position).getImagePath())
                    .into(holder.image);
        }

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    ArrayList<String> arrList;
                    arrList = (ArrayList) documentSnapshot.get("likes");

                    if (arrList.contains(events.get(position).getID()))
                    {holder.toggleButton.setChecked(true);}
                }}
        });


        holder.toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    docRef.update("likes", FieldValue.arrayUnion(events.get(position).getID()));
                }else{
                    docRef.update("likes",FieldValue.arrayRemove(events.get(position).getID()));
                }
            }
        });

    holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,EventDetails.class);
                intent.putExtra("Event",events.get(position));
                intent.putExtra("latitude",events.get(position).getGeoPoint().getLatitude());
                intent.putExtra("longitude",events.get(position).getGeoPoint().getLongitude());
                context.startActivity(intent);
            }
        });
}


@Override
public int getItemCount() {
        return events.size();
        }

public class ViewHolder extends RecyclerView.ViewHolder {
    TextView textView;
    TextView textViewTime;
    ImageView image;
    ToggleButton toggleButton;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        textView=itemView.findViewById(R.id.EventTitle);
        textViewTime=itemView.findViewById(R.id.EventTime);
        image=itemView.findViewById(R.id.background_image);
        toggleButton=itemView.findViewById(R.id.like_event);
    }

}
}

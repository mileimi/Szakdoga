package com.example.szakdoga.main_menu_to_participants;

import android.content.Context;
import android.content.Intent;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

class RecycleAdapter2 extends RecyclerView.Adapter<RecycleAdapter2.ViewHolder> {
    ArrayList<EventModel> events;
    Context context;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private DocumentReference docRef;

    public RecycleAdapter2(Context context,ArrayList<EventModel> events){
        this.context=context;
        this.events=events;
        this.firebaseAuth=FirebaseAuth.getInstance();
        this.firestore=FirebaseFirestore.getInstance();
        this.docRef=firestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
    }

    @NonNull
    @Override
    public RecycleAdapter2.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.eventcard,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecycleAdapter2.ViewHolder holder, final int position) {

        holder.textView.setText(events.get(position).getTitle());
        holder.textViewTime.setText(events.get(position).getTime());
        if (!events.get(position).getImage().isEmpty()) {
            Glide.with(context)
                    .load(events.get(position).getImage())
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
                    docRef.update("likes", FieldValue.arrayRemove(events.get(position).getID())).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            events.remove(position);
                            notifyItemRemoved(position);
                        }
                    });


                }
            }
        });

        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,EventDetails.class);
                intent.putExtra("Event",events.get(position));
                intent.putExtra("latitude",events.get(position).getGeoPoint().getLatitude());
                intent.putExtra("longitude",events.get(position).getGeoPoint().getLongitude());
                context.startActivity(intent);
            }
        });
*/
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
package com.example.szakdoga.main_menu_to_participants;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.szakdoga.R;
import com.example.szakdoga.main_menu_to_organizer.EventModel;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {
        ArrayList<EventModel> events;
        Context context;
        private StorageReference mStorageRef;


public RecycleAdapter(Context context,ArrayList<EventModel> events){
        this.context=context;
        this.events=events;
        mStorageRef = FirebaseStorage.getInstance().getReference();
        }

@NonNull
@Override
public RecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
        .inflate(R.layout.eventcard,parent,false);
        return new ViewHolder(view);
        }

@Override
public void onBindViewHolder(@NonNull RecycleAdapter.ViewHolder holder, final int position) {

        holder.textView.setText(events.get(position).getTitle());
        holder.textViewTime.setText(events.get(position).getTime());
        if (!events.get(position).getImagePath().isEmpty()) {
            Glide.with(context)
                    .load(events.get(position).getImagePath())
                    .into(holder.image);
        }

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

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        textView=itemView.findViewById(R.id.EventTitle);
        textViewTime=itemView.findViewById(R.id.EventTime);
        image=itemView.findViewById(R.id.background_image);

    }

}
}

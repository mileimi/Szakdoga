package com.example.szakdoga.main_menu_to_organizer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.szakdoga.R;

import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    ArrayList<EventModel> events;
    Context context;


    public EventAdapter(Context context,ArrayList<EventModel> events){
        this.context=context;
        this.events=events;
    }

    @NonNull
    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_list_simple_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.ViewHolder holder, int position) {
        holder.textView.setText(events.get(position).getTitle());
        holder.textViewTime.setText(events.get(position).getTime());
        Glide.with(context)
                .load(events.get(position).getImagePath())
                .into(holder.background);

        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //EDIT functions
            }
        });
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DELETE function
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
        ImageView background,deleteButton, editButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.EventTitle1);
            textViewTime=itemView.findViewById(R.id.EventTime1);
            background=itemView.findViewById(R.id.background_image1);
            deleteButton=itemView.findViewById(R.id.delete_event);
            editButton=itemView.findViewById(R.id.edit_event);
        }
    }
}

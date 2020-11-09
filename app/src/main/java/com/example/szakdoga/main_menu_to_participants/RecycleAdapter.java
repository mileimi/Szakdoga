package com.example.szakdoga.main_menu_to_participants;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.szakdoga.R;
import com.example.szakdoga.main_menu_to_organizer.EventModel;

import java.util.ArrayList;

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {
        ArrayList<EventModel> events;
        ArrayList<String> likedEvents;
        Context context;


public RecycleAdapter(Context context,ArrayList<EventModel> events,ArrayList<String> likedEvents){
        this.context=context;
        this.events=events;
        this.likedEvents=likedEvents;
        }

@NonNull
@Override
public RecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
        .inflate(R.layout.eventcard,parent,false);
        return new ViewHolder(view);
        }

@Override
public void onBindViewHolder(@NonNull RecycleAdapter.ViewHolder holder, int position) {
        holder.textView.setText(events.get(position).getTitle());
        holder.textViewTime.setText(events.get(position).getTime());
        for (int i=0;i<events.size();i++){
            for (int j=0;j<likedEvents.size();j++){
                if (events.get(i).getID()==likedEvents.get(j)){
                    holder.star.setVisibility(View.INVISIBLE);
                }
            }
        }
        }

@Override
public int getItemCount() {
        return events.size();
        }

public class ViewHolder extends RecyclerView.ViewHolder {

    TextView textView;
    TextView textViewTime;
    ImageView star;
    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        textView=itemView.findViewById(R.id.EventTitle);
        textViewTime=itemView.findViewById(R.id.EventTime);
        star=itemView.findViewById(R.id.like_event);
    }
}
}

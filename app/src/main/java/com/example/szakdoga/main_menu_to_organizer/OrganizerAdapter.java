package com.example.szakdoga.main_menu_to_organizer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.szakdoga.R;
import com.example.szakdoga.main_menu_to_participants.RecycleAdapter;

import java.util.ArrayList;

public class OrganizerAdapter extends RecyclerView.Adapter<OrganizerAdapter.ViewHolder3> {

    private Context context;
    private ArrayList<UserModel> list;

    public OrganizerAdapter(Context context, ArrayList<UserModel> list) {
        this.context=context;
        this.list=list;
    }

    @NonNull
    @Override
    public OrganizerAdapter.ViewHolder3 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.organizer_card,parent,false);
        return new OrganizerAdapter.ViewHolder3(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder3 holder, int position) {

        holder.textName.setText(list.get(position).getFirstName()+" "+list.get(position).getLastName());
        holder.textEmail.setText(list.get(position).getEmail());
        holder.imageEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //szervezői státusz állítása
            }
        });
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder3 extends RecyclerView.ViewHolder {
        TextView textName,textEmail;
        ImageView imageEdit;

        public ViewHolder3(@NonNull View itemView) {
            super(itemView);
            textName=itemView.findViewById(R.id.nameOrganizer);
            textEmail=itemView.findViewById(R.id.emailOrganizer);
            imageEdit=itemView.findViewById(R.id.edit_organizer);
        }

    }
}

package com.example.szakdoga.main_menu_to_organizer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.szakdoga.R;
import com.example.szakdoga.main_menu_to_participants.RecycleAdapter;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class OrganizerAdapter extends RecyclerView.Adapter<OrganizerAdapter.ViewHolder3> {

    private Context context;
    private ArrayList<UserModel> list;
    private FirebaseFirestore firestore;

    public OrganizerAdapter(Context context, ArrayList<UserModel> list) {
        this.context=context;
        this.list=list;
        firestore=FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public OrganizerAdapter.ViewHolder3 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.organizer_card,parent,false);
        return new OrganizerAdapter.ViewHolder3(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder3 holder, final int position) {

        holder.textName.setText(list.get(position).getFirstName()+" "+list.get(position).getLastName());
        holder.textEmail.setText(list.get(position).getEmail());
        holder.imageEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id=list.get(position).getID();
                showPopUpMenu(v, id,position);
            }
        });
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    private void showPopUpMenu(final View v, final String id, final int position){
        final PopupMenu popupMenu=new PopupMenu(v.getContext(),v);
        popupMenu.inflate(R.menu.pop_up_edit_organizer_permission);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.take_back:
                        if (list.size()>1) {
                            DocumentReference reference = firestore.collection("users").document(id);
                            reference.update("organizer", false);
                        }
                        else{
                            Toast.makeText(v.getContext(), "Someone has to be an organizer.",Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    default:return false;
            }
        }});
        popupMenu.show();
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

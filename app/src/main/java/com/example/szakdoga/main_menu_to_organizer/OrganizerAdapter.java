package com.example.szakdoga.main_menu_to_organizer;

import android.annotation.SuppressLint;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * Az adapter célja a szervezők RecyclerView-ban történő megjelenítése.
 * Itt megjelenik a szervező neve, email címe.
 * Illetve lehetőség van egy szervező engedélyének visszavonására.
 */

public class OrganizerAdapter extends RecyclerView.Adapter<OrganizerAdapter.ViewHolder3> {

    //Változók
    private Context context;
    private ArrayList<UserModel> list;
    private final FirebaseFirestore firestore;

    //Adapter:ami megkapja a szervezők listáját
    public OrganizerAdapter(Context context, ArrayList<UserModel> list) {
        this.context=context;
        this.list=list;
        firestore=FirebaseFirestore.getInstance();
    }

    //CardView-ot használok az adatok megjelenítésre
    @NonNull
    @Override
    public OrganizerAdapter.ViewHolder3 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.organizer_card,parent,false);
        return new OrganizerAdapter.ViewHolder3(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder3 holder, final int position) {

        //Adatok
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

    //Popup menü az engedély visszavonásának megjelenítésére
    private void showPopUpMenu(final View v, final String id, final int position){
        final PopupMenu popupMenu=new PopupMenu(v.getContext(),v);
        popupMenu.inflate(R.menu.pop_up_edit_organizer_permission);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.take_back) {
                    if (list.size() > 1) {
                        DocumentReference reference = firestore.collection("users").document(id);
                        reference.update("organizer", false).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                list.remove(position);
                                notifyItemRemoved(position);
                            }
                        });
                    } else {
                        Toast.makeText(v.getContext(), R.string.has_to_be_org, Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            }});
        popupMenu.show();
    }

    public class ViewHolder3 extends RecyclerView.ViewHolder {
        TextView textName,textEmail;
        ImageView imageEdit;

        public ViewHolder3(@NonNull View itemView) {
            super(itemView);

            //Elemek megkeresése a CardView-on
            textName=itemView.findViewById(R.id.nameOrganizer);
            textEmail=itemView.findViewById(R.id.emailOrganizer);
            imageEdit=itemView.findViewById(R.id.edit_organizer);
        }

    }
}

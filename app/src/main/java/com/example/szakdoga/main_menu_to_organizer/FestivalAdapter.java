package com.example.szakdoga.main_menu_to_organizer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.szakdoga.R;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
/**
 * Fesztivál kártyák megjelenytéséhez szükséges adapter
 */
public class FestivalAdapter extends FirestorePagingAdapter<FestivalModel,FestivalAdapter.FestivalViewHolder> {
    private final FirebaseFirestore firestore;

    public FestivalAdapter(@NonNull FirestorePagingOptions<FestivalModel> options) {
        super(options);
        firestore=FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public FestivalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_festival_item,parent,false);
        return new FestivalViewHolder(view);
    }

    //A fesztivál cardView-on lévő elemek funkcióinak beállítása
    @Override
    protected void onBindViewHolder(@NonNull final FestivalViewHolder holder, final int position, @NonNull final FestivalModel model) {
        final String id=model.getId();
        holder.name.setText(model.getName().toUpperCase());
        holder.date.setText(model.getDate());
        holder.place.setText(model.getPlace());
        //Fesztivál törlése
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //Dialógust nyit meg
                AlertDialog.Builder builder1 = new AlertDialog.Builder(v.getContext());
                builder1.setMessage(R.string.want_delete);
                builder1.setCancelable(true);

                //Igen gomb-törli a kiválasztott fesztivált
                builder1.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int id1) {
                        DocumentReference reference=firestore.collection("festivals").document(id);
                        reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                refresh();
                                Toast.makeText(v.getContext(),R.string.festival_deleted,Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(v.getContext(),R.string.festival_not_deleted,Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        });
                    }
                });
                //Nem gomb-kilép a dialógusból
                builder1.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

                AlertDialog alert11 = builder1.create();
                alert11.show();

            }
        });
        //Szerkesztés gomb
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Edit-dialógus indítása
                final Dialog dialog=new Dialog(v.getContext());
                dialog.setContentView(R.layout.festival_updating_dialog);
                //Ablak beállítások
                int width= WindowManager.LayoutParams.MATCH_PARENT;
                int height=WindowManager.LayoutParams.WRAP_CONTENT;
                dialog.getWindow().setLayout(width,height);
                dialog.setCancelable(false);
                dialog.show();

                //dialóguson lévő editTextek beállítása
                final Button backButton=dialog.findViewById(R.id.btnBack312);
                final Button updateButton=dialog.findViewById(R.id.btnUpdateFestival);
                final EditText festivalName=dialog.findViewById(R.id.festivalNameUpdate);
                final EditText festivalDate=dialog.findViewById(R.id.textView9);
                final EditText festivalPlace=dialog.findViewById(R.id.textView123);
                festivalDate.setText(model.getDate());
                festivalName.setText(model.getName());
                festivalPlace.setText(model.getPlace());

                //Dialógus bezárása
                backButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                //Ha menteni akarjuk a változtatásokat
                updateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        DocumentReference reference=firestore.collection("festivals").document(id);
                        reference.update("name",festivalName.getText().toString(),"date",festivalDate.getText().toString(),"place",festivalPlace.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                refresh();
                                Toast.makeText(v.getContext(),R.string.festival_updated,Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(v.getContext(),R.string.festival_not_updated,Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        });

                    }
                });
            }
        });
        //A fesztiválra kattintva megnyitja annak eseményeit
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),NavigationBar.class);
                intent.putExtra("FestivalId",id);
                v.getContext().startActivity(intent);
            }
        });
    }

    //Az adatbázis lekérések állapotainak figyelése
    @Override
    protected void onLoadingStateChanged(@NonNull LoadingState state) {
        super.onLoadingStateChanged(state);

        switch (state){
            case ERROR:
                Log.d("Paging Log","Error");
                break;
            case LOADED:
                Log.d("Paging Log","Loaded"+getItemCount());
                break;
            case FINISHED:
                Log.d("Paging Log","Finished");
                break;
            case LOADING_MORE:
                Log.d("Paging Log","Loading more");
                break;
            case LOADING_INITIAL:
                Log.d("Paging Log","Loading initial");
                break;
        }

    }
    //ViewHolder
    public class FestivalViewHolder extends RecyclerView.ViewHolder{
        private TextView name;
        private TextView place;
        private TextView date;
        private ImageView delete;
        private ImageView edit;

        public FestivalViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.festivalName);
            date=itemView.findViewById(R.id.festivalDate);
            place=itemView.findViewById(R.id.festivalPlace);
            delete=itemView.findViewById(R.id.delete_festival);
            edit=itemView.findViewById(R.id.edit_festival);
        }
    }
}

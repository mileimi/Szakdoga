package com.example.szakdoga.main_menu_to_participants;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.szakdoga.R;
import com.example.szakdoga.main_menu_to_organizer.FestivalModel;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * RecyclerView adaptere
 */
public class SimpleFestivalAdapter extends FirestorePagingAdapter<FestivalModel,SimpleFestivalAdapter.Festival1ViewHolder> {
    private final FirebaseFirestore firestore;

    public SimpleFestivalAdapter(@NonNull FirestorePagingOptions<FestivalModel> options) {
        super(options);
        firestore=FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public Festival1ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_festival_item2,parent,false);
        return new SimpleFestivalAdapter.Festival1ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull Festival1ViewHolder holder, int position, @NonNull FestivalModel model) {
        //Fesztivál adatok megjelenítése a holdereken
        final String id=model.getId();
        holder.name.setText(model.getName().toUpperCase());
        holder.date.setText(model.getDate());
        holder.place.setText(model.getPlace());

        //Egy fesztiválra kattintva tovább ugrik annak eseményeire
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(), Navigation.class);
                intent.putExtra("FestivalId",id);
                v.getContext().startActivity(intent);
            }
        });
    }

    //Paging lekérdezés állapotainak figyelése
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
    public class Festival1ViewHolder extends RecyclerView.ViewHolder{
        private TextView name;
        private TextView place;
        private TextView date;

        public Festival1ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.festivalName12);
            date=itemView.findViewById(R.id.festivalDate12);
            place=itemView.findViewById(R.id.festivalPlace12);

        }

}}

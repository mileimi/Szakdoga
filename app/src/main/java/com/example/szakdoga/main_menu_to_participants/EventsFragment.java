package com.example.szakdoga.main_menu_to_participants;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.szakdoga.R;
import com.example.szakdoga.main_menu_to_organizer.EventModel;
import com.firebase.ui.firestore.SnapshotParser;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
/**
 *  Az események megjelenítése RecyclerView használatával
 */
public class EventsFragment extends Fragment {

    //Változók
    private RecyclerView recyclerView;
    private RecycleAdapter eventAdapter;
    private FirebaseFirestore db;
    String festID;

    public EventsFragment(String festID) {
        this.festID = festID;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (eventAdapter!=null){ eventAdapter.notifyDataSetChanged();}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v;
        v=inflater.inflate(R.layout.fragment_events, container, false);
        recyclerView=v.findViewById(R.id.recyclerViewForEvent1);
        //Fesztivál eseményeinek lekérdezése
        Query query=db.collection("festivals").document(festID).collection("events");

        PagedList.Config config=new PagedList.Config.Builder()
                .setInitialLoadSizeHint(3)
                .setPageSize(3)
                .build();
        FirestorePagingOptions<EventModel> options=new FirestorePagingOptions.Builder<EventModel>()
                .setQuery(query, config, new SnapshotParser<EventModel>() {
                    @NonNull
                    @Override
                    public EventModel parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        EventModel eventModel=snapshot.toObject(EventModel.class);
                        String itemId=snapshot.getId();
                        assert eventModel != null;
                        eventModel.setID(itemId);
                        return eventModel;
                    }
                })
                .setLifecycleOwner(this)
                .build();

        //Adapter beállítások
        eventAdapter=new RecycleAdapter(options,festID);
        recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));
        recyclerView.setAdapter(eventAdapter);

        return v;
    }

}
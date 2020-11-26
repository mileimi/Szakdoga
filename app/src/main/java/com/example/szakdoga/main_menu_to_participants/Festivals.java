package com.example.szakdoga.main_menu_to_participants;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.szakdoga.R;
import com.example.szakdoga.main_menu_to_organizer.FestivalModel;
import com.firebase.ui.firestore.SnapshotParser;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * Fesztiválok megjelenítése
 */
public class Festivals extends AppCompatActivity {
    //Változók
    private RecyclerView festivalRecycler;
    private FirebaseFirestore firestore;
    private SimpleFestivalAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_festivals);

        //RecycylerView-t használok a felsoroláshoz
        festivalRecycler = findViewById(R.id.festivalRecycler61);
        firestore=FirebaseFirestore.getInstance();

        //Dátum szerint rendezi a fesztiválokat
        Query query=firestore.collection("festivals").orderBy("date");

        //Paging beállításokat használok, egyszerre csak 3 fesztivált tölt le, majd ha tovább görgetünk újabb adag kerül letöltésre
        PagedList.Config config=new PagedList.Config.Builder()
                .setInitialLoadSizeHint(3)
                .setPageSize(3)
                .build();

        FirestorePagingOptions<FestivalModel> options=new FirestorePagingOptions.Builder<FestivalModel>()
                .setQuery(query, config, new SnapshotParser<FestivalModel>() {
                        @NonNull
                        @Override
                        public FestivalModel parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                      FestivalModel festivalModel=snapshot.toObject(FestivalModel.class);
                      String itemId=snapshot.getId();
                      assert festivalModel != null;
                      festivalModel.setId(itemId);
                      return festivalModel;
                        }
                    })
                    .setLifecycleOwner(this)
                    .build();

            //Adapter beállítások
            adapter=new SimpleFestivalAdapter(options);
            festivalRecycler.setLayoutManager(new LinearLayoutManager(this));
            festivalRecycler.setAdapter(adapter);
    }
}
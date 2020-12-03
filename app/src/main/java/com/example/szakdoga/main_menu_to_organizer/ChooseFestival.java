package com.example.szakdoga.main_menu_to_organizer;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.szakdoga.R;
import com.firebase.ui.firestore.SnapshotParser;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
/**
 * Fesztiválok felsorolása
 */

public class ChooseFestival extends AppCompatActivity {

    //Változók
    private RecyclerView festivalRecycler;
    private FirebaseFirestore firestore;
    private FestivalAdapter adapter;
    private Button createFestival;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_festival);

        //RecycylerView-t használok a felsoroláshoz
        festivalRecycler = findViewById(R.id.festivalRecycler);
        createFestival=findViewById(R.id.festivalCreate);
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
        adapter=new FestivalAdapter(options);
        festivalRecycler.setLayoutManager(new LinearLayoutManager(this));
        festivalRecycler.setAdapter(adapter);

        //Új fesztivál létrehozása
        createFestival.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialógus indítása
                final Dialog dialog=new Dialog(v.getContext());
                dialog.setContentView(R.layout.festival_creating_dialog);
                //Ablak beállítások
                int width= WindowManager.LayoutParams.MATCH_PARENT;
                int height=WindowManager.LayoutParams.WRAP_CONTENT;
                dialog.getWindow().setLayout(width,height);
                dialog.setCancelable(false);
                dialog.show();
                //dialóguson lévő elemek beállítása
                final Button backButton=dialog.findViewById(R.id.btnBack);
                final Button createButton=dialog.findViewById(R.id.btnCreate);
                final EditText festivalName=dialog.findViewById(R.id.festivalNameText);
                final TextView festivalFromDate=dialog.findViewById(R.id.fromTime);
                final TextView festivalToDate=dialog.findViewById(R.id.toTime);
                final EditText festivalPlace=dialog.findViewById(R.id.palceEditText);

                //Mikortól kezdődik?
                festivalFromDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDatePickerDialog(v,festivalFromDate);
                    }
                });
                //Meddig tart?
                festivalToDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDatePickerDialog(v,festivalToDate);
                    }
                });

                //Új fesztivál létrehozása
                createButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Map<String, Object> festival = new HashMap<>();
                        festival.put("name", festivalName.getText().toString());
                        festival.put("place",festivalPlace.getText().toString());
                        festival.put("date",festivalFromDate.getText().toString()+"-"+festivalToDate.getText().toString());
                        firestore.collection("festivals").add(festival).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                adapter.refresh();
                                Toast.makeText(ChooseFestival.this,getString(R.string.festival_created),Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ChooseFestival.this,getString(R.string.festival_not_created),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                //Vissza gomb
                backButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
            }
        });
    }

    //Dátum beállítása DatePickerDialog segítségével
    private void showDatePickerDialog(final View v,final TextView t) {
        final Calendar calendar=Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yy/MM/dd");
                t.setText(simpleDateFormat.format(calendar.getTime()));
                }
        };
        new DatePickerDialog(v.getContext(),dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
}
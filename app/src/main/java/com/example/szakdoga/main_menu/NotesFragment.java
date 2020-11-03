package com.example.szakdoga.main_menu;
/**
 * Ez a fragment a jegyzetek kezelését teszi lehetővé
 */

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.szakdoga.R;
import com.example.szakdoga.room_database.NoteAdapter;
import com.example.szakdoga.room_database.NoteDatabase;
import com.example.szakdoga.room_database.NoteModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NotesFragment extends Fragment {

    private RecyclerView recyclerView;

    //Jegyzet lista,adatbázis és adapter
    List<NoteModel> notesList=new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    NoteDatabase database;
    NoteAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View v;
        v = inflater.inflate(R.layout.fragment_notes, container, false);
        recyclerView = v.findViewById(R.id.list);

        //Az adatbázisból lekérjük az összes jegyzetet a listába
        database = NoteDatabase.getInstance(getContext());
        notesList = database.noteDao().getAllNotes();

        //RecyclerView beállítása
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new NoteAdapter(getActivity(), notesList);
        recyclerView.setAdapter(adapter);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //A felületen megjelenő "+" gombal tudunk új jegyzetet létrehozni
        Button fab=view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

    }

    //Dátum és idő beállítása
    private void showDatePickerDialog(final TextView dateText) {
        final Calendar calendar=Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

                TimePickerDialog.OnTimeSetListener timeSetListener=new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        calendar.set(Calendar.MINUTE,minute);

                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yy-MM-dd HH:MM");

                        dateText.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                };
                new TimePickerDialog(getContext(),timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false).show();
            }
        };
        new DatePickerDialog(getContext(),dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    // Az openDialog metódus felelős az új jegyzet létrehozásában
    private void openDialog() {

        //Egy dialógus ablak jelenik meg
        View view=LayoutInflater.from(getContext()).inflate(R.layout.adding_notes,null);

        //A dialóguson megjelenő elemek összekapcsolása a layout-al
        final EditText titleN=view.findViewById(R.id.title_note);
        final EditText descriptionN=view.findViewById(R.id.note_description);
        final TextView textDueDate=view.findViewById(R.id.noteDueDate);
        final Spinner spinner=view.findViewById(R.id.spinner1);

        //Spinner elem és a hozzá tartozó adapter definiálása
        final ArrayAdapter<CharSequence> adapterSpin=ArrayAdapter.createFromResource(getContext(),R.array.priorities, android.R.layout.simple_spinner_item);
        adapterSpin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterSpin);

        //Az új jegyzet készítése AlertDialog formájában jelenik meg
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext())
                .setTitle("Create a new Note")
                .setView(view)
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String sTitle=titleN.getText().toString().trim();
                        String sDescription =descriptionN.getText().toString().trim();
                        int sSpinner=spinner.getSelectedItemPosition();
                        String sDate=textDueDate.getText().toString().trim();
                        NoteModel note=new NoteModel(sTitle,sSpinner,sDate,sDescription);
                        database.noteDao().insertNote(note);
                        titleN.setText("");
                        descriptionN.setText("");
                        notesList.clear();
                        notesList.addAll(database.noteDao().getAllNotes());
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancel",null)
                .setCancelable(false);
        // Az AlertDialog-on megjelenő dátum beállítására meghívódik a showPickerDialog metódus
        textDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(textDueDate);
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }
}


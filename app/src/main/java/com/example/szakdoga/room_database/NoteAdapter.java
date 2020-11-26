package com.example.szakdoga.room_database;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.szakdoga.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
/**
 * A jegyzetek kártyákon (notecard) való megjlenítéséhez készült adapter,
 * amit a RecyclerView használ
 */

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    //Jegyzet lista, adatbázis
    private List<NoteModel> noteModelList;
    private Activity context;
    private NoteDatabase noteDatabase;

    //Adapter, ami megkapja a jegyzetek listát
    public NoteAdapter(Activity context,List<NoteModel> list){
        this.context=context;
        this.noteModelList=list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notecard,parent,false);
        return new ViewHolder(view);
    }

    //Egy-egy jegyzet adatainak megjelenítése a NoteCard-on
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final NoteModel noteModel=noteModelList.get(position);
        noteDatabase= NoteDatabase.getInstance(context);

        holder.title.setText(noteModel.getTitle());
        holder.date.setText(noteModel.getDate());
        if (noteModel.getPriority()==0){
            holder.priority.setVisibility(View.INVISIBLE);
        }
        else {
            holder.priority.setVisibility(View.VISIBLE);
        }

        //A jegyzetre kattintva megjelenik Toast üzenetben annak leírása
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),noteModel.getDescription(),Toast.LENGTH_LONG).show();
            }
        });

        //A jegyzet szerkesztése
        holder.btEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoteModel note=noteModelList.get(holder.getAdapterPosition());
                //kiválasztott jegyzet adatainak lekérése
                final int sID=note.getId();
                String sTitle=note.getTitle();
                String sDescription=note.getDescription();
                int sPriority=note.getPriority();
                String sDate=note.getDate();

                //Edit-dialógus indítása
                final Dialog dialog=new Dialog(context);
                dialog.setContentView(R.layout.dialog_update);

                int width= WindowManager.LayoutParams.MATCH_PARENT;
                int height=WindowManager.LayoutParams.WRAP_CONTENT;
                dialog.getWindow().setLayout(width,height);
                dialog.show();

                //dialóguson lévő editTextek beállítása
                final EditText editTitle=dialog.findViewById(R.id.title_note);
                final EditText editDescription=dialog.findViewById(R.id.note_description);
                final Spinner priority=dialog.findViewById(R.id.spinner1);
                final TextView date=dialog.findViewById(R.id.noteDueDate);

                //Prioritás beállítása
                final ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(v.getContext(),R.array.priorities, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                priority.setAdapter(adapter);

                Button btnpdate=dialog.findViewById(R.id.btn_update);

                editTitle.setText(sTitle);
                editDescription.setText(sDescription);
                priority.setSelection(sPriority);
                date.setText(sDate);

                //Dátum beállítása
                date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDatePickerDialog(v,date);
                    }
                });

                //Jegyzet módosításának mentése
                btnpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        String mTitle=editTitle.getText().toString().trim();
                        String mDescription=editDescription.getText().toString().trim();
                        int mPriority=priority.getSelectedItemPosition();
                        String mDate=date.getText().toString().trim();
                        noteDatabase.noteDao().update(sID,mTitle,mDescription,mPriority,mDate);

                        noteModelList.clear();
                        noteModelList.addAll(noteDatabase.noteDao().getAllNotes());
                        notifyDataSetChanged();
                    }
                });
            }
        });

        //Jegyzet törlése
        holder.btDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoteModel d=noteModelList.get(holder.getAdapterPosition());
                noteDatabase.noteDao().deleteNote(d);
                int position=holder.getAdapterPosition();
                noteModelList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,noteModelList.size());
            }
        });
    }

    private void showDatePickerDialog(final View v, final TextView t) {
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

                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yy-MM-dd HH:mm");

                        t.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                };
                new TimePickerDialog(v.getContext(),timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false).show();
            }
        };
        new DatePickerDialog(v.getContext(),dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Override
    public int getItemCount() {
        return noteModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView priority;
        TextView title,date;
        ImageView btEdit,btDelete;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            title=itemView.findViewById(R.id.noteTitle);
            date=itemView.findViewById(R.id.noteDate);
            priority=itemView.findViewById(R.id.priority_note);

            btEdit=itemView.findViewById(R.id.noteEditbtn);
            btDelete=itemView.findViewById(R.id.noteDeletebtn);
        }
    }
}

package com.example.szakdoga.main_menu_to_organizer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.szakdoga.R;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
/**
 * A főmenü aminek alján található egy navigációs sáv,
 * ennek segítségével válthatunk a fragment-ek között
 */
public class NavigationBar extends AppCompatActivity {

    //vÁLTOZÓK
    ChipNavigationBar bottomNav;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_bar);

        bottomNav = findViewById(R.id.bottom_nav);
        fragmentManager = getSupportFragmentManager();
        //A kiválasztott fesztivál ID-ját megkapja
        Intent intent=getIntent();
        String felstivalID=intent.getStringExtra("FestivalId");

        //Fragmentek:4 típus
        final HomeFragment home=new HomeFragment(felstivalID);
        final NotesFragment notes=new NotesFragment();
        final MapFragment map=new MapFragment(felstivalID);
        final OrganizerFragment organizerFragment=new OrganizerFragment();

        //Ha belépünk az alkalmazásba akkor alapértelmezetten a Home fragment jelenik meg
        makeCurrentFragment(home);
        bottomNav.setItemSelected(R.id.home, true);

        //Itt pedig válthatunk a fragment-ek között
        bottomNav.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onItemSelected(int id) {
                switch (id) {
                    case R.id.home:
                        makeCurrentFragment(home);
                        break;
                    case R.id.notes:
                        makeCurrentFragment(notes);
                        break;
                    case R.id.map:
                        makeCurrentFragment(map);
                        break;
                    case R.id.profile0:
                        makeCurrentFragment(organizerFragment);
                        break;
                }
            }
        });
    }
    //Váltás a fragmentek között
    private void makeCurrentFragment(Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
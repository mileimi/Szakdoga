package com.example.szakdoga.main_menu_to_participants;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.szakdoga.R;
import com.example.szakdoga.main_menu_to_organizer.MapFragment;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
/**
 * Navigációs activity a fragmentek között
 */
public class Navigation extends AppCompatActivity {

    ChipNavigationBar bottomNav;
    FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation2);

        //Fesztivál ID
        Intent intent=getIntent();
        String festID=intent.getStringExtra("FestivalId");

        bottomNav = findViewById(R.id.bottom_nav2);
        fragmentManager = getSupportFragmentManager();

        //Fragmentek
        final EventsFragment home=new EventsFragment(festID);
        final MapFragment map=new MapFragment(festID);
        final ProfileFragment profile=new ProfileFragment(festID);

        //Ha belépünk az alkalmazásba akkor alapértelmezetten a Home fragment jelenik meg
        makeCurrentFragment(home);
        bottomNav.setItemSelected(R.id.events, true);

        //Itt pedig válthatunk a fragment-ek között
        bottomNav.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onItemSelected(int id) {
                switch (id) {
                    case R.id.events:
                        makeCurrentFragment(home);
                        break;
                    case R.id.map2:
                        makeCurrentFragment(map);
                        break;
                    case R.id.profile:
                        makeCurrentFragment(profile);
                        break;
                }
            }
        });
    }
    //Váltás a fragmentek között
    private void makeCurrentFragment(Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container2, fragment)
                .commit();
    }
}
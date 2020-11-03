package com.example.szakdoga.main_menu;
/**
 * A főmenü aminek alján található egy navigációs sáv,
 * ennek segítségével válthatunk a fragment-ek között
 */
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;

import com.example.szakdoga.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class NavigationBar extends AppCompatActivity {

    ChipNavigationBar bottomNav;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_bar);

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        bottomNav = findViewById(R.id.bottom_nav);


        //Ha belépünk az alkalmazásba akkor alapértelmezetten a Home fragment jelenik meg
        if (savedInstanceState==null){
            bottomNav.setItemSelected(R.id.home,true);
            fragmentManager=getSupportFragmentManager();
            HomeFragment homeFragment=new HomeFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container,homeFragment)
                    .commit();
        }
        //Itt pedig válthatunk a fragment-ek között
        bottomNav.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {
                Fragment fragment=null;
                switch (id){
                    case R.id.home:
                        fragment=new HomeFragment();
                        break;
                    case R.id.notes:
                        fragment=new NotesFragment();
                        break;
                    case R.id.map:
                        fragment=new MapFragment();
                        break;
                }
                if (fragment!=null){
                    fragmentManager=getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment_container,fragment)
                            .commit();
                }
                else{
                    Log.e("TAG","ERROR in creating fragment");
                }
            }
        });


        };

    }

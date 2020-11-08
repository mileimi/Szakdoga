package com.example.szakdoga.main_menu_to_participants;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.szakdoga.R;
import com.example.szakdoga.main_menu.HomeFragment;
import com.example.szakdoga.main_menu.MapFragment;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class Navigation extends AppCompatActivity {

    ChipNavigationBar bottomNav;
    FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation2);

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        bottomNav = findViewById(R.id.bottom_nav2);


        //Ha belépünk az alkalmazásba akkor alapértelmezetten a Home fragment jelenik meg
        if (savedInstanceState==null){
            bottomNav.setItemSelected(R.id.events,true);
            fragmentManager=getSupportFragmentManager();
            HomeFragment homeFragment=new HomeFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container2,homeFragment)
                    .commit();
        }
        //Itt pedig válthatunk a fragment-ek között
        bottomNav.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {
                Fragment fragment=null;
                switch (id){
                    case R.id.events:
                        fragment=new HomeFragment();
                        break;
                    case R.id.map2:
                        fragment=new MapFragment();
                        break;
                }
                if (fragment!=null){
                    fragmentManager=getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment_container2,fragment)
                            .commit();
                }
                else{
                    Log.e("TAG","ERROR in creating fragment");
                }
            }
        });


    };

}
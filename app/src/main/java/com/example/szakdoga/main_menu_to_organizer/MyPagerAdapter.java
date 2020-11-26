package com.example.szakdoga.main_menu_to_organizer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.szakdoga.R;

import java.util.ArrayList;
/**
 * A ViewPager-hez tartozó adapter, ami megkapja a képek elérési útját egy listában
 */
public class MyPagerAdapter extends PagerAdapter {
    private android.content.Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<String> imagePaths;

    public MyPagerAdapter(android.content.Context context,ArrayList<String> imagePaths) {
       this.context = context;
       this.imagePaths = imagePaths;
    }

    @Override
    public int getCount() {
        return imagePaths.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View view=layoutInflater.inflate(R.layout.image_item,null);
        ImageView imageView=view.findViewById(R.id.image_view_item);
        Glide.with(view)
                .load(imagePaths.get(position))
                .into(imageView);
        ViewPager viewPager=(ViewPager) container;
        viewPager.addView(view,0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager viewPager=(ViewPager) container;
        View view=(View) object;
        viewPager.removeView(view);
    }

}

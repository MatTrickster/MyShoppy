package com.e.myshoppy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

public class Categories extends Fragment {

    Context context;
    GridView gridView;
    String type;

    public Integer[] img = {
            R.mipmap.clothing, R.mipmap.jewellary,
            R.mipmap.electonics, R.mipmap.medicine,
            R.mipmap.grocery, R.mipmap.restaurent,
            R.mipmap.dep_store, R.mipmap.furniture,
            R.mipmap.cosmetics, R.mipmap.books,
            R.mipmap.music, R.mipmap.elec_repair, R.mipmap.textile
    };

    public Categories(){

    }

    public Categories(Context c,String type){
        context = c;
        this.type = type;
    }

    final String[] category = {
            "Clothing", "Jewellery", "Electronics", "Medicine", "Grocery", "Restaurant","Departmental Stores",
            "Furniture","Cosmetics","Books","Musical Instruments","Electronics Repairing","Textile"

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_categories, container, false);
        gridView = view.findViewById(R.id.grid);
        gridView.setAdapter(new GridViewAdapter(context,img,category));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if(type.equals("customer")) {
                    Intent intent = new Intent(context, ShopsActivity.class);
                    intent.putExtra("category", category[i]);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(context, ProductsActivity.class);
                    intent.putExtra("type", category[i]);
                    startActivity(intent);
                }

            }
        });


        final RecyclerView recyclerView = view.findViewById(R.id.horizontal_recycler);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        SnapHelper snapHelper = new PagerSnapHelper();
        recyclerView.setLayoutManager(layoutManager);
        snapHelper.attachToRecyclerView(recyclerView);
        final BannerAdapter banner = new BannerAdapter(getContext(),img);
        recyclerView.setAdapter(banner);

        final int speedScroll = 2000;
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            int count = 0;
            boolean flag = true;
            @Override
            public void run() {
                if(count < banner.getItemCount()){
                    if(count==banner.getItemCount()-1){
                        flag = false;
                    }else if(count == 0){
                        flag = true;
                    }
                    if(flag) count++;
                    else count--;

                    recyclerView.smoothScrollToPosition(count);
                    handler.postDelayed(this,speedScroll);
                }
            }
        };
        handler.postDelayed(runnable,speedScroll);

        return view;
    }
}
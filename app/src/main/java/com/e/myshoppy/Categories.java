package com.e.myshoppy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

        /*
        final RecyclerView rv_autoScroll = view.findViewById(R.id.banner);
        final int duration = 10;
        final int pixelsToMove = 30;
        final Handler mHandler = new Handler(Looper.getMainLooper());
        final Runnable SCROLLING_RUNNABLE = new Runnable() {

            @Override
            public void run() {
                rv_autoScroll.smoothScrollBy(pixelsToMove, 0);
                mHandler.postDelayed(this, duration);
            }
        };

        final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_autoScroll.setLayoutManager(layoutManager);

        rv_autoScroll.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastItem = layoutManager.findLastCompletelyVisibleItemPosition();
                if(lastItem == layoutManager.getItemCount()-1){
                    mHandler.removeCallbacks(SCROLLING_RUNNABLE);
                    Handler postHandler = new Handler();
                    postHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            rv_autoScroll.setAdapter(null);
                            mHandler.postDelayed(SCROLLING_RUNNABLE, 2000);
                        }
                    }, 2000);
                }
            }
        });
        mHandler.postDelayed(SCROLLING_RUNNABLE, 2000);

         */

        return view;
    }
}
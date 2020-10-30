package com.e.myshoppy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
                    Intent intent = new Intent(context, UserProductActivity.class);
                    intent.putExtra("category", category[i]);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(context, ProductsActivity.class);
                    intent.putExtra("type", category[i]);
                    startActivity(intent);
                }

            }
        });
        return view;
    }
}
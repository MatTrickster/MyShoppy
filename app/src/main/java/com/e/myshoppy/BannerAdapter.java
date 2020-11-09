package com.e.myshoppy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.ViewHolder> {

    Integer[] list;
    Context context;

    public BannerAdapter(Context context,Integer[] list){
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public BannerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.banner_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerAdapter.ViewHolder holder, int position) {

        holder.itemView.setTag(list[position]);
        holder.imageView.setImageResource(list[position]);

    }

    @Override
    public int getItemCount() {
        return list.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        ViewHolder(View view) {
            super(view);

            imageView = view.findViewById(R.id.banner);

        }

    }
}

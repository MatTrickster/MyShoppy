package com.e.myshoppy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;

public class ShopsAdapter extends ArrayAdapter<Shop> {

    Context context;
    ArrayList<Shop> list;

    public ShopsAdapter(Context context,ArrayList<Shop> list) {
        super(context, 0, list);
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.shop_list_item, parent, false
            );
        }

        Shop shop = getItem(position);

        ImageView imageView = listItemView.findViewById(R.id.image);
        TextView name = listItemView.findViewById(R.id.name);
        TextView address = listItemView.findViewById(R.id.address);

        name.setText(shop.getName());
        address.setText(shop.getAddress());

        return listItemView;
    }
}

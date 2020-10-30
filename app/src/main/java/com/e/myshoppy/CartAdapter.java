package com.e.myshoppy;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    Context context;
    List<ShoppingItem> items ;
    String type;

    public CartAdapter(Context context, List<ShoppingItem> items, String type){
        this.context = context;
        this.items = items;
        this.type = type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        holder.itemView.setTag(items.get(position));

        ShoppingItem pu = items.get(position);

        holder.name.setText(pu.getTitle());
        holder.price.setText(pu.getPrice());
        holder.quan.setText(""+pu.getQuantity());
        holder.total.setText("Rs. "+Integer.valueOf(pu.getPrice())*pu.getQuantity());

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageButton img;
        public TextView name,quan,price,total;

        ViewHolder(View view) {
            super(view);
            img = view.findViewById(R.id.removeFromCart);
            name = view.findViewById(R.id.cartItemName);
            quan = view.findViewById(R.id.cartItemQuantity);
            price = view.findViewById(R.id.cartItemPrice);
            total = view.findViewById(R.id.cartItemTotal);

        }

    }
}

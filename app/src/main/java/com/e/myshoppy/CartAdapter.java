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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    Context context;
    List<ShoppingItem> items ;
    String type;
    DataSnapshot snap;

    public CartAdapter(Context context, List<ShoppingItem> items, String type, DataSnapshot snap){
        this.context = context;
        this.items = items;
        this.type = type;
        this.snap = snap;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {


        holder.itemView.setTag(items.get(position));

        ShoppingItem pu = items.get(position);

        if(!type.equals("cart"))
            holder.remove.setVisibility(View.GONE);

        holder.name.setText(pu.getTitle());
        holder.price.setText(pu.getPrice());
        holder.quan.setText(""+pu.getQuantity());
        holder.total.setText("Rs. "+Integer.valueOf(pu.getPrice())*pu.getQuantity());
        Picasso.with(context).load(pu.getPath()).placeholder(R.drawable.loading_gif).into(holder.icon);

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/"+
                        FirebaseAuth.getInstance().getCurrentUser().getUid());

                if(snap.getChildrenCount() == 1){
                    ref.child("isCartEmpty").getRef().setValue(true);
                    ref.child("shopId").getRef().setValue("null");

                }
                items.remove(position);
                ref.child("cartItems").child(""+position).removeValue();

            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageButton remove;
        public TextView name,quan,price,total;
        public ImageView icon;


        ViewHolder(View view) {
            super(view);

            icon = view.findViewById(R.id.cartItemIcon);
            remove = view.findViewById(R.id.removeFromCart);
            name = view.findViewById(R.id.cartItemName);
            quan = view.findViewById(R.id.cartItemQuantity);
            price = view.findViewById(R.id.cartItemPrice);
            total = view.findViewById(R.id.cartItemTotal);

        }

    }
}

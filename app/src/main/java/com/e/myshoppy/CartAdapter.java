package com.e.myshoppy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends ArrayAdapter<ShoppingItem> {

    Context context;
    ArrayList<ShoppingItem> items;

    public CartAdapter(Context context, List<ShoppingItem> items){
        super(context, 0, items);
        this.context = context;
        this.items = (ArrayList<ShoppingItem>) items;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.cart_item, parent, false
            );
        }

        ShoppingItem currentItem = getItem(position);

        ImageButton img = listItemView.findViewById(R.id.removeFromCart);
        img.setImageResource(R.drawable.remove);

        ((TextView) listItemView.findViewById(R.id.cartItemName))
                .setText(currentItem.getTitle());

        String x = "x " + currentItem.getQuantity();
        ((TextView) listItemView.findViewById(R.id.cartItemQuantity))
                .setText(x);

        String itemPrice = currentItem.getPrice();
        ((TextView) listItemView.findViewById(R.id.cartItemPrice))
                .setText("Rs. " + itemPrice);

        ((TextView) listItemView.findViewById(R.id.cartItemTotal))
                .setText("Rs. "+String.valueOf(Integer.parseInt(itemPrice) * currentItem.getQuantity()));

        return listItemView;
    }

}

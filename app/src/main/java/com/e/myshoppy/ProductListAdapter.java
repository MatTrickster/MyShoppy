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

import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductListAdapter extends ArrayAdapter<ShoppingItem> {

    Context context;

    public ProductListAdapter(Context context, List<ShoppingItem> items){
        super(context, 0, items);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false
            );
        }

        ShoppingItem currentItem = getItem(position);

        ImageView img = listItemView.findViewById(R.id.itemIcon);

        TextView name = listItemView.findViewById(R.id.itemName);
        name.setText(currentItem.getTitle());

        TextView description = listItemView.findViewById(R.id.itemDescription);
        description.setText(currentItem.getDescription());

        TextView cost = listItemView.findViewById(R.id.itemPrice);
        cost.setText(currentItem.getPrice());

        TextView quantity = listItemView.findViewById(R.id.quantity);
        quantity.setText(currentItem.getQuantity());

        TextView minus = listItemView.findViewById(R.id.minus);
        TextView plus = listItemView.findViewById(R.id.plus);

        minus.setText("-");
        plus.setText("+");

        return listItemView;
    }

}

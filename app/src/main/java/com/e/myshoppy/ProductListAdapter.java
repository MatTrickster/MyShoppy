package com.e.myshoppy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductListAdapter extends ArrayAdapter<ShoppingItem> {

    Context context;
    String user;

    public ProductListAdapter(Context context, List<ShoppingItem> items,String user){
        super(context, 0, items);
        this.context = context;
        this.user = user;
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

        final ShoppingItem currentItem = getItem(position);

        ImageView img = listItemView.findViewById(R.id.itemIcon);

        TextView name = listItemView.findViewById(R.id.itemName);
        name.setText(currentItem.getTitle());

        TextView description = listItemView.findViewById(R.id.itemDescription);
        description.setText(currentItem.getDescription());

        TextView cost = listItemView.findViewById(R.id.itemPrice);
        cost.setText(currentItem.getPrice());

        Button plus = listItemView.findViewById(R.id.plus);
        Button minus = listItemView.findViewById(R.id.minus);
        View change = listItemView.findViewById(R.id.change);

        if(user.equals("seller")) {
            TextView quantity = listItemView.findViewById(R.id.quantity);
            String quan = String.valueOf(currentItem.getQuantity());
            quantity.setText(quan);
            plus.setText("+");
            minus.setText("-");
        }else{
            change.setVisibility(View.GONE);
        }

        return listItemView;
    }

}

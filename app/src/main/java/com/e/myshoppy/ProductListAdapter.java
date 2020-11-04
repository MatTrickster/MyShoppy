package com.e.myshoppy;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProductListAdapter extends ArrayAdapter<ShoppingItem> {

    String cat;
    ArrayList<String> refs;
    Context context;
    String user;

    public ProductListAdapter(Context context, List<ShoppingItem> items,ArrayList<String> refs,String cat,String user){
        super(context, 0, items);
        this.context = context;
        this.user = user;
        this.refs = refs;
        this.cat = cat;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false
            );
        }

        final ShoppingItem currentItem = getItem(position);

        ImageView img = listItemView.findViewById(R.id.itemIcon);
        Picasso.with(context).load(currentItem.getPath()).placeholder(R.drawable.add).into(img);

        TextView name = listItemView.findViewById(R.id.itemName);
        name.setText(currentItem.getTitle());

        TextView description = listItemView.findViewById(R.id.itemDescription);
        description.setText(currentItem.getDescription());

        TextView cost = listItemView.findViewById(R.id.itemPrice);
        cost.setText(currentItem.getPrice());

        Button plus = listItemView.findViewById(R.id.plus);
        Button minus = listItemView.findViewById(R.id.minus);

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("shopkeepers/" +
                        FirebaseAuth.getInstance().getCurrentUser().getUid() + "/products/" + cat +"/" +
                        refs.get(position) + "/") ;
                ref.child("quantity").setValue(currentItem.getQuantity()+1);
            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("shopkeepers/" +
                        FirebaseAuth.getInstance().getCurrentUser().getUid() + "/products/" + cat +"/" +
                        refs.get(position) + "/") ;
                if(currentItem.getQuantity() == 0)
                    Toast.makeText(getContext(),"Item is already Out of Stock",Toast.LENGTH_SHORT).show();
                else
                    ref.child("quantity").setValue(currentItem.getQuantity()-1);
            }
        });

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

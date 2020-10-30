package com.e.myshoppy;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ScrollView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Orders extends Fragment {

    OrderParentAdapter parentItemAdapter;
    Context context;
    String type;

    public Orders(Context c, String type) {
        context = c;
        this.type = type;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_orders, container, false);
        RecyclerView currentList = view.findViewById(R.id.list1);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);

        parentItemAdapter = new OrderParentAdapter(ParentItemList(), getContext());
        currentList.setAdapter(parentItemAdapter);
        currentList.setLayoutManager(layoutManager);

        return view;
    }

    public List<OrderParentItem> ParentItemList() {

        final List<OrderParentItem> items = new ArrayList<>();
        DatabaseReference ref;

        if (type.equals("customer")) {

            ref = FirebaseDatabase.getInstance().getReference("users/" +
                    FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + "orders/");
        } else {
            ref = FirebaseDatabase.getInstance().getReference("shopkeepers/" +
                    FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + "orders/");
        }
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int i=0;
                for (DataSnapshot snap : snapshot.getChildren()) {

                    List<ShoppingItem> item = ChildItemList(snap,type.equals("customer")?0:1);
                    items.add(new OrderParentItem("Order " + snap.getKey(),
                            item, "Rs ." + snap.child("amount").getValue().toString()));

                    i++;
                }

                parentItemAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return items;
    }

    public List<ShoppingItem> ChildItemList(DataSnapshot snap,int x) {

        List<ShoppingItem> item = new ArrayList<>();
        Log.i("TAG","as"+snap.getChildrenCount());
        long loop = (x==0)?snap.getChildrenCount()-1:snap.getChildrenCount()-2;
        for (int i = 0; i < loop; i++) {

            item.add(new ShoppingItem(
                    "",
                    snap.child(String.valueOf(i)).child("title").getValue().toString(),
                    "",
                    snap.child(String.valueOf(i)).child("description").getValue().toString(),
                    snap.child(String.valueOf(i)).child("price").getValue().toString(),
                    Integer.valueOf(snap.child(String.valueOf(i)).child("quantity").getValue().toString()),
                    "",null
            ));

        }

        return item;
    }
}
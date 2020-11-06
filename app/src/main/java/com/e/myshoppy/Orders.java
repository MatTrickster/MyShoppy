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
import android.widget.TextView;

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

    OrderParentAdapter parentOngoingOrderAdapter,parentPastOrderAdapter;
    Context context;
    String type;
    TextView current,past;
    ArrayList<DataSnapshot> snaps1 = new ArrayList<>(), snaps2 = new ArrayList<>();

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
        final RecyclerView currentList = view.findViewById(R.id.list1);
        final RecyclerView pastList = view.findViewById(R.id.list2);
        current = view.findViewById(R.id.current);
        past = view.findViewById(R.id.past);

        current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentList.getVisibility() == View.GONE)
                    currentList.setVisibility(View.VISIBLE);
                else
                    currentList.setVisibility(View.GONE);
            }
        });

        past.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pastList.getVisibility() == View.GONE)
                    pastList.setVisibility(View.VISIBLE);
                else
                    pastList.setVisibility(View.GONE);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        parentOngoingOrderAdapter = new OrderParentAdapter(ParentItemList("current"), getContext(),type,snaps1);
        currentList.setAdapter(parentOngoingOrderAdapter);
        currentList.setLayoutManager(layoutManager);


        LinearLayoutManager layoutManager1 = new LinearLayoutManager(context);
        parentPastOrderAdapter = new OrderParentAdapter(ParentItemList("past"), getContext(),type,snaps2);
        pastList.setAdapter(parentPastOrderAdapter);
        pastList.setLayoutManager(layoutManager1);

        return view;
    }

    public List<OrderParentItem> ParentItemList(final String list) {

        final List<OrderParentItem> items = new ArrayList<>();
        DatabaseReference ref;

        if (type.equals("customer")) {

            if(list.equals("current")) {
                ref = FirebaseDatabase.getInstance().getReference("users/" +
                        FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + "orders/");
            }else{
                ref = FirebaseDatabase.getInstance().getReference("users/" +
                        FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + "past orders/");
            }
        } else {
            if(list.equals("current")) {
                ref = FirebaseDatabase.getInstance().getReference("shopkeepers/" +
                        FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + "orders/");
            }else{
                ref = FirebaseDatabase.getInstance().getReference("shopkeepers/" +
                        FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + "past orders/");
            }
        }
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                items.clear();

                if(list.equals("current"))
                    snaps1.clear();
                else
                    snaps2.clear();

                int i=0;
                for (DataSnapshot snap : snapshot.getChildren()) {

                    List<ShoppingItem> item = ChildItemList(snap,type.equals("customer")?0:1);
                    items.add(new OrderParentItem("Order " + snap.getKey(),
                            item, "Rs. " + snap.child("amount").getValue().toString()));
                    if(list.equals("current"))
                        snaps1.add(snap);
                    else
                        snaps2.add(snap);
                    i++;
                }

                parentOngoingOrderAdapter.notifyDataSetChanged();

                if(list.equals("current"))
                    current.setText("Ongoing Orders (" + items.size() +")");
                else
                    past.setText("Past Orders (" + items.size() +")");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return items;
    }

    public List<ShoppingItem> ChildItemList(DataSnapshot snap,int x) {

        List<ShoppingItem> item = new ArrayList<>();
        long loop = (x==0)?snap.getChildrenCount()-3:snap.getChildrenCount()-5;
        for (int i = 0; i < loop; i++) {

            item.add(new ShoppingItem(
                    "",
                    snap.child(String.valueOf(i)).child("title").getValue().toString(),
                    "",
                    snap.child(String.valueOf(i)).child("description").getValue().toString(),
                    snap.child(String.valueOf(i)).child("price").getValue().toString(),
                    Integer.valueOf(snap.child(String.valueOf(i)).child("quantity").getValue().toString()),
                    "",
                    snap.child(String.valueOf(i)).child("path").getValue().toString()
            ));

        }

        return item;
    }
}
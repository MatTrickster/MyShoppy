package com.e.myshoppy;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrderParentAdapter extends RecyclerView.Adapter<OrderParentAdapter.ParentViewHolder>{

    Context context;
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private List<OrderParentItem> itemList;
    String type;
    ArrayList<DataSnapshot> snaps;

    OrderParentAdapter(List<OrderParentItem> itemList, Context context, String type, ArrayList<DataSnapshot> snaps) {
        this.context = context;
        this.itemList = itemList;
        this.type = type;
        this.snaps = snaps;
    }

    @NonNull
    @Override
    public OrderParentAdapter.ParentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_parent_view, parent, false);

        return new ParentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderParentAdapter.ParentViewHolder parentViewHolder, final int position) {

        OrderParentItem parentItem = itemList.get(position);

        parentViewHolder.number.setText(parentItem.getNumber());
        parentViewHolder.price.setText(parentItem.getPrice());

        LinearLayoutManager layoutManager = new LinearLayoutManager(parentViewHolder.ChildRecyclerView
                        .getContext(), LinearLayoutManager.VERTICAL, false);

        layoutManager.setInitialPrefetchItemCount(parentItem.getChildItemList().size());

        CartAdapter childItemAdapter = new CartAdapter(context,parentItem.getChildItemList(),"");
        parentViewHolder.ChildRecyclerView.setLayoutManager(layoutManager);
        parentViewHolder.ChildRecyclerView.setAdapter(childItemAdapter);
        parentViewHolder.ChildRecyclerView .setRecycledViewPool(viewPool);

        parentViewHolder.status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(type.equals("customer")){

                    


                }else{

                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class ParentViewHolder extends RecyclerView.ViewHolder {

        private TextView number,price,status;
        private RecyclerView ChildRecyclerView;

        ParentViewHolder(final View itemView)
        {
            super(itemView);

            number = itemView.findViewById(R.id.number);
            ChildRecyclerView = itemView.findViewById(R.id.child);
            price = itemView.findViewById(R.id.price);
            status = itemView.findViewById(R.id.status);
        }
    }
}

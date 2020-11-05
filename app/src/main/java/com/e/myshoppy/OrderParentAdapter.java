package com.e.myshoppy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Calendar;
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

        CartAdapter childItemAdapter = new CartAdapter(context,parentItem.getChildItemList(),"",null);
        parentViewHolder.ChildRecyclerView.setLayoutManager(layoutManager);
        parentViewHolder.ChildRecyclerView.setAdapter(childItemAdapter);
        parentViewHolder.ChildRecyclerView .setRecycledViewPool(viewPool);

        parentViewHolder.status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View view1 = LayoutInflater.from(context).inflate(R.layout.order_status,null);
                final DatePicker picker = view1.findViewById(R.id.picker);
                final TextView ongoing = view1.findViewById(R.id.ongoing);
                final TextView delivered = view1.findViewById(R.id.delivered);
                String  isDelivered = snaps.get(position).child("delivered").getValue().toString();
                Log.i("TAG","x"+isDelivered+" "+position);

                if(type.equals("seller") && isDelivered.equals("false")){

                    final int[] val = {0};
                    picker.setMinDate(System.currentTimeMillis() - 1000);
                    ongoing.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ongoing.setBackgroundColor(Color.parseColor("#FF5722"));
                            delivered.setTextColor(Color.parseColor("#000000"));
                            delivered.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            val[0] = 0;
                        }
                    });
                    delivered.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            delivered.setBackgroundColor(Color.parseColor("#25BA2B"));
                            ongoing.setTextColor(Color.parseColor("#000000"));
                            ongoing.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            val[0] = 1;

                        }
                    });
                    AlertDialog dialog = new AlertDialog.Builder(context)
                            .setView(view1)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    String date = ""+picker.getDayOfMonth()+"/"+(picker.getMonth()+1)+"/"+picker.getYear();
                                    String uid = snaps.get(position).child("userId").getValue().toString();
                                    String orderNo = snaps.get(position).child("user order number").getValue().toString();

                                    DatabaseReference ref = FirebaseDatabase.getInstance()
                                            .getReference("users/"+uid+"/orders/"+orderNo+"/");
                                    ref.child("expected delivery").setValue(date);
                                    snaps.get(position).child("expected delivery").getRef().setValue(date);

                                    if(val[0]==1){
                                        ref.child("delivered").setValue(true);
                                        snaps.get(position).child("delivered").getRef().setValue(true);

                                        //TODO

                                    }else{
                                        ref.child("delivered").setValue(false);
                                        snaps.get(position).child("delivered").getRef().setValue(false);
                                    }

                                }
                            })
                            .create();
                    dialog.show();


                }else if(type.equals("seller") && isDelivered.equals("true")){

                    String date =  snaps.get(position).child("expected delivery").getValue().toString();
                    String []d =date.split("/");
                    d[1] = String.valueOf(Integer.parseInt(d[1]) - 1);
                    picker.updateDate(Integer.parseInt(d[2]),Integer.parseInt(d[1]),Integer.parseInt(d[0]));
                    picker.setEnabled(false);
                    delivered.setBackgroundColor(Color.parseColor("#25BA2B"));
                    ongoing.setTextColor(Color.parseColor("#000000"));
                    ongoing.setBackgroundColor(Color.parseColor("#FFFFFF"));

                    AlertDialog dialog = new AlertDialog.Builder(context)
                            .setView(view1)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {


                                }
                            })
                            .create();
                    dialog.show();
                }else{
                    String date =  snaps.get(position).child("expected delivery").getValue().toString();

                    if(date.equals("null")){
                        picker.setVisibility(View.GONE);
                        TextView soon = view1.findViewById(R.id.soon);
                        soon.setVisibility(View.VISIBLE);
                    }else{
                        String []d =date.split("/");
                        d[1] = String.valueOf(Integer.parseInt(d[1]) - 1);
                        picker.updateDate(Integer.parseInt(d[2]),Integer.parseInt(d[1]),Integer.parseInt(d[0]));
                    }

                    picker.setEnabled(false);

                    if(isDelivered.equals("true")){
                        delivered.setBackgroundColor(Color.parseColor("#25BA2B"));
                        ongoing.setTextColor(Color.parseColor("#000000"));
                        ongoing.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    }else{
                        ongoing.setBackgroundColor(Color.parseColor("#FF5722"));
                        delivered.setTextColor(Color.parseColor("#000000"));
                        delivered.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    }

                    AlertDialog dialog = new AlertDialog.Builder(context)
                            .setView(view1)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            })
                            .create();
                    dialog.show();
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
        private ImageView icon;

        ParentViewHolder(final View itemView)
        {
            super(itemView);

            icon = itemView.findViewById(R.id.cartItemIcon);
            number = itemView.findViewById(R.id.number);
            ChildRecyclerView = itemView.findViewById(R.id.child);
            price = itemView.findViewById(R.id.price);
            status = itemView.findViewById(R.id.status);

        }
    }
}

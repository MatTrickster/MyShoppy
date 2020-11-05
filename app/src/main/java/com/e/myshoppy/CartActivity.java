package com.e.myshoppy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref, ref1, ref2;
    Boolean isCartEmpty = true;
    TextView priceView, empty;
    private FirebaseUser user;
    String sId="null";
    ArrayList<Integer> stocks;
    int totalAmount = 0;
    ArrayList<ShoppingItem> items;
    DataSnapshot data;
    Boolean possible = true;
    Boolean alreadyOrderExist = false, shopOrdersExist = false, isAddressAvailable = false;
    long noOfOrders = 0, noOfShopOrders = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        user = FirebaseAuth.getInstance().getCurrentUser();
        ref = database.getReference("users/" + user.getUid());
        priceView = findViewById(R.id.totalPriceCheckout);
        empty = findViewById(R.id.empty);
        stocks = new ArrayList<>();
        items = new ArrayList<>();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getKey().equals(user.getUid())) {

                    isCartEmpty = (Boolean) dataSnapshot.child("isCartEmpty").getValue();
                    sId = dataSnapshot.child("shopId").getValue().toString();

                    if(dataSnapshot.child("info").getChildrenCount() > 2)
                        isAddressAvailable = true;

                    if(dataSnapshot.child("orders").getChildrenCount()!=0){
                        alreadyOrderExist = true;
                        noOfOrders = dataSnapshot.child("orders").getChildrenCount();
                    }

                    if (isCartEmpty) {
                        priceView.setText("0");
                        items = new ArrayList<>();
                        RecyclerView view = findViewById(R.id.shoppingCartList);
                        view.setHasFixedSize(true);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                        view.setLayoutManager(layoutManager);
                        view.setAdapter(new CartAdapter(getApplicationContext(),items,"cart",dataSnapshot));
                        empty.setVisibility(View.VISIBLE);

                    } else {
                        setUpShoppingCart(dataSnapshot.child("cartItems"));

                        ref1 = database.getReference().child("shopkeepers").child(sId).child("products");
                        ref1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                data = snapshot;

                                for(int i=0;i<items.size();i++){

                                    int quan = Integer.parseInt(snapshot.child(items.get(i).getType())
                                            .child(items.get(i).getProductID()).child("quantity").getValue().toString());

                                    stocks.add(quan);

                                    if(stocks.get(i) < items.get(i).getQuantity()){
                                        possible = false;
                                        break;
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        ref2 = database.getReference().child("shopkeepers").child(sId);
                        ref2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.child("orders").getChildrenCount() != 0){
                                    shopOrdersExist = true;
                                    noOfShopOrders = snapshot.child("orders").getChildrenCount();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });


        (findViewById(R.id.returnToPrevPage)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        (findViewById(R.id.order)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(items.size()>0)
                    Order();
                else{
                    Toast.makeText(getApplicationContext(),"Cart is Empty",Toast.LENGTH_SHORT).show();
                }

            }
        });

        (findViewById(R.id.clearCart)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearCart();
                Map<String, Object> shopId = new HashMap<>();
                shopId.put("shopId", "null");
                Toast.makeText(getApplicationContext(), "Cart Cleared", Toast.LENGTH_SHORT).show();
                ref.updateChildren(shopId);
                finish();
            }
        });

    }

    private void setUpShoppingCart(DataSnapshot dataSnapshot) {

        totalAmount = 0;

        if (items != null) {
            items.clear();
        } else {
            items = new ArrayList<>();
        }

        for (DataSnapshot snap : dataSnapshot.getChildren()) {

            String price = snap.child("price").getValue().toString();
            price = price.substring(4);

            int quantity = 0, itemPrice = Integer.valueOf(price);

            quantity = Integer.valueOf(snap.child("quantity").getValue().toString());

            items.add(new ShoppingItem(
                    snap.child("productID").getValue().toString(),
                    snap.child("title").getValue().toString(),
                    snap.child("type").getValue().toString(),
                    snap.child("description").getValue().toString(),
                    String.valueOf(itemPrice),
                    quantity,
                    snap.child("shopId").getValue().toString(),
                    snap.child("path").getValue().toString()
            ));

            totalAmount += quantity * itemPrice;
        }

        RecyclerView view = findViewById(R.id.shoppingCartList);
        view.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        view.setLayoutManager(layoutManager);
        view.setAdapter(new CartAdapter(getApplicationContext(),items,"cart",dataSnapshot));
        if(items.size()>0)
            empty.setVisibility(View.INVISIBLE);
        else
            empty.setVisibility(View.VISIBLE);

        priceView.setText("Rs. " + totalAmount);
    }

    private void clearCart() {

        if (!isCartEmpty) {
            DatabaseReference myRefClear = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
            myRefClear.child("cartItems").removeValue();

            Map<String, Object> cartState = new HashMap<>();
            cartState.put("isCartEmpty", Boolean.TRUE);

            myRefClear.updateChildren(cartState);

            isCartEmpty = true;


        } else {
            Toast.makeText(getApplicationContext(), "Nothing To Clear", Toast.LENGTH_SHORT).show();
        }
    }

    public void Order() {

        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
        builder.setMessage("Placing Order ...");
        AlertDialog dialog = builder.create();
        dialog.show();

        if(possible && isAddressAvailable){

            for(int i=0;i<items.size();i++){

                for(DataSnapshot snap: data.getChildren()){

                    if(snap.getKey().equals(items.get(i).getType())) {

                        for (DataSnapshot snap2 : snap.getChildren()) {

                            if (snap2.getKey().equals(items.get(i).getProductID())){

                                snap2.getRef().child("quantity").setValue(stocks.get(i) - items.get(i).getQuantity());

                            }

                        }
                    }

                }
            }

            if(alreadyOrderExist){
                ref.child("orders").child(""+(noOfOrders+1)).setValue(items);
            }else{

                Map<String,Object> order = new HashMap<>();
                order.put("1",items);
                ref.child("orders").updateChildren(order);

            }

            Map<String,Object> amount = new HashMap<>();
            amount.put("amount",totalAmount);
            ref.child("orders").child(""+(noOfOrders+1)).updateChildren(amount);

            if(shopOrdersExist){
                ref2.child("orders").child(""+(noOfShopOrders+1)).setValue(items);

            }else{
                Map<String,Object> order = new HashMap<>();
                order.put("1",items);
                ref2.child("orders").updateChildren(order);
            }

            Map<String,Object> userId = new HashMap<>();
            userId.put("userId",user.getUid());
            ref2.child("orders").child(""+(noOfShopOrders+1)).updateChildren(userId);
            ref2.child("orders").child(""+(noOfShopOrders+1)).updateChildren(amount);

            Map<String, Object> expectedDelivery = new HashMap<>();
            expectedDelivery.put("expected delivery", "null");
            Map<String, Object> delivered = new HashMap<>();
            delivered.put("delivered", false);
            Map<String,Object> userOrderNo = new HashMap<>();
            userOrderNo.put("user order number",""+(noOfOrders+1));
            ref.child("orders").child(""+(noOfOrders+1)).updateChildren(expectedDelivery);
            ref.child("orders").child(""+(noOfOrders+1)).updateChildren(delivered);
            ref2.child("orders").child(""+(noOfShopOrders+1)).updateChildren(expectedDelivery);
            ref2.child("orders").child(""+(noOfShopOrders+1)).updateChildren(delivered);
            ref2.child("orders").child(""+(noOfShopOrders+1)).updateChildren(userOrderNo);

            Map<String, Object> shopId = new HashMap<>();
            shopId.put("shopId", "null");

            ref.updateChildren(shopId);



            clearCart();

            final AlertDialog.Builder builder1 = new AlertDialog.Builder(CartActivity.this);
            View view = LayoutInflater.from(CartActivity.this).inflate(R.layout.order_placed,null);
            ImageView img = view.findViewById(R.id.gif);
            Glide.with(view).load(R.mipmap.tick).into(img);
            builder1.setView(view);
            builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    finish();
                }
            });
            final AlertDialog dialog1 = builder1.create();
            dialog1.show();

        }else if(!isAddressAvailable){
            Toast.makeText(getApplicationContext(),"Address Not Available",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(),"Out Of Stock",Toast.LENGTH_SHORT).show();
        }

        dialog.dismiss();

    }

}
package com.e.myshoppy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref, ref1;
    Boolean isCartEmpty = true;
    TextView priceView, empty;
    private FirebaseUser user;
    String sId="null";
    ArrayList<Integer> stocks;
    int totalAmount = 0;
    ArrayList<ShoppingItem> items;
    DataSnapshot data;
    Boolean possible = true;

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
                    Log.i("TAG","xdd"+sId);
                    if (isCartEmpty) {
                        priceView.setText("0");
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

                Order();

            }
        });

        (findViewById(R.id.clearCart)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearCart();
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
                    snap.child("shopId").getValue().toString()
            ));

            totalAmount += quantity * itemPrice;
        }

        ListView view = findViewById(R.id.shoppingCartList);
        view.setAdapter(new CartAdapter(getApplicationContext(), items));
        view.setEmptyView(empty);

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

            Toast.makeText(getApplicationContext(), "Cart Cleared", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Nothing To Clear", Toast.LENGTH_SHORT).show();
        }
    }

    public void Order() {

        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
        builder.setMessage("Placing Order ...");
        AlertDialog dialog = builder.create();
        dialog.show();

        if(possible){

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

            Map<String,Object> order = new HashMap<>();
            order.put("orders",items);
            ref.updateChildren(order);
            Toast.makeText(getApplicationContext(),"Order Placed",Toast.LENGTH_SHORT).show();

            clearCart();

            finish();
        }else{
            Toast.makeText(getApplicationContext(),"Out Of Stock",Toast.LENGTH_SHORT).show();
        }

        dialog.dismiss();

    }

}
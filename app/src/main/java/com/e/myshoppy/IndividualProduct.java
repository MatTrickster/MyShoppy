package com.e.myshoppy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class IndividualProduct extends AppCompatActivity {

    ShoppingItem item;
    String sId,pId,type;
    DatabaseReference reference;
    ProgressBar progressBar;
    TextView name,price,description,quantity;
    Button add,sub,addToCart;
    int val = 1;
    FirebaseUser user;
    DatabaseReference ref;
    DataSnapshot data;
    ArrayList<ShoppingItem> cartItems;
    private Boolean isItemAlreadyInCart = false;
    int indexOfAlreadyPresentItem = -1, stock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_product);


        user = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference(
                "users/" + user.getUid()+"/");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                data = snapshot;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        item = (ShoppingItem) getIntent().getSerializableExtra("item");

        sId = item.getShopId();
        pId = item.getProductID();
        type = item.getType();
        stock = item.getQuantity();

        progressBar = findViewById(R.id.progress);
        add = findViewById(R.id.plus);
        sub = findViewById(R.id.minus);
        name = findViewById(R.id.p_name);
        price = findViewById(R.id.price);
        description = findViewById(R.id.p_description);
        quantity = findViewById(R.id.p_quantity);
        addToCart = findViewById(R.id.add_to_cart);

        name.setText(item.getTitle());
        price.setText(item.getPrice());
        description.setText(item.getDescription());
        quantity.setText("1");

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddToCart();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                increment();
            }
        });

        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decrement();
            }
        });

        reference = FirebaseDatabase.getInstance().getReference(
                "shopkeepers/" + sId +"/products/" + type + "/" + pId +"/"
         );

        progressBar.setVisibility(View.GONE);
    }

    private void AddToCart() {

        String cartShopId = data.child("shopId").getValue().toString();

        if(cartShopId.equals("null")){
            cartItems = new ArrayList<>();
            Map<String, Object> cartState = new HashMap<>();
            cartState.put("isCartEmpty", Boolean.FALSE);

            Map<String, Object> shopId = new HashMap<>();
            shopId.put("shopId", sId);

            ref.updateChildren(shopId);
            ref.updateChildren(cartState);

            if (isItemAlreadyInCart) {

                if (cartItems.get(indexOfAlreadyPresentItem).getQuantity() + val <= stock) {
                    cartItems.get(indexOfAlreadyPresentItem)
                            .setQuantity(cartItems.get(indexOfAlreadyPresentItem).getQuantity() + val);

                    Map<String, Object> cartItemsMap = new HashMap<>();
                    cartItemsMap.put("cartItems", cartItems);
                    ref.updateChildren(cartItemsMap);

                    Toast.makeText(getApplicationContext(), "Added To Cart", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Out Of Stock", Toast.LENGTH_SHORT).show();
                }

            } else {

                if (val <= stock) {
                    item.setQuantity(val);
                    cartItems.add(item);

                    Map<String, Object> cartItemsMap = new HashMap<>();
                    cartItemsMap.put("cartItems", cartItems);
                    ref.updateChildren(cartItemsMap);

                    Toast.makeText(getApplicationContext(), "Added To Cart", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Out Of Stock", Toast.LENGTH_SHORT).show();
                }
            }
        }else if(cartShopId.equals(sId)){

            cartItems = new ArrayList<>();
            int tempIndex = 0;
            for (DataSnapshot snap : data.child("cartItems").getChildren()) {

                String itemPrice = snap.child("price").getValue().toString();
                String productID = snap.child("productID").getValue().toString();

                if (productID.equals(item.getProductID())) {
                    isItemAlreadyInCart = true;
                    indexOfAlreadyPresentItem = tempIndex;
                }

                cartItems.add(new ShoppingItem(
                        productID,
                        snap.child("title").getValue().toString(),
                        snap.child("type").getValue().toString(),
                        snap.child("description").getValue().toString(),
                        itemPrice,
                        Integer.valueOf(snap.child("quantity").getValue().toString()),
                        sId
                ));

                tempIndex++;
            }

            if (isItemAlreadyInCart) {

                if (cartItems.get(indexOfAlreadyPresentItem).getQuantity() + val <= stock) {
                    cartItems.get(indexOfAlreadyPresentItem)
                            .setQuantity(cartItems.get(indexOfAlreadyPresentItem).getQuantity() + val);

                    Map<String, Object> cartItemsMap = new HashMap<>();
                    cartItemsMap.put("cartItems", cartItems);
                    ref.updateChildren(cartItemsMap);

                    Toast.makeText(getApplicationContext(), "Added To Cart", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Out Of Stock", Toast.LENGTH_SHORT).show();
                }

            } else {

                if (val <= stock) {
                    item.setQuantity(val);
                    cartItems.add(item);

                    Map<String, Object> cartItemsMap = new HashMap<>();
                    cartItemsMap.put("cartItems", cartItems);
                    ref.updateChildren(cartItemsMap);

                    Toast.makeText(getApplicationContext(), "Added To Cart", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Out Of Stock", Toast.LENGTH_SHORT).show();
                }
            }

        }else{
            Toast.makeText(getApplicationContext(),"Products to be of Same Shop",Toast.LENGTH_SHORT).show();
        }

    }

    public void increment(){
        if (val < 5){
            val++;
            quantity.setText(String.valueOf(val));
        } else {
            Toast.makeText(getApplicationContext(), "Limit of 5 products only", Toast.LENGTH_SHORT).show();
        }
    }

    public void decrement(){
        if (val > 1){
            val--;
            quantity.setText(String.valueOf(val));
        }
    }
}
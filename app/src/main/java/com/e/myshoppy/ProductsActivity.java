package com.e.myshoppy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;

public class ProductsActivity extends AppCompatActivity {

    ListView listView;
    ProductListAdapter proAdapter;
    ProgressBar progressBar;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    TextView empty;
    FloatingActionButton fab;
    private ArrayList<ShoppingItem> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        final String category = getIntent().getStringExtra("type");

        progressBar = findViewById(R.id.progress);
        progressBar.setVisibility(View.VISIBLE);
        listView = findViewById(R.id.listview);
        empty = findViewById(R.id.empty_view);
        listView.setEmptyView(empty);
        fab = findViewById(R.id.add);

        DatabaseReference ref = database.getReference("shopkeepers/" +
                FirebaseAuth.getInstance().getCurrentUser().getUid() + "/products/" + category +"/");

        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                listView.setVisibility(View.VISIBLE);

                products = setUpList(snapshot);
                proAdapter = new ProductListAdapter(getApplicationContext(),products,"seller");
                listView.setAdapter(proAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                });

                listView.setTextFilterEnabled(true);
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(ProductsActivity.this, AddProduct.class);
                intent.putExtra("type",category);
                startActivity(intent);
            }
        });
    }

    public static ArrayList<ShoppingItem> setUpList(DataSnapshot dataSnapshot) {

        ArrayList<ShoppingItem> items  = new ArrayList<ShoppingItem>();

        for (DataSnapshot snap : dataSnapshot.getChildren()){

            String itemPrice = snap.child("price").getValue().toString();
            int quantity = 0;

            quantity = Integer.valueOf(snap.child("quantity").getValue().toString());
            items.add(new ShoppingItem(
                    snap.child("productID").getValue().toString(),
                    snap.child("title").getValue().toString(),
                    snap.child("type").getValue().toString(),
                    snap.child("description").getValue().toString(),
                    "Rs. "+itemPrice,
                    quantity,
                    FirebaseAuth.getInstance().getCurrentUser().getUid()
            ));
        }

        return items;

    }
}
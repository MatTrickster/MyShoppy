package com.e.myshoppy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;

public class ProductsActivity extends AppCompatActivity {

    ListView listView;
    ArrayAdapter<String> proAdapter;
    ProgressBar progressBar;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    TextView empty;
    FloatingActionButton fab;
    private ArrayList<String> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        final String category = getIntent().getStringExtra("type");

        progressBar = findViewById(R.id.progress);
        progressBar.setVisibility(View.VISIBLE);
        listView = findViewById(R.id.listview);
        empty = findViewById(R.id.empty);
        listView.setEmptyView(empty);
        fab = findViewById(R.id.add);

        DatabaseReference ref = database.getReference("shopkeepers/" +
                FirebaseAuth.getInstance().getCurrentUser().getUid() + "/products/" + category +"/");

        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                listView.setVisibility(View.VISIBLE);

                products = setUpList(snapshot);
                proAdapter = new ArrayAdapter<String>(getApplication(),android.R.layout.simple_list_item_1,products);
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

    public static ArrayList<String> setUpList(DataSnapshot dataSnapshot) {

        ArrayList<String> items = new ArrayList<String>();

        for (DataSnapshot snap : dataSnapshot.getChildren()) {

            items.add(snap.getKey());
        }

        return items;

    }
}
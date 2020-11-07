package com.e.myshoppy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShopsActivity extends AppCompatActivity {

    EditText search;
    TextView empty;
    ListView listView;
    ProgressBar progressBar;
    String category;
    ArrayList<Shop> list;
    DatabaseReference ref;
    ShopsAdapter adapter;
    DataSnapshot data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shops);

        search = findViewById(R.id.searchBar);
        empty = findViewById(R.id.empty_view);
        listView = findViewById(R.id.list_view);
        progressBar = findViewById(R.id.progress);
        progressBar.setVisibility(View.VISIBLE);
        listView.setEmptyView(empty);
        category = getIntent().getStringExtra("category");
        list = new ArrayList<>();

        ref = FirebaseDatabase.getInstance().getReference().child("shopkeepers/");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                list = getItems(snapshot);
                data = snapshot;
                adapter = new ShopsAdapter(getApplicationContext(), list);
                listView.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(ShopsActivity.this,UserProductActivity.class);
                intent.putExtra("sId",list.get(i).getId());
                intent.putExtra("category",category);
                startActivity(intent);

            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                int textLength = charSequence.length();
                ArrayList<Shop> temp = new ArrayList<>();
                for (Shop x : list) {
                    if (textLength <= x.getName().length()) {
                        if (x.getName().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                            temp.add(x);
                        }
                    }
                }
                listView.setAdapter(new ShopsAdapter(getApplicationContext(), temp));

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logoutItem) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        } else if (id == R.id.cart) {
            startActivity(new Intent(getApplicationContext(), CartActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    public ArrayList<Shop> getItems(DataSnapshot snapshot) {

        ArrayList<Shop> items = new ArrayList<Shop>();

        for (DataSnapshot snapshot1 : snapshot.getChildren()) {

            for (DataSnapshot snapshot2 : snapshot1.child("products").getChildren()) {

                if (snapshot2.getKey().equals(category)) {

                    items.add(new Shop(snapshot1.child("shopDetails").child("sName").getValue().toString(),
                                        snapshot1.child("shopDetails").child("sAddress").getValue().toString(),
                                    null,
                                        snapshot1.getKey()));
                    break;
                }

            }

        }
        return items;
    }
}
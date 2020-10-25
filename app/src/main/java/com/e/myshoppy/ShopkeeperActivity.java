package com.e.myshoppy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class ShopkeeperActivity extends AppCompatActivity {

    ListView shoppingItemView;
    ArrayAdapter<String> catAdapter;
    ProgressBar progressBar;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    TextView empty;
    FloatingActionButton fab;
    private ArrayList<String> category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopkeeper);

        progressBar = findViewById(R.id.progress);
        progressBar.setVisibility(View.VISIBLE);
        shoppingItemView = findViewById(R.id.listview);
        empty = findViewById(R.id.empty);
        shoppingItemView.setEmptyView(empty);
        fab = findViewById(R.id.add);

        DatabaseReference ref = database.getReference("shopkeepers/" +
                FirebaseAuth.getInstance().getCurrentUser().getUid() + "/products/");

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ShopkeeperActivity.this);
                builder.setTitle("Add Category");
                final EditText input = new EditText(ShopkeeperActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        category.add(input.getText().toString());
                        ((BaseAdapter) shoppingItemView.getAdapter()).notifyDataSetChanged();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                shoppingItemView.setVisibility(View.VISIBLE);

                category = setUpList(snapshot);
                catAdapter = new ArrayAdapter<String>(getApplication(),android.R.layout.simple_list_item_1,category);
                shoppingItemView.setAdapter(catAdapter);

                shoppingItemView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        Intent intent  = new Intent(ShopkeeperActivity.this, ProductsActivity.class);
                        intent.putExtra("type",category.get(i));
                        startActivity(intent);
                    }
                });

                shoppingItemView.setTextFilterEnabled(true);
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logoutItem) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public static ArrayList<String> setUpList(DataSnapshot dataSnapshot) {

        ArrayList<String> items = new ArrayList<String>();

        for (DataSnapshot snap : dataSnapshot.getChildren()) {

            items.add(snap.getKey());
        }

        return items;

    }
}
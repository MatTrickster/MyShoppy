package com.e.myshoppy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShopkeeperCategories extends Fragment {

    Context context;
    ListView shoppingItemView;
    ArrayAdapter<String> catAdapter;
    ProgressBar progressBar;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    TextView empty;
    FloatingActionButton fab;
    private ArrayList<String> category;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v =  inflater.inflate(R.layout.fragment_shopkeeper_categories, container, false);

        progressBar = v.findViewById(R.id.progress);
        progressBar.setVisibility(View.VISIBLE);
        shoppingItemView = v.findViewById(R.id.listview);
        empty = v.findViewById(R.id.empty);
        shoppingItemView.setEmptyView(empty);
        fab = v.findViewById(R.id.add);

        DatabaseReference ref = database.getReference("shopkeepers/" +
                FirebaseAuth.getInstance().getCurrentUser().getUid() + "/products/");

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Add Category");
                final Spinner input = new Spinner(getContext());
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.categories, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                input.setAdapter(adapter);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

                input.setLayoutParams(lp);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if(category.contains(input.getSelectedItem())){
                            Toast.makeText(getContext(),"Category Already exist", Toast.LENGTH_SHORT).show();
                        }else{
                            category.add(input.getSelectedItem().toString());
                            ((BaseAdapter) shoppingItemView.getAdapter()).notifyDataSetChanged();
                        }


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
                catAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,category);
                shoppingItemView.setAdapter(catAdapter);

                shoppingItemView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        Intent intent  = new Intent(getContext(), ProductsActivity.class);
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

        return v;
    }

    public static ArrayList<String> setUpList(DataSnapshot dataSnapshot) {

        ArrayList<String> items = new ArrayList<String>();

        for (DataSnapshot snap : dataSnapshot.getChildren()) {

            items.add(snap.getKey());
        }

        return items;

    }
}
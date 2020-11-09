package com.e.myshoppy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    OrderParentAdapter parentOngoingOrderAdapter, parentPastOrderAdapter;
    TextView ongoing, past;
    RecyclerView ongoingList, pastList;
    ArrayList<DataSnapshot> snaps1 = new ArrayList<>(), snaps2 = new ArrayList<>();
    ArrayList<String> refList = new ArrayList<>();
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        ongoing = findViewById(R.id.ongoing);
        past = findViewById(R.id.past);
        ongoingList = findViewById(R.id.ongoing_list);
        pastList = findViewById(R.id.past_list);

        ref = FirebaseDatabase.getInstance().getReference("admin/");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot snaps : snapshot.child("referral codes").getChildren()) {
                    refList.add(snaps.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ongoing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ongoingList.getVisibility() == View.GONE)
                    ongoingList.setVisibility(View.VISIBLE);
                else
                    ongoingList.setVisibility(View.GONE);
            }
        });

        past.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pastList.getVisibility() == View.GONE)
                    pastList.setVisibility(View.VISIBLE);
                else
                    pastList.setVisibility(View.GONE);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        parentOngoingOrderAdapter = new OrderParentAdapter(ParentItemList("current"), AdminActivity.this, "", snaps1);
        ongoingList.setAdapter(parentOngoingOrderAdapter);
        ongoingList.setLayoutManager(layoutManager);


        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getApplicationContext());
        parentPastOrderAdapter = new OrderParentAdapter(ParentItemList("past"), AdminActivity.this, "", snaps2);
        pastList.setAdapter(parentPastOrderAdapter);
        pastList.setLayoutManager(layoutManager1);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.change_admin_code) {

            AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
            builder.setTitle("New Admin Code");
            final EditText text = new EditText(AdminActivity.this);
            text.setInputType(InputType.TYPE_CLASS_NUMBER);
            builder.setView(text);
            builder.setPositiveButton("OK", null);
            final AlertDialog dialog = builder.create();
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final ProgressDialog progressDialog = new ProgressDialog(AdminActivity.this);
                            progressDialog.show();
                            final String code = text.getText().toString();
                            if(!code.isEmpty()){
                                ref.child("admin code").setValue(code);
                                Toast.makeText(AdminActivity.this,"Admin Code Changed",Toast.LENGTH_SHORT).show();
                            }else{
                                text.setError("Field Empty");
                            }
                            progressDialog.dismiss();
                            dialog.dismiss();
                        }
                    });
                }
            });
            dialog.show();

        }else if(id == R.id.ref_code){

            final View view = LayoutInflater.from(AdminActivity.this).inflate(R.layout.referral_codes,null);
            final ListView listView = view.findViewById(R.id.ref_list);
            final ArrayAdapter adapter = new ArrayAdapter<>(AdminActivity.this,
                    android.R.layout.simple_list_item_1, refList);
            listView.setAdapter(adapter);

            AlertDialog dialog = new AlertDialog.Builder(AdminActivity.this)
                    .setTitle("Referral Codes")
                    .setView(view)
                    .setPositiveButton("OK",null)
                    .create();
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    TextView add = view.findViewById(R.id.add_ref_code);
                    final EditText editText = view.findViewById(R.id.new_ref_code);

                    add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            editText.setVisibility(View.VISIBLE);
                            String code = editText.getText().toString();

                            if(!code.isEmpty()){
                                ref.child("referral codes").push().setValue(code);
                                editText.setVisibility(View.GONE);
                                refList.add(code);
                                editText.setText("");
                                ((BaseAdapter) adapter).notifyDataSetChanged();
                            }
                        }
                    });
                }
            });
            dialog.show();


        }else if(id == R.id.about){
            startActivity(new Intent(getApplicationContext(),AboutActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    public List<OrderParentItem> ParentItemList(final String list) {

        final List<OrderParentItem> items = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("shopkeepers/");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                items.clear();
                String type;
                if (list.equals("current")) {
                    snaps1.clear();
                    type = "orders";
                }
                else {
                    snaps2.clear();
                    type = "past orders";
                }

                for (DataSnapshot snap1 : snapshot.getChildren()) {
                    for (DataSnapshot snap2 : snap1.child(type).getChildren()) {

                        List<ShoppingItem> item = ChildItemList(snap2);
                        items.add(new OrderParentItem("Order " + snap2.getKey(),
                                item, "Rs. " + snap2.child("amount").getValue().toString()));
                        if (list.equals("current"))
                            snaps1.add(snap2);
                        else
                            snaps2.add(snap2);

                    }
                }

                parentOngoingOrderAdapter.notifyDataSetChanged();

                if (list.equals("current"))
                    ongoing.setText("Ongoing Orders (" + items.size() + ")");
                else
                    past.setText("Past Orders (" + items.size() + ")");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return items;
    }

    public List<ShoppingItem> ChildItemList(DataSnapshot snap) {

        List<ShoppingItem> item = new ArrayList<>();
        for (int i = 0; i < snap.getChildrenCount()-5; i++) {

            item.add(new ShoppingItem(
                    "",
                    snap.child(String.valueOf(i)).child("title").getValue().toString(),
                    "",
                    snap.child(String.valueOf(i)).child("description").getValue().toString(),
                    snap.child(String.valueOf(i)).child("price").getValue().toString(),
                    Integer.valueOf(snap.child(String.valueOf(i)).child("quantity").getValue().toString()),
                    "",
                    snap.child(String.valueOf(i)).child("path").getValue().toString()
            ));

        }

        return item;
    }


}
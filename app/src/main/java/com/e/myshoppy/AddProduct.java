package com.e.myshoppy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
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

public class AddProduct extends AppCompatActivity {

    TextView productid, title, type, description, price, quantity;
    DatabaseReference myref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        String pType = getIntent().getStringExtra("type");
        Log.i("TSG",pType+"d");

        myref = FirebaseDatabase.getInstance().getReference("shopkeepers/" +
                FirebaseAuth.getInstance().getCurrentUser().getUid() + "/products/" + pType + "/");

        productid = findViewById(R.id.addProductId);
        title = findViewById(R.id.addProductTitle);
        type = findViewById(R.id.addProductType);
        description = findViewById(R.id.addProductDescription);
        price = findViewById(R.id.addProductPrice);
        quantity = findViewById(R.id.addProductQuantity);

        findViewById(R.id.addProductSubmit).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (productid.getText().toString().matches("") ||
                        title.getText().toString().matches("") ||
                        type.getText().toString().matches("") ||
                        description.getText().toString().matches("") ||
                        price.getText().toString().matches("") ||
                        quantity.getText().toString().matches("")) {

                    Toast.makeText(getApplicationContext(), "Fill everything", Toast.LENGTH_SHORT).show();

                } else {

                    myref.addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            ShoppingItem item = new ShoppingItem(
                                    productid.getText().toString(),
                                    title.getText().toString(),
                                    type.getText().toString(),
                                    description.getText().toString(),
                                    Integer.valueOf(price.getText().toString()),
                                    Integer.valueOf(quantity.getText().toString()));

                            myref.child(productid.getText().toString()).setValue(item);
                            finish();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w("", "Failed to read value.", databaseError.toException());
                        }
                    });

                }
            }
        });

    }
}
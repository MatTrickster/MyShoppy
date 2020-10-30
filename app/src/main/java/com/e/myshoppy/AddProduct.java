package com.e.myshoppy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddProduct extends AppCompatActivity {

    EditText productid, title, type, description, price, quantity;
    DatabaseReference myref;
    Button addImage;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 22;
    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            addImage.setText("Change");
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        final String pType = getIntent().getStringExtra("type");

        myref = FirebaseDatabase.getInstance().getReference("shopkeepers/" +
                FirebaseAuth.getInstance().getCurrentUser().getUid() + "/products/" + pType + "/");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        productid = findViewById(R.id.addProductId);
        title = findViewById(R.id.addProductTitle);
        type = findViewById(R.id.addProductType);
        description = findViewById(R.id.addProductDescription);
        price = findViewById(R.id.addProductPrice);
        quantity = findViewById(R.id.addProductQuantity);
        addImage = findViewById(R.id.add_image);
        type.setText(pType);

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image from here..."),
                        PICK_IMAGE_REQUEST);

            }
        });


        findViewById(R.id.addProductSubmit).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (productid.getText().toString().matches("") ||
                        title.getText().toString().matches("") ||
                        type.getText().toString().matches("") ||
                        description.getText().toString().matches("") ||
                        price.getText().toString().matches("") ||
                        quantity.getText().toString().matches("") ||
                        filePath == null) {

                    Toast.makeText(getApplicationContext(), "Data Incomplete", Toast.LENGTH_SHORT).show();

                }else{
                    Upload(pType);
                }
            }
        });
    }

    public void Upload(final String pType){

        final ProgressDialog progressDialog = new ProgressDialog(AddProduct.this);
        progressDialog.setTitle("Adding ...");
        progressDialog.show();

        final StorageReference ref = storageReference.child("Product Images")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(type.getText().toString()).child(productid.getText().toString());

        Bitmap bmp = null;
        try {
            bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
        byte[] data = baos.toByteArray();

        ref.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        ShoppingItem item = new ShoppingItem(
                                productid.getText().toString(),
                                title.getText().toString(),
                                pType,
                                description.getText().toString(),
                                price.getText().toString(),
                                Integer.valueOf(quantity.getText().toString()),
                                FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                uri.toString()
                        );

                        myref.child(productid.getText().toString()).setValue(item);
                        progressDialog.dismiss();
                        finish();

                    }
                });

                Toast.makeText(getApplicationContext(), "Item Added",
                        Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getApplicationContext(), "Failed " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                progressDialog.setMessage("Uploaded " + (int)progress + "%");
            }
        });

    }



}
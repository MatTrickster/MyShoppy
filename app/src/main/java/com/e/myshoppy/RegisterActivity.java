package com.e.myshoppy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    ProgressBar progressBar;
    private FirebaseAuth auth;
    private Button register;
    private String email, pass,name,sName,sRegNumber,sCity,code,mobile;
    private EditText email_edit, pass_edit, name_edit, sName_edit, sRegNumber_edit, sCity_edit, code_edit, no_edit;
    private Spinner spinner;
    int pos = 0;
    DatabaseReference ref;
    DataSnapshot snap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        name_edit = findViewById(R.id.name);
        email_edit = findViewById(R.id.email);
        no_edit = findViewById(R.id.mobile);
        pass_edit = findViewById(R.id.password);
        sName_edit = findViewById(R.id.s_name);
        sRegNumber_edit = findViewById(R.id.s_reg_number);
        sCity_edit = findViewById(R.id.s_city);
        code_edit = findViewById(R.id.code);
        register = findViewById(R.id.register);
        spinner = findViewById(R.id.spin_register);
        progressBar = findViewById(R.id.registrationPageProgressBar);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if(spinner.getSelectedItemPosition() == 0){
                    sName_edit.setVisibility(View.GONE);
                    sRegNumber_edit.setVisibility(View.GONE);
                    sCity_edit.setVisibility(View.GONE);
                    code_edit.setVisibility(View.GONE);
                }else{
                    sName_edit.setVisibility(View.VISIBLE);
                    sRegNumber_edit.setVisibility(View.VISIBLE);
                    sCity_edit.setVisibility(View.VISIBLE);
                    code_edit.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                name = name_edit.getText().toString();
                email = email_edit.getText().toString();
                mobile = no_edit.getText().toString();
                pass = pass_edit.getText().toString();

                pos = spinner.getSelectedItemPosition();

                if(pos == 1){

                    sName = sName_edit.getText().toString();
                    sRegNumber = sRegNumber_edit.getText().toString();
                    sCity = sCity_edit.getText().toString();
                    code = code_edit.getText().toString();

                }

                if(name.isEmpty()){
                    name_edit.setError("Field is Empty");
                }else if(email.isEmpty()){
                    email_edit.setError("Field is Empty");
                }else if(mobile.isEmpty()) {
                    no_edit.setError("Field is Empty");
                }else if(mobile.length() != 10){
                    no_edit.setError("Invalid Number");
                }else if(pass.isEmpty()){
                    pass_edit.setError("Field is Empty");
                }else if(pos == 1 && sName.isEmpty()){
                    sName_edit.setError("Field is Empty");
                }else if(pos== 1 && sRegNumber.isEmpty()){
                    sRegNumber_edit.setError("Field is Empty");
                }else if(pos == 1 && sCity.isEmpty()){
                    sCity_edit.setError("Field is Empty");
                }else if(pos == 1 && code.isEmpty()){
                    code_edit.setError("Field is Empty");
                }else{
                    verify(mobile);
                }
            }
        });

        ref = FirebaseDatabase.getInstance().getReference("admin/referral codes/");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snap = snapshot;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void verify(String no){

        Intent intent = new Intent(RegisterActivity.this,VerifyActivity.class);
        intent.putExtra("no",no);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == 1 && resultCode == 11){
            createAccount();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void createAccount() {
        progressBar.setVisibility(View.VISIBLE);

        if(pos == 1){
            boolean create = false;
            for(DataSnapshot x: snap.getChildren()){
                String code = x.getValue().toString();

                if(code.equals(code_edit.getText().toString())){
                    create = true;
                    break;
                }
            }

            if(!create){
                Toast.makeText(RegisterActivity.this,"Code Invalid",Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                return;
            }
        }

        auth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }else{

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            if (user != null) {

                                Log.d("TAG", "onAuthStateChanged:signed_in:" + user.getUid());
                                String name = name_edit.getText().toString();
                                UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().
                                        setDisplayName(name).build();
                                user.updateProfile(profileChangeRequest);

                                DatabaseReference myRef;

                                if (pos == 0){
                                    myRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
                                    myRef.child(user.getUid()).push();

                                    ArrayList<ShoppingItem> cart = new ArrayList<>();
                                    cart.add(new ShoppingItem("", "", "", "",
                                            "", -1,"",null));
                                    Map<String, Object> cartItems = new HashMap<>();
                                    cartItems.put("cartItems", cart);

                                    Map<String, Object> cartState = new HashMap<>();
                                    cartState.put("isCartEmpty", Boolean.TRUE);

                                    Map<String, Object> shopId = new HashMap<>();
                                    shopId.put("shopId","null");

                                    Map<String,Object> info = new HashMap<>();
                                    info.put("name",name_edit.getText().toString());
                                    info.put("email",email_edit.getText().toString());
                                    info.put("contact",no_edit.getText().toString());
                                    Map<String,Object> infod = new HashMap<>();
                                    infod.put("info",info);

                                    myRef.updateChildren(infod);
                                    myRef.updateChildren(shopId);
                                    myRef.updateChildren(cartItems);
                                    myRef.updateChildren(cartState);
                                } else {
                                    myRef = FirebaseDatabase.getInstance().getReference("shopkeepers").child(user.getUid());
                                    myRef.child(user.getUid()).push();

                                    ShopDetails details = new ShopDetails(sName,name,sRegNumber,sCity,
                                            no_edit.getText().toString(),email_edit.getText().toString(),
                                            "0");
                                    myRef.child("shopDetails").setValue(details);
                                }


                            } else {

                                Log.d("TAG", "onAuthStateChanged:signed_out");
                            }

                            Toast.makeText(getApplicationContext(), "Registration Successful", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                    }
                });
    }


}
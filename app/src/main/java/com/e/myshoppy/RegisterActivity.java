package com.e.myshoppy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Button register;
    private String email, pass,name,sName,sRegNumber,sAddress,code;
    private EditText email_edit, pass_edit, name_edit, sName_edit, sRegNumber_edit, sAddress_edit, code_edit;
    private Spinner spinner;
    private boolean isRegistrationClicked = false;
    int pos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name_edit = findViewById(R.id.name);
        email_edit = findViewById(R.id.email);
        pass_edit = findViewById(R.id.password);
        sName_edit = findViewById(R.id.s_name);
        sRegNumber_edit = findViewById(R.id.s_reg_number);
        sAddress_edit = findViewById(R.id.s_address);
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
                    sAddress_edit.setVisibility(View.GONE);
                    code_edit.setVisibility(View.GONE);
                }else{
                    sName_edit.setVisibility(View.VISIBLE);
                    sRegNumber_edit.setVisibility(View.VISIBLE);
                    sAddress_edit.setVisibility(View.VISIBLE);
                    code_edit.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        auth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

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
                        cart.add(new ShoppingItem("", "", "", "", -1, -1));
                        Map<String, Object> cartItems = new HashMap<>();
                        cartItems.put("cartItems", cart);

                        Map<String, Object> cartState = new HashMap<>();
                        cartState.put("isCartEmpty", Boolean.TRUE);

                        myRef.updateChildren(cartItems);
                        myRef.updateChildren(cartState);
                    } else {
                        myRef = FirebaseDatabase.getInstance().getReference("shopkeepers").child(user.getUid());
                        myRef.child(user.getUid()).push();

                        ArrayList<ShoppingItem> prods = new ArrayList<>();
                        prods.add(new ShoppingItem("", "", "", "", -1, -1));
                        Map<String, Object> prodslist = new HashMap<>();
                        prodslist.put("products", prods);

                        Map<String, Object> state = new HashMap<>();
                        state.put("isEmpty", Boolean.TRUE);

                        ShopDetails details = new ShopDetails(sName,name,sRegNumber,sAddress);

                        myRef.child("shopDetails").setValue(details);
                        myRef.updateChildren(prodslist);
                        myRef.updateChildren(state);
                    }


                } else {

                    Log.d("TAG", "onAuthStateChanged:signed_out");
                }
            }
        };

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                name = name_edit.getText().toString();
                email = email_edit.getText().toString();
                pass = pass_edit.getText().toString();

                pos = spinner.getSelectedItemPosition();

                if(pos == 1){

                    sName = sName_edit.getText().toString();
                    sRegNumber = sRegNumber_edit.getText().toString();
                    sAddress = sAddress_edit.getText().toString();
                    code = code_edit.getText().toString();

                }

                if(name.isEmpty()){
                    name_edit.setError("Field is Empty");
                }else if(email.isEmpty()){
                    email_edit.setError("Field is Empty");
                }else if(pass.isEmpty()){
                    pass_edit.setError("Field is Empty");
                }else if(pos == 1 && sName.isEmpty()){
                    sName_edit.setError("Field is Empty");
                }else if(pos== 1 && sRegNumber.isEmpty()){
                    sRegNumber_edit.setError("Field is Empty");
                }else if(pos == 1 && sAddress.isEmpty()){
                    sAddress_edit.setError("Field is Empty");
                }else if(pos == 1 && code.isEmpty()){
                    code_edit.setError("Field is Empty");
                }else{
                    createAccount();
                }
            }
        });
    }

    private void createAccount() {
        progressBar.setVisibility(View.VISIBLE);

        auth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("TAG", "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }else{
                            Toast.makeText(getApplicationContext(), "Registration Successful", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            auth.removeAuthStateListener(mAuthListener);
        }
    }


}
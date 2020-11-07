package com.e.myshoppy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.NetworkInterface;

public class LoginActivity extends AppCompatActivity {
    ImageView logo;
    TextView register,admin;
    ProgressBar progressBar;
    Spinner spinner;
    private Button login;
    private EditText email_edit, pass_edit;
    private String email, pass;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    int pos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setMessage("Loading ...");
        final AlertDialog dialog = builder.create();
        dialog.show();
        logo = findViewById(R.id.welcome);
        logo.setImageResource(R.drawable.logo_transparent);
        register = findViewById(R.id.register);
        login = findViewById(R.id.login);
        email_edit = findViewById(R.id.usernameLogin);
        pass_edit = findViewById(R.id.passwordLogin);
        spinner = findViewById(R.id.spinner);
        progressBar = findViewById(R.id.loginPageProgressBar);
        auth = FirebaseAuth.getInstance();
        admin = findViewById(R.id.admin);

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    String tag = "";

                    if(pos == 0)
                        tag = "users/";
                    else if(pos == 1)
                        tag = "shopkeepers/";

                    FirebaseDatabase.getInstance().getReference(tag + user.getUid())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    dialog.dismiss();
                                    dialog.setMessage("Signing In ...");
                                    if(dataSnapshot.exists()) {
                                        if (pos == 0) {
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(intent);
                                            Toast.makeText(getApplicationContext(), "Sign in Successful!", Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                            finish();
                                        } else if (pos == 1) {
                                            Intent intent = new Intent(getApplicationContext(), ShopkeeperActivity.class);
                                            startActivity(intent);
                                            Toast.makeText(getApplicationContext(), "Sign in Successful!", Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                            finish();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                    Log.w("TAG", "Failed to read value.", databaseError.toException());
                                }
                            });
                } else {
                    dialog.dismiss();
                    Log.d("TAG", "onAuthStateChanged:signed_out");
                }

            }
        };

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressBar.setVisibility(View.VISIBLE);
                email = email_edit.getText().toString();
                pass = pass_edit.getText().toString();

                if(email.isEmpty()){
                    email_edit.setError("Field is Empty");
                }else if(pass.isEmpty()){
                    pass_edit.setError("Field is Empty");
                }else{

                    pos = spinner.getSelectedItemPosition();
                    loginUser(email,pass);

                }

            }
        });

        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("Code");
                final EditText text = new EditText(LoginActivity.this);
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
                                final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                                progressDialog.show();
                                final String code = text.getText().toString();
                                if(!code.isEmpty()){

                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("admin/admin code/");
                                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            String pass = snapshot.getValue().toString();

                                            if(code.equals(pass)){
                                                dialog.dismiss();
                                                Toast.makeText(getApplicationContext(), "Admin Logged In", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                                progressDialog.dismiss();
                                                startActivity(new Intent(LoginActivity.this,AdminActivity.class));
                                                finish();
                                            }else{
                                                Toast.makeText(getApplicationContext(), "Incorrect Code", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                }else{
                                    text.setError("Field Empty");
                                    progressDialog.dismiss();
                                }
                            }
                        });
                    }
                });
                dialog.show();

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    public void loginUser(String email, String password) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), R.string.auth_failed, Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }
}
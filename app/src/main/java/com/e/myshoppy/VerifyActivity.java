package com.e.myshoppy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.text.UnicodeSetSpanner;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;

import java.util.concurrent.TimeUnit;

public class VerifyActivity extends AppCompatActivity {

    ProgressBar progressBar;
    TextView no,timer;
    Button verify, resend;
    EditText code;
    String mVerificationId,sentCode;
    PhoneAuthProvider.ForceResendingToken mResendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        final String number = getIntent().getStringExtra("no");
        progressBar = new ProgressBar(this);

        final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                sentCode = credential.getSmsCode();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(VerifyActivity.this,"Invalid Number",Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(VerifyActivity.this,"Too Many Request",Toast.LENGTH_SHORT).show();
                }
                Log.i("TAG","nai"+e.getMessage());
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {

                Toast.makeText(VerifyActivity.this,"Code Sent",Toast.LENGTH_SHORT).show();
                Timer(60);
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                        .setPhoneNumber("+91"+number)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

        code = findViewById(R.id.code);
        no = findViewById(R.id.number);
        no.setText("Mobile Number : "+number);
        timer = findViewById(R.id.timer);
        verify = findViewById(R.id.verify);
        resend = findViewById(R.id.resend);

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String temp = code.getText().toString();

                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,temp);
                FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            Toast.makeText(VerifyActivity.this,"Verification Successful",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            setResult(11,intent);
                            progressBar.setVisibility(View.INVISIBLE);
                            finish();

                        } else {

                            Toast.makeText(VerifyActivity.this,"Invalid Code",Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber("+91"+number,
                        60L,
                        TimeUnit.SECONDS,
                        VerifyActivity.this,
                        mCallbacks,
                        mResendToken
                );
            }
        });
    }


    public void Timer(int seconds) {
        resend.setEnabled(false);

        new CountDownTimer(seconds*1000, 1000) {

            public void onTick(long millisUntilFinished) {
                String secondsString = Long.toString(millisUntilFinished/1000);
                if (millisUntilFinished<10000) {
                    secondsString = "0"+secondsString;
                }
                timer.setText(" (0:"+ secondsString+")");
            }

            public void onFinish() {
                resend.setEnabled(true);
            }
        }.start();
    }
}
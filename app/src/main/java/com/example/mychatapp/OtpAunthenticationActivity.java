package com.example.mychatapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class OtpAunthenticationActivity extends AppCompatActivity {

    TextView mchangenumber;
    EditText mgetotp;
    Button mverifyotp;
    String enteredotp;

    FirebaseAuth firebaseAuth;
    ProgressBar mprogressbarfototpauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_aunthentication);

        mchangenumber=findViewById(R.id.changenumber);
        mverifyotp=findViewById(R.id.verifyotp);
        mgetotp=findViewById(R.id.getotp);
        mprogressbarfototpauth=findViewById(R.id.progerssbarbuttonofotpauthencation);

        firebaseAuth=FirebaseAuth.getInstance();

        mchangenumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent=new Intent(OtpAunthenticationActivity.this,ChatActivity.class);
//                startActivity(intent);

                new CommonMethod(OtpAunthenticationActivity.this,MainActivity.class);
            }
        });

        mverifyotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enteredotp=mgetotp.getText().toString();
                if(enteredotp.isEmpty())
                {
                    new CommonMethod(OtpAunthenticationActivity.this,"Enter Your Otp First");
                }
                else
                {
                    mprogressbarfototpauth.setVisibility(View.VISIBLE);
                    String codereceive=getIntent().getStringExtra("otp");

                    PhoneAuthCredential credential= PhoneAuthProvider.getCredential(codereceive,enteredotp);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });

    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential)
    {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    mprogressbarfototpauth.setVisibility(View.INVISIBLE);
                    new CommonMethod(OtpAunthenticationActivity.this,"Login Sucessfully");
                    new CommonMethod(OtpAunthenticationActivity.this,SetProfileActivity.class);
                    finish();
                }
                else
                {
                    if(task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                    {
                        mprogressbarfototpauth.setVisibility(View.INVISIBLE);
                        new CommonMethod(OtpAunthenticationActivity.this,"Login Failed");
                    }
                }
            }
        });
    }
}
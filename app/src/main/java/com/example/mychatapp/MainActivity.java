package com.example.mychatapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    EditText mgetphonenumber;
    Button msendotp;
    CountryCodePicker mcountrycodepicker;
    String countrycode;
    String phonenumber;

    FirebaseAuth firebaseAuth;
    ProgressBar mprogressbarofmain;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mcallbacks;
    String codesent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mcountrycodepicker = findViewById(R.id.countrycodepicker);
        msendotp = findViewById(R.id.sendotpbutton);
        mgetphonenumber = findViewById(R.id.getphonenumber);
        mprogressbarofmain = findViewById(R.id.progerssbarbuttonofmain);

        firebaseAuth = FirebaseAuth.getInstance();

        countrycode = mcountrycodepicker.getSelectedCountryCodeWithPlus();

        mcountrycodepicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                countrycode = mcountrycodepicker.getSelectedCountryCodeWithPlus();
            }
        });

        msendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number;

                number = mgetphonenumber.getText().toString();

                if (number.isEmpty()) {
                    new CommonMethod(MainActivity.this, "Please Enter your number");
                } else if (number.length() < 10) {
                    new CommonMethod(MainActivity.this, "Please Enter a Correct Phone Number");
                } else {
                    mprogressbarofmain.setVisibility(view.VISIBLE);
                    phonenumber = countrycode + number;

                    PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                            .setPhoneNumber(phonenumber)
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(MainActivity.this)
                            .setCallbacks(mcallbacks)
                            .build();

                    PhoneAuthProvider.verifyPhoneNumber(options);
                }
            }
        });


        mcallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                //how to automatically fetch the otp.

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                new CommonMethod(MainActivity.this, "OTP Sent");

                mprogressbarofmain.setVisibility(View.VISIBLE);
                codesent = s;

                Intent intent = new Intent(MainActivity.this, OtpAunthenticationActivity.class);
                intent.putExtra("otp", codesent);
                startActivity(intent);
            }
        };

    }


    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {
            Intent intent=new Intent(MainActivity.this,ChatActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
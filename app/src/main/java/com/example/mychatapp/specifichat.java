package com.example.mychatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class specifichat extends AppCompatActivity {

    EditText mgetmessage;
    ImageButton msendmessagebutton;

    CardView msendmessageforcardview;
    androidx.appcompat.widget.Toolbar mtoolbarofspecifichat;
    ImageView mimageviewofspecichat;
    TextView mnameofspecifichat;

    private String enteredmessage;
    Intent intent;
    String mreceivername,msendername,mreceiveruid,msenderuid;
    private FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    String senderroom,receiverroom;

    ImageButton mbackbuuttonofspecifichat;

    RecyclerView mmessageofrecyclerview;

    String currenttime;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;

    MessageAdapter messageAdapter;
    ArrayList<Messages> messagesArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specifichat);

        mgetmessage=findViewById(R.id.getmessage);
        msendmessageforcardview=findViewById(R.id.cardviewpofsendmessage);
        msendmessagebutton=findViewById(R.id.imageviewofsendmessage);
        mtoolbarofspecifichat=findViewById(R.id.toolbarofspecifichat);
        mnameofspecifichat=findViewById(R.id.nameofspecifiuser);
        mimageviewofspecichat=findViewById(R.id.specificuserimageinimageview);
        mbackbuuttonofspecifichat=findViewById(R.id.backbuttonofspecifichat);

        messagesArrayList=new ArrayList<>();
        mmessageofrecyclerview=findViewById(R.id.recyclerviewofspecifichat);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mmessageofrecyclerview.setLayoutManager(linearLayoutManager);
        messageAdapter=new MessageAdapter(specifichat.this,messagesArrayList);
        mmessageofrecyclerview.setAdapter(messageAdapter);



        intent=getIntent();

        setSupportActionBar(mtoolbarofspecifichat);

        mtoolbarofspecifichat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CommonMethod(specifichat.this,"Toolbar is select");
                //if you update when i click the user name toolbar give me a details about his/her user.
            }
        });

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        calendar=Calendar.getInstance();
        simpleDateFormat=new SimpleDateFormat("hh-mm a");

        msenderuid=firebaseAuth.getUid();
        mreceiveruid=getIntent().getStringExtra("receiveruid");
        mreceivername=getIntent().getStringExtra("name");

        senderroom=msenderuid+mreceiveruid;
        receiverroom=mreceiveruid+msenderuid;

        DatabaseReference databaseReference=firebaseDatabase.getReference().child("chats").child(senderroom).child("messages");
        messageAdapter=new MessageAdapter(specifichat.this,messagesArrayList);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                 messagesArrayList.clear();
                 for(DataSnapshot snapshot1:snapshot.getChildren())
                 {
                     Messages messages=snapshot1.getValue(Messages.class);
                     messagesArrayList.add(messages);
                 }
                 messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        mbackbuuttonofspecifichat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mnameofspecifichat.setText(mreceivername);
        String Uri=intent.getStringExtra("imageuri");

        if(Uri.isEmpty())
        {
            new CommonMethod(specifichat.this,"Null String returned");
        }
        else
        {
            Picasso.get().load(Uri).into(mimageviewofspecichat);
        }

        msendmessagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                enteredmessage=mgetmessage.getText().toString();

                if(enteredmessage.isEmpty())
                {
                    new CommonMethod(specifichat.this,"Enter Message First");
                }
                else {
                    Date date=new Date();
                    currenttime=simpleDateFormat.format(calendar.getTime());
                    Messages messages=new Messages(enteredmessage,firebaseAuth.getUid(),date.getTime(),currenttime);

                    firebaseDatabase=FirebaseDatabase.getInstance();
                    firebaseDatabase.getReference().child("chats").child(senderroom)
                            .child("messages").
                            push().setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    firebaseDatabase.getReference()
                                            .child("chats")
                                            .child(receiverroom)
                                            .child("messages")
                                            .push()
                                            .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                }
                                            });
                                }
                            });
                    mgetmessage.setText(null);

                }


            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        messageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(messageAdapter!=null)
        {
            messageAdapter.notifyDataSetChanged();
        }
    }
}
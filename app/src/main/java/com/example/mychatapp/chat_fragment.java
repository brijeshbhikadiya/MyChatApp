package com.example.mychatapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

public class chat_fragment extends Fragment {

    private FirebaseFirestore firebaseFirestore;
    LinearLayoutManager linearLayoutManager;
    private FirebaseAuth firebaseAuth;

    ImageView mimageviewofuser;

    RecyclerView mrecyclerview;

    FirestoreRecyclerAdapter<firebasemodel,NoteViewHolder> chatAapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.chat_fragment,container,false);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();

        mrecyclerview=v.findViewById(R.id.recyclerview);

        //Query query=firebaseFirestore.collection("USers"); -> this used only single users.
        Query query=firebaseFirestore.collection("USers").whereNotEqualTo("uid",firebaseAuth.getUid());
        FirestoreRecyclerOptions<firebasemodel> allusername= new FirestoreRecyclerOptions.Builder<firebasemodel>().setQuery(query,firebasemodel.class).build();

        chatAapter=new FirestoreRecyclerAdapter<firebasemodel, NoteViewHolder>(allusername) {
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder holder, int position, @NonNull firebasemodel model) {

                holder.perticularusername.setText(model.getName());
                String uri=model.getImage();

                Picasso.get().load(uri).into(mimageviewofuser);

                if(model.getStatus().equals("Online"))
                {
                    holder.statusofuser.setText(model.getStatus());
                    holder.statusofuser.setTextColor(Color.GREEN);
                }
                else
                {
                    holder.statusofuser.setText(model.getStatus());
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       // Toast.makeText(getActivity(),"Item Is Click",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(getActivity(),specifichat.class);
                        intent.putExtra("name",model.getName());
                        intent.putExtra("receiveruid",model.getUid());
                        intent.putExtra("imageuri",model.getImage());
                        startActivity(intent);
                    }
                });


            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.chatviewlayout,parent,false);
                return new NoteViewHolder(view);
            }
        };

        mrecyclerview.setHasFixedSize(true);
        linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mrecyclerview.setLayoutManager(linearLayoutManager);
        mrecyclerview.setAdapter(chatAapter);

        return v;
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder
    {
        private TextView perticularusername;
        private TextView statusofuser;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            perticularusername=itemView.findViewById(R.id.nameforuser);
            statusofuser=itemView.findViewById(R.id.statusforuser);
            mimageviewofuser=itemView.findViewById(R.id.imageviewofuser);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        chatAapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(chatAapter!=null)
        {
            chatAapter.stopListening();
        }
    }
}

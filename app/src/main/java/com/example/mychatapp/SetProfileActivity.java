package com.example.mychatapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class SetProfileActivity extends AppCompatActivity {

    private CardView mgetuserimage;
    private ImageView mgetuserimageinimageview;
    private static int PICK_IMAGE=123;
    private Uri imagepath;

    private EditText mgetusername;

    private Button msaveprofile;

    private FirebaseAuth firebaseAuth;
    private String name;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private String ImageUriAccessToken;

    private FirebaseFirestore firebaseFirestore;

    ProgressBar mprogressbarofsetprofile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();
        firebaseFirestore=FirebaseFirestore.getInstance();

        mgetuserimage=findViewById(R.id.getuserimage);
        mgetuserimageinimageview=findViewById(R.id.getuserimageinimageview);
        mgetusername=findViewById(R.id.getusername);
        msaveprofile=findViewById(R.id.saveprofile);
        mprogressbarofsetprofile=findViewById(R.id.progerssbarbuttonforsaveprofilr);

        mgetuserimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent,PICK_IMAGE);
            }
        });

            msaveprofile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    name=mgetusername.getText().toString();
                    if(name.isEmpty())
                    {
                        new CommonMethod(SetProfileActivity.this,"Name is Empty");
                    }
                    else if(imagepath==null)
                    {
                        new CommonMethod(SetProfileActivity.this,"Image is Empty");
                    }
                    else
                    {
                        mprogressbarofsetprofile.setVisibility(View.VISIBLE);
                        sendDataNewUser();
                        mprogressbarofsetprofile.setVisibility(View.INVISIBLE);

                        new CommonMethod(SetProfileActivity.this,ChatActivity.class);
                        finish();
                    }
                }
            });

    }



    private void sendDataNewUser()
    {
        sendDataToRealTimeDatabase();

    }

    private void sendDataToRealTimeDatabase()
    {
        name=mgetusername.getText().toString().trim();
        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference=firebaseDatabase.getReference(firebaseAuth.getUid());

        UserProfile muserprofile=new UserProfile(name,firebaseAuth.getUid());
        databaseReference.setValue(muserprofile);
        new CommonMethod(SetProfileActivity.this,"User Profile added Sucessfully");
        sendImageToStroage();
    }

    private void sendImageToStroage()
    {
        StorageReference imageref=storageReference.child("Images").child(firebaseAuth.getUid()).child("Profile Pic");

        //Image Compression(optinal)

        Bitmap bitmap=null;
        try{
            bitmap=MediaStore.Images.Media.getBitmap(getContentResolver(),imagepath);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,25,byteArrayOutputStream);
        byte[] data=byteArrayOutputStream.toByteArray();

        //putting image into stroage

        UploadTask uploadTask=imageref.putBytes(data);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imageref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        ImageUriAccessToken=uri.toString();
                        new CommonMethod(SetProfileActivity.this,"URI GET SUCESS");
                        sendDataToCloudFireStore();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        new CommonMethod(SetProfileActivity.this,"URI GET FAILD");
                    }
                });

                new CommonMethod(SetProfileActivity.this,"Image is Uploaded");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                new CommonMethod(SetProfileActivity.this,"Image Not Uploaded");
            }
        });


    }

    private void sendDataToCloudFireStore() {
        DocumentReference documentReference=firebaseFirestore.collection("USers").document(firebaseAuth.getUid());
        Map<String,Object> userdata=new HashMap<>();
        userdata.put("name",name);
        userdata.put("image",ImageUriAccessToken);
        userdata.put("uid",firebaseAuth.getUid());
        userdata.put("status","Online");

        documentReference.set(userdata).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                new CommonMethod(SetProfileActivity.this,"Data On Cloud FireStroage send Sucess");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode==PICK_IMAGE && resultCode==RESULT_OK)
        {
            imagepath=data.getData();
            mgetuserimageinimageview.setImageURI(imagepath);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
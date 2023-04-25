package com.example.mychatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class UpdateProfile extends AppCompatActivity {


    //declare all variable
   private EditText mnewusername;
    private FirebaseAuth firebaseAuth;
   private FirebaseDatabase firebaseDatabase;

   private FirebaseFirestore firebaseFirestore;

    private ImageView mgetnewusernewimageinimageview;

  private   StorageReference storageReference;

    private  String ImageURIAccessToken;

   private androidx.appcompat.widget.Toolbar mtoolbarofupdateprofile;
    private ImageButton mbackbuttonofupdateprofile;

    private   FirebaseStorage firebaseStorage;
    ProgressBar mprogressbarofupdateprofile;

    private Uri imagepath;

    Intent intent;

    Button updateprofilebutton;

    private static int PICK_IMAGE=123;

    String newname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

            //id intialization
        mtoolbarofupdateprofile=findViewById(R.id.toolbarofupdateprofile);
        mbackbuttonofupdateprofile=findViewById(R.id.backbuttonofupdateprofile);
        mgetnewusernewimageinimageview=findViewById(R.id.getnewimageinimageview);
        mprogressbarofupdateprofile=findViewById(R.id.progerssbarbuttonofupdateprofile);
        mnewusername=findViewById(R.id.getnewusername);
        updateprofilebutton=findViewById(R.id.updateprofilepbutton);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();

        intent=getIntent();

        setSupportActionBar(mtoolbarofupdateprofile);

        mbackbuttonofupdateprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mnewusername.setText(intent.getStringExtra("nameofuser"));

        DatabaseReference databaseReference=firebaseDatabase.getReference(firebaseAuth.getUid());

        updateprofilebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newname=mnewusername.getText().toString();
                if(newname.isEmpty())
                {
                    new CommonMethod(UpdateProfile.this,"Name is empty");
                } else if (imagepath!=null) {

                    mprogressbarofupdateprofile.setVisibility(View.VISIBLE);
                    UserProfile muserprofile=new UserProfile(newname,firebaseAuth.getUid());
                    databaseReference.setValue(muserprofile);

                    updateimagetostorage();

                    new CommonMethod(UpdateProfile.this,"Updated");
                    mprogressbarofupdateprofile.setVisibility(View.INVISIBLE);
                    new CommonMethod(UpdateProfile.this,ChatActivity.class);
                    finish();

                }
                else {
                    mprogressbarofupdateprofile.setVisibility(View.VISIBLE);
                    UserProfile muserprofile=new UserProfile(newname,firebaseAuth.getUid());
                    databaseReference.setValue(muserprofile);

                    updatenameofcloudfirestore();

                    new CommonMethod(UpdateProfile.this,"Updated");
                    mprogressbarofupdateprofile.setVisibility(View.INVISIBLE);
                    new CommonMethod(UpdateProfile.this,ChatActivity.class);
                    finish();

                }
            }
        });

        mgetnewusernewimageinimageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent,PICK_IMAGE);
            }
        });

        storageReference=firebaseStorage.getReference();
        storageReference.child("Images").child(firebaseAuth.getUid()).child("Profile Pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                ImageURIAccessToken=uri.toString();
                Picasso.get().load(uri).into(mgetnewusernewimageinimageview);
            }
        });
    }

    private void updatenameofcloudfirestore() {
        DocumentReference documentReference=firebaseFirestore.collection("USers").document(firebaseAuth.getUid());
        Map<String,Object> userdata=new HashMap<>();
        userdata.put("name",newname);
        userdata.put("image",ImageURIAccessToken);
        userdata.put("uid",firebaseAuth.getUid());
        userdata.put("status","Online");

        documentReference.set(userdata).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                new CommonMethod(UpdateProfile.this,"Profile Updated Sucessfully");
            }
        });
    }

    private void updateimagetostorage() {

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
                        ImageURIAccessToken=uri.toString();
                        new CommonMethod(UpdateProfile.this,"URI GET SUCESS");
                        updatenameofcloudfirestore();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        new CommonMethod(UpdateProfile.this,"URI GET FAILD");
                    }
                });

                new CommonMethod(UpdateProfile.this,"Image is Updated");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                new CommonMethod(UpdateProfile.this,"Image Not Updated");
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode==PICK_IMAGE && resultCode==RESULT_OK)
        {
            imagepath=data.getData();
            mgetnewusernewimageinimageview.setImageURI(imagepath);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStop() {
        super.onStop();
        DocumentReference documentReference=firebaseFirestore.collection("USers").document(firebaseAuth.getUid());
        documentReference.update("status","Offline").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                new CommonMethod(UpdateProfile.this,"Now User Is Offilne");
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        DocumentReference documentReference=firebaseFirestore.collection("USers").document(firebaseAuth.getUid());
        documentReference.update("status","Online").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                new CommonMethod(UpdateProfile.this,"Now User Is Online");
            }
        });
    }
}
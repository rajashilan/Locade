package com.example.locade;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    DatabaseReference database;
    FirebaseStorage mStorage;

    Uri imageUri;

    private ImageButton chatButton, locationsButton;

    private ImageView mImageViewUser;
    private TextView mTextViewNickName, mTextViewNumber;
    private Button mButtonUpdatePicture, mButtonLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mImageViewUser = findViewById(R.id.image_view_user_profile);
        mTextViewNickName = findViewById(R.id.text_view_user_nickname);
        mTextViewNumber = findViewById(R.id.text_view_user_number);
        mButtonUpdatePicture = findViewById(R.id.button_edit_profile_picture);
        mButtonLogout = findViewById(R.id.button_logout);

        mStorage = FirebaseStorage.getInstance();

        chatButton = findViewById(R.id.chats_button);
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToChats = new Intent(ProfileActivity.this, ChatsActivity.class);
                startActivity(goToChats);
                finish();
            }
        });

        locationsButton = findViewById(R.id.locations_button);
        locationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToLocation = new Intent(ProfileActivity.this, LocationsActivity.class);
                startActivity(goToLocation);
                finish();
            }
        });

        mButtonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent goToLogin = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(goToLogin);
                finish();
            }
        });

        mAuth = FirebaseAuth.getInstance();

        String userID = mAuth.getCurrentUser().getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://locade-10249-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference()
                .child("Users").child(userID);

        if(!userID.equals("")){

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if(dataSnapshot.getKey().equals("profileImgUrl"))
                            if(String.valueOf(dataSnapshot.getValue()).equals("default"))
                                        Picasso.with(ProfileActivity.this)
                                                .load("https://firebasestorage.googleapis.com/v0/b/locade-10249.appspot.com/o/profileImg%2Fno-img-profile.png?alt=media&token=b9b063a9-9177-4037-b845-bc7f3402cbf8")
                                                .into(mImageViewUser);
                            else
                                Picasso.with(ProfileActivity.this)
                                        .load(String.valueOf(dataSnapshot.getValue()))
                                        .into(mImageViewUser);

                        if(dataSnapshot.getKey().equals("nickName"))
                            mTextViewNickName.setText(String.valueOf(dataSnapshot.getValue()));

                        if(dataSnapshot.getKey().equals("number"))
                            mTextViewNumber.setText(String.valueOf(dataSnapshot.getValue()));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        mButtonUpdatePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getPhoto.launch("image/*");

            }
        });
    }

    ActivityResultLauncher<String> getPhoto = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    if(result != null)
                        mImageViewUser.setImageURI(result);
                        imageUri = result;

                        if(imageUri != null){
                            mAuth = FirebaseAuth.getInstance();
                            String userID = mAuth.getCurrentUser().getUid();
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://locade-10249-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference()
                                    .child("Users").child(userID);

                            StorageReference reference = mStorage.getReference().child("profileImg/" + UUID.randomUUID().toString());

//                            reference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                                @Override
//                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                                    if(task.isSuccessful()) {
////                                        Map userData = new HashMap<>();
////                                        userData.put("profileImgUrl", reference.getDownloadUrl());
////                                        databaseReference.updateChildren(userData);
//                                        Toast.makeText(ProfileActivity.this, String.valueOf(task.getResult()), Toast.LENGTH_SHORT).show();
//                                    }
//                                    else {
//                                        Toast.makeText(ProfileActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            });

                            reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                        Map userData = new HashMap<>();
                                        userData.put("profileImgUrl", uri.toString());
                                        databaseReference.updateChildren(userData);

                                            Toast.makeText(ProfileActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ProfileActivity.this, "Failed to upload", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                }
            });
}
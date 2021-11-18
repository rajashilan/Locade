package com.example.locade;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationSwipeActivity extends AppCompatActivity {

    private ImageView imageViewLocationSwipe;
    private TextView mTextViewName, mTextViewDistance;

    String imgUrl, place, distance, placeID;
    String uid;

    private FirebaseAuth mAuth;

    RecyclerView usersToSwipeRecyclerView;

    List<String> userNickName = new ArrayList<>();
    List<String> userImgUrl = new ArrayList<>();
    List<String> userID = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_swipe);

        imageViewLocationSwipe = findViewById(R.id.image_view_location_swipe);
        mTextViewName = findViewById(R.id.text_view_name_location_swipe);
        mTextViewDistance = findViewById(R.id.text_view_distance_location_swipe);

        getData();
        setData();
        getUsers();

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();

        //if this is the user's first time opening this location, add this location to user's places database along with the createdAt value
        DatabaseReference databaseReferenceCheckPlace = FirebaseDatabase.getInstance("https://locade-10249-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users")
                .child(uid).child("places").child(placeID);

        databaseReferenceCheckPlace.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.getValue() == null) {
                    DatabaseReference databaseReferenceAddPlace = FirebaseDatabase.getInstance("https://locade-10249-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users")
                                .child(uid).child("places").child(placeID);
                    Map addPlace = new HashMap<>();
                    addPlace.put("createdAt", System.currentTimeMillis());
                    databaseReferenceAddPlace.updateChildren(addPlace);

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://locade-10249-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Places")
                            .child(placeID).child("users").child(uid);

                    Map addUser = new HashMap<>();
                    addUser.put("createdAt", System.currentTimeMillis());
                    databaseReference.updateChildren(addUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getData(){
        if(getIntent().hasExtra("imgUrl") && getIntent().hasExtra("place") && getIntent().hasExtra("distance")){

            imgUrl = getIntent().getStringExtra("imgUrl");
            place = getIntent().getStringExtra("place");
            distance = getIntent().getStringExtra("distance");

            //wwe are already getting the place name here
            //just convert all into lowercase and replace spaces with underscore
            //use that variable to find the path in the places database
            //find the users under the database
            //assgin to list, use for loop in the list to get each user's details and assign them all to each array list
            //use those array list to populate the userstoswipeadapter

            //if user likes another user
            //if current location id is not present in user's database
            //add that current location id to user's place database
            //add

            placeID = place.trim().replaceAll(" ", "_").toLowerCase();

        } else {
            Toast.makeText(LocationSwipeActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    private void setData(){

        Picasso.with(this)
                .load(imgUrl)
                .into(imageViewLocationSwipe);

        mTextViewName.setText(place);
        mTextViewDistance.setText(distance);
    }

    private void getUsers(){

        usersToSwipeRecyclerView = findViewById(R.id.usersRecyclerView);

        UsersToSwipeAdapter usersToSwipeAdapter = new UsersToSwipeAdapter(LocationSwipeActivity.this, userNickName, userImgUrl, userID, placeID);
        usersToSwipeRecyclerView.setAdapter(usersToSwipeAdapter);
        usersToSwipeRecyclerView.setLayoutManager(new LinearLayoutManager(LocationSwipeActivity.this));

        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://locade-10249-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Places")
                .child(placeID).child("users");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    //if(String.valueOf(dataSnapshot.getKey()).equals("users")){
                        //if(!String.valueOf(dataSnapshot.getValue()).equals(uid)){
                    if(!userID.contains((String) dataSnapshot.getKey())){
                        userID.add((String) dataSnapshot.getKey());
                    }
                            Log.i("userids: ", String.valueOf(userID));
                            DatabaseReference databaseReferenceUsers = FirebaseDatabase.getInstance("https://locade-10249-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference()
                                    .child("Users").child(String.valueOf(dataSnapshot.getKey()));

                            databaseReferenceUsers.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                        //Toast.makeText(LocationSwipeActivity.this, "daaei: " + dataSnapshot.getKey(), Toast.LENGTH_LONG).show();
                                        if(dataSnapshot.getKey().equals("nickName"))
                                            userNickName.add((String) dataSnapshot.getValue());

                                        if(dataSnapshot.getKey().equals("profileImgUrl"))
                                            if(String.valueOf(dataSnapshot.getValue()).equals("default"))
                                                userImgUrl.add("https://firebasestorage.googleapis.com/v0/b/locade-10249.appspot.com/o/profileImg%2Fno-img-profile.png?alt=media&token=b9b063a9-9177-4037-b845-bc7f3402cbf8");
                                            else
                                                userImgUrl.add((String) dataSnapshot.getValue());
                                    }
                                    usersToSwipeAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        //}
                    //}
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent goToLocations = new Intent(LocationSwipeActivity.this, LocationsActivity.class);
        startActivity(goToLocations);
        finish();
    }
}
package com.example.locade;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatsActivity extends AppCompatActivity {

    RecyclerView myMessagesRecyclerView;
    List<String> userNickName = new ArrayList<>();
    List<String> userImgUrl = new ArrayList<>();
    List<String> userNumber = new ArrayList<>();
    List<String> userID = new ArrayList<>();
    List<String> placeID = new ArrayList<>();

    private ImageButton locationButton, profileButton;

    private FirebaseAuth mAuth;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        locationButton = findViewById(R.id.locations_button);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToChats = new Intent(ChatsActivity.this, LocationsActivity.class);
                startActivity(goToChats);
                finish();
            }
        });

        profileButton = findViewById(R.id.profile_button);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToProfile = new Intent(ChatsActivity.this, ProfileActivity.class);
                startActivity(goToProfile);
                finish();
            }
        });

        //need to get roots that has the current user id in it
        //use the user id to get info of the other user

        //pass other user ids, other user nicknames, placeid

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();

        getMessages();

    }

    private void getMessages(){
        myMessagesRecyclerView = findViewById(R.id.myMessagesRecyclerView);

        MyMessagesAdapter myMessagesAdapter = new MyMessagesAdapter(this, userNickName, userImgUrl, userNumber, userID, placeID);
        myMessagesRecyclerView.setAdapter(myMessagesAdapter);
        myMessagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference databaseReferenceCheckMessages = FirebaseDatabase.getInstance("https://locade-10249-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Messages");

        databaseReferenceCheckMessages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                    String userID1 = dataSnapshot.getKey();
                    userID1 = userID1.split("_")[0];
                    Log.i("userID1: ", userID1);

                    String userID2 = dataSnapshot.getKey();
                    userID2 = userID2.split("_")[1];
                    Log.i("userID2: ", userID2);

                    if(userID1.equals(uid)){
                        String place = dataSnapshot.getKey();
                        place = place.split(userID2)[1];
                        place = place.substring(1);
                        Log.i("place: ", place);

                        //if user id 1 is the current user id, then user id 2 is the other user's id

                        DatabaseReference databaseReferenceGetUser = FirebaseDatabase.getInstance("https://locade-10249-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users")
                                .child(userID2);

                        String finalPlace = place;
                        String finalUserID1 = userID2;
                        databaseReferenceGetUser.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                                    if(dataSnapshot.getKey().equals("nickName"))
                                        userNickName.add((String) dataSnapshot.getValue());

                                    if(dataSnapshot.getKey().equals("profileImgUrl"))
                                        userImgUrl.add((String) dataSnapshot.getValue());

                                    if(dataSnapshot.getKey().equals("number"))
                                        userNumber.add((String) dataSnapshot.getValue());

                                    placeID.add(finalPlace);
                                    userID.add(finalUserID1);
                                }

                                myMessagesAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    } else if(userID2.equals(uid)){
                        String place = dataSnapshot.getKey();
                        place = place.split(userID2)[1];
                        place = place.substring(1);

                        //if user id 2 is the current user id, then user id 1 is the other user's id

                        DatabaseReference databaseReferenceGetUser = FirebaseDatabase.getInstance("https://locade-10249-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users")
                                .child(userID1);

                        String finalUserID = userID1;
                        String finalPlace = place;
                        databaseReferenceGetUser.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                    if(dataSnapshot.getKey().equals("nickName"))
                                        userNickName.add((String) dataSnapshot.getValue());

                                    if(dataSnapshot.getKey().equals("profileImgUrl"))
                                        userImgUrl.add((String) dataSnapshot.getValue());

                                    if(dataSnapshot.getKey().equals("number"))
                                        userNumber.add((String) dataSnapshot.getValue());

                                    placeID.add(finalPlace);
                                    userID.add(finalUserID);
                                }
                                myMessagesAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
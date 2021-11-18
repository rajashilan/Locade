package com.example.locade;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LocationsActivity extends AppCompatActivity {

    RecyclerView mySpotsRecyclerView;

    private static final int REQEUST_CODE_LOCATION_PERMISSION = 1;
    Double latitude, longitude;

    private FirebaseAuth mAuth;

    private TextView mTextViewYourSpotsDiplay;

    private ImageButton chatButton, profileButton;

    RecyclerView spotsNearMeRecyclerView;
    DatabaseReference database;
    ArrayList<Locations> list;
    List<String> myLocationsName = new ArrayList<>();
    List<String> myLocationsDistance = new ArrayList<>();
    List<String> myLocationsImg = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);

        if (ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    LocationsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQEUST_CODE_LOCATION_PERMISSION
            );
        } else {
            getCurrentLocation();
        }

        mTextViewYourSpotsDiplay = findViewById(R.id.text_view_your_spots_display);

        chatButton = findViewById(R.id.chats_button);
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToChats = new Intent(LocationsActivity.this, ChatsActivity.class);
                startActivity(goToChats);
                finish();
            }
        });

        profileButton = findViewById(R.id.profile_button);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToProfile = new Intent(LocationsActivity.this, ProfileActivity.class);
                startActivity(goToProfile);
                finish();
            }
        });



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQEUST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getCurrentLocation() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            return;
        }

        LocationServices.getFusedLocationProviderClient(LocationsActivity.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(LocationsActivity.this)
                                .removeLocationUpdates(this);

                        if(locationResult != null && locationResult.getLocations().size() > 0){
                            int latestLocationIndex = locationResult.getLocations().size()- 1;

                            double lat =
                                    locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            double longi =
                                    locationResult.getLocations().get(latestLocationIndex).getLongitude();

                            latitude = lat;
                            longitude = longi;

                            //initiate spots near me adapter and recycler view
                            spotsNearMeRecyclerView = findViewById(R.id.spotsNearMeRecyclerView);

                            database = FirebaseDatabase.getInstance("https://locade-10249-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Places");

                            list = new ArrayList<>();
                            SpotsNearMeAdapter spotsNearMeAdapter = new SpotsNearMeAdapter(LocationsActivity.this, list, latitude, longitude);
                            spotsNearMeRecyclerView.setAdapter(spotsNearMeAdapter);
                            spotsNearMeRecyclerView.setLayoutManager(new LinearLayoutManager(LocationsActivity.this));

                            database.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        Locations locations = dataSnapshot.getValue(Locations.class);
                                        list.add(locations);
                                    }
                                    spotsNearMeAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            //initiate my spots adapter and recycler view
                            mySpotsRecyclerView = findViewById(R.id.mySpotsRecyclerView);

//                            if(myLocationsName.size() < 1)
//                                mTextViewYourSpotsDiplay.setVisibility(View.INVISIBLE);
                            MySpotsAdapter mySpotsAdapter = new MySpotsAdapter(LocationsActivity.this, myLocationsName, myLocationsDistance, myLocationsImg, latitude, longitude);
                            mySpotsRecyclerView.setAdapter(mySpotsAdapter);
                            mySpotsRecyclerView.setLayoutManager(new LinearLayoutManager(LocationsActivity.this, LinearLayoutManager.HORIZONTAL, false));

                            mAuth = FirebaseAuth.getInstance();

                            String userID = mAuth.getCurrentUser().getUid();

                            if(!userID.equals("")){
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://locade-10249-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference()
                                        .child("Users").child(userID).child("places");

                                databaseReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            DatabaseReference databaseReferenceMyLocations = FirebaseDatabase.getInstance("https://locade-10249-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Places")
                                                    .child(dataSnapshot.getKey());

                                            databaseReferenceMyLocations.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                        //here it is iterating through the children under places
                                                        if(dataSnapshot.getKey().equals("name"))
                                                            myLocationsName.add((String) dataSnapshot.getValue());

                                                        if(dataSnapshot.getKey().equals("location"))
                                                            myLocationsDistance.add((String) dataSnapshot.getValue());

                                                        if(dataSnapshot.getKey().equals("img_url"))
                                                            myLocationsImg.add((String) dataSnapshot.getValue());
                                                    }
                                                     mySpotsAdapter.notifyDataSetChanged();
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                        }
                    }
                }, Looper.getMainLooper());

    }
}
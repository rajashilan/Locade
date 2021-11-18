package com.example.locade;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsersToSwipeAdapter extends RecyclerView.Adapter<UsersToSwipeAdapter.MyViewHolder> {

    Context context;
    List<String> userNickName, userImgUrl, userID;
    String placeID;

    public UsersToSwipeAdapter(Context context, List<String> userNickName, List<String> userImgUrl, List<String> userID, String placeID){
        this.context = context;
        this.userNickName = userNickName;
        this.userImgUrl = userImgUrl;
        this.userID = userID;
        this.placeID = placeID;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String currentUserID = auth.getCurrentUser().getUid();
        String uid = userID.get(position);

        DatabaseReference databaseReferenceLikedUser = FirebaseDatabase.getInstance("https://locade-10249-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference()
                .child("Users").child(uid).child("places").child(placeID).child("matchedUsers").child(currentUserID);

        databaseReferenceLikedUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (snapshot.getValue() == null) {
                    //no data available
                    //Toast.makeText(context, "No", Toast.LENGTH_SHORT).show();

                    if(!uid.equals(currentUserID)){
                        Picasso.with(context)
                                .load(userImgUrl.get(position))
                                .into(holder.mImageViewUserProfileImg);

                        holder.mTextViewUserNickName.setText(userNickName.get(position));
                    } else {
                        holder.mCardViewUserSwipe.setVisibility(View.GONE);
                    }
                } else {

                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                        String matched = String.valueOf(snapshot.getValue());
                        matched = matched.split("matched=")[1];
                        matched = matched.substring(0, matched.length()-1);
                        Log.i("matched: ", matched);

                        if(dataSnapshot.getKey().equals("likedBy")){
                            if(dataSnapshot.getValue().equals(currentUserID) || matched.equals("true")){
                                holder.mCardViewUserSwipe.setVisibility(View.GONE);
                            } else {
                                Picasso.with(context)
                                        .load(userImgUrl.get(position))
                                        .into(holder.mImageViewUserProfileImg);

                                holder.mTextViewUserNickName.setText(userNickName.get(position));
                            }
                        }
                    }

                    if(userNickName.size() == 0)
                        Toast.makeText(context, "No users available :(", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(userNickName.size() == 0)
            Toast.makeText(context, "No users available :(", Toast.LENGTH_SHORT).show();

        holder.mImageButtonLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                DatabaseReference databaseReferenceCurrentUser = FirebaseDatabase.getInstance("https://locade-10249-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference()
                        .child("Users").child(currentUserID).child("places").child(placeID).child("matchedUsers").child(uid);

                databaseReferenceCurrentUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.getValue() == null) {
                            //Toast.makeText(context, "nope", Toast.LENGTH_SHORT).show();
                            Map currentUserUpdate = new HashMap<>();
                            currentUserUpdate.put("matched", false);
                            currentUserUpdate.put("likedBy", currentUserID);
                            databaseReferenceCurrentUser.updateChildren(currentUserUpdate);

                            DatabaseReference databaseReferenceLikedUser = FirebaseDatabase.getInstance("https://locade-10249-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference()
                                    .child("Users").child(uid).child("places").child(placeID).child("matchedUsers").child(currentUserID);

                            Map likedUserUpdate = new HashMap<>();
                            likedUserUpdate.put("matched", false);
                            likedUserUpdate.put("likedBy", currentUserID);
                            databaseReferenceLikedUser.updateChildren(likedUserUpdate);
                        } else {


                            //update match value to true for current user
                            DatabaseReference databaseReferenceCurrentUserMatch = FirebaseDatabase.getInstance("https://locade-10249-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference()
                                    .child("Users").child(currentUserID).child("places").child(placeID).child("matchedUsers").child(uid);

                            Map updateCurrentUserMatch = new HashMap<>();
                            updateCurrentUserMatch.put("matched", true);
                            databaseReferenceCurrentUserMatch.updateChildren(updateCurrentUserMatch);


                            //update match value to true for liked user
                            DatabaseReference databaseReferenceLikedUserMatch = FirebaseDatabase.getInstance("https://locade-10249-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference()
                                    .child("Users").child(uid).child("places").child(placeID).child("matchedUsers").child(currentUserID);

                            Map updateLikedUserMatch = new HashMap<>();
                            updateLikedUserMatch.put("matched", true);
                            databaseReferenceLikedUserMatch.updateChildren(updateLikedUserMatch);

                            //msgs root for current user
                            String currentUserChatID = currentUserID+"_"+uid+"_"+placeID;
                            String likedUserChatID = uid+"_"+currentUserID+"_"+placeID;

                            DatabaseReference databaseReferenceCurrentUserChat = FirebaseDatabase.getInstance("https://locade-10249-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference()
                                    .child("Messages").child(currentUserChatID);

                            Map createCurrentUserChat = new HashMap<>();
                            createCurrentUserChat.put("createdAt", System.currentTimeMillis());
                            databaseReferenceCurrentUserChat.updateChildren(createCurrentUserChat);


                            //msgs root for liked user
                            DatabaseReference databaseReferenceLikedUserChat = FirebaseDatabase.getInstance("https://locade-10249-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference()
                                    .child("Messages").child(likedUserChatID);

                            Map createLikedUserChat= new HashMap<>();
                            createLikedUserChat.put("createdAt", System.currentTimeMillis());
                            databaseReferenceLikedUserChat.updateChildren(createLikedUserChat);

                            //update matched to true
                            //create new chat with current user id and the other user id
                            //create messages root
                            //inside,
                            //create new child with chat id for current user with current user id, and in it, liked user id
                            //chat id = currentuserid_otheruserid_placeid
                            //create another child for the other user with other user id, and in it, current user id
                            //for the chat,
                            //chat data, message time, sender, reciever
                            //sender and reciever are user ids
                            //whenever a chat is sent, create an id for the chat and update it in both roots

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return userNickName.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView mImageViewUserProfileImg;
        TextView mTextViewUserNickName;
        ImageButton mImageButtonLike;
        CardView mCardViewUserSwipe;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageViewUserProfileImg = itemView.findViewById(R.id.image_view_user_location_swipe_card);
            mTextViewUserNickName = itemView.findViewById(R.id.text_view_user_location_swipe_card);
            mImageButtonLike = itemView.findViewById(R.id.image_button_like);
            mCardViewUserSwipe = itemView.findViewById(R.id.card_view_swipe_user);
        }
    }
}

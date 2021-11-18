package com.example.locade;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MyMessagesAdapter extends RecyclerView.Adapter<MyMessagesAdapter.MyViewHolder> {

    Context context;
    List<String> userNickName, userImgUrl, userNumber, userID, placeID;

    public MyMessagesAdapter(Context context, List<String> userNickName, List<String> userImgUrl, List<String> userNumber, List<String> userID, List<String> placeID){
        this.context = context;
        this.userNickName = userNickName;
        this.userImgUrl = userImgUrl;
        this.userNumber = userNumber;
        this.userID = userID;
        this.placeID = placeID;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_messages, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        if(position%2==0){
            holder.mTextViewUser.setText(userNickName.get(position));

            String placeName = placeID.get(position).trim().replaceAll("_", " ");

            holder.mTextViewMsgPreview.setText(placeName);

            holder.mTextViewUserNumber.setText(userNumber.get(position));
            Picasso.with(context)
                    .load(userImgUrl.get(position))
                    .into(holder.mImageViewUserPic);
        } else {
            holder.mCardViewMyMsgs.setVisibility(View.GONE);
            holder.mTextViewUser.setVisibility(View.GONE);
            holder.mTextViewMsgPreview.setVisibility(View.GONE);
            holder.mTextViewUserNumber.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return userNickName.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mTextViewUser, mTextViewUserNumber, mTextViewMsgPreview;
        ImageView mImageViewUserPic;
        CardView mCardViewMyMsgs;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mTextViewUser = itemView.findViewById(R.id.text_view_my_messages_username);
            mTextViewMsgPreview = itemView.findViewById(R.id.text_view_my_messages_preview);
            mTextViewUserNumber = itemView.findViewById(R.id.text_view_my_messages_number);
            mImageViewUserPic = itemView.findViewById(R.id.image_view_my_messages_userpic);
            mCardViewMyMsgs = itemView.findViewById(R.id.card_view_my_messages);
        }
    }
}

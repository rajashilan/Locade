package com.example.locade;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MySpotsAdapter extends RecyclerView.Adapter<MySpotsAdapter.MyViewHolder> {

    Context context;
    List<String> myLocationName, myLocationDistance, myLocationImgUrl;
    double currentLatitude, currentLongitude;

    public MySpotsAdapter(Context context, List<String> myLocationName, List<String> myLocationDistance, List<String> myLocationImgUrl, double currentLatitude, double currentLongitude){
        this.context = context;
        this.myLocationName = myLocationName;
        this.myLocationDistance = myLocationDistance;
        this.myLocationImgUrl = myLocationImgUrl;
        this.currentLatitude = currentLatitude;
        this.currentLongitude = currentLongitude;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_spots, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.mTextViewLocation.setText(myLocationName.get(position));

        String placeLocation = myLocationDistance.get(position);
        Double placeLatitude = Double.parseDouble(placeLocation.split(",")[0].trim());
        Double placeLongitude = Double.parseDouble(placeLocation.split(",")[1].trim());

        Location current = new Location("");

        current.setLatitude(currentLatitude);
        current.setLongitude(currentLongitude);

        Location place = new Location("");
        place.setLatitude(placeLatitude);
        place.setLongitude(placeLongitude);

        float placeDistance = Math.round(current.distanceTo(place)/1000);
        String placeDistanceString = String.valueOf(placeDistance);

        holder.mTextViewDistance.setText(placeDistanceString+"km");

        Picasso.with(context)
                .load(myLocationImgUrl.get(position))
                .into(holder.mImageViewLocation);

        holder.mCardViewMySpots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToPlace = new Intent(context, LocationSwipeActivity.class);
                goToPlace.putExtra("imgUrl", myLocationImgUrl.get(position));
                goToPlace.putExtra("place", myLocationName.get(position));
                goToPlace.putExtra("distance", placeDistanceString+"km");
                context.startActivity(goToPlace);
            }
        });
    }

    @Override
    public int getItemCount() {
        return myLocationName.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mTextViewLocation, mTextViewDistance;
        ImageView mImageViewLocation;
        CardView mCardViewMySpots;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextViewLocation = itemView.findViewById(R.id.text_view_my_spots_name);
            mTextViewDistance = itemView.findViewById(R.id.text_view_my_spots_distance);
            mImageViewLocation = itemView.findViewById(R.id.image_view_my_spots);
            mCardViewMySpots = itemView.findViewById(R.id.card_view_my_spots);
        }
    }
}

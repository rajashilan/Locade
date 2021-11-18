package com.example.locade;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
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

public class SpotsNearMeAdapter extends RecyclerView.Adapter<SpotsNearMeAdapter.MyViewHolder> {

    Context context;
    ArrayList<Locations> list;
    double currentLatitude, currentLongitude;

    public SpotsNearMeAdapter(Context context, ArrayList<Locations> list, double currentLatitude, double currentLongitude){
        this.context = context;
        this.list = list;
        this.currentLatitude = currentLatitude;
        this.currentLongitude = currentLongitude;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.spots_near_you, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Locations locations = list.get(position);

        holder.mTextViewLocation.setText(locations.getName());

        String placeLocation = locations.getLocation();
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
                .load(locations.getImg_url())
                .into(holder.mImageViewLocation);

        holder.mCardViewSpots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToPlace = new Intent(context, LocationSwipeActivity.class);
                goToPlace.putExtra("imgUrl", locations.getImg_url());
                goToPlace.putExtra("place", locations.getName());
                goToPlace.putExtra("distance", placeDistanceString+"km");
                context.startActivity(goToPlace);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mTextViewLocation, mTextViewDistance;
        ImageView mImageViewLocation;
        CardView mCardViewSpots;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextViewLocation = itemView.findViewById(R.id.textViewLocationNear);
            mTextViewDistance = itemView.findViewById(R.id.textViewDistanceNear);
            mImageViewLocation = itemView.findViewById(R.id.imageViewLocationNear);
            mCardViewSpots = itemView.findViewById(R.id.card_view_spots_near_you);
        }
    }

}

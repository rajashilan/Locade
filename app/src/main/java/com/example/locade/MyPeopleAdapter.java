/*
package com.example.locade;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyPeopleAdapter extends RecyclerView.Adapter<MyPeopleAdapter.MyViewHolder> {

    String people[];
    int images[];
    Context context;

    public MyPeopleAdapter(Context ct, String ppl[], int img[]){
        context = ct;
        people = ppl;
        images = img;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_people, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.mTextViewPeople.setText(people[position]);
        holder.mImageViewPeople.setImageResource(images[position]);
    }

    @Override
    public int getItemCount() {
        return people.length;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mTextViewPeople;
        ImageView mImageViewPeople;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextViewPeople = itemView.findViewById(R.id.text_view_my_people);
            mImageViewPeople = itemView.findViewById(R.id.image_view_my_people);
        }
    }
}
*/

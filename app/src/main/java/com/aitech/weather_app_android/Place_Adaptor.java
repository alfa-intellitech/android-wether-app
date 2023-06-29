package com.aitech.weather_app_android;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public  class Place_Adaptor extends RecyclerView.Adapter<Place_Holder>{
    Context context;
    ArrayList<Geoname>geonames;

    public Place_Adaptor(Context context, ArrayList<Geoname> geonames) {
        this.context = context;
        this.geonames = geonames;
    }


    @NonNull
    @Override
    public Place_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Place_Holder(LayoutInflater.from(context).inflate(R.layout.placenames, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull Place_Holder holder, int position) {
        final Geoname data = geonames.get(position);
        holder.place_list.setText(geonames.get(position).getName());

        holder.place_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, MainActivity.class);
                intent.putExtra("yourcity",data.getName());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
       return geonames.size();
    }
}

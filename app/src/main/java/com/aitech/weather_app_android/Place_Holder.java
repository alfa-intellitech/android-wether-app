package com.aitech.weather_app_android;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aitech.weather_app_android.R;

public class Place_Holder extends RecyclerView.ViewHolder{
    TextView place_list;

    public Place_Holder(@NonNull View itemView) {
        super(itemView);
        place_list=itemView.findViewById(R.id.place_list);
    }
}

package com.aitech.weather_app_android;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {
    TextView cityname,citytemp;
    RelativeLayout savecity;
    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        cityname=itemView.findViewById(R.id.cityname);
        citytemp=itemView.findViewById(R.id.citytemp);
        savecity=itemView.findViewById(R.id.savecity);
    }
}

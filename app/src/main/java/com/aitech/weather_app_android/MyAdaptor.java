package com.aitech.weather_app_android;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdaptor extends RecyclerView.Adapter<MyViewHolder> {

    Context context;
    ArrayList<DataModel> dataModels;

    public MyAdaptor(Context context, ArrayList<DataModel> dataModels) {
        this.context = context;
        this.dataModels = dataModels;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.citys, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final DataModel data = dataModels.get(position);

        holder.cityname.setText(dataModels.get(position).getCityname());
        holder.citytemp.setText(dataModels.get(position).getCitytemp()+"°c");
        int temp=Integer.parseInt(dataModels.get(position).getCitytemp());
        if (temp > 37) {

            holder.savecity.setBackgroundResource(R.drawable.orange_greadient);

        } else if (temp <= 37 && temp > 20) {

            holder.savecity.setBackgroundResource(R.drawable.yellow_greadient);

        } else {

            holder.savecity.setBackgroundResource(R.drawable.skyblue_greadient);

        }
        holder.cityname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, MainActivity.class);
                intent.putExtra("yourcity",data.getCityname());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

    }



    @Override
    public int getItemCount() {
        if (dataModels == null)
        {
            return 0;
        }
            return dataModels.size();

    }
}

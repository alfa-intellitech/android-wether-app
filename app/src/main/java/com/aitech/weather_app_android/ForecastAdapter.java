package com.aitech.weather_app_android;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {
    private List<ForecastItem> forecastItems;

    public ForecastAdapter(List<ForecastItem> forecastItems) {
        this.forecastItems = forecastItems;
    }


    @Override
    public ForecastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.daily_forcast, parent, false);
        return new ForecastViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position) {
        ForecastItem forecastItem = forecastItems.get(position);
        String timestamp = forecastItem.getTimestamp();
        Date dateTime = convertTimestampToDate(timestamp);
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String time = timeFormat.format(dateTime);
        int temp= (int) forecastItem.getTemperatureInfo().getTemperature();
        float speed = (float) (forecastItem.getTemperatureInfo().getWind() * 3.6);

        holder.timestampTextView.setText(time);
        holder.temperatureTextView.setText(String.valueOf(temp)+"°c");
//        holder.windtextview.setText(String.valueOf(speed)+"km/h");
//        holder.windtextview.setVisibility(View.INVISIBLE);


        if (temp > 37) {
            holder.daily_forcast.setBackgroundResource(R.drawable.orange_greadient);
            holder.img.setImageResource(R.drawable.fever);

        } else if (temp <= 37 && temp > 20) {
            holder.daily_forcast.setBackgroundResource(R.drawable.yellow_greadient);
            holder.img.setImageResource(R.drawable.thermometer);
        } else {
            holder.daily_forcast.setBackgroundResource(R.drawable.skyblue_greadient);
            holder.img.setImageResource(R.drawable.low);

        }
    }

    @Override
    public int getItemCount() {
        return forecastItems.size();
    }

    public static class ForecastViewHolder extends RecyclerView.ViewHolder {
        public TextView timestampTextView;
        public TextView temperatureTextView,windtextview;
        ImageView img;
        LinearLayout daily_forcast;

        public ForecastViewHolder(@NonNull View itemView) {
            super(itemView);
            timestampTextView = itemView.findViewById(R.id.dailytime);
            temperatureTextView = itemView.findViewById(R.id.daily_temp);
            img=itemView.findViewById(R.id.img);
            daily_forcast=itemView.findViewById(R.id.daily_forcast);
//            windtextview=itemView.findViewById(R.id.dailywind);
        }
    }
    private Date convertTimestampToDate(String timestamp) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return dateFormat.parse(timestamp);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}

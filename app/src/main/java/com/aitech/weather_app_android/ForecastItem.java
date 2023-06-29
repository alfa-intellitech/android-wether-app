package com.aitech.weather_app_android;

import com.google.gson.annotations.SerializedName;

public class ForecastItem {
        @SerializedName("dt_txt")
        private String timestamp;

        @SerializedName("main")
        private TemperatureInfo temperatureInfo;

    public ForecastItem(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimestamp() {
            return timestamp;
        }

        public TemperatureInfo getTemperatureInfo() {
            return temperatureInfo;
        }


}

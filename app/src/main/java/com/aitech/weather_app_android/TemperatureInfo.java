package com.aitech.weather_app_android;

import com.google.gson.annotations.SerializedName;

public class TemperatureInfo {
    @SerializedName("temp")
    private double temperature;

    @SerializedName("wind")
    private double wind;

    public double getWind() {
        return wind;
    }

    public double getTemperature() {
        return temperature;
    }
}

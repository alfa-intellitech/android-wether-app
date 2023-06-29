package com.aitech.weather_app_android;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherForecastResponse {
    @SerializedName("list")
    private List<ForecastItem> forecastItems;
    public List<ForecastItem> getForecastItems() {
        return forecastItems;
    }
}

package com.aitech.weather_app_android;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApi {
    @GET("weather")
    Call<Exp>GetWeather(@Query("q")String cityname,
                            @Query("appid")String apikey);

    @GET("searchJSON")
    Call<Exp> MyPlace(
            @Query("q") String name,
            @Query("maxRows") int maxRows,
            @Query("username") String username
    );
    @GET("forecast")
    Call<WeatherForecastResponse> getWeatherForecast(
            @Query("q") String location,
            @Query("appid") String apiKey,
            @Query("units") String units,
            @Query("cnt") int count
    );

}

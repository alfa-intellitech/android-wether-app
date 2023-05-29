package com.example.retrofit;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApi {
    @GET("weather")
    Call<Exp>GetWeather(@Query("q")String cityname,
                            @Query("appid")String apikey);

}

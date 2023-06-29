package com.aitech.weather_app_android;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Exp {
    @SerializedName("main")
    Main main;
    Sys sys;

    Wind wind;


    private List<Geoname> geonames;


    public List<Geoname> getGeonames() {
        return geonames;
    }

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public Sys getSys() {
        return sys;
    }

    public void setSys(Sys sys) {
        this.sys = sys;
    }
    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }


}

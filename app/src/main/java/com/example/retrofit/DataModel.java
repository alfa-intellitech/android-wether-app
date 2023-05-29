package com.example.retrofit;

public class DataModel {
    String cityname,citytemp;

    public DataModel(String cityname, String citytemp) {
        this.cityname = cityname;
        this.citytemp = citytemp;
    }



    public String getCityname() {
        return cityname;
    }

    public void setCityname(String cityname) {
        this.cityname = cityname;
    }

    public String getCitytemp() {
        return citytemp;
    }

    public void setCitytemp(String citytemp) {
        this.citytemp = citytemp;
    }
}

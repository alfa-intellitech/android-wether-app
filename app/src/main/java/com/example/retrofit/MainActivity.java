package com.example.retrofit;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    EditText etyourcity;
    TextView tvtemp, tvdate, tvfeellike, tvmintemp, tvmextemp, tvcity, tvwind, nodata;
    LinearLayout home;
    ImageView icon, search, addbutton;
    String mycity, yourcity, yourtemp;
int i=0;
    String url = "https://api.openweathermap.org/data/2.5/";
    String apikey = "be7a255cfd8215e9ee4974d82ee66cb4";

    private  String API_USERNAME = "demo";
    private  String API_BASE_URL = "http://api.geonames.org/";
    boolean fav = false;
    MyAdaptor adaptor;
    public static ArrayList<DataModel> dataModels = new ArrayList<>();
    private SharedPreferences sharedPreferences;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("MySharedPrefs", MODE_PRIVATE);
        nodata = findViewById(R.id.nodata);
        etyourcity = findViewById(R.id.etyourcity);
        tvtemp = findViewById(R.id.tvtemp);
        tvdate = findViewById(R.id.tvdate);
        tvfeellike = findViewById(R.id.tvfeellike);
        tvmintemp = findViewById(R.id.tvmintemp);
        tvmextemp = findViewById(R.id.tvmaxtemp);
        tvcity = findViewById(R.id.tvcity);
        search = findViewById(R.id.search);
        icon = findViewById(R.id.ivicon);
        tvwind = findViewById(R.id.tvwind);
        home = findViewById(R.id.main);
        addbutton = findViewById(R.id.addbutton);
        adaptor = new MyAdaptor(getApplicationContext(),dataModels);
        sharedPreferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        Intent intent;
        intent = getIntent();
        Log.d("SecondActivity", "Received intent: " + getIntent().toString());
        mycity = intent.getStringExtra("yourcity");
        etyourcity.setText(mycity);
        for (int i = 0; i < dataModels.size(); i++) {
            if (mycity.equals(dataModels.get(i).getCityname())) {
                addbutton.setImageResource(R.drawable.like);
                fav = true;
                break;

            } else {
                addbutton.setImageResource(R.drawable.unlike);
                fav = false;
            }
        }
        GetWeather();


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click event

                if (etyourcity.getText().toString().isEmpty()) {
                    nodata.setText("enter city!!");
                } else {

                    for (int i = 0; i < dataModels.size(); i++) {
                        if (dataModels.get(i).getCityname().equals(etyourcity.getText().toString())) {
                            addbutton.setImageResource(R.drawable.like);
                            fav = true;
                            break;
                        } else {
                            addbutton.setImageResource(R.drawable.unlike);
                            fav = false;
                        }
                    }
                    GetWeather();
                }
            }
        });

        addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fav) {
                    addbutton.setImageResource(R.drawable.unlike);
                    fav = false;
                    for (int i = 0; i < dataModels.size(); i++) {
                        if (dataModels.get(i).getCityname().equals(etyourcity.getText().toString())) {
                            dataModels.remove(i);
                            SaveData(dataModels);
                        }
                    }
//

                } else {
                    addbutton.setImageResource(R.drawable.like);
                    fav = true;
                    dataModels.add(new DataModel(tvcity.getText().toString(), tvtemp.getText().toString()));
                    SaveData(dataModels);
                }

            }

        });

       // updatedata();

    }

        private void SaveData(ArrayList<DataModel> dataModels){
            sharedPreferences=getSharedPreferences("saved",MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            Gson gson=new Gson();
            String Json=gson.toJson(dataModels);
            editor.putString("list",Json);
            editor.apply();
    }
    public  void dataupdate() {

    if (!(dataModels.isEmpty())) {
            yourcity = dataModels.get(i).getCityname();
            etyourcity.setText(yourcity);
            GetWeather();
        adaptor.notifyDataSetChanged();
        i++;
        if (i >= dataModels.size()) {
            i = 0;
        }
        Referash(60000);
    }
    else {
        Log.d("TAG", "updatedata: ");
    }

}

    private void Referash(int miliceconds) {
        Handler handler=new Handler();
        Runnable runnable=new Runnable() {
            @Override
            public void run() {

                dataupdate();
            }
        };
        handler.postDelayed(runnable,miliceconds);
    }
    @Override
    public void onBackPressed() {

        dataupdate();
        Intent returnintent;
        returnintent = new Intent(this, SearchActivity.class);
        startActivity(returnintent);
        finish();
    }
    public void GetWeather() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        WeatherApi myapi = retrofit.create(WeatherApi.class);
        Call<Exp> example = myapi.GetWeather(etyourcity.getText().toString(), apikey);
        example.enqueue(new Callback<Exp>() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<Exp> call, Response<Exp> response) {
                if (response.code() == 404) {
                    Toast.makeText(MainActivity.this, "Please Enter A Valid City", Toast.LENGTH_SHORT).show();
                    // Log.i("pankaj", response.message());
                } else if (!(response.isSuccessful())) {
                    String errorMessage = "Unknown error occurred";
                    if (response.errorBody() != null) {
                        try {
                            errorMessage = response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    // Display or log the error message
                    Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    //Toast.makeText(MainActivity.this, "Response Code: " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.i("response", errorMessage);
                } else {
                    Exp mydata = response.body();
                    Main main = mydata.getMain();
                     Sys sys = mydata.getSys();
                    Wind wind = mydata.getWind();

                    Double temp = main.getTemp();
                    Double feel = main.getFeelsLike();
                    Double minimum = main.getTempMin();
                    Double maximum = main.getTempMax();
                     String country = sys.getCountry();
                    Double speedmtr = wind.getSpeed();


                    Integer temprature = (int) (temp - 273.15);
                    Integer feellike = (int) (feel - 273.15);
                    Integer mintemp = (int) (minimum - 273.15);
                    Integer maxtemp = (int) (maximum - 273.15);
                    float speed = (float) (speedmtr * 3.6);

                    LocalDateTime dateTime = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                    String formattedDateTime = dateTime.format(formatter);


                    tvdate.setText(formattedDateTime);
                    tvcity.setText(etyourcity.getText().toString());

                    tvtemp.setText(String.valueOf(temprature) + "°c");
                    tvfeellike.setText(String.valueOf(feellike) + "°c");
                    tvmintemp.setText(String.valueOf(mintemp) + "°c");
                    tvmextemp.setText(String.valueOf(maxtemp) + "°c");
                    tvwind.setText(String.valueOf(speed) + " km/h");
                    yourcity = tvcity.getText().toString();
                    yourtemp = tvtemp.getText().toString();
for ( int i=0;i<dataModels.size();i++) {
    if(yourcity.equals(dataModels.get(i).cityname)) {
        dataModels.set(i, new DataModel(dataModels.get(i).getCityname(),yourtemp));
SaveData(dataModels);
        Log.d("updates", yourcity + String.valueOf(i) + yourtemp);

    }
}
    int ic = Geticon(temprature);
    icon.setImageResource(ic);

                }
            }


            @Override
            public void onFailure(Call<Exp> call, Throwable t) {

            }
        });

    }

    private int Geticon(Integer temprature) {
        int icon = 0;
        if (temprature > 37) {
            icon = R.drawable.fever;
            home.setBackground(getDrawable(R.drawable.hot));
        } else if (temprature <= 37 && temprature > 20) {
            icon = R.drawable.thermometer;
            home.setBackground(getDrawable(R.drawable.sunny));
        } else {
            icon = R.drawable.low;
            home.setBackground(getDrawable(R.drawable.cold));
        }
        return icon;
    }
}
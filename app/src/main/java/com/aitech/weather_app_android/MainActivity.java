package com.aitech.weather_app_android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    ProgressBar progressbar;
    Integer temprature;
    EditText etyourcity;
    TextView tvtemp, tvdate, tvfeellike, tvmintemp, tvmextemp, tvcity, tvwind,dataprovider;
    LinearLayout home;
    ImageView icon, search, addbutton;
    String mycity, yourcity, yourtemp, searchcity;
    int i = 0;
    String url = "https://api.openweathermap.org/data/2.5/";
    String apikey = "be7a255cfd8215e9ee4974d82ee66cb4";

    Place_Adaptor placeAdaptor;
   ForecastAdapter forecastAdapter;
    String BASE_URL = "http://api.geonames.org/";
    int MAX_ROWS = 5;
    String USERNAME = "pankaj15";
    boolean fav;
    MyAdaptor adaptor;
    public static ArrayList<DataModel> dataModels = new ArrayList<>();
    public ArrayList<Geoname> geonames = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    RecyclerView recycler_place,recycle_dailyWeather;
    private List<ForecastItem> forecastItems;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("MySharedPrefs", MODE_PRIVATE);
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
        recycler_place = findViewById(R.id.recycler_place);
        recycler_place.setLayoutManager(new LinearLayoutManager(this));
        adaptor = new MyAdaptor(getApplicationContext(), dataModels);
        sharedPreferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        progressbar = findViewById(R.id.progressbar);
        LinearLayoutManager la=new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false);
        recycle_dailyWeather=findViewById(R.id.recycle_daily);
        recycle_dailyWeather.setLayoutManager(la);
        dataprovider=findViewById(R.id.dataprovider);
        etyourcity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etyourcity.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        // No implementation needed
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        // No implementation needed

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                        GetPlace();

                    }

                });

            }
        });
        Intent intent;
        intent = getIntent();
        Log.d("SecondActivity", "Received intent: " + getIntent().toString());
        mycity = intent.getStringExtra("yourcity");
        etyourcity.setText(mycity);
        if (!(dataModels == null)) {
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
        } else {
            addbutton.setImageResource(R.drawable.unlike);
            fav = false;
        }
        GetWeather();

//        DailyWeather();

        etyourcity.setOnEditorActionListener((v, actionId,event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                getSearchData();
            }
            return false;
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSearchData();
            }
        });


        addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(yourcity == null)) {
                    if (fav) {
                        addbutton.setImageResource(R.drawable.unlike);
                        Toast.makeText(MainActivity.this, "Removed from favourite.", Toast.LENGTH_SHORT).show();

                        fav = false;
                        if (!(dataModels == null)) {
                            for (int i = 0; i < dataModels.size(); i++) {
                                if (dataModels.get(i).getCityname().equals(yourcity)) {
                                    dataModels.remove(i);
                                    SaveData(dataModels);
                                }
                            }
                        }
                    } else {
                        fav = true;
                        addbutton.setImageResource(R.drawable.like);
                        Toast.makeText(MainActivity.this, "Added to favourite ", Toast.LENGTH_SHORT).show();

                        if (dataModels == null) {
                            DataModel dataModel = new DataModel(yourcity, temprature.toString());
                            dataModels = new ArrayList<>();
                            dataModels.add(dataModel);
                        } else {
                            dataModels.add(new DataModel(yourcity, temprature.toString()));
                        }
                        SaveData(dataModels);
                    }

                }
            }
        });
        dataprovider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String weburl="https://openweathermap.org/";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(weburl));
                startActivity(intent);
            }
        });
    }

    public void getSearchData()
    {
        searchcity = etyourcity.getText().toString().trim();
        String[] city = searchcity.split("\\s+");

        StringBuilder stringBuilder = new StringBuilder();

        for (String word : city) {
            if (word.length() > 0) {
                char firstLetter = word.charAt(0); // Get the first letter of the word
                String modifiedWord = Character.toString(firstLetter).toUpperCase() + word.substring(1);
                stringBuilder.append(modifiedWord).append(" "); // Append the modified word back to the string
            }
        }

        searchcity = stringBuilder.toString().trim();
        etyourcity.setText(searchcity);
        // Handle click event
        if (etyourcity.getText().toString().trim().isEmpty()) {
            Toast.makeText(MainActivity.this, "Enter City...", Toast.LENGTH_SHORT).show();
        } else {
            progressbar.setVisibility(View.VISIBLE);

            GetWeather();
            if (!(dataModels == null)) {
                for (int i = 0; i < dataModels.size(); i++) {
                    if (searchcity.equals(dataModels.get(i).getCityname())) {
                        addbutton.setImageResource(R.drawable.like);
                        fav = true;
                        break;
                    } else {
                        addbutton.setImageResource(R.drawable.unlike);
                        fav = false;
                    }
                }
            } else {
                addbutton.setImageResource(R.drawable.unlike);
                fav = false;
            }

        }
    }

    private boolean isNetworkConnected() {
        // Get the ConnectivityManager instance
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get the active network information
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        // Check if there is an active network connection
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public void HourleyData()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherApi weatherApiService = retrofit.create(WeatherApi.class);

        Call<WeatherForecastResponse> call = weatherApiService.getWeatherForecast(etyourcity.getText().toString(), apikey, "metric", 8);
        call.enqueue(new Callback<WeatherForecastResponse>() {
            @Override
            public void onResponse(Call<WeatherForecastResponse> call, Response<WeatherForecastResponse> response) {
                if (response.isSuccessful()) {
                    WeatherForecastResponse forecastResponse = response.body();
                    if (forecastResponse != null) {
                        for (ForecastItem forecastItem : forecastResponse.getForecastItems()) {
                            String timestamp = forecastItem.getTimestamp();
                            double temperature = forecastItem.getTemperatureInfo().getTemperature();
                            double wind = forecastItem.getTemperatureInfo().getWind();

                            Log.d("WeatherForecast", "Timestamp: " + timestamp + ", Temperature: " + temperature+"wind"+wind);
                           forecastItems= forecastResponse.getForecastItems();
                            forecastAdapter = new ForecastAdapter(forecastItems);
                            recycle_dailyWeather.setAdapter(forecastAdapter);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<WeatherForecastResponse> call, Throwable t) {
                Log.e("WeatherForecast", "Request failed: " + t.getMessage());
            }
        });
    }
    public void GetPlace() {
        if(isNetworkConnected()) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            // Create GeoNamesService instance
            WeatherApi api = retrofit.create(WeatherApi.class);

            // Make the API call
            Call<Exp> call = api.MyPlace(etyourcity.getText().toString().trim(), MAX_ROWS, USERNAME);
            call.enqueue(new Callback<Exp>() {
                @Override
                public void onResponse(Call<Exp> call, Response<Exp> response) {
                    if (response.isSuccessful()) {
                        Exp placeResponse = response.body();
                        List<Geoname> places = placeResponse.getGeonames();
                        geonames.clear();
                        for (Geoname place : places) {
                            String placeName = place.getName();
                            Log.d("Place Name", placeName);
                            geonames.add(new Geoname(placeName));
                            placeAdaptor = new Place_Adaptor(getApplicationContext(), geonames);
                            recycler_place.setAdapter(placeAdaptor);
                            // placeAdaptor.notifyDataSetChanged();

                        }
                    } else {
                        Log.e("API Error", "API request failed. Error: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<Exp> call, Throwable t) {
                    Log.e("API Error", "API request failed. Error: " + t.getMessage());
                }
            });
        }
        else
        {
            progressbar.setVisibility(View.GONE);
            Toast.makeText(this, "please check your internet connection", Toast.LENGTH_SHORT).show();
        }
    }


    private void SaveData(ArrayList<DataModel> dataModels) {
        sharedPreferences = getSharedPreferences("saved", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String Json = gson.toJson(dataModels);
        editor.putString("list", Json);
        editor.apply();
    }

    public void dataupdate() {

        if ((dataModels.size() > 0)) {
            yourcity = dataModels.get(i).getCityname();
            etyourcity.setText(yourcity);
            GetWeather();

            i++;
            if (i >= dataModels.size()) {
                i = 0;
            }
            Referash(60000);
        } else {
            Log.d("TAG", "updatedata: ");
        }

    }

    private void Referash(int miliceconds) {
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                dataupdate();
            }
        };
        handler.postDelayed(runnable, miliceconds);
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
        if(isNetworkConnected()) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            WeatherApi myapi = retrofit.create(WeatherApi.class);
            Call<Exp> example = myapi.GetWeather(etyourcity.getText().toString().trim(), apikey);

            example.enqueue(new Callback<Exp>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onResponse(Call<Exp> call, Response<Exp> response) {
                    if (response.code() == 404) {
                        Toast.makeText(MainActivity.this, "Please Enter A Valid City", Toast.LENGTH_SHORT).show();
                        yourcity = null;
                        progressbar.setVisibility(View.GONE);
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


                        temprature = (int) (temp - 273.15);
                        Integer feellike = (int) (feel - 273.15);
                        Integer mintemp = (int) (minimum - 273.15);
                        Integer maxtemp = (int) (maximum - 273.15);
                        float speed = (float) (speedmtr * 3.6);

                        LocalDateTime dateTime = LocalDateTime.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                        String formattedDateTime = dateTime.format(formatter);

                        yourcity = etyourcity.getText().toString().trim();
                        tvdate.setText(formattedDateTime);
                        tvcity.setText(yourcity + "," + country);

                        tvtemp.setText(String.valueOf(temprature) + "°c");
                        tvfeellike.setText(String.valueOf(feellike) + "°c");
                        tvmintemp.setText(String.valueOf(mintemp) + "°c");
                        tvmextemp.setText(String.valueOf(maxtemp) + "°c");
                        tvwind.setText(String.valueOf(speed) + " km/h");

                        yourtemp = temprature.toString();
                        if (progressbar != null) {
                            progressbar.setVisibility(View.GONE);
                        }
                        if (!(dataModels == null)) {
                            for (int i = 0; i < dataModels.size(); i++) {
                                if (yourcity.equals(dataModels.get(i).cityname)) {
                                    dataModels.set(i, new DataModel(dataModels.get(i).getCityname(), yourtemp));
                                    SaveData(dataModels);
                                    adaptor.notifyDataSetChanged();
                                    Log.d("updates", yourcity + String.valueOf(i) + yourtemp);

                                }
                            }
                        }

                        int ic = Geticon(temprature);
                        icon.setImageResource(ic);
                        HourleyData();
                    }
                }


                @Override
                public void onFailure(Call<Exp> call, Throwable t) {

                }
            });
        }
        else
        {
            progressbar.setVisibility(View.GONE);
            Toast.makeText(this, "please check your internet connection", Toast.LENGTH_SHORT).show();
        }

    }

    private int Geticon(Integer temprature) {
        int icon = 0;
        if (temprature > 37) {
            icon = R.drawable.fever;
            home.setBackground(getDrawable(R.drawable.orange_greadient));


        } else if (temprature <= 37 && temprature > 20) {
            icon = R.drawable.thermometer;
            home.setBackground(getDrawable(R.drawable.yellow_greadient));


        } else {
            icon = R.drawable.low;
            home.setBackground(getDrawable(R.drawable.skyblue_greadient));

        }
        return icon;
    }
}
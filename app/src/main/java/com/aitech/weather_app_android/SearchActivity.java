package com.aitech.weather_app_android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchActivity extends AppCompatActivity {
    EditText yourcity;
    ImageView ivsearch;
    String city;
    TextView tvcity;
    RecyclerView recyclerView,recycler_place;
    MyAdaptor adaptor;
    Place_Adaptor placeAdaptor;
     String BASE_URL = "http://api.geonames.org/";
     int MAX_ROWS = 5;
     String USERNAME = "pankaj15";
     int i=0;

    public    ArrayList<Geoname> geonames = new ArrayList<>();
    @SuppressLint({"MissingInflatedId", "NotifyDataSetChanged"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        yourcity = findViewById(R.id.yourcity);
        ivsearch = findViewById(R.id.ivsearch);
        tvcity = findViewById(R.id.tvcity);
        recyclerView = findViewById(R.id.recyclerview);
recycler_place=findViewById(R.id.recycler_place);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recycler_place.setLayoutManager(new LinearLayoutManager(this));


        yourcity.addTextChangedListener(new TextWatcher() {
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

            ivsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                city = yourcity.getText().toString();
                if (city.isEmpty()) {
                    tvcity.setText("enter city!!");
                } else {


                    Intent i = new Intent(SearchActivity.this, MainActivity.class);
                    i.putExtra("yourcity", city);
                    Log.d("FirstActivity", "Starting second activity with text: " + city);
                    startActivity(i);

                }
            }
        });
       MainActivity.dataModels=loadArrayList();
        updatedata();
    }

    public void GetPlace()
    {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create GeoNamesService instance
        WeatherApi api = retrofit.create(WeatherApi.class);

        // Make the API call
        Call<Exp> call = api.MyPlace(yourcity.getText().toString(), MAX_ROWS, USERNAME);
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
                        placeAdaptor = new Place_Adaptor(getApplicationContext(),geonames);
                        recycler_place.setAdapter(placeAdaptor);
                        placeAdaptor.notifyDataSetChanged();

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

    private ArrayList<DataModel> loadArrayList() {
        SharedPreferences sharedPreferences = getSharedPreferences("saved", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("list", null);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<DataModel>>() {}.getType();

        return gson.fromJson(json, type);
    }
    public void updatedata() {

            adaptor = new MyAdaptor(getApplicationContext(),MainActivity.dataModels);
            recyclerView.setAdapter(adaptor);
            adaptor.notifyDataSetChanged();
            Referash(60000);
    }

    private void Referash(int miliceconds) {
        Handler handler=new Handler();
        Runnable runnable=new Runnable() {
            @Override
            public void run() {

                updatedata();
            }
        };
        handler.postDelayed(runnable,miliceconds);
    }


    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent serviceIntent = new Intent(this, MainActivity.class);
        stopService(serviceIntent);

    }
}
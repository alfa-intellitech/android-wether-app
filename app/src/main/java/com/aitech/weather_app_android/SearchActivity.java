package com.aitech.weather_app_android;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    ImageView ivsearch,ivabout;
    String city;
    TextView tvcity,appversion,githublink;
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
        ivabout=findViewById(R.id.ivabout);
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
        yourcity.setOnEditorActionListener((v, actionId,event) -> {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        getWeatherData();
                    }
                    return false;
                });
            ivsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWeatherData();
            }

        });
            ivabout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog dialog = new Dialog(SearchActivity.this);
                    dialog.setContentView(R.layout.about_popup);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                    appversion=dialog.findViewById(R.id.appversion);
                    githublink=dialog.findViewById(R.id.githublink);
                    PackageManager packageManager = getPackageManager();
                    String versionName = "";
                    try {
                        PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
                        versionName = packageInfo.versionName;
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                    // Set the version name as the text value of the TextView
                    appversion.setText("version-"+versionName);
                    githublink.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String url="https://github.com/alfa-intellitech/android-wether-app";
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(intent);
                        }
                    });
                   }
            });



       MainActivity.dataModels=loadArrayList();
        updatedata();
    }
    public  void getWeatherData()
    {
        city = yourcity.getText().toString().trim();
        String[]yourcity=city.split("\\s+");

        StringBuilder stringBuilder = new StringBuilder();

        for (String word :yourcity) {
            if (word.length() > 0) {
                char firstLetter = word.charAt(0); // Get the first letter of the word
                String modifiedWord = Character.toString(firstLetter).toUpperCase() + word.substring(1);
                stringBuilder.append(modifiedWord).append(" "); // Append the modified word back to the string
            }
        }

        city = stringBuilder.toString().trim();
        if (city.isEmpty()) {
            Toast.makeText(SearchActivity.this, "Enter City...", Toast.LENGTH_SHORT).show();
        } else {


            Intent i = new Intent(SearchActivity.this, MainActivity.class);
            i.putExtra("yourcity", city);
            Log.d("FirstActivity", "Starting second activity with text: " + city);
            startActivity(i);

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
    public void GetPlace()
    {
if(isNetworkConnected()) {
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    // Create GeoNamesService instance
    WeatherApi api = retrofit.create(WeatherApi.class);

    // Make the API call
    Call<Exp> call = api.MyPlace(yourcity.getText().toString().trim(), MAX_ROWS, USERNAME);
    call.enqueue(new Callback<Exp>() {
        @Override
        public void onResponse(Call<Exp> call, Response<Exp> response) {
            if (response.isSuccessful()) {
                Exp placeResponse = response.body();
                List<Geoname> places = placeResponse.getGeonames();
                geonames.clear();
                if(yourcity.getText().toString()!=null) {
                    for (Geoname place : places) {
                        String placeName = place.getName();
                        Log.d("Place Name", placeName);
                        geonames.add(new Geoname(placeName));
                        placeAdaptor = new Place_Adaptor(getApplicationContext(), geonames);
                        recycler_place.setAdapter(placeAdaptor);
                        placeAdaptor.notifyDataSetChanged();

                    }
                }
                else{
                    placeAdaptor = new Place_Adaptor(getApplicationContext(), geonames);
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
else
{
    Toast.makeText(this, "please check your internet connection", Toast.LENGTH_SHORT).show();
}
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
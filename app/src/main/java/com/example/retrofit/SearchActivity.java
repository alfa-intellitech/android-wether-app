package com.example.retrofit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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

public class SearchActivity extends AppCompatActivity {
    EditText yourcity;
    ImageView ivsearch;
    String city;
    TextView tvcity;
    RecyclerView recyclerView,cityRecyclerView;
    MyAdaptor adaptor;
    SharedPreferences sharedPreferences = getSharedPreferences("saved", Context.MODE_PRIVATE);

    public static   ArrayList<DataModel> dataModel = new ArrayList<>();
    @SuppressLint({"MissingInflatedId", "NotifyDataSetChanged"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        yourcity = findViewById(R.id.yourcity);
        ivsearch = findViewById(R.id.ivsearch);
        tvcity = findViewById(R.id.tvcity);
        recyclerView = findViewById(R.id.recyclerview);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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
        //dataModel=MainActivity.dataModels;
        updatedata();

    }
    protected void onResume() {
        super.onResume();
        // Call the method in the second activity
        MainActivity mainActivity = new MainActivity();
        mainActivity.dataupdate();
    }
    private ArrayList<DataModel> loadArrayList() {

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
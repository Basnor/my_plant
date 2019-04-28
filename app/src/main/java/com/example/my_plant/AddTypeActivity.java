package com.example.my_plant;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.my_plant.fragments.FragmentAdd;
import com.example.my_plant.fragments.FragmentAddType;

import database.DBCollection;
import model.Collection;

@SuppressLint("Registered")
public class AddTypeActivity extends  AppCompatActivity{

    private static final String TAG = FragmentAddType.class.getSimpleName();

    private EditText name;
    private EditText humidity;
    private EditText temperature;
    private EditText light;
    private EditText waterPeriod;

    private DBCollection mCollectionDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_type);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.mCollectionDB = new DBCollection(this);

        name = findViewById(R.id.edit_collection_name);
        humidity = findViewById(R.id.edit_collection_humidity);
        temperature = findViewById(R.id.edit_collection_temperature);
        light = findViewById(R.id.edit_collection_light);
        waterPeriod = findViewById(R.id.edit_collection_water_period);


        Button btnAddType = findViewById(R.id.btn_add_type);
        btnAddType.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (isEmpty(name) || isEmpty(humidity) || isEmpty(temperature) || isEmpty(light) || isEmpty(waterPeriod)) {
                    Toast.makeText(getApplicationContext()
                            , "Не все поля заполнены."
                            , Toast.LENGTH_SHORT).show();
                    return;
                }

                String name_collection = name.getText().toString();
                int humidity_collection = Integer.parseInt(humidity.getText().toString());
                int temperature_collection = Integer.parseInt(temperature.getText().toString());
                int light_collection = Integer.parseInt(light.getText().toString());
                int waterPeriod_collection = Integer.parseInt(waterPeriod.getText().toString());


                if (humidity_collection < 20 || humidity_collection > 80 ||
                        temperature_collection > 50 || light_collection > 100 ||
                        waterPeriod_collection == 0) {
                    Toast.makeText(getApplicationContext()
                            , "Ошибка в веденных значениях."
                            , Toast.LENGTH_SHORT).show();
                    return;
                }

                // add the collection to database
                Collection createdCollection = mCollectionDB.createCollection(
                        name_collection, humidity_collection,
                        temperature_collection, light_collection, waterPeriod_collection);

                Log.d(TAG, "added collection : "+ createdCollection.getTypeName());
/*
                Toast.makeText(getApplicationContext()
                        , "Создан новый тип растения."
                        , Toast.LENGTH_SHORT).show();*/

                finish();
            }
        });

    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    // Позволяет прятать клавиатуру при нажатии на пустую часть поля
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }

            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCollectionDB.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

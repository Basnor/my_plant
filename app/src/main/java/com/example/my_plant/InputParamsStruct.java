package com.example.my_plant;

import android.annotation.SuppressLint;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

class InputParamsStruct {
    private static final String TAG = "INPUT PARAMS";

    int humidity;
    int temperature;
    int light;
    int flgMoist;

    // constructor
    InputParamsStruct(int humidity, int temperature, int light,
                      int flgMoist) {

        this.humidity = humidity;
        this.temperature = temperature;
        this.light = light;
        this.flgMoist = flgMoist;

        Date now = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf_updating = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Log.d(TAG, humidity + "  " + temperature + "   " + light +
                "   " + flgMoist + "   " + sdf_updating.format(now) + "///");
    }
}


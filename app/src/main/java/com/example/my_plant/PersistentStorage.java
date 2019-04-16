package com.example.my_plant;

import android.content.Context;
import android.content.SharedPreferences;

public class PersistentStorage {
    public static final String STORAGE_NAME = "CustomValues";

    public static final String ADDRESS_KEY = "btAddress";
    public static final String NAME_KEY = "namePlant";
    public static final String HUMIDITY_KEY = "humidity";
    public static final String TEMPERATURE_KEY = "temperature";
    public static final String LIGHT_KEY = "light";
    public static final String UPDATE_TIME_KEY = "lastUpdateTime";
    public static final String WATER_TIME_KEY = "lastWaterTime";

    private static SharedPreferences settings = null;
    private static SharedPreferences.Editor editor = null;
    private static Context context = null;

    public static void init( Context cont ){
        context = cont;
    }

    private static void init(){
        settings = context.getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
    }

    public static void addProperty( String name, String value ){
        if( settings == null ){
            init();
        }
        editor.putString(name, value);
        editor.apply();

    }

    public static String getProperty ( String name ){
        if( settings == null ){
            init();
        }
        return settings.getString( name, "" );
    }

}

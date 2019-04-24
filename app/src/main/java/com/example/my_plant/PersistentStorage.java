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

    //settings
    public static final String MAX_REQ_NUM = "maxReqNum";
    public static final String PERIOD_OF_RECORDs = "periodOfRecords";

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

    public static void addStrProperty( String name, String value ){
        if( settings == null ){
            init();
        }
        editor.putString(name, value);
        editor.apply();

    }

    public static void addIntProperty( String name, int value ){
        if( settings == null ){
            init();
        }
        editor.putInt(name, value);
        editor.apply();

    }

    public static void addLongProperty( String name, Long value ){
        if( settings == null ){
            init();
        }
        editor.putLong(name, value);
        editor.apply();

    }

    public static String getStrProperty ( String name ){
        if( settings == null ){
            init();
        }
        return settings.getString( name, "" );
    }

    public static int getIntProperty ( String name ){
        if( settings == null ){
            init();
        }
        return settings.getInt(name, 0);
    }

    public static Long getLongProperty ( String name ){
        if( settings == null ){
            init();
        }
        return settings.getLong( name, 0 );
    }

}

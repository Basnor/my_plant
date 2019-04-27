package model;

import java.io.Serializable;

public class Collection implements Serializable {

    public static final String TAG = "Collection";

    public static final String TABLE_COLLECTION = "collection";
    public static final String COLUMN_COLLECTION_ID = "_id";
    public static final String COLUMN_COLLECTION_NAME = "type_name";
    public static final String COLUMN_COLLECTION_HUMIDITY = "humidity";
    public static final String COLUMN_COLLECTION_TEMPERATURE = "temperature";
    public static final String COLUMN_COLLECTION_LIGHT = "light";
    public static final String COLUMN_COLLECTION_WATER_PERIOD = "water_period";

    private long mId;
    private String mTypeName;
    private int mHumidity;
    private int mTemperature;
    private int mLight;
    private long mWaterPeriod;

    public Collection() {

    }

    public Collection(String typeName, int humidity, int temperature, int light, long waterPeriod) {
        this.mTypeName = typeName;
        this.mHumidity = humidity;
        this.mTemperature = temperature;
        this.mLight = light;
        this.mWaterPeriod = waterPeriod;
    }

    public long getId() {
        return mId;
    }
    public void setId(long mId) {
        this.mId = mId;
    }

    public String getTypeName() {
        return mTypeName;
    }
    public void setTypeName(String mTypeName) {
        this.mTypeName = mTypeName;
    }

    public int getHumidity() {
        return mHumidity;
    }
    public void setHumidity(int mHumidity) {
        this.mHumidity = mHumidity;
    }

    public int getTemperature() {
        return mTemperature;
    }
    public void setTemperature(int mTemperature) { this.mTemperature = mTemperature; }

    public int getmLight() {
        return mLight;
    }
    public void setLight(int mLight) {
        this.mLight = mLight;
    }

    public long getWaterPeriod() {
        return mWaterPeriod;
    }
    public void setWaterPeriod(long mWaterPeriod) {
        this.mWaterPeriod = mWaterPeriod;
    }

}
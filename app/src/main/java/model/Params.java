package model;

import java.io.Serializable;

public class Params implements Serializable {

    public static final String TAG = "Params";

    private long mId;
    private long mDate;
    private int mHumidity;
    private int mTemperature;
    private int mLight;
    private int mFlgMoisture;
    private Profile mProfile;

    public Params() {
    }

    public Params(long date, int humidity, int temperature, int light, int flgMoisture) {
        this.mDate = date;
        this.mHumidity = humidity;
        this.mTemperature = temperature;
        this.mLight = light;
        this.mFlgMoisture = flgMoisture;
    }

    public long getId() {
        return mId;
    }
    public void setId(long mId) {
        this.mId = mId;
    }

    public long getDate() { return mDate; }
    public void setDate(long mDate) {
        this.mDate = mDate;
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
    public void setTemperature(int mTemperature) {
        this.mTemperature = mTemperature;
    }

    public int getLight() {
        return mLight;
    }
    public void setLight(int mLight) {
        this.mLight = mLight;
    }

    public int getFlgMoisture() {
        return mFlgMoisture;
    }
    public void setFlgMoisture(int mFlgMoisture) {
        this.mFlgMoisture = mFlgMoisture;
    }

    public Profile getProfile() { return mProfile; }
    public void setProfile(Profile mProfile) { this.mProfile = mProfile; }

}

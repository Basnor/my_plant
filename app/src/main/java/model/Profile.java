package model;

import java.io.Serializable;

public class Profile implements Serializable {

    public static final String TAG = "Profile";

    private long mId;
    private String mName;
    private String mAddress;
    private Collection mCollection;

    public Profile() {

    }

    public Profile(String name, String address) {
        this.mName = name;
        this.mAddress = address;
    }

    public long getId() {
        return mId;
    }
    public void setId(long mId) {
        this.mId = mId;
    }

    public String getName() { return mName; }
    public void setName(String mName) {
        this.mName = mName;
    }

    public String getAddress() {
        return mAddress;
    }
    public void setAddress(String mAddress) { this.mAddress = mAddress; }

    public Collection getCollection() {
        return mCollection;
    }
    public void setCollection(Collection mCollection) {
        this.mCollection = mCollection;
    }
}
package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Collection;
import model.Params;
import model.Profile;

public class DBCollection {

    public static final String TAG = "DBCollection";

    // Database fields
    private SQLiteDatabase mDatabase;
    private DBHelper mDbHelper;
    private Context mContext;
    private String[] mAllColumns = { DBHelper.COLUMN_COLLECTION_ID,
            DBHelper.COLUMN_COLLECTION_NAME,
            DBHelper.COLUMN_COLLECTION_HUMIDITY,
            DBHelper.COLUMN_COLLECTION_TEMPERATURE,
            DBHelper.COLUMN_COLLECTION_LIGHT,
            DBHelper.COLUMN_COLLECTION_WATER_PERIOD };

    public DBCollection(Context context) {
        this.mContext = context;
        mDbHelper = new DBHelper(context);
        // open the database
        try {
            open();
        } catch (SQLException e) {
            Log.e(TAG, "SQLException on opening database " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void open() throws SQLException {
        mDatabase = mDbHelper.getWritableDatabase();
    }

    public void close() {
        mDbHelper.close();
    }

    public Collection createCollection(String typeName,int humidity, int temperature,
                                    int light, long waterPeriod) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_COLLECTION_NAME, typeName);
        values.put(DBHelper.COLUMN_COLLECTION_HUMIDITY, humidity);
        values.put(DBHelper.COLUMN_COLLECTION_TEMPERATURE, temperature);
        values.put(DBHelper.COLUMN_COLLECTION_LIGHT, light);
        values.put(DBHelper.COLUMN_COLLECTION_WATER_PERIOD, waterPeriod);
        long insertId = mDatabase
                .insert(DBHelper.TABLE_COLLECTION, null, values);
        Cursor cursor = mDatabase.query(DBHelper.TABLE_COLLECTION, mAllColumns,
                DBHelper.COLUMN_COLLECTION_ID + " = " + insertId, null, null,
                null, null);
        cursor.moveToFirst();
        Collection newCollection = cursorToCollection(cursor);
        cursor.close();
        return newCollection;
    }

    public void deleteCollection(Collection collection) {
        long collectionId = collection.getId();
        // delete all profile and params of this type plant

        DBProfile profile = new DBProfile(mContext);
        List<Profile> listProfile = profile.getProfileOfCollection(collectionId);
        if (listProfile != null && !listProfile.isEmpty()) {
            for (Profile e : listProfile) {
                long profileId = e.getId();

                DBParams params = new DBParams(mContext);
                List<Params> listParams = params.getParamsOfProfile(profileId);
                if (listParams != null && !listParams.isEmpty()) {
                    for (Params p : listParams) {
                        params.deleteParam(p);
                    }
                }

                System.out.println("the deleted profile has the id: " + profileId);
                mDatabase.delete(DBHelper.TABLE_PROFILE, DBHelper.COLUMN_PROFILE_ID
                        + " = " + profileId, null);

                profile.deleteProfile(e);
            }
        }

        System.out.println("the deleted collection has the id: " + collectionId);
        mDatabase.delete(DBHelper.TABLE_COLLECTION, DBHelper.COLUMN_COLLECTION_ID
                + " = " + collectionId, null);
    }

    public List<Collection> getAllCollection() {
        List<Collection> listCompanies = new ArrayList<Collection>();

        Cursor cursor = mDatabase.query(DBHelper.TABLE_COLLECTION, mAllColumns,
                null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Collection collection = cursorToCollection(cursor);
                listCompanies.add(collection);
                cursor.moveToNext();
            }

            // make sure to close the cursor
            cursor.close();
        }
        return listCompanies;
    }

    public Collection getCollectionById(long id) {
        Cursor cursor = mDatabase.query(DBHelper.TABLE_COLLECTION, mAllColumns,
                DBHelper.COLUMN_COLLECTION_ID + " = ?",
                new String[] { String.valueOf(id) }, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        Collection collection = cursorToCollection(cursor);
        return collection;
    }

    protected Collection cursorToCollection(Cursor cursor) {
        Collection collection = new Collection();
        collection.setId(cursor.getLong(0));
        collection.setTypeName(cursor.getString(1));
        collection.setHumidity(cursor.getInt(2));
        collection.setTemperature(cursor.getInt(3));
        collection.setLight(cursor.getInt(4));
        collection.setWaterPeriod(cursor.getLong(5));
        return collection;
    }

}
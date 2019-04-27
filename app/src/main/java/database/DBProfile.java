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

public class DBProfile {

    public static final String TAG = "DBProfile";

    // Database fields
    private SQLiteDatabase mDatabase;
    private DBHelper mDbHelper;
    private Context mContext;
    private String[] mAllColumns = { DBHelper.COLUMN_PROFILE_ID,
            DBHelper.COLUMN_PROFILE_NAME,
            DBHelper.COLUMN_PROFILE_TYPE,
            DBHelper.COLUMN_PROFILE_ADDRESS };

    public DBProfile(Context context) {
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

    public Profile createProfile(String name, String address) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_PROFILE_NAME, name);
        values.put(DBHelper.COLUMN_PROFILE_ADDRESS, address);
        long insertId = mDatabase
                .insert(DBHelper.TABLE_PROFILE, null, values);
        Cursor cursor = mDatabase.query(DBHelper.TABLE_PROFILE, mAllColumns,
                DBHelper.COLUMN_PROFILE_ID + " = " + insertId, null, null,
                null, null);
        cursor.moveToFirst();
        Profile newProfile = cursorToProfile(cursor);
        cursor.close();
        return newProfile;
    }

    public void deleteProfile(Profile profile) {
        long id = profile.getId();
        // delete all params of this profile
        DBParams params = new DBParams(mContext);
        List<Params> listParams = params.getParamsOfProfile(id);
        if (listParams != null && !listParams.isEmpty()) {
            for (Params e : listParams) {
                params.deleteParam(e);
            }
        }

        System.out.println("the deleted profile has the id: " + id);
        mDatabase.delete(DBHelper.TABLE_PROFILE, DBHelper.COLUMN_PROFILE_ID
                + " = " + id, null);
    }

    public List<Profile> getAllProfile() {
        List<Profile> listProfile = new ArrayList<Profile>();

        Cursor cursor = mDatabase.query(DBHelper.TABLE_PROFILE, mAllColumns,
                null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Profile profile = cursorToProfile(cursor);
                listProfile.add(profile);
                cursor.moveToNext();
            }

            // make sure to close the cursor
            cursor.close();
        }
        return listProfile;
    }

    public List<Profile> getProfileOfCollection(long collectionId) {
        List<Profile> listProfile = new ArrayList<Profile>();

        Cursor cursor = mDatabase.query(DBHelper.TABLE_PROFILE, mAllColumns,
                DBHelper.COLUMN_COLLECTION_ID + " = ?",
                new String[]{String.valueOf(collectionId)}, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Profile profile = cursorToProfile(cursor);
            listProfile.add(profile);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return listProfile;
    }

    public Profile getProfileById(long id) {
        Cursor cursor = mDatabase.query(DBHelper.TABLE_PROFILE, mAllColumns,
                DBHelper.COLUMN_PROFILE_ID + " = ?",
                new String[] { String.valueOf(id) }, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        Profile profile = cursorToProfile(cursor);
        return profile;
    }

    protected Profile cursorToProfile(Cursor cursor) {
        Profile profile = new Profile();
        profile.setId(cursor.getLong(0));
        profile.setName(cursor.getString(1));

        // get The collection by id
        long collectionId = cursor.getLong(2);
        DBCollection dbCollection = new DBCollection(mContext);
        Collection collection = dbCollection.getCollectionById(collectionId);
        if (collection != null)
            profile.setCollection(collection);

        profile.setAddress(cursor.getString(3));
        return profile;
    }

}
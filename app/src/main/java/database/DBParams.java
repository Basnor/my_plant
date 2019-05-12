package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import model.Params;
import model.Profile;

public class DBParams {
    public static final String TAG = "DBParams";

    // Database fields
    private SQLiteDatabase mDatabase;
    private DBHelper mDbHelper;
    private Context mContext;
    private String[] mAllColumns = {DBHelper.COLUMN_PARAMS_ID,
            DBHelper.COLUMN_PARAMS_PROFILE, DBHelper.COLUMN_PARAMS_DATE,
            DBHelper.COLUMN_PARAMS_HUMIDITY,
            DBHelper.COLUMN_PARAMS_TEMPERATURE,
            DBHelper.COLUMN_PARAMS_LIGHT,
            DBHelper.COLUMN_PARAMS_FLG_MOISTURE};


    public DBParams(Context context) {
        this.mContext = context;
        mDbHelper = new DBHelper(context);
        // open the database
        try {
            open();
        } catch (SQLException e) {
            Log.e(TAG, "SQLException on openning database " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void open() throws SQLException {
        mDatabase = mDbHelper.getWritableDatabase();
    }

    public void close() {
        mDbHelper.close();
    }

    public Params createParam(long profileId, long date, int humidity, int temperature, int light, int flgMoisture) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_PARAMS_PROFILE, profileId);
        values.put(DBHelper.COLUMN_PARAMS_DATE, date);
        values.put(DBHelper.COLUMN_PARAMS_HUMIDITY, humidity);
        values.put(DBHelper.COLUMN_PARAMS_TEMPERATURE, temperature);
        values.put(DBHelper.COLUMN_PARAMS_LIGHT, light);
        values.put(DBHelper.COLUMN_PARAMS_FLG_MOISTURE, flgMoisture);
        long insertId = mDatabase
                .insert(DBHelper.TABLE_PARAMS, null, values);
        Cursor cursor = mDatabase.query(DBHelper.TABLE_PARAMS, mAllColumns,
                DBHelper.COLUMN_PARAMS_ID + " = " + insertId, null, null,
                null, null);
        cursor.moveToFirst();
        Params newParam = cursorToParam(cursor);
        cursor.close();
        return newParam;
    }

    public void deleteParam(Params params) {
        long id = params.getId();
        System.out.println("the deleted employee has the id: " + id);
        mDatabase.delete(DBHelper.TABLE_PARAMS, DBHelper.COLUMN_PARAMS_ID
                + " = " + id, null);
    }

    public List<Params> getParamsOfProfile(long profileId) {
        List<Params> listParams = new ArrayList<Params>();

        Cursor cursor = mDatabase.query(DBHelper.TABLE_PARAMS, mAllColumns,
                DBHelper.COLUMN_PROFILE_ID + " = ?",
                new String[]{String.valueOf(profileId)}, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Params param = cursorToParam(cursor);
            listParams.add(param);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return listParams;
    }

    public List<Params> getParamsOfProfileChart(long profileId) {
        List<Params> listParams = new ArrayList<Params>();

        Cursor cursor = mDatabase.query(DBHelper.TABLE_PARAMS, mAllColumns,
                DBHelper.COLUMN_PROFILE_ID + " = ?",
                new String[]{String.valueOf(profileId)}, null, null, DBHelper.COLUMN_PARAMS_DATE + " DESC", "100");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Params param = cursorToParam(cursor);
            listParams.add(param);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return listParams;
    }

    public List<Long> getWaterDatesToProfile(long profile_id) {

        List<Long> list = new ArrayList<Long>();

        Cursor cursor = mDatabase.query(DBHelper.TABLE_PARAMS, new String[]{DBHelper.COLUMN_PARAMS_DATE },
                DBHelper.COLUMN_PROFILE_ID + " = ? AND " + DBHelper.COLUMN_PARAMS_FLG_MOISTURE + " = ?",
                new String[]{String.valueOf(profile_id), Integer.toString(1)}, null, null, null, "30");
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Long date = cursor.getLong(0);
                list.add(date);
                cursor.moveToNext();
            }
        }
        // make sure to close the cursor
        cursor.close();
        return list;
    }

    public Params getParamById(long id) {
        Cursor cursor = mDatabase.query(DBHelper.TABLE_PARAMS, mAllColumns,
                DBHelper.COLUMN_PARAMS_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        Params params = cursorToParam(cursor);
        return params;
    }

    public long getLastDateToProfile(long profile_id) {
        Cursor cursor = mDatabase.query(DBHelper.TABLE_PARAMS, new String[]{"MAX(" + DBHelper.COLUMN_PARAMS_DATE + ") AS maxDATE"},
                DBHelper.COLUMN_PROFILE_ID + " = ?",
                new String[]{String.valueOf(profile_id)}, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        return cursor.getLong(0);
    }

    public long getLastWaterDateToProfile(long profile_id) {
        Cursor cursor = mDatabase.query(DBHelper.TABLE_PARAMS, new String[]{"MAX(" + DBHelper.COLUMN_PARAMS_DATE + ") AS maxDATE"},
                DBHelper.COLUMN_PROFILE_ID + " = ? AND " + DBHelper.COLUMN_PARAMS_FLG_MOISTURE + " = ?",
                new String[]{String.valueOf(profile_id), Integer.toString(1)}, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        return cursor.getLong(0);
    }

    protected Params cursorToParam(Cursor cursor) {
        Params params = new Params();
        params.setId(cursor.getLong(0));

        // get The profile by id
        long profileId = cursor.getLong(1);
        DBProfile dbprofile = new DBProfile(mContext);
        Profile profile = dbprofile.getProfileById(profileId);
        if (profile != null)
            params.setProfile(profile);

        params.setDate(cursor.getLong(2));
        params.setHumidity(cursor.getInt(3));
        params.setTemperature(cursor.getInt(4));
        params.setLight(cursor.getInt(5));
        params.setFlgMoisture(cursor.getInt(6));

        return params;
    }
}

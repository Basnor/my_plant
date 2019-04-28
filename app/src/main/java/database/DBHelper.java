package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    // columns of the collection table
    static final String TABLE_COLLECTION = "collection";
    static final String COLUMN_COLLECTION_ID = "_id_collection";
    static final String COLUMN_COLLECTION_NAME = "type_name";
    static final String COLUMN_COLLECTION_HUMIDITY = "humidity";
    static final String COLUMN_COLLECTION_TEMPERATURE = "temperature";
    static final String COLUMN_COLLECTION_LIGHT = "light";
    static final String COLUMN_COLLECTION_WATER_PERIOD = "water_period";

    // columns of the profile table
    static final String TABLE_PROFILE = "profile";
    static final String COLUMN_PROFILE_ID = "_id_profile";
    static final String COLUMN_PROFILE_NAME = "name";
    static final String COLUMN_PROFILE_TYPE = COLUMN_COLLECTION_ID;
    static final String COLUMN_PROFILE_ADDRESS = "address";

    // columns of the params table
    static final String TABLE_PARAMS = "params";
    static final String COLUMN_PARAMS_ID = "_id_params";
    static final String COLUMN_PARAMS_PROFILE = COLUMN_PROFILE_ID;
    static final String COLUMN_PARAMS_DATE = "date";
    static final String COLUMN_PARAMS_HUMIDITY = "humidity";
    static final String COLUMN_PARAMS_TEMPERATURE = "temperature";
    static final String COLUMN_PARAMS_LIGHT = "light";
    static final String COLUMN_PARAMS_FLG_MOISTURE = "flg_moisture";

    private static final String DATABASE_NAME = "my_plant.db";
    private static final int DATABASE_VERSION = 1;

    // SQL statement of the collection table creation
    private static final String SQL_CREATE_TABLE_COLLECTION = "CREATE TABLE " + TABLE_COLLECTION + "("
            + COLUMN_COLLECTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_COLLECTION_NAME + " TEXT NOT NULL, "
            + COLUMN_COLLECTION_HUMIDITY + " INTEGER NOT NULL, "
            + COLUMN_COLLECTION_TEMPERATURE + " INTEGER NOT NULL, "
            + COLUMN_COLLECTION_LIGHT + " INTEGER NOT NULL, "
            + COLUMN_COLLECTION_WATER_PERIOD + " INTEGER NOT NULL "
            + ");";

    // SQL statement of the profile table creation
    private static final String SQL_CREATE_TABLE_PROFILE = "CREATE TABLE " + TABLE_PROFILE + "("
            + COLUMN_PROFILE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_PROFILE_NAME + " TEXT NOT NULL, "
            + COLUMN_PROFILE_TYPE + " INTEGER NOT NULL, "
            + COLUMN_PROFILE_ADDRESS + " TEXT NOT NULL "
            + ");";

    // SQL statement of the params table creation
    private static final String SQL_CREATE_TABLE_PARAMS = "CREATE TABLE " + TABLE_PARAMS + "("
            + COLUMN_PARAMS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_PARAMS_PROFILE + " INTEGER NOT NULL, "
            + COLUMN_PARAMS_DATE + " INTEGER NOT NULL, "
            + COLUMN_PARAMS_HUMIDITY + " INTEGER NOT NULL, "
            + COLUMN_PARAMS_TEMPERATURE + " INTEGER NOT NULL, "
            + COLUMN_PARAMS_LIGHT + " INTEGER NOT NULL, "
            + COLUMN_PARAMS_FLG_MOISTURE + " INTEGER NOT NULL "
            + ");";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(SQL_CREATE_TABLE_COLLECTION);
        database.execSQL(SQL_CREATE_TABLE_PROFILE);
        database.execSQL(SQL_CREATE_TABLE_PARAMS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // clear all data
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COLLECTION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PARAMS);

        // recreate the tables
        onCreate(db);
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }
}

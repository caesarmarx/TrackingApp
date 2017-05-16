package hitec.com.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import hitec.com.consts.DBConsts;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME_PREFIX = "OFFICE_BOOK";
    private static final int DB_VERSION = 2;

    protected static String TEMP_LOCATION_TABLE_CREATE_SQL =
            "CREATE TABLE IF NOT EXISTS " + DBConsts.TABLE_TEMP_LOCATION + " (" +
                    DBConsts.FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    DBConsts.FIELD_LATITUDE + " TEXT," +
                    DBConsts.FIELD_LONGITUDE + " TEXT," +
                    DBConsts.FIELD_TRACK_TIME + " TEXT," +
                    DBConsts.FIELD_SEND + " INTEGER);";

    protected static String USER_TABLE_CREATE_SQL =
            "CREATE TABLE IF NOT EXISTS " + DBConsts.TABLE_USER + " (" +
                    DBConsts.FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    DBConsts.FIELD_USER_NAME + " TEXT," +
                    DBConsts.FIELD_CREATED_AT + " TEXT);";

    protected static String MESSAGE_TABLE_CREATE_SQL =
            "CREATE TABLE IF NOT EXISTS " + DBConsts.TABLE_MESSAGE + " (" +
                    DBConsts.FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    DBConsts.FIELD_FROM_USER + " TEXT," +
                    DBConsts.FIELD_TO_USER + " TEXT," +
                    DBConsts.FIELD_MESSAGE + " TEXT," +
                    DBConsts.FIELD_IMAGE_URL + " TEXT," +
                    DBConsts.FIELD_CREATED_AT + " TEXT);";

    public DBHelper(Context context) {
        super(context, DB_NAME_PREFIX, null, DB_VERSION);
        this.getWritableDatabase().close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        try {
            db.execSQL(TEMP_LOCATION_TABLE_CREATE_SQL);
            db.execSQL(USER_TABLE_CREATE_SQL);
            db.execSQL(MESSAGE_TABLE_CREATE_SQL);
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            onCreate(db);
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
        }
    }
}

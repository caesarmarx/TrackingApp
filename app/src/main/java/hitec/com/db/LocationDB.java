package hitec.com.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import hitec.com.consts.DBConsts;
import hitec.com.model.LocationItem;
import hitec.com.util.DBHelper;

/**
 * Created by Caesar on 5/2/2017.
 */

public class LocationDB extends DBHelper {
    private static final Object[] DB_LOCK 		= new Object[0];

    public LocationDB(Context context) {
        super(context);
    }

    public ArrayList<LocationItem> fetchAllLocations() {
        ArrayList<LocationItem> ret = null;
        try {
            String szWhere = DBConsts.FIELD_SEND + " = " + 0;
            String szOrderBy = DBConsts.FIELD_TRACK_TIME + " DESC";
            synchronized (DB_LOCK) {
                SQLiteDatabase db = getReadableDatabase();
                Cursor cursor = db.query(DBConsts.TABLE_TEMP_LOCATION, null, szWhere, null, null, null, szOrderBy);
                ret = createOutletBeans(cursor);
                db.close();
            }
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
        }

        return ret;
    }

    public long addLocation(LocationItem bean) {
        long ret = -1;
        try {
            ContentValues value = new ContentValues();
            value.put(DBConsts.FIELD_LATITUDE, bean.getLatitude());
            value.put(DBConsts.FIELD_LONGITUDE, bean.getLongitude());
            value.put(DBConsts.FIELD_TRACK_TIME, bean.getTime());
            value.put(DBConsts.FIELD_SEND, bean.getSend());
            synchronized (DB_LOCK) {
                SQLiteDatabase db = getWritableDatabase();
                ret = db.insert(DBConsts.TABLE_TEMP_LOCATION, null, value);
                db.close();
            }
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
        }

        return ret;
    }

    public void updateSendStatus(String id) {
        try {
            String szWhere = DBConsts.FIELD_ID + " = " + Integer.valueOf(id);
            ContentValues value = new ContentValues();
            value.put(DBConsts.FIELD_SEND, 1);

            synchronized (DB_LOCK) {
                SQLiteDatabase db = getReadableDatabase();
                db.update(DBConsts.TABLE_TEMP_LOCATION, value, szWhere, null);
                db.close();
            }
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
        }
    }

    private ArrayList<LocationItem> createOutletBeans(Cursor c) {
        ArrayList<LocationItem> ret = null;
        try {
            ret = new ArrayList();

            final int COL_ID	            = c.getColumnIndexOrThrow(DBConsts.FIELD_ID),
                    COL_LATITUDE     	    = c.getColumnIndexOrThrow(DBConsts.FIELD_LATITUDE),
                    COL_LONGITUDE         	= c.getColumnIndexOrThrow(DBConsts.FIELD_LONGITUDE),
                    COL_TRACK_TIME 		    = c.getColumnIndexOrThrow(DBConsts.FIELD_TRACK_TIME);

            while (c.moveToNext()) {
                LocationItem bean = new LocationItem();
                bean.setID(c.getString(COL_ID));
                bean.setLatitude(c.getString(COL_LATITUDE));
                bean.setLongitude(c.getString(COL_LONGITUDE));
                bean.setTime(c.getString(COL_TRACK_TIME));
                ret.add(bean);
            }

            c.close();
            getReadableDatabase().close();
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
        }

        return ret;
    }
}
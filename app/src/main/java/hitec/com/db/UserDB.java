package hitec.com.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import hitec.com.consts.DBConsts;
import hitec.com.model.LocationItem;
import hitec.com.model.UserItem;
import hitec.com.util.DBHelper;

/**
 * Created by Caesar on 5/2/2017.
 */

public class UserDB extends DBHelper {
    private static final Object[] DB_LOCK 		= new Object[0];

    public UserDB(Context context) {
        super(context);
    }

    public ArrayList<UserItem> fetchAllUsers() {
        ArrayList<UserItem> ret = null;
        try {
            String szOrderBy = DBConsts.FIELD_CREATED_AT + " ASC";
            synchronized (DB_LOCK) {
                SQLiteDatabase db = getReadableDatabase();
                Cursor cursor = db.query(DBConsts.TABLE_USER, null, null, null, null, null, szOrderBy);
                ret = createUserBeans(cursor);
                db.close();
            }
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
        }

        return ret;
    }

    public long addUser(UserItem bean) {
        long ret = -1;
        try {
            ContentValues value = new ContentValues();
            value.put(DBConsts.FIELD_USER_NAME, bean.getUsername());
            value.put(DBConsts.FIELD_CREATED_AT, bean.getCreatedAt());
            synchronized (DB_LOCK) {
                SQLiteDatabase db = getWritableDatabase();
                ret = db.insert(DBConsts.TABLE_USER, null, value);
                db.close();
            }
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
        }

        return ret;
    }

    private ArrayList<UserItem> createUserBeans(Cursor c) {
        ArrayList<UserItem> ret = null;
        try {
            ret = new ArrayList();

            final int COL_ID	            = c.getColumnIndexOrThrow(DBConsts.FIELD_ID),
                    COL_USER_NAME     	    = c.getColumnIndexOrThrow(DBConsts.FIELD_USER_NAME),
                    COL_CREATED_AT         	= c.getColumnIndexOrThrow(DBConsts.FIELD_CREATED_AT);

            while (c.moveToNext()) {
                UserItem bean = new UserItem();
                bean.setUserName(c.getString(COL_USER_NAME));
                bean.setCreatedAt(c.getString(COL_CREATED_AT));
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
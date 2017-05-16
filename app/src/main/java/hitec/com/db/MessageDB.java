package hitec.com.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import hitec.com.consts.DBConsts;
import hitec.com.model.MessageItem;
import hitec.com.util.DBHelper;

/**
 * Created by Caesar on 5/2/2017.
 */

public class MessageDB extends DBHelper {
    private static final Object[] DB_LOCK 		= new Object[0];

    public MessageDB(Context context) {
        super(context);
    }

    public ArrayList<MessageItem> fetchUserMessage(String user) {
        ArrayList<MessageItem> ret = null;
        try {
            String szWhere = DBConsts.FIELD_FROM_USER + " = '" + user + "' OR " + DBConsts.FIELD_TO_USER + " = '" + user + "'";
            String szOrderBy = DBConsts.FIELD_CREATED_AT + " DESC";
            synchronized (DB_LOCK) {
                SQLiteDatabase db = getReadableDatabase();
                Cursor cursor = db.query(DBConsts.TABLE_MESSAGE, null, szWhere, null, null, null, szOrderBy);
                ret = createMessageBeans(cursor);
                db.close();
            }
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
        }

        return ret;
    }

    public ArrayList<MessageItem> fetchUserMessageByDate(String user, String date) {
        ArrayList<MessageItem> ret = null;
        try {
            String szWhere = "(" + DBConsts.FIELD_FROM_USER + " = '" + user + "' OR " + DBConsts.FIELD_TO_USER + " = '" + user + "') AND DATE(" + DBConsts.FIELD_CREATED_AT + ") = '" + date + "'";
            String szOrderBy = DBConsts.FIELD_CREATED_AT + " DESC";
            synchronized (DB_LOCK) {
                SQLiteDatabase db = getReadableDatabase();
                Cursor cursor = db.query(DBConsts.TABLE_MESSAGE, null, szWhere, null, null, null, szOrderBy);
                ret = createMessageBeans(cursor);
                db.close();
            }
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
        }

        return ret;
    }

    public ArrayList<MessageItem> fetchRecentMessage(String user) {
        ArrayList<MessageItem> ret = null;
        try {
            String szWhere = DBConsts.FIELD_FROM_USER + " = '" + user + "' OR " + DBConsts.FIELD_TO_USER + " = '" + user + "'";
            String szOrderBy = DBConsts.FIELD_CREATED_AT + " DESC";
            synchronized (DB_LOCK) {
                SQLiteDatabase db = getReadableDatabase();
                Cursor cursor = db.query(DBConsts.TABLE_MESSAGE, null, szWhere, null, null, null, szOrderBy, "20");
                ret = createMessageBeans(cursor);
                db.close();
            }
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
        }

        return ret;
    }

    public long addMessage(MessageItem bean) {
        long ret = -1;
        if(isExistMessage(bean))
            return ret;
        try {
            ContentValues value = new ContentValues();
            value.put(DBConsts.FIELD_FROM_USER, bean.getFromUser());
            value.put(DBConsts.FIELD_TO_USER, bean.getToUser());
            value.put(DBConsts.FIELD_MESSAGE, bean.getMessage());
            value.put(DBConsts.FIELD_IMAGE_URL, bean.getImageURL());
            value.put(DBConsts.FIELD_CREATED_AT, bean.getTime());
            synchronized (DB_LOCK) {
                SQLiteDatabase db = getWritableDatabase();
                ret = db.insert(DBConsts.TABLE_MESSAGE, null, value);
                db.close();
            }
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
        }

        return ret;
    }

    private boolean isExistMessage(MessageItem bean) {
        try {
            String whereClause = DBConsts.FIELD_FROM_USER + " = ? AND " + DBConsts.FIELD_TO_USER + " = ? AND " + DBConsts.FIELD_MESSAGE + " = ? AND " + DBConsts.FIELD_CREATED_AT + " = ?";
            String[] whereArgs = new String[] {
                    bean.getFromUser(),
                    bean.getToUser(),
                    bean.getMessage(),
                    bean.getTime()
            };

            String szWhere = DBConsts.FIELD_FROM_USER + " = '" + bean.getFromUser() + "' AND " + DBConsts.FIELD_TO_USER + " = '" + bean.getToUser() + "' AND " + DBConsts.FIELD_MESSAGE + " = '" + bean.getMessage() + "' AND " + DBConsts.FIELD_CREATED_AT + " = '" + bean.getTime() + "'";
            synchronized (DB_LOCK) {
                SQLiteDatabase db = getReadableDatabase();
                Cursor cursor = db.query(DBConsts.TABLE_MESSAGE, null, whereClause, whereArgs, null, null, null);
                if(cursor.getCount() == 0)
                    return false;
                db.close();
            }
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
        }
        return true;
    }

    private ArrayList<MessageItem> createMessageBeans(Cursor c) {
        ArrayList<MessageItem> ret = null;
        try {
            ret = new ArrayList();

            final int COL_ID	            = c.getColumnIndexOrThrow(DBConsts.FIELD_ID),
                    COL_FROM_USER     	    = c.getColumnIndexOrThrow(DBConsts.FIELD_FROM_USER),
                    COL_TO_USER     	    = c.getColumnIndexOrThrow(DBConsts.FIELD_TO_USER),
                    COL_MESSAGE     	    = c.getColumnIndexOrThrow(DBConsts.FIELD_MESSAGE),
                    COL_IMAGE_URL     	    = c.getColumnIndexOrThrow(DBConsts.FIELD_IMAGE_URL),
                    COL_CREATED_AT         	= c.getColumnIndexOrThrow(DBConsts.FIELD_CREATED_AT);

            while (c.moveToNext()) {
                MessageItem bean = new MessageItem();
                bean.setFromUser(c.getString(COL_FROM_USER));
                bean.setToUser(c.getString(COL_TO_USER));
                bean.setMessage(c.getString(COL_MESSAGE));
                bean.setImageURL(c.getString(COL_IMAGE_URL));
                bean.setTime(c.getString(COL_CREATED_AT));
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
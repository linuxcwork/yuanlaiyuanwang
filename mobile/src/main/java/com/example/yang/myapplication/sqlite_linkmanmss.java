package com.example.yang.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.yang.util.FileOperationUtil;

import java.util.Map;


/**
 * Created by yang on 2018/3/17.
 */

public class sqlite_linkmanmss{
    public final String TAG = "DBAdapter" ;
    //头像
    public static final String KEY_ROWID = "image";
    //昵称
    public static final String KEY_NAME = "name";
	//称谓
	public static final String KEY_APPELLATION = "appellation";
    //账号
    public static final String KEY_ACTNB = "actnb";
    //手机
    public static final String KEY_EMAIL = "telphone";
    //信用值
    public static final String KEY_CREDIT_VALUES = "creditvalues";
    //关系
    public static final String KEY_RELATION = "relation";
    //类型
    public static final String EKY_MESSAGETYPE = "messagetype";
    public static final String KEY_MESSAGE_TYPE_STRING = "text";
    public static final String KEY_MESSAGE_TYPE_IMAGE  = "image";
    public static final String KEY_MESSAGE_TYPE_GIF    = "gif";
    public static final String KEY_MESSAGE_TYPE_VOICE  = "voice";
    public static final String KEY_MESSAGE_TYPE_CALL = "startcall";
    public static final String KEY_MESSAGE_TYPE_REJECT_CALL = "rejectcall";
    public static final String KEY_MESSAGE_TYPE_FINESH_CALL = "finesgcall";
    public static final String KEY_MESSAGE_TYPE_VIDEO  = "video";
    public static final String KEY_MESSAGE_TYPE_FILE   = "file";
    public static final String KEY_MESSAGE_TYPE_EMJ_AND_STRING   = "mut";
    //是否是新消息
    public static final String KEY_ISNEWMESSAGE = "isnew";
    //聊天内容
    public static final String KEY_CONTENT = "content";
    //时间
    public static final String KEY_TIME = "time";
    //消息走向
    public static final String KEY_DIRECTION = "direction";

    public final String DATABASE_NAME = "Message.db";
    public static final String DATABASE_TABLE = "contacts";
    public final int DATABASE_VERSION = 1;


    public final String CREATE_MESSAGE_DATABASE =
            "create table if not exists "+DATABASE_TABLE+"( _id integer primary key autoincrement, " +
                    KEY_ROWID+" text, " +
                    KEY_NAME+" text," +
                    KEY_ACTNB+" text," +
                    KEY_EMAIL+" text," +
                    KEY_RELATION+" INTEGER," +
                    EKY_MESSAGETYPE+" text," +
                    KEY_CREDIT_VALUES+" INTEGER,"+
                    KEY_ISNEWMESSAGE+" INTEGER," +
                    KEY_CONTENT+" text," +
                    KEY_TIME+" TEXT,"+
                    KEY_DIRECTION+" TEXT);";

    private Context context;

    private DatabaseHelper DBHelper;
    private SQLiteDatabase db=null;
    public sqlite_linkmanmss(Context context, String table, SQLiteDatabase.CursorFactory factory, Integer version) {
        this.context = context;
        FileOperationUtil.CreateDir(FileOperationUtil.SECONDMESSAFEDIRPATH);
        DBHelper = new DatabaseHelper(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    private class DatabaseHelper extends SQLiteOpenHelper
    {

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO Auto-generated method stub
            try
            {
                db.execSQL(CREATE_MESSAGE_DATABASE);
            }
            catch(SQLException e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
            Log.wtf(TAG, "Upgrading database from version "+ oldVersion + "to "+
                    newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS contacts");
            onCreate(db);
        }
    }

    public void CreateTable(String table){
        if(table == null) {
            db.execSQL(CREATE_MESSAGE_DATABASE);
        }else {
            db.execSQL(table);
        }
    }

    //open the database
    public boolean open() throws SQLException
    {

        db = DBHelper.getWritableDatabase();
        return (db == null) ? false : true;
    }
    //close the database
    public void close()
    {
        DBHelper.close();
    }

    /***********************************************************************
     * insert a contact into the database
     * ********************************************************************/
    public long insertContact(String table,Map<String, Object> map)
    {
        ContentValues initialValues = new ContentValues();
        if (map != null) {
            //增强for循环遍历
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if(entry.getValue() != null) {
                    initialValues.put(entry.getKey(), entry.getValue().toString());
                    System.out.println(entry.getValue().toString());
                }
            }
        }

        return db.insert(table, null, initialValues);
    }

    /***********************************************************************
     * delete a particular contact
     * ********************************************************************/
    public boolean deleteContact(String table,String rowId,String key)
    {
        return db.delete(table,key+" = ?", new String[]{rowId}) > 0;
    }

    /********************************************************************
     * retreves all the contacts
     * ******************************************************************/
    public Cursor getAllContacts(String tablename)
    {
        return db.query(tablename, null, null, null, null, null, null);
    }

    /*******************************************************************
     * 获取指定的内容
     * *****************************************************************/
    public Cursor getContact(String table,String key,String rowId) throws SQLException
    {
        if(db == null){
            Log.e(TAG,"not open the sql");
        }
        Cursor mCursor=
                db.query(true, table,null , key+" = ?", new String[]{rowId}, null, null, null, null);
        return mCursor;
    }

    /******************************************************************
     * updates a contact
     * ****************************************************************/
    public boolean updateContact(String table,String key,String value,Map<String, String> map)
    {
        ContentValues args = new ContentValues();
        if (map != null) {
            //增强for循环遍历
            for (Map.Entry<String, String> entry : map.entrySet()) {
                args.put(entry.getKey(), entry.getValue());
            }
        }
        return db.update(table, args, key+" = ?",  new String[]{value}) > 0;
    }

}

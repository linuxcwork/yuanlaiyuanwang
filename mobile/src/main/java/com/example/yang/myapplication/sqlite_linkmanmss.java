package com.example.yang.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.text.Editable;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by yang on 2018/3/17.
 */

public class sqlite_linkmanmss{

    static final String KEY_ROWID = "image";  //头像
    static final String KEY_NAME = "name";    //昵称
    static final String KEY_ACTNB = "actnb";  //账号
    static final String KEY_EMAIL = "telphone";  //手机
    static final String KEY_IDCARD = "id_card";  //身份证号
    static final String KEY_POSTON = "postion";  //位置
    static final String KEY_SIGNATURE = "the_signature";  //签名
    static final String KEY_MARRIAGE = "marriage";  //结婚与否
    static final String KEY_CONTENT = "content"; //聊天内容
    static final String KEY_TIME = "time";  //时间
    static final String KEY_DIRECTION = "direction";
    static final String TAG = "DBAdapter" ;


    static final String DATABASE_NAME = "MyDB.db";
    static final String DATABASE_TABLE = "contacts";
    static final int DATABASE_VERSION = 1;
    static final int OLD_VERSION = 0;


    static final String DATABASE_CREATE =
            "create table contacts( _id integer primary key autoincrement, " +
                    "name text, " +
                    "email text," +
                    "actnb text," +
                    "telphone text," +
                    "id_card text," +
                    "the_signature text," +
                    "marriage text," +
                    "content BLOB," +
                    "time text," +
                    "direction text);";
    final Context context;

    DatabaseHelper DBHelper;
    SQLiteDatabase db=null;
    String path=null;
    public sqlite_linkmanmss(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {

        this.context = context;
        path = Environment.getExternalStorageDirectory().getPath()+File.separator+"YLYW"+File.separator+"data";
        DBHelper = new DatabaseHelper(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO Auto-generated method stub
            try
            {
                db.execSQL(DATABASE_CREATE);
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

    public void CreateTable(String tablename){
        db.execSQL(tablename);
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

    //insert a contact into the database
    public long insertContact(String itable,Map<String, String> map)
    {
        ContentValues initialValues = new ContentValues();
        if (map != null) {
            //增强for循环遍历
            for (Map.Entry<String, String> entry : map.entrySet()) {
                initialValues.put(entry.getKey(),  entry.getValue());
            }
        }

        return db.insert(itable, null, initialValues);
    }
    //delete a particular contact
    public boolean deleteContact(String rowId)
    {
        return db.delete(DATABASE_TABLE,"name = ?", new String[]{rowId}) > 0;
    }
    //retreves all the contacts
    public Cursor getAllContacts()
    {
        return db.query(DATABASE_TABLE, new String[]{KEY_CONTENT,KEY_TIME,KEY_DIRECTION}, null, null, null, null, null);
    }

    /*******************************************************************
     * 获取指定的内容
     * *****************************************************************/
    public Cursor getContact(String gettable,String rowId,String condition) throws SQLException
    {
        String[] querycontact = new String[100];
        if(condition.equals(KEY_CONTENT)){
            querycontact=new String[]{KEY_CONTENT,KEY_TIME,KEY_DIRECTION};
        }else if(false){
          Log.i(TAG,"SEARCH");
        }

        Cursor mCursor=
                db.query(true, gettable, querycontact, "name = ?", new String[]{rowId}, null, null, null, null);
        if (mCursor != null)
            mCursor.moveToFirst();
        return mCursor;
    }

    //updates a contact
    public boolean updateContact(Map<String, String> map)
    {
        ContentValues args = new ContentValues();
        if (map != null) {
            //增强for循环遍历
            for (Map.Entry<String, String> entry : map.entrySet()) {
                args.put(entry.getKey(), entry.getValue());
            }
        }
        return db.update(DATABASE_TABLE, args, "name = ?", null) > 0;
    }

}

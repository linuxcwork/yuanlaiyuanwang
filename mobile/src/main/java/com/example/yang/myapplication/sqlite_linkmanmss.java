package com.example.yang.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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

    static final String TAG = "DBAdapter" ;

    static final String DATABASE_NAME = "MyDB";
    static final String DATABASE_TABLE = "contacts";
    static final int DATABASE_VERSION = 1;

    static final String DATABASE_CREATE =
            "create table contacts( _id integer primary key autoincrement, " +
                    "name text not null, email text not null);";
    final Context context;

    DatabaseHelper DBHelper;
    SQLiteDatabase db;

    public sqlite_linkmanmss(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {

        this.context = context;
        DBHelper = new DatabaseHelper(context);
    }


    private static class DatabaseHelper extends SQLiteOpenHelper
    {

        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
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

    //open the database
    public sqlite_linkmanmss open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }
    //close the database
    public void close()
    {
        DBHelper.close();
    }

    //insert a contact into the database
    public long insertContact(String name, String email,Integer idcard,String actnb,boolean marriage,
                              String postion,String rowid,String signature)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_EMAIL, email);
        initialValues.put(KEY_IDCARD, idcard);
        initialValues.put(KEY_ACTNB, actnb);
        initialValues.put(KEY_MARRIAGE, marriage);
        initialValues.put(KEY_POSTON, postion);
        initialValues.put(KEY_ROWID, rowid);
        initialValues.put(KEY_SIGNATURE, signature);

        return db.insert(DATABASE_TABLE, null, initialValues);
    }
    //delete a particular contact
    public boolean deleteContact(long rowId)
    {
        return db.delete(DATABASE_TABLE, KEY_ROWID + "=" +rowId, null) > 0;
    }
    //retreves all the contacts
    public Cursor getAllContacts()
    {
        return db.query(DATABASE_TABLE, new String[]{ KEY_ROWID,KEY_NAME,KEY_EMAIL}, null, null, null, null, null);
    }

    //retreves a particular contact
    public Cursor getContact(long rowId) throws SQLException
    {
        Cursor mCursor =
                db.query(true, DATABASE_TABLE, new String[]{ KEY_ROWID,
                        KEY_NAME, KEY_EMAIL}, KEY_ROWID + "=" + rowId, null, null, null, null, null);
        if (mCursor != null)
            mCursor.moveToFirst();
        return mCursor;
    }

    //updates a contact
    public boolean updateContact(long rowId, String name, String email,Integer idcard,String actnb,boolean marriage,
                                 String postion,String rowid,String signature)
    {
        ContentValues args = new ContentValues();
        args.put(KEY_NAME, name);
        args.put(KEY_EMAIL, email);
        args.put(KEY_IDCARD, idcard);
        args.put(KEY_ACTNB, actnb);
        args.put(KEY_MARRIAGE, marriage);
        args.put(KEY_POSTON, postion);
        args.put(KEY_ROWID, rowid);
        args.put(KEY_SIGNATURE, signature);
        return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" +rowId, null) > 0;
    }

}

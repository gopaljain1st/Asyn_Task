package com.example.testasync;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper
{
    public static final String TABLE_NAME="registration";
    public static final String COL_1="ID";
    public static final String COL_2="Name";
    public static final String COL_3="Phone";
    public static final int SYNC_STATUS_OK=0;
    public static final int SYNC_STATUS_FAILED=1;
   public DatabaseHelper(Context context)
    {
        super(context,"test.sqlite",null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
          String sql="create table contact(id integer primary key AUTOINCREMENT,name varchar(100),sync_status int)";
          sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

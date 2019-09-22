package com.example.alexd.assignment1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class datasqlite{

    public static final String KEY_NAME = "taskName";


    private final String DATABASE_NAME = "taskDatabase";
    private final String DATABASE_TABLE = "taskTable";

    private final int DATABASE_VERSION = 3;
    private datasqlite.DBHelper ourHelper;
    private final Context ourContext;
    private SQLiteDatabase ourDatabase;

    public datasqlite(Context context){
        this.ourContext = context;
    }
    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context){
            super(context, DATABASE_NAME,null,DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            /*
            CREATE TABLE AccountsTable(_id INTEGER PRIMARY KEY AUTOINCREMENT,
                                    person_name TEXT NOT NULL, _cell TEXT NOT NULL)
            */
            String sqlCode = "CREATE TABLE " + DATABASE_TABLE + " (" + KEY_NAME + " TEXT  PRIMARY KEY" + ")";
            db.execSQL(sqlCode);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }

    public datasqlite open() throws SQLException {
        this.ourHelper = new datasqlite.DBHelper(this.ourContext);
        this.ourDatabase = this.ourHelper.getWritableDatabase();
        //ourHelper.onUpgrade(ourDatabase,1,DATABASE_VERSION);
        return this;
    }
    public void create(){
        this.ourHelper = new datasqlite.DBHelper(this.ourContext);
        this.ourDatabase = this.ourHelper.getWritableDatabase();
        this.delete();
        this.ourDatabase.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        //this.close();
        ourHelper.onCreate(ourDatabase);
    }

    public void close(){
        this.ourHelper.close();
    }

    public long createEntry(String taskName){
        ContentValues cv = new ContentValues();
        cv.put(KEY_NAME,taskName);
        return ourDatabase.insert(DATABASE_TABLE,null,cv);
    }
    public void updateEntry(String id, String selected){
        ContentValues cv = new ContentValues();
        cv.put(KEY_NAME,selected);
        id = id.trim();
        String[] args = new String[]{id};
        ourDatabase.update(DATABASE_TABLE, cv, KEY_NAME + "=?", args);
    }
    public String getData(){
        String [] colomns = new String []{KEY_NAME};

        Cursor cursor = this.ourDatabase.query(DATABASE_TABLE, colomns,null,null,null,null,null);

        String result = "";

        int iName = cursor.getColumnIndex(KEY_NAME);

        for (cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
            result += cursor.getString(iName)+",";
        }
        cursor.close();

        return result;
    }

    public long deleteEntry(String rowId){
        return this.ourDatabase.delete(DATABASE_TABLE,KEY_NAME + "=?",new String[]{rowId});
    }

    public void delete(){
        ourDatabase.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
    }
}
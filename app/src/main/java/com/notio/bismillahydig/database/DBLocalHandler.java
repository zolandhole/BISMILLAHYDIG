package com.notio.bismillahydig.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.notio.bismillahydig.models.ModelUser;

import java.util.ArrayList;
import java.util.HashMap;

public class DBLocalHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ydig";
    private static final String TABLE_USER = "user";

    private static final String KEY_ID = "id";
    private static final String KEY_SUMBER_LOGIN = "sumber_login";
    private static final String KEY_ID_LOGIN = "id_login";
    private static final String KEY_PASSWORD = "password";

    public DBLocalHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_SUMBER_LOGIN + " TEXT, "
                + KEY_ID_LOGIN + " TEXT, "
                + KEY_PASSWORD + " TEXT)";
        db.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }


    public void addUser(ModelUser modelUser) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, modelUser.getId());
        values.put(KEY_SUMBER_LOGIN, modelUser.getSumber_login());
        values.put(KEY_ID_LOGIN, modelUser.getId_user());
        values.put(KEY_PASSWORD, modelUser.getPassword());
        db.insert(TABLE_USER, null, values);
        db.close();
    }

    public ArrayList<HashMap<String, String>> getUser(int i) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> userList = new ArrayList<>();
//        String query = "SELECT * FROM " + TABLE_USER;
        @SuppressLint("Recycle") Cursor cursor = db.query(TABLE_USER,
                new String[]{KEY_ID, KEY_SUMBER_LOGIN, KEY_ID_LOGIN, KEY_PASSWORD},
                KEY_ID + "=?",
                new String[]{String.valueOf(i)},
                null, null, null, null);

        if (cursor.moveToNext()){
            HashMap<String, String> user = new HashMap<>();
            user.put("id", cursor.getString(cursor.getColumnIndex(KEY_ID)));
            user.put("sumber_login", cursor.getString(cursor.getColumnIndex(KEY_SUMBER_LOGIN)));
            user.put("id_login", cursor.getString(cursor.getColumnIndex(KEY_ID_LOGIN)));
            user.put("password", cursor.getString(cursor.getColumnIndex(KEY_PASSWORD)));
            userList.add(user);
        }
        db.close();
        return userList;
    }

    public void deleteDB() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER, null, null);
        db.execSQL("DELETE FROM " + TABLE_USER);
        db.close();
    }
}

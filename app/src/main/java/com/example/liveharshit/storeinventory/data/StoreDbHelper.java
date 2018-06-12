package com.example.liveharshit.storeinventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.liveharshit.storeinventory.data.StoreContract.StoreEntry;

public class StoreDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "product.db";
    private static final int DATABASE_VERSION = 1;

    public StoreDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_PRODUCT_TABLE = "CREATE TABLE "+ StoreEntry.TABLE_NAME + "(" +
                StoreEntry._ID + "INTEGER PRIMARY KEY AUTOINCREMENT," +
                StoreEntry.COLUMN_PRODUCT_NAME + "TEXT NOT NULL," +
                StoreEntry.COLUMN_AVAILABLE_QUANTITY  + "INTEGER NOT NULL," +
                StoreEntry.COLUMN_PRODUCT_PRICE + "INTEGER NOT NULL," +
                StoreEntry.COLUMN_PRODUCT_CATEGORY + "INTEGER NOT NULL DEFAULT 0);";

        db.execSQL(SQL_CREATE_PRODUCT_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


    }
}

package com.example.liveharshit.storeinventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.CursorAdapter;

public class StoreProvider extends ContentProvider {

    private StoreDbHelper mDbHelper;
    private static final int PRODUCT = 100;
    private static final int PRODUCT_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(StoreContract.CONTENT_AUTHORITY, StoreContract.StoreEntry.TABLE_NAME,PRODUCT);
        sUriMatcher.addURI(StoreContract.CONTENT_AUTHORITY, StoreContract.StoreEntry.TABLE_NAME+"/#",PRODUCT_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new StoreDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase sqLiteDatabase = mDbHelper.getReadableDatabase();
        Cursor cursor = null;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                cursor = sqLiteDatabase.query(StoreContract.StoreEntry.TABLE_NAME,null,null,null,null,null,null);
                break;
            case PRODUCT_ID:
                String s = StoreContract.StoreEntry._ID + "=?";
                String [] selectionArg = {String.valueOf(ContentUris.parseId(uri))};
                cursor = sqLiteDatabase.query(StoreContract.StoreEntry.TABLE_NAME,null,s,selectionArg,null,null,null);
                break;
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int match = sUriMatcher.match(uri);
        long newRawId=0;
        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();
        switch (match) {
            case PRODUCT:
                newRawId = sqLiteDatabase.insert(StoreContract.StoreEntry.TABLE_NAME,null,values);
                break;
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId(uri,newRawId);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}

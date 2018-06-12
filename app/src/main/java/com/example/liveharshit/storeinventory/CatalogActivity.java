package com.example.liveharshit.storeinventory;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.liveharshit.storeinventory.data.StoreContract;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int PRODUCT_LOADER =1;
    private StoreCursorAdapter cursorAdapter;
    int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        if(Build.VERSION.SDK_INT>=23) {

            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // Explain to the user why we need to read the contacts
                }

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                // app-defined int constant that should be quite unique

            }
        }

        FloatingActionButton floatingActionButton = (FloatingActionButton)findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CatalogActivity.this,EditorActivity.class);
                startActivity(intent);
            }
        });
        ListView listView = (ListView)findViewById(R.id.list);
        cursorAdapter = new StoreCursorAdapter(this,null);
        listView.setAdapter(cursorAdapter);

        getLoaderManager().initLoader(PRODUCT_LOADER,null,this);

        /*String [] projection = {StoreContract.StoreEntry.COLUMN_PRODUCT_IMAGE};
        Cursor cursor = getContentResolver().query(StoreContract.StoreEntry.CONTENT_URI,projection,null,null,null);
        cursor.moveToFirst();
        byte[] imageBytes = cursor.getBlob(cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_PRODUCT_IMAGE));
        Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0,imageBytes.length);
        ImageView extraImageView = (ImageView)findViewById(R.id.extra_image_view);
        extraImageView.setImageBitmap(imageBitmap);*/
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String [] projection = {StoreContract.StoreEntry._ID,
                StoreContract.StoreEntry.COLUMN_PRODUCT_IMAGE,
                StoreContract.StoreEntry.COLUMN_PRODUCT_NAME,
                StoreContract.StoreEntry.COLUMN_AVAILABLE_QUANTITY,
                StoreContract.StoreEntry.COLUMN_PRODUCT_PRICE,
                StoreContract.StoreEntry.COLUMN_PRODUCT_CATEGORY};
        return new CursorLoader(this, StoreContract.StoreEntry.CONTENT_URI,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }
}

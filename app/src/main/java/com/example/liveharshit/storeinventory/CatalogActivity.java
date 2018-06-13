package com.example.liveharshit.storeinventory;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.liveharshit.storeinventory.data.StoreContract;

import java.io.ByteArrayOutputStream;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int PRODUCT_LOADER =0;
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


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.catalog_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data :
                insertDummyData();
                return true;
            case R.id.action_delete_all_data:
                getContentResolver().delete(StoreContract.StoreEntry.CONTENT_URI,null,null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertDummyData() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.sample_product);
        Bitmap reducedBitmap = getResizedBitmap(bitmap,512);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        reducedBitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        byte[] image = stream.toByteArray();
        String productName = "Sony MDR-ZX110 A Headphone  (White, Over the Ear)";
        String productPrice = "699";
        int quantity = 20;
        String category = "Headphone";

        ContentValues values = new ContentValues();
        values.put(StoreContract.StoreEntry.COLUMN_PRODUCT_IMAGE,image);
        values.put(StoreContract.StoreEntry.COLUMN_PRODUCT_NAME,productName);
        values.put(StoreContract.StoreEntry.COLUMN_PRODUCT_PRICE,productPrice);
        values.put(StoreContract.StoreEntry.COLUMN_AVAILABLE_QUANTITY,quantity);
        values.put(StoreContract.StoreEntry.COLUMN_PRODUCT_CATEGORY,category);

        getContentResolver().insert(StoreContract.StoreEntry.CONTENT_URI,values);

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

    private Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}

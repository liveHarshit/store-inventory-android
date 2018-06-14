package com.example.liveharshit.storeinventory;

import android.Manifest;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liveharshit.storeinventory.data.StoreContract;

import java.io.ByteArrayOutputStream;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static int RESULT_LOAD_IMAGE = 1;
    private ImageView addImageView;
    private int quantity=0;
    private TextView quantityTextView;
    private EditText nameEditText;
    private EditText priceEditText;
    private EditText categoryEditText;
    private Uri currentProductUri;
    private boolean hasProductChanged = false;

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            hasProductChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        currentProductUri = intent.getData();

        if (currentProductUri==null) {
            setTitle("Add a product");
        } else {

            setTitle("Edit this product");
            getLoaderManager().initLoader(0,null,this);
        }

        int REQUEST_CODE = 0;
        quantityTextView = (TextView) findViewById(R.id.quantity_text_view);
        addImageView = (ImageView)findViewById(R.id.add_image);
        addImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

         nameEditText = (EditText)findViewById(R.id.product_name);
         priceEditText = (EditText)findViewById(R.id.product_price);
         categoryEditText = (EditText)findViewById(R.id.product_category);

         addImageView.setOnTouchListener(touchListener);
         nameEditText.setOnTouchListener(touchListener);
         priceEditText.setOnTouchListener(touchListener);
         categoryEditText.setOnTouchListener(touchListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            ImageView imageView = (ImageView) findViewById(R.id.add_image);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }
    }

    //For increment and decrement available quantity of product
    public void increment(View view) {
        quantity++;
        display(quantity);
    }
    public void decrement(View view) {
        if (quantity>0) {
            quantity--;
        }
        else quantity=0;
        display(quantity);
    }
    private void display(int number) {
        quantityTextView.setText(Integer.toString(number));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                insertProduct();
                return true;
            case android.R.id.home:
                if(!hasProductChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                showDiscardAlertDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertProduct() {
        Bitmap imageBitmap = ((BitmapDrawable) addImageView.getDrawable()).getBitmap();
        Bitmap reducedBitmap = getResizedBitmap(imageBitmap,1024);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        reducedBitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        byte[] image = stream.toByteArray();

        String name = nameEditText.getText().toString().trim();
        String stringQuantity = quantityTextView.getText().toString().trim();
        String stringPrice = priceEditText.getText().toString().trim();
        String category = categoryEditText.getText().toString().trim();

        if(name.isEmpty()&&stringPrice.isEmpty()&&category.isEmpty()) {
            Toast.makeText(this, "Please enter all information...", Toast.LENGTH_SHORT).show();

        } else if (stringQuantity.equals("0")) {
            Toast.makeText(this, "Please increase quantity", Toast.LENGTH_SHORT).show();
        } else {

            int quantity = Integer.parseInt(stringQuantity);
            int price = Integer.parseInt(stringPrice);

            ContentValues values = new ContentValues();
            values.put(StoreContract.StoreEntry.COLUMN_PRODUCT_IMAGE, image);
            values.put(StoreContract.StoreEntry.COLUMN_PRODUCT_NAME, name);
            values.put(StoreContract.StoreEntry.COLUMN_AVAILABLE_QUANTITY, quantity);
            values.put(StoreContract.StoreEntry.COLUMN_PRODUCT_PRICE, price);
            values.put(StoreContract.StoreEntry.COLUMN_PRODUCT_CATEGORY, category);

            if (currentProductUri == null) {

                getContentResolver().insert(StoreContract.StoreEntry.CONTENT_URI, values);
            } else {
                getContentResolver().update(currentProductUri, values, null, null);
            }
            finish();
        }

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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String [] project = {StoreContract.StoreEntry._ID, StoreContract.StoreEntry.COLUMN_PRODUCT_IMAGE, StoreContract.StoreEntry.COLUMN_PRODUCT_NAME,  StoreContract.StoreEntry.COLUMN_AVAILABLE_QUANTITY,StoreContract.StoreEntry.COLUMN_PRODUCT_PRICE, StoreContract.StoreEntry.COLUMN_PRODUCT_CATEGORY};
        return new CursorLoader(this,currentProductUri,project,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.moveToFirst()) {
            byte[] imageBytes = data.getBlob(data.getColumnIndex(StoreContract.StoreEntry.COLUMN_PRODUCT_IMAGE));
            Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            String name = data.getString(data.getColumnIndex(StoreContract.StoreEntry.COLUMN_PRODUCT_NAME));
            quantity = data.getInt(data.getColumnIndex(StoreContract.StoreEntry.COLUMN_AVAILABLE_QUANTITY));
            String quantityString = Integer.toString(quantity);
            int price = data.getInt(data.getColumnIndex(StoreContract.StoreEntry.COLUMN_PRODUCT_PRICE));
            String priceString = Integer.toString(price);
            String category = data.getString(data.getColumnIndex(StoreContract.StoreEntry.COLUMN_PRODUCT_CATEGORY));


            addImageView.setImageBitmap(imageBitmap);
            nameEditText.setText(name);
            quantityTextView.setText(quantityString);
            priceEditText.setText(priceString);
            categoryEditText.setText(category);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        addImageView.setImageBitmap(null);
        nameEditText.setText("");
        quantityTextView.setText("");
        priceEditText.setText("");
        priceEditText.setText("");
        categoryEditText.setText("");
    }

    @Override
    public void onBackPressed() {
        if (!hasProductChanged) {
            super.onBackPressed();
        }
        showDiscardAlertDialog();
    }

    private void showDiscardAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.exit_without_saving);
        builder.setNegativeButton(R.string.discard_changes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setPositiveButton(R.string.stay_here, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}

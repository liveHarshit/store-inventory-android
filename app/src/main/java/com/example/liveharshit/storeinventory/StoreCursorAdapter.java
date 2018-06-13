package com.example.liveharshit.storeinventory;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liveharshit.storeinventory.data.StoreContract;

import java.io.ByteArrayOutputStream;
import java.util.zip.Inflater;

import static android.support.v4.app.ActivityCompat.requestPermissions;
import static android.support.v4.app.ActivityCompat.shouldShowRequestPermissionRationale;
import static android.support.v4.content.ContextCompat.checkSelfPermission;

public class StoreCursorAdapter extends CursorAdapter {

    public StoreCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        ImageView imageView = (ImageView)view.findViewById(R.id.list_item_image_view);
        TextView nameTextView = (TextView)view.findViewById(R.id.name_text_view);
        TextView quantityTextView = (TextView)view.findViewById(R.id.quantity_text_view);
        TextView priceTextView = (TextView)view.findViewById(R.id.price_text_view);
        TextView categoryTextView = (TextView)view.findViewById(R.id.category_text_view);

        byte[] imageBytes = cursor.getBlob(cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_PRODUCT_IMAGE));
        final Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0,imageBytes.length);
        imageView.setImageBitmap(imageBitmap);

        final String productName = cursor.getString(cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_PRODUCT_NAME));
        nameTextView.setText(productName);

        String availableQuantity = cursor.getString(cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_AVAILABLE_QUANTITY));
        quantityTextView.setText(availableQuantity);

        String price = cursor.getString(cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_PRODUCT_PRICE));
        price ="₹" +  price;
        priceTextView.setText(price);

        final String category = cursor.getString(cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_PRODUCT_CATEGORY));
        categoryTextView.setText(category);

        int currentItemId = cursor.getInt(cursor.getColumnIndex(StoreContract.StoreEntry._ID));
        final String id = Integer.toString(currentItemId);
        final Uri currentProductUri = Uri.withAppendedPath(StoreContract.StoreEntry.CONTENT_URI,id);

        ImageView deleteImageView = (ImageView)view.findViewById(R.id.delete_product);
        deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(R.string.delete_this_item);
                builder.setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        context.getContentResolver().delete(currentProductUri,null,null);
                    }
                });
                builder.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(dialog!=null) {
                            dialog.dismiss();
                        }
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        ImageView editImageView = (ImageView) view.findViewById(R.id.edit_product);
        editImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,EditorActivity.class);
                intent.setData(currentProductUri);
                context.startActivity(intent);
            }
        });

        final String SEND_TEXT = productName + "\n" + "Price: " + price + "\n" + "Available quantity: " + availableQuantity + "\nType: " + category;


        ImageView shareImageView = (ImageView)view.findViewById(R.id.share_product);
        shareImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri imageUri = getImageUri(context,imageBitmap);
                Log.e("Image uri",imageUri.toString());
                Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_TEXT, SEND_TEXT);
                intent.putExtra(Intent.EXTRA_STREAM, imageUri);
                context.startActivity(intent);
            }
        });
    }

    private Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}

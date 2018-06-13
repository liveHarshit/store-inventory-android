package com.example.liveharshit.storeinventory;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liveharshit.storeinventory.data.StoreContract;

import java.util.zip.Inflater;

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
        Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0,imageBytes.length);
        imageView.setImageBitmap(imageBitmap);

        String productName = cursor.getString(cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_PRODUCT_NAME));
        nameTextView.setText(productName);

        String availableQuantity = cursor.getString(cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_AVAILABLE_QUANTITY));
        quantityTextView.setText(availableQuantity);

        String price = cursor.getString(cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_PRODUCT_PRICE));
        price ="₹" +  price;
        priceTextView.setText(price);

        String category = cursor.getString(cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_PRODUCT_CATEGORY));
        categoryTextView.setText(category);

        int currentItemId = cursor.getInt(cursor.getColumnIndex(StoreContract.StoreEntry._ID));
        String id = Integer.toString(currentItemId);
        final Uri currentProducturi = Uri.withAppendedPath(StoreContract.StoreEntry.CONTENT_URI,id);

        ImageView deleteImageView = (ImageView)view.findViewById(R.id.delete_product);
        deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Currend product uri",currentProducturi.toString());
                Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show();
                context.getContentResolver().delete(currentProducturi,null,null);
            }
        });
    }
}

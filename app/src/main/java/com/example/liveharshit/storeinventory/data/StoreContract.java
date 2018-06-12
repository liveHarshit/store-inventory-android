package com.example.liveharshit.storeinventory.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class StoreContract {
    public static final String CONTENT_AUTHORITY = "com.example.liveharshit.storeinventory";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private StoreContract () {}
    public static final class StoreEntry implements BaseColumns {
        public static final String TABLE_NAME = "products";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_IMAGE = "image";
        public static final String COLUMN_PRODUCT_NAME = "name";
        public static final String COLUMN_AVAILABLE_QUANTITY = "quantity";
        public static final String COLUMN_PRODUCT_PRICE = "price";
        public static final String COLUMN_PRODUCT_CATEGORY = "category";
        public static final Uri CONTENT_URI  = Uri.withAppendedPath(BASE_CONTENT_URI,TABLE_NAME);
    }
}

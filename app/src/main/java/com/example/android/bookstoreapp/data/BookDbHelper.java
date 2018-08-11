package com.example.android.bookstoreapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.bookstoreapp.data.BookContract.BookEntry;

/**
 * Created by mekaelkoreshi on 6.08.2018.
 */

public class BookDbHelper extends SQLiteOpenHelper {

    // Name of the table
    public static final String DATABASE_NAME = "inventory.db";

    // Version of the table, increment by one if upgraded.
    public static final int DATABASE_VERSION = 1;

    // Public constructor
    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_BOOKS_TABLE = "CREATE TABLE " + BookEntry.TABLE_NAME + "("
                + BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BookEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + BookEntry.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL, "
                + BookEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL, "
                + BookEntry.COLUMN_SUPPLIER_NAME + " TEXT, "
                + BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER + " INTEGER);";

        db.execSQL(SQL_CREATE_BOOKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

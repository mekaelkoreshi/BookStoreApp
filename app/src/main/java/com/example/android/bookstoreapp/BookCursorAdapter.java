package com.example.android.bookstoreapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookstoreapp.data.BookContract;
import com.example.android.bookstoreapp.data.BookContract.BookEntry;
import com.example.android.bookstoreapp.data.BookDbHelper;

import java.util.zip.Inflater;

/**
 * Created by mekaelkoreshi on 11.08.2018.
 */

public class BookCursorAdapter extends CursorAdapter {

    // Database helper object.
    private BookDbHelper mDbHelper;

    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find the fields that in the inflated view that are to be populated
        TextView nameView = (TextView) view.findViewById(R.id.book_name_text_view);
        TextView priceView = (TextView) view.findViewById(R.id.price_text_view);
        TextView quantityView = (TextView) view.findViewById(R.id.quantity_text_view);

        // Extract text from cursor
        String name = cursor.getString(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_NAME));
        String price = cursor.getString(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_PRICE));
        final String quantity = cursor.getString(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_QUANTITY));

        // Set cursor data on text views.
        nameView.setText(name);
        priceView.setText(price);
        quantityView.setText(quantity);

        // Set up the sale button
//        Button saleButton = view.findViewById(R.id.button_sale);
//        saleButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int quantityInt = Integer.parseInt(quantity);
//                sale(quantityInt);
//            }
//        });
    }

//    private void sale(int quantity) {
//        SQLiteDatabase database = mDbHelper.getWritableDatabase();
//
//        if (quantity > 0){
//            quantity -= 1;
//            ContentValues values = new ContentValues();
//            values.put(BookEntry.COLUMN_PRODUCT_QUANTITY, quantity);
//
//            database.update(BookEntry.TABLE_NAME, );
//        }
//    }

}
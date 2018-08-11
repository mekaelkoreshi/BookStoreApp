package com.example.android.bookstoreapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.bookstoreapp.data.BookContract.BookEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int BOOK_LOADER = 0;

    private BookCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the Floating Action Button to take the user to the Add activity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addIntent = new Intent(MainActivity.this, AddEditActivity.class);
                startActivity(addIntent);
            }
        });

        // Find the ListView which will be populated with the pet data
        ListView listView = (ListView) findViewById(R.id.list_view);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        // Set up an adapter and attach it to the list view.
        mCursorAdapter = new BookCursorAdapter(this, null);
        listView.setAdapter(mCursorAdapter);

        // Set Up on Click Listener for the list view
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Create a new intent to go to the Add Edit Activity
                Intent editIntent = new Intent(MainActivity.this, AddEditActivity.class);

                // Form the content URI that represents the specific book that was clicked on,
                // by appending the "id" (passed as input to this method) onto the BookEntry Content URI,
                Uri currentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                Log.e("Main Activity", currentBookUri.toString());

                // Set the URI on the data field of the intent
                editIntent.setData(currentBookUri);

                // Launch the Add Edit Activity to display the data for the current book.
                startActivity(editIntent);
            }
        });

        //Kick off loader
        getLoaderManager().initLoader(BOOK_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu from main.xml
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Delete All" menu option
            case R.id.action_delete_all:
                showDeleteAllConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteAllConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete all the books in the database? (This can not be undone)");
        builder.setPositiveButton("Delete All", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the book.
                deleteAllBooks();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteAllBooks() {
        getContentResolver().delete(BookEntry.CONTENT_URI, null, null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns we care about
        String[] projection = {BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRODUCT_PRICE,
                BookEntry.COLUMN_PRODUCT_QUANTITY};

        return new CursorLoader(this,
                BookEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // update pet cursor adapter with new cursor containing updated pet data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when data needs to be reset
        mCursorAdapter.swapCursor(null);
    }

}

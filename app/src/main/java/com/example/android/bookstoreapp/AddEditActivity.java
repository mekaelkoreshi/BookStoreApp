package com.example.android.bookstoreapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.Touch;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookstoreapp.data.BookContract.BookEntry;

import java.util.Set;

public class AddEditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_BOOK_LOADER = 0;
    private Uri mCurrentBookUri = null;

    private boolean mBookHasChanged = false;

    /**
     * EditText field to enter the book's name
     */
    private EditText mNameEditText;

    /**
     * EditText field to enter the book's price
     */
    private EditText mPriceEditText;

    /**
     * Text field to enter the book's quantity
     */
    private TextView mQuantityTextView;

    // Buttons to update mQuantityTextView
    private Button mQuantityIncrement;
    private Button mQuantityDecrement;

    /**
     * EditText field to enter the supplier's name
     */
    private EditText mSupplierNameEditText;

    /**
     * EditText field to enter the supplier's phone number
     */
    private EditText mSupplierNumberEditText;

    // variable for the quantity
    public int quantity = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        // button for ordering from supplier
        Button order = findViewById(R.id.button_order);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new book or editing an existing one.
        Intent intent = getIntent();
        Uri currentBookUri = intent.getData();
        if (currentBookUri != null) {
            mCurrentBookUri = currentBookUri;
        }

        // If the intent DOES NOT contain a book content URI, then we know that we are
        // creating a new book.
        if (currentBookUri == null) {
            // This is a new pet, so change the app bar to say "Add a Book"
            setTitle("Add a Book");

            // Hide order button if adding a new book
            order.setVisibility(View.GONE);
        } else {
            // Otherwise this is an existing book, so change the app bar to say "Edit Book"
            setTitle("Edit Book");
        }

        // Invalidate the options menu, so the "Delete" menu option can be hidden.
        // (It doesn't make sense to delete a book that hasn't been created yet.)
        invalidateOptionsMenu();

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_product_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_product_price);
        mSupplierNameEditText = (EditText) findViewById(R.id.edit_supplier_name);
        mSupplierNumberEditText = (EditText) findViewById(R.id.edit_supplier_number);

        mQuantityTextView = (TextView) findViewById(R.id.edit_product_quantity);
        mQuantityIncrement = findViewById(R.id.edit_product_quantity_increment);
        mQuantityDecrement = findViewById(R.id.edit_product_quantity_decrement);

        // Set on Touch Listener to know that the field has changed.
        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierNumberEditText.setOnTouchListener(mTouchListener);
        mQuantityIncrement.setOnTouchListener(mTouchListener);
        mQuantityDecrement.setOnTouchListener(mTouchListener);

            //Kick off loader
        getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu resource from add_edit.xml
        getMenuInflater().inflate(R.menu.add_edit, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new book, hide the "Delete" menu item.
        if (mCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                saveBook();
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(AddEditActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(AddEditActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveBook() {
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityTextView.getText().toString();
        String supplierNameString = mSupplierNameEditText.getText().toString().trim();
        String supplierNumberString = mSupplierNumberEditText.getText().toString().trim();

        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(BookEntry.COLUMN_PRODUCT_PRICE, priceString);
        values.put(BookEntry.COLUMN_PRODUCT_QUANTITY, quantityString);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierNumberString);

        if (mCurrentBookUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(supplierNameString) && TextUtils.isEmpty(supplierNumberString) &&
                quantity == 0) {
            return;
        }

        if (mCurrentBookUri == null) {
            Uri returnedUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

            if (returnedUri == null) {
                Toast toast = Toast.makeText(this, "Error with saving book.", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                Toast toast = Toast.makeText(this, "Book saved.", Toast.LENGTH_SHORT);
                toast.show();
            }
        } else {
            // Otherwise this is an EXISTING book, so update the book with content URI: mCurrentBookUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentBookUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast toast = Toast.makeText(this, "Updating book failed.", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast toast = Toast.makeText(this, "Updating book successful.", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete the current book?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the book.
                deleteBook();
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

    private void deleteBook() {
        // Only perform the delete if this is an existing book.
        if (mCurrentBookUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast toast = Toast.makeText(this, "Deleting the book failed.", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast toast = Toast.makeText(this, "Delting the book was successful.", Toast.LENGTH_SHORT);
                toast.show();
            }

            // Close the activity
            finish();
        }
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit without saving any changes?");
        builder.setPositiveButton("Discard Changes", discardButtonClickListener);
        builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
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

    @Override
    public void onBackPressed() {
        // If the book hasn't changed, continue with handling back button press
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    // OnTouchListener that listens for any user touches on a View, implying that they are modifying
    // the view, and we change the mPetHasChanged boolean to true.
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };

    public void changeQuantityIncrement(View v) {
        quantity += 1;
        mQuantityTextView.setText(Integer.toString(quantity));
    }

    public void changeQuantityDecrement(View v) {
        if (quantity > 0){
            quantity -= 1;
            mQuantityTextView.setText(Integer.toString(quantity));
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies the columns we care about
        if (mCurrentBookUri != null) {
            String[] projection = {BookEntry._ID,
                    BookEntry.COLUMN_PRODUCT_NAME,
                    BookEntry.COLUMN_PRODUCT_PRICE,
                    BookEntry.COLUMN_PRODUCT_QUANTITY,
                    BookEntry.COLUMN_SUPPLIER_NAME,
                    BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER};

            return new CursorLoader(this,
                    mCurrentBookUri,
                    projection,
                    null,
                    null,
                    null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
            int supplierNumberColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantityItem = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            int supplierNumber = cursor.getInt(supplierNumberColumnIndex);

            quantity = quantityItem;

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mPriceEditText.setText(Integer.toString(price));
            mQuantityTextView.setText(Integer.toString(quantityItem));
            mSupplierNameEditText.setText(supplierName);
            mSupplierNumberEditText.setText(Integer.toString(supplierNumber));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
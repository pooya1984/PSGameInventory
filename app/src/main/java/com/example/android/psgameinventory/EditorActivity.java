package com.example.android.psgameinventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.example.android.psgameinventory.data.GameContract.GameEntry;


public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_GAME_LOADER = 0;

    /** Content URI for the existing game (null if it's a new game) */
    private Uri mCurrentGAMEUri;

    /** EditText field to enter the game's name */
    private EditText mNameEditText;

    /** EditText field to enter the game's console */
    private Spinner mConsoleSpinner;

    /** EditText field to enter the game's quantity */
    private EditText mQuantityEditText;

    /** EditText field to enter the game's genre */
    private Spinner mGenreSpinner;

    private int mConsole = GameEntry.CONSOLE_UNKNOWN;

    private int mGenre = GameEntry.GENRE_UNKNOWN;

    /** Boolean flag that keeps track of whether the game has been edited (true) or not (false) */
    private boolean mGameHasChanged = false;


    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mGameHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentGAMEUri = intent.getData();

        if (mCurrentGAMEUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_game));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_game));
            getLoaderManager().initLoader(EXISTING_GAME_LOADER, null, this);
        }

        mNameEditText = (EditText) findViewById(R.id.edit_game_name);
        mConsoleSpinner = (Spinner) findViewById(R.id.spinner_console);
        mQuantityEditText = (EditText) findViewById(R.id.edit_game_quantity);
        mGenreSpinner = (Spinner) findViewById(R.id.spinner_genre);


        mNameEditText.setOnTouchListener(mTouchListener);
        mConsoleSpinner.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mGenreSpinner.setOnTouchListener(mTouchListener);

        setupSpinner();
    }


    private void setupSpinner() {
        ArrayAdapter genreSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_genre_options, android.R.layout.simple_spinner_item);
        ArrayAdapter consoleSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_genre_options, android.R.layout.simple_spinner_item);

        genreSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        consoleSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);


        mGenreSpinner.setAdapter(genreSpinnerAdapter);
        mGenreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.SCI_FI))) {
                        mGenre = GameEntry.GENRE_SCI_FI;
                    } else if (selection.equals(getString(R.string.ACTION))) {
                        mGenre = GameEntry.GENRE_ACTION;
                    } else if (selection.equals(getString(R.string.SPORT))) {
                        mGenre = GameEntry.GENRE_SPORT;
                    } else if (selection.equals(getString(R.string.ADVENTURE))) {
                        mGenre = GameEntry.GENRE_ADVENTURE;
                    } else if (selection.equals(getString(R.string.SHOOTING))) {
                        mGenre = GameEntry.GENRE_SHOOTING;
                    } else {
                        mGenre = GameEntry.GENRE_UNKNOWN;
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGenre = GameEntry.GENRE_UNKNOWN;
            }
        });

        mConsoleSpinner.setAdapter(consoleSpinnerAdapter);
        mConsoleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.PS))) {
                        mConsole = GameEntry.CONSOLE_PS;
                    } else if (selection.equals(getString(R.string.PS1))) {
                        mConsole = GameEntry.CONSOLE_PS1;
                    } else if (selection.equals(getString(R.string.PS2))) {
                        mConsole = GameEntry.CONSOLE_PS2;
                    } else if (selection.equals(getString(R.string.PS3))) {
                        mConsole = GameEntry.CONSOLE_PS3;
                    } else if (selection.equals(getString(R.string.PS4))) {
                        mConsole = GameEntry.CONSOLE_PS4;
                    } else {
                        mConsole = GameEntry.CONSOLE_UNKNOWN;
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mConsole = GameEntry.CONSOLE_UNKNOWN;
            }
        });
    }


    private void savePet() {
        String nameString = mNameEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();

        if (mCurrentGAMEUri == null &&
                TextUtils.isEmpty(nameString) && mGenre == GameEntry.GENRE_UNKNOWN &&
                TextUtils.isEmpty(quantityString) && mConsole == GameEntry.CONSOLE_UNKNOWN) {
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(GameEntry.COLUMN_GAME_NAME, nameString);
        values.put(GameEntry.COLUMN_GAME_GENRE, mGenre);
        values.put(GameEntry.COLUMN_GAME_CONSOLE, mConsole);
        // If the weight is not provided by the user, don't try to parse the string into an
        // integer value. Use 0 by default.
        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }
        values.put(GameEntry.COLUMN_GAME_STOCK, quantity);

        // Determine if this is a new or existing pet by checking if mCurrentGAMEUri is null or not
        if (mCurrentGAMEUri == null) {
            Uri newUri = getContentResolver().insert(GameEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_game_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_game_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentGAMEUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_game_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_game_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (mCurrentGAMEUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveGame();
                finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:

                if (!mGameHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;}
        return super.onOptionsItemSelected(item);}


    @Override
    public void onBackPressed() {
        if (!mGameHasChanged) {
            super.onBackPressed();
            return;}

    DialogInterface.OnClickListener discardButtonClickListener =
     new DialogInterface.OnClickListener() {
       @Override
       public void onClick(DialogInterface dialogInterface, int i) {finish();}};
        showUnsavedChangesDialog(discardButtonClickListener);}

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                GameEntry._ID,
                GameEntry.COLUMN_GAME_NAME,
                GameEntry.COLUMN_GAME_GENRE,
                GameEntry.COLUMN_GAME_CONSOLE,
                GameEntry.COLUMN_GAME_STOCK };

        return new CursorLoader(this,   // Parent activity context
                mCurrentGAMEUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {

            int nameColumnIndex = cursor.getColumnIndex(GameEntry.COLUMN_GAME_NAME);
            int genreColumnIndex = cursor.getColumnIndex(GameEntry.COLUMN_GAME_GENRE);
            int consoleColumnIndex = cursor.getColumnIndex(GameEntry.COLUMN_GAME_CONSOLE);
            int quantityColumnIndex = cursor.getColumnIndex(GameEntry.COLUMN_GAME_STOCK);

            String name = cursor.getString(nameColumnIndex);
            int genre = cursor.getInt(genreColumnIndex);
            int console = cursor.getInt(consoleColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mQuantityEditText.setText(Integer.toString(quantity));

            // Gender is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options (0 is Unknown, 1 is Male, 2 is Female).
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (console) {
                case GameEntry.CONSOLE_PS:
                    mConsoleSpinner.setSelection(1);
                    break;
                case GameEntry.CONSOLE_PS1:
                    mConsoleSpinner.setSelection(2);
                    break;
                case GameEntry.CONSOLE_PS2:
                    mConsoleSpinner.setSelection(3);
                case GameEntry.CONSOLE_PS3:
                    mConsoleSpinner.setSelection(4);
                    break;
                case GameEntry.CONSOLE_PS4:
                    mConsoleSpinner.setSelection(5);
                default:
                    mConsoleSpinner.setSelection(0);
                    break;
            }
            switch (genre) {
                case GameEntry.GENRE_SCI_FI:
                    mGenreSpinner.setSelection(1);
                    break;
                case GameEntry.GENRE_ACTION:
                    mGenreSpinner.setSelection(2);
                    break;
                case GameEntry.GENRE_SPORT:
                    mGenreSpinner.setSelection(3);
                case GameEntry.GENRE_ADVENTURE:
                    mGenreSpinner.setSelection(4);
                    break;
                case GameEntry.GENRE_SHOOTING:
                    mGenreSpinner.setSelection(5);
                default:
                    mGenreSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mNameEditText.setText("");
        mGenreSpinner.setSelection(0); //select "unknown" genre
        mQuantityEditText.setText("");
        mConsoleSpinner.setSelection(0); // Select "Unknown" console
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteGame();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void deleteGame() {
        if (mCurrentGAMEUri != null) {

            int rowsDeleted = getContentResolver().delete(mCurrentGAMEUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_game_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_game_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

}

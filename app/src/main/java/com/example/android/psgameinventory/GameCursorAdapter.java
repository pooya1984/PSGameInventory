package com.example.android.psgameinventory;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.psgameinventory.data.GameContract.GameEntry;

public class GameCursorAdapter extends CursorAdapter {

    public GameCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView summaryTextView = (TextView) view.findViewById(R.id.summary);
        TextView genreTextView = (TextView) view.findViewById(R.id.genre);
        TextView consoleTextView= (TextView)view.findViewById(R.id.console);


        // Find the columns of pet attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(GameEntry.COLUMN_GAME_NAME);
        int gameColumnIndex = cursor.getColumnIndex(GameEntry.COLUMN_GAME_STOCK);
        int genreColumnIndex=cursor.getColumnIndex(GameEntry.COLUMN_GAME_GENRE);
        int consoleColumnIndex=cursor.getColumnIndex(GameEntry.COLUMN_GAME_CONSOLE);

        // Read the pet attributes from the Cursor for the current pet
        String gameName = cursor.getString(nameColumnIndex);
        String gameStock = cursor.getString(gameColumnIndex);
        String gameGenre = cursor.getString(genreColumnIndex);
        String gameConsole = cursor.getString(consoleColumnIndex);

        if (TextUtils.isEmpty(gameStock)) {
            gameStock = context.getString(R.string.unknown_Price);
        }
        // Update the TextViews with the attributes for the current pet
        nameTextView.setText(gameName);
        summaryTextView.setText(gameStock);
        genreTextView.setText(gameGenre);
        consoleTextView.setText(gameConsole);
    }}
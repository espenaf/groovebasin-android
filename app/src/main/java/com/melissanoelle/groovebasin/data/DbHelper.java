package com.melissanoelle.groovebasin.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.melissanoelle.groovebasin.data.DbContract.GroovebasinEntry;

/**
 * Created by mel on 2/21/15.
 */
public class DbHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = DbHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "groovebasin.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_GROOVEBASINS_TABLE = "CREATE TABLE " + GroovebasinEntry.TABLE_NAME + " (" +
                GroovebasinEntry._ID + " INTEGER PRIMARY KEY, " +
                GroovebasinEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                GroovebasinEntry.COLUMN_URL + " TEXT NOT NULL," +
                "UNIQUE (" + GroovebasinEntry.COLUMN_URL + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_GROOVEBASINS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + GroovebasinEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}

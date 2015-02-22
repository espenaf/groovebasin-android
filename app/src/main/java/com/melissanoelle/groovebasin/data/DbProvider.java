package com.melissanoelle.groovebasin.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.melissanoelle.groovebasin.data.DbContract.GroovebasinEntry;

import java.text.ParseException;

/**
 * Created by mel on 2/21/15.
 */
public class DbProvider extends ContentProvider {
    public static final String LOG_TAG = DbProvider.class.getSimpleName();

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DbHelper mDbHelper;

    // URI codes
    private static final int GROOVEBASIN = 100;

    private static final SQLiteQueryBuilder sGroovebasinsQueryBuilder;

    static {
        sGroovebasinsQueryBuilder = new SQLiteQueryBuilder();
        sGroovebasinsQueryBuilder.setTables(GroovebasinEntry.TABLE_NAME);
    }

    private Cursor getGroovebasins(Uri uri, String[] projection, String sortOrder) {
        return sGroovebasinsQueryBuilder.query(mDbHelper.getReadableDatabase(),
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );
    }

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DbContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, DbContract.PATH_GROOVEBASIN, GROOVEBASIN);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new DbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor returnCursor;

        switch (sUriMatcher.match(uri)) {
            case GROOVEBASIN: {
                returnCursor = getGroovebasins(uri, projection, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknonw URI: " + uri);
        }

        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return returnCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case GROOVEBASIN: {
                long _id = db.insert(GroovebasinEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = GroovebasinEntry.buildGroovebasinUri(_id);
                else
                    throw new SQLException("Failed to insert row into " + uri + ".");
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        switch (match) {
            case GROOVEBASIN:
                rowsDeleted = db.delete(GroovebasinEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case GROOVEBASIN: {
                rowsUpdated = db.update(GroovebasinEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        db.beginTransaction();
        int returnCount = 0;

        switch (match) {
            case GROOVEBASIN: {
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(GroovebasinEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case GROOVEBASIN:
                return GroovebasinEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
    }
}

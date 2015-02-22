package com.melissanoelle.groovebasin.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by mel on 2/21/15.
 */
public class DbContract {

    public static final String CONTENT_AUTHORITY = "com.melissanoelle.groovebasin";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_GROOVEBASIN = "groovebasin";

    public static final class GroovebasinEntry implements BaseColumns {
        public static final String TABLE_NAME = "groovebasins";

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_URL = "url";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_GROOVEBASIN).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_GROOVEBASIN;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_GROOVEBASIN;

        public static Uri buildGroovebasinUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri returnContentUri() { return CONTENT_URI; }
    }
}

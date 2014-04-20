package com.celsa.globetrotterpacklist.persistance;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import roboguice.content.RoboContentProvider;

import java.util.ArrayList;

public class ItemContentProvider extends RoboContentProvider {

    public static final String AUTHORITY = "com.celsa.globetrotterpacklist.persistance.ItemContentProvider";
    public static final Uri ITEMS = Uri.parse("content://" + AUTHORITY + "/items");

    private static final int ITEMS_ALL = 1;

    private DatabaseHandler dbHandler;

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "items", ITEMS_ALL);
    }

    @Override
    public boolean onCreate() {
        dbHandler = new DatabaseHandler(getContext());

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        qb.setTables("item");

        Cursor cursor = qb.query(dbHandler.getReadableDatabase(), projection, selection, selectionArgs,
                null, null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        Uri insertedId = null;

        long id = db.insert("item", null, values);
        if (id > -1) {
            insertedId = ContentUris.withAppendedId(uri, id);
            getContext().getContentResolver().notifyChange(insertedId, null);
        }

        return insertedId;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHandler.getWritableDatabase();

        int numRowsDeleted = db.delete("item", selection, selectionArgs);
        if (numRowsDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHandler.getWritableDatabase();

        int numRowsUpdated = db.update("item", values, selection, selectionArgs);
        if (numRowsUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsUpdated;
    }

    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        ContentProviderResult[] result = new ContentProviderResult[operations.size()];
        SQLiteDatabase db = dbHandler.getWritableDatabase();

        db.beginTransaction();
        try {
            int i = 0;

            for (ContentProviderOperation operation : operations) {
                result[i++] = operation.apply(this, result, i);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        return result;
    }
}
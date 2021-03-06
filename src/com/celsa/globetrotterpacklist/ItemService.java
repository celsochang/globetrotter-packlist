package com.celsa.globetrotterpacklist;

import android.content.*;
import android.database.Cursor;
import android.os.RemoteException;
import android.provider.ContactsContract;
import com.celsa.globetrotterpacklist.entities.Item;
import com.celsa.globetrotterpacklist.persistance.ItemContentProvider;
import com.google.inject.Inject;

import java.util.ArrayList;

public class ItemService {

    @Inject
    private Context context;

    @Inject
    ContentResolver cr;

    public Item getItem(long id) {
        Cursor cursor = cr.query(ItemContentProvider.ITEMS, new String[] {"_id", "name", "image", "status", "\"order\""},
                "_id = ?", new String[] {String.valueOf(id)}, null);

        Item item = null;

        try {
            cursor.moveToFirst();
            item = new Item(
                    cursor.getLong(cursor.getColumnIndex("_id")),
                    cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getBlob(cursor.getColumnIndex("image")),
                    cursor.getInt(cursor.getColumnIndex("status")),
                    cursor.getInt(cursor.getColumnIndex("order")));
        } finally {
            cursor.close();
        }

        return item;
    }

    public byte[] getItemImage(long id) {
        Cursor cursor = cr.query(ItemContentProvider.ITEMS, new String[] {"image"}, "_id = ?",
                new String[] {String.valueOf(id)}, null);

        byte[] image = null;

        try {
            cursor.moveToFirst();
            image = cursor.getBlob(cursor.getColumnIndex("image"));
        } finally {
            cursor.close();
        }

        return image;
    }

    public int getItemMaxOrder() {
        Cursor cursor = cr.query(ItemContentProvider.ITEMS, new String[] {"max(\"order\")"}, null, null, null);

        try {
            cursor.moveToFirst();
            return cursor.getInt(0);
        } finally {
            cursor.close();
        }
    }

    public int checkItem(long id) {
        ContentValues cv = new ContentValues();

        cv.put("status", 1);

        return cr.update(ItemContentProvider.ITEMS, cv, "_id = ?", new String[] {String.valueOf(id)});
    }

    public int uncheckItem(long id) {
        ContentValues cv = new ContentValues();

        cv.put("status", 0);

        return cr.update(ItemContentProvider.ITEMS, cv, "_id = ?", new String[] {String.valueOf(id)});
    }

    public int uncheckAllItems() {
        ContentValues cv = new ContentValues();
        cv.put("status", 0);

        return cr.update(ItemContentProvider.ITEMS, cv, null, null);
    }

    public int updateItemsOrder(long[] orderedItemIds) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        for (int i = 0; i < orderedItemIds.length; i++) {
            ops.add(ContentProviderOperation.newUpdate(ItemContentProvider.ITEMS)
                    .withSelection("_id = ?", new String[] {String.valueOf(orderedItemIds[i])})
                    .withValue("\"order\"", i+1).build());
        }

        try {
            cr.applyBatch(ItemContentProvider.AUTHORITY, ops);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }

        return 0;
    }
}

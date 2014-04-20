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
        Cursor result = cr.query(ItemContentProvider.ITEMS,
                new String[] {"_id", "name", "photo_id"},"_id = ?", new String[] {String.valueOf(id)}, null);

        Item item = null;

        try {
            result.moveToFirst();
            item = new Item(
                    result.getLong(result.getColumnIndex("_id")),
                    result.getString(result.getColumnIndex("name")),
                    result.getString(result.getColumnIndex("photo_id")));
        } finally {
            result.close();
        }

        return item;
    }

    public long getItemMaxOrder() {
        Cursor result = cr.query(ItemContentProvider.ITEMS, new String[] {"max(\"order\")"}, null, null, null);

        try {
            result.moveToFirst();
            return result.getInt(0);
        } finally {
            result.close();
        }
    }

    public int toggleItemStatus(long id, int currentStatus) {
        ContentValues cv = new ContentValues();

        int newStatus = currentStatus == 0 ? 1 : 0;
        cv.put("status", newStatus);
        cr.update(ItemContentProvider.ITEMS, cv, "_id = ?", new String[] {String.valueOf(id)});

        return newStatus;
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

package com.celsa.globetrotterpacklist;

import android.app.ActionBar;
import android.content.ContentResolver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.*;
import android.widget.*;
import com.celsa.globetrotterpacklist.dialog.*;
import com.celsa.globetrotterpacklist.dslv.DragSortListView;
import com.celsa.globetrotterpacklist.persistance.ItemContentProvider;
import com.celsa.globetrotterpacklist.persistance.ItemCursorAdapter;
import com.google.inject.Inject;
import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.items)
public class Items extends RoboFragmentActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        ItemContextOptionsDialog.OnItemContextOptionsListener, ItemAddEditDialog.OnAddEditItemListener,
        ItemDeleteDialog.OnDeleteItemListener, StartPackingConfDialog.OnUncheckAllItemsListener {

    private static final int ITEMS_LOADER_ID = 1;

    @InjectView(R.id.list_items)
    private DragSortListView items;

    private TextView headerMessage;

    @Inject
    private ItemService itemService;

    @Inject
    ContentResolver cr;

    private ItemCursorAdapter itemsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        itemsAdapter = new ItemCursorAdapter(this, null, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        getSupportLoaderManager().initLoader(ITEMS_LOADER_ID, null, this);

        items.setAdapter(itemsAdapter);
        items.setDropListener(new DragSortListView.DropListener() {
            @Override
            public void drop(int from, int to) {
                itemsAdapter.drop(from, to);
                itemService.updateItemsOrder(itemsAdapter.getListOrderedIds());
            }
        });

        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowCustomEnabled(true);

        View v = LayoutInflater.from(this).inflate(R.layout.items_header, null, false);
        headerMessage = (TextView) v.findViewById(R.id.header_message);
        actionBar.setCustomView(v);

        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);

        itemsAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                headerMessage.setText(itemsAdapter.getNumCheckedItems() + " / " + itemsAdapter.getCount());
            }
        });

        items.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ItemContextOptionsDialog cod = ItemContextOptionsDialog.newInstance(id,
                        ((TextView) view.findViewById(R.id.items_item_name)).getText().toString());
                cod.show(getSupportFragmentManager(), "item_operations_dialog");

                return true;
            }
        });

        items.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = ((TextView) view.findViewById(R.id.items_item_name)).getText().toString();

                if (itemService.toggleItemStatus(id, ((Integer) ((ViewGroup) view).getChildAt(0).getTag())) == 1) {
                    Toast.makeText(Items.this, "\"" + name + "\" checked", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Items.this, "\"" + name + "\" unchecked", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean itemHandled = false;

        switch (item.getItemId()) {
            case R.id.menu_add: {
                ItemAddEditDialog dialog = new ItemAddEditDialog();
                dialog.show(getSupportFragmentManager(), "item_add_edit_dialog");
                itemHandled = true;
                break;
            }

            case R.id.menu_startpacking: {
                StartPackingConfDialog dialog = new StartPackingConfDialog();
                dialog.show(getSupportFragmentManager(), "startpacking_conf_dialog");
                itemHandled = true;
                break;
            }
        }

        return itemHandled;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, ItemContentProvider.ITEMS, new String[]{"_id", "name", "photo_id", "status"},
                null, null, "\"order\" ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        itemsAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        itemsAdapter.swapCursor(null);
    }

    @Override
    public void onEditOption(long itemId, String name) {
        ItemAddEditDialog dialog = ItemAddEditDialog.newInstance(itemService.getItem(itemId));
        dialog.show(getSupportFragmentManager(), "item_add_edit_dialog");
    }

    @Override
    public void onDeleteOption(long itemId, String name) {
        ItemDeleteDialog idd = ItemDeleteDialog.newInstance(itemId, name);
        idd.show(getSupportFragmentManager(), "item_delete_dialog");
    }

    @Override
    public void onAddItem(Uri insertedId, String newName) {
        ((ItemAddEditDialog) getSupportFragmentManager().findFragmentByTag("item_add_edit_dialog")).dismiss();

        if (insertedId != null) {
            Toast.makeText(this, "New item \"" + newName + "\" added", Toast.LENGTH_LONG).show();
        } else {
            ErrorDialog dialog = ErrorDialog.newInstance("App Error", "Failed to add new item \"" + newName + "\"");
            dialog.show(getSupportFragmentManager(), "error_dialog");
        }
    }

    @Override
    public void onEditItem(int numRowsUpdated, String newName) {
        ((ItemAddEditDialog) getSupportFragmentManager().findFragmentByTag("item_add_edit_dialog")).dismiss();

        if (numRowsUpdated > 0) {
            Toast.makeText(this, "Item \"" + newName + "\" updated", Toast.LENGTH_LONG).show();
        } else {
            ErrorDialog dialog = ErrorDialog.newInstance("App Error", "Failed to update item \"" + newName + "\"");
            dialog.show(getSupportFragmentManager(), "error_dialog");
        }
    }

    @Override
    public void onDeleteItemOK(long itemId, String name) {
        ((ItemDeleteDialog) getSupportFragmentManager().findFragmentByTag("item_delete_dialog")).dismiss();

        if (cr.delete(ItemContentProvider.ITEMS, "_id = ?", new String[] {String.valueOf(itemId)}) > 0) {
            Toast.makeText(this, "\"" + name + "\" deleted", Toast.LENGTH_SHORT).show();
        } else {
            ErrorDialog dialog = ErrorDialog.newInstance("App Error", "Error deleting item \"" + name + "\"");
            dialog.show(getSupportFragmentManager(), "error_dialog");
        }
    }

    @Override
    public void onDeleteItemCancel() {
        ((ItemDeleteDialog) getSupportFragmentManager().findFragmentByTag("item_delete_dialog")).dismiss();
    }

    @Override
    public void onStartPackingOk() {
        ((StartPackingConfDialog) getSupportFragmentManager().findFragmentByTag(
                "startpacking_conf_dialog")).dismiss();

        if (itemService.uncheckAllItems() > 0) {
            Toast.makeText(this, "All items unchecked successfully", Toast.LENGTH_LONG).show();
        } else {
            ErrorDialog dialog = ErrorDialog.newInstance("App Error", "Failed to uncheck all items");
            dialog.show(getSupportFragmentManager(), "error_dialog");
        }
    }

    @Override
    public void onStartPackingCancel() {
        ((StartPackingConfDialog) getSupportFragmentManager().findFragmentByTag(
                "startpacking_conf_dialog")).dismiss();
    }

    public ItemService getItemService() {
        return itemService;
    }
}
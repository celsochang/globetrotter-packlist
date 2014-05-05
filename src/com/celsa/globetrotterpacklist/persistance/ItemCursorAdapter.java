package com.celsa.globetrotterpacklist.persistance;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.celsa.globetrotterpacklist.ItemService;
import com.celsa.globetrotterpacklist.R;
import com.celsa.globetrotterpacklist.dslv.DragSortCursorAdapter;
import com.google.inject.Inject;
import roboguice.RoboGuice;

public class ItemCursorAdapter extends DragSortCursorAdapter {

    @Inject
    private ItemService itemService;

    public ItemCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        RoboGuice.getInjector(context).injectMembers(this);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View v = LayoutInflater.from(context).inflate(R.layout.items_item, viewGroup, false);

        v.setTag(R.id.items_item_name, v.findViewById(R.id.items_item_name));
        v.setTag(R.id.items_item_photo, v.findViewById(R.id.items_item_photo));
        v.setTag(R.id.items_item_status, v.findViewById(R.id.items_item_status));

        return v;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView name = (TextView) view.getTag(R.id.items_item_name);
        final ImageView photo = (ImageView) view.getTag(R.id.items_item_photo);
        ImageView status = (ImageView) view.getTag(R.id.items_item_status);

        name.setText(cursor.getString(cursor.getColumnIndex("name")));

        new AsyncTask<Long, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Long... params) {
                byte[] image = itemService.getItemImage(params[0]);

                return (image != null)
                        ? BitmapFactory.decodeByteArray(image, 0, image.length)
                        : BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                photo.setImageBitmap(result);
            }
        }.execute(cursor.getLong(cursor.getColumnIndex("_id")));

        if (cursor.getInt(cursor.getColumnIndex("status")) == 0) {
            status.setImageResource(android.R.drawable.ic_delete);
            view.setTag(0);
        } else {
            status.setImageResource(android.R.drawable.btn_star);
            view.setTag(1);
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public int getNumCheckedItems() {
        int numCheckedItems = 0;

        for (int i = 0; i < getCount(); i++) {
            if ((Integer) (getView(i, null, null).getTag()) == 1) {
                numCheckedItems++;
            }
        }

        return numCheckedItems;
    }

    public long[] getListOrderedIds() {
        long[] itemIds = new long[getCount()];

        for (int i = 0; i < itemIds.length; i++) {
            itemIds[i] = getItemId(i);
        }

        return itemIds;
    }
}

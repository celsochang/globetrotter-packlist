package com.celsa.globetrotterpacklist.persistance;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.celsa.globetrotterpacklist.R;
import com.celsa.globetrotterpacklist.dslv.DragSortCursorAdapter;

public class ItemCursorAdapter extends DragSortCursorAdapter {

    public ItemCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
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
    public void bindView(View view, Context context, Cursor cursor) {
        TextView name = (TextView) view.getTag(R.id.items_item_name);
        final ImageView photo = (ImageView) view.getTag(R.id.items_item_photo);
        ImageView status = (ImageView) view.getTag(R.id.items_item_status);

        name.setText(cursor.getString(cursor.getColumnIndex("name")));

        final String photoId = cursor.getString(cursor.getColumnIndex("photo_id"));
        if (photoId != null) {
            new AsyncTask<Void, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(Void... params) {
                    return ExternalStorageUtils.loadItemPhoto(photoId);
                }

                @Override
                protected void onPostExecute(Bitmap result) {
                    photo.setImageBitmap(result);
                }
            }.execute();
        } else {
            photo.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher));
        }

        if (cursor.getInt(cursor.getColumnIndex("status")) == 0) {
            status.setImageResource(android.R.drawable.ic_delete);
            view.setTag(0);
        } else {
            status.setImageResource(android.R.drawable.btn_star);
            view.setTag(1);
        }
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

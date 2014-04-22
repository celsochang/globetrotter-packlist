package com.celsa.globetrotterpacklist.dialog;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.celsa.globetrotterpacklist.entities.Item;
import com.celsa.globetrotterpacklist.persistance.ExternalStorageUtils;
import com.celsa.globetrotterpacklist.persistance.ItemContentProvider;
import com.celsa.globetrotterpacklist.ItemService;
import com.celsa.globetrotterpacklist.R;
import com.google.inject.Inject;
import roboguice.fragment.RoboDialogFragment;
import roboguice.inject.InjectView;

import java.io.*;

public class ItemAddEditDialog extends RoboDialogFragment {

    private static final int REQUEST_IMAGE_CAPTURE_CODE = 1;

    @InjectView(R.id.add_edit_item_title)
    private TextView title;
    @InjectView(R.id.add_edit_item_name)
    private EditText nameET;
    @InjectView(R.id.add_edit_item_save)
    private Button save;

    @Inject
    private ItemService itemService;

    @Inject
    ContentResolver cr;

    private Long id;
    private String name;
    private String photoId;
    private ImageView photoIV;

    public static ItemAddEditDialog newInstance(Item item) {
        ItemAddEditDialog dialog = new ItemAddEditDialog();

        Bundle b = new Bundle();
        b.putLong("id", item.getId());
        b.putString("name", item.getName());
        b.putString("photo_id", item.getPhotoId());

        dialog.setArguments(b);

        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        id = (getArguments() == null) ? -1 : getArguments().getLong("id");
        name = (getArguments() == null) ? null : getArguments().getString("name");
        photoId = (getArguments() == null) ? null : getArguments().getString("photo_id");

        return inflater.inflate(R.layout.item_add_edit, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        photoIV = (ImageView) view.findViewById(R.id.add_edit_item_photo);
        photoIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (i.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(i, REQUEST_IMAGE_CAPTURE_CODE);
                }
            }
        });

        if (id == -1) {
            title.setText("Add Item");
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        } else {
            title.setText(name);
            nameET.setText(name);
            if (photoId != null) {
                photoIV.setImageBitmap(ExternalStorageUtils.loadItemThumbnail(photoId));
            }
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNewItemValid()) {
                    ContentValues cv = new ContentValues();
                    cv.put("name", nameET.getText().toString());
                    cv.put("photo_id", photoId);

                    if (id == -1) {
                        cv.put("\"order\"", itemService.getItemMaxOrder() + 1);
                        ((OnAddEditItemListener) getActivity()).onAddItem(
                                cr.insert(ItemContentProvider.ITEMS, cv), nameET.getText().toString());
                    } else {
                        ((OnAddEditItemListener) getActivity()).onEditItem(cr.update(ItemContentProvider.ITEMS, cv,
                                "_id = ?", new String[] {String.valueOf(id)}), nameET.getText().toString());
                    }
                }
            }

            private boolean isNewItemValid() {
                boolean isValid = true;
                int newItemLength = nameET.getText().toString().length();

                if (newItemLength <= 0) {
                    isValid = false;
                    nameET.setError("Can't be blank");
                }

                if (isValid && !((newItemLength >= 1) && (newItemLength <= 100))) {
                    isValid = false;
                    nameET.setError("Must have between 1 and 100 characters");
                }

                return isValid;
            }
        });
    }

    public interface OnAddEditItemListener {
        void onAddItem(Uri insertedId, String newName);
        void onEditItem(int numRowsUpdated, String newName);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE_CODE && resultCode == Activity.RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");

            try {
                // Save image thumbnail
                photoId = ExternalStorageUtils.saveItemThumbnail(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Delete image from gallery
            cr.delete(data.getData(), null, null);

            photoIV.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}

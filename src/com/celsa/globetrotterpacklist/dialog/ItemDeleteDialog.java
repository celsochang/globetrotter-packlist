package com.celsa.globetrotterpacklist.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ItemDeleteDialog extends DialogFragment {

    public static ItemDeleteDialog newInstance(long id, String name) {
        Bundle args = new Bundle();
        args.putLong("id", id);
        args.putString("name", name);

        ItemDeleteDialog dialog = new ItemDeleteDialog();
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Bundle b = getArguments();
        final String name = b.getString("name");

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle("Delete " + name);
        dialog.setMessage("Are you sure you want to delete this item?");

        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((OnDeleteItemListener) getActivity()).onDeleteItemOK(b.getLong("id"), name);
            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((OnDeleteItemListener) getActivity()).onDeleteItemCancel();
            }
        });

        return dialog.create();
    }

    public interface OnDeleteItemListener {
        void onDeleteItemOK(long itemId, String name);

        void onDeleteItemCancel();
    }
}
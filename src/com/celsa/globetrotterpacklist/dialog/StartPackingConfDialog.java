package com.celsa.globetrotterpacklist.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class StartPackingConfDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());

        dialog.setTitle("Start Packing");
        dialog.setMessage("Are you sure you want to uncheck all items?");

        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((OnUncheckAllItemsListener) getActivity()).onStartPackingOk();
            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((OnUncheckAllItemsListener) getActivity()).onStartPackingCancel();
            }
        });

        return dialog.create();
    }

    public interface OnUncheckAllItemsListener {
        void onStartPackingOk();
        void onStartPackingCancel();
    }
}
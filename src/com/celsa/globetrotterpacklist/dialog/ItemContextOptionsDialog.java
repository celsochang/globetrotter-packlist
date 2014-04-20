package com.celsa.globetrotterpacklist.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.celsa.globetrotterpacklist.R;

public class ItemContextOptionsDialog extends DialogFragment {

    public static ItemContextOptionsDialog newInstance(long id, String name) {
        Bundle args = new Bundle();
        args.putLong("id", id);
        args.putString("name", name);

        ItemContextOptionsDialog dialog = new ItemContextOptionsDialog();
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle b = getArguments();
        final long itemId = b.getLong("id");
        final String name = b.getString("name");

        View v = inflater.inflate(R.layout.item_context_operations, container, false);

        getDialog().setTitle(name);

        ((ListView) v).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getDialog().cancel();

                String selectedOption = ((TextView) view).getText().toString();

                if (selectedOption.equals("Edit")) {
                    ((OnItemContextOptionsListener) getActivity()).onEditOption(itemId, name);
                } else {
                    ((OnItemContextOptionsListener) getActivity()).onDeleteOption(itemId, name);
                }
            }
        });

        return v;
    }

    public interface OnItemContextOptionsListener {
        void onEditOption(long itemId, String name);
        void onDeleteOption(long itemId, String name);
    }
}
package com.assistne.mywallet.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.assistne.mywallet.R;

/**
 * Created by assistne on 15/10/5.
 */
public class RemoveCategoryDialogFragment extends DialogFragment {

    private Callback callback;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_edit_category)
                .setCancelable(true)
                .setItems(new String[]{getResources().getString(R.string.dialog_remove_category)}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (callback != null) {
                            callback.remove();
                        }
                    }
                })
                ;
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    interface Callback {
        void remove();
        void edit();
    }
}

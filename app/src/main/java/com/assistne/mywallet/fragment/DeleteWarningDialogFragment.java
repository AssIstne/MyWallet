package com.assistne.mywallet.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.assistne.mywallet.R;

/**
 * Created by assistne on 15/10/1.
 */
public class DeleteWarningDialogFragment extends DialogFragment{
    private Callback callback = null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_delete_bill)
                .setTitle(R.string.global_delete)
                .setPositiveButton(R.string.global_ensure, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (callback != null) {
                            callback.positive();
                        }
                    }
                })
                .setNegativeButton(R.string.global_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (callback != null) {
                            callback.negative();
                        }
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback{
        void positive();
        void negative();
    }
}

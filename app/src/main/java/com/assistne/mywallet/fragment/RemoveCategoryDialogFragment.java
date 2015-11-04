package com.assistne.mywallet.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.assistne.mywallet.R;
import com.assistne.mywallet.model.BillCategory;

/**
 * Created by assistne on 15/10/5.
 */
public class RemoveCategoryDialogFragment extends DialogFragment {

    public static final String TYPE = "type";
    private Callback callback;

    public static RemoveCategoryDialogFragment newInstance(int categoryType) {
        Bundle bundle = new Bundle();
        bundle.putInt(TYPE, categoryType);
        RemoveCategoryDialogFragment dialogFragment = new RemoveCategoryDialogFragment();
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Bundle bundle = getArguments();
        int type = BillCategory.SYSTEM_INCOME;
        if (bundle != null) {
            type = bundle.getInt(TYPE, BillCategory.SYSTEM_INCOME);
        }
        String[] list;
        if (type == BillCategory.SYSTEM_INCOME || type == BillCategory.SYSTEM_SPENT) {
            list = new String[]{getResources().getString(R.string.dialog_item_remove_category)};
        } else {
            list = new String[2];
            list[0] = getResources().getString(R.string.dialog_item_remove_category);
            list[1] = getResources().getString(R.string.global_modify);
        }
        builder.setTitle(R.string.dialog_title_edit_category)
                .setCancelable(true)
                .setItems(list, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (callback != null) {
                            if (which == 0) {
                                callback.remove();
                            } else {
                                callback.edit();
                            }
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

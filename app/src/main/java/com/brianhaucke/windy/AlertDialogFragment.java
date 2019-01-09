package com.brianhaucke.windy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

public class AlertDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(getString(R.string.error_title))
        .setMessage(getString(R.string.error_message))
        .setPositiveButton(getString(R.string.error_button_ok_text), null);

        return builder.create();
    }
}

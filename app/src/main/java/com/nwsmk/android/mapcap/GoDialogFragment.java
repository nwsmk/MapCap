package com.nwsmk.android.mapcap;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by nwsmk on 2/28/2017.
 */

public class GoDialogFragment extends DialogFragment {

    private View v;

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface GoDialogListener {
        public void onGoDialogPositiveClick(DialogFragment dialog);
        public void onGoDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    GoDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the SettingsDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the SettingsDialogListener so we can send events to the host
            mListener = (GoDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement GoDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle SavedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Get view inflater
        View v = inflater.inflate(R.layout.diag_go, null);

        builder.setView(v)
                .setMessage(R.string.diag_settings)
                .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        mListener.onGoDialogPositiveClick(GoDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked cancel button
                        GoDialogFragment.this.getDialog().cancel();
                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();


    }
}

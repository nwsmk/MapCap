package com.nwsmk.android.mapcap;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class SettingsDialogFragment extends DialogFragment {

    private View v;

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface SettingsDialogListener {
        public void onSettingsDialogPositiveClick(DialogFragment dialog);
        public void onSettingsDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    SettingsDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the SettingsDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the SettingsDialogListener so we can send events to the host
            mListener = (SettingsDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement SettingsDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle SavedInstanceState) {

        double initLat = getArguments().getDouble("lat");
        double initLon = getArguments().getDouble("lon");

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Get view inflater
        View v = inflater.inflate(R.layout.diag_settings, null);

        // Set init values
        // Latitude
        EditText editLat = (EditText) v.findViewById(R.id.edt_lat);
        editLat.setText(String.valueOf(initLat));

        // Longtitude
        EditText editLon = (EditText) v.findViewById(R.id.edt_lon);
        editLon.setText(String.valueOf(initLon));

        builder.setView(v)
                .setMessage(R.string.diag_settings)
                .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        mListener.onSettingsDialogPositiveClick(SettingsDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked cancel button
                        SettingsDialogFragment.this.getDialog().cancel();
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

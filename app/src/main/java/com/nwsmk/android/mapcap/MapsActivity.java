package com.nwsmk.android.mapcap;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, SettingsDialogFragment.SettingsDialogListener {

    private GoogleMap mMap;

    /** Base variables */
    private double lat = 0;
    private double lon = 0;
    private int ppm = 5;
    private float dx = 0;
    private float dy = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Specified starting point
        /**
         Button btnGo = (Button) findViewById(R.id.btn_go);
         btnGo.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
        // Show Go dialog
        DialogFragment newFragment = new GoDialogFragment();
        newFragment.show(getSupportFragmentManager(), "go");
        }
        });
         */

        // Capture
        Button btnCapture = (Button) findViewById(R.id.btn_capture);
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
                    Bitmap bitmap;

                    @Override
                    public void onSnapshotReady(Bitmap snapshot) {
                        bitmap = snapshot;
                        try {
                            Calendar c = Calendar.getInstance();
                            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
                            String formattedDate = df.format(c.getTime());

                            // FileOutputStream out = new FileOutputStream("/mnt/sdcard/Pictures/outlat.png");
                            String filename = formattedDate + ".png";
                            String filepath = "/mnt/sdcard/Pictures/" + filename;
                            FileOutputStream out = new FileOutputStream(filepath);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);

                            // get screen bounds
                            LatLngBounds mLatLngBounds = mMap.getProjection().getVisibleRegion().latLngBounds;
                            LatLng mLatLngNE = mLatLngBounds.northeast;
                            LatLng mLatLngSW = mLatLngBounds.southwest;
                            Log.d("LOCATION", "NE: " + mLatLngNE + " SW: " + mLatLngSW);

                            // Display successful toast
                            Toast.makeText(MapsActivity.this, "Image exported successfully", Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                mMap.snapshot(callback);
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        // mMap.setMyLocationEnabled(true);
        LatLng currentLatLong = getCurrentLocation();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLong));

        // Add a marker in Sydney and move the camera
/*        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

        // Add long press listener: activate Settings dialog
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                // Get current lat, lon and zoom level
                lat = latLng.latitude;
                lon = latLng.longitude;

                // Show additional setting dialog
                Bundle args = new Bundle();
                args.putDouble("lat", lat);
                args.putDouble("lon", lon);
                DialogFragment newFragment = new SettingsDialogFragment();
                newFragment.setArguments(args);
                newFragment.show(getSupportFragmentManager(), "settings");
            }
        });
    }

    private LatLng getCurrentLocation() {

        LatLng currentLatLng = new LatLng(13.7563, 100.5018);
        return currentLatLng;
    }


    /** Check if required permissions are enabled */
    private void checkPermissions() {

    }

    @Override
    public void onSettingsDialogPositiveClick(DialogFragment dialog) {
        // Get Settings dialog form values
        Dialog viewDialog  = dialog.getDialog();
        EditText mEditLat  = (EditText) viewDialog.findViewById(R.id.edt_lat);
        EditText mEditLon  = (EditText) viewDialog.findViewById(R.id.edt_lon);
        /**
        EditText mEditPpm = (EditText) viewDialog.findViewById(R.id.edt_ppm);
         */
        lat  = Double.parseDouble(mEditLat.getText().toString());
        lon  = Double.parseDouble(mEditLon.getText().toString());
        /**
        ppm = Integer.parseInt(mEditPpm.getText().toString());
         */

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(lat, lon);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Selected Marker"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, zoom));
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(zoom), 2000, null);

        // update map display
        fitMap(ppm);
    }

    @Override
    public void onSettingsDialogNegativeClick(DialogFragment dialog) {

    }

    private void fitMap(int ppm) {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        dx = width/ppm;
        Log.d("DISTANCE", "DISTANCE DX " + dx);
        dy = height/ppm;
        Log.d("DISTANCE", "DISTANCE DY" + dy);

        LatLng topLeft = new LatLng(lat, lon);
        LatLng bottomLeft = getDestinationPoint(topLeft, 90, dx);
        LatLng topRight = getDestinationPoint(topLeft, 180, dy);
        LatLngBounds latLngBounds = new LatLngBounds(topRight, bottomLeft);
        /**
        mMap.setLatLngBoundsForCameraTarget(latLngBounds);
         */
        //mMap.addMarker(new MarkerOptions().position(bottomLeft).title("Bottom Left"));
        //mMap.addMarker(new MarkerOptions().position(topRight).title("Top Right"));
        int padding = 0; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(latLngBounds, padding);
        mMap.animateCamera(cu);

        float[] results = new float[1];
        Location.distanceBetween(topLeft.latitude, topLeft.longitude,
                bottomLeft.latitude, bottomLeft.longitude,
                results);
        Log.d("DISTANCE", "DISTANCE " + results[0]);
    }

    private LatLng getDestinationPoint(LatLng source, double brng, double dist) {
        dist = dist / (6371.0*1000);
        brng = Math.toRadians(brng);

        double lat1 = Math.toRadians(source.latitude), lon1 = Math.toRadians(source.longitude);
        double lat2 = Math.asin(Math.sin(lat1) * Math.cos(dist) +
                Math.cos(lat1) * Math.sin(dist) * Math.cos(brng));
        double lon2 = lon1 + Math.atan2(Math.sin(brng) * Math.sin(dist) *
                        Math.cos(lat1),
                Math.cos(dist) - Math.sin(lat1) *
                        Math.sin(lat2));
        if (Double.isNaN(lat2) || Double.isNaN(lon2)) {
            return null;
        }
        return new LatLng(Math.toDegrees(lat2), Math.toDegrees(lon2));
    }

    /**
    @Override
    public void onGoDialogPositiveClick(DialogFragment dialog) {
        // Get Settings dialog form values
        Dialog viewDialog  = dialog.getDialog();
        EditText mEditLat  = (EditText) viewDialog.findViewById(R.id.edt_lat);
        EditText mEditLon  = (EditText) viewDialog.findViewById(R.id.edt_lon);
        EditText mEditPpm = (EditText) viewDialog.findViewById(R.id.edt_ppm);
        lat  = Double.parseDouble(mEditLat.getText().toString());
        lon  = Double.parseDouble(mEditLon.getText().toString());

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(lat, lon);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Selected Marker"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, zoom));
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(zoom), 2000, null);

        // update map display
        fitMap(ppm);
    }

    @Override
    public void onGoDialogNegativeClick(DialogFragment dialog) {

    }
    */
}

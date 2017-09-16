package com.example.aaron.hackintoit;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    Button close;
    EditText editText;
    TextView nonNumberError;


    private static final String TAG = "tag";
    private PlaceDetectionClient mPlaceDetectionClient;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // Used for selecting the current place.
    private String mLikelyPlaceNames;

    private TextView loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //set content view AFTER ABOVE sequence (to avoid crash)
        this.setContentView(R.layout.activity_main);

        //Set the toolbar to show on the screen
        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(myToolbar);
    }

    private PopupWindow pw;
    protected void addCost(View v) {
        try {
            // We need to get the instance of the LayoutInflater
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.popup,
                    (ViewGroup) findViewById(R.id.popup_1));
            editText = layout.findViewById(R.id.money_input);
            nonNumberError = layout.findViewById(R.id.number_format_error_message);
            pw = new PopupWindow(layout, 1250, 750, true);
            pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
            close = layout.findViewById(R.id.update_money);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateMoney(view);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getLoc(View view) {
        System.out.println("ASD");

        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        if (!mLocationPermissionGranted) {
            getLocationPermission();
        }

        if (mLocationPermissionGranted) {
            // Get the likely places - that is, the businesses and other points of interest that
            // are the best match for the device's current location.
            @SuppressWarnings("MissingPermission") final
            Task<PlaceLikelihoodBufferResponse> placeResult =
                    mPlaceDetectionClient.getCurrentPlace(null);
            placeResult.addOnCompleteListener
                    (new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                System.out.println("HJSDFHLKJSDF");

                                PlaceLikelihood place = task.getResult().get(0);

                                mLikelyPlaceNames = (String) place.getPlace().getName();

                                loc = (TextView)findViewById(R.id.money_left);
                                loc.setText(mLikelyPlaceNames);

                            } else {
                                Log.e(TAG, "Exception: %s", task.getException());
                            }
                        }
                    });
        }

    }

    private void updateMoney(View view) {
        try {
            TextView textView = findViewById(R.id.money_left);
            Float moneyRemaining = Float.parseFloat(textView.getText().toString().replaceAll("\\$", ""));
            moneyRemaining -= Float.parseFloat(editText.getText().toString().replaceAll("\\$", ""));
            if (moneyRemaining < 0) {
                textView.setText("-$" + String.format("%.2f", Math.abs(moneyRemaining)));
            } else {
                textView.setText("$" + String.format("%.2f", moneyRemaining));
            }
            nonNumberError.setText("That is not a dollar amount...");
            pw.dismiss();
        } catch (NumberFormatException e) {
            nonNumberError.setText("That is not a dollar amount...");
        }

    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
}

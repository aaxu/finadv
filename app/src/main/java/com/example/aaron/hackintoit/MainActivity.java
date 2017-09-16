package com.example.aaron.hackintoit;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;
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

    private Button close;
    private EditText editText;
    private TextView nonNumberError;
    private TextView moneyLeft;
    private Context context;


    private static final String TAG = "tag";
    private PlaceDetectionClient mPlaceDetectionClient;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    public static final String KEY_QUICK_REPLY_TEXT = "quick_reply";

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

        moneyLeft = (TextView) findViewById(R.id.money_left);
        context = this;
    }

    private void createNotification() {
        PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        Notification notification = new NotificationCompat.Builder(this)
                .setTicker(getResources().getString(R.string.app_name))
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(getResources().getString(R.string.app_name))
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
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
            editText.setHint("Amount spent");
            close = layout.findViewById(R.id.update_money);
            close.setText("Update Money");
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateMoney(context);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void set_money_for_week(View v) {
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
            close.setText("Set money");
            editText.setHint("Money limit for this week");
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setMoney(context);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setMoney(Context context) {
        try {
            float newMoney = Float.parseFloat(editText.getText().toString().replaceAll("\\$", ""));
            if (newMoney < 0) {
                moneyLeft.setText("-$" + String.format("%.2f", Math.abs(newMoney)));
                moneyLeft.setTextColor(ContextCompat.getColor(context, R.color.negative)); //holo_red_dark
            } else {
                moneyLeft.setText("$" + String.format("%.2f", newMoney));
                moneyLeft.setTextColor(ContextCompat.getColor(context, R.color.positive)); //holo_red_dark

            }
            nonNumberError.setText(" ");
            pw.dismiss();
            createNotification();
        } catch (NumberFormatException e) {
            nonNumberError.setText("That is not a dollar amount...");
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

    private void updateMoney(Context context) {
        try {
            Float moneyRemaining = Float.parseFloat(moneyLeft.getText().toString().replaceAll("\\$", ""));
            moneyRemaining -= Float.parseFloat(editText.getText().toString().replaceAll("\\$", ""));
            if (moneyRemaining < 0) {
                moneyLeft.setTextColor(ContextCompat.getColor(context, R.color.negative)); //holo_red_dark
                moneyLeft.setText("-$" + String.format("%.2f", Math.abs(moneyRemaining)));
            } else {
                moneyLeft.setText("$" + String.format("%.2f", moneyRemaining));
                moneyLeft.setTextColor(ContextCompat.getColor(context, R.color.positive)); //holo_red_dark
            }
            nonNumberError.setText(" ");
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

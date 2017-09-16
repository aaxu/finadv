package com.example.aaron.hackintoit;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.app.job.JobScheduler;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.app.job.JobInfo;

import android.content.ComponentName;
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

    private Button close;
    private EditText editText;
    private TextView nonNumberError;
    private TextView moneyLeft;
    SharedPreferences.Editor data;

    private static final String TAG = "tag";
    private static PlaceDetectionClient mPlaceDetectionClient;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // Used for selecting the current place.
    private static String location;

    private int jobId = 0;
    private JobScheduler jobScheduler;
    private Context context;

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
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        data = preferences.edit();
        moneyLeft.setText(preferences.getString("moneyLeft", "$0"));
        // TODO: Currently does not save color.

        getLocationPermission();
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
        jobScheduler = (JobScheduler) getSystemService( Context.JOB_SCHEDULER_SERVICE );

        JobInfo.Builder builder = new JobInfo.Builder(jobId++, new ComponentName( getPackageName(), MyJobService.class.getName()) );

        long time = 10000;
        builder.setPeriodic(time);
        jobScheduler.schedule(builder.build());


    }

    @Override
    protected void onNewIntent(Intent intent) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        try {
            Float moneyRemaining = Float.parseFloat(moneyLeft.getText().toString().replaceAll("\\$", ""));
//                No support for android versions below Nougat. Can't direct reply
//                moneyRemaining -= Float.parseFloat(quickReplyResult.toString().replaceAll("\\$", ""));
            if (moneyRemaining < 0) {
                moneyLeft.setTextColor(ContextCompat.getColor(context, R.color.negative)); //holo_red_dark
                moneyLeft.setText("-$" + String.format("%.2f", Math.abs(moneyRemaining)));
            } else {
                moneyLeft.setText("$" + String.format("%.2f", moneyRemaining));
                moneyLeft.setTextColor(ContextCompat.getColor(context, R.color.positive)); //holo_red_dark
            }
        } catch (NumberFormatException e) {
            moneyLeft.setText("ERROR");
        }

        addCost("Are you spending again? How much did you spend...");
    }



    private PopupWindow pw;
    protected void clicked_add_cost(View v) {
        addCost(" ");
    }

    private void addCost(String initialText) {
        try {
            // We need to get the instance of the LayoutInflater
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.popup,
                    (ViewGroup) findViewById(R.id.popup_1));
            editText = layout.findViewById(R.id.money_input);
            nonNumberError = layout.findViewById(R.id.number_format_error_message);
            pw = new PopupWindow(layout, 1500, 1000, true);
            pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
            editText.setHint("Amount spent");
            close = layout.findViewById(R.id.update_money);
            close.setText("Update Money");
            nonNumberError.setText(initialText);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateMoney(context);
                    data.putString("moneyLeft", moneyLeft.getText().toString());
                    data.commit();
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
                    data.putString("moneyLeft", moneyLeft.getText().toString());
                    data.commit();
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
        } catch (NumberFormatException e) {
            nonNumberError.setText("That is not a dollar amount...");
        }
    }

    @Override
    protected void onStop() {
        stopService(new Intent(this, MyJobService.class));
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent startServiceIntent = new Intent(this, MyJobService.class);
//        Messenger messengerIncoming = new Messenger(mHandler);
//        startServiceIntent.putExtra(MESSENGER_INTENT_KEY, messengerIncoming);
        startService(startServiceIntent);
    }

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.example.aaron.hackintoit.MyJobService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static String getLoc() {
        @SuppressWarnings("MissingPermission") final
        Task<PlaceLikelihoodBufferResponse> placeResult =
                mPlaceDetectionClient.getCurrentPlace(null);
        placeResult.addOnCompleteListener
                (new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                        if (task.isSuccessful() && task.getResult() != null) {

                            PlaceLikelihood place = task.getResult().get(0);

                            location = (String) place.getPlace().getName();

                        } else {
                            Log.e(TAG, "Exception: %s", task.getException());
                            location = "";
                        }
                    }
                });
        return location;

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



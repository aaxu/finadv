package com.example.aaron.hackintoit;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "tag";
    private static PlaceDetectionClient mPlaceDetectionClient;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // Used for selecting the current place.
    private static String location;
    private String prevLocation;
    private long start;
    private int timeUntilPush;
    private TextView requestLog;

    private ComponentName serviceComponent;
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

        getLocationPermission();
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

//        requestLog = (TextView) findViewById(R.id.money_left);

//        context = getApplicationContext();


//        serviceComponent = new ComponentName(this, MyJobService.class);

//        JobScheduler tm = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler = (JobScheduler) getSystemService( Context.JOB_SCHEDULER_SERVICE );
//        scheduleJob();


        JobInfo.Builder builder = new JobInfo.Builder(jobId++, new ComponentName( getPackageName(), MyJobService.class.getName() ) );

//        builder.setMinimumLatency(30000);
//        builder.setOverrideDeadline(60000);
        builder.setPeriodic(3000);

        jobScheduler.schedule(builder.build());

//        timer = new Timer();

//        timeUntilPush = 5000; //5 * 60 * 1000;
//        prevLocation = getLoc();
//        System.out.println("SDFJSLDKFJL");
//        start = System.currentTimeMillis();

//        while (true) {
//            location = getLoc();
//            System.out.println(location);
//            if (prevLocation != location) {
//                start = System.currentTimeMillis();
//            }
//            if (start >= timeUntilPush) {
//                System.out.println("lasdjfs");
//                //send a push notification
//                requestLog.setText("DONT BUY ANYTHING");
//            }
//            try {
//                sleep(1000);
//            } catch (InterruptedException ie){
//                System.out.println("hey");
//            }
//            prevLocation = location;
//        }

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

    public void scheduleJob() {


//        String delay = mDelayEditText.getText().toString();
//        if (!TextUtils.isEmpty(delay)) {
//            builder.setMinimumLatency(Long.valueOf(delay) * 1000);
//        }
//        String deadline = mDeadlineEditText.getText().toString();
//        if (!TextUtils.isEmpty(deadline)) {
//            builder.setOverrideDeadline(Long.valueOf(deadline) * 1000);
//        }
//        boolean requiresUnmetered = mWiFiConnectivityRadioButton.isChecked();
//        boolean requiresAnyConnectivity = mAnyConnectivityRadioButton.isChecked();
//        if (requiresUnmetered) {
//            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED);
//        } else if (requiresAnyConnectivity) {
//            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
//        }
//        builder.setRequiresDeviceIdle(mRequiresIdleCheckbox.isChecked());
//        builder.setRequiresCharging(mRequiresChargingCheckBox.isChecked());

        // Extras, work duration.
//        PersistableBundle extras = new PersistableBundle();
//        String workDuration = mDurationTimeEditText.getText().toString();
//        if (TextUtils.isEmpty(workDuration)) {
//            workDuration = "1";
//        }
//        extras.putLong(WORK_DURATION_KEY, Long.valueOf(workDuration) * 1000);

//        builder.setExtras(extras);

        // Schedule job
//        Log.d(TAG, "Scheduling job");
    }

    public static String getLoc() {


//        if (!mLocationPermissionGranted) {
//            getLocationPermission();
//        }

//        if (mLocationPermissionGranted) {
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

                                PlaceLikelihood place = task.getResult().get(0);

                                location = (String) place.getPlace().getName();

                            } else {
                                Log.e(TAG, "Exception: %s", task.getException());
                                location = "";
                            }
                        }
                    });
//        }
        return location;

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



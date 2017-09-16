package com.example.aaron.hackintoit;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import static com.example.aaron.hackintoit.MainActivity.getLoc;

/**
 * Created by Yena on 9/16/2017.
 */

public class MyJobService extends JobService {

    private String location;
    private String prevLocation;
    private int count = 0;
//    private MainActivity ma = new MainActivity();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public boolean onStartJob(final JobParameters params) {
        mJobHandler.sendMessage( Message.obtain( mJobHandler, 1, params ) );

        // Return true as there's more work to be done with this job.
        return true;
    }

    private Handler mJobHandler = new Handler( new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            System.out.println("DSFSDJF");
//            MainActivity ma = new MainActivity();
//
            String location = getLoc();
            System.out.println(location);
            Toast.makeText( getApplicationContext(),
                    "You've been in " + location + " for a while. Reminder that your spending goal is blank", Toast.LENGTH_SHORT )
                    .show();
            createNotification();
            jobFinished( (JobParameters) msg.obj, false );
            return true;
        }

    } );

    public void createNotification() {
        Intent newInt = new Intent(this, MainActivity.class);
        newInt.putExtra("CALLED", true);
        PendingIntent pi = PendingIntent.getActivity(this, 0, newInt, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Action action = new Notification.Action.Builder(
                null, "Reply", pi)
                .addRemoteInput(new RemoteInput.Builder("quick_reply")
                        .setLabel("Quick reply").build())
                .build();
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification notification = new Notification.Builder(this)
                .setTicker(getResources().getString(R.string.app_name))
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText("Are you spending again? How much did you spend?")
                .setContentIntent(pi)
                .setAutoCancel(true)
                .addAction(action)
                .setAutoCancel(true)
                .build();
        notification.defaults |= Notification.DEFAULT_SOUND;


        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        // Stop tracking these job parameters, as we've 'finished' executing.
        mJobHandler.removeMessages( 1 );
        // Return false to drop the job.
        return false;
    }

}

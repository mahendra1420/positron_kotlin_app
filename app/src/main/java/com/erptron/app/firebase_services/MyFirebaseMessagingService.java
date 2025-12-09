package com.positron.teachers.firebase_services;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.positron.teachers.R;
import com.positron.teachers.activity.AttendanceActivity;
import com.positron.teachers.activity.TeacherNoticeActivity;
import com.positron.teachers.util.ApplicationPrefs;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    private ApplicationPrefs prefs;
    boolean rate = true;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String from = remoteMessage.getFrom();
        Map data = remoteMessage.getData();
        Log.e("Notification - 1", "onMessageReceived" + remoteMessage);
        Log.e("Notification - 1", "onMessageReceived" + data);
        Log.e("Notification - 2", "From: " + remoteMessage.getFrom());
//        generateNotification(remoteMessage.getData());
        if (remoteMessage.getData().size() > 0) {
            Log.d("Notification - 3", "Message data payload: " + remoteMessage.getData());
            if ( true) {
                generateNotification(remoteMessage);
                scheduleJob();
            } else {
                handleNow();
            }
        }

        if (remoteMessage.getNotification() != null) {
            generateNotification(remoteMessage);
            scheduleJob();
            Log.e("Notification - 4", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        if (remoteMessage.getData().size() > 0) {
            Log.e("Notification - 5", "onMessageReceived: Data - " + remoteMessage.getData().toString());
            generateNotification(remoteMessage);
            scheduleJob();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private void generateNotification(RemoteMessage remoteMessage) {

        try {
            sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody() , remoteMessage.getData().get("tag") );
            String CHANNEL_ID = "my_channel_01";
            CharSequence CHANNEL_NAME = "General";
            String CHANNEL_DESCRIPTION = "General Notification";
            createNotificationChannel(CHANNEL_ID, CHANNEL_NAME, CHANNEL_DESCRIPTION);
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
            Intent msgintent = new Intent("orderStatusUpdated");
            localBroadcastManager.sendBroadcast(msgintent);
            Intent intent = new Intent(this, AttendanceActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        /*try {
            sendNotification(data.get("title"), data.get("message"), data.get("notification_for"));
            if (data.containsKey("notification_for")) {
                if (Objects.requireNonNull(data.get("notification_for")).equalsIgnoreCase("Attendance")) {
                    if (data.containsKey("title") && data.containsKey("message")) {
                        String CHANNEL_ID = "my_channel_01";
                        CharSequence CHANNEL_NAME = "General";
                        String CHANNEL_DESCRIPTION = "General Notification";
                        createNotificationChannel(CHANNEL_ID, CHANNEL_NAME, CHANNEL_DESCRIPTION);
                        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
                        Intent msgintent = new Intent("orderStatusUpdated");
                        localBroadcastManager.sendBroadcast(msgintent);
                        Intent intent = new Intent(this, AttendanceActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        sendNotification(data.get("title"), data.get("message"), data.get("notification_for"));
                    }
                } else if (data.get("notification_for").equalsIgnoreCase("Notice")) {
                    if (data.containsKey("title") && data.containsKey("message")) {
                        String CHANNEL_ID = "my_channel_02";
                        CharSequence CHANNEL_NAME = "User";
                        String CHANNEL_DESCRIPTION = "User Accepted Notification";
                        createNotificationChannel(CHANNEL_ID, CHANNEL_NAME, CHANNEL_DESCRIPTION);
                        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
                        Intent msgintent = new Intent("orderStatusUpdated");
                        localBroadcastManager.sendBroadcast(msgintent);
                        Intent intent = new Intent(this, NoticeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        sendNotification(data.get("title"), data.get("message"), data.get("notification_for"));
                    }
                } else if (data.get("notification_for").equalsIgnoreCase("PersonalNotice")) {
                    if (data.containsKey("title") && data.containsKey("message")) {
                        String CHANNEL_ID = "my_channel_03";
                        CharSequence CHANNEL_NAME = "User";
                        String CHANNEL_DESCRIPTION = "User Rejested Notification";
                        createNotificationChannel(CHANNEL_ID, CHANNEL_NAME, CHANNEL_DESCRIPTION);
                        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
                        Intent msgintent = new Intent("orderStatusUpdated");
                        localBroadcastManager.sendBroadcast(msgintent);
                    Intent intent = new Intent(this, PersonalNoticeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        sendNotification(data.get("title"), data.get("message"), data.get("notification_for"));
                    }
                }*/
            //}
        } catch (Exception e) {
                e.printStackTrace();
        }
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        sendRegistrationToServer(token);
    }

    private void scheduleJob() {
        // [START dispatch_job]
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(MyWorker.class)
                .build();
        WorkManager.getInstance().beginWith(work).enqueue();
        // [END dispatch_job]
    }


    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }


    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }

    Intent intent;

    @RequiresApi(api = Build.VERSION_CODES.S)
    private void sendNotification(String title, String message, String notification_for) {



        intent = new Intent(this, TeacherNoticeActivity.class);
        /*if (Objects.equals(tag, "Notice")) {
        } else {
            intent = new Intent(this, PersonalNoticeActivity.class);
        }*/
        /*if (Objects.equals(notification_for, "Attendance")) {
            intent = new Intent(this, AttendanceActivity.class);

        }else if (Objects.equals(notification_for, "Notice")){
            intent = new Intent(this, NoticeActivity.class);

        }else if (Objects.equals(notification_for, "PersonalNotice")){
            intent = new Intent(this, PersonalNoticeActivity.class);

        } else {
            intent = new Intent(this, HomeActivity.class);
        }*/

         // intent = new Intent(this, AttendanceActivity.class);
        // intent = new Intent(this, HomePageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        String channelId = "";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.logonew)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setSound(defaultSoundUri)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private void createNotificationChannel(String CHANNEL_ID, CharSequence CHANNEL_NAME, String CHANNEL_DESCRIPTION) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESCRIPTION);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}

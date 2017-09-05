package com.raydevelopers.sony.match.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.raydevelopers.sony.match.R;
import com.raydevelopers.sony.match.utils.ChatHandler;
import com.raydevelopers.sony.match.utils.Constants;

/**
 * Created by SONY on 03-09-2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage.getData().size()>0)
        {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            String title = remoteMessage.getData().get("title");
            String message = remoteMessage.getData().get("text");
            String username = remoteMessage.getData().get("username");
            String fcmToken = remoteMessage.getData().get("fcm_token");

        if (!ChatHandler.active) {
            sendNotification(title,
                    message,
                    username,
                    fcmToken);
        } else {
           /* EventBus.getDefault().post(new PushNotificationEvent(title,
                    message,
                    username,
                    uid,
                    fcmToken));*/
        }
    }}
    private void sendNotification(String title,
                                  String message,
                                  String receiver,
                                  String firebaseToken) {
        Intent intent = new Intent(this, ChatHandler.class);
        intent.putExtra("receiver_key", receiver);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
.setSmallIcon(R.drawable.com_facebook_button_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}

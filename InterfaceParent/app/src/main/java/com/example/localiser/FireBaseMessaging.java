package com.example.localiser;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.*;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.localiser.Models.NotificationBody;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Map;
import java.util.Random;

public class FireBaseMessaging extends FirebaseMessagingService {

    private static final String TAG = "Token";
    FirebaseDatabase rootNode;
    DatabaseReference Token;
    @Override
    public void onNewToken(String mToken) {
        super.onNewToken(mToken);
        rootNode = FirebaseDatabase.getInstance("https://localisation-2b7ab-default-rtdb.firebaseio.com");
        Token = rootNode.getReference("NotificationToken");
        sendToken(mToken);
    }

    private void sendToken(String token) {
        Log.d(TAG, "sendToken: "+token);
        Token.setValue(token);

    }
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage != null){

                sendNotif(remoteMessage);

        }
    }

    private void sendNotif(RemoteMessage remoteMessage) {
/*
        String data_notify= remoteMessage.getNotification().getBody();
*/

        Map<String,String> data = remoteMessage.getData();
        String titre = data.get("title");
        String contenu = data.get("content");
        Log.d(TAG, "sendNotif: "+titre);
/*
        RemoteMessage.Notification notification = remoteMessage.getNotification();
*/
        Log.d(TAG, "sendNotif: ");
        /*Gson gson = new Gson();
        Log.d(TAG, "sendNotif: "+remoteMessage.getData().get("titre"));
        NotificationBody data = gson.fromJson(remoteMessage.getData().get("body"), NotificationBody.class);
        String titre = data.getTitre();
        String contenu = data.getContenu();*/
        /*Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = */
        String NOTIFICATION_CHANNEL_ID = "send_zones_notification";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,"Notify",
                    NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription("Information Localiser Votre enfant");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle(titre)
                .setContentText(contenu)
                .setAutoCancel(false)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher));
        Notification notification = builder.build();

        notificationManager.notify(new Random().nextInt(),notification);

    }


}

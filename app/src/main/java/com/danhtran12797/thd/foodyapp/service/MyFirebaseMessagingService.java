package com.danhtran12797.thd.foodyapp.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.activity.DetailProductActivity;
import com.danhtran12797.thd.foodyapp.activity.MainActivity;
import com.danhtran12797.thd.foodyapp.ultil.Ultil;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMessagingServ";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            sendNotification(remoteMessage.getData());
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "New token: " + token);
        insertToken(token);
    }

    private void insertToken(String token) {
        DataService dataService = APIService.getService();
        Call<String> callback = dataService.InsertToken(token);
        callback.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String message = response.body();
                if (message.equals("success")) {
                    Log.d(TAG, "Insert token success");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            Log.d(TAG, "getBitmapFromURL: " + e.getMessage());
            return null;
        }
    }

    private void sendNotification(Map data) {
        String title = (String) data.get("title");
        String message = (String) data.get("message");
        String image = (String) data.get("image");
        String id_product = (String) data.get("id_product");

        int notificationId = new Random().nextInt();

        Intent intent = null;
        PendingIntent pendingIntent = null;
        if (!id_product.equals("0")) {
            intent = new Intent(this, DetailProductActivity.class);
            intent.putExtra("notify", id_product);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntentWithParentStack(intent);
            pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            intent = new Intent(this, MainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.icon_foody_app)
                        .setLargeIcon(!image.equals("12797") ? getBitmapFromURL(Ultil.url_image_notify + image) : null)
                        .setContentTitle(title)
                        .setContentText(message)
//                        .setStyle(new NotificationCompat.BigPictureStyle()
//                                .bigPicture(image.length()>0? getBitmapFromURL(Ultil.url_image_notify + image) : null)
//                                .bigLargeIcon(null))
                        .setSound(defaultSoundUri)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);
//                        .setDefaults(Notification.DEFAULT_ALL)
//                        .setPriority(NotificationManager.IMPORTANCE_HIGH);

//                      .setStyle(new NotificationCompat.BigTextStyle().bigText(message))

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(notificationId /* ID of notification */, notificationBuilder.build());
    }
}

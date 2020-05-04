package com.example.meetchrysallis.Activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;

import com.example.meetchrysallis.Models.Evento;
import com.example.meetchrysallis.R;

public class Notification_reciever extends BroadcastReceiver {

    private Evento evento = new Evento();
    private String tag;


    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getBundleExtra("bundle");
        evento = (Evento)bundle.getSerializable("evento");
        tag = (String)bundle.getString("tag");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent repeating_intent = new Intent(context, EventoDetalladoActivity.class);
        repeating_intent.putExtra("evento", evento);
        repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, repeating_intent, PendingIntent.FLAG_UPDATE_CURRENT);

//        NotificationChannel nChannel = new NotificationChannel(id, "Nombre de la notificacion", importancia);

        String CHANNEL_ID = "my_channel_10";
        CharSequence name = "my_channel10";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_chat_black_24dp)
                .setContentTitle("TITULO")
                .setContentText("Se creó la notificacion con tag " + tag)
                .setAutoCancel(true);

        if (intent.getAction().equals("NOTIFICACION")) {
            notificationManager.notify(tag,150, builder.build());
        }
    }
}

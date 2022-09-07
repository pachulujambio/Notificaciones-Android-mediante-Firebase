package com.firebase.firebaseopc02.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.firebase.firebaseopc02.MainActivity;
import com.firebase.firebaseopc02.MainActivity2;
import com.firebase.firebaseopc02.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    //Definición de las variables
    private static final String CHANNEL_ID = "canal";
    private PendingIntent pendingIntent;
    String ejem1 = "";
    String ejem2 = "";

    /**
     * Genera y muestra el nuevo token del dispositivo, este se genera al instalar la aplicación
     *
     * @param token
     */
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        //Mostramos en el log el token del dispositivo, este se genera cuando se instala la aplicación
        Log.e("token,", "Mi token es: " + token);
        SaveToken(token);
    }

    /**
     * Escribimos el token dentro de firebase para tener un resgistro de los mismos
     *
     * @param token
     */
    private void SaveToken(String token) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Token");
        ref.child("Virtual").setValue(token);
    }

    /**
     * Recibe los mensajes enviados en las notificaciones dentro del LOG
     *
     * @param remoteMessage
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        //Saber desde que emisor llega el push
        Log.e("TAG", "Mensaje recibido de " + remoteMessage.getFrom());

        /*
         * Obtener título y cuerpo de una notificación, utilizando GetNotificación
         * (método para trabajar sobre la misma, no sobre la información que trae)
         */
        if (remoteMessage.getNotification() != null) {
            Log.e("tag", "El título es :" + remoteMessage.getNotification().getTitle());
            Log.e("tag", "El mensaje es :" + remoteMessage.getNotification().getBody());

        }

        //Obtener datos enviados mediante key/value
        if (remoteMessage.getData().size() > 0) {

            Log.e("TAG", "Lo enviado primero: " + remoteMessage.getData().get("ejem1"));
            Log.e("TAG", "Lo enviado segundo: " + remoteMessage.getData().get("ejem2"));

            //Obtener datos de la notificación en segundo plano. Se envia la key como parámetro del GET
            ejem1 = remoteMessage.getData().get("ejem1");
            ejem2 = remoteMessage.getData().get("ejem2");

            showNotification();

        }

    }

    //Construye el canal
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showNotification() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                "NEW", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);
        setPendingIntent(MainActivity2.class);
    }

    //Envia la notificación y la muestra
    private void showNewNotification() {
        setPendingIntent(MainActivity.class);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),
                CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_wienerlab)
                .setContentTitle(ejem1)
                .setContentText(ejem2)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Por favor controlar las pedidos realizados en el día de ayer."));
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getApplicationContext());
        managerCompat.notify(1, builder.build());

    }


    /**
     * Actividad para cambiar de layout luego de presionar la notificación
     *
     * @param clsActivity
     */
    private void setPendingIntent(Class<?> clsActivity) {
        Intent intent = new Intent(this, clsActivity);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(clsActivity);
        stackBuilder.addNextIntent(intent);
        pendingIntent = stackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT);
    }

}

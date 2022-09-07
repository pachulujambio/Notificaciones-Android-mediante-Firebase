package com.firebase.firebaseopc02;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    //Definición de las variables
    private static final String CHANNEL_ID = "canal";
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = findViewById(R.id.txt);

        //Crear entidad para la BD
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //Persistencia de la BD sin conexión
        database.getInstance().setPersistenceEnabled(true);
        //Creamos una referencia a la BD y le seteamos un valor nuevo
        DatabaseReference apellido = database.getReference("Apellido");
        apellido.setValue("Lujambio");

        // Lectura desde la BD en tiempo real, utilizamos la variable creada anteriorimente
        apellido.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                textView.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                String value = error.getMessage();
                textView.setText(value);
            }
        });

        TextView txt = findViewById(R.id.txt);

        // Recupera los datos enviados por key/value desde firebase
        if (getIntent().getExtras() != null) {
            String value = getIntent().getExtras().getString("title");
            Log.e("TAG", "Capturado en segundo plano: " + value);
            txt.setText(value);
            value = getIntent().getExtras().getString("details");
            Log.e("TAG", "Capturado en segundo plano: " + value);
        }

        Button button = findViewById(R.id.secLayout);
        button.setText("Send Notification");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Corrobora la versión del android para saber que método utilizar
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    showNotification();
                } else {
                    showNewNotification();
                }
            }
        });

    }

    //Contruye el canal
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showNotification() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                "NEW", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);
        showNewNotification();
    }

    //Envia la notificación y la muestra
    private void showNewNotification() {
        setPendingIntent(MainActivity.class);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),
                CHANNEL_ID)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.logo_wienerlab)
                .setContentTitle("New Notification")
                .setContentText("New MainActivity Notification")
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
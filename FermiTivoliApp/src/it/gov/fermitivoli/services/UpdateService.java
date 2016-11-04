package it.gov.fermitivoli.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import it.gov.fermitivoli.R;
import it.gov.fermitivoli.activity.MainMenuActivity;
import it.gov.fermitivoli.model.C_JSonCircolariDeltaServletResponse;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by stefano on 23/09/16.
 */
public class UpdateService extends Service {
    public static final int ID_NOTIFICA_UPDATE = 0;
    public static final int ID_NOTIFICA_START_UPDATE = 1;
    final ScheduledExecutorService scheduledExecutorService;
    private int initCount = 0;

    public UpdateService() {
        super();
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(new UpdateThreadService(this), 0, 5, TimeUnit.MINUTES);
    }

    public static boolean isNetworkAvailable(Context a) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) a.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onCreate() {
        //avvia il servizio (una sola volta)
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //chiude il servizio
        scheduledExecutorService.shutdownNow();

        System.out.println("STOP SERVICE");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("AVVIO SERVIZIO");
        notifica_avvio();

        if (initCount > 0)
            return START_STICKY;
        initCount = 1;

        //invocato ogni volta che qualcuno richiede l'avvio del servizio
        return super.onStartCommand(intent, flags, startId);
    }


    public void notifica_nuove_notizie(C_JSonCircolariDeltaServletResponse data) {
        // prepare intent which is triggered if the
        // notification is selected

        Intent intent = new Intent(this, MainMenuActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        int num = data.newsDaAggiungereAggiornare.size() + data.circolariDaAggiungereAggiornare.size();
        // build notification
        // the addAction re-use the same intent to keep the example short
        final Notification.Builder subject = new Notification.Builder(this)
                .setContentTitle("ITCG E FERMI di Tivoli")
                .setContentText(num + " nuove notizie dall'Istituto. Click per aprire l'applicazione.")
                .setSmallIcon(R.drawable.logo_fermi_150x150_bordato)
                .setContentIntent(pIntent)
                .setAutoCancel(true);
        Notification n = subject.getNotification();


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(ID_NOTIFICA_UPDATE, n);
    }

    public void notifica_avvio() {
        // prepare intent which is triggered if the
        // notification is selected

        Intent intent = new Intent(this, MainMenuActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);


        // build notification
        // the addAction re-use the same intent to keep the example short
        final Notification.Builder subject = new Notification.Builder(this)
                .setContentTitle("ITCG E FERMI di Tivoli")
                .setContentText("Avvio servizio di aggiornamento dati...")
                .setSmallIcon(R.drawable.logo_fermi_150x150_bordato)
                .setContentIntent(pIntent)
                .setAutoCancel(true);
        Notification n = subject.getNotification();


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(ID_NOTIFICA_START_UPDATE, n);
    }


}

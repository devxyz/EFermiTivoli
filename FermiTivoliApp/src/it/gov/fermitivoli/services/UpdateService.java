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
    final ScheduledExecutorService scheduledExecutorService;
    private boolean scheduled = false;

    public UpdateService() {
        super();
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
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

        //avvia il task
        scheduledExecutorService.schedule(new UpdateThreadService(this), 0, TimeUnit.MILLISECONDS);

        //se necessario effettua uno scheduling fisso
        if (!scheduled)
            scheduledExecutorService.scheduleAtFixedRate(new UpdateThreadService(this), 5, 5, TimeUnit.MINUTES);
        else
            return START_STICKY;

        scheduled = true;


        //invocato ogni volta che qualcuno richiede l'avvio del servizio
        return super.onStartCommand(intent, flags, startId);
    }


    /*public void notifica_errore(Throwable e) {
        // prepare intent which is triggered if the
        // notification is selected

        Intent intent = new Intent(this, MainMenuActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);


        // build notification
        // the addAction re-use the same intent to keep the example short
        final Notification.Builder subject = new Notification.Builder(this)
                .setContentTitle("ITCG E FERMI di Tivoli")
                .setContentText("Errore durante l'aggiornamento:" + e.getMessage() + ". Click per aprire l'applicazione.")
                .setSmallIcon(R.drawable.logo_fermi_150x150_bordato)
                .setContentIntent(pIntent)
                .setAutoCancel(true);
        Notification n = subject.getNotification();


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(ID_NOTIFICA_NUOVE_NOTIZIE_CIRCOLARI, n);
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

        notificationManager.notify(ID_NOTIFICA_NUOVE_NOTIZIE_CIRCOLARI, n);
    }

    public void rimuovi_notifica_nuove_notizie() {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.cancel(ID_NOTIFICA_NUOVE_NOTIZIE_CIRCOLARI);

    }

    public void rimuovi_notifica_avvio() {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.cancel(ID_NOTIFICA_AVVIA_AGGIORNAMENTO);
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
                .setSmallIcon(R.drawable.update_128x128)
                .setContentIntent(pIntent)
                .setAutoCancel(true);
        Notification n = subject.getNotification();


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(ID_NOTIFICA_AVVIA_AGGIORNAMENTO, n);
    }*/


}

package it.gov.fermitivoli.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import it.gov.fermitivoli.util.SharedPreferenceWrapper;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by stefano on 23/09/16.
 */
public class UpdateService extends Service {
    private static final int UPDATE_MINUTE = 5;
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

    public synchronized boolean shouldUpdate() {
        final SharedPreferenceWrapper e = SharedPreferenceWrapper.getCommonInstance(getApplication());
        final Date d = e.getLastDataUpdate();
        long minutes = ((System.currentTimeMillis() - d.getTime()) / 1000 / 60);
        return (minutes >= UPDATE_MINUTE);
    }

    public synchronized void updatedNow() {
        final SharedPreferenceWrapper e = SharedPreferenceWrapper.getCommonInstance(getApplication());
        e.setLastDataUpdate(new Date());
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
            scheduledExecutorService.scheduleAtFixedRate(new UpdateThreadService(this), UPDATE_MINUTE, UPDATE_MINUTE, TimeUnit.MINUTES);
        else
            return START_STICKY;

        scheduled = true;


        //invocato ogni volta che qualcuno richiede l'avvio del servizio
        return super.onStartCommand(intent, flags, startId);
    }


}

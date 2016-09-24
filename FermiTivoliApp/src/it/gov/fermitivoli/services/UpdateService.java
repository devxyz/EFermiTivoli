package it.gov.fermitivoli.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by stefano on 23/09/16.
 */
public class UpdateService extends IntentService {
    public UpdateService() {
        super("update service");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}

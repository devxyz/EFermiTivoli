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
import android.util.Log;
import com.google.gson.Gson;
import it.gov.fermitivoli.R;
import it.gov.fermitivoli.activity.MainMenuActivity;
import it.gov.fermitivoli.dao.DaoSession;
import it.gov.fermitivoli.dao.FermiAppDBHelperRun;
import it.gov.fermitivoli.dao.FermiAppDbHelper;
import it.gov.fermitivoli.dao.FermiAppDbHelperCallable;
import it.gov.fermitivoli.db.ManagerCircolare;
import it.gov.fermitivoli.db.ManagerNews;
import it.gov.fermitivoli.model.C_JSonCircolariDeltaServletRequest;
import it.gov.fermitivoli.model.C_JSonCircolariDeltaServletResponse;
import it.gov.fermitivoli.util.DebugUtil;
import it.gov.fermitivoli.util.StreamAndroid;
import it.gov.fermitivoli.util.ThreadUtil;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
        scheduledExecutorService.scheduleAtFixedRate(new UpdateThread(), 0, 5, TimeUnit.MINUTES);
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

    /**
     * richiesta al server
     *
     * @return
     * @throws IOException
     */
    private C_JSonCircolariDeltaServletResponse requestData() throws IOException {
        //prepara request
        //-------------------------------------------
        final C_JSonCircolariDeltaServletRequest req1 = new C_JSonCircolariDeltaServletRequest();
        final FermiAppDbHelper db = new FermiAppDbHelper(this.getApplicationContext());
        try {
            db.runInTransaction(new FermiAppDBHelperRun() {
                @Override
                public void run(DaoSession session, Context ctx) throws Throwable {
                    ManagerCircolare m = new ManagerCircolare(session);
                    req1.responseInZipFormat = true;
                    req1.maxToken = m.maxToken();
                }
            });

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            db.close();
        }
        final C_JSonCircolariDeltaServletRequest req = req1;

        //analizza la risposta
        //-------------------------------------------
        final String json = new Gson().toJson(req);
        if (DebugUtil.DEBUG__SincronizzaCircolariAsync) {
            Log.d("SINCRONIZZA CIRCOLARI", "xxx");
            for (int i = 0; i < json.length(); i = i + 100) {
                System.out.println(json.substring(i, Math.min(json.length(), i + 100)));
            }
        }


        //effettua chiamata post
        //---------------------------------------------
        String url = getResources().getString(R.string.url_data_json);
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");

        String urlParameters = "param=" + URLEncoder.encode(json, "UTF-8");

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        //attede la risposta
        //---------------------------------------------
        int responseCode = con.getResponseCode();
        if (DebugUtil.DEBUG__SincronizzaCircolariAsync) {
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + urlParameters);
            System.out.println("Response Code : " + responseCode);
        }

        //legge la risposta
        //---------------------------------------------
        Gson g = new Gson();
        ZipInputStream in = new ZipInputStream(new BufferedInputStream(con.getInputStream()));
        final ZipEntry nextEntry = in.getNextEntry();

        final String content = StreamAndroid.loadFileContent(in);
        in.close();

        return g.fromJson(content, C_JSonCircolariDeltaServletResponse.class);
    }

    private int syncNewsAndCircolari(final C_JSonCircolariDeltaServletResponse data) throws Throwable {

        final Context activity = getApplicationContext();
        try {
            final FermiAppDbHelper db = new FermiAppDbHelper(activity);
            try {
                db.runInTransaction(new FermiAppDbHelperCallable<Void>() {
                    @Override
                    public Void call(DaoSession session, Context ctx) throws Throwable {

                        //sync news
                        {
                            ManagerNews m = new ManagerNews(session);
                            m.sincronizzaLista(data.newsDaAggiungereAggiornare, data.keyNewsDaRimuovere);
                        }

                        //sync circolari
                        {
                            ManagerCircolare m = new ManagerCircolare(session);
                            for (String key : data.keyCircolariDaRimuovere) {
                                m.rimuoveCircolare(key);
                            }
                            m.salva(data.circolariDaAggiungereAggiornare);
                        }

                        return null;
                    }
                });
            } finally {
                db.close();
            }

        } catch (Throwable e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e);
        }
        return data.newsDaAggiungereAggiornare.size() + data.circolariDaAggiungereAggiornare.size();

    }

    private void notifica(C_JSonCircolariDeltaServletResponse data) {
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

    private void notifica_avvio() {
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

    private class UpdateThread implements Runnable {

        @Override
        public void run() {
            try {
                System.out.println("UPDATE");

                //attende che la rete dati sia disponibile
                int i = 0;
                while (!isNetworkAvailable(UpdateService.this) && i < 10) {
                    ThreadUtil.sleep(10000);
                    i++;
                }

                final C_JSonCircolariDeltaServletResponse data = requestData();
                final int num = syncNewsAndCircolari(data);

                if (num > 0) {
                    //notifica nuove circolari aggiunte
                    //notifica(data);
                    notifica(data);
                }

            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }


        }
    }


}

package it.gov.fermitivoli.services;

import android.content.Context;
import android.content.Intent;
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
import it.gov.fermitivoli.notification.NotificationMessage;
import it.gov.fermitivoli.notification.NotificationUtil;
import it.gov.fermitivoli.util.DebugUtil;
import it.gov.fermitivoli.util.StreamAndroid;
import it.gov.fermitivoli.util.ThreadUtil;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by stefano on 04/11/2016.
 */
class UpdateThreadService implements Runnable {

    private UpdateService updateService;

    public UpdateThreadService(UpdateService updateService) {
        this.updateService = updateService;
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
        final FermiAppDbHelper db = new FermiAppDbHelper(updateService.getApplicationContext());
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
        String url = updateService.getResources().getString(R.string.url_data_json);
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

    private int syncLocalDB(final C_JSonCircolariDeltaServletResponse data) throws Throwable {

        final Context activity = updateService.getApplicationContext();
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

    @Override
    public void run() {
        try {
            //se non richiesto l'update, skip
            if (!updateService.shouldUpdate())
                return;


            System.out.println("UPDATE");

            //attende che la rete dati sia disponibile
            int i = 0;
            while (!UpdateService.isNetworkAvailable(updateService) && i < 10) {
                ThreadUtil.sleep(10000);
                i++;
            }

            //final NotificationMessage n = NotificationUtil.updateProcessMessage();
            //n.show(updateService);
            final C_JSonCircolariDeltaServletResponse data = requestData();
            final int num = syncLocalDB(data);
            //n.cancel(updateService);

            if (num > 0) {
                //notifica_nuove_notizie nuove circolari aggiunte
                //notifica_nuove_notizie(data);
                NotificationUtil.newDataAvailableMessage(data.circolariDaAggiungereAggiornare.size() + data.newsDaAggiungereAggiornare.size());

                //aggiorna l'interfaccia grafica, se possibile
                Intent x = new Intent(MainMenuActivity.RECEIVER_ACTION_UPDATE);
                updateService.sendBroadcast(x);
            }

        } catch (Throwable throwable) {
            //notifica errore
            //NotificationUtil.updateProcessMessage().cancel(updateService);
            //NotificationUtil.errorMessage(throwable).cancel(updateService);
            throwable.printStackTrace();
        }


    }
}

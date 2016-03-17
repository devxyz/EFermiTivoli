package it.gov.fermitivoli.fragment.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import com.google.gson.Gson;
import it.gov.fermitivoli.R;
import it.gov.fermitivoli.api.AbstractActivity;
import it.gov.fermitivoli.dao.*;
import it.gov.fermitivoli.db.ManagerCircolare;
import it.gov.fermitivoli.db.ManagerNews;
import it.gov.fermitivoli.model.C_JSonCircolariDeltaServletRequest;
import it.gov.fermitivoli.model.C_JSonCircolariDeltaServletResponse;
import it.gov.fermitivoli.util.C_DateUtil;
import it.gov.fermitivoli.util.DebugUtil;
import it.gov.fermitivoli.util.StreamAndroid;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * classe di supporto per la sincronizzazione dei dati esterni (circolari e news)
 */
public abstract class ExternalDataSync_AsyncTask extends AsyncTask<Void, Integer, ExternalDataSync_AsyncTask.ExternalDataSync_Container> {
    private AbstractActivity activity;

    public ExternalDataSync_AsyncTask(AbstractActivity activity) {
        this.activity = activity;
    }


    public static boolean isNetworkAvailable(Activity a) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) a.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean shouldUpdate(AbstractActivity ctx) {
        final Date d = ctx.getSharedPreferences().getDataUltimoDownloadDati();
        if (C_DateUtil.differenzaInMinuti(d, new Date()) <= 2) {
            return false;
        }
        return true;
    }

    private static List<CircolareDB> __syncCircolari(final C_JSonCircolariDeltaServletResponse response, AbstractActivity activity) throws Throwable {

        FermiAppDbHelper db = new FermiAppDbHelper(activity);
        final List<CircolareDB> ris;
        try {

            ris = db.runInTransaction(new FermiAppDbHelperCallable<List<CircolareDB>>() {
                @Override
                public List<CircolareDB> call(DaoSession session, Context ctx) throws Throwable {
                    ManagerCircolare m = new ManagerCircolare(session);
                    for (String key : response.keyCircolariDaRimuovere) {
                        m.rimuoveCircolare(key);
                    }
                    m.salva(response.circolariDaAggiungereAggiornare);
                    return m.listAllCircolari();

                }
            });


        } finally {
            db.close();
        }
        return ris;

    }

    private static C_JSonCircolariDeltaServletResponse requestData(AbstractActivity activity) throws IOException {
        //prepara request
        //-------------------------------------------
        final C_JSonCircolariDeltaServletRequest req1 = new C_JSonCircolariDeltaServletRequest();
        final FermiAppDbHelper db = new FermiAppDbHelper(activity);
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
        String url = activity.getResources().getString(R.string.url_data_json);
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

    private static List<NewsDB> __syncNews(final C_JSonCircolariDeltaServletResponse data, AbstractActivity activity) throws Throwable {


        try {
            final FermiAppDbHelper db = new FermiAppDbHelper(activity);
            try {
                return db.runInTransaction(new FermiAppDbHelperCallable<List<NewsDB>>() {
                    @Override
                    public List<NewsDB> call(DaoSession session, Context ctx) throws Throwable {
                        ManagerNews m = new ManagerNews(session);
                        m.sincronizzaLista(data.newsDaAggiungereAggiornare, data.keyNewsDaRimuovere);
                        return m.listAllNews();
                    }
                });
            } finally {
                db.close();
            }

        } catch (Throwable e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e);
        }


    }

    public static ExternalDataSync_Container syncAll(AbstractActivity activity) {
        ExternalDataSync_Container ris = new ExternalDataSync_Container();
        boolean error = false;

        C_JSonCircolariDeltaServletResponse data = null;
        try {
            data = requestData(activity);
        } catch (Throwable throwable) {
            error = true;
            ris.setError(throwable);
            return ris;
        }
        try {
            ris.setNotizie(__syncNews(data, activity));
        } catch (Throwable throwable) {
            error = true;
            ris.setError(throwable);
            return ris;
        }

        try {
            ris.setCircolari(__syncCircolari(data, activity));
        } catch (Throwable throwable) {
            ris.setError(throwable);
            return ris;
        }

        activity.getSharedPreferences().setDataUltimoDownloadDati(new Date());
        return ris;
    }

    @Override
    protected final ExternalDataSync_Container doInBackground(Void... params) {
        return syncAll(activity);

    }

    public static class ExternalDataSync_Container {
        private List<NewsDB> notizie;
        private List<CircolareDB> circolari;
        private Throwable error;


        public ExternalDataSync_Container() {
        }

        public Throwable composeError() {
            return error;

        }

        public boolean containsErrors() {
            return error != null;
        }

        public List<NewsDB> getNotizie() {
            return notizie;
        }

        void setNotizie(List<NewsDB> notizie) {
            this.notizie = notizie;
        }

        public List<CircolareDB> getCircolari() {
            return circolari;
        }

        void setCircolari(List<CircolareDB> circolari) {
            this.circolari = circolari;
        }

        public Throwable getError() {
            return error;
        }

        void setError(Throwable error) {
            this.error = error;
        }

    }
}

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
import it.gov.fermitivoli.model.rss.RssFeed;
import it.gov.fermitivoli.rss.RssReader;
import it.gov.fermitivoli.util.C_DateUtil;
import it.gov.fermitivoli.util.DebugUtil;
import it.gov.fermitivoli.util.StreamAndroid;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
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

    private static List<CircolareDB> syncCircolari(AbstractActivity activity) throws Throwable {
        if (!isNetworkAvailable(activity))
            throw new IllegalArgumentException("Nessuna connessione dati disponibile");

        //prepara request
        //-------------------------------------------
        final C_JSonCircolariDeltaServletRequest req = getRequestData(activity);

        final String json = new Gson().toJson(req);
        if (DebugUtil.DEBUG__SincronizzaCircolariAsync) {
            Log.d("SINCRONIZZA CIRCOLARI", "xxx");
            for (int i = 0; i < json.length(); i = i + 100) {
                System.out.println(json.substring(i, Math.min(json.length(), i + 100)));
            }
        }


        //effettua chiamata post
        //---------------------------------------------
        String url = activity.getResources().getString(R.string.url_circolari_json);
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
        final C_JSonCircolariDeltaServletResponse response = g.fromJson(content, C_JSonCircolariDeltaServletResponse.class);

        /*System.out.println("=========================================== RESPONSE ===============================");
        System.out.println(response.circolariDaRimuovere);
        for (C_CircolareDto x : response.circolariDaAggiungereAggiornare) {
            System.out.println(x);
        }*/

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

    private static List<NewsDB> syncNews(AbstractActivity activity) throws Throwable {
        final List<NewsDB> res;
        HttpURLConnection con = null;
        try {
            URL u = new URL(activity.getResources().getString(R.string.url_rss_notizie));
            con = (HttpURLConnection) u.openConnection();
            BufferedInputStream in = new BufferedInputStream(con.getInputStream());
            final RssFeed feed;
            feed = RssReader.read(u);
            in.close();

            RssReader.normalizeHtml(feed);
            Log.d(ExternalDataSync_AsyncTask.class.getName(), "Rss-feed found:" + feed.getRssItems().size());

            final FermiAppDbHelper db = new FermiAppDbHelper(activity);
            try {
                res = db.runInTransaction(new FermiAppDbHelperCallable<List<NewsDB>>() {
                    @Override
                    public List<NewsDB> call(DaoSession session, Context ctx) throws Throwable {
                        ManagerNews m = new ManagerNews(session);
                        m.sincronizzaLista(feed.getRssItems());
                        return m.listAllNews();
                    }
                });
            } finally {
                db.close();
            }


        } catch (Throwable e) {
            if (con != null)
                con.disconnect();

            e.printStackTrace();
            throw new IllegalArgumentException(e);
        }
        return res;

    }

    private static C_JSonCircolariDeltaServletRequest getRequestData(AbstractActivity activity) {
        final C_JSonCircolariDeltaServletRequest req = new C_JSonCircolariDeltaServletRequest();
        final FermiAppDbHelper db = new FermiAppDbHelper(activity);
        try {
            db.runInTransaction(new FermiAppDBHelperRun() {
                @Override
                public void run(DaoSession session, Context ctx) throws Throwable {
                    ManagerCircolare m = new ManagerCircolare(session);
                    req.responseInZipFormat = true;
                    req.maxToken = m.maxToken();
                }
            });

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            db.close();
        }
        return req;
    }

    public static ExternalDataSync_Container syncAll(AbstractActivity activity) {
        ExternalDataSync_Container ris = new ExternalDataSync_Container();
        boolean error = false;
        try {
            ris.setNotizie(syncNews(activity));
        } catch (Throwable throwable) {
            error = true;
            ris.setErrorNotizie(throwable);
        }

        try {
            ris.setCircolari(syncCircolari(activity));
        } catch (Throwable throwable) {
            error = true;
            ris.setErrorCircolari(throwable);
        }

        if (!error) {
            activity.getSharedPreferences().setDataUltimoDownloadDati(new Date());
        }
        return ris;
    }

    @Override
    protected final ExternalDataSync_Container doInBackground(Void... params) {
        return syncAll(activity);

    }

    public static class ExternalDataSync_Container {
        private List<NewsDB> notizie;
        private List<CircolareDB> circolari;
        private Throwable errorNotizie;
        private Throwable errorCircolari;

        public ExternalDataSync_Container() {
        }

        public Throwable composeError() {
            if (errorNotizie == null)
                return errorCircolari;
            if (errorCircolari == null)
                return errorNotizie;
            return new IllegalArgumentException(errorNotizie.getMessage() + " - " + errorCircolari.getMessage(), errorCircolari);
        }

        public boolean containsErrors() {
            return errorCircolari != null || errorNotizie != null;
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

        public Throwable getErrorNotizie() {
            return errorNotizie;
        }

        void setErrorNotizie(Throwable errorNotizie) {
            this.errorNotizie = errorNotizie;
        }

        public Throwable getErrorCircolari() {
            return errorCircolari;
        }

        void setErrorCircolari(Throwable errorCircolari) {
            this.errorCircolari = errorCircolari;
        }
    }
}

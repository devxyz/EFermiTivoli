package it.gov.fermitivoli.cache;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;
import it.gov.fermitivoli.dao.*;
import it.gov.fermitivoli.model.C_NormalizedURL;
import it.gov.fermitivoli.util.C_DateUtil;
import it.gov.fermitivoli.util.DebugUtil;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by stefano on 13/04/15.
 */

public class UrlFileCache {
    private final int N_THREAD;
    //mappa tra url e loader in attesa

    private final File cacheDir;

    /**
     * database gestito dalla classe (metodo closeAsync)
     */
    private final FermiAppDbHelper db;
    private final __WorkingUrlTasks workingTasks = new __WorkingUrlTasks(this);
    protected Activity context;
    private ExecutorService __downloadExecutorService;

    public UrlFileCache(Activity context, int threadNumber) {
        this.context = context;


        //Find the dir to save cached images
        File __cacheDir = null;
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            __cacheDir = context.getExternalCacheDir();

        if (__cacheDir == null)
            __cacheDir = context.getCacheDir();


        this.cacheDir = new File(__cacheDir, "_file_cache_folder");

        if (!cacheDir.exists())
            cacheDir.mkdirs();

        N_THREAD = threadNumber;

        db = new FermiAppDbHelper(context);

        //cancella download pendenti e non completati
        try {
            __deleteFilesByCondition(CacheFileDBDao.Properties.Load_complete.eq(false));
        } catch (Throwable throwable) {
            throw new IllegalArgumentException(throwable);
        }
    }

    public UrlFileCache(Activity context) {
        this(context, 3);
    }

    public void cancel(AsyncUrlLoaderCallback callback) {
        workingTasks.cancel(callback);
    }

    public synchronized void cancelAll() {
        workingTasks.cancelAll();
    }

    private ExecutorService __downloadExecutorService() {
        if (__downloadExecutorService == null) {
            __downloadExecutorService = Executors.newFixedThreadPool(N_THREAD);
        }
        return __downloadExecutorService;
    }

    public synchronized File[] getFiles() {
        return cacheDir.listFiles();
    }

    public synchronized Map<String, String> getReport() {

        try {
            final Map<String, String> report = new TreeMap<String, String>();
            db.runInTransaction(new FermiAppDBHelperRun() {
                @Override
                public void run(DaoSession session, Context ctx) throws Throwable {
                    CacheFileDBDao dao = session.getCacheFileDBDao();
                    List<CacheFileDB> list = dao.queryBuilder().list();
                    for (CacheFileDB c : list) {
                        File f = __composeFilename(c);
                        report.put(
                                "#" + c.getId() + " " + c.getFilename(),
                                "<html>Name:" + " " + c.getFilename() + "<br>" +
                                        "ID:" + c.getId() + "<br>" +
                                        "Date:" + c.getInsertion_date() + "<br>" +
                                        "Url:" + c.getUrl() + "<br>" +
                                        "Complete (T/F): " + c.getLoad_complete() + "<br>" +
                                        (f.exists() ? ("Size:" + f.length() / 1024 + " KB<br>") : "File not exists<br>") +
                                        "</html>"
                        );
                    }
                }
            });
            return report;
        } catch (Throwable throwable) {
            throw new IllegalArgumentException(throwable);
        }
    }

    private synchronized void __deleteFilesByCondition(final WhereCondition condition, final WhereCondition... condition2) throws Throwable {
        final WhereCondition condition1 = condition;
        db.runInTransaction(new FermiAppDBHelperRun() {

            @Override
            public void run(DaoSession session, Context ctx) throws Throwable {
                final List<File> ris = new ArrayList<File>();
                final CacheFileDBDao c = session.getCacheFileDBDao();

                //elenca i file pendenti
                {

                    QueryBuilder<CacheFileDB> cc = condition1 == null ? c.queryBuilder() : c.queryBuilder().where(condition1, condition2);
                    cc.build();
                    for (CacheFileDB CacheFileDB : cc.list()) {
                        ris.add(__composeFilename(CacheFileDB));
                    }
                }

                //li rimuove
                {
                    session.clear();//svuota cache per fix dati
                    QueryBuilder<CacheFileDB> cc = condition1 == null ? c.queryBuilder() : c.queryBuilder().where(condition1, condition2);
                    cc.build();
                    DeleteQuery<CacheFileDB> deleteQuery = cc.buildDelete();
                    deleteQuery.executeDeleteWithoutDetachingEntities();

                }

                //cancella i rispettivi files
                for (File ri : ris) {
                    ri.delete();
                }
            }
        });
    }

    public synchronized File __composeFilename(long id, String filename) {
        return new File(cacheDir, id + "_" + filename);
    }

    public synchronized File __composeFilename(CacheFileDB f) {
        return __composeFilename(f.getId(), f.getFilename());
    }

    /**
     * rimuove tutti i file inseriti prima della data specificata e completati
     */
    public synchronized void clear(Date expireDate) {

        //cancella file temporanei nel db
        try {
            WhereCondition c1 = CacheFileDBDao.Properties.Insertion_date.le(expireDate);
            WhereCondition c2 = CacheFileDBDao.Properties.Load_complete.eq(true);
            __deleteFilesByCondition(c1, c2);

        } catch (Throwable throwable) {
            throw new IllegalArgumentException();
        }
    }

    public synchronized void clear() {

        //cancella file temporanei nel db
        try {
            WhereCondition c2 = CacheFileDBDao.Properties.Load_complete.eq(true);
            __deleteFilesByCondition(c2);

        } catch (Throwable throwable) {
            throw new IllegalArgumentException();
        }
    }


    public synchronized void closeAsync() {
        //cancella tutti i task
        workingTasks.cancelAll();

        //chiude i thread
        if (__downloadExecutorService != null) {
            __downloadExecutorService.shutdownNow();
        }

        //chiude il db
        db.close();
    }


    /**
     * @param url
     * @param callback
     * @param oldTime
     */
    public <T extends AsyncUrlLoaderCallback> T downloadFileAsync(final C_NormalizedURL url, int oldTime, TimeUnit unit, final T callback) {
        long minuti = unit.toMinutes(oldTime);
        System.out.println("Minuti in meno: " + minuti);
        __downloadFileAsync(url, C_DateUtil.sottraiMinuti(new Date(), (int) Math.max(1, minuti)), callback);
        return callback;
    }

    protected synchronized void __updateDB_downloadCompleted(final C_NormalizedURL url) {
        try {
            db.runInTransaction(new FermiAppDBHelperRun() {
                @Override
                public void run(DaoSession session, Context ctx) throws Throwable {
                    //controlla se il file e' gia' presente e scaricato
                    CacheFileDBDao d1 = session.getCacheFileDBDao();
                    QueryBuilder<CacheFileDB> q = d1.queryBuilder().where(CacheFileDBDao.Properties.Url.eq(url));
                    CacheFileDB ris = q.unique();
                    if (ris != null) {
                        ris.setLoad_complete(true);
                        //aggiornamento cache
                        session.getCacheFileDBDao().update(ris);
                    }
                    workingTasks.removeWorkingTask(url);
                }
            });
        } catch (Throwable throwable) {
            throw new IllegalArgumentException(throwable);
        }
    }

    protected synchronized void __updateDB_downloadError(final C_NormalizedURL url) {
        try {
            db.runInTransaction(new FermiAppDBHelperRun() {
                @Override
                public void run(DaoSession session, Context ctx) throws Throwable {
                    //controlla se il file e' gia' presente e scaricato
                    CacheFileDBDao d1 = session.getCacheFileDBDao();
                    QueryBuilder<CacheFileDB> q = d1.queryBuilder().where(CacheFileDBDao.Properties.Url.eq(url));
                    CacheFileDB ris = q.unique();
                    if (ris != null)
                        session.delete(ris);
                    workingTasks.removeWorkingTask(url);
                }
            });
        } catch (Throwable throwable) {
            throw new IllegalArgumentException(throwable);
        }
    }

    /**
     * ritorna il file associato alla url, riscaricandolo se e' presente in cache da prima della data specificata
     *
     * @param url
     * @param callback
     * @param downloadIfBeforeDate
     */
    private synchronized void __downloadFileAsync(final C_NormalizedURL url, final Date downloadIfBeforeDate, final AsyncUrlLoaderCallback callback) {
        //controlla se dati connessi
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetworkInfo = connManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && !activeNetworkInfo.isConnected()) {
            callback.onLoadError(url, new NetworkErrorException("Not connected"));
            return;
        }


        try {
            //elenco di task da eseguire al termine dell'elaborazione che vedono essere eseguiti fuori dalla transazione del db
            final ArrayList<Runnable> todo = new ArrayList<Runnable>();

            db.runInTransaction(new FermiAppDBHelperRun() {
                @Override
                public void run(DaoSession session, Context ctx) throws Throwable {


                    if (DebugUtil.DEBUG__UrlFileCache) {
                        Log.d("URL-FILE-CACHE", "SUBMIT " + url);
                    }

                    //se e' gia' un task in lavorazione, lo aggiungo
                    {
                        final __UrlTaskDownloadFile urlTask = workingTasks.searchWorkingTask(url);
                        if (urlTask != null) {
                            //aggiunge il task alla coda
                            urlTask.addTask(callback);
                            //notifica_nuove_notizie accodamento
                            if (DebugUtil.DEBUG__UrlFileCache) {
                                Log.d("URL-FILE-CACHE", "QUEUED EXISTING TASK " + url);
                            }

                            //da eseguire fuori dalla transazione
                            todo.add(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onQueueForDownload(url);
                                }
                            });

                            return;
                        }
                    }

                    //controlla se il file e' gia' presente e scaricato
                    CacheFileDBDao d1 = session.getCacheFileDBDao();
                    QueryBuilder<CacheFileDB> q = d1.queryBuilder().where(CacheFileDBDao.Properties.Url.eq(url));
                    CacheFileDB ris = q.unique();

                    //controlla se esistente
                    if (ris != null) {
                        if (DebugUtil.DEBUG__UrlFileCache) {
                            Log.d("URL-FILE-CACHE", "FOUND in database " + ris.getFilename() + " " + url);
                        }

                        //controlla se il file e' presente e la data non e' scaduta
                        final File f = __composeFilename(ris);

                        //se file cancellato o la data di inserimento e' precedente alla data limite oppure non e' caricato (e nemmeno programmato)
                        // si annulla la riga...
                        if (!f.exists() || ris.getInsertion_date().compareTo(downloadIfBeforeDate) < 0 || !ris.getLoad_complete()) {
                            if (DebugUtil.DEBUG__UrlFileCache) {
                                Log.d("URL-FILE-CACHE", "REMOVED from database because is too old. Insertion date: " + ris.getInsertion_date() + ", max date: " + downloadIfBeforeDate + " " + url);
                            }
                            session.delete(ris);
                            f.delete();
                            ris = null;
                        } else {
                            if (DebugUtil.DEBUG__UrlFileCache) {
                                Log.d("URL-FILE-CACHE", "FOUND " + ris.getFilename() + " " + url);
                            }
                            final CacheFileDB finalRis = ris;

                            //da eseguire fuori dalla transazione
                            todo.add(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onLoadFinished(url, __composeFilename(finalRis));
                                }
                            });

                            return;
                        }
                    }

                    //sono qui se il file non e' stato trovato o era scaduto...
                    //In questo caso si deve generare un nuovo task di lavoro
                    //nuova richiesta
                    {
                        final CacheFileDB a = new CacheFileDB();
                        a.setFilename(__extractFileName(url));
                        a.setInsertion_date(new Date());
                        a.setLoad_complete(false);
                        a.setUrl(url.getUrl());
                        long id = d1.insert(a);
                        final File f = __composeFilename(id, a.getFilename());
                        __UrlTaskDownloadFile ut = workingTasks.addWorkingTask(url, f);
                        if (DebugUtil.DEBUG__UrlFileCache) {
                            Log.d("URL-FILE-CACHE", "QUEUED IN NEW TASK " + callback.getClass() + " #### " + url);
                        }
                        ut.addTask(callback);
                        __downloadExecutorService().submit(ut);
                    }
                }
            });

            //esegue i task conclusivi
            for (Runnable r : todo) {
                r.run();
            }


        } catch (Throwable ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    private String __extractFileName(C_NormalizedURL url) {
        String[] split = url.getUrl().split("/");
        String[] split2 = split[split.length - 1].split("\\?");
        return split2[0];
    }


    private class FileCallable implements Callable<File> {
        private final C_NormalizedURL url;
        private final int oldTime;
        private final TimeUnit unit;
        private final CountDownLatch waitLatch;

        private volatile File result = null;
        private volatile Throwable resultError = null;
        private volatile Future<File> submit = null;

        public FileCallable(C_NormalizedURL url, int oldTime, TimeUnit unit) {
            this.url = url;
            this.oldTime = oldTime;
            this.unit = unit;
            waitLatch = new CountDownLatch(1);
        }

        public void setSubmit(Future<File> submit) {
            this.submit = submit;
        }

        @Override
        public File call() throws Exception {

            downloadFileAsync(url, oldTime, unit, new AsyncUrlLoaderCallback() {
                @Override
                public void onLoadFinished(C_NormalizedURL url, File f) {
                    result = f;
                    waitLatch.countDown();
                }

                @Override
                public void onLoadError(C_NormalizedURL url, Throwable error) {
                    resultError = error;
                    waitLatch.countDown();
                }

                @Override
                public void onQueueForDownload(C_NormalizedURL url) {
                    //do nothing
                }

                @Override
                public void onCancelled(C_NormalizedURL url) {
                    waitLatch.countDown();
                }

                @Override
                public boolean isCancelled() {
                    if (submit != null)
                        return submit.isCancelled();
                    return false;
                }
            });
            //attende una risposta
            waitLatch.await();

            //se cancellato annulla
            if (submit != null && submit.isCancelled())
                return null;

            //se errore
            if (resultError != null) {
                if (resultError instanceof Exception)
                    throw (Exception) resultError;
                else
                    throw new Exception(resultError);
            }

            return result;
        }
    }
}
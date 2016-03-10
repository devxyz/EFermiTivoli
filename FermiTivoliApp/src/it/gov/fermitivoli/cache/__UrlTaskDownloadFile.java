package it.gov.fermitivoli.cache;

import android.util.Log;
import it.gov.fermitivoli.model.C_NormalizedURL;
import it.gov.fermitivoli.util.DebugUtil;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by stefano on 05/06/15.
 */
class __UrlTaskDownloadFile implements Runnable {
    protected final C_NormalizedURL url;
    private final ArrayList<AsyncUrlLoaderCallback> task;
    private UrlFileCache urlFileCache2;
    private File file;
    /**
     * true se cancellato
     */
    private volatile boolean flagCancelled = false;
    private volatile boolean executed = false;
    /**
     * true se eseguito metodo di callback
     */
    private volatile boolean executedCallback = false;

    __UrlTaskDownloadFile(UrlFileCache urlFileCache2, C_NormalizedURL url, File file) {
        this.urlFileCache2 = urlFileCache2;
        this.url = url;
        this.file = file;
        task = new ArrayList<AsyncUrlLoaderCallback>();
    }

    public boolean contains(AsyncUrlLoaderCallback callback) {
        return task.contains(callback);
    }

    public int size() {
        return task.size();
    }

    public void removeTask(AsyncUrlLoaderCallback t) {
        task.remove(t);
    }

    public void addTask(AsyncUrlLoaderCallback t) {
        if (executed) throw new IllegalArgumentException("Task closed");
        //if (task.size() > 0)
        task.add(t);

        onQueueForDownload();

    }

    public void cancel() {
        if (executed) return;
        if (flagCancelled) return;
        flagCancelled = true;
        onCancelled();
    }

    private boolean isCancelled() {
        if (flagCancelled) return true;
        for (AsyncUrlLoaderCallback t : task) {
            if (!t.isCancelled()) return false;
        }
        return true;
    }

    private void onLoadError(final Throwable ex) {
        //controlla se metodo di callback gia' eseguito (evita di eseguirlo piu' volte)
        if (this.executedCallback)
            return;
        executedCallback = true;

        if (DebugUtil.DEBUG__UrlTaskDownloadFile)
            Log.d("URL_TASK", "onLoadError " + task.size() + " listener");
        for (AsyncUrlLoaderCallback t : task) {
            if (!t.isCancelled())
                t.onLoadError(url, ex);
            else
                t.onCancelled(url);
        }
    }

    private void onLoadFinished(final File f) {
        //controlla se metodo di callback gia' eseguito (evita di eseguirlo piu' volte)
        if (this.executedCallback)
            return;
        executedCallback = true;

        Log.d(this.getClass().getName(), "onLoadFinished" + task.size() + " listener");
        for (AsyncUrlLoaderCallback t : task) {
            if (!t.isCancelled())
                t.onLoadFinished(url, f);
            else
                t.onCancelled(url);
        }
    }

    private void onQueueForDownload() {
        //pu' essere eseguito molte volte....
        for (AsyncUrlLoaderCallback t : task) {
            if (!t.isCancelled())
                t.onQueueForDownload(url);
        }
        if (DebugUtil.DEBUG__UrlTaskDownloadFile)
            Log.d("URL_TASK", "onQueueForDownload " + task.size() + " listener");

    }

    private void onCancelled() {
        //controlla se metodo di callback gia' eseguito (evita di eseguirlo piu' volte)
        if (this.executedCallback)
            return;
        executedCallback = true;

        if (DebugUtil.DEBUG__UrlTaskDownloadFile)
            Log.d("URL_TASK", "onCancelled" + task.size() + " listener");
        for (AsyncUrlLoaderCallback t : task) {
            if (t.isCancelled())
                t.onCancelled(url);
        }
    }

    @Override
    public void run() {
        if (isCancelled()) return;

        if (DebugUtil.DEBUG__UrlTaskDownloadFile) {
            Log.d("URL-TASK(run method)", "START " + url);
        }
        try {
            boolean result = __copy(url, file);
            if (DebugUtil.DEBUG__UrlTaskDownloadFile) {
                Log.d("URL-TASK(run method)", "DOWNLOADED " + url);
            }
            if (isCancelled()) return;
            if (!result)
                throw new IOException("Task non terminato per motivi sconosciuti");
            urlFileCache2.__updateDB_downloadCompleted(url);
            if (DebugUtil.DEBUG__UrlTaskDownloadFile) {
                Log.d("URL-TASK(run method)", "DB UPDATED (cache) " + url);
            }
            if (isCancelled()) return;
            onLoadFinished(file);
            if (DebugUtil.DEBUG__UrlTaskDownloadFile) {
                Log.d("URL-TASK(run method)", "FINISHED SUCC. " + url);
            }

        } catch (Throwable ex) {

            if (DebugUtil.DEBUG__UrlTaskDownloadFile) {
                Log.d("URL-TASK(run method)", "ERROR " + url);
            }
            if (isCancelled()) {
                if (DebugUtil.DEBUG__UrlTaskDownloadFile) {
                    Log.d("URL-TASK(run method)", "CANCELLED " + url);
                }
                return;
            }

            ex.printStackTrace();
            if (DebugUtil.DEBUG__UrlTaskDownloadFile) {
                Log.d("URL-TASK(run method)", "ERROR " + url);
            }

            urlFileCache2.__updateDB_downloadError(url);
            onLoadError(ex);
            if (DebugUtil.DEBUG__UrlTaskDownloadFile) {
                Log.d("URL-TASK(run method)", "ERROR FINISHED " + url);
            }

        }
    }

    private synchronized boolean __copy(C_NormalizedURL url, File f) throws IOException {
        if (isCancelled()) return false;
        URL u = new URL(url.getUrl());
        HttpURLConnection conn = (HttpURLConnection) u.openConnection();
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(5000);
        conn.setInstanceFollowRedirects(true);

        if (conn == null)
            throw new NullPointerException("Null connection");
        if (conn.getResponseCode() != 200)
            throw new IOException("Error code: " + conn.getResponseCode() + " " + conn.getResponseMessage());

        InputStream is = conn.getInputStream();
        OutputStream os = new FileOutputStream(f);
        try {
            final int buffer_size = 16 * 1024;

            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
                if (isCancelled()) {
                    return false;
                }
            }

        } catch (IOException e) {
            //chiude gli stream e cancella il file
            conn.disconnect();
            os.close();
            is.close();
            f.delete();
            throw e;
        } finally {
            executed = true;
            conn.disconnect();
            os.close();
            is.close();

        }

        //chiusura stream
        conn.disconnect();
        os.close();
        is.close();
        return true;
    }
}

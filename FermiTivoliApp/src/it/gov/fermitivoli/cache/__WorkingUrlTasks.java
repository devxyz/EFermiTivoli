package it.gov.fermitivoli.cache;

import it.gov.fermitivoli.model.C_NormalizedURL;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by stefano on 05/06/15.
 */
class __WorkingUrlTasks {
    private final ArrayList<__UrlTaskDownloadFile> workingTasks = new ArrayList<__UrlTaskDownloadFile>();
    private UrlFileCache urlFileCache2;

    public __WorkingUrlTasks(UrlFileCache urlFileCache2) {
        this.urlFileCache2 = urlFileCache2;
    }

    public __UrlTaskDownloadFile searchWorkingTask(C_NormalizedURL url) {
        for (__UrlTaskDownloadFile w : workingTasks) {
            if (w.url.equals(url))
                return w;
        }
        return null;
    }

    public __UrlTaskDownloadFile addWorkingTask(C_NormalizedURL url, File f) {
        __UrlTaskDownloadFile u = searchWorkingTask(url);
        if (u != null) return u;
        u = new __UrlTaskDownloadFile(urlFileCache2, url, f);
        workingTasks.add(u);
        return u;
    }


    public __UrlTaskDownloadFile removeWorkingTask(C_NormalizedURL url) {
        for (Iterator<__UrlTaskDownloadFile> iterator = workingTasks.iterator(); iterator.hasNext(); ) {
            __UrlTaskDownloadFile w = iterator.next();
            if (w.url.equals(url)) {
                iterator.remove();
                break;
            }
        }
        return null;
    }

    public List<__UrlTaskDownloadFile> getWorkingTasks() {
        return Collections.unmodifiableList(workingTasks);
    }

    public void cancel(AsyncUrlLoaderCallback callback) {
        for (__UrlTaskDownloadFile w : workingTasks) {
            if (w.contains(callback)) {
                w.removeTask(callback);
                if (w.size() == 0)
                    w.cancel();
                return;
            }
        }

    }

    public void cancelAll() {
        for (__UrlTaskDownloadFile w : workingTasks) {
            w.cancel();
        }
        workingTasks.clear();
    }
}

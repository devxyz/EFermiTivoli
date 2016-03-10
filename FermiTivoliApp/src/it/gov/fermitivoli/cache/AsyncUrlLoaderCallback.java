package it.gov.fermitivoli.cache;

import it.gov.fermitivoli.model.C_NormalizedURL;

import java.io.File;

/**
 *xz interfaccia di callback
 */
public interface AsyncUrlLoaderCallback {
    public void onLoadFinished(C_NormalizedURL url, File f);

    public void onLoadError(C_NormalizedURL url, Throwable error);

    public void onQueueForDownload(C_NormalizedURL url);

    public void onCancelled(C_NormalizedURL url);

    /**
     * true if task is cancelled
     *
     * @return
     */
    public boolean isCancelled();
}

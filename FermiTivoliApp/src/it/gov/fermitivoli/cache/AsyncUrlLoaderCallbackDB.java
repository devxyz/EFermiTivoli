package it.gov.fermitivoli.cache;

import android.content.Context;
import it.gov.fermitivoli.api.AbstractActivity;
import it.gov.fermitivoli.dao.DaoSession;
import it.gov.fermitivoli.dao.FermiAppDBHelperRun;
import it.gov.fermitivoli.dao.FermiAppDbHelper;
import it.gov.fermitivoli.model.C_NormalizedURL;

import java.io.File;

/**
 * Created by stefano on 26/05/15.
 */
public abstract class AsyncUrlLoaderCallbackDB implements AsyncUrlLoaderCallback {
    private volatile boolean flagCancel = false;

    protected final AbstractActivity ctx;

    public AsyncUrlLoaderCallbackDB(AbstractActivity ctx) {
        this.ctx = ctx;
    }


    @Override
    public final void onCancelled(final C_NormalizedURL url) {
        onCancelImpl(url);
    }

    @Override
    public boolean isCancelled() {
        return flagCancel;
    }

    protected abstract void onLoadFinishedImpl(C_NormalizedURL url, File f, DaoSession session) throws Throwable;

    protected abstract void onLoadErrorImpl(C_NormalizedURL url, Throwable error);

    protected abstract void onQueueForDownloadImpl(C_NormalizedURL url);

    protected abstract void onCancelImpl(C_NormalizedURL url);

    @Override
    public final void onLoadFinished(final C_NormalizedURL url, final File f) {


        FermiAppDbHelper db = new FermiAppDbHelper(ctx.getActivity());
        try {
            db.runInTransaction(new FermiAppDBHelperRun() {
                @Override
                public void run(DaoSession session, Context ctx) throws Throwable {
                    onLoadFinishedImpl(url, f, session);
                }
            });

        } catch (Throwable ex) {
            db.close();
            onLoadError(url, ex);
        } finally {
            db.close();
        }
    }

    @Override
    public final void onLoadError(final C_NormalizedURL url, final Throwable error) {
        onLoadErrorImpl(url, error);
    }

    @Override
    public final void onQueueForDownload(final C_NormalizedURL url) {
        onQueueForDownloadImpl(url);
    }

}

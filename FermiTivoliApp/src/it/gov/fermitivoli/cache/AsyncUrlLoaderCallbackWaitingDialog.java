package it.gov.fermitivoli.cache;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.util.Log;
import it.gov.fermitivoli.api.AbstractActivity;
import it.gov.fermitivoli.listener.OnClickListenerDialogErrorCheck;
import it.gov.fermitivoli.model.C_NormalizedURL;
import it.gov.fermitivoli.util.DialogUtil;

import java.io.File;

/**
 * Created by stefano on 26/05/15.
 */
public abstract class AsyncUrlLoaderCallbackWaitingDialog implements AsyncUrlLoaderCallback {
    private volatile boolean flagCancel = false;
    private ProgressDialog p;
    private AbstractActivity ctx;

    public AsyncUrlLoaderCallbackWaitingDialog(AbstractActivity ctx) {
        this.ctx = ctx;
    }


    @Override
    public final void onCancelled(final C_NormalizedURL url) {
        ctx.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (p != null) {
                    p.dismiss();
                    p = null;
                }
                onCancelImpl(url);
            }
        });

    }

    @Override
    public boolean isCancelled() {
        return flagCancel;
    }

    protected abstract void onLoadFinishedImpl(C_NormalizedURL url, File f);

    protected abstract void onLoadErrorImpl(C_NormalizedURL url, Throwable error);

    protected abstract void onQueueForDownloadImpl(C_NormalizedURL url);

    protected abstract void onCancelImpl(C_NormalizedURL url);

    protected abstract String getWaitingTitle(C_NormalizedURL url);

    protected abstract String getWaitingMessage(C_NormalizedURL url);

    @Override
    public final void onLoadFinished(final C_NormalizedURL url, final File f) {

        ctx.runOnUiThreadBlocking(new Runnable() {
            @Override
            public void run() {
                if (p != null) {
                    p.dismiss();
                    p = null;
                }
            }
        });
        onLoadFinishedImpl(url, f);
    }

    @Override
    public final void onLoadError(final C_NormalizedURL url, final Throwable error) {
        ctx.runOnUiThreadBlocking(new Runnable() {
            @Override
            public void run() {
                if (p != null) {
                    p.dismiss();
                    p = null;
                }
            }
        });
        onLoadErrorImpl(url, error);
    }

    @Override
    public final void onQueueForDownload(final C_NormalizedURL url) {
        Log.d("ASYNCTASK_DIALOG", "OPEN DIALOGGGGGG");
        if (getWaitingTitle(url) != null || getWaitingMessage(url) != null) {
            ctx.runOnUiThreadBlocking(new Runnable() {
                @Override
                public void run() {
                    // Toast.makeText(getMainActivity(), "Download in corso --- COMPLETARE", Toast.LENGTH_SHORT).show();
                    p = DialogUtil.openProgressDialog(ctx.getActivity(), getWaitingTitle(url), getWaitingMessage(url), "Annulla", new OnClickListenerDialogErrorCheck(ctx) {
                        @Override
                        protected void onClickImpl(DialogInterface dialog, int which) throws Throwable {
                            flagCancel = true;
                            //dialog.dismiss();
                            //p = null;

                            //esegue l'flagCancel immediatamente al click
                            //onCancelImpl(url);
                        }
                    },null);

                }
            });
        }
        onQueueForDownloadImpl(url);
    }

}

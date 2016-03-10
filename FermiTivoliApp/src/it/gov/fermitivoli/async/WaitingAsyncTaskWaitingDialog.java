package it.gov.fermitivoli.async;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.KeyEvent;
import it.gov.fermitivoli.api.AbstractActivity;
import it.gov.fermitivoli.listener.OnClickListenerDialogErrorCheck;
import it.gov.fermitivoli.util.DialogUtil;

/**
 * Created by stefano on 12/03/15.
 */
public abstract class WaitingAsyncTaskWaitingDialog<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    private final AbstractActivity cx;
    protected boolean flagCancelled = false;
    private Throwable lastError;
    private ProgressDialog progress = null;
    private boolean cancelButtonEnabled = true;

    public WaitingAsyncTaskWaitingDialog(AbstractActivity cx) {
        this.cx = cx;
    }

    private void setCancellableButton(boolean status) {
        cancelButtonEnabled = status;
    }

    protected ProgressDialog getProgress() {
        return progress;
    }

    protected abstract String getDialogTitle();

    protected abstract String getDialogMessage();

    @Override
    protected final void onPreExecute() {
        //GUI THREAD
        super.onPreExecute();
        Log.d("WaitingAsyncTaskWaiting", "onPreExecute");

        lastError = null;

        //progressbar solo se messaggio / titolo presenti
        if (getDialogMessage() != null || getDialogTitle() != null) {

            if (cancelButtonEnabled) {
                progress = DialogUtil.openProgressDialog(cx.getActivity(), getDialogTitle(), getDialogMessage(),"Annulla",
                        new OnClickListenerDialogErrorCheck(cx) {
                            @Override
                            public void onClickImpl(DialogInterface dialog, int which) {
                                if (flagCancelled) return;
                                flagCancelled = true;
                                Log.d("WaitingAsyncTaskWaiting", "cancel");
                                cancel(true);

                                Runnable r = new Runnable() {
                                    @Override
                                    public void run() {
                                        onCancelPressedRunUI();
                                    }
                                };
                                cx.getActivity().runOnUiThread(r);
                            }
                        }, new DialogInterface.OnKeyListener() {
                            @Override
                            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                if (keyCode == KeyEvent.KEYCODE_BACK) {
                                }
                                return true;
                            }
                        });
                progress.setCanceledOnTouchOutside(false);
            } else
                progress = DialogUtil.openProgressDialog(cx.getActivity(), getDialogTitle(), getDialogMessage(),"Annulla", null,null);
        }

        onPreExecuteRunUI();
    }

    protected void onPreExecuteRunUI() {
    }

    protected abstract void onPostExecuteRunUI_OnSuccess(Result result);

    protected abstract void onPostExecuteRunUI_OnError(Throwable error);

    @Override
    protected final void onPostExecute(Result result) {
        //GUI THREAD

        Log.d("WaitingAsyncTaskWaiting", "onPostExecute");
        super.onPostExecute(result);
        if (progress != null) {
            progress.dismiss();
        }

        if (lastError == null)
            onPostExecuteRunUI_OnSuccess(result);
        else {
            onPostExecuteRunUI_OnError(lastError);
            lastError = null;
        }
    }

    @Override
    protected final void onProgressUpdate(Progress... values) {
        //GUI THREAD

        super.onProgressUpdate(values);
        onProgressUpdateRunUI(values);
    }

    protected void onProgressUpdateRunUI(Progress... values) {

    }

    @Override
    protected final void onCancelled(Result result) {
        Log.d("WaitingAsyncTaskWaiting", "onCancelled(Result)");
        new Throwable().printStackTrace(System.out);
        cx.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("WaitingAsyncTaskWaiting", "XXXXXXXXXXXXX");
                progress.dismiss();
            }
        });
        Log.d("WaitingAsyncTaskWaiting", "EEEEEEEEEEEE");
        onCancelledRunUI(result);
        Log.d("WaitingAsyncTaskWaiting", "YYYYYYYYYYYY");
    }

    //cancellato con risultato
    protected abstract void onCancelledRunUI(Result result);

    //cancellato senza risultato
    protected abstract void onCancelledRunUI();

    //annullamento premuto (eseguito nel thread ui)
    protected final void onCancelPressedRunUI() {

    }

    @Override
    protected final void onCancelled() {
        cx.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("WaitingAsyncTaskWaiting", "onCancelled()");
                new Throwable().printStackTrace(System.out);
                progress.dismiss();
                onCancelledRunUI();
            }
        });

    }

    @Override
    protected final Result doInBackground(Params... params) {
        try {
            return doInBackgroundImpl(params);
        } catch (Throwable e) {
            lastError = e;
            return null;
        }
    }

    protected abstract Result doInBackgroundImpl(Params... params) throws Throwable;
}

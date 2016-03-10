package it.gov.fermitivoli.listener;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import it.gov.fermitivoli.util.DebugUtil;
import it.gov.fermitivoli.util.DialogUtil;

/**
 * Created by stefano on 19/03/15.
 */
public abstract class OnClickListenerViewErrorCheck implements View.OnClickListener {
    protected final Activity activity;

    public OnClickListenerViewErrorCheck(Activity context) {
        this.activity = context;
    }

    @Override
    public final void onClick(View v) {
        try {
            onClickImpl(v);
        } catch (Throwable throwable) {
            try {
                onErrorImpl(v, throwable);
            } catch (Throwable e) {

            }
        }
    }

    /**
     * implementazione base in caso di errore
     *
     * @param v
     * @param ex
     */
    protected void onErrorImpl(View v, final Throwable ex) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DialogUtil.openErrorDialog(activity, "Errore", "Errore durante l'esecuzione dell'operazione", ex);
                if (DebugUtil.DEBUG__OnClickListenerViewErrorCheck)
                    Log.d("CLICK_LISTENER", "Error", ex);

            }
        });
    }

    protected abstract void onClickImpl(View v) throws Throwable;
}

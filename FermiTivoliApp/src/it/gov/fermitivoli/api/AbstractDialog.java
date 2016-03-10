package it.gov.fermitivoli.api;

import android.app.Dialog;
import android.os.AsyncTask;
import it.gov.fermitivoli.activity.MainMenuActivity;
import it.gov.fermitivoli.cache.AsyncUrlLoaderCallback;

import java.util.ArrayList;

/**
 * Created by stefano on 05/10/15.
 */
public class AbstractDialog extends Dialog {
    private final ArrayList<AsyncTask> task1 = new ArrayList<AsyncTask>();
    private final ArrayList<AsyncUrlLoaderCallback> task2 = new ArrayList<AsyncUrlLoaderCallback>();
    private AbstractActivity mainActivity;

    public AbstractDialog(AbstractActivity context) {
        super(context);
        mainActivity = context;
    }


    @Override
    protected void onStop() {
        super.onStop();
        cancelAllTask();
    }

    /**
     * aggiunge un task alla lista dei task asincroni
     */
    public <T extends AsyncUrlLoaderCallback> T addTask(T task) {
        task2.add(task);
        return task;
    }

    protected AbstractActivity getMainActivity() {
        return mainActivity;
    }

    protected int cancelAllTask() {
        //interrompe tutti i task non terminati
        int i = 0;
        for (AsyncTask task : task1) {
            task.cancel(true);
            i++;
        }
        //interrompe tutti i task non terminati
        for (AsyncUrlLoaderCallback task : task2) {
            getMainActivity().getCache().cancel(task);
            i++;
        }
        return i;
    }


}

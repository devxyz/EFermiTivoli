package it.gov.fermitivoli.api;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import it.gov.fermitivoli.cache.UrlFileCache;
import it.gov.fermitivoli.dao.FermiAppDbHelper;
import it.gov.fermitivoli.util.SharedPreferenceWrapper;
import it.gov.fermitivoli.util.ThreadUtil;

/**
 * Created by stefano on 27/05/15.
 */
public abstract class AbstractActivity extends Activity {
    private UrlFileCache cache;
    private FermiAppDbHelper database;
    private SharedPreferenceWrapper sharedpreferences;

    public AbstractActivity() {
    }

    public FermiAppDbHelper getDatabase() {
        return database;
    }

    public final SharedPreferenceWrapper getSharedPreferences() {
        return sharedpreferences;
    }

    public final Activity getActivity() {
        return this;
    }

    public final UrlFileCache getCache() {
        return cache;
    }

    public final void runOnUiThreadBlocking(Runnable r) {
        ThreadUtil.runOnUiThreadAndWait(this, r);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cache = new UrlFileCache(this);
        sharedpreferences = new SharedPreferenceWrapper(getSharedPreferences("fermi-tivoli", Context.MODE_PRIVATE));
        database = new FermiAppDbHelper(getActivity());

    }
}

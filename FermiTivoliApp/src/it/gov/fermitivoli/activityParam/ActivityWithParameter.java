package it.gov.fermitivoli.activityParam;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.io.Serializable;
import java.util.Map;

/**
 * Activity che gestisce in maniera autonoma un parametro (memorizzandolo, ricaricandolo ecc)
 */
@Deprecated
public abstract class ActivityWithParameter<T extends Serializable> extends Activity {
    public static final String PARAMETER_KEY = "#PARAMETER_KEY_ACTIVITY#";
    protected T parameter;

    protected T getParameter() {
        return parameter;
    }

    public <Q extends Serializable, C extends ActivityWithParameter<Q>> void startActivity(Class<C> intent, Q parameter) {
        startActivity(intent, parameter, null);
    }

    public <Q extends Serializable, C extends ActivityWithParameter<Q>> void startActivity(Class<C> intent, Q parameter, Map<String, Serializable> parameters) {
        ActivityParameterManager.startActivity(this, intent, parameter, parameters);
    }

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parameter = null;
        if (parameter == null && savedInstanceState != null) {
            parameter = (T) savedInstanceState.getSerializable(PARAMETER_KEY);
        }

        if (parameter == null) {
            Intent intent = getIntent();
            if (intent != null && intent.getExtras() != null)
                parameter = (T) intent.getExtras().getSerializable(PARAMETER_KEY);
        }

        //retrieve the parameter value
        onCreate0(savedInstanceState);
    }

    @Override
    protected final void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //save parameter
        if (parameter != null)
            outState.putSerializable(PARAMETER_KEY, parameter);
        onSaveInstanceState0(outState);
    }

    protected void onSaveInstanceState0(Bundle outState) {
    }

    ;

    protected void onCreate0(Bundle savedInstanceState) {
    }
}

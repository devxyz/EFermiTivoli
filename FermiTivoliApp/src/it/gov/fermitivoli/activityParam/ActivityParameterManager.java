package it.gov.fermitivoli.activityParam;

import android.content.Context;
import android.content.Intent;

import java.io.Serializable;
import java.util.Map;

/**
 * Gestione passaggio parametri tra activity/restore ecc
 */
@Deprecated
public class ActivityParameterManager {
    public static <Q extends Serializable, C extends ActivityWithParameter<Q>> void startActivity(Context ctx, Class<C> intent, Q parameter) {
        startActivity(ctx, intent, parameter, null);
    }

    public static <Q extends Serializable, C extends ActivityWithParameter<Q>> void startActivity(Context ctx, Class<C> intent, Q parameter, Map<String, Serializable> parameters) {
        Intent i = new Intent(ctx, intent);
        i.putExtra(ActivityWithParameter.PARAMETER_KEY, (Serializable) parameter);
        if (parameters != null)
            for (Map.Entry<String, Serializable> e : parameters.entrySet()) {
                i.putExtra(e.getKey(), (Serializable) e.getValue());
            }
        ctx.startActivity(i);
    }

}

package it.gov.fermitivoli.action;

import android.content.Intent;
import android.net.Uri;
import it.gov.fermitivoli.activity.MainMenuActivity;
import it.gov.fermitivoli.R;
import it.gov.fermitivoli.action.api.ActivityAction;
import it.gov.fermitivoli.model.menu.DataMenuInfo;

/**
 * Created by stefano on 06/04/15.
 */

public class CotralAction implements ActivityAction {
    @Override
    public void doTask(MainMenuActivity activity, DataMenuInfo item) {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(activity.getString(R.string.url_cotral_form_ricerca_orari)));
        activity.startActivity(i);
    }
}

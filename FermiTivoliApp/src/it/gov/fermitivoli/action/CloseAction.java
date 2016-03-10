package it.gov.fermitivoli.action;

import it.gov.fermitivoli.activity.MainMenuActivity;
import it.gov.fermitivoli.action.api.ActivityAction;
import it.gov.fermitivoli.model.menu.DataMenuInfo;

/**
 * Created by stefano on 06/04/15.
 */
public class CloseAction implements ActivityAction {

    @Override
    public void doTask(MainMenuActivity activity, DataMenuInfo item) {
        activity.finish();

    }
}

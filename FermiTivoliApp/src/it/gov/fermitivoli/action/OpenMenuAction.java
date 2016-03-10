package it.gov.fermitivoli.action;

import it.gov.fermitivoli.activity.MainMenuActivity;
import it.gov.fermitivoli.action.api.ActivityAction;
import it.gov.fermitivoli.model.menu.DataMenuInfo;

/**
 * Created by stefano on 06/04/15.
 */
public class OpenMenuAction implements ActivityAction {

    @Override
    public void doTask(MainMenuActivity activity, DataMenuInfo item) {
        activity.openMenu();

    }
}

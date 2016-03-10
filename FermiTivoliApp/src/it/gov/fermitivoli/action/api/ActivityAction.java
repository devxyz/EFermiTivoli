package it.gov.fermitivoli.action.api;

import it.gov.fermitivoli.activity.MainMenuActivity;
import it.gov.fermitivoli.model.menu.DataMenuInfo;

/**
 * Created by stefano on 06/04/15.
 */
public interface ActivityAction {
    public void doTask(MainMenuActivity activity, DataMenuInfo item);
}

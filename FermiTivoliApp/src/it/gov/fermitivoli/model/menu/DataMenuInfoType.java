package it.gov.fermitivoli.model.menu;

import it.gov.fermitivoli.R;

/**
 * Created by stefano on 09/09/15.
 */
public enum DataMenuInfoType {
    COMMAND, FRAGMENT, LABEL_ONLY;

    public static DataMenuInfoType search(Class<?> z) {
        if (z == null) return LABEL_ONLY;
        if (DataMenuInfo.isCommandClass(z)) return COMMAND;
        if (DataMenuInfo.isFragmentClass(z)) return FRAGMENT;


        throw new IllegalArgumentException("Invalid class " + z);
    }
}

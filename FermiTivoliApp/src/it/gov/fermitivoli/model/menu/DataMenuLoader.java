package it.gov.fermitivoli.model.menu;

import android.content.res.TypedArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stefano on 18/03/15.
 */
public class DataMenuLoader {
    public static List<DataMenuInfo> load(TypedArray navMenuInfo) {
        List<DataMenuInfo> ris = new ArrayList<DataMenuInfo>(navMenuInfo.length() / 5);
        if (navMenuInfo.length() % 7 != 0) {
            throw new IllegalArgumentException("Dimensioni del vettore dei menu non corretta: " + navMenuInfo.length() + ". Deve essere multipla di 6");
        }

        for (int i = 0; i < navMenuInfo.length(); ) {
            int menuID = Integer.parseInt(navMenuInfo.getString(i));
            i++;
            String label = navMenuInfo.getString(i);
            i++;
            String longLabel = navMenuInfo.getString(i);
            i++;
            String className = navMenuInfo.getString(i);
            i++;
            int imageID = navMenuInfo.getResourceId(i, -1);
            i++;
            String flags = navMenuInfo.getString(i);
            i++;
            String chiusura = navMenuInfo.getString(i);
            i++;
            if (!chiusura.equals("END-ITEM"))
                throw new IllegalArgumentException("Disallineamento " + chiusura + " (" + menuID + "," + label + "," + longLabel + "," + className + "," + imageID + "," + flags + ",");

            Class fragmentClass = null;
            try {
                if (className != null && className.trim().length() > 0)
                    fragmentClass = Class.forName(className);
            } catch (ClassNotFoundException e) {
                //e.printStackTrace();
                //skip
                //continue;
                throw new IllegalArgumentException(e);
            }
            final DataMenuInfoType search = DataMenuInfoType.search(fragmentClass);
            ris.add(new DataMenuInfo(menuID, label, longLabel, fragmentClass, imageID, search, DataMenuInfoFlag.valueOf(flags.split("([ ,])+"))));
        }
        return ris;

    }
}

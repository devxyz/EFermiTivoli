package it.gov.fermitivoli.model.menu;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by stefano on 07/09/15.
 */
public class DataMenuInfoLatestUsed {

    private final LinkedList<Integer> menuIdSortByUse;

    public DataMenuInfoLatestUsed(List<Integer> value) {
        menuIdSortByUse = new LinkedList<>(value);
    }

    public void add(int item) {
        //aggiunge in testa (rimuovendolo se gia' presente)
        menuIdSortByUse.remove((Object) item);
        menuIdSortByUse.addFirst(item);

    }

    public void add(DataMenuInfo item) {
        //aggiunge in testa (rimuovendolo se gia' presente)
        if (!item.getFlags().contains(DataMenuInfoFlag.DONT_SHOW_IN_HOME))
            add(item.getMenuID());
    }

    private DataMenuInfo _find(List<DataMenuInfo> ll, int id) {
        for (DataMenuInfo l : ll) {
            if (l.getMenuID() == id) return l;
        }
        return null;
    }

    public List<DataMenuInfo> filter(List<DataMenuInfo> ll) {
        List<DataMenuInfo> ris = new ArrayList<>(ll.size());
        for (Integer id : menuIdSortByUse) {
            final DataMenuInfo m = _find(ll, id);

            if (m != null)
                if (!m.getFlags().contains(DataMenuInfoFlag.DONT_SHOW_IN_HOME))
                    ris.add(m);

        }
        return ris;
    }


    public LinkedList<Integer> getMenuIdSortByUse() {
        return menuIdSortByUse;
    }
}

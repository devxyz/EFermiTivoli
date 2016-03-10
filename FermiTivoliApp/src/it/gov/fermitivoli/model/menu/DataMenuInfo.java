package it.gov.fermitivoli.model.menu;

import it.gov.fermitivoli.action.api.ActivityAction;
import it.gov.fermitivoli.api.AbstractFragment;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by stefano on 18/03/15.
 */
public class DataMenuInfo {
    //etichetta menu
    private final int menuID;
    //etichetta menu
    private final String menuLabel;
    //etichetta lunga
    private final String longLabel;

    //id immagine
    private final Integer imageId;

    private final DataMenuInfoBuilder action;
    private final Set<DataMenuInfoFlag> flags;

    public DataMenuInfo(int menuID, String menuLabel, String longLabel, final Class actionClass, Integer imageId, final DataMenuInfoType type, Set<DataMenuInfoFlag> flags) {
        this(menuID, menuLabel, longLabel, imageId, new DataMenuInfoBuilder() {
            @Override
            public DataMenuInfoType type() {
                return type;
            }

            @Override
            public Object build() {
                try {
                    return actionClass.newInstance();
                } catch (Exception e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }, flags);
    }

    public DataMenuInfo(int menuID, String menuLabel, String longLabel, Integer imageId, DataMenuInfoBuilder action, Set<DataMenuInfoFlag> flags) {
        this.menuID = menuID;
        if (flags != null)
            this.flags = new TreeSet<>(flags);
        else
            this.flags = new TreeSet<>();
        this.action = action;
        this.menuLabel = menuLabel;
        this.longLabel = longLabel == null ? menuLabel : longLabel;
        this.imageId = imageId;
    }

    public DataMenuInfo(int menuID, String menuLabel, String longLabel, Class actionClass, DataMenuInfoType type, Set<DataMenuInfoFlag> flags) {
        this(menuID, menuLabel, longLabel, actionClass, null, type, flags);
    }

    //indica se si tratta di un comando o di un fragment
    public static boolean isFragmentClass(Class<?> actionClass) {
        return actionClass != null && AbstractFragment.class.isAssignableFrom(actionClass);
    }

    public static boolean isCommandClass(Class<?> c) {
        return c != null && ActivityAction.class.isAssignableFrom(c);
    }

    public Set<DataMenuInfoFlag> getFlags() {
        return flags;
    }

    public int getMenuID() {
        return menuID;
    }

    public String getMenuLabel() {
        return menuLabel;
    }

    public String getLongLabel() {
        return longLabel;
    }

    public Integer getImageId() {
        return imageId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataMenuInfo that = (DataMenuInfo) o;

        if (menuID != that.menuID) return false;
        if (menuLabel != null ? !menuLabel.equals(that.menuLabel) : that.menuLabel != null) return false;
        return !(action != null ? !action.equals(that.action) : that.action != null);

    }

    @Override
    public int hashCode() {
        int result = menuID;
        result = 31 * result + (menuLabel != null ? menuLabel.hashCode() : 0);
        result = 31 * result + (action != null ? action.hashCode() : 0);
        return result;
    }

    public DataMenuInfoBuilder getAction() {
        return action;
    }

    public DataMenuInfoType type() {
        return action.type();

    }

    public boolean isSubItem() {
        return flags.contains(DataMenuInfoFlag.SUB_ITEM);

    }

    public static class DataMenuInfoSortByName implements Comparator<DataMenuInfo> {

        @Override
        public int compare(DataMenuInfo a, DataMenuInfo b) {
            return a.menuLabel.compareTo(b.menuLabel);
        }
    }

}

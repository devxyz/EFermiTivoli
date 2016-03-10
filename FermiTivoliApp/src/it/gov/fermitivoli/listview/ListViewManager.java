package it.gov.fermitivoli.listview;

import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by stefano on 24/08/15.
 */
public class ListViewManager<T extends ListAdapter> {
    private final ListView listView;
    private final T adapter;

    public ListViewManager(ListView listView, T adapter) {
        this.listView = listView;
        this.adapter = adapter;
    }

    public ListView getListView() {
        return listView;
    }

    public T getAdapter() {
        return adapter;
    }


}

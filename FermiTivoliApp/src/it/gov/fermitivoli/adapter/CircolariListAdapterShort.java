package it.gov.fermitivoli.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import it.gov.fermitivoli.R;
import it.gov.fermitivoli.dao.CircolareDB;
import it.gov.fermitivoli.layout.LayoutObjs_listview_circolari_and_notizie_short_xml;
import it.gov.fermitivoli.util.C_DateUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by stefano on 04/03/15.
 */
public class CircolariListAdapterShort extends BaseAdapter {

    private final List<CircolareDB> circolari;
    private Activity activity;
    private LayoutInflater layoutInflater;


    public CircolariListAdapterShort(Activity f) {
        this(f, new ArrayList<CircolareDB>());

    }

    public CircolariListAdapterShort(Activity f, List<CircolareDB> circolari) {
        this.circolari = new ArrayList<CircolareDB>(circolari == null ? Collections.EMPTY_LIST : circolari);
        activity = f;
        layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public List<CircolareDB> getCircolari() {
        return circolari;
    }

    public void update(List<CircolareDB> nuovaLista) {
        this.circolari.clear();
        this.circolari.addAll(circolari == null ? Collections.EMPTY_LIST : nuovaLista);
        super.notifyDataSetChanged();
    }

    public void close() {
    }

    @Override
    public int getCount() {

        // Set the total list item count
        return circolari.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Inflate the item layout and set the views
        View listItem = convertView;
        int pos = position;
        if (listItem == null) {
            listItem = layoutInflater.inflate(R.layout.listview_circolari_and_notizie_short, null);
        }

        LayoutObjs_listview_circolari_and_notizie_short_xml LAYOUT_OBJs;
        LAYOUT_OBJs = new LayoutObjs_listview_circolari_and_notizie_short_xml(listItem);
        // Initialize the views in the layout
        TextView tvTitle = LAYOUT_OBJs.title;
        CircolareDB c = this.circolari.get(pos);
        tvTitle.setText("Circolare n." + c.getNumero() + ": " + c.getTitolo() + " del " + C_DateUtil.toDDMMYYY(c.getData()));
        return listItem;
    }


}

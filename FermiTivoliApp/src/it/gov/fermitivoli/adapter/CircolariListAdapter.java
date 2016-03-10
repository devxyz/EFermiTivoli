package it.gov.fermitivoli.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import it.gov.fermitivoli.R;
import it.gov.fermitivoli.dao.CircolareDB;
import it.gov.fermitivoli.layout.LayoutObjs_listview_circolari_and_notizie_xml;
import it.gov.fermitivoli.util.C_CircolariUtil;
import it.gov.fermitivoli.util.C_DateUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by stefano on 04/03/15.
 */
public class CircolariListAdapter extends BaseAdapter {

    private final List<CircolareDB> circolari;
    private Activity activity;
    private Date dataPrecedenteVisualizzazione;
    private LayoutInflater layoutInflater;

    public CircolariListAdapter(Activity f, Date dataPrecedenteVisualizzazione) {
        this(f, new ArrayList<CircolareDB>(), dataPrecedenteVisualizzazione);

    }

    public CircolariListAdapter(Activity f, List<CircolareDB> circolari, Date dataPrecedenteVisualizzazione) {
        this.circolari = new ArrayList<CircolareDB>(circolari == null ? Collections.EMPTY_LIST : circolari);
        activity = f;
        this.dataPrecedenteVisualizzazione = dataPrecedenteVisualizzazione;
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


    private String extractText(String testo) {
        if (testo == null)
            return null;

        final StringBuilder sb = new StringBuilder();
        for (int j = 0; j < testo.length(); j++) {
            char c = testo.charAt(j);
            if (c == '\n' && sb.length() > 200) {
                return sb.substring(0, 200) + "...";
            }
            sb.append(c);
        }

        return (sb.toString().replaceAll("[\n \\s]+", " ")).trim();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Inflate the item layout and set the views
        View listItem = convertView;
        int pos = position;
        if (listItem == null) {
            listItem = layoutInflater.inflate(R.layout.listview_circolari_and_notizie, null);
        }

        LayoutObjs_listview_circolari_and_notizie_xml LAYOUT_OBJs;
        LAYOUT_OBJs = new LayoutObjs_listview_circolari_and_notizie_xml(listItem);
        // Initialize the views in the layout
        ImageView iv = LAYOUT_OBJs.image;
        TextView tvTitle = LAYOUT_OBJs.title;
        TextView tvDate = LAYOUT_OBJs.date;


        final TextView desc = LAYOUT_OBJs.descrizione;


        CircolareDB c = this.circolari.get(pos);

        if (c.getTesto() != null)
            desc.setText(extractText(C_CircolariUtil.normalizeTextAndLineFeed_forTextCircolari(c.getTesto(), true)));

        else
            desc.setText("(Testo non disponibile)");

        tvDate.setText(C_DateUtil.toDDMMYYY(c.getData()));



        /*final Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DAY_OF_MONTH, -1);
        final Date ieri = instance.getTime();*/

        //se non visualizzato
        if ((dataPrecedenteVisualizzazione.compareTo(c.getDataInserimento()) <= 0) && !c.getFlagContenutoLetto()) {
            iv.setImageResource(R.drawable.new_icon_50x50);
        } else {
            iv.setImageResource(R.drawable.pdf_40x40);
        }

        if (c.getFlagContenutoLetto()) {
            tvTitle.setTypeface(Typeface.DEFAULT);
            tvTitle.setText(c.getNumero() + " - " + c.getTitolo());
        } else {
            tvTitle.setTypeface(Typeface.DEFAULT_BOLD);
            tvTitle.setText("(*) "+c.getNumero() + " - " + c.getTitolo()+"");
        }

        return listItem;
    }


}

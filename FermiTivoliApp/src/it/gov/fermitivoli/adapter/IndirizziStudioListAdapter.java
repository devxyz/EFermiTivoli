package it.gov.fermitivoli.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import it.gov.fermitivoli.R;
import it.gov.fermitivoli.cache.UrlImageLoader;
import it.gov.fermitivoli.api.AbstractFragment;
import it.gov.fermitivoli.layout.LayoutObjs_listview_indirizzi_studio_description_livello1_categoria_xml;
import it.gov.fermitivoli.layout.LayoutObjs_listview_indirizzi_studio_description_livello2_nomecorso_xml;
import it.gov.fermitivoli.layout.LayoutObjs_listview_indirizzi_studio_description_livello3_informazioni_xml;
import it.gov.fermitivoli.model.IndirizziStudioDescription;
import it.gov.fermitivoli.util.ScreenUtil;

import java.util.List;

/**
 * Created by stefano on 04/03/15.
 */
public class IndirizziStudioListAdapter extends BaseAdapter {

    private final List<IndirizziStudioDescription> items;
    public UrlImageLoader imageLoader;
    private Activity activity;
    private LayoutInflater layoutInflater;

    public IndirizziStudioListAdapter(AbstractFragment e, List<IndirizziStudioDescription> items) {
        this.items = items;
        activity = e.getActivity();
        layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Point size = ScreenUtil.getSize(activity);
        imageLoader = new UrlImageLoader(e, size.x / 4, size.y, R.drawable.clessidra_30x30);
    }

    @Override
    public int getCount() {

        // Set the total list item count
        return items.size();
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
        final IndirizziStudioDescription info = items.get(position);

        // Inflate the item layout and set the views
        final View listItem;

        if (info instanceof IndirizziStudioDescription.IndirizziStudioDescription_CategoriaLivello1) {
            listItem = layoutInflater.inflate(R.layout.listview_indirizzi_studio_description_livello1_categoria, null);
            final IndirizziStudioDescription.IndirizziStudioDescription_CategoriaLivello1 x = (IndirizziStudioDescription.IndirizziStudioDescription_CategoriaLivello1) info;

            LayoutObjs_listview_indirizzi_studio_description_livello1_categoria_xml LAYOUT_OBJs_livello1 = new LayoutObjs_listview_indirizzi_studio_description_livello1_categoria_xml(listItem);
            // Initialize the views in the layout

            TextView tvTitle = LAYOUT_OBJs_livello1.title;
            tvTitle.setText(x.categoria);

        } else if (info instanceof IndirizziStudioDescription.IndirizziStudioDescription_CorsoLivello2) {
            listItem = layoutInflater.inflate(R.layout.listview_indirizzi_studio_description_livello2_nomecorso, null);
            final IndirizziStudioDescription.IndirizziStudioDescription_CorsoLivello2 x = (IndirizziStudioDescription.IndirizziStudioDescription_CorsoLivello2) info;

            final LayoutObjs_listview_indirizzi_studio_description_livello2_nomecorso_xml LAYOUT_OBJs_livello2 = new LayoutObjs_listview_indirizzi_studio_description_livello2_nomecorso_xml(listItem);
            // Initialize the views in the layout

            TextView tvTitle = LAYOUT_OBJs_livello2.title;


            tvTitle.setText(x.nomeCorso);


        } else if (info instanceof IndirizziStudioDescription.IndirizziStudioDescription_RisorsaCorsoLivello3) {

            listItem = layoutInflater.inflate(R.layout.listview_indirizzi_studio_description_livello3_informazioni, null);
            LayoutObjs_listview_indirizzi_studio_description_livello3_informazioni_xml LAYOUT_OBJs_livello3 = new LayoutObjs_listview_indirizzi_studio_description_livello3_informazioni_xml(listItem);
            IndirizziStudioDescription.IndirizziStudioDescription_RisorsaCorsoLivello3 x = (IndirizziStudioDescription.IndirizziStudioDescription_RisorsaCorsoLivello3) info;
            TextView tvTitle = LAYOUT_OBJs_livello3.title;


            tvTitle.setText(x.nomeRisorsa);


        } else
            throw new IllegalArgumentException("invalid type");

        return listItem;
    }


}

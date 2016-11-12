package it.gov.fermitivoli.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import it.gov.fermitivoli.R;
import it.gov.fermitivoli.adapter.IndirizziStudioListAdapter;
import it.gov.fermitivoli.api.AbstractFragment;
import it.gov.fermitivoli.dialog.HtmlPageDialog;
import it.gov.fermitivoli.layout.LayoutObjs_fragment_indirizzi_studio_xml;
import it.gov.fermitivoli.listener.OnClickListenerViewErrorCheck;
import it.gov.fermitivoli.model.IndirizziStudioDescription;
import it.gov.fermitivoli.util.StreamAndroid;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class IndirizziStudioFragment extends AbstractFragment {

    private LayoutObjs_fragment_indirizzi_studio_xml LAYOUT_OBJs;   //***************************

    public IndirizziStudioFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_indirizzi_studio, container, false);
        LAYOUT_OBJs = new LayoutObjs_fragment_indirizzi_studio_xml(rootView);

        final ArrayList<IndirizziStudioDescription> items = new ArrayList<IndirizziStudioDescription>();
        items.add(new IndirizziStudioDescription.IndirizziStudioDescription_CategoriaLivello1("Presentazione"));
        items.add(new IndirizziStudioDescription.IndirizziStudioDescription_RisorsaCorsoLivello3(
                "Struttura degli istituti tecnici", "Struttura degli istituti tecnici",
                R.raw.indirizzi_studio_riforma)
        );

        items.add(new IndirizziStudioDescription.IndirizziStudioDescription_RisorsaCorsoLivello3(
                "Obiettivi comuni ai corsi", "Obiettivi comuni trasversali",
                R.raw.indirizzi_studio_obiettivi_trasversali)
        );


        items.add(new IndirizziStudioDescription.IndirizziStudioDescription_CategoriaLivello1("Settore Economico"));

        //AFM
        items.add(new IndirizziStudioDescription.IndirizziStudioDescription_CorsoLivello2(getResources().getString(
                R.string.indirizzi_studio_AFM_nome),
                R.raw.indirizzi_studio_afm_descrizione, R.drawable.logo_indirizzo_afm)
        );
        items.add(new IndirizziStudioDescription.IndirizziStudioDescription_RisorsaCorsoLivello3(
                getResources().getString(
                        R.string.indirizzi_studio_AFM_nome), "Descrizione",
                R.raw.indirizzi_studio_afm_descrizione)
        );
        items.add(new IndirizziStudioDescription.IndirizziStudioDescription_RisorsaCorsoLivello3(
                getResources().getString(
                        R.string.indirizzi_studio_AFM_nome), "Quadro orario discipline",
                R.raw.indirizzi_studio_afm_orario)
        );

        //SIA
        items.add(new IndirizziStudioDescription.IndirizziStudioDescription_CorsoLivello2(getResources().getString(
                R.string.indirizzi_studio_SIA_nome),
                R.raw.indirizzi_studio_sia_descrizione, R.drawable.logo_indirizzo_sia)
        );
        items.add(new IndirizziStudioDescription.IndirizziStudioDescription_RisorsaCorsoLivello3(
                getResources().getString(
                        R.string.indirizzi_studio_SIA_nome), "Descrizione",
                R.raw.indirizzi_studio_sia_descrizione)
        );
        items.add(new IndirizziStudioDescription.IndirizziStudioDescription_RisorsaCorsoLivello3(
                getResources().getString(
                        R.string.indirizzi_studio_SIA_nome), "Quadro orario discipline",
                R.raw.indirizzi_studio_sia_orario)
        );


        items.add(new IndirizziStudioDescription.IndirizziStudioDescription_CategoriaLivello1("Settore Tecnologico"));
        //GEOM
        items.add(new IndirizziStudioDescription.IndirizziStudioDescription_CorsoLivello2(getResources().getString(
                R.string.indirizzi_studio_GEOM_nome),
                R.raw.indirizzi_studio_geom_descrizione, R.drawable.logo_indirizzo_geom)
        );
        items.add(new IndirizziStudioDescription.IndirizziStudioDescription_RisorsaCorsoLivello3(
                getResources().getString(
                        R.string.indirizzi_studio_GEOM_nome), "Descrizione",
                R.raw.indirizzi_studio_geom_descrizione)
        );
        items.add(new IndirizziStudioDescription.IndirizziStudioDescription_RisorsaCorsoLivello3(
                getResources().getString(
                        R.string.indirizzi_studio_GEOM_nome), "Quadro orario discipline",
                R.raw.indirizzi_studio_geom_orario)
        );

        //GRAFICA
        items.add(new IndirizziStudioDescription.IndirizziStudioDescription_CorsoLivello2(getResources().getString(
                R.string.indirizzi_studio_GRAFICA_nome),
                R.raw.indirizzi_studio_grafica_descrizione, R.drawable.logo_indirizzo_grafica)
        );
        items.add(new IndirizziStudioDescription.IndirizziStudioDescription_RisorsaCorsoLivello3(
                getResources().getString(
                        R.string.indirizzi_studio_GRAFICA_nome), "Descrizione",
                R.raw.indirizzi_studio_grafica_descrizione)
        );
        items.add(new IndirizziStudioDescription.IndirizziStudioDescription_RisorsaCorsoLivello3(
                getResources().getString(
                        R.string.indirizzi_studio_GRAFICA_nome), "Quadro orario discipline",
                R.raw.indirizzi_studio_grafica_orario)
        );


        IndirizziStudioListAdapter ii = new IndirizziStudioListAdapter(this, items);
        LAYOUT_OBJs.listView3.setAdapter(ii);

        LAYOUT_OBJs.listView3.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final IndirizziStudioDescription info = items.get(position);
                if (info instanceof IndirizziStudioDescription.IndirizziStudioDescription_CategoriaLivello1) {
                    final IndirizziStudioDescription.IndirizziStudioDescription_CategoriaLivello1 x = (IndirizziStudioDescription.IndirizziStudioDescription_CategoriaLivello1) info;
                } else if (info instanceof IndirizziStudioDescription.IndirizziStudioDescription_CorsoLivello2) {
                    final IndirizziStudioDescription.IndirizziStudioDescription_CorsoLivello2 x = (IndirizziStudioDescription.IndirizziStudioDescription_CorsoLivello2) info;
                } else if (info instanceof IndirizziStudioDescription.IndirizziStudioDescription_RisorsaCorsoLivello3) {
                    IndirizziStudioDescription.IndirizziStudioDescription_RisorsaCorsoLivello3 x = (IndirizziStudioDescription.IndirizziStudioDescription_RisorsaCorsoLivello3) info;
                    InputStream htmlStream = getActivity().getResources().openRawResource(x.idRisorsa);
                    String s;
                    try {
                        s = StreamAndroid.loadFileContent(htmlStream);
                    } catch (IOException e) {
                        e.printStackTrace();
                        s = e.getMessage();
                    } finally {
                        try {
                            htmlStream.close();
                        } catch (IOException e) {
                        }
                    }

                    System.out.println("==========================");
                    System.out.println(htmlStream);
                    HtmlPageDialog d = new HtmlPageDialog(getMainActivity(), x.nomeIndirizzo, (s), null);
                    d.show();
                } else
                    throw new IllegalArgumentException("invalid type");
            }
        });

        LAYOUT_OBJs.imageButton.setOnClickListener(new OnClickListenerViewErrorCheck(getMainActivity()) {
            @Override
            public void onClickImpl(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.url_iscrizioni)));
                startActivity(i);
            }
        });

        return rootView;
    }


}

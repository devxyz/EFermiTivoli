package it.gov.fermitivoli.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import it.gov.fermitivoli.R;
import it.gov.fermitivoli.api.AbstractFragment;
import it.gov.fermitivoli.layout.LayoutObjs_fragment_cotral_xml;
import it.gov.fermitivoli.listener.OnClickListenerViewErrorCheck;
import it.gov.fermitivoli.model.FermateCotral;
import it.gov.fermitivoli.model.menu.DataMenuInfo;
import it.gov.fermitivoli.model.menu.DataMenuInfoBuilder;
import it.gov.fermitivoli.model.menu.DataMenuInfoFlag;
import it.gov.fermitivoli.model.menu.DataMenuInfoType;
import it.gov.fermitivoli.util.DialogUtil;
import it.gov.fermitivoli.util.SharedPreferenceWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.TreeSet;


public class CotralFragment extends AbstractFragment {


    private static final String LABEL_COMUNE_TIVOLI = "Tivoli";
    private static final String LABEL_FERMATA_TIVOLI = "Tivoli";
    private FermateCotral comuni;
    private LayoutObjs_fragment_cotral_xml LAYOUT_OBJs;   //***************************

    public CotralFragment() {
    }


    @Override
    public void onStop() {
        super.onStop();
        String localita = LAYOUT_OBJs.autoCompleteTextView.getText().toString();

        SharedPreferenceWrapper sp = getMainActivity().getSharedPreferences();
        sp.setNomeLocalitaProvenienzaCotral(localita);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_cotral, container, false);

        LAYOUT_OBJs = new LayoutObjs_fragment_cotral_xml(rootView);
        comuni = new FermateCotral(getActivity());
        ArrayList<String> s = new ArrayList<String>(comuni.getFermate());
        Collections.sort(s);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this.getMainActivity(),
                android.R.layout.simple_dropdown_item_1line,
                s.toArray(new String[s.size()])
        );
        LAYOUT_OBJs.autoCompleteTextView.setSingleLine(true);
        LAYOUT_OBJs.autoCompleteTextView.setAdapter(adapter);

        SharedPreferenceWrapper sp = getMainActivity().getSharedPreferences();
        LAYOUT_OBJs.autoCompleteTextView.setText(sp.getNomeLocalitaProvenienzaCotral());


        //============================================================================
        //============================================================================

        //elenco fermate da tivoli
        final OnClickListenerViewErrorCheck tivoli2home = new OnClickListenerViewErrorCheck(getMainActivity()) {
            @Override
            protected void onClickImpl(View v) throws Throwable {

                final String fermataResidenza = LAYOUT_OBJs.autoCompleteTextView.getText().toString();
                final String comuneResidenza = comuni.getComuneFermata(fermataResidenza);

                if (!comuni.containsFermata(fermataResidenza)) {
                    DialogUtil.openInfoDialog(getMainActivity(), "Dati mancanti", "Specificare la località di provenienza, scegliendola tra quelle disponibili");
                    return;
                }


                final DataMenuInfoBuilder dd = new DataMenuInfoBuilder() {

                    @Override
                    public DataMenuInfoType type() {
                        return DataMenuInfoType.FRAGMENT;
                    }

                    @Override
                    public Object build() {
                        CotralOrariFragment f = new CotralOrariFragment();
                        f.setPartenza(LABEL_FERMATA_TIVOLI, LABEL_COMUNE_TIVOLI);
                        f.setArrivo(comuneResidenza, fermataResidenza);
                        return f;
                    }
                };
                final TreeSet<DataMenuInfoFlag> flags = new TreeSet<>(Arrays.asList(DataMenuInfoFlag.DONT_SHOW_IN_HOME));
                DataMenuInfo d = new DataMenuInfo(1, "Partenze per casa", "Partenze per casa", R.drawable.bus, dd, flags);

                getMainActivity().doAction(d);


            }
        };
        LAYOUT_OBJs.buttonOrariCasa.setOnClickListener(tivoli2home);
        LAYOUT_OBJs.imgbuttonOrariCasa.setOnClickListener(tivoli2home);


        //============================================================================
        //============================================================================


        final OnClickListenerViewErrorCheck home2tivoli = new OnClickListenerViewErrorCheck(getMainActivity()) {
            @Override
            protected void onClickImpl(View v) throws Throwable {

                final String fermataResidenza = LAYOUT_OBJs.autoCompleteTextView.getText().toString();
                final String comuneResidenza = comuni.getComuneFermata(fermataResidenza);
                if (!comuni.containsFermata(fermataResidenza)) {
                    DialogUtil.openInfoDialog(getMainActivity(), "Dati mancanti", "Specificare la località di provenienza, scegliendola tra quelle disponibili");
                    return;
                }


                final DataMenuInfoBuilder dd = new DataMenuInfoBuilder() {

                    @Override
                    public DataMenuInfoType type() {
                        return DataMenuInfoType.FRAGMENT;
                    }

                    @Override
                    public Object build() {
                        CotralOrariFragment f = new CotralOrariFragment();
                        f.setArrivo(LABEL_FERMATA_TIVOLI, LABEL_COMUNE_TIVOLI);
                        f.setPartenza(comuneResidenza, fermataResidenza);
                        return f;
                    }
                };
                final TreeSet<DataMenuInfoFlag> flags = new TreeSet<>(Arrays.asList(DataMenuInfoFlag.DONT_SHOW_IN_HOME));
                DataMenuInfo d = new DataMenuInfo(1, "Partenze per casa", "Partenze per casa", R.drawable.bus, dd, flags);

                getMainActivity().doAction(d);


            }
        };
        LAYOUT_OBJs.buttonOrariScuola.setOnClickListener(home2tivoli);
        LAYOUT_OBJs.imgbuttonOrariScuola.setOnClickListener(home2tivoli);


        //============================================================================
        //============================================================================

/*
        LAYOUT_OBJs.buttonOrariVersoScuola.setOnClickListener(new OnClickListenerViewErrorCheck(getMainActivity()) {
            @Override
            protected void onClickImpl(View v) throws Throwable {
                final String fermata = LAYOUT_OBJs.autoCompleteTextView.getText().toString();
                if (!comuni.containsFermata(fermata)) {
                    DialogUtil.openInfoDialog(getMainActivity(), "Dati mancanti", "Specificare la località di provenienza, scegliendola tra quelle disponibili");
                    return;
                }
                final String comune = comuni.getComuneFermata(fermata);

                CotralFermateFragment f = new CotralFermateFragment();
                f.setTitolo("Partenze da " + fermata + " per Tivoli");
                f.setUrl(urlOrari(fermata, LABEL_COMUNE_TIVOLI));
                getMainActivity().doAction("Autobus per Tivoli", f, getMainActivity().getResources().getDrawable(R.drawable.bus));

            }
        });


        LAYOUT_OBJs.buttonOrariVersoCasa.setOnClickListener(new OnClickListenerViewErrorCheck(getMainActivity()) {
            @Override
            protected void onClickImpl(View v) throws Throwable {
                final String fermata = LAYOUT_OBJs.autoCompleteTextView.getText().toString();
                if (!comuni.containsFermata(fermata)) {
                    DialogUtil.openInfoDialog(getMainActivity(), "Dati mancanti", "Specificare la località di provenienza, scegliendola tra quelle disponibili");
                    return;
                }
                final String comune = comuni.getComuneFermata(fermata);

                CotralFermateFragment f = new CotralFermateFragment();
                f.setTitolo("Partenze da Tivoli per " + fermata);
                f.setUrl(urlOrari(fermata, LABEL_COMUNE_TIVOLI));
                getMainActivity().doAction("Autobus per " + fermata, f, getMainActivity().getResources().getDrawable(R.drawable.bus));


            }
        });*/

        LAYOUT_OBJs.buttonPercorso.setOnClickListener(new OnClickListenerViewErrorCheck(getMainActivity()) {
            @Override
            protected void onClickImpl(View v) throws Throwable {

                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_cotral_form_ricerca_orari)));
                startActivity(i);

            }
        });

        return rootView;
    }


}

package it.gov.fermitivoli.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import it.gov.fermitivoli.R;
import it.gov.fermitivoli.api.AbstractFragment;
import it.gov.fermitivoli.layout.LayoutObjs_fragment_cotral_orari_xml;
import it.gov.fermitivoli.listener.OnClickListenerViewErrorCheck;
import it.gov.fermitivoli.model.menu.DataMenuInfo;
import it.gov.fermitivoli.model.menu.DataMenuInfoBuilder;
import it.gov.fermitivoli.model.menu.DataMenuInfoFlag;
import it.gov.fermitivoli.model.menu.DataMenuInfoType;
import it.gov.fermitivoli.util.C_DateUtil;

import java.util.Arrays;
import java.util.Date;
import java.util.TreeSet;


public class CotralOrariFragment extends AbstractFragment {


    private static final String KEY_SAVE_COMUNE_PARTENZA = "KEY_SAVE_COMUNE_PARTENZA";
    private static final String KEY_SAVE_FERMATA_PARTENZA = "KEY_SAVE_FERMATA_PARTENZA";
    private static final String KEY_SAVE_COMUNE_ARRIVO = "KEY_SAVE_COMUNE_ARRIVO";
    private static final String KEY_SAVE_FERMATA_ARRIVO = "KEY_SAVE_FERMATA_ARRIVO";
    private LayoutObjs_fragment_cotral_orari_xml LAYOUT_OBJs;   //***************************

    private String fermataPartenza;
    private String comunePartenza;
    private String fermataArrivo;
    private String comuneArrivo;

    public CotralOrariFragment() {
    }

    public void setPartenza(String comunePartenza, String fermataPartenza) {
        this.comunePartenza = comunePartenza;
        this.fermataPartenza = fermataPartenza;
    }

    public void setArrivo(String comuneArrivo, String fermataArrivo) {
        this.comuneArrivo = comuneArrivo;
        this.fermataArrivo = fermataArrivo;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //gestisce il cambio di configurazione ricreando da capo il fragment
        setRetainInstance(false);

        if (comunePartenza == null)
            comunePartenza = savedInstanceState.getString(KEY_SAVE_COMUNE_PARTENZA);
        if (fermataPartenza == null)
            fermataPartenza = savedInstanceState.getString(KEY_SAVE_FERMATA_PARTENZA);

        if (comuneArrivo == null)
            comuneArrivo = savedInstanceState.getString(KEY_SAVE_COMUNE_ARRIVO);
        if (fermataArrivo == null)
            fermataArrivo = savedInstanceState.getString(KEY_SAVE_FERMATA_ARRIVO);

        initWWW();
    }

    public String urlOrari(String fermataPartenza, String fermataArrivo) {

        String url = getMainActivity().getActivity().getResources().getString(R.string.url_cotral_orari_per_tratta);
        return url.replace("XXXX", fermataPartenza).replace("YYYY", fermataArrivo).replace("AAAAMMGG", C_DateUtil.toYYYYMMDD(new Date()));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_cotral_orari, container, false);

        LAYOUT_OBJs = new LayoutObjs_fragment_cotral_orari_xml(rootView);
        LAYOUT_OBJs.imageButtonFermate.setOnClickListener(new OnClickListenerViewErrorCheck(getMainActivity()) {
            @Override
            protected void onClickImpl(View v) throws Throwable {

                final DataMenuInfoBuilder dd = new DataMenuInfoBuilder() {

                    @Override
                    public DataMenuInfoType type() {
                        return DataMenuInfoType.FRAGMENT;
                    }

                    @Override
                    public Object build() {
                        CotralFermateFragment f = new CotralFermateFragment();
                        f.setComune(comunePartenza);
                        return f;
                    }
                };
                final TreeSet<DataMenuInfoFlag> flags = new TreeSet<>(Arrays.asList(DataMenuInfoFlag.DONT_SHOW_IN_HOME));
                DataMenuInfo d = new DataMenuInfo(1, "Fermate autobus", "Fermate bus a " + comunePartenza, R.drawable.bus, dd, flags);
                getMainActivity().doAction(d);


            }
        });

        initWWW();

        return rootView;
    }

    private void initWWW() {
        if (LAYOUT_OBJs == null) return;

        System.out.println("XXXXXXXXXXXXXX initWWW " + fermataPartenza + ", " + fermataArrivo);
        LAYOUT_OBJs.textViewTitle.setText("Partenze da " + fermataPartenza + " per " + fermataArrivo);
        WebView x = LAYOUT_OBJs.textViewDescrizione;
        x.getSettings().setDisplayZoomControls(true);
        x.getSettings().setJavaScriptEnabled(true);
        x.getSettings().setLoadWithOverviewMode(true);
        x.getSettings().setUseWideViewPort(true);

        LAYOUT_OBJs.progressBar.setMax(100);
        LAYOUT_OBJs.progressBar.setIndeterminate(true);

        x.setVisibility(View.INVISIBLE);
        x.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {

                view.loadUrl("javascript:(function() { " +
                        "document.getElementById('left').style.display=\"none\"; " +
                        "document.getElementById('maindiv').style.display=\"none\"; " +
                        "document.getElementById('footer').style.display=\"none\"; " +
                        "document.getElementsByClassName('riepilogo')[0].style.display=\"none\"; " +
                        "document.getElementsByClassName('riepilogo')[1].style.display=\"none\"; " +
                        "document.getElementsByClassName('riepilogo')[2].style.display=\"none\"; " +
                        "})()");
                /*view.loadUrl("javascript:(function() { " +
                        "document.getElementsById('maindiv')[0].style.display=\"none\"; " +
                        "})()");*/
                view.setVisibility(View.VISIBLE);

            }
        });
        x.setWebChromeClient(new MyWebViewClient());


        x.loadUrl(urlOrari(fermataPartenza, fermataArrivo));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_SAVE_COMUNE_PARTENZA, comunePartenza);
        outState.putString(KEY_SAVE_FERMATA_PARTENZA, fermataPartenza);
        outState.putString(KEY_SAVE_COMUNE_ARRIVO, comuneArrivo);
        outState.putString(KEY_SAVE_FERMATA_ARRIVO, fermataArrivo);
    }

    private class MyWebViewClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (LAYOUT_OBJs.progressBar.isIndeterminate())
                LAYOUT_OBJs.progressBar.setIndeterminate(false);

            if (LAYOUT_OBJs.progressBar.getVisibility() != View.VISIBLE)
                LAYOUT_OBJs.progressBar.setVisibility(View.VISIBLE);

            LAYOUT_OBJs.progressBar.setProgress(newProgress);

            //------------------------------------
            if (newProgress >= 100)
                LAYOUT_OBJs.progressBar.setVisibility(View.GONE);

            super.onProgressChanged(view, newProgress);
        }
    }


}

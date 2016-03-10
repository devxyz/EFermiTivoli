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
import it.gov.fermitivoli.layout.LayoutObjs_fragment_cotral_fermate_xml;


public class CotralFermateFragment extends AbstractFragment {


    private static final String KEY_SAVE_COMUNE = "KEY_SAVE_COMUNE";

    private LayoutObjs_fragment_cotral_fermate_xml LAYOUT_OBJs;   //***************************

    private String comune;

    public CotralFermateFragment() {
    }

    public String urlFermate(String comune) {

        String url = getMainActivity().getActivity().getResources().getString(R.string.url_cotral_fermate_comune);
        return url.replace("XXXX", comune);
    }

    public void setComune(String comune) {
        this.comune = comune;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //gestisce il cambio di configurazione ricreando da capo il fragment
        setRetainInstance(false);

        if (comune == null)
            comune = savedInstanceState.getString(KEY_SAVE_COMUNE);
        initWWW();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_cotral_fermate, container, false);

        LAYOUT_OBJs = new LayoutObjs_fragment_cotral_fermate_xml(rootView);

        initWWW();

        return rootView;
    }

    private void initWWW() {
        if (LAYOUT_OBJs == null) return;

        System.out.println("XXXXXXXXXXXXXX initWWW " + comune);
        LAYOUT_OBJs.textViewTitle.setText("Fermate a " + comune);
        WebView x = LAYOUT_OBJs.textViewDescrizione;
        x.getSettings().setDisplayZoomControls(true);
        x.getSettings().setJavaScriptEnabled(true);


        x.getSettings().setLoadWithOverviewMode(true);
        x.getSettings().setUseWideViewPort(true);
        LAYOUT_OBJs.progressBar.setMax(100);
        LAYOUT_OBJs.progressBar.setIndeterminate(true);
        LAYOUT_OBJs.textViewDescrizione.setVisibility(View.INVISIBLE);

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
                        "})()");
                /*view.loadUrl("javascript:(function() { " +
                        "document.getElementsById('maindiv')[0].style.display=\"none\"; " +
                        "})()");*/
                view.setVisibility(View.VISIBLE);

            }

        });
        x.setWebChromeClient(new MyWebViewClient());


        x.loadUrl(urlFermate(comune));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_SAVE_COMUNE, comune);
    }

    private class MyWebViewClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (LAYOUT_OBJs.progressBar.isIndeterminate())
                LAYOUT_OBJs.progressBar.setIndeterminate(false);

            if (LAYOUT_OBJs.progressBar.getVisibility() == View.INVISIBLE)
                LAYOUT_OBJs.progressBar.setVisibility(View.VISIBLE);

            LAYOUT_OBJs.progressBar.setProgress(newProgress);

            //------------------------------------
            if (newProgress >= 100)
                LAYOUT_OBJs.progressBar.setVisibility(View.INVISIBLE);

            super.onProgressChanged(view, newProgress);
        }
    }



}

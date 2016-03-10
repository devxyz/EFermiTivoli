package it.gov.fermitivoli.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import it.gov.fermitivoli.R;
import it.gov.fermitivoli.api.AbstractFragment;
import it.gov.fermitivoli.layout.LayoutObjs_fragment_view_html_xml;

public abstract class AbstractHtmlPageFragment extends AbstractFragment {


    private LayoutObjs_fragment_view_html_xml LAYOUT_OBJs;   //***************************

    public AbstractHtmlPageFragment() {
    }

    protected abstract String getHtmlText();

    protected abstract String getTitle();

    protected abstract String getUrl();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_view_html, container, false);

        //ON CREATE method
        //**************************
        LAYOUT_OBJs = new LayoutObjs_fragment_view_html_xml(rootView);

        WebView x = LAYOUT_OBJs.textViewDescrizione;
        x.getSettings().setDisplayZoomControls(true);
        x.getSettings().setJavaScriptEnabled(true);
        x.getSettings().setLoadWithOverviewMode(true);
        x.getSettings().setUseWideViewPort(true);

        LAYOUT_OBJs.progressBar7.setMax(100);
        LAYOUT_OBJs.progressBar7.setIndeterminate(true);

        // x.setVisibility(View.INVISIBLE);

        x.setWebChromeClient(new MyWebViewClient());


        final String htmlText = getHtmlText();
        final String title = getTitle();
        final String url = getUrl();
        if (htmlText != null) {
            LAYOUT_OBJs.textViewDescrizione.loadData(htmlText, "text/html", "UTF-8");
        } else {
            LAYOUT_OBJs.textViewDescrizione.loadUrl(url);

        }
        if (title != null) {
            LAYOUT_OBJs.textViewTitle.setText(title);
        } else {
            LAYOUT_OBJs.textViewTitle.setText(getUrl());
        }

        if (url != null)
            LAYOUT_OBJs.buttonClose.setVisibility(View.VISIBLE);
        else
            LAYOUT_OBJs.buttonClose.setVisibility(View.GONE);

        //Ist.+Tec.+Stat.+E.+Fermi/@41.956178,12.806626
        return rootView;
    }

    private class MyWebViewClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (LAYOUT_OBJs.progressBar7.isIndeterminate())
                LAYOUT_OBJs.progressBar7.setIndeterminate(false);

            if (LAYOUT_OBJs.progressBar7.getVisibility() != View.VISIBLE)
                LAYOUT_OBJs.progressBar7.setVisibility(View.VISIBLE);

            LAYOUT_OBJs.progressBar7.setProgress(newProgress);

            //------------------------------------
            if (newProgress >= 100)
                LAYOUT_OBJs.progressBar7.setVisibility(View.GONE);

            super.onProgressChanged(view, newProgress);
        }
    }


}

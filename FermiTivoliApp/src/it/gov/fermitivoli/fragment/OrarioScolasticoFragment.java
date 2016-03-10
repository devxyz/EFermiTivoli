package it.gov.fermitivoli.fragment;

import it.gov.fermitivoli.R;

/**
 * Created by stefano on 22/09/15.
 */
public class OrarioScolasticoFragment extends AbstractHtmlPageFragment {
    @Override
    protected String getHtmlText() {
        return null;
    }

    @Override
    protected String getTitle() {
        return "Orario delle lezioni";
    }

    @Override
    protected String getUrl() {
        return getActivity().getResources().getString(R.string.url_orario_scolastico);
    }
}

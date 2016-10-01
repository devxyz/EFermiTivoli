package it.gov.fermitivoli.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.MultiAutoCompleteTextView;
import it.gov.fermitivoli.R;
import it.gov.fermitivoli.activity.MainMenuActivity;
import it.gov.fermitivoli.adapter.NewsListAdapter;
import it.gov.fermitivoli.api.AbstractFragment;
import it.gov.fermitivoli.dao.*;
import it.gov.fermitivoli.db.ManagerNews;
import it.gov.fermitivoli.dialog.NewsDetailsDialog;
import it.gov.fermitivoli.layout.LayoutObjs_fragment_news_rss_xml;
import it.gov.fermitivoli.listener.OnClickListenerViewErrorCheck;
import it.gov.fermitivoli.util.DebugUtil;
import it.gov.fermitivoli.util.DialogUtil;

import java.util.*;

public class NewsFragmentRss extends AbstractFragment {
    private LayoutObjs_fragment_news_rss_xml LAYOUT_OBJs;   //***************************
    private NewsListAdapter a;
    private ArrayAdapter<String> multiTextViewAdapter;


    public NewsFragmentRss() {
    }

    public static void updateNews_impostaFlagLettura(MainMenuActivity m, final NewsListAdapter a, final boolean flagLettura) {
        //aggiorna il database
        FermiAppDbHelper db = new FermiAppDbHelper(m);
        try {
            db.runInTransaction(new FermiAppDBHelperRun() {
                @Override
                public void run(DaoSession session, Context ctx) throws Throwable {
                    final List<NewsDB> y = a.getRssItems();
                    final NewsDBDao x = session.getNewsDBDao();
                    for (NewsDB c : y) {
                        if (c.getFlagContenutoLetto() != flagLettura) {
                            c.setFlagContenutoLetto(flagLettura);
                            x.update(c);
                        }
                    }
                }
            });
        } catch (Throwable throwable) {

        } finally {
            db.close();
        }
        a.notifyDataSetChanged();
    }

    public static void updateNews_impostaFlagLettura(MainMenuActivity m, NewsListAdapter a, final NewsDB c, boolean flagLettura) {
        //controlla se segnare la lettura
        if (c.getFlagContenutoLetto() != flagLettura) {
            c.setFlagContenutoLetto(flagLettura);

            //aggiorna il database
            FermiAppDbHelper db = new FermiAppDbHelper(m);
            try {
                db.runInTransaction(new FermiAppDBHelperRun() {
                    @Override
                    public void run(DaoSession session, Context ctx) throws Throwable {
                        final NewsDBDao circolareDBDao = session.getNewsDBDao();
                        circolareDBDao.update(c);

                    }
                });
            } catch (Throwable throwable) {

            } finally {
                db.close();
            }
            a.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * aggiorna la lista degli elementi della autocomplete list
     */
    private void aggiornaViewNewsAndTerminiDalDB() {
        try {
            FermiAppDbHelper.runOneTransactionSync(getMainActivity(), new FermiAppDBHelperRun() {
                @Override
                public void run(DaoSession session, Context ctx) throws Throwable {
                    try {
                        if (DebugUtil.DEBUG__NewsFragmentRss) {
                            Log.d("CERCA_News_FRAG", "START UPDATE aggiornaViewNewsAndTerminiDalDB");
                        }
                        final ManagerNews managerNews = new ManagerNews(session);

                        //update termin
                        final Set<String> termini = managerNews.listAllTermini();
                        multiTextViewAdapter.clear();
                        multiTextViewAdapter.addAll(termini);
                        multiTextViewAdapter.notifyDataSetChanged();
                        if (DebugUtil.DEBUG__NewsFragmentRss)
                            Log.d("CERCA_News_FRAG", "UPDATE LISTA TERMINI " + termini.size());

                        //update circolari
                        final List<NewsDB> cc = managerNews.listAllNews();
                        ArrayList<NewsDB> uu = new ArrayList<>(cc);


                        ManagerNews.sortLastToFirst(uu);
                        a.update(uu);


                        if (DebugUtil.DEBUG__NewsFragmentRss) {
                            Log.d("CERCA_News_FRAG", "END UPDATE aggiornaViewNewsAndTerminiDalDB");
                        }
                    } catch (Throwable ex) {
                    }

                }
            });
        } catch (Throwable ex) {
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_news_rss, container, false);
        //ON CREATE method
        //**************************
        LAYOUT_OBJs = new LayoutObjs_fragment_news_rss_xml(rootView);
        //**************************

        a = new NewsListAdapter(this);

        LAYOUT_OBJs.listView.setAdapter(a);
        LAYOUT_OBJs.listView.setEmptyView(LAYOUT_OBJs.textViewListaVuota);

        LAYOUT_OBJs.listView.setOnItemClickListener(new __ApriNewsOnItemClickListener(a, this));
        LAYOUT_OBJs.listView.setOnItemLongClickListener(new MyOnItemLongClickListener(a, this));
        LAYOUT_OBJs.imageButton3.setOnClickListener(new __ClickButtonCerca());

        multiTextViewAdapter = new ArrayAdapter<String>(
                getMainActivity(),
                android.R.layout.simple_dropdown_item_1line);


        LAYOUT_OBJs.multiAutoCompleteTextView.addTextChangedListener(new ListenerModificaTestoMultiText());
        LAYOUT_OBJs.multiAutoCompleteTextView.setAdapter(multiTextViewAdapter);
        LAYOUT_OBJs.multiAutoCompleteTextView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());


        //new LoadRss(getMainActivity()).execute();
        aggiornaViewNewsAndTerminiDalDB();

        return rootView;
    }

    public static class MyOnItemLongClickListener implements AdapterView.OnItemLongClickListener {

        private final NewsListAdapter a;
        private final AbstractFragment fragment;

        public MyOnItemLongClickListener(NewsListAdapter a, AbstractFragment fragment) {
            this.a = a;
            this.fragment = fragment;
        }

        private MainMenuActivity getMainActivity() {
            return fragment.getMainActivity();
        }

        @Override
        public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, final long id) {

            final NewsDB c = a.getRssItems().get(position);

            DialogUtil.openChooseDialog(fragment.getActivity(), "Quale azione vuoi svolgere?", new CharSequence[]{
                            "Segna tutte da leggere",
                            "Segna tutte lette",
                            "Segna notizia corrente da leggere",
                            "Segna notizia corrente letta"
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            switch (which) {

                                case 0:
                                    updateNews_impostaFlagLettura(getMainActivity(), a, false);

                                    break;
                                case 1:
                                    updateNews_impostaFlagLettura(getMainActivity(), a, true);
                                    break;
                                case 2:
                                    updateNews_impostaFlagLettura(getMainActivity(), a, c, false);
                                    break;
                                case 3:
                                    updateNews_impostaFlagLettura(getMainActivity(), a, c, true);
                                    break;
                                default:
                                    throw new IllegalArgumentException("code:" + which + "");

                            }


                        }
                    }
           ,null );

            return true;
        }
    }

    private static class __ApriNewsOnItemClickListener implements AdapterView.OnItemClickListener {
        private NewsListAdapter a;
        private AbstractFragment fragment;

        public __ApriNewsOnItemClickListener(NewsListAdapter a, AbstractFragment fragment) {
            this.a = a;
            this.fragment = fragment;
        }

        private MainMenuActivity getMainActivity() {
            return fragment.getMainActivity();
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final NewsDB n = a.getRssItems().get(position);

            //controlla se segnare la lettura
            if (!n.getFlagContenutoLetto()) {
                FermiAppDbHelper db = new FermiAppDbHelper(getMainActivity());
                try {
                    db.runInTransaction(new FermiAppDBHelperRun() {
                        @Override
                        public void run(DaoSession session, Context ctx) throws Throwable {
                            n.setFlagContenutoLetto(true);
                            //aggiorna il database
                            final NewsDBDao newsDBDao = session.getNewsDBDao();
                            newsDBDao.update(n);
                        }
                    });

                } catch (Throwable ex) {
                    db.close();
                    return;
                } finally {
                    db.close();
                }


                //apre file esterno
                getMainActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //aggiorna la lista
                        a.notifyDataSetChanged();
                    }
                });
            }
            NewsDetailsDialog d = new NewsDetailsDialog(fragment, n);
            d.show();
        }
    }

    /**
     * click bottone
     */
    private class __ClickButtonCerca extends OnClickListenerViewErrorCheck {
        public __ClickButtonCerca() {
            super(NewsFragmentRss.this.getMainActivity());
        }

        @Override
        protected void onClickImpl(View v) throws Throwable {
            final FermiAppDbHelper db = new FermiAppDbHelper(getMainActivity());
            try {
                db.runInTransaction(new FermiAppDBHelperRun() {
                    @Override
                    public void run(DaoSession session, Context ctx) throws Throwable {
                        final ManagerNews m = new ManagerNews(session);

                        if (DebugUtil.DEBUG__NewsFragmentRss)
                            Log.d("CERCA_News_FRAG", "LIST CIRCOLARI");

                        final String s = LAYOUT_OBJs.multiAutoCompleteTextView.getText().toString();

                        final TreeSet<String> termini = new TreeSet<String>(Arrays.asList(s.split("[, \t\n]+")));
                        termini.remove("");
                        if (DebugUtil.DEBUG__NewsFragmentRss)
                            Log.d("CERCA_News_FRAG", "LIST CIRCOLARI (Termini: " + termini.size() + ", " + (termini) + " " + ")");


                        List<NewsDB> rr;
                        if (termini.size() == 0) {
                            if (DebugUtil.DEBUG__NewsFragmentRss)
                                Log.d("CERCA_News_FRAG", "ELENCO TOTALE");
                            rr = new ArrayList<>(m.listAllNews());
                        } else {
                            if (DebugUtil.DEBUG__NewsFragmentRss)
                                Log.d("CERCA_News_FRAG", "SELECT CIRCOLARI BY RADICE");
                            rr = new ArrayList<>(m.selectNewsByTerms(termini));
                        }

                        //ordina
                        ManagerNews.sortLastToFirst(rr);
                        a.update(rr);

                    }
                });

            } finally {
                db.close();
            }
        }
    }

    private class ListenerModificaTestoMultiText implements TextWatcher

    {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            final String s1 = LAYOUT_OBJs.multiAutoCompleteTextView.getText().toString();
            if (s1.trim().endsWith(",")) {
                LAYOUT_OBJs.imageButton3.performClick();
                return;
            }


            final ListAdapter adapter = LAYOUT_OBJs.multiAutoCompleteTextView.getAdapter();
            if (adapter.getCount() > 0) {
                a.update(new ArrayList<NewsDB>());
            }
        }
    }
}

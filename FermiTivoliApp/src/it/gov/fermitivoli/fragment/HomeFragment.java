package it.gov.fermitivoli.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import de.greenrobot.dao.query.QueryBuilder;
import it.gov.fermitivoli.R;
import it.gov.fermitivoli.adapter.MenuHomeListAdapter;
import it.gov.fermitivoli.api.AbstractFragment;
import it.gov.fermitivoli.dao.*;
import it.gov.fermitivoli.db.ManagerCircolare;
import it.gov.fermitivoli.layout.LayoutObjs_fragment_home_xml;
import it.gov.fermitivoli.listener.OnClickListenerViewErrorCheck;
import it.gov.fermitivoli.model.menu.DataMenuInfo;
import it.gov.fermitivoli.model.menu.impl.StringsMenuPrincipale;

import java.util.Date;
import java.util.List;

public class HomeFragment extends AbstractFragment {

    private LayoutObjs_fragment_home_xml LAYOUT_OBJs;   //***************************
    private int numNotizieNonLette = 0;
    private int numCircolariNonLette = 0;
    private int numCircolariInEvidenzaOggi = 0;

    public HomeFragment() {
    }

    @Override
    protected Integer getHelpScreen() {
        return R.drawable.help_home_fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        //ON CREATE method
        //**************************
        LAYOUT_OBJs = new LayoutObjs_fragment_home_xml(rootView);
        //**************************
        //**************************
        LAYOUT_OBJs.textView5.setOnClickListener(new OnClickListenerViewErrorCheck(getMainActivity()) {
            @Override
            protected void onClickImpl(View v) throws Throwable {
                getMainActivity().doAction(StringsMenuPrincipale.CONTATTACI_2);
            }
        });

        //Bitmap b = ScreenUtil.getResourceAsBitmap(this.getMainActivity(), R.drawable.logo_fermi_150x150);
        //LAYOUT_OBJs.imageView.setImageBitmap(ScreenUtil.scaleAndAdapt(b, 300, 300));

        LAYOUT_OBJs.imageView.setOnClickListener(new OnClickListenerViewErrorCheck(getMainActivity()) {
            @Override
            public void onClickImpl(View v) {
                getMainActivity().openMenu();

            }
        });

        FermiAppDbHelper f = new FermiAppDbHelper(getMainActivity());
        try {
            f.runInTransaction(new FermiAppDBHelperRun() {
                @Override
                public void run(DaoSession session, Context ctx) throws Throwable {
                    QueryBuilder<NewsDB> q1 = session.getNewsDBDao().queryBuilder().where(NewsDBDao.Properties.FlagContenutoLetto.eq(false));
                    QueryBuilder<CircolareDB> q2 = session.getCircolareDBDao().queryBuilder().where(CircolareDBDao.Properties.FlagContenutoLetto.eq(false));
                    ManagerCircolare c = new ManagerCircolare(session);
                    final List<CircolareDB> circolariDiOggi = c.circolariByDate(new Date());

                    numNotizieNonLette = q1.list().size();
                    numCircolariNonLette = q2.list().size();
                    numCircolariInEvidenzaOggi = circolariDiOggi.size();
                }
            });
        } catch (Throwable e) {

        } finally {
            f.close();
        }


        LAYOUT_OBJs.textViewTipoUtente.setText(getMainActivity().getSharedPreferences().getUserType().getDescrizione());


        final MenuHomeListAdapter adapter = new MenuHomeListAdapter(getMainActivity(), numCircolariNonLette, numNotizieNonLette, numCircolariInEvidenzaOggi);
        LAYOUT_OBJs.listView4.setAdapter(adapter);

        LAYOUT_OBJs.listView4.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final DataMenuInfo dataMenuInfo = adapter.getDataMenuInfo(position);
                getMainActivity().doAction(dataMenuInfo);
                adapter.update();

            }
        });


        //Ist.+Tec.+Stat.+E.+Fermi/@41.956178,12.806626
        return rootView;
    }


}

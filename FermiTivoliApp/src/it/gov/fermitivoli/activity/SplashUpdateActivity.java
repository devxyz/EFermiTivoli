package it.gov.fermitivoli.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import it.gov.fermitivoli.R;
import it.gov.fermitivoli.adapter.CircolariListAdapter;
import it.gov.fermitivoli.api.AbstractActivity;
import it.gov.fermitivoli.dao.CircolareDB;
import it.gov.fermitivoli.dao.DaoSession;
import it.gov.fermitivoli.dao.FermiAppDBHelperRun;
import it.gov.fermitivoli.dao.FermiAppDbHelper;
import it.gov.fermitivoli.db.ManagerCircolare;
import it.gov.fermitivoli.layout.LayoutObjs_activity_splash_update2_xml;
import it.gov.fermitivoli.services.UpdateService;
import it.gov.fermitivoli.util.C_DateUtil;
import it.gov.fermitivoli.util.DebugUtil;
import it.gov.fermitivoli.util.ThreadUtil;

import java.util.*;

/**
 * Created by stefano on 10/09/15.
 */
public class SplashUpdateActivity extends AbstractActivity {
    private LayoutObjs_activity_splash_update2_xml obj;

    private List<CircolareDB> getCircolariDataCorrente() {

        final TreeSet<CircolareDB> ris = new TreeSet<>(ManagerCircolare.getCircolareDBComparator());
        final FermiAppDbHelper db = new FermiAppDbHelper(getActivity());
        try {
            db.runInTransaction(new FermiAppDBHelperRun() {
                @Override
                public void run(DaoSession session, Context ctx) throws Throwable {
                    //circolari odierne
                    List<CircolareDB> circolari;
                    final ManagerCircolare managerCircolare = new ManagerCircolare(session);

                    Calendar c = Calendar.getInstance();
                    c.setTime(new Date());
                    c.add(Calendar.DAY_OF_MONTH, -1);
                    Date d = c.getTime();

                    circolari = managerCircolare.circolariByDate(d);
                    obj.textViewMsgUpdate.setText("Oggi " + C_DateUtil.toDDMMYYY(d) + " in evidenza:");


                    ris.addAll(circolari);

                    if (true) {
                        if (DebugUtil.DEBUG__CircolariGiornaliereFragment) {
                            Log.d("CIRCOLARI GIORNALIERE ", "AGGIUNGI PUBBLICATE");
                        }
                        final List<CircolareDB> circolareInfoWebs1 = managerCircolare.circolariByPubDate(new Date());
                        if (circolareInfoWebs1 != null)
                            ris.addAll(circolareInfoWebs1);
                    }
                    ArrayList<CircolareDB> ultimeCircolari = new ArrayList<>(ris);
                    ManagerCircolare.sortLastToFirst(ultimeCircolari);


                }
            });

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            db.close();
        }

        ArrayList<CircolareDB> ultimeCircolari = new ArrayList<>(ris);
        ManagerCircolare.sortLastToFirst(ultimeCircolari);

        return ultimeCircolari;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_update2);
        obj = new LayoutObjs_activity_splash_update2_xml(this);


        obj.txtInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startApplication();
            }
        });

        //start service
        Intent serviceIntent = new Intent(this, UpdateService.class);
        startService(serviceIntent);

        Thread t = new Thread() {
            @Override
            public void run() {
                final List<CircolareDB> circolari = getCircolariDataCorrente();
                CircolariListAdapter a = new CircolariListAdapter(getActivity(), circolari);
                obj.listViewCircolariDelGiorno.setAdapter(a);
                ThreadUtil.sleep(20000);
                startApplication();
            }
        };


        t.start();
    }

    private void startApplication() {

        Intent i = new Intent(SplashUpdateActivity.this, MainMenuActivity.class);
        startActivity(i);
        finish();
    }
}
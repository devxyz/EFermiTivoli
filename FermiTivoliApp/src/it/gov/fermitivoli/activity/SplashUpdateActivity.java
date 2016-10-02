package it.gov.fermitivoli.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import it.gov.fermitivoli.R;
import it.gov.fermitivoli.adapter.CircolariListAdapterShort;
import it.gov.fermitivoli.api.AbstractActivity;
import it.gov.fermitivoli.dao.CircolareDB;
import it.gov.fermitivoli.dao.DaoSession;
import it.gov.fermitivoli.dao.FermiAppDBHelperRun;
import it.gov.fermitivoli.dao.FermiAppDbHelper;
import it.gov.fermitivoli.db.ManagerCircolare;
import it.gov.fermitivoli.layout.LayoutObjs_activity_splash_update2_xml;
import it.gov.fermitivoli.model.AppUserType;
import it.gov.fermitivoli.model.menu.impl.StringsMenuPrincipale;
import it.gov.fermitivoli.services.UpdateService;
import it.gov.fermitivoli.util.C_DateUtil;
import it.gov.fermitivoli.util.DebugUtil;
import it.gov.fermitivoli.util.DialogUtil;
import it.gov.fermitivoli.util.ThreadUtil;

import java.util.*;

/**
 * Created by stefano on 10/09/15.
 */
public class SplashUpdateActivity extends AbstractActivity {
    private LayoutObjs_activity_splash_update2_xml obj;
    private boolean closed = false;

    public static void chooseUserType(final AbstractActivity e, final DialogInterface.OnClickListener onClickListener) {


        DialogUtil.openChooseDialog(e, "Scegli il profilo piu' adatto a te.", false, new CharSequence[]
                        {"Sono uno studente", "Sono un docente", "Sono un genitore", "Altro"},
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        switch (which) {
                            case 0:
                                e.getSharedPreferences().setUserType(AppUserType.STUDENTE);
                                break;
                            case 1:
                                e.getSharedPreferences().setUserType(AppUserType.DOCENTE);
                                break;
                            case 2:
                                e.getSharedPreferences().setUserType(AppUserType.FAMIGLIA);
                                break;
                            case 3:
                                e.getSharedPreferences().setUserType(AppUserType.ALTRO);
                                break;
                        }

                        onClickListener.onClick(dialog, which);
                    }
                }, new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                        }
                        return true;
                    }
                });


    }

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
                    c.add(Calendar.DAY_OF_MONTH, -2);
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
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_update2);
        obj = new LayoutObjs_activity_splash_update2_xml(this);

        Drawable rightArrow = getResources().getDrawable(R.drawable.fermi_tivoli_foto);

        // setting the opacity (alpha)
        if (rightArrow != null) {
            rightArrow.setAlpha(100);

            // setting the images on the ImageViews
            obj.listViewCircolariDelGiorno.setBackground(rightArrow);
        }


        obj.txtInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainMenuActivity.startMainActivity(SplashUpdateActivity.this);
                closed = true;
                finish();
            }
        });

        obj.listViewCircolariDelGiorno.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainMenuActivity.startMainActivity(SplashUpdateActivity.this, StringsMenuPrincipale.CIRCOLARI_DI_OGGI_6);
                closed = true;
                finish();

            }
        });

        //start service
        Intent serviceIntent = new Intent(this, UpdateService.class);
        startService(serviceIntent);

        final AppUserType userType = getSharedPreferences().getUserType();
        if (userType == null) {
            chooseUserType(this, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (savedInstanceState == null) {
                        MainMenuActivity.startMainActivity(SplashUpdateActivity.this);
                        closed = true;
                        finish();
                    }
                }
            });
        } else {
            //attende 15 secondi
            final int SECONDS = 15;
            obj.progressBar2.setIndeterminate(false);
            obj.progressBar2.setMax(SECONDS);


            Thread t = new Thread() {
                @Override
                public void run() {
                    final List<CircolareDB> circolari = getCircolariDataCorrente();
                    if (circolari.size() == 0) {
                        MainMenuActivity.startMainActivity(SplashUpdateActivity.this);
                    } else {
                        CircolariListAdapterShort a = new CircolariListAdapterShort(getActivity(), circolari);
                        obj.listViewCircolariDelGiorno.setAdapter(a);

                        for (int i = 0; i <= SECONDS && !closed; i = i + 1) {
                            final int finalI = i;
                            ThreadUtil.runOnUiThreadAndWait(SplashUpdateActivity.this, new Runnable() {
                                @Override
                                public void run() {
                                    obj.txtInfo.setText("Continua (" + (SECONDS - finalI) + ") >>");
                                    obj.progressBar2.setProgress(finalI);
                                }
                            });
                            ThreadUtil.sleep(1000);
                        }
                        if (closed) return;
                        MainMenuActivity.startMainActivity(SplashUpdateActivity.this);
                        closed = true;
                        finish();
                    }
                }
            };


            t.start();
        }
    }
}
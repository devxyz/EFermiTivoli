package it.gov.fermitivoli.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import it.gov.fermitivoli.api.AbstractActivity;
import it.gov.fermitivoli.R;
import it.gov.fermitivoli.fragment.utils.ExternalDataSync_AsyncTask;
import it.gov.fermitivoli.layout.LayoutObjs_activity_splash_update2_xml;
import it.gov.fermitivoli.util.DialogUtil;

/**
 * Created by stefano on 10/09/15.
 */
public class SplashUpdateActivity extends AbstractActivity {
    private ExternalDataSync_AsyncTask updater;
    private LayoutObjs_activity_splash_update2_xml obj;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_update2);
        obj = new LayoutObjs_activity_splash_update2_xml(this);

        if (!ExternalDataSync_AsyncTask.shouldUpdate(this)) {

            startApplication();
            return;
        }

        if (!ExternalDataSync_AsyncTask.isNetworkAvailable(this)) {
            //DialogUtil.openErrorDialog(SplashUpdateActivity.this, "Errore", "Connessione non disponibile",new IllegalArgumentException("Connessione non presente"));

            obj.progressBar9.setVisibility(View.GONE);
            //obj.txtInfo.setVisibility(View.GONE);
            //obj.txtInfo.setText("Accedi comunque");
            obj.textViewMsgUpdate.setText("Connessione non disponibile. Impossibile effettuare l'aggiornamento.");
            obj.txtInfo.setText("Accedi comunque >>");
        }


        updater = new ExternalDataSync_AsyncTask(this) {
            @Override
            protected void onPostExecute(ExternalDataSync_Container c) {
                if (isCancelled())
                    return;
                if (c.containsErrors()) {
                    DialogUtil.openErrorDialog(SplashUpdateActivity.this, "Errore", "Errore nell'aggiornamento dati", c.composeError());

                    obj.progressBar9.setVisibility(View.GONE);
                    //obj.txtInfo.setVisibility(View.GONE);
                    //obj.txtInfo.setText("Accedi comunque");
                    obj.textViewMsgUpdate.setText("Errori durante l'aggiornamento");
                    obj.txtInfo.setText("Accedi comunque >>");
                } else {
                    
                    startApplication();
                    /*obj.textViewMsgUpdate.setText(" ");
                    obj.progressBar9.setVisibility(View.GONE);
                    obj.txtInfo.setText("Aggiornamento riuscito. Accedi >");
                    obj.txtInfo.setBackgroundColor(getResources().getColor(R.color.color_blue_scuro));*/
                }
                updater = null;


            }
        };
        updater.execute();

        obj.txtInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (updater != null) {
                    updater.cancel(true);
                    updater = null;
                }
                startApplication();
            }
        });


    }

    private void startApplication() {

        Intent i = new Intent(SplashUpdateActivity.this, MainMenuActivity.class);
        startActivity(i);
        finish();
    }
}
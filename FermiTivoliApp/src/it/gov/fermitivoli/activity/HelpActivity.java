package it.gov.fermitivoli.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import it.gov.fermitivoli.api.AbstractActivity;
import it.gov.fermitivoli.R;
import it.gov.fermitivoli.layout.LayoutObjs_activity_help_xml;

/**
 * Created by stefano on 24/08/15.
 */
public class HelpActivity extends AbstractActivity {
    private static final String KEY_PARAMETER = "IMAGE_ID";
    private LayoutObjs_activity_help_xml LAYOUT_OBJs;   //***************************

    public static void setParameter(Intent i, int imageId) {
        i.putExtra(KEY_PARAMETER, imageId);
    }

    public static int getParameter(Intent i) {
        final Bundle extras = i.getExtras();
        if (extras == null) return -1;
        return extras.getInt(KEY_PARAMETER, -1);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        //ON CREATE method
        //**************************
        LAYOUT_OBJs = new LayoutObjs_activity_help_xml(this);
        //**************************

        final int imageId = getParameter(getIntent());
        if (imageId >= 0) {
            LAYOUT_OBJs.xyz.setBackgroundResource(imageId);
        }


        LAYOUT_OBJs.buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        LAYOUT_OBJs.xyz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }



}
package it.gov.fermitivoli.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import it.gov.fermitivoli.R;
import it.gov.fermitivoli.layout.LayoutObjs_activity_viewtext_xml;
import it.gov.fermitivoli.listener.OnClickListenerViewErrorCheck;

/**
 * Created by stefano on 04/07/15.
 */
public class ViewTextActivity extends Activity {

    public static final String KEY_TITLE = "TITLE";
    public static final String KEY_TEXT = "TEXT";
    private LayoutObjs_activity_viewtext_xml LAYOUT_OBJs;   //***************************
    private String title;
    private String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewtext);
        LAYOUT_OBJs = new LayoutObjs_activity_viewtext_xml(this);

        final Intent intent = getIntent();
        if (intent != null) {
            title = intent.getStringExtra(KEY_TITLE);
            text = intent.getStringExtra(KEY_TEXT);

        } else {

            if (savedInstanceState != null) {
                text = savedInstanceState.getString(KEY_TEXT);
                title = savedInstanceState.getString(KEY_TITLE);
            }
        }

        LAYOUT_OBJs.textView11.setText(title);
        LAYOUT_OBJs.textView12.setText(text);
        LAYOUT_OBJs.imageButton2.setOnClickListener(new OnClickListenerViewErrorCheck(ViewTextActivity.this) {
            @Override
            protected void onClickImpl(View v) throws Throwable {
                finish();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_TEXT, text);
        outState.putString(KEY_TITLE, title);
    }


}

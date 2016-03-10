package it.gov.fermitivoli.dialog;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import it.gov.fermitivoli.api.AbstractActivity;
import it.gov.fermitivoli.R;
import it.gov.fermitivoli.api.AbstractDialog;
import it.gov.fermitivoli.layout.LayoutObjs_dialog_view_html_xml;
import it.gov.fermitivoli.listener.OnClickListenerViewErrorCheck;
import it.gov.fermitivoli.util.ScreenUtil;

/**
 * Created by stefano on 25/02/15.
 */
@Deprecated
public class HtmlPageDialog extends AbstractDialog {
    private LayoutObjs_dialog_view_html_xml LAYOUT_OBJs;

    public HtmlPageDialog(final AbstractActivity context, String title, String htmlText, final String url) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_view_html);


        //**************************
        LAYOUT_OBJs = new LayoutObjs_dialog_view_html_xml(this);
        //**************************
        if (htmlText == null && url == null) {
            throw new NullPointerException("Specificare url o testo");
        }

        setCancelable(true);
        setCanceledOnTouchOutside(true);
        //imageLoader.clearCache();

        if (htmlText != null) {
            LAYOUT_OBJs.textViewTitle.setText(title);
            LAYOUT_OBJs.textViewDescrizione.loadData(htmlText, "text/html", "UTF-8");
        } else {
            LAYOUT_OBJs.textViewTitle.setText(title);
            LAYOUT_OBJs.textViewDescrizione.loadUrl(url);

        }

        LAYOUT_OBJs.buttonOK.setOnClickListener(new OnClickListenerViewErrorCheck(context.getActivity()) {
            @Override
            public void onClickImpl(View v) {
                dismiss();
            }
        });

        if (url != null)
            LAYOUT_OBJs.buttonClose.setVisibility(View.VISIBLE);
        else
            LAYOUT_OBJs.buttonClose.setVisibility(View.GONE);

        LAYOUT_OBJs.buttonClose.setOnClickListener(new OnClickListenerViewErrorCheck(context.getActivity()) {
            @Override
            public void onClickImpl(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                getOwnerActivity().startActivity(intent);
                // dismiss();
            }
        });


        ScreenUtil.fullScreen(this);


    }


}

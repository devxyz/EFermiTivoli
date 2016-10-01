package it.gov.fermitivoli.dialog;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import it.gov.fermitivoli.R;
import it.gov.fermitivoli.api.AbstractDialog;
import it.gov.fermitivoli.api.AbstractFragment;
import it.gov.fermitivoli.cache.UrlImageLoader;
import it.gov.fermitivoli.dao.NewsDB;
import it.gov.fermitivoli.layout.LayoutObjs_dialog_news_details_xml;
import it.gov.fermitivoli.listener.OnClickListenerDialogErrorCheck;
import it.gov.fermitivoli.listener.OnClickListenerViewErrorCheck;
import it.gov.fermitivoli.util.*;

import java.util.Locale;

/**
 * Created by stefano on 25/02/15.
 */
public class NewsDetailsDialog extends AbstractDialog {
    public UrlImageLoader imageLoader;
    private LayoutObjs_dialog_news_details_xml LAYOUT_OBJs;
    private TextToSpeech engine;
    private ProgressDialog dialog;

    public NewsDetailsDialog(final AbstractFragment context, final NewsDB pp) {
        super(context.getMainActivity());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_news_details);

        //**************************
        LAYOUT_OBJs = new LayoutObjs_dialog_news_details_xml(this);
        //**************************
        Point size = ScreenUtil.getSize(context.getActivity());
        imageLoader = new UrlImageLoader(context, size.x, size.y, R.drawable.clessidra_30x30);
        //imageLoader.clearCache();
        ScreenUtil.fullScreen(this);


        engine = new TextToSpeech(getMainActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                Log.d("Speech", "OnInit - Status [" + status + "]");

                if (status == TextToSpeech.SUCCESS) {
                    Log.d("Speech", "Success!");
                    engine.setLanguage(Locale.ITALY);
                    LAYOUT_OBJs.imageVoice.setEnabled(true);
                } else {
                    DialogUtil.openAlertDialog(getMainActivity(), "Errore", "Impossibile avviare la sintesi vocale");
                    engine = null;
                }
            }
        });


        if (pp != null) {
            final String title = C_TextUtil.normalizeTextFromHtml(pp.getTitolo());


            LAYOUT_OBJs.textDate.setText(C_DateUtil.toDDMMYYY_HHMMSS(pp.getPubDate()));


            WebView x = LAYOUT_OBJs.textViewDescrizione;
            final String data = "<h2>" + title + "</h2><hr>" +
                    C_TextUtil.normalizeTextFromHtml(pp.getTesto());
            x.loadData(data, "text/html", "utf-8");
            if (DebugUtil.DEBUG__RssDetailsDialog)
                System.out.println(data);


            LAYOUT_OBJs.buttonClose.setOnClickListener(new OnClickListenerViewErrorCheck(context.getActivity()) {
                @Override
                public void onClickImpl(View v) {
                    dismiss();
                }
            });

            final OnClickListenerViewErrorCheck l = new OnClickListenerViewErrorCheck(context.getActivity()) {
                @Override
                public void onClickImpl(View v) {
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(pp.getLink())));
                }
            };

            final NewsDB c1 = pp;
            LAYOUT_OBJs.imageButtonWWW.setOnClickListener(new OnClickListenerViewErrorCheck(NewsDetailsDialog
                    .this.getOwnerActivity()) {
                private NewsDB c = c1;

                @Override
                protected void onClickImpl(View v) throws Throwable {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(c.getLink()));
                    getMainActivity().startActivity(i);
                }
            });
            LAYOUT_OBJs.imageVoice.setOnClickListener(new OnClickListenerViewErrorCheck(getMainActivity()) {
                @Override
                protected void onClickImpl(View v) throws Throwable {
                    try {

                        String normalizedText = C_TextUtil.normalize_forTextToSpeech(C_TextUtil.normalizeTextAndLineFeed_forTextCircolari(C_TextUtil.extractContentFromHTML(pp.getTesto()), false));
                        final String msg = "Lettura notizia " + pp.getTitolo() + " del " + C_DateUtil.toDDMMYYY(pp.getPubDate());

                        dialog = DialogUtil.openProgressDialog(getMainActivity(), "Sintesi vocale in corso", pp.getTitolo(), "Ok", new OnClickListenerDialogErrorCheck(getMainActivity()) {
                            @Override
                            protected void onClickImpl(DialogInterface dialog, int which) throws Throwable {
                                engine.stop();
                            }
                        }, new OnKeyListener() {
                            @Override
                            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                if (keyCode == KeyEvent.KEYCODE_BACK) {
                                    onStop();
                                }
                                return true;
                            }
                        });

                        engine.setPitch(1.0f);
                        engine.setSpeechRate(1.5f);

                        engine.speak(msg + ".\n" + normalizedText, TextToSpeech.QUEUE_FLUSH, null);


                    } catch (Throwable e) {
                        if (dialog != null)
                            dialog.cancel();
                        dialog = null;
                        if (engine != null) {
                            engine.stop();
                        }

                    }

                }
            });


        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (engine != null) {
            engine.stop();
            engine.shutdown();
        }
        if (dialog != null) {
            dialog.cancel();
            dialog = null;
        }
    }


}

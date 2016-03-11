package it.gov.fermitivoli.dialog;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import it.gov.fermitivoli.R;
import it.gov.fermitivoli.activity.ViewTextActivity;
import it.gov.fermitivoli.api.AbstractDialog;
import it.gov.fermitivoli.api.AbstractFragment;
import it.gov.fermitivoli.cache.AsyncUrlLoaderCallbackDialogDB;
import it.gov.fermitivoli.cache.UrlFileCache;
import it.gov.fermitivoli.cache.UrlImageLoader;
import it.gov.fermitivoli.dao.CircolareDB;
import it.gov.fermitivoli.dao.DaoSession;
import it.gov.fermitivoli.layout.LayoutObjs_dialog_circolari_details_xml;
import it.gov.fermitivoli.listener.OnClickListenerDialogErrorCheck;
import it.gov.fermitivoli.listener.OnClickListenerViewErrorCheck;
import it.gov.fermitivoli.model.C_NormalizedURL;
import it.gov.fermitivoli.util.*;

import java.io.File;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by stefano on 25/02/15.
 */
public class CircolariDetailsDialog extends AbstractDialog {
    private static final int EXPIRE_TIME_DAYS = 15;
    public UrlImageLoader imageLoader;
    private LayoutObjs_dialog_circolari_details_xml LAYOUT_OBJs;
    private TextToSpeech engine;
    private ProgressDialog dialog;

    public CircolariDetailsDialog(final AbstractFragment context, final CircolareDB pp) {
        super(context.getMainActivity());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_circolari_details);

        //**************************
        LAYOUT_OBJs = new LayoutObjs_dialog_circolari_details_xml(this);
        //**************************
        Point size = ScreenUtil.getSize(context.getActivity());
        imageLoader = new UrlImageLoader(context, size.x, size.y, R.drawable.clessidra_30x30);
        //imageLoader.clearCache();
        ScreenUtil.fullScreen(this);

        if (pp != null) {
            final String title = C_TextUtil.normalizeTextFromHtml(pp.getTitolo());

            LAYOUT_OBJs.textDate.setText(C_DateUtil.toDDMMYYY(pp.getData()));
            final String t = C_TextUtil.normalize_UTF8__to__ASCII(pp.getTesto());
            final String normalizedText = C_TextUtil.normalize_forTextToSpeech(C_CircolariUtil.normalizeTextAndLineFeed_forTextCircolari(t, true));
            final String text = "<h2>" + title + "</h2><hr>" +
                    normalizedText.replace("\n", "<br>");
            LAYOUT_OBJs.testoCircolare.setText(Html.fromHtml(text));

            LAYOUT_OBJs.buttonClose.setOnClickListener(new OnClickListenerViewErrorCheck(context.getActivity()) {
                @Override
                public void onClickImpl(View v) {
                    dismiss();
                }
            });
            LAYOUT_OBJs.buttonPDF.setEnabled(false);

            engine = new TextToSpeech(getMainActivity(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    Log.d("Speech", "OnInit - Status [" + status + "]");

                    if (status == TextToSpeech.SUCCESS) {
                        Log.d("Speech", "Success!");
                        engine.setLanguage(Locale.ITALY);
                        LAYOUT_OBJs.buttonPDF.setEnabled(true);
                    } else {
                        DialogUtil.openAlertDialog(getMainActivity(), "Errore", "Impossibile avviare la sintesi vocale");
                        engine = null;
                    }
                }
            });

            LAYOUT_OBJs.buttonPDF.setOnClickListener(new MyOnClickListenerViewErrorCheck(pp));
            LAYOUT_OBJs.buttonVoice.setOnClickListener(new OnClickListenerViewErrorCheck(getMainActivity()) {
                @Override
                protected void onClickImpl(View v) throws Throwable {
                    try {
                        final String msg = "Lettura circolare " + pp.getTitolo() + " del " + C_DateUtil.toDDMMYYY(pp.getData());

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

                        engine.speak(msg+".\n"+normalizedText, TextToSpeech.QUEUE_FLUSH, null);

                        /*final String[] tt = normalizedText.split("[\n\\.]");
                        for (String phrase : tt) {
                            engine.speak(phrase, TextToSpeech.QUEUE_FLUSH, null);
                        }*/
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

    private class MyOnClickListenerViewErrorCheck extends OnClickListenerViewErrorCheck {
        private CircolareDB c;

        public MyOnClickListenerViewErrorCheck(CircolareDB c) {
            super(CircolariDetailsDialog.this.getOwnerActivity());
            this.c = c;
        }

        @Override
        protected void onClickImpl(View v) throws Throwable {
            final UrlFileCache cache = getMainActivity().getCache();
            addTask(cache.downloadFileAsync(new C_NormalizedURL(c.getUrl()),
                    EXPIRE_TIME_DAYS, TimeUnit.DAYS,
                    new AsyncUrlLoaderCallbackDialogDB(getMainActivity()) {
                        @Override
                        public void onLoadFinishedImpl(C_NormalizedURL url, final File f, final DaoSession session) throws Throwable {


                            //apre file esterno
                            getMainActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    Uri path = Uri.fromFile(f);
                                    Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                                    pdfIntent.setDataAndType(path, "application/pdf");
                                    pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    try {
                                        getMainActivity().startActivity(pdfIntent);
                                    } catch (Throwable e) {
                                        Intent i = new Intent(getMainActivity(), ViewTextActivity.class);
                                        i.putExtra(ViewTextActivity.KEY_TITLE, c.getTitolo());
                                        i.putExtra(ViewTextActivity.KEY_TEXT, c.getTesto());
                                        getMainActivity().startActivity(i);
                                    }


                                }
                            });
                        }

                        @Override
                        public void onLoadErrorImpl(C_NormalizedURL url, final Throwable error) {

                            getMainActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DialogUtil.openErrorDialog(getMainActivity(), "Errore di apertura", error.getMessage(), error);
                                }
                            });

                        }

                        @Override
                        public void onDownloadingImpl(C_NormalizedURL url) {
                        }

                        @Override
                        public void onCancelImpl(C_NormalizedURL url) {
                        }

                        @Override
                        public String getWaitingTitle(C_NormalizedURL url) {
                            return "Circolari del dirigente";
                        }

                        @Override
                        public String getWaitingMessage(C_NormalizedURL url) {
                            return "Caricamento elenco delle circolari. Attendere...";
                        }
                    }));

        }
    }
}

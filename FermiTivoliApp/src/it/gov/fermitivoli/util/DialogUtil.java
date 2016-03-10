package it.gov.fermitivoli.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.text.Html;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import it.gov.fermitivoli.R;
import it.gov.fermitivoli.listener.OnClickListenerDialogErrorCheck;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by stefano on 12/03/15.
 */
public class DialogUtil {


    public static void openInfoDialog(final Activity context, final String title, final Map<String, String> message) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                context);
        builderSingle.setIcon(R.drawable.icon_info);
        builderSingle.setTitle(title);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                context,
                android.R.layout.select_dialog_item);
        for (String m : message.keySet()) {
            arrayAdapter.add(m);
        }

        builderSingle.setNegativeButton("OK",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(arrayAdapter,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = arrayAdapter.getItem(which);
                        AlertDialog.Builder builderInner = new AlertDialog.Builder(
                                context);

                        TextView msg = new TextView(context);
                        msg.setText(Html.fromHtml("<html><b>" + strName + "</b><br> <i>" + message.get(strName) + "</i> </html>"));
                        //builderInner.setMessage(""+strName);
                        builderInner.setView(msg);
                        builderInner.setTitle("Dettagli");
                        builderInner.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(
                                            DialogInterface dialog,
                                            int which) {
                                        dialog.dismiss();
                                    }
                                });
                        builderInner.show();
                    }
                });
        AlertDialog show = builderSingle.show();

    }

    public static void openInfoDialog(final Activity context, final String title, final ArrayList<String> message) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                context);
        builderSingle.setIcon(R.drawable.icon_info);
        builderSingle.setTitle(title);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                context,
                android.R.layout.select_dialog_item);
        for (String m : message) {
            arrayAdapter.add(m);
        }

        builderSingle.setNegativeButton("OK",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(arrayAdapter,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog show = builderSingle.show();


    }


    public static void openChooseDialog(final Activity coontext, final String title, final CharSequence[] values,
                                        final DialogInterface.OnClickListener onClickListener,
                                        final Dialog.OnKeyListener keyListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(coontext);
        builder.setTitle(title);
        //builder.setMessage("L'applicazione impostera' le viste in modo adeguato alla scelta fatta. Potrai sempre cambiare modalita' dal menu.");

        builder.setItems(values, onClickListener);
        final AlertDialog dialog = builder.create();
        if (keyListener != null)
            dialog.setOnKeyListener(keyListener);
        dialog.show();
    }

    public static ProgressDialog openProgressDialog(final Activity context, final String title, final String message, String buttonLabel,
                                                    final OnClickListenerDialogErrorCheck listener,
                                                    final Dialog.OnKeyListener keyListener
    ) {
        ProgressDialog progress = new ProgressDialog(context);
        progress.setTitle(title);
        progress.setMessage(message);

        if (listener != null) {
            progress.setButton(DialogInterface.BUTTON_NEGATIVE, buttonLabel, listener);
            progress.setCancelable(true);
        }

        if (keyListener != null) {
            progress.setOnKeyListener(keyListener);
        }
        progress.show();
        return progress;
    }

    public static void openInfoDialog(final Activity context, final String title, final String message) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage(message);
        builder1.setTitle(title);
        builder1.setCancelable(true);
        builder1.setIcon(R.drawable.dialog_info);
        builder1.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public static void openAskDialog(final Activity context, final String title, final String message, final Runnable yes, final Runnable no) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        yes.run();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        no.run();
                        break;
                    default:
                        no.run();
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message).setTitle(title).setPositiveButton("Si", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    public static AlertDialog openCancelDialog(final Activity context, final String title, final String message, final Runnable cancel) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        cancel.run();
                        break;

                    default:
                        cancel.run();
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog.Builder interrompi = builder.setMessage(message).setTitle(title).setPositiveButton("Interrompi", dialogClickListener);
        final AlertDialog d = interrompi.show();
        return d;
    }

    public static void openAlertDialog(final Activity context, final String title, final String message) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage(message);
        builder1.setTitle(title);
        builder1.setCancelable(true);
        builder1.setIcon(R.drawable.dialog_alert);
        builder1.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public static void openErrorDialog(final Activity context, final String title, final String message, final Throwable error) {
        if (error != null)
            error.printStackTrace();

        if (DebugUtil.DEBUG__DialogUtil) {
            Log.d("DIALOG_UTIL", title + ":" + message, error);
        }
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        Log.e(DialogUtil.class.getSimpleName(), message);
        final String message1 = error == null ? "" : error.getMessage();
        final String message2 = "<b>" + message + "</b>" + "<br><small>" + message1.replace("\n", "<br> ** ") + "</small>";
        TextView msg = new TextView(context);
        msg.setPadding(10, 10, 10, 10);
        msg.setText(Html.fromHtml("<html>" + message2 + "</html>"));

        //builder1.setMessage("<html>"+message2+"</html>");
        builder1.setView(msg);
        builder1.setTitle(title);
        builder1.setCancelable(true);
        builder1.setIcon(R.drawable.dialog_error);
        builder1.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();

    }

    private static class MyContainer<T> {
        volatile T value;
    }


}

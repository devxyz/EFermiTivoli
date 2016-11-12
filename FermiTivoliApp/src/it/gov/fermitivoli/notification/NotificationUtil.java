package it.gov.fermitivoli.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import it.gov.fermitivoli.R;
import it.gov.fermitivoli.activity.MainMenuActivity;

/**
 * raccolta delle notifiche
 */

public class NotificationUtil {
    public static NotificationMessage updateProcessMessage() {
        return new NotificationMessage() {
            @Override
            public int getNotificationID() {
                return 2;
            }

            @Override
            protected void prepare(Notification.Builder build, Context ctx) {
                Intent intent = new Intent(ctx, MainMenuActivity.class);
                PendingIntent pIntent = PendingIntent.getActivity(ctx, 0, intent, 0);

                build.setContentTitle("ITCG E FERMI di Tivoli")
                        .setContentText("Avvio servizio di aggiornamento dati...")
                        .setSmallIcon(R.drawable.update_128x128)
                        .setContentIntent(pIntent)
                        .setAutoCancel(true);
            }
        };
    }

    public static NotificationMessage newDataAvailableMessage(final int num) {
        return new NotificationMessage() {
            @Override
            public int getNotificationID() {
                return 3;
            }

            @Override
            protected void prepare(Notification.Builder build, Context ctx) {
                Intent intent = new Intent(ctx, MainMenuActivity.class);
                PendingIntent pIntent = PendingIntent.getActivity(ctx, 0, intent, 0);

                build.setContentTitle("ITCG E FERMI di Tivoli")
                        .setContentText(num + " nuove notizie dall'Istituto. Click per aprire l'applicazione.")
                        .setSmallIcon(R.drawable.logo_fermi_150x150_bordato)
                        .setContentIntent(pIntent)
                        .setAutoCancel(true);
            }
        };
    }

    public static NotificationMessage errorMessage(final Throwable e) {
        return new NotificationMessage() {
            @Override
            public int getNotificationID() {
                return 1;
            }

            @Override
            protected void prepare(Notification.Builder build, Context ctx) {
                Intent intent = new Intent(ctx, MainMenuActivity.class);
                PendingIntent pIntent = PendingIntent.getActivity(ctx, 0, intent, 0);

                build.setContentTitle("ITCG E FERMI di Tivoli")
                        .setContentText("Errore durante l'aggiornamento:" + e.getMessage() + ". Click per aprire l'applicazione.")
                        .setSmallIcon(R.drawable.logo_fermi_150x150_bordato)
                        .setContentIntent(pIntent)
                        .setAutoCancel(true);
            }
        };
    }


}

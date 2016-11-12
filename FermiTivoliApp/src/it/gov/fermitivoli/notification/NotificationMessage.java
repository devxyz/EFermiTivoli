package it.gov.fermitivoli.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

/**
 * Created by stefano on 07/11/2016.
 */
public abstract class NotificationMessage {
    public abstract int getNotificationID();

    /**
     * visualizza la notifica
     *
     * @param ctx
     */
    public final void show(Context ctx) {
        NotificationManager notificationManager =
                (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        final Notification.Builder subject = new Notification.Builder(ctx);
        prepare(subject, ctx);
        Notification n = subject.getNotification();
        notificationManager.notify(getNotificationID(), n);
    }

    protected abstract void prepare(Notification.Builder build, Context ctx);

    /**
     * cancella la notifica
     *
     * @param ctx
     */
    public void cancel(Context ctx) {
        NotificationManager notificationManager =
                (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        //notificationManager.cancel(UpdateService.ID_NOTIFICA_AVVIA_AGGIORNAMENTO);
        notificationManager.cancel(getNotificationID());

    }
}

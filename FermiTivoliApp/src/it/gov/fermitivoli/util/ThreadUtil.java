package it.gov.fermitivoli.util;

import android.app.Activity;
import android.os.Looper;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * Created by stefano on 12/06/15.
 */
public class ThreadUtil {
    public static void sleep(long l){
        try {
            Thread.sleep(l);
        } catch (InterruptedException e) {

        }
    }
    public static boolean isUIThread() {
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            // On UI thread.
            return true;
        } else {
            // Not on UI thread.
            return false;
        }
    }

    public static void printThreadStatus() {
        final Map<Thread, StackTraceElement[]> as = Thread.getAllStackTraces();
        StringBuilder sb = new StringBuilder("THREAD STATUS\n");
        for (Map.Entry<Thread, StackTraceElement[]> e : as.entrySet()) {

            if (e.getValue().length > 0) {
                sb.append("  >  ").append(e.getKey().toString()).append("\n");
                for (StackTraceElement s : e.getValue()) {
                    sb.append("      - ").append(s).append("\n");
                }
            } else
                sb.append("  >  ").append(e.getKey().toString()).append(" no trace").append("\n");
        }
        System.out.println(sb);
    }

    /**
     * esegue in modalita' asincrona nel thread UI
     *
     * @param a
     * @param r
     */
    public static void runOnUiThreadAsync(Activity a, Runnable r) {
        a.runOnUiThread(r);
    }

    /**
     * esegue in modalita' sincrona nel thread UI (attende l'esecuzione
     *
     * @param a
     * @param r
     */
    public static void runOnUiThreadAndWait(Activity a, final Runnable r) {
        final CountDownLatch waitLatch = new CountDownLatch(1);
        a.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    r.run();
                } finally {
                    waitLatch.countDown();
                }
            }
        });
        try {
            waitLatch.await();
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }

    }


    /**
     * avvia un esecutore per il callable specificato, chiudendo l'esecuore subito dopo
     *
     * @param callable
     * @param <T>
     * @return
     */
    public static <T> FutureTask<T> newThread(Callable<T> callable) {
        FutureTask<T> t = new FutureTask<T>(callable);
        new Thread(t).start();
        return t;
    }


    /**
     * avvia un esecutore per il callable specificato, chiudendo l'esecuore subito dopo
     *
     * @param callable
     * @param <T>
     * @return
     */
    public static <T> Future<T> newThread(Future<T> future, Callable<T> callable) {
        FutureTask<T> t = new FutureTask<T>(callable);
        new Thread(t).start();
        return t;
    }
}

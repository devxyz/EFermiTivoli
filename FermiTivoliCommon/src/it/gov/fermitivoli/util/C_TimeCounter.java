package it.gov.fermitivoli.util;

/**
 * Created by stefano on 05/05/15.
 */
public class C_TimeCounter {
    private long startTime;

    public C_TimeCounter() {
        start();
    }

    public void start() {
        startTime = System.currentTimeMillis();
    }

    public long elapsedMillis() {
        return System.currentTimeMillis() - startTime;
    }

    public String toString() {
        long e = elapsedMillis();
        long sec = e / 1000;
        long min = sec / 60;
        return "Elapsed time [mm.ss,millis: " + min + "." + sec % 60 + "," + e % 1000 + "]";

    }
}

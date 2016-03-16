package it.gov.fermitivoli.server.util;

/**
 * Created by stefano on 14/03/16.
 */
public class DebugUtil {
    private static final boolean DEBUG = true;

    public static void debug(Object... o) {
        if (!DEBUG) return;
        for (Object o1 : o) {
            System.out.print(o1);
        }
    }
}

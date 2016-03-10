package it.gov.fermitivoli.backoffice.utilities;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by stefano on 27/06/15.
 */
public class AsciiUtil {
    public static void main(String[] args) {
        TreeMap<String, String> m = new TreeMap<String, String>();
        m.put("èéêë", "e'");
        m.put("ûù", "u'");
        m.put("ïî", "i'");
        m.put("àâ", "a'");
        m.put("Ô", "o'");

        m.put("ÈÉÊË", "E'");
        m.put("ÛÙ", "U'");
        m.put("ÏÎ", "I'");
        m.put("ÀÂÃ", "A'");
        m.put("Ô", "O'");
        System.out.println(m);

        for (Map.Entry<String, String> e : m.entrySet()) {
            final String key = e.getKey();
            for (int i = 0; i < key.length(); i++) {
                char c = key.charAt(i);
                System.out.println((int) c + " " + c + "->" + e.getValue());
            }
        }

    }
}

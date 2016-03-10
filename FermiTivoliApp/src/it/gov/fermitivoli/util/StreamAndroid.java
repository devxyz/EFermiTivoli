package it.gov.fermitivoli.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by stefano on 11/03/15.
 */
public class StreamAndroid {
    public static String loadFileContent(Reader r) throws IOException {
        StringBuilder sb = new StringBuilder();

        char[] buf = new char[1024];
        int amt = r.read(buf);
        while (amt > 0) {
            sb.append(buf, 0, amt);
            amt = r.read(buf);
        }
        return sb.toString();
    }

    public static String loadFileContent(InputStream r) throws IOException {
        return loadFileContent(new InputStreamReader(r));
    }
}

package it.gov.fermitivoli.server;

import it.gov.fermitivoli.util.C_TextUtil;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by stefano on 18/03/16.
 */
public class GAE_MainTest {
    private static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int len = in.read(buffer);
        while (len != -1) {
            out.write(buffer, 0, len);
            len = in.read(buffer);
        }

    }

    public static void main(String[] args) throws IOException {
        final byte[] xmlContent;
        {
            BufferedInputStream in = new BufferedInputStream(new URL(GAE_Settings.LISTA_NEWS_JOOMLA_URL).openConnection().getInputStream());
            ByteArrayOutputStream out = new ByteArrayOutputStream(10000);
            copy(in, out);
            in.close();
            out.close();
            xmlContent = out.toByteArray();
        }
        System.out.println("XML");

        System.out.println(C_TextUtil.normalizeTextFromHtml(new String(xmlContent)));
        System.out.println();
    }
}

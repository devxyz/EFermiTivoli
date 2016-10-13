package it.gov.fermitivoli.server.test;

import it.gov.fermitivoli.model.rss.RssFeed;
import it.gov.fermitivoli.rss.RssReader;
import it.gov.fermitivoli.server.GAE_Settings;
import it.gov.fermitivoli.util.C_TextUtil;
import org.xml.sax.SAXException;

import java.io.*;
import java.net.URL;

/**
 * Created by stefano on 13/10/16.
 */
public class MainTest {
    private static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int len = in.read(buffer);
        while (len != -1) {
            out.write(buffer, 0, len);
            len = in.read(buffer);
        }

    }

    public static void main(String[] args) throws IOException, SAXException {
        final byte[] xmlContent;
        {
            BufferedInputStream in = new BufferedInputStream(new URL(GAE_Settings.LISTA_NEWS_JOOMLA_URL).openConnection().getInputStream());
            ByteArrayOutputStream out = new ByteArrayOutputStream(10000);
            copy(in, out);
            in.close();
            out.close();
            xmlContent = out.toByteArray();
        }

        String xmlNormalized = C_TextUtil.normalizeTextFromHtml(new String(xmlContent, "UTF-8"));
        xmlNormalized=xmlNormalized.substring(xmlNormalized.indexOf("<rss"));
        System.out.println(xmlNormalized);


        //final URL url = new URL(GAE_Settings.LISTA_NEWS_JOOMLA_URL);
        final RssFeed read = RssReader.read(xmlNormalized);

    }
}

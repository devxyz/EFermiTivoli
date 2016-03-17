package it.gov.fermitivoli.server.util;

import it.gov.fermitivoli.model.C_CircolareDto;
import it.gov.fermitivoli.util.C_TextUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by stefano on 10/08/15.
 */
public class GAE_MainTest {

    private static boolean isNormalized(char c) {
        return !("" + c).equals(C_TextUtil.normalize_UTF8__to__ASCII(c));
    }

    public static void main(String[] args) throws Exception {
        //carica la url
        /*
        URL u = new URL("http://www.fermitivoli.gov.it/joomla/components/com_chronoforms5/chronoforms/pdfs/frm_addCircolari/20140919173016_Avv_10_2014_2015.pdf");

        final HttpURLConnection con = (HttpURLConnection) u.openConnection();
        con.setReadTimeout(10000);*/

        final List<C_CircolareDto> xx = GAE_DownloadCircolari.downloadListaCircolari();
        for (C_CircolareDto f : xx) {
            System.out.println(f.getUrl());
            if (true) {
                URL u = new URL(f.getUrl().getUrl());
                final URLConnection con = u.openConnection();
                final String s = GAE_PdfUtil.extractTextPdf(con.getInputStream());


                //System.out.println(s);
                //System.out.println("================================================");

                Charset utf8charset = Charset.forName("UTF-8");
                Charset iso88591charset = Charset.forName("US-ASCII");

                ByteBuffer inputBuffer = ByteBuffer.wrap(s.getBytes());

                // decode UTF-8
                CharBuffer data = utf8charset.decode(inputBuffer);

                // encode ISO-8559-1
                ByteBuffer outputBuffer = iso88591charset.encode(data);
                byte[] outputData = outputBuffer.array();
                final String s2 = new String(outputData, iso88591charset);

                //System.out.println(normalize_UTF8__to__ASCII(s));

                //System.out.println("================================================");
                //System.out.println("================================================");
                for (int i = 0; i < Math.min(s.length(), s2.length()); i++) {
                    if (s.charAt(i) != s2.charAt(i)) {
                        if (!isNormalized(s.charAt(i)))
                            System.out.println(s.charAt(i) + " " + ((int) s.charAt(i)) + " --> " + s2.charAt(i));
                    }
                }

            }
        }

    }

    public static void main1(String[] args) throws IOException {
        File f = new File("/Users/stefano/Downloads/20140919173016_Avv_10_2014_2015.pdf");
        final FileInputStream in0 = new FileInputStream(f);
        final String s = GAE_PdfUtil.extractTextPdf(new BufferedInputStream(in0));
        in0.close();

        System.out.println(s);
    }
}

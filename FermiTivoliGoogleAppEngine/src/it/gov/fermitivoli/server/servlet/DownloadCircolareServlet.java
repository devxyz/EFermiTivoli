package it.gov.fermitivoli.server.servlet;

import it.gov.fermitivoli.model.C_CircolareDto;
import it.gov.fermitivoli.server.GAE_Settings;
import it.gov.fermitivoli.server.datastore.DataStoreOptimizer;
import it.gov.fermitivoli.server.datastore.IDataStoreOptimizer;
import it.gov.fermitivoli.server.util.GAE_PdfUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

/**
 * Created by stefano on 01/08/15.
 */
@Deprecated
public class DownloadCircolareServlet extends HttpServlet {

    private static final boolean DEBUG = true;

    /**
     * @param key
     * @throws IOException
     */
    public static void doTask(String key) throws IOException {
        final IDataStoreOptimizer ds = DataStoreOptimizer.getInstance();

        if (DEBUG) {
            final String s = "Scarica circolare " + key;
            System.out.println(s);
        }
        if (key == null) {
            if (DEBUG) {
                final String s = "KEY non specificata";
                System.out.println(s);
            }

            return;
        }


        final C_CircolareDto ris;
        //cerca la circolare (se esiste)
        {

            ris = ds.getByKey(key);
            if (ris == null) {
                if (DEBUG) {
                    final String s = "Circolare " + key + " non presente (SKIP)";
                    System.out.println(s);
                }
                return;
            }
            ;

            if (ris.getTesto() != null) {
                if (DEBUG) {
                    final String s = "Circolare " + key + " gia' scaricata (SKIP)";
                    System.out.println(s);
                }
                return;
            }
        }

        //carica il file pdf
        final String text;
        {
            //carica la url
            URL u = new URL(ris.getUrl().getUrl());

            final HttpURLConnection con = (HttpURLConnection) u.openConnection();
            con.setReadTimeout(10000);
            text = GAE_PdfUtil.extractTextPdf(con.getInputStream());
        }

        //aggiorna il file
        {
            if (ris.getTesto() != null) {
                if (DEBUG) {
                    final String s = "Circolare " + key + " gia' scaricata (SKIP)";
                    System.out.println(s);
                }
                return;
            }


            ds.update(key, text);
            if (DEBUG) {
                final String s = "Circolare " + key + " salvata con testo completo";
                System.out.println(s);
            }

        }


    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //check call id
        final String secretKey = request.getParameter("secret-key");
        if (secretKey == null || !secretKey.equals(GAE_Settings.SECRET_KEY)) {
            System.out.println("Secret key error " + new Date());
            return;
        }

        final String[] key = request.getParameter("keys").split(",");
        for (String s : key) {
            s = s.trim();
            if (s.length() == 0) continue;
            doTask(s);
        }


    }
}

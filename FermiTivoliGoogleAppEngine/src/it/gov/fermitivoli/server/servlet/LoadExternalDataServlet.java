package it.gov.fermitivoli.server.servlet;

import it.gov.fermitivoli.model.C_CircolareDto;
import it.gov.fermitivoli.model.C_NewsDto;
import it.gov.fermitivoli.model.C_Pair;
import it.gov.fermitivoli.model.rss.RssFeed;
import it.gov.fermitivoli.model.rss.RssItem;
import it.gov.fermitivoli.rss.RssReader;
import it.gov.fermitivoli.server.GAE_Settings;
import it.gov.fermitivoli.server.datalayer.DataLayerBuilder;
import it.gov.fermitivoli.server.datalayer.impl.circolari.InMemoryCacheLayerCircolareDB;
import it.gov.fermitivoli.server.datalayer.impl.news.InMemoryCacheLayerNewsDB;
import it.gov.fermitivoli.server.model.GAE_CircolareDB_V2;
import it.gov.fermitivoli.server.model.GAE_NewsDB_V2;
import it.gov.fermitivoli.server.util.DebugUtil;
import it.gov.fermitivoli.server.util.GAE_DtoUtil;
import it.gov.fermitivoli.server.util.GAE_PdfUtil;
import it.gov.fermitivoli.server.util.MergeUtil;
import it.gov.fermitivoli.util.C_CircolariUtil;
import it.gov.fermitivoli.util.C_TextUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.xml.sax.SAXException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by stefano on 13/03/16.
 */
public class LoadExternalDataServlet extends HttpServlet {
    private static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int len = in.read(buffer);
        while (len != -1) {
            out.write(buffer, 0, len);
            len = in.read(buffer);
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            final PrintWriter out = resp.getWriter();
            resp.setContentType("text/plain");
            MyToken t = new MyToken();
            t.token = DataLayerBuilder.maxToken();


            aggiornaDbNews(out, t);
            aggiornaDbCircolari(out, t);
            out.println("Max token: " + t.token);


        } catch (Exception e) {
            throw new IOException(e);
        }

    }

    private void aggiornaDbCircolari(PrintWriter resp, MyToken t) throws IOException, SAXException {
        try {
            final InMemoryCacheLayerCircolareDB manager = DataLayerBuilder.getLoaderCircolari();

            //scarica lista circolari aggiornate
            //==================================================================================
            final List<C_CircolareDto> circolariWEB = downloadListaCircolari();
            //map circolari by key
            final Map<String, C_CircolareDto> circolariWEB_MAP = new TreeMap<>();
            for (C_CircolareDto c : circolariWEB) {
                circolariWEB_MAP.put(c.getKey(), c);
            }

            DebugUtil.debug("Circolari totali del sito web: " + circolariWEB.size());


            //scarica le circolari del db
            //==================================================================================
            final Map<String, GAE_CircolareDB_V2> circolariDB = manager.allEntitiesMapByKey();


            //corrispondenza
            final MergeUtil<GAE_CircolareDB_V2, C_CircolareDto> merge = MergeUtil.matchKey(circolariDB, circolariWEB_MAP);

            int rimosse = 0;
            int aggiunte = 0;

            //cancella circolari vecchie
            //==================================================================================
            final List<GAE_CircolareDB_V2> circolariDBnonPiuValide = merge.onlyT1;
            for (GAE_CircolareDB_V2 x : circolariDBnonPiuValide) {
                if (!x.isFlagDelete()) {
                    x.setFlagDelete(true);
                    t.token++;
                    x.setToken(t.token);
                    manager.update(x);
                    rimosse++;
                }
            }

            //aggiorna quelle comuni ma che sono con flag cancellato
            //==================================================================================
            int aggiornate=0;
            final List<C_Pair<GAE_CircolareDB_V2, C_CircolareDto>> circolariDBRimaste = merge.common;
            for (C_Pair<GAE_CircolareDB_V2, C_CircolareDto> v : circolariDBRimaste) {
                final GAE_CircolareDB_V2 x = v.a;
                if (x.isFlagDelete()) {
                    x.setFlagDelete(false);
                    t.token++;
                    x.setToken(t.token);
                    manager.update(x);
                    aggiornate++;
                }
            }

            //aggiunge le nuove
            //==================================================================================
            final List<C_CircolareDto> circolariNuoveDaInserire = merge.onlyT2;

            for (C_CircolareDto x : circolariNuoveDaInserire) {
                //carica il file pdf
                final String text;

                //carica la url
                URL u = new URL(x.getUrl().getUrl());

                final HttpURLConnection con = (HttpURLConnection) u.openConnection();
                con.setReadTimeout(10000);
                text = GAE_PdfUtil.extractTextPdf(con.getInputStream());
                x.setTesto(text);

                GAE_CircolareDB_V2 x2 = new GAE_CircolareDB_V2();
                GAE_DtoUtil.copy(x, x2, false);
                t.token++;
                x2.setToken(t.token);
                manager.insert(x2);
            }


            {
                final PrintWriter out = resp;
                out.println("AGGIUNTE " + rimosse + " circolari");
                out.println("RIMOSSE " + aggiunte + " circolari");
                out.println("RIPRISTINATE DA FLAG DELETE " + aggiornate + " circolari");
                out.println("CONFERMATE " + merge.common.size() + " circolari");
            }


        } catch (Throwable e) {
            e.printStackTrace();
            throw new IOException(e);
        }

    }

    private List<C_CircolareDto> downloadListaCircolari() throws IOException {
        final Document parse = Jsoup.parse(new URL(GAE_Settings.LISTA_CIRCOLARI_JOOMLA_URL), GAE_Settings.LISTA_CIRCOLARI_TIMEOUT_MILLIS);
        return C_CircolariUtil.parseFromHtmlFromJoomla(parse, GAE_Settings.LISTA_CIRCOLARI_BASEURL);

    }

    private void aggiornaDbNews(PrintWriter pw, MyToken t) throws IOException, SAXException {

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
        //cerca l'inizio degli rss

        //se non c'e' il file xml salta
        final int beginIndex = xmlNormalized.indexOf("<rss");
        if (beginIndex < 0) return;
        xmlNormalized = xmlNormalized.substring(beginIndex);


        //final URL url = new URL(GAE_Settings.LISTA_NEWS_JOOMLA_URL);
        final InMemoryCacheLayerNewsDB loader = DataLayerBuilder.getLoaderNews();
        final RssFeed read = RssReader.read(xmlNormalized);
        //final RssFeed read = RssReader.read(url);

        final Map<String, RssItem> n2 = new TreeMap<>();
        for (RssItem rssItem : read.getRssItems()) {
            final String k = C_NewsDto.composeKey(rssItem.getTitle(), rssItem.getPubDate());
            n2.put(k, rssItem);
        }
        final Map<String, GAE_NewsDB_V2> n1 = loader.allEntitiesMapByKey();


        //confronta i dati
        final MergeUtil<GAE_NewsDB_V2, RssItem> merge = MergeUtil.matchKey(n1, n2);


        int aggiunte = 0;
        int rimosse = 0;

        //segna le news da rimuovere
        final List<GAE_NewsDB_V2> toBeRemoved = merge.onlyT1;
        for (GAE_NewsDB_V2 x : toBeRemoved) {
            if (!x.isFlagDelete()) {
                x.setFlagDelete(true);

                t.token++;
                x.setToken(t.token);

                loader.update(x);
                rimosse++;
            }
        }

        //aggiunge le news non presenti
        final List<RssItem> toBeAdded = merge.onlyT2;
        for (RssItem x : toBeAdded) {
            final String k = C_NewsDto.composeKey(x.getTitle(), x.getPubDate());

            GAE_NewsDB_V2 n = new GAE_NewsDB_V2();
            n.setFlagDelete(false);
            n.setContenuto(x.getContent());
            n.setDataInserimento(new Date());
            n.setFullimageLink(x.getFullimageLink());
            n.setKey(x.getTitle());
            n.setLink(x.getLink());
            n.setThumbimageLink(x.getThumbimageLink());
            n.setTesto(x.getDescription());
            n.setPubDate(x.getPubDate());
            n.setTitolo(x.getTitle());
            n.setKey(k);

            t.token++;
            n.setToken(t.token);


            loader.insert(n);
            aggiunte++;
        }

        pw.println("News aggiunte: " + aggiunte);
        pw.println("News rimosse: " + rimosse);
        pw.println("News confermate nel database: " + merge.common.size());

    }

    private class MyToken {
        long token;
    }
}

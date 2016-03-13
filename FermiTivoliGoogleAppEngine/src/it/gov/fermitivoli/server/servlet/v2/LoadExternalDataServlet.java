package it.gov.fermitivoli.server.servlet.v2;

import it.gov.fermitivoli.model.C_NewsDto;
import it.gov.fermitivoli.model.rss.RssFeed;
import it.gov.fermitivoli.model.rss.RssItem;
import it.gov.fermitivoli.rss.RssReader;
import it.gov.fermitivoli.server.GAE_Settings;
import it.gov.fermitivoli.server.datalayer.DataLayerBuilder;
import it.gov.fermitivoli.server.datalayer.impl.news.InMemoryCacheLayerNewsDB;
import it.gov.fermitivoli.server.model.GAE_NewsDB_V2;
import it.gov.fermitivoli.server.util.MergeUtil;
import org.xml.sax.SAXException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by stefano on 13/03/16.
 */
public class LoadExternalDataServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    private void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int len = in.read(buffer);
        while (len != -1) {
            out.write(buffer, 0, len);
            len = in.read(buffer);
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {

            final byte[]xmlContent;
            {
                BufferedInputStream in = new BufferedInputStream(new URL(GAE_Settings.LISTA_NEWS_JOOMLA_URL).openConnection().getInputStream());
                ByteArrayOutputStream out = new ByteArrayOutputStream(10000);
                copy(in, out);
                in.close();
                out.close();
                xmlContent=out.toByteArray();
            }
            System.out.println("XML");
            System.out.println(new String(xmlContent));
            System.out.println();

            final InMemoryCacheLayerNewsDB loader = DataLayerBuilder.getLoaderNews();
            final RssFeed read = RssReader.read(new String(xmlContent));

            final Map<String, RssItem> n2 = new TreeMap<>();
            for (RssItem rssItem : read.getRssItems()) {
                final String k = C_NewsDto.composeKey(rssItem.getTitle(), rssItem.getPubDate());
                n2.put(k, rssItem);
            }
            final Map<String, GAE_NewsDB_V2> n1 = loader.allEntitiesMap();


            //confronta i dati
            final MergeUtil<GAE_NewsDB_V2, RssItem> merge = MergeUtil.matchKey(n1, n2);

            long token = DataLayerBuilder.maxToken() + 10;

            int aggiunte = 0;
            int rimosse = 0;

            //segna le news da rimuovere
            final List<GAE_NewsDB_V2> toBeRemoved = merge.onlyT1;
            for (GAE_NewsDB_V2 x : toBeRemoved) {
                if (!x.isFlagDelete()) {
                    x.setFlagDelete(true);

                    token++;
                    x.setToken(token);

                    loader.update(x);
                    rimosse++;
                }
            }

            //aggiunge le news non presenti
            final List<RssItem> toBeAdded = merge.onlyT2;
            for (RssItem x : toBeAdded) {
                GAE_NewsDB_V2 n = new GAE_NewsDB_V2();
                n.setFlagDelete(false);
                n.setContenuto(x.getContent());
                n.setDataInserimento(new Date());
                n.setFullimageLink(x.getFullimageLink());
                n.setKeyTitlePubDate(x.getTitle());
                n.setLink(x.getLink());
                n.setThumbimageLink(x.getThumbimageLink());
                n.setTesto(x.getContent());
                final String k = C_NewsDto.composeKey(x.getTitle(), x.getPubDate());
                n.setKeyTitlePubDate(k);
                token++;
                n.setToken(token);


                loader.insert(n);
                aggiunte++;
            }

            final PrintWriter out = resp.getWriter();
            resp.setContentType("text/plain");
            out.println("News aggiunte: " + aggiunte);
            out.println("News rimosse: " + rimosse);
            out.println("News confermate nel database: " + merge.common.size());
            out.println("Ultimo token utilizzato: " + token);


        } catch (Exception e) {
            throw new IOException(e);
        }

    }
}

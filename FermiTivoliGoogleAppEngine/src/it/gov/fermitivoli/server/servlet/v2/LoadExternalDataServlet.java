package it.gov.fermitivoli.server.servlet.v2;

import it.gov.fermitivoli.model.rss.RssFeed;
import it.gov.fermitivoli.rss.RssReader;
import it.gov.fermitivoli.server.GAE_Settings;
import it.gov.fermitivoli.server.datalayer.DataLayerBuilder;
import it.gov.fermitivoli.server.model.GAE_NewsDB_V2;
import org.xml.sax.SAXException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Created by stefano on 13/03/16.
 */
public class LoadExternalDataServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            final RssFeed read = RssReader.read(GAE_Settings.LISTA_NEWS_JOOMLA_URL);
            final Map<String, GAE_NewsDB_V2> nn = DataLayerBuilder.getLoaderNews().allEntitiesMap();


        } catch (SAXException e) {
            throw new IOException(e);
        }

    }
}

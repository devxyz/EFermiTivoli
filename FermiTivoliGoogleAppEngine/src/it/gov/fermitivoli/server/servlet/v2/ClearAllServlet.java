package it.gov.fermitivoli.server.servlet.v2;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import it.gov.fermitivoli.server.datalayer.DataLayerBuilder;
import it.gov.fermitivoli.server.datastore.DataStoreOptimizer;
import it.gov.fermitivoli.server.datastore.IDataStoreOptimizer;
import it.gov.fermitivoli.server.model.GAE_CircolareDB_V2;
import it.gov.fermitivoli.server.model.GAE_NewsDB_V2;
import it.gov.fermitivoli.server.model.GAE_Token_V2;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by stefano on 01/08/15.
 */
public class ClearAllServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        DataLayerBuilder.getLoaderCircolari().invalidate();
        DataLayerBuilder.getLoaderNews().invalidate();

        final Objectify ofy = ObjectifyService.ofy();

        final List<GAE_CircolareDB_V2> list = ofy.load().type(GAE_CircolareDB_V2.class).list();
        final PrintWriter out = response.getWriter();
        out.println(list.size() + " circolari nel datastore cancellate\n");
        ofy.delete().entities(list).now();

        final List<GAE_NewsDB_V2> list2 = ofy.load().type(GAE_NewsDB_V2.class).list();
        out.println(list2.size() + " new nel datastore cancellate\n");
        ofy.delete().entities(list2).now();

        final List<GAE_Token_V2> ll = ofy.load().type(GAE_Token_V2.class).list();
        ofy.delete().entities(ll).now();
        out.println(ll.size() + " token nel datastore cancellate\n");


        final IDataStoreOptimizer ds = DataStoreOptimizer.getInstance();
        ds.invalidate();


    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}

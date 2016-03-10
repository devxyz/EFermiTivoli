package it.gov.fermitivoli.server.servlet;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import it.gov.fermitivoli.server.GAE_Settings;
import it.gov.fermitivoli.server.datastore.DataStoreOptimizer;
import it.gov.fermitivoli.server.datastore.IDataStoreOptimizer;
import it.gov.fermitivoli.server.model.GAE_CircolareDB_V2;
import it.gov.fermitivoli.server.model.GAE_Token_V2;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by stefano on 01/08/15.
 */
public class ResetServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //check call id
        final String secretKey = request.getParameter("secret-key");
        if (secretKey == null || !secretKey.equals(GAE_Settings.SECRET_KEY)) return;

        final Objectify ofy = ObjectifyService.ofy();
        final List<GAE_CircolareDB_V2> list = ofy.load().type(GAE_CircolareDB_V2.class).list();
        System.out.println("Entities trovate: " + list.size());
        ofy.delete().entities(list).now();

        final List<GAE_Token_V2> ll = ofy.load().type(GAE_Token_V2.class).list();
        ofy.delete().entities(ll);
        final IDataStoreOptimizer ds = DataStoreOptimizer.getInstance();
        ds.invalidate();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}

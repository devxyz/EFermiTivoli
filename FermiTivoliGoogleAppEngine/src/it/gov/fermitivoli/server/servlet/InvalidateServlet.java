package it.gov.fermitivoli.server.servlet;

import com.google.appengine.api.memcache.MemcacheServiceFactory;
import it.gov.fermitivoli.server.GAE_Settings;
import it.gov.fermitivoli.server.datastore.DataStoreOptimizer;
import it.gov.fermitivoli.server.datastore.IDataStoreOptimizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by stefano on 01/08/15.
 */
public class InvalidateServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //check call id
        final String secretKey = request.getParameter("secret-key");
        if (secretKey == null || !secretKey.equals(GAE_Settings.SECRET_KEY)) return;

        final IDataStoreOptimizer ds = DataStoreOptimizer.getInstance();
        ds.invalidate();

        MemcacheServiceFactory.getMemcacheService().clearAll();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}

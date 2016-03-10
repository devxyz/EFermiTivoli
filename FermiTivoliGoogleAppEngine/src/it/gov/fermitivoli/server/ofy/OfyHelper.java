package it.gov.fermitivoli.server.ofy;

/**
 * Created by stefano on 01/08/15.
 */

import com.googlecode.objectify.ObjectifyService;
import it.gov.fermitivoli.server.model.GAE_CircolareDB_V2;
import it.gov.fermitivoli.server.model.GAE_NewsDB_V2;
import it.gov.fermitivoli.server.model.GAE_Token_V2;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * OfyHelper, a ServletContextListener, is setup in web.xml to run before a JSP is run.  This is
 * required to let JSP's access Ofy.
 **/

public class OfyHelper implements ServletContextListener {
    public void contextInitialized(ServletContextEvent event) {
        // This will be invoked as part of a warmup request, or the first user
        // request if no warmup request was invoked.
        ObjectifyService.register(GAE_CircolareDB_V2.class);
        ObjectifyService.register(GAE_NewsDB_V2.class);
        ObjectifyService.register(GAE_Token_V2.class);
    }

    public void contextDestroyed(ServletContextEvent event) {
        // App Engine does not currently invoke this method.
    }
}

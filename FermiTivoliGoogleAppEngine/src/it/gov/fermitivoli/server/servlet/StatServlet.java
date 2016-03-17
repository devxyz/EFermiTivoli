package it.gov.fermitivoli.server.servlet;

import it.gov.fermitivoli.server.datalayer.DataLayerBuilder;
import it.gov.fermitivoli.server.datalayer.impl.circolari.InMemoryCacheLayerCircolareDB;
import it.gov.fermitivoli.server.datalayer.impl.news.InMemoryCacheLayerNewsDB;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by stefano on 01/08/15.
 */
public class StatServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        final InMemoryCacheLayerCircolareDB l1 = DataLayerBuilder.getLoaderCircolari();
        final InMemoryCacheLayerNewsDB l2 = DataLayerBuilder.getLoaderNews();

        final PrintWriter out = response.getWriter();
        response.setContentType("text/plain");
        out.print(l1.toStat());
        out.print("\n\n\n======================================================================\n");
        out.print(l2.toStat());
        out.print("\n\n\n======================================================================\n");
        out.println("Max token: " + DataLayerBuilder.maxToken());


    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}

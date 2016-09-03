package it.gov.fermitivoli.server.servlet;

import it.gov.fermitivoli.server.datalayer.DataLayerBuilder;
import it.gov.fermitivoli.server.datalayer.impl.circolari.InMemoryCacheLayerCircolareDB;
import it.gov.fermitivoli.server.datalayer.impl.news.InMemoryCacheLayerNewsDB;
import it.gov.fermitivoli.server.model.GAE_CircolareDB_V2;
import it.gov.fermitivoli.server.model.GAE_NewsDB_V2;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by stefano on 01/08/15.
 */
public class PrintCircolariServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = new PrintWriter(new BufferedOutputStream(response.getOutputStream()));
        out.print("<html><body>");

        out.print("<h1>CIRCOLARI</h1>");
        out.print("<table border=1>");
        out.print("<tr><td>Progressivo</td><td>Tipo</td><td>Contenuto</td></tr>");
        final InMemoryCacheLayerCircolareDB cl = DataLayerBuilder.getLoaderCircolari();
        int i = 1;
        for (GAE_CircolareDB_V2 c : cl.allEntities()) {
            if (c.isFlagDelete()) continue;
            out.println("<tr><td rowspan=6>" + i + "</td><td><b>URL</b></td>  <td><a href='" + c.getUrl() + "'>" + c.getUrl() + "</a></td></tr>");
            out.println("<tr><td><b>Data</b></td>  <td>" + c.getData() + "(delete:" + c.isFlagDelete() + ")" + "</td></tr>");
            out.println("<tr><td><b>Titolo</b></td>  <td>" + c.getNumero() + " - " + c.getTitolo() + "</td></tr>");
            out.println("<tr><td><b>Token</b></td>  <td>" + c.getToken() + "</td></tr>");
            if (c.getTesto() != null)
                out.println("<tr><td><b>Testo</b></td>  <td>" + c.getTesto().replaceAll("[\n]+", "<br>") + "</td></tr>");
            else
                out.println("<tr><td><b>Testo</b></td>  <td> NULL </td></tr>");
            out.println("<tr><td> ------- </td>  <td> ----------------------------- </td></tr>");
            i++;
        }
        out.print("</table></body>");
        out.print("<br><br>");


        out.print("<h1>NEWS</h1>");
        out.print("<table border=1>");
        out.print("<tr><td>Progressivo</td><td>Tipo</td><td>Contenuto</td></tr>");
        final InMemoryCacheLayerNewsDB cl2 = DataLayerBuilder.getLoaderNews();
        i = 1;
        for (GAE_NewsDB_V2 c : cl2.allEntities()) {
            if (c.isFlagDelete()) continue;
            out.println("<tr><td rowspan=6>" + i + "</td><td><b>LINK</b></td>  <td><a href='" + c.getLink() + "'>" + c.getLink() + "</a></td></tr>");
            out.println("<tr><td><b>Data</b></td>  <td>" + c.getPubDate() + "</td></tr>");
            out.println("<tr><td><b>Titolo</b></td>  <td>" + c.getTitolo() + "</td></tr>");
            out.println("<tr><td><b>Token</b></td>  <td>" + c.getToken() + "</td></tr>");
            if (c.getTesto() != null)
                out.println("<tr><td><b>Testo</b></td>  <td>" + c.getTesto().replaceAll("[\n]+", "<br>") + "</td></tr>");
            else
                out.println("<tr><td><b>Testo</b></td>  <td> NULL </td></tr>");
            out.println("<tr><td> ------- </td>  <td> ----------------------------- </td></tr>");
            i++;
        }
        out.print("</table></body>");
        out.print("<br><br>");


        out.print("</html>");
        out.flush();
        out.close();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}

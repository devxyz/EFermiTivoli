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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
        final List<GAE_CircolareDB_V2> xx = cl.allEntities();
        Collections.sort(xx, new Comparator<GAE_CircolareDB_V2>() {
            @Override
            public int compare(GAE_CircolareDB_V2 o1, GAE_CircolareDB_V2 o2) {
                return -Integer.valueOf(o1.getNumero()).compareTo(o2.getNumero());
            }
        });
        for (GAE_CircolareDB_V2 c : xx) {
            if (c.isFlagDelete())
                out.println("<tr><td rowspan=6> ##D## " + i + "</td><td><b>URL</b></td>  <td><a href='" + c.getUrl() + "'>" + c.getUrl() + "</a></td></tr>");
            else
                out.println("<tr><td rowspan=6>" + i + "</td><td><b>URL</b></td>  <td><a href='" + c.getUrl() + "'>" + c.getUrl() + "</a></td></tr>");
            out.println("<tr><td><b>Data</b></td>  <td>" + c.getData() + "(delete:" + c.isFlagDelete() + ")" + "</td></tr>");
            out.println("<tr><td><b>Titolo</b></td>  <td>" + c.getNumero() + " - " + c.getTitolo() + "</td></tr>");
            out.println("<tr><td><b>Token</b></td>  <td>" + c.getToken() + "</td></tr>");
            if (c.getTesto() != null)
                out.println("<tr><td><b>Testo</b></td>  <td>" + c.getTesto().replaceAll("[\n]+", "<br>") + "</td></tr>");
            else
                out.println("<tr><td><b>Testo</b></td>  <td> NULL </td></tr>");
            final String form_token = "<form action='UpdateDebugServlet' method='get' target='_blank'> " +
                    "<input type='submit' value='Token'>" +
                    "<input type='hidden' name='ID' value='" + c.getKey() + "'>" +
                    "<input type='hidden' name='TYPE' value='CIRCOLARI'>" +
                    "<input type='hidden' name='OPERATION' value='TOKEN'>" +
                    "</form>";
            final String form_flagdelete = "<form action='UpdateDebugServlet' method='get' target='_blank'> " +
                    "<input type='submit' value='Flag Delete'>" +
                    "<input type='hidden' name='ID' value='" + c.getKey() + "'>" +
                    "<input type='hidden' name='TYPE' value='CIRCOLARI'>" +
                    "<input type='hidden' name='OPERATION' value='FLAG_DELETE'>" +
                    "</form>";

            out.println("<tr><td>-</td><td> " +
                    form_token + form_flagdelete + " </td></tr>");
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
            if (c.isFlagDelete())
                out.println("<tr><td rowspan=6>##D## " + i + "</td><td><b>LINK</b></td>  <td><a href='" + c.getLink() + "'>" + c.getLink() + "</a></td></tr>");
            else
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

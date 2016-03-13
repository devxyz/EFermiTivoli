package it.gov.fermitivoli.server.servlet;

import it.gov.fermitivoli.model.C_CircolareDto;
import it.gov.fermitivoli.server.datastore.DataStoreOptimizer;
import it.gov.fermitivoli.server.datastore.IDataStoreOptimizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by stefano on 01/08/15.
 */
public class PrintCircolariServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = new PrintWriter(new BufferedOutputStream(response.getOutputStream()));
        out.print("<html><body>");
        out.print("<table border=1>");

        final IDataStoreOptimizer ds = DataStoreOptimizer.getInstance();
        final List<C_CircolareDto> source = ds.listAllCircolariAttive();
        out.println("<h2>Statistiche persistenza: " + ds.stats() + "</h2>");

        out.print("<tr><td>Tipo</td><td>Contenuto</td></tr>");

        for (C_CircolareDto c : source) {
            out.println("<tr><td><b>URL</b></td>  <td><a href='" + c.getUrl() + "'>" + c.getUrl() + "</a></td></tr>");
            out.println("<tr><td><b>Data</b></td>  <td>" + c.getData() + "</td></tr>");
            out.println("<tr><td><b>Titolo</b></td>  <td>" + c.getNumero() + " - " + c.getTitolo() + "</td></tr>");
            if (c.getTesto() != null)
                out.println("<tr><td><b>Testo</b></td>  <td>" + c.getTesto().replaceAll("[\n]+", "<br>") + "</td></tr>");
            else
                out.println("<tr><td><b>Testo</b></td>  <td> NULL </td></tr>");
            out.println("<tr><td> ------- </td>  <td> ----------------------------- </td></tr>");
        }
        out.print("</table></body></html>");
        out.flush();
        out.close();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}

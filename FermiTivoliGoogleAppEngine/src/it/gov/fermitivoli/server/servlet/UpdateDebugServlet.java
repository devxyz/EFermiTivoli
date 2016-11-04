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
 * Created by stefano on 04/11/2016.
 */
public class UpdateDebugServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("ID");
        String type = request.getParameter("TYPE");
        String operation = request.getParameter("OPERATION");
        operation = operation == null ? "" : operation;
        type = type == null ? "" : type;
        id = id == null ? "" : id;


        PrintWriter out = new PrintWriter(new BufferedOutputStream(response.getOutputStream()));
        out.print("<html><body>");
        out.print("ID=" + id + "<br>");
        out.print("TYPE=" + type + "<br>");
        out.print("OPERATION=" + operation + "<br>");
        out.print("<hr>");
        out.flush();

        if (operation.equals("TOKEN")) {

            if (type.equals("CIRCOLARI")) {

                final InMemoryCacheLayerCircolareDB cl = DataLayerBuilder.getLoaderCircolari();
                final GAE_CircolareDB_V2 c = cl.get(id);
                if (c == null) {
                    out.print("Circolare non trovata<br>");
                } else {

                    long token = DataLayerBuilder.maxToken();
                    token++;
                    c.setToken(token);
                    cl.update(c);
                    out.print("Circolare " + c.getTitolo() + "<br>");
                    out.print("Token " + c.getToken() + "<br>");
                    cl.invalidate();
                }
            } else if (type.equals("NEWS")) {
                final InMemoryCacheLayerNewsDB cl = DataLayerBuilder.getLoaderNews();
                final GAE_NewsDB_V2 c = cl.get(id);
                if (c == null) {
                    out.print("News non trovata<br>");
                } else {

                    long token = DataLayerBuilder.maxToken();
                    token++;
                    c.setToken(token);
                    cl.update(c);
                    out.print("News " + c.getTitolo() + "<br>");
                    out.print("Token " + c.getToken() + "<br>");
                    cl.invalidate();
                }
            } else {
                out.print("Operazione sconosciuta");
            }
        } else if (operation.equals("FLAG_DELETE")) {
            if (type.equals("CIRCOLARI")) {

                final InMemoryCacheLayerCircolareDB cl = DataLayerBuilder.getLoaderCircolari();
                final GAE_CircolareDB_V2 c = cl.get(id);
                if (c == null) {
                    out.print("Circolare non trovata<br>");
                } else {

                    long token = DataLayerBuilder.maxToken();
                    token++;
                    c.setToken(token);
                    c.setFlagDelete(!c.isFlagDelete());
                    cl.update(c);
                    out.print("Circolare " + c.getTitolo() + "<br>");
                    out.print("Token " + c.getToken() + "<br>");
                    out.print("Flag delete " + c.isFlagDelete() + "<br>");
                    cl.invalidate();
                }
            } else if (type.equals("NEWS")) {
                final InMemoryCacheLayerNewsDB cl = DataLayerBuilder.getLoaderNews();
                final GAE_NewsDB_V2 c = cl.get(id);
                if (c == null) {
                    out.print("News non trovata<br>");
                } else {

                    long token = DataLayerBuilder.maxToken();
                    token++;
                    c.setToken(token);
                    c.setFlagDelete(!c.isFlagDelete());
                    cl.update(c);
                    out.print("News " + c.getTitolo() + "<br>");
                    out.print("Token " + c.getToken() + "<br>");
                    out.print("Flag delete " + c.isFlagDelete() + "<br>");
                    cl.invalidate();
                }
            } else {
                out.print("Operazione sconosciuta");
            }

        } else {

        }


        out.print("</body></html>");
        out.flush();
    }
}

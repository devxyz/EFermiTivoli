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
import java.io.*;
import java.util.ArrayList;

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
        //id = id == null ? "" : id;


        PrintWriter out = new PrintWriter(new BufferedOutputStream(response.getOutputStream()));
        out.print("<html><body>");

        out.print("OBJECT = " + type + "<br>");
        out.print("OPERATION = " + operation + "<br>");
        out.print("OPTIONAL ID = " + id + "<br>");
        out.print("<hr>");
        out.flush();

        final String TYPE_OPERATION_TOKEN = "TOKEN";
        final String TYPE_OPERATION_FLAG_DELETE_TOGGLE = "FLAG_DELETE_TOGGLE";
        final String TYPE_OPERATION_FLAG_DELETE_TRUE = "FLAG_DELETE_TRUE";
        final String TYPE_OPERATION_FLAG_DELETE_FALSE = "FLAG_DELETE_FALSE";

        final String TYPE_OBJECT_CIRCOLARI = "CIRCOLARI";
        final String TYPE_OBJECT_NEWS = "NEWS";

        try {
            switch (type) {
                case TYPE_OBJECT_CIRCOLARI: {
                    out.print("CIRCOLARI<br>");
                    out.flush();
                    final InMemoryCacheLayerCircolareDB cl = DataLayerBuilder.getLoaderCircolari();


                    ArrayList<GAE_CircolareDB_V2> ris = new ArrayList<>();
                    if (id == null || id.trim().length() == 0) {
                        ris.addAll(cl.allEntities());
                        out.print("Tutte le circolari: " + ris.size() + "<br>");
                        out.flush();
                    } else {
                        final GAE_CircolareDB_V2 c = cl.get(id);
                        ris.add(c);
                        out.print("Unica circolare: " + ris.size() + "<br>");
                        out.flush();
                        out.flush();
                    }

                    long token = DataLayerBuilder.maxToken();
                    for (GAE_CircolareDB_V2 x : ris) {
                        token++;
                        x.setToken(token);

                        switch (operation) {
                            case TYPE_OPERATION_FLAG_DELETE_TOGGLE:
                                x.setFlagDelete(!x.isFlagDelete());
                                break;
                            case TYPE_OPERATION_FLAG_DELETE_TRUE:
                                x.setFlagDelete(true);
                                break;
                            case TYPE_OPERATION_FLAG_DELETE_FALSE:
                                x.setFlagDelete(false);
                                break;
                            case TYPE_OPERATION_TOKEN:
                                //only renew token
                                break;
                            default:

                                break;
                        }

                        cl.update(x);
                        out.print("<hr>");
                        out.print("Circolare " + x.getTitolo() + "<br>");
                        out.print("Token " + x.getToken() + "<br>");
                        out.print("flag delete " + x.isFlagDelete() + "<br>");

                    }
                    cl.invalidate();
                    break;
                }
                case TYPE_OBJECT_NEWS: {
                    out.print("NEWS<br>");
                    out.flush();
                    final InMemoryCacheLayerNewsDB cl = DataLayerBuilder.getLoaderNews();

                    ArrayList<GAE_NewsDB_V2> ris = new ArrayList<>();
                    if (id == null || id.trim().length() == 0) {

                        ris.addAll(cl.allEntities());
                        out.print("TUTTE LE NEWS<br>");

                    } else {
                        final GAE_NewsDB_V2 c = cl.get(id);
                        ris.add(c);
                        out.print("UNICA NEWS<br>");
                    }


                    long token = DataLayerBuilder.maxToken();
                    for (GAE_NewsDB_V2 x : ris) {
                        token++;
                        x.setToken(token);

                        switch (operation) {
                            case TYPE_OPERATION_FLAG_DELETE_TOGGLE:
                                x.setFlagDelete(!x.isFlagDelete());
                                break;
                            case TYPE_OPERATION_FLAG_DELETE_TRUE:
                                x.setFlagDelete(true);
                                break;
                            case TYPE_OPERATION_FLAG_DELETE_FALSE:
                                x.setFlagDelete(false);
                                break;
                            case TYPE_OPERATION_TOKEN:
                                //only renew token
                                break;
                            default:

                                break;
                        }

                        cl.update(x);
                        out.print("<hr>");
                        out.print("News " + x.getTitolo() + "<br>");
                        out.print("Token " + x.getToken() + "<br>");
                        out.print("flag delete " + x.isFlagDelete() + "<br>");
                    }
                    cl.invalidate();
                    break;
                }
                default:
                    out.print("TIPO sconosciuto");
                    out.flush();
                    break;
            }
        } catch (Throwable ex) {
            ByteArrayOutputStream o = new ByteArrayOutputStream(1000);
            PrintStream po = new PrintStream(o);
            ex.printStackTrace(po);
            po.close();
            out.print(o.toString().replace("\n", "<br>"));
            out.flush();
            throw new IOException(ex);
        }


        out.print("</body></html>");
        out.flush();
    }
}

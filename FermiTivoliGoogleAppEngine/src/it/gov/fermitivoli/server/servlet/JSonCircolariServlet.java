package it.gov.fermitivoli.server.servlet;

import com.google.gson.Gson;
import it.gov.fermitivoli.model.C_JSonCircolariDeltaServletRequest;
import it.gov.fermitivoli.model.C_JSonCircolariDeltaServletResponse;
import it.gov.fermitivoli.server.datastore.DataStoreOptimizer;
import it.gov.fermitivoli.server.datastore.IDataStoreOptimizer;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by stefano on 01/08/15.
 */
public class JSonCircolariServlet extends HttpServlet {
    private static final boolean DEBUG = false;

    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        final String param = request.getParameter("param");
        if (param == null) throw new IOException("Parameter (param) not specified");

        final Gson g = new Gson();
        final C_JSonCircolariDeltaServletRequest req = g.fromJson(param, C_JSonCircolariDeltaServletRequest.class);
        final C_JSonCircolariDeltaServletResponse resp = new C_JSonCircolariDeltaServletResponse();

        try {
            if (DEBUG)
                System.out.println("RICHIESTA LISTA CIRCOLARI");
            final IDataStoreOptimizer ds = DataStoreOptimizer.getInstance();

            //----------------------------------------------------------------------
            //individua le nuove circolari da inviare / aggiornare
            //----------------------------------------------------------------------
            resp.circolariDaAggiungereAggiornare.addAll(ds.listAllCircolariAttive(req.maxToken));
            resp.circolariDaRimuovere.addAll(ds.listAllCircolariEliminate(req.maxToken));

            final String s = g.toJson(resp);
            if (req.responseInZipFormat) {
                response.setContentType("application/zip");
                response.addHeader("Content-Disposition", "inline; filename=circolari.dto.zip");

                final ServletOutputStream outputStream = response.getOutputStream();
                ZipOutputStream out = new ZipOutputStream(outputStream);
                out.putNextEntry(new ZipEntry("circolari.json"));
                out.write(s.getBytes());
                out.closeEntry();

                out.close();
            } else {
                final ServletOutputStream outputStream = response.getOutputStream();
                outputStream.write(s.getBytes());
                outputStream.flush();
            }
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        doPost(request, response);

    }
}

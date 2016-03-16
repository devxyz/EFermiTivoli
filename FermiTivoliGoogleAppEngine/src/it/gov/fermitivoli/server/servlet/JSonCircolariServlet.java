package it.gov.fermitivoli.server.servlet;

import com.google.gson.Gson;
import it.gov.fermitivoli.model.C_CircolareDto;
import it.gov.fermitivoli.model.C_JSonCircolariDeltaServletRequest;
import it.gov.fermitivoli.model.C_JSonCircolariDeltaServletResponse;
import it.gov.fermitivoli.model.C_NewsDto;
import it.gov.fermitivoli.server.datalayer.DataLayerBuilder;
import it.gov.fermitivoli.server.datalayer.impl.circolari.InMemoryCacheLayerCircolareDB;
import it.gov.fermitivoli.server.datalayer.impl.news.InMemoryCacheLayerNewsDB;
import it.gov.fermitivoli.server.model.GAE_CircolareDB_V2;
import it.gov.fermitivoli.server.model.GAE_NewsDB_V2;
import it.gov.fermitivoli.server.util.GAE_DtoUtil;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.util.List;
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

        //loader
        final InMemoryCacheLayerNewsDB loaderNews = DataLayerBuilder.getLoaderNews();
        final InMemoryCacheLayerCircolareDB loaderCircolari = DataLayerBuilder.getLoaderCircolari();


        try {
            if (DEBUG)
                System.out.println("RICHIESTA LISTA CIRCOLARI");

            final List<GAE_NewsDB_V2> news = loaderNews.allEntities(req.maxToken);
            final List<GAE_CircolareDB_V2> circolari = loaderCircolari.allEntities(req.maxToken);

            //----------------------------------------------------------------------
            //individua le nuove circolari da inviare / aggiornare
            //----------------------------------------------------------------------
            for (GAE_NewsDB_V2 x : news) {
                if (x.isFlagDelete()) {
                    resp.keyNewsDaRimuovere.add(x.getKey());
                } else {
                    final C_NewsDto n = new C_NewsDto();
                    GAE_DtoUtil.copy(x, n);
                    resp.newsDaAggiungereAggiornare.add(n);
                }
            }

            for (GAE_CircolareDB_V2 x : circolari) {
                if (x.isFlagDelete()) {
                    resp.keyCircolariDaRimuovere.add(x.getKey());
                } else {
                    final C_CircolareDto n = new C_CircolareDto();
                    GAE_DtoUtil.copy(x, n);
                    resp.circolariDaAggiungereAggiornare.add(n);
                }
            }

            final String s = g.toJson(resp);
            if (req.responseInZipFormat) {
                response.setContentType("application/zip");
                response.addHeader("Content-Disposition", "inline; filename=data.dto.zip");

                final ServletOutputStream outputStream = response.getOutputStream();
                ZipOutputStream out = new ZipOutputStream(outputStream);
                out.putNextEntry(new ZipEntry("data.json"));
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

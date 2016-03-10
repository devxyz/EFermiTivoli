package it.gov.fermitivoli.server.util;

import it.gov.fermitivoli.model.C_CircolareDto;
import it.gov.fermitivoli.server.GAE_Settings;
import it.gov.fermitivoli.util.C_CircolariUtil;
import it.gov.fermitivoli.util.C_DateUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by stefano on 31/07/15.
 */
public class GAE_DownloadCircolari {
    private static final GAE_DownloadCircolari instance = new GAE_DownloadCircolari();
    private static final boolean DEBUG = false;
    /**
     * task attivo
     */
    private transient List<C_CircolareDto> result = null;
    /**
     * data ultimo risultato
     */
    private transient Date downloadDate = null;

    private GAE_DownloadCircolari() {
    }

    public static GAE_DownloadCircolari getInstance() {
        return instance;
    }

    public void forceUpdate() {
        downloadDate = null;
    }

    /**
     * task sincrono
     *
     * @return
     * @throws Exception
     */
    public List<C_CircolareDto> downloadListaCircolari() throws Exception {
        if (DEBUG) {
            System.out.println("downloadDate:" + downloadDate);
            System.out.println("result:" + (result == null ? "NULL" : "size " + result.size()));
            if (downloadDate != null) {
                System.out.println("difference: " + C_DateUtil.differenzaInMinuti(downloadDate, new Date()));
            }
        }

        if (downloadDate == null || result == null || C_DateUtil.differenzaInMinuti(downloadDate, new Date()) > 1) {

            result = null;

            if (DEBUG)
                System.out.println("DOWNLOAD NUOVA LISTA CIRCOLARI");
            final Document parse = Jsoup.parse(new URL(GAE_Settings.LISTA_CIRCOLARI_JOOMLA_URL), GAE_Settings.LISTA_CIRCOLARI_TIMEOUT_MILLIS);

            final List<C_CircolareDto> c = C_CircolariUtil.parseFromHtmlFromJoomla(parse, GAE_Settings.LISTA_CIRCOLARI_BASEURL);

            downloadDate = new Date();
            result = c;
        }

        try {
            return new ArrayList<>(result);
        } catch (Throwable e) {
            result = null;
            throw e;
        }
    }

}

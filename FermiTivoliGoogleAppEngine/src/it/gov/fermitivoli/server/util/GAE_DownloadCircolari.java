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
    public static List<C_CircolareDto> downloadListaCircolari() throws Exception {
        final Document parse = Jsoup.parse(new URL(GAE_Settings.LISTA_CIRCOLARI_JOOMLA_URL), GAE_Settings.LISTA_CIRCOLARI_TIMEOUT_MILLIS);
        return C_CircolariUtil.parseFromHtmlFromJoomla(parse, GAE_Settings.LISTA_CIRCOLARI_BASEURL);
    }

}

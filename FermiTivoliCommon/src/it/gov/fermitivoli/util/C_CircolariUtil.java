package it.gov.fermitivoli.util;

import it.gov.fermitivoli.model.C_CircolareDto;
import it.gov.fermitivoli.model.C_NormalizedURL;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by stefano on 31/07/15.
 */
public class C_CircolariUtil {

    public static List<C_CircolareDto> parseFromHtmlFromJoomla(Document d, String prefixUrlCircolari) {
        List<C_CircolareDto> ris = new ArrayList<C_CircolareDto>();
        SimpleDateFormat sp = new SimpleDateFormat("yyyy-MM-dd");
        //cerca le righe delle tabelle
        final Elements htmlTr = d.select("tr");
        for (Element e : htmlTr) {
            final Elements htmlTd = e.select("td");
            //skip header
            if (htmlTd.size() == 0) {
                continue;
            }
            if (htmlTd.size() != 4) {
                throw new IllegalArgumentException("Invalid number of elements: " + htmlTd.size() + " in" + htmlTd + "\nCurrent row:" + e);
                //continue;
            }

            final Element htmlNumero = htmlTd.get(0);
            final Element htmlData = htmlTd.get(1);
            final Element htmlTitolo = htmlTd.get(2);
            final Element htmlUrl = htmlTd.get(3);
            final Element htmlLink = htmlUrl.select("a").get(0);

            final int numero = Integer.parseInt(htmlNumero.html().trim());
            final String href = prefixUrlCircolari + htmlLink.attr("href").trim();
            final String titolo = C_TextUtil.normalizeTextFromHtml(htmlTitolo.html().trim());
            final String data = htmlData.html().trim();


            Date dd = null;
            try {
                dd = sp.parse(data);
            } catch (ParseException e1) {
                throw new IllegalArgumentException(e1);
            }

            ris.add(new C_CircolareDto(titolo, null, dd, new C_NormalizedURL(href), numero));

        }
        return ris;
    }




}

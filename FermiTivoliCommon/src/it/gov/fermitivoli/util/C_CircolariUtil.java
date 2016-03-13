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
    private static final ArrayList<String[]> stopWordsCircolai = new ArrayList<>(Arrays.asList(
            "Powered by TCPDF www.tcpdf.org".split("[ ]+"),
            "ISTITUTO TECNICO COMMERCIALE E PER GEOMETRI".split("[ ]+"),
            "UFFICIO SCOLASTICO REGIONALE PER IL LAZIO".split("[ ]+"),
            "MINISTERO ISTRUZIONE UNIVERSIT RICERCA".split("[ ]+"),
            "WEB www.fermitivoli.gov.it EMAIL rmtd07000g@istruzione.it PEC rmtd07000g@pec.istruzione.it".split("[ ]+"),
            "C.F. 86000020585  Cod. Ist RMTD07000G Distretto scol. 34".split("[ ]+"),
            "Via Acquaregna, 112 00019 TIVOLI Tel. 06/121126986 06/121126985 Fax 0774/334373".split("[ ]+")
    ));

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


    private static boolean containsAll(String s, String[] values) {
        for (String value : values) {
            if (!s.contains(value)) return false;
        }
        return true;
    }



    /**
     * normalizza un testo eliminando gli \n non necessari in funzione dellle maiuscole
     *
     * @param text
     * @return
     */
    public static String normalizeTextAndLineFeed_forTextCircolari(String text, boolean skipStopLines) {
        final String[] split = text.split("[\n]");
        StringBuilder sb = new StringBuilder(text.length());
        for (String line : split) {
            String trim = line.trim();
            //controlla se cancellare
            if (skipStopLines) {
                boolean skip = false;
                for (String[] s : stopWordsCircolai) {
                    if (containsAll(trim, s)) {
                        skip = true;
                        break;
                    }
                }
                if (skip) continue;
            }

            sb.append("\n");
            sb.append(line);
        }

        return C_TextUtil.normalize_lineFeed(sb.toString());
    }
}

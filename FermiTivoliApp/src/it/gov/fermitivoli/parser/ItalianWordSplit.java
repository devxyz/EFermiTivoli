package it.gov.fermitivoli.parser;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import it.gov.fermitivoli.model.TermineInfoWeb;
import it.gov.fermitivoli.parser.impl.ParseException;
import it.gov.fermitivoli.parser.impl.Token;
import it.gov.fermitivoli.parser.impl.WordParser;
import it.gov.fermitivoli.parser.impl.WordParserConstants;
import it.gov.fermitivoli.util.DebugUtil;
import org.tartarus.snowball.ext.ItalianStemmer;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by stefano on 15/06/15.
 */
public class ItalianWordSplit {
    private static final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    private static final Calendar c = Calendar.getInstance();

    private static String normalize(Token t) {
        switch (t.kind) {
            case WordParserConstants.DATA: {
                String n = t.image.toLowerCase()
                        .replace("gennaio", "01")
                        .replace("febbraio", "02")
                        .replace("marzo", "03")
                        .replace("aprile", "04")
                        .replace("maggio", "05")
                        .replace("giugno", "06")
                        .replace("luglio", "07")
                        .replace("agosto", "08")
                        .replace("settembre", "09")
                        .replace("ottobre", "10")
                        .replace("novembre", "11")
                        .replace("dicembre", "12")
                        .replace("-", "/")
                        .replaceAll("[ /-]+", "/");
                String[] s = n.split("/");

                try {
                    c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(s[0]));
                    c.set(Calendar.MONTH, Integer.parseInt(s[1]) - 1);
                    c.set(Calendar.YEAR, Integer.parseInt(s[2]));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Errore nell'analisi della data " + t.image + "(norm:" + n + ")", e);
                }
                return formatter.format(c.getTime()) + "(#DATA)";
            }
            case WordParserConstants.CLASSE: {
                return t.image.toUpperCase().replace(" ", "") + "(#CLASSE)";
                /*return "#CLASSE_"+t.image.toUpperCase().replace(" ", "") ;*/
            }
            case WordParserConstants.NUMERO: {
                return null;//"classe:" + t.image.toUpperCase().replace(" ", "");
            }
            case WordParserConstants.CIRCOLARE: {
                return null;// "circolare:" + t.image.toLowerCase();
            }
            default:
                if (t.image.length() <= 3) return null;
                return t.image.toLowerCase();

        }
    }

    public static TreeSet<TermineInfoWeb> parseWords(String... ss) {
        return parseWords(Arrays.asList(ss));
    }

    public static Set<String> estraiRadiciTermini(Collection<String> termini) {
        ItalianStemmer it = new ItalianStemmer();
        Set<String> ris = new TreeSet<String>();
        for (String s : termini) {
            ris.add(it.stem(s));
        }
        return ris;
    }

    public static TreeSet<TermineInfoWeb> parseWords(Collection<String> ss) {
        final StringBuffer sb = new StringBuffer();
        for (String s : ss) {
            sb.append(s).append("\n");
        }
        if (DebugUtil.DEBUG__ItalianWordSplit) {
            System.out.println("PARSING CIRCOLARE:\n" + sb.toString());
        }

        final TreeMap<String, TermineInfoWeb> ris = new TreeMap<String, TermineInfoWeb>();
        final WordParser parser = new WordParser(new StringReader(sb.toString()));
        Token nextToken;
        do {
            nextToken = parser.getNextToken();
            if (nextToken.kind != WordParserConstants.ALTRO) {
                String normalize = normalize(nextToken);
                if (normalize != null) {
                    TermineInfoWeb tf = ris.get(normalize);
                    if (tf == null) {
                        tf = new TermineInfoWeb(normalize);
                        ris.put(normalize, tf);
                    }
                    tf.aggiungiOccorrenze(1);
                }
                if (DebugUtil.DEBUG__ItalianWordSplit)
                    System.out.println("WORD " + nextToken.image + " type " + WordParserConstants.tokenImage[nextToken.kind]);
            } else {
                if (DebugUtil.DEBUG__ItalianWordSplit)
                    System.out.println("SKIPPED " + nextToken.image);

            }
        } while (nextToken.kind != WordParserConstants.EOF);
        return new TreeSet<TermineInfoWeb>(ris.values());
    }

    public static void main(String[] args) throws ParseException {
        String s = "MINISTERO DELL’ISTRUZIONE, DELL’UNIVERSITÀ E DELLA RICERCA\n" +
                " UFFICIO SCOLASTICO REGIONALE PER IL LAZIO\n" +
                " ISTITUTO TECNICO COMMERCIALE E PER GEOMETRI “E. FERMI”\n" +
                "Ai tutti i docenti\n" +
                "Circolare n.249 del 16-06-2015\n" +
                "OGGETTO: disponibilità corsi di recupero\n" +
                "Tutti i docenti che si rendono disponibili per i corsi di recupero estivi, devono darne comunicazioni inviando una\n" +
                "e-mail all’indirizzo istituzionale rmtd07000g@istruzione.it entro il 19/06/2015\n" +
                "Il Dirigente scolastico\n" +
                "Prof.ssa Laura Maria Giovannelli\n" +
                "Powered by TCPDF (www.tcpdf.org)\n" +
                "Via Acquaregna, 112 - 00019 TIVOLI Tel.06/121126986 06/121126985 Fax 0774/334373\n" +
                "C.F. 86000020585 - Cod. Ist.RMTD07000G - Distretto scol. 34\n" +
                "WEB: www.fermitivoli.gov.it - EMAIL: rmtd07000g@istruzione.it - PEC: rmtd07000g@pec.istruzione.it";
        TreeSet<TermineInfoWeb> ris = parseWords(s);

        for (TermineInfoWeb r : ris) {
            System.out.println(r);
        }
        /*

        WordParser parser = new WordParser(new StringReader(s));
        Token nextToken;
        do {
            nextToken = parser.getNextToken();
            if (nextToken.kind != WordParserConstants.ALTRO)
                System.out.println(nextToken);
        } while (nextToken.kind != WordParserConstants.EOF);
        System.out.println(nextToken);
*/

    }

    @Deprecated
    public static String extractTextPdf(File f) throws IOException {
        StringBuffer sb = new StringBuffer();
        InputStream in = new BufferedInputStream(new FileInputStream(f));

        PdfReader reader = new PdfReader(in);
        PdfReaderContentParser parser = new PdfReaderContentParser(reader);
        TextExtractionStrategy ex;
        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
            ex = parser.processContent(i, new SimpleTextExtractionStrategy());
            sb.append(ex.getResultantText().trim());
            sb.append(" ");
        }
        reader.close();
        in.close();
        return sb.toString().replaceAll("[ ]+", " ");
    }


}

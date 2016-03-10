package it.gov.fermitivoli.server.util;

import com.itextpdf.text.exceptions.InvalidPdfException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import it.gov.fermitivoli.util.C_TextUtil;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by stefano on 10/08/15.
 */
public class GAE_PdfUtil {
    public static String extractTextPdf(InputStream in0) throws IOException {
        try {
            StringBuffer sb = new StringBuffer();
            InputStream in = new BufferedInputStream(in0);

            PdfReader reader = new PdfReader(in);
            PdfReaderContentParser parser = new PdfReaderContentParser(reader);
            TextExtractionStrategy ex;
            for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                ex = parser.processContent(i, new SimpleTextExtractionStrategy());

                sb.append(C_TextUtil.normalize_UTF8__to__ASCII(ex.getResultantText().trim()));
                sb.append(" ");
            }
            reader.close();
            in.close();
            return C_TextUtil.normalize_UTF8__to__ASCII(sb.toString().replaceAll("[ ]+", " "));
        } catch (InvalidPdfException e) {
            return "....<INVALID FILE>....";//NO DATA
        }
    }
}

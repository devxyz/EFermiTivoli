package it.gov.fermitivoli.backoffice;

import java.io.*;

/**
 * Created by stefano on 24/09/15.
 */
public class IpCommand {
    public static void main(String[] args) throws IOException {
        File template = new File("/Users/stefano/DATA/scuola/insegnamento/scuola-AS-2014-15/Fermi-TIVOLI-14-15/development/EFermiTivoli/JavaSEUtilities/src/it/gov/fermitivoli/backoffice/utilities/template.txt");
        StringBuilder sb = new StringBuilder();
        BufferedReader in = new BufferedReader(new FileReader(template));
        String s;
        while ((s = in.readLine()) != null) {
            sb.append(s + "\r\n");
        }
        in.close();

        for (int i = 2; i <= 31; i++) {
            final String replace = sb.toString().replace("#", "" + i);
            File o2 = new File("/Users/stefano/DATA/scuola/insegnamento/scuola-AS-2014-15/Fermi-TIVOLI-14-15/development/EFermiTivoli/JavaSEUtilities/src/it/gov/fermitivoli/backoffice/utilities/files/" + i + ".bat");
            FileWriter out = new FileWriter(o2);
            out.write(replace);
            out.close();
        }


    }
}

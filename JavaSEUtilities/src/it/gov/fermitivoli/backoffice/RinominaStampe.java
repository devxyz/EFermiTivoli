package it.gov.fermitivoli.backoffice;

import java.io.File;

/**
 * Created by stefano on 03/07/15.
 */
public class RinominaStampe {
    public static void main(String[] args) {
        File dir = new File("/private/var/spool/pdfwriter/stefano");
        for (File file : dir.listFiles()) {

            String nuovoNome = file.getName();
            if (nuovoNome.startsWith("."))continue;

            System.out.println(nuovoNome);
            int i = nuovoNome.indexOf("_");
            i = nuovoNome.indexOf(" ", i);
            nuovoNome = nuovoNome.substring(i);
            final File f2 = new File(file.getParent(), nuovoNome);
            if (f2.exists())
                f2.delete();
            file.renameTo(f2);
        }
    }
}

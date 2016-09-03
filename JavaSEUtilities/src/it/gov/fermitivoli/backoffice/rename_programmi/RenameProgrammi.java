package it.gov.fermitivoli.backoffice.rename_programmi;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by stefano on 20/06/16.
 */
public class RenameProgrammi {

    public static void zipFolder(final File folder, final File zipFile) throws IOException {
        zipFolder(folder, new FileOutputStream(zipFile));
    }

    public static void zipFolder(final File folder, final OutputStream outputStream) throws IOException {
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
            processFolder(folder, zipOutputStream, folder.getPath().length() + 1);
        }
    }

    private static void copy(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024 * 4];
        int read = 0;
        while ((read = input.read(buffer)) != -1) {
            output.write(buffer, 0, read);
        }
    }

    private static void processFolder(final File folder, final ZipOutputStream zipOutputStream, final int prefixLength)
            throws IOException {
        for (final File file : folder.listFiles()) {
            if (file.isFile()) {
                final ZipEntry zipEntry = new ZipEntry(file.getPath().substring(prefixLength));
                zipOutputStream.putNextEntry(zipEntry);
                try (FileInputStream inputStream = new FileInputStream(file)) {
                    copy(inputStream, zipOutputStream);
                    inputStream.close();
                }
                zipOutputStream.closeEntry();
            } else if (file.isDirectory()) {
                processFolder(file, zipOutputStream, prefixLength);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        File f1 = new File("/Users/stefano/DATA/scuola/insegnamento/scuola-AS-2015-16/FalconeBorsellino-Zagarolo-15-16/programmi/PROGRAMMI_A.S.2015_2016/Programmi_IPIA");
        File f2 = new File("/Users/stefano/DATA/scuola/insegnamento/scuola-AS-2015-16/FalconeBorsellino-Zagarolo-15-16/programmi/PROGRAMMI_A.S.2015_2016/Programmi_LICEO");
        File dest = new File("/Users/stefano/DATA/scuola/insegnamento/scuola-AS-2015-16/FalconeBorsellino-Zagarolo-15-16/programmi/PROGRAMMI_A.S.2015_2016/zip");
        final List<File> files = Arrays.asList(f1, f2);
        for (File root : files) {
            final File[] dirs = root.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory();
                }
            });
            for (File dir : dirs) {
                System.out.println(dir);
                final File file = new File(dest, dir.getName().replace(" ", "_") + ".zip");
                zipFolder(dir, file);
            }

        }

    }
}

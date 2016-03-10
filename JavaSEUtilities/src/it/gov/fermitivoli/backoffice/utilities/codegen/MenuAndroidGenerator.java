package it.gov.fermitivoli.backoffice.utilities.codegen;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ArrayList;

/**
 * Created by stefano on 18/09/15.
 */
public class MenuAndroidGenerator {
    public static void main(String[] args) throws Exception {
        File destFolder = new File("/Users/stefano/DATA/scuola/insegnamento/scuola-AS-2014-15/Fermi-TIVOLI-14-15/development/EFermiTivoli/FermiTivoliApp/src/it/gov/fermitivoli/model/menu/impl");

        File f1 = new File("/Users/stefano/DATA/scuola/insegnamento/scuola-AS-2014-15/Fermi-TIVOLI-14-15/development/EFermiTivoli/FermiTivoliApp/res/values/strings_menu_principale.xml");
        File f2 = new File("/Users/stefano/DATA/scuola/insegnamento/scuola-AS-2014-15/Fermi-TIVOLI-14-15/development/EFermiTivoli/FermiTivoliApp/res/values/strings_menu_home_docente.xml");
        File f3 = new File("/Users/stefano/DATA/scuola/insegnamento/scuola-AS-2014-15/Fermi-TIVOLI-14-15/development/EFermiTivoli/FermiTivoliApp/res/values/strings_menu_home_studente.xml");
        File f4 = new File("/Users/stefano/DATA/scuola/insegnamento/scuola-AS-2014-15/Fermi-TIVOLI-14-15/development/EFermiTivoli/FermiTivoliApp/res/values/strings_menu_home_famiglie.xml");
        File f5 = new File("/Users/stefano/DATA/scuola/insegnamento/scuola-AS-2014-15/Fermi-TIVOLI-14-15/development/EFermiTivoli/FermiTivoliApp/res/values/strings_menu_home_altro.xml");
        genera(f1, destFolder);
        genera(f2, destFolder);
        genera(f3, destFolder);
        genera(f4, destFolder);
        genera(f5, destFolder);
    }

    public static void genera(File f, File destFolder) throws Exception {
        final StringWriter out1 = new StringWriter(1000);
        PrintWriter out = new PrintWriter(out1);

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        ArrayList<String> ss = new ArrayList<>();

        DocumentBuilder builder = dbf.newDocumentBuilder();
        File xmlFile = f;
        Document document = builder.parse(xmlFile);
        final Element elem = document.getDocumentElement();
        final NodeList cl = elem.getChildNodes();
        for (int i = 0; i < cl.getLength(); i++) {
            final Node item = cl.item(i);
            if (item instanceof Element) {
                Element e = (Element) item;
                System.out.println(e.getNodeName());
                final NodeList items = e.getChildNodes();
                for (int j = 0; j < items.getLength(); j++) {
                    final Node it = items.item(j);
                    if (it instanceof Element) {
                        Element eit = (Element) it;
                        final Node firstChild = eit.getFirstChild();
                        if (firstChild != null) {
                            final String nodeValue = firstChild.getNodeValue();
                            if (nodeValue != null)
                                ss.add(nodeValue);
                            else
                                ss.add("");

                        } else {
                            ss.add("");
                        }
                    }
                }
            }
        }
        for (String s : ss) {
            //  System.out.println(s);
        }


        final String className = capitalize(f.getName().replaceAll(".xml", ""));


        out.println("package it.gov.fermitivoli.model.menu.impl;");
        out.println("import it.gov.fermitivoli.R;\n");
        out.println("import it.gov.fermitivoli.model.menu.*;");
        out.println("public class " + className + "{");

        int i = 0;
        while (i < ss.size()) {
            String menuID = ss.get(i++);
            String menuLabel = ss.get(i++);
            String longLabel = ss.get(i++);
            String actionClass = ss.get(i++);
            String imageId = ss.get(i++);
            String x1 = ss.get(i++);
            String x2 = ss.get(i++);
            String nomeCampo = menuLabel.replaceAll("[ .-]+", "_").toUpperCase().replace("(", "").replace(")", "") + "_" + menuID;

            //skip null
            if (actionClass.trim().length() == 0) continue;

            //final DataMenuInfoType type, Set<DataMenuInfoFlag > flags
            final String s = MessageFormat.format("     public static final DataMenuInfo {0}= new DataMenuInfo({1},\"{2}\",\"{3}\",{4}.class,{5},DataMenuInfoType.search({4}.class),{7}     );",
                    nomeCampo,
                    menuID, menuLabel, longLabel.length() == 0 ? menuLabel : longLabel, actionClass, imageId.replace("@drawable/", "R.drawable."), "", "null");
            //System.out.println(s);
            out.println(s);
        }
        out.println("}");

        System.out.println(out1);

        File fout = new File(destFolder, className + ".java");
        FileWriter fout2 = new FileWriter(fout);
        fout2.write(out1.toString());
        fout2.close();


    }


    private static String capitalize0(String s) {
        if (s == null || s.length() == 0) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
    }

    private static String capitalize(String s) {
        final String[] split = s.split("[_]+");
        StringBuilder sb = new StringBuilder();
        for (String x : split) {
            System.out.println(sb.append(capitalize0(x)));
        }
        return sb.toString();
    }

}

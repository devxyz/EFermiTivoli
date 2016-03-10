package it.gov.fermitivoli.backoffice.utilities.codegen;

import it.gov.fermitivoli.backoffice.utilities.util.StreamUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by stefano on 01/09/15.
 */
public class BatchCodeGenerator_forLayout {
    public static void main(String[] args) throws Exception {
        File sourceDir = new File("/Users/stefano/DATA/scuola/insegnamento/scuola-AS-2014-15/Fermi-TIVOLI-14-15/development/EFermiTivoli/FermiTivoliApp/res/layout");
        File destDir = new File("/Users/stefano/DATA/scuola/insegnamento/scuola-AS-2014-15/Fermi-TIVOLI-14-15/development/EFermiTivoli/FermiTivoliApp/src/it/gov/fermitivoli/layout");
        String _package = "it.gov.fermitivoli.layout";
        for (File file : destDir.listFiles()) {
            if (file.getName().toLowerCase().endsWith(".java"))
                file.delete();
        }

        for (File xmlFile : sourceDir.listFiles()) {
            final String className = generateClassName(xmlFile);
            final String xml = StreamUtil.getFileContent(new FileReader(xmlFile));
            final String code = generateCode(className, _package, xml);
            System.out.println("Write "+className);

            FileWriter out = new FileWriter(new File(destDir, className + ".java"));
            out.write(code);
            out.close();

        }
    }


    private static void addAll(NodeList nodeList, List<Node> toBeAnalyzed) {
        for (int i = 0; i < nodeList.getLength(); i++)
            toBeAnalyzed.add(nodeList.item(i));
    }

    public static String generateClassName(File layoutFile) {
        final String s = layoutFile.getName().replace(".", "_");
        return "LayoutObjs_" + s;
    }

    public static String generateCode(String _className, String _package, String xml) throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(new ByteArrayInputStream(xml.getBytes()));

        //optional, but recommended
        //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        final Element documentElement = doc.getDocumentElement();
        documentElement.normalize();

        //System.out.println("Root element :" + documentElement.getNodeName());

        LinkedList<Node> toBeAnalyzed = new LinkedList<Node>();
        TreeSet<String> foundIds = new TreeSet<String>();
        StringBuilder declaration = new StringBuilder();
        StringBuilder onCreate = new StringBuilder();


        NodeList nList = doc.getElementsByTagName("*");
        addAll(nList, toBeAnalyzed);
        while (toBeAnalyzed.size() > 0) {
            Node current = toBeAnalyzed.removeFirst();
            if (current.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) current;
                String attrib = eElement.getAttribute("android:id");

                if (attrib != null && attrib.trim().length() > 0) {

                    String id = attrib.replace("@+id/", "");
                    String tagName = eElement.getTagName();
                    if (foundIds.contains(id))
                        continue;
                    foundIds.add(id);
                    //System.out.println(tagName + " " + id);
                    declaration.append(String.format("  public final %s %s;\n", tagName, id));
                    onCreate.append(String.format("  %s= (%s)view.findViewById(R.id.%s);\n", id, tagName, id));
                }
                addAll(eElement.getElementsByTagName("*"), toBeAnalyzed);
            }

        }
        String s1 = String.format(


                "package " + _package + ";\n" +
                        "import android.app.*;\n" +
                        "import android.view.*;\n" +
                        "import android.widget.*;\n" +
                        "import android.webkit.*;\n" +
                        "import it.gov.fermitivoli.R;\n" +

                        "public class " + _className + "{\n%s\n" +
                        "public " + _className + "(Fragment f){\n" +
                        "  View view=f.getView();\n" +
                        "  %s" +
                        "}\n" +
                        "public " + _className + "(Activity view){\n" +
                        "  %s\n" +
                        "}\n" +
                        "public " + _className + "(View view){\n" +
                        "  %s\n" +
                        "}\n" +
                        "public " + _className + "(Dialog view){\n" +
                        "  %s\n" +
                        "}\n" +
                        "}\n" +

                        "   \n" + "", declaration, onCreate, onCreate, onCreate, onCreate);
        return s1;
    }


}

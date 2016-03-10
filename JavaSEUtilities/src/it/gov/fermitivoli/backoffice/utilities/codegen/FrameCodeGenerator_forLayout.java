package it.gov.fermitivoli.backoffice.utilities.codegen;

import it.gov.fermitivoli.backoffice.utilities.util.StreamUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

/**
 * Generate variables associated to the specific xml layout
 */
public class FrameCodeGenerator_forLayout {

    public static void main(String[] args) throws IOException {
        MyFrame f = new MyFrame();
        //f.setXml(new File("/Users/stefano/DATA/scuola/insegnamento/scuola-AS-2014-15/Fermi-TIVOLI-14-15/development/FermiDevelop/FermiApp/res/layout/main.xml"));
        //f.setXml(new File("/Users/stefano/DATA/scuola/insegnamento/scuola-AS-2014-15/Fermi-TIVOLI-14-15/development/FermiDevelop/FermiApp/res/layout/activity__activitywebview.xml"));
        //f.setXml(codeDeclaration);
        f.setVisible(true);

    }

    private static void addAll(NodeList nodeList, List<Node> toBeAnalyzed) {
        for (int i = 0; i < nodeList.getLength(); i++)
            toBeAnalyzed.add(nodeList.item(i));
    }

    public static String[] generateCode(String xml) throws Exception {
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
                    System.out.println(tagName + " " + id);
                    declaration.append(String.format("  protected final %s %s;\n", tagName, id));
                    onCreate.append(String.format("  %s= (%s)view.findViewById(R.id.%s);\n", id, tagName, id));
                }
                addAll(eElement.getElementsByTagName("*"), toBeAnalyzed);
            }

        }
        String s1 = String.format(
                "   //DECLARATION\n" +
                        "   //***************************\n" +
                        "private class LayoutObjs{\n%s\n" +
                        "private LayoutObjs(Fragment f){\n" +
                        "  View view=f.getView();\n" +
                        "  %s" +
                        "}\n" +
                        "private LayoutObjs(Activity view){\n" +
                        "  %s\n" +
                        "}\n" +
                        "private LayoutObjs(View view){\n" +
                        "  %s\n" +
                        "}\n" +
                        "private LayoutObjs(Dialog view){\n" +
                        "  %s\n" +
                        "}\n" +
                        "}\n" +
                        "private LayoutObjs LAYOUT_OBJs;" +
                        "   //***************************\n" + "", declaration, onCreate, onCreate, onCreate, onCreate);
        String s2 = String.format(
                "   //ON CREATE method\n" +
                        "   //**************************\n" +
                        "   LAYOUT_OBJs=new LayoutObjs(this);\n" +
                        "   //**************************\n");
        return new String[]{s1, s2};
    }

    private static class MyFrame extends JFrame {
        final JTextArea xml = new JTextArea();
        final JTextArea codeDeclaration = new JTextArea();
        final JTextArea codeCreate = new JTextArea();
        final JButton perform = new JButton("Generate code");
        final JButton clear = new JButton("Paste from clipboard & generate");
        final JButton copyDeclaration = new JButton("Copy declaration");
        final JButton copyCreate = new JButton("Copy create");

        private MyFrame() throws HeadlessException {
            setLayout(new VerticalLayout());
            add(new JLabel("Paste your LAYOUT xml here"));
            add(new JScrollPane(xml));
            final JPanel p1 = new JPanel();


            p1.add(clear);
            p1.add(perform);
            add(p1);
            xml.setLineWrap(true);
            xml.setRows(10);
            xml.setColumns(60);


            codeDeclaration.setEditable(false);
            codeDeclaration.setRows(10);
            codeDeclaration.setColumns(60);
            codeCreate.setEditable(false);
            codeCreate.setRows(10);
            codeCreate.setColumns(60);

            {
                JPanel p2 = new JPanel(new GridLayout());
                p2.setPreferredSize(new Dimension(600, 40));
                JLabel l1 = new JLabel("Declaration code");
                l1.setHorizontalAlignment(SwingConstants.CENTER);
                p2.add(l1);
                JLabel l2 = new JLabel("On create code");
                l2.setHorizontalAlignment(SwingConstants.CENTER);
                p2.add(l2);
                add(p2);
            }
            {
                JPanel p2 = new JPanel(new GridLayout(1, 2, 10, 10));
                p2.setPreferredSize(new Dimension(750, 250));
                p2.add(new JScrollPane(codeDeclaration));
                p2.add(new JScrollPane(codeCreate));
                add(p2);
            }

            {
                JPanel p2 = new JPanel(new GridLayout(1, 2, 100, 100));
                p2.setPreferredSize(new Dimension(750, 30));
                p2.add(copyDeclaration);
                p2.add(copyCreate);
                add(p2);
                copyDeclaration.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //copy clip
                        StringSelection selection = new StringSelection(codeDeclaration.getText());
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(selection, selection);

                    }
                });

                copyCreate.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //copy clip
                        StringSelection selection = new StringSelection(codeCreate.getText());
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(selection, selection);

                    }
                });
            }

            perform.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        String[] strings = generateCode(xml.getText());
                        codeDeclaration.setText(strings[0]);
                        codeCreate.setText(strings[1]);

                    } catch (Exception e1) {
                        e1.printStackTrace();
                        JOptionPane.showMessageDialog(MyFrame.this, "Errore " + e1.getMessage());
                    }
                }
            });

            clear.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        String data = (String) Toolkit.getDefaultToolkit()
                                .getSystemClipboard().getData(DataFlavor.stringFlavor);
                        xml.setText(data);
                        perform.doClick();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        JOptionPane.showMessageDialog(clear, "Impossibile leggere dagli appunti");
                    }
                }
            });
            this.setSize(800, 800);
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        }

        void setXml(File xml) throws IOException {
            FileReader r = new FileReader(xml);
            setXml(StreamUtil.getFileContent(r));
            r.close();
        }

        void setXml(String xml) {
            this.xml.setText(xml);
            perform.doClick();
        }
    }
}

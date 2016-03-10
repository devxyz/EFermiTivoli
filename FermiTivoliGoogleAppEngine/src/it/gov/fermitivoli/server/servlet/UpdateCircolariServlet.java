package it.gov.fermitivoli.server.servlet;

import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import it.gov.fermitivoli.model.C_CircolareDto;
import it.gov.fermitivoli.server.GAE_Settings;
import it.gov.fermitivoli.server.datastore.DataStoreOptimizer;
import it.gov.fermitivoli.server.datastore.IDataStoreOptimizer;
import it.gov.fermitivoli.util.C_CircolariUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Created by stefano on 01/08/15.
 */
public class UpdateCircolariServlet extends HttpServlet {
    private static final boolean DEBUG = true;
    private static final Semaphore sem = new Semaphore(1);

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    public static List<C_CircolareDto> downloadListaCircolari() throws Exception {
        final Document parse = Jsoup.parse(new URL(GAE_Settings.LISTA_CIRCOLARI_JOOMLA_URL), GAE_Settings.LISTA_CIRCOLARI_TIMEOUT_MILLIS);
        return C_CircolariUtil.parseFromHtmlFromJoomla(parse, GAE_Settings.LISTA_CIRCOLARI_BASEURL);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //check call id
        final String secretKey = request.getParameter("secret-key");
        if (secretKey == null || !secretKey.equals(GAE_Settings.SECRET_KEY)) {
            System.out.println("Secret key error " + new Date());
            return;
        }

        final String sync = request.getParameter("sync");

        try {
            sem.acquire();

            if (DEBUG) {
                System.out.println("UpdateCircolariServlet " + new Date());
            }

            //scarica lista circolari aggiornate
            //==================================================================================
            final List<C_CircolareDto> circolariWEB = downloadListaCircolari();
            //map circolari by key
            final Map<String, C_CircolareDto> circolariWEB_MAP = new HashMap<>();
            for (C_CircolareDto c : circolariWEB) {
                circolariWEB_MAP.put(c.getKey(), c);
            }

            if (DEBUG)
                System.out.println("Circolari totali del sito web: " + circolariWEB.size());


            //scarica le circolari del db
            //==================================================================================
            final IDataStoreOptimizer ds = DataStoreOptimizer.getInstance();


            // cerca nel db quelle non piu' valide (da rimuovere)
            //==================================================================================
            final ArrayList<C_CircolareDto> todoRemove = new ArrayList<>();
            {
                for (C_CircolareDto e : ds.listAllCircolariAttive()) {
                    if (!circolariWEB_MAP.containsKey(e.getKey())) {
                        //da rimuovere
                        todoRemove.add(e);
                    }
                }
            }

            //cerca le circolari da aggiungere
            //==================================================================================
            final ArrayList<C_CircolareDto> todoAdd = new ArrayList<>();
            {
                for (C_CircolareDto e : circolariWEB_MAP.values()) {
                    if (!ds.isActive(e.getKey())) {
                        //da aggiungere
                        todoAdd.add(e);
                    }
                }
            }

            //effettua le operazioni nel db
            //==================================================================================
            if (todoAdd.size() + todoRemove.size() > 0) {
                if (todoAdd.size() > 0) {
                    for (C_CircolareDto x : todoAdd) {
                        ds.insert(x);
                    }
                    if (DEBUG)
                        System.out.println("Circolari da aggiungere nel DB: " + todoAdd.size());
                }

                if (todoRemove.size() > 0) {
                    for (C_CircolareDto x : todoRemove) {
                        ds.delete(x.getKey());
                    }
                    if (DEBUG)
                        System.out.println("Circolari da eliminare nel DB: " + todoRemove.size());
                }

            }

            if (DEBUG) {
                System.out.println("AGGIUNTE " + todoAdd.size() + " circolari");
                System.out.println(("RIMOSSE " + todoRemove.size() + " circolari"));
            }

            final boolean flagSync = sync != null && sync.equals("true");

            //controlla le circolari da aggiornare perche' senza testo
            final com.google.appengine.api.taskqueue.Queue q = QueueFactory.getQueue("scarica-circolare");

            {
                List<C_CircolareDto> todoWork = new ArrayList<>();
                for (C_CircolareDto c : ds.listAllCircolariAttive()) {
                    if (c.getTesto() == null) {
                        todoWork.add(c);
                    }
                }
                Collections.sort(todoWork, new Comparator<C_CircolareDto>() {
                    @Override
                    public int compare(C_CircolareDto o1, C_CircolareDto o2) {
                        return -o1.getNumero() + o2.getNumero();
                    }
                });
                if (DEBUG) {
                    System.out.println("CIRCOLARI NO FULLTEXT DA SCARICARE " + todoWork.size());
                }

                int count = 0;
                if (false) {
                    for (final C_CircolareDto c : todoWork) {
                        // DownloadCircolareServlet.doTask(c.getKey());
                    }
                } else {

                    ArrayList<ArrayList<String>> kk = new ArrayList<>();
                    kk.add(new ArrayList<String>());
                    for (C_CircolareDto c : todoWork) {
                        ArrayList<String> last = kk.get(kk.size() - 1);
                        if (last.size() >= 5) {
                            last = new ArrayList<>();
                            kk.add(last);
                        }
                        last.add(c.getKey());

                       /*
                        if (count++ > 10)
                            return;*/
                    }

                    for (ArrayList<String> z : kk) {
                        final String s = z.toString();
                        String query = s.substring(1, s.length() - 1);
                        System.out.println("QUERY " + query);
                        q.add(TaskOptions.Builder.withUrl("/DownloadCircolareServlet").param("keys", query).param("secret-key", GAE_Settings.SECRET_KEY));

                    }

                }
            }

            //final ExecutorService es = Executors.newFixedThreadPool(5);


        } catch (Throwable e) {
            e.printStackTrace();
            throw new IOException(e);
        } finally {
            sem.release();
        }
    }

}

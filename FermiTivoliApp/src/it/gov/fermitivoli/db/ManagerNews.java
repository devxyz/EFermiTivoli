package it.gov.fermitivoli.db;

import android.database.Cursor;
import android.util.Log;
import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;
import it.gov.fermitivoli.dao.*;
import it.gov.fermitivoli.model.C_NewsDto;
import it.gov.fermitivoli.model.TermineInfoWeb;
import it.gov.fermitivoli.parser.ItalianWordSplit;
import it.gov.fermitivoli.util.DebugUtil;
import it.gov.fermitivoli.util.DtoUtil;
import it.gov.fermitivoli.util.QueryCache;
import org.tartarus.snowball.ext.ItalianStemmer;

import java.text.MessageFormat;
import java.util.*;

/**
 * Created by stefano on 21/07/15.
 */
public class ManagerNews {
    /**
     * query
     */
    public final QueryCache<NewsContieneTermineDB> queryRicercaTerminiNewsByIdNews = new QueryCache<NewsContieneTermineDB>() {
        @Override
        protected QueryBuilder<NewsContieneTermineDB> queryBuilder(DaoSession session) {
            NewsContieneTermineDBDao dd = session.getNewsContieneTermineDBDao();
            QueryBuilder<NewsContieneTermineDB> b = dd.queryBuilder();
            return b.where(NewsContieneTermineDBDao.Properties._id_news.eq("?"));
        }

        @Override
        protected void setParameters(DaoSession session, Query<NewsContieneTermineDB> q, Object... parametri) {
            if (parametri.length != 1)
                throw new IllegalArgumentException("Specificare id_News");
            //imposta il termine
            long numero = ((Number) parametri[0]).longValue();
            q.setParameter(0, numero);
        }

        @Override
        protected void setParameters(DaoSession session, DeleteQuery<NewsContieneTermineDB> q, Object... parametri) {
            if (parametri.length != 1)
                throw new IllegalArgumentException("Specificare id_News");
            //imposta il termine
            long numero = ((Number) parametri[0]).longValue();
            q.setParameter(0, numero);
        }
    };


    private final DaoSession session;
    private final ItalianStemmer stemmer = new ItalianStemmer();

    public ManagerNews(DaoSession session) {
        this.session = session;
    }

    public static void sortLastToFirst(List<NewsDB> cc) {
        Collections.sort(cc, new Comparator<NewsDB>() {
            @Override
            public int compare(NewsDB o1, NewsDB o2) {
                return -o1.getPubDate().compareTo(o2.getPubDate());
            }
        });

    }

    private TermineDB __aggiungiTermine(String termine) {
        //cerca il termine
        TermineDBDao TermineDBDao = session.getTermineDBDao();

        TermineDB t = new TermineDB();
        t.setTermine(termine);
        t.setRadice(stemmer.stem(termine));
        TermineDBDao.insert(t);
        return t;

    }

    public Set<String> listAllTermini() {
        TreeSet<String> termini = new TreeSet<>();
        Cursor c = session.getDatabase().rawQuery("select DISTINCT " + TermineDBDao.Properties.Termine.columnName + " from " +
                TermineDBDao.TABLENAME + " order by " + TermineDBDao.Properties.Termine.columnName, null);
        if (c.moveToFirst()) {
            do {
                termini.add(c.getString(0));
            } while (c.moveToNext());
        }
        c.close();
        return termini;
    }


    private void inserisciTermini(NewsDB news) {

        final Set<TermineInfoWeb> termini;
        if (news.getTesto() == null)
            termini = ItalianWordSplit.parseWords(news.getTitolo());
        else
            termini = ItalianWordSplit.parseWords(news.getTitolo(), news.getTesto());


        //inserisce i termini/circolari mancanti
        final NewsContieneTermineDBDao ctdao = session.getNewsContieneTermineDBDao();

        //elenco termini
        final Map<String, TermineDB> mapTerminiIndexByTermine = new TreeMap<String, TermineDB>();
        {
            TermineDBDao TermineDBDao = session.getTermineDBDao();
            final List<TermineDB> tt = TermineDBDao.queryBuilder().list();
            for (TermineDB TermineDB : tt) {
                mapTerminiIndexByTermine.put(TermineDB.getTermine(), TermineDB);
            }
        }


        for (TermineInfoWeb t : termini) {
            final String termine = t.getTermine();

            NewsContieneTermineDB NewsTermineDB = null;

            //se non c'e' il collegamento....

            //controlla se il termine esiste
            TermineDB TermineDB = mapTerminiIndexByTermine.get(termine);
            if (TermineDB == null) {
                TermineDB = __aggiungiTermine(termine);
                mapTerminiIndexByTermine.put(termine, TermineDB);
            }

            NewsTermineDB = new NewsContieneTermineDB();
            NewsTermineDB.setNewsDB(news);
            NewsTermineDB.setTermineDB(TermineDB);
            NewsTermineDB.setOccorrenze(t.getOccorrenze());
            ctdao.insert(NewsTermineDB);

        }
    }

    public List<NewsDB> listAllNews() {
        final List<NewsDB> list = session.getNewsDBDao().queryBuilder().list();
        return list;
    }


    private NewsDB cercaNewsDB(Collection<NewsDB> cc, String key) {
        for (NewsDB x : cc) {
            if (x.getKey().equalsIgnoreCase(key))
                return x;
        }
        return null;
    }

    public List<NewsDB> selectNewsByTerms(Set<String> termini) {

        //estrae le radici dei termini
        Set<String> radiciTermini = new TreeSet<String>();
        ItalianStemmer is = new ItalianStemmer();
        for (String s : termini) {
            if (s.contains("(") || s.contains(")"))
                radiciTermini.add(s);
            else
                radiciTermini.add(is.stem(s));
        }

        //lavora sulla query di ricerca
        final String[] radici = radiciTermini.toArray(new String[radiciTermini.size()]);


        //estra tutti gli id News di interesse
        final TreeSet<Long> idCircolari;
        {
            final StringBuffer sb = new StringBuffer();
            for (String t : radiciTermini) {
                if (sb.length() > 0) {
                    sb.append("\n INTERSECT \n");
                }


                //ordine della select uguale all'ordine del costruttore di NewsNewsDB e cioe'
                // Long id, java.util.Date dataInserimento, boolean flagContenutoScaricato, java.util.Date data, int numero, String titolo, String url
                final String TAB_C = NewsDBDao.TABLENAME;
                final String TAB_T = TermineDBDao.TABLENAME;

                String select = MessageFormat.format("SELECT {0}.{1}", TAB_C, NewsDBDao.Properties.Id.columnName);

                final String TAB_CT = NewsContieneTermineDBDao.TABLENAME;
                String q = select +
                        " from " +
                        TAB_C + " JOIN " + TAB_CT +
                        " on " + TAB_C + "." + NewsDBDao.Properties.Id.columnName + " = " + TAB_CT + "." + NewsContieneTermineDBDao.Properties._id_news.columnName +
                        " JOIN " + TAB_T +
                        " on " + TAB_CT + "." + NewsContieneTermineDBDao.Properties._id_termine.columnName + " = " + TAB_T + "." + TermineDBDao.Properties.Id.columnName +
                        " where " + TAB_T + "." + TermineDBDao.Properties.Radice.columnName + " like ?";
                sb.append(q);
            }

            final String sql = sb.toString();
            if (DebugUtil.DEBUG__ManagerNews)
                Log.d("CERCA_News_FRAG", "LIST CIRCOLARI (Query: " + sql + ")");


            //parametri
            final ArrayList<String> terminiLike = new ArrayList<String>();
            for (int i = 0; i < radici.length; i++) {
                final String r = radici[i].trim();

                if (r.length() > 0)
                    terminiLike.add(r + "%");
            }

            //esegue la query
            idCircolari = new TreeSet<Long>();
            final Cursor c = session.getDatabase().rawQuery(sql, terminiLike.toArray(new String[terminiLike.size()]));
            if (c.moveToFirst()) {
                do {
                    final long __idNews = c.getLong(0);
                    idCircolari.add(__idNews);
                } while (c.moveToNext());
            }
            c.close();
        }

        //carica le circolari
        final List<NewsDB> circolariFinali;
        {
            final QueryBuilder<NewsDB> qb = session.getNewsDBDao().queryBuilder().where(NewsDBDao.Properties.Id.in(idCircolari));
            circolariFinali = qb.list();

        }

        if (DebugUtil.DEBUG__ManagerNews)
            Log.d("CERCA_News_FRAG", "LIST NEWS (News trovate: " + circolariFinali.size() + ")");

        return (circolariFinali);
    }

    public void sincronizzaLista(List<C_NewsDto> listaDaAggiungere, Collection<String> listaDaRimuovere) {
        final NewsDBDao dao = session.getNewsDBDao();
        final List<NewsDB> lisdb = dao.queryBuilder().list();

        //rimuove le news non presenti
        for (String x : listaDaRimuovere) {
            final NewsDB it = cercaNewsDB(lisdb, x);
            dao.delete(it);
        }

        //aggiunge quelli nuovi
        for (C_NewsDto x : listaDaAggiungere) {
            NewsDB news = new NewsDB();
            news.setFlagContenutoLetto(false);
            news.setDataInserimento(new Date());
            DtoUtil.copy(x, news);
            dao.insert(news);
            inserisciTermini(news);
        }
    }


}

package it.gov.fermitivoli.db;

import android.database.Cursor;
import android.util.Log;
import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;
import it.gov.fermitivoli.dao.*;
import it.gov.fermitivoli.model.*;
import it.gov.fermitivoli.parser.ItalianWordSplit;
import it.gov.fermitivoli.util.DebugUtil;
import it.gov.fermitivoli.util.QueryCache;
import org.tartarus.snowball.ext.ItalianStemmer;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by stefano on 11/06/15.
 */
public class ManagerCircolare {

    /**
     * query
     */
    public final QueryCache<CircolareContieneTermineDB> queryRicercaTerminiCircolareByIdCircolare = new QueryCache<CircolareContieneTermineDB>() {
        @Override
        protected QueryBuilder<CircolareContieneTermineDB> queryBuilder(DaoSession session) {
            CircolareContieneTermineDBDao dd = session.getCircolareContieneTermineDBDao();
            QueryBuilder<CircolareContieneTermineDB> b = dd.queryBuilder();
            return b.where(CircolareContieneTermineDBDao.Properties._id_circolare.eq("?"));
        }

        @Override
        protected void setParameters(DaoSession session, Query<CircolareContieneTermineDB> q, Object... parametri) {
            if (parametri.length != 1)
                throw new IllegalArgumentException("Specificare id_circolare");
            //imposta il termine
            long numero = ((Number) parametri[0]).longValue();
            q.setParameter(0, numero);
        }

        @Override
        protected void setParameters(DaoSession session, DeleteQuery<CircolareContieneTermineDB> q, Object... parametri) {
            if (parametri.length != 1)
                throw new IllegalArgumentException("Specificare id_circolare");
            //imposta il termine
            long numero = ((Number) parametri[0]).longValue();
            q.setParameter(0, numero);
        }
    };

    /**
     * query
     */
    public final QueryCache<TermineDB> queryRicercaTerminiNonCollegati = new QueryCache<TermineDB>() {
        @Override

        protected QueryBuilder<TermineDB> queryBuilder(DaoSession session) {
            String subQuery =
                    " select * from " + TermineDBDao.TABLENAME + " WHERE " + TermineDBDao.Properties.Id + " not in " +
                            "( select " + CircolareContieneTermineDBDao.Properties._id_termine + " from " + CircolareContieneTermineDBDao.TABLENAME + ")";
            TermineDBDao dd = session.getTermineDBDao();
            QueryBuilder<TermineDB> b = dd.queryBuilder().where(new WhereCondition.StringCondition(TermineDBDao.Properties.Id + "not in " +
                    "(" + subQuery + ")"));
            return b;
        }

        @Override
        protected void setParameters(DaoSession session, Query<TermineDB> q, Object... parametri) {
            if (parametri.length != 0)
                throw new IllegalArgumentException("No parametri");
        }

        @Override
        protected void setParameters(DaoSession session, DeleteQuery<TermineDB> q, Object... parametri) {
            if (parametri.length != 0)
                throw new IllegalArgumentException("No parametri");
        }
    };


    private final ItalianStemmer stemmer = new ItalianStemmer();
    private final DaoSession session;

    public ManagerCircolare(DaoSession session) {
        this.session = session;
    }

    public static void copy(C_CircolareDto source, CircolareDB destination) {
        destination.setTitolo(source.getTitolo());
        destination.setData(source.getData());
        destination.setUrl(source.getUrl().getUrl());
        destination.setNumero(source.getNumero());
        destination.setTesto(source.getTesto());
        destination.setKey(source.getKey());
    }

    public static void sortLastToFirst(List<CircolareDB> cc) {

        if (cc == null) return;
        final Comparator<CircolareDB> comparator = getCircolareDBComparator();
        Collections.sort(cc, comparator);
    }

    public static Comparator<CircolareDB> getCircolareDBComparator() {
        return new Comparator<CircolareDB>() {
            @Override
            public int compare(CircolareDB a, CircolareDB b) {
                final C_MyDate d1 = getDataGiornoMeseAnno(a.getData());
                final C_MyDate d2 = getDataGiornoMeseAnno(b.getData());
                final int i = d1.compareTo(d2);
                if (i != 0) return -i;
                if (a.getNumero() < b.getNumero())
                    return 1;
                else if (a.getNumero() > b.getNumero())
                    return -1;

                return a.getTitolo().compareTo(b.getTitolo());
            }

            public C_MyDate getDataGiornoMeseAnno(Date data) {
                return new C_MyDate(data);
            }
        };
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

    public List<CircolareDB> listAllCircolari() {
        final List<CircolareDB> list = session.getCircolareDBDao().queryBuilder().list();
        return (list);
    }

    public long maxToken() {
        final Cursor c = session.getDatabase().rawQuery("SELECT MAX(" + CircolareDBDao.Properties.Token.columnName + ") FROM " + CircolareDBDao.TABLENAME, null);
        final long result;
        if (c.moveToFirst()) {
            result = c.getLong(0);
        } else {
            result = 0;
        }
        c.close();
        return result;
    }

    public List<CircolareDB> listAllCircolariComplete() {
        final List<CircolareDB> list = session.getCircolareDBDao().queryBuilder()
                .where(CircolareDBDao.Properties.Testo.isNotNull()).list();
        return (list);
    }

    public List<CircolareDB> selectCircolariByTerms(Set<String> termini) {

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


        //estra tutti gli id circolare di interesse
        final TreeSet<Long> idCircolari;
        {
            final StringBuffer sb = new StringBuffer();
            for (String t : radiciTermini) {
                if (sb.length() > 0) {
                    sb.append("\n INTERSECT \n");
                }


                //ordine della select uguale all'ordine del costruttore di CircolareNewsDB e cioe'
                // Long id, java.util.Date dataInserimento, boolean flagContenutoScaricato, java.util.Date data, int numero, String titolo, String url
                final String TAB_C = CircolareDBDao.TABLENAME;
                final String TAB_T = TermineDBDao.TABLENAME;

                String select = MessageFormat.format("SELECT {0}.{1}", TAB_C, CircolareDBDao.Properties.Id.columnName);

                final String TAB_CT = CircolareContieneTermineDBDao.TABLENAME;
                String q = select +
                        " from " +
                        TAB_C + " JOIN " + TAB_CT +
                        " on " + TAB_C + "." + CircolareDBDao.Properties.Id.columnName + " = " + TAB_CT + "." + CircolareContieneTermineDBDao.Properties._id_circolare.columnName +
                        " JOIN " + TAB_T +
                        " on " + TAB_CT + "." + CircolareContieneTermineDBDao.Properties._id_termine.columnName + " = " + TAB_T + "." + TermineDBDao.Properties.Id.columnName +
                        " where " + TAB_T + "." + TermineDBDao.Properties.Radice.columnName + " like ?";
                sb.append(q);
            }

            final String sql = sb.toString();
            if (DebugUtil.DEBUG__ManagerCircolare)
                Log.d("CERCA_CIRCOLARE_FRAG", "LIST CIRCOLARI (Query: " + sql + ")");


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
                    final long __idCircolare = c.getLong(0);
                    idCircolari.add(__idCircolare);
                } while (c.moveToNext());
            }
            c.close();
        }

        //carica le circolari
        final List<CircolareDB> circolariFinali;
        {
            final QueryBuilder<CircolareDB> qb = session.getCircolareDBDao().queryBuilder().where(CircolareDBDao.Properties.Id.in(idCircolari));
            circolariFinali = qb.list();

        }

        if (DebugUtil.DEBUG__ManagerCircolare)
            Log.d("CERCA_CIRCOLARE_FRAG", "LIST CIRCOLARI (Circolari trovate: " + circolariFinali.size() + ")");

        return (circolariFinali);
    }


    public CircolariContainerByDate circolariByDate() {

        //mappa delle circolari (mappa mediante id)
        final Map<Long, CircolareDB> circolari = new TreeMap<Long, CircolareDB>();
        final Map<C_MyDate, Set<CircolareDB>> circolariByDataPubblicazione = new TreeMap<C_MyDate, Set<CircolareDB>>();


        final List<CircolareDB> cc = session.getCircolareDBDao().queryBuilder().list();
        for (CircolareDB c : cc) {
            circolari.put(c.getId(), c);
            final C_MyDate dataPubblicazione = new C_MyDate(c.getData());

            Set<CircolareDB> x = circolariByDataPubblicazione.get(dataPubblicazione);
            if (x == null) {
                x = new TreeSet<CircolareDB>(getCircolareDBComparator());
                circolariByDataPubblicazione.put(dataPubblicazione, x);
            }
            x.add(c);
        }

        String sql = "select DISTINCT t1." + TermineDBDao.Properties.Termine.columnName + ",t2." + CircolareContieneTermineDBDao.Properties._id_circolare.columnName + "\n" +
                "from " + TermineDBDao.TABLENAME + " as t1," + CircolareContieneTermineDBDao.TABLENAME + " as t2\n" +
                "where t1." + TermineDBDao.Properties.Id.columnName + " = " + CircolareContieneTermineDBDao.Properties._id_termine.columnName + "\n" +
                "  and " + TermineDBDao.Properties.Termine.columnName + " like '%(#DATA)'\n";

        final SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
        final Map<C_MyDate, Set<CircolareDB>> circolariByDataApplicazione = new TreeMap<C_MyDate, Set<CircolareDB>>();


        final Cursor c = session.getDatabase().rawQuery(sql, null);
        if (c.moveToFirst()) {
            do {
                try {
                    final String __termine = c.getString(0);
                    final long __idCircolare = c.getLong(1);

                    //seleziona
                    final String dataString = __termine.replace("(#DATA)", "");

                    final C_MyDate d = new C_MyDate(f.parse(dataString));

                    Set<CircolareDB> lista = circolariByDataApplicazione.get(d);
                    if (lista == null) {
                        lista = new TreeSet<CircolareDB>(getCircolareDBComparator());
                        circolariByDataApplicazione.put(d, lista);
                    }

                    lista.add(circolari.get(__idCircolare));

                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }

            } while (c.moveToNext());
        }
        c.close();


        return new CircolariContainerByDate(cc, circolariByDataApplicazione, circolariByDataPubblicazione);

    }


    private void inserisciTermini(CircolareDB circolare) {

        final Set<TermineInfoWeb> termini;
        if (circolare.getTesto() == null)
            termini = ItalianWordSplit.parseWords(circolare.getTitolo());
        else
            termini = ItalianWordSplit.parseWords(circolare.getTitolo(), circolare.getTesto());


        //inserisce i termini/circolari mancanti
        final CircolareContieneTermineDBDao ctdao = session.getCircolareContieneTermineDBDao();

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

            CircolareContieneTermineDB circolareTermineDB = null;

            //se non c'e' il collegamento....

            //controlla se il termine esiste
            TermineDB TermineDB = mapTerminiIndexByTermine.get(termine);
            if (TermineDB == null) {
                TermineDB = __aggiungiTermine(termine);
                mapTerminiIndexByTermine.put(termine, TermineDB);
            }

            circolareTermineDB = new CircolareContieneTermineDB();
            circolareTermineDB.setCircolareDB(circolare);
            circolareTermineDB.setTermineDB(TermineDB);
            circolareTermineDB.setOccorrenze(t.getOccorrenze());
            ctdao.insert(circolareTermineDB);

        }
    }

    private void cancellaTerminiNonUsati() {
        queryRicercaTerminiNonCollegati.delete(session);
    }

    private void cancellaTermini(CircolareDB circolare) {
        queryRicercaTerminiCircolareByIdCircolare.delete(session, circolare.getId());
    }

    /**
     * salva le circolari cc (aggiungendo quelle nuove se necessario e gestendo i termini)
     *
     * @param cc
     */
    public void salva(List<C_CircolareDto> cc) {
        final CircolareDBDao circolareDBDao = session.getCircolareDBDao();
        final List<CircolareDB> list = circolareDBDao.queryBuilder().list();
        final ArrayList<Pair<CircolareDB, C_CircolareDto>> pairs = raccordaCircolari(cc, list);

        //aggiorna le coppie comuni
        for (Pair<CircolareDB, C_CircolareDto> p : pairs) {
            CircolareDB cDB = p.a;
            C_CircolareDto cDTO = p.b;

            if (cDB != null && cDTO != null) {
                //solo aggiornamento del testo
                if (cDB.getTesto() == null && cDTO.getTesto() != null) {
                    copy(cDTO, cDB);
                    circolareDBDao.update(cDB);

                    //aggiornamento dei termini
                    cancellaTermini(cDB);
                    inserisciTermini(cDB);
                }
            } else {
                if (cDB == null && cDTO != null) {
                    //solo aggiornamento del testo
                    cDB = new CircolareDB();
                    cDB.setDataInserimento(new Date());
                    copy(cDTO, cDB);
                    circolareDBDao.insert(cDB);

                    //aggiornamento dei termini
                    inserisciTermini(cDB);
                }
            }

        }

        cancellaTerminiNonUsati();

    }


    private CircolareDB cercaCircolare(C_CircolareDto c) {
        return cercaCircolare(c.getKey());
    }

    private CircolareDB cercaCircolare(String key) {
        CircolareDBDao dd = session.getCircolareDBDao();
        QueryBuilder<CircolareDB> b = dd.queryBuilder();
        return b.where(CircolareDBDao.Properties.Key.eq(key)).unique();
    }

    /**
     * rimuove la circolare e i termini ad essa collegata
     */
    public void rimuoveCircolare(String key) {
        //cerca la circolare nel DB (data e numero)
        final CircolareDB circolareDB = cercaCircolare(key);
        if (circolareDB == null)
            return;//throw new IllegalArgumentException("Circolare " + key + " non nel database");
        rimuoveCircolare(circolareDB);
    }

    /**
     * rimuove la circolare e i termini ad essa collegata
     *
     * @param c
     */

    private void rimuoveCircolare(CircolareDB c) {
        //elenca i termini della circolare da rimuovere
        queryRicercaTerminiCircolareByIdCircolare.delete(session, c.getId()).executeDeleteWithoutDetachingEntities();
        session.getCircolareDBDao().delete(c);

        //rimuove eventuali termini non piu' usati
        queryRicercaTerminiNonCollegati.delete(session).executeDeleteWithoutDetachingEntities();
    }


    public final ArrayList<Pair<CircolareDB, C_CircolareDto>> raccordaCircolari(List<C_CircolareDto> _circolariWeb, List<CircolareDB> _circolariDB) {
        //------------------------------------------------------------
        //indicizza
        //------------------------------------------------------------
        final Map<String, CircolareDB> circolariDbByKey = getStringCircolareDBMap(_circolariDB);

        final Map<String, C_CircolareDto> circolariDtoByKey = getStringCircolareDtoMap(_circolariWeb);

        final ArrayList<Pair<CircolareDB, C_CircolareDto>> ris = new ArrayList<>();

        //------------------------------------------------------------
        //aggiunge le coppie (anche se non comuni)
        //------------------------------------------------------------
        for (C_CircolareDto c : _circolariWeb) {
            final CircolareDB c2 = circolariDbByKey.get(c.getKey());
            if (c2 == null)
                ris.add(new Pair<CircolareDB, C_CircolareDto>(null, c));
            else
                ris.add(new Pair<>(c2, c));
        }

        for (CircolareDB c : _circolariDB) {
            final C_CircolareDto c2 = circolariDtoByKey.get(c.getKey());
            if (c2 == null)
                ris.add(new Pair<CircolareDB, C_CircolareDto>(c, null));
        }

        return ris;
    }

    private Map<String, C_CircolareDto> getStringCircolareDtoMap(List<C_CircolareDto> _circolariWeb) {
        final Map<String, C_CircolareDto> circolariDtoByKey = new TreeMap<String, C_CircolareDto>();
        for (C_CircolareDto c : _circolariWeb) {
            circolariDtoByKey.put(c.getKey(), c);
        }
        return circolariDtoByKey;
    }

    private Map<String, CircolareDB> getStringCircolareDBMap(List<CircolareDB> _circolariDB) {
        final Map<String, CircolareDB> circolariDbByKey = new TreeMap<String, CircolareDB>();
        for (CircolareDB c : _circolariDB) {
            circolariDbByKey.put(c.getKey(), c);
        }
        return circolariDbByKey;
    }

    private TermineDB __aggiungiTermine(String termine)  {
        //cerca il termine
        TermineDBDao TermineDBDao = session.getTermineDBDao();

        TermineDB t = new TermineDB();
        t.setTermine(termine);
        t.setRadice(stemmer.stem(termine));
        TermineDBDao.insert(t);
        return t;

    }


}

package it.gov.fermitivoli.server.datastore;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Work;
import it.gov.fermitivoli.model.C_CircolareDto;
import it.gov.fermitivoli.server.model.GAE_CircolareDB_V2;
import it.gov.fermitivoli.server.util.GAE_DtoUtil;

import java.util.*;

/**
 * Classe per la gestione del datastore ottimale
 */
@Deprecated
public class DataStoreOptimizer implements IDataStoreOptimizer {
    private static final DataStoreOptimizer instance = new DataStoreOptimizer();
    private static final byte[] TOKEN_LABEL_MEMCACHE = "TOKEN".getBytes();
    private static final boolean DEBUG__DataStoreOptimizer = true;
    private final MemcacheService memcache;
    private final Objectify ofy;
    /**
     * ultimo token conosciuto (usato per capire se c'e' disallineamento con il server)
     */
    private long currentTokenNumber = -1;
    private TreeMap<String, GAE_CircolareDB_V2> indexKeyCircolare = new TreeMap<>();
    private TreeMap<Long, GAE_CircolareDB_V2> indexTokenCircolare = new TreeMap<>();
    private boolean flagValidData = false;
    private int countRead = 0;
    private int countInsert = 0;
    private int countUpdate = 0;
    private int countDelete = 0;

    private DataStoreOptimizer() {
        memcache = MemcacheServiceFactory.getMemcacheService();
        ofy = ObjectifyService.ofy().cache(true);
    }

    public static IDataStoreOptimizer getInstance() {
        return DataStoreOptimizerMC2.getInstance();
    }

    private long getNextToken() {
        __syncToken();
        return memcache.increment(TOKEN_LABEL_MEMCACHE, 1, 0L);
    }

    /**
     * sync token generator and return last used token
     *
     * @return
     */
    private long __syncToken() {
        Long lastUsedToken = (Long) memcache.get(TOKEN_LABEL_MEMCACHE);
        if (lastUsedToken == null) {
            final List<GAE_CircolareDB_V2> l = ofy.load().type(GAE_CircolareDB_V2.class).list();

            //indicizza le circolari
            final long token = __indexingCircolari(l);
            memcache.put(TOKEN_LABEL_MEMCACHE, token);
            lastUsedToken = token;

            if (l.size() == 0) {
                if (DEBUG__DataStoreOptimizer) {
                    System.out.println("SINCRONIZZAZIONE TOKEN (NESSUNA CIRCOLARE)");
                }
            } else {
                memcache.put(TOKEN_LABEL_MEMCACHE, token);
                if (DEBUG__DataStoreOptimizer) {
                    System.out.println("SINCRONIZZAZIONE TOKEN CIRCOLARE " + l.get(0));
                }
            }
            if (DEBUG__DataStoreOptimizer) {
                System.out.println("SINCRONIZZAZIONE TOKEN: lastUsedToken=" + lastUsedToken);
            }
        }
        return lastUsedToken;
    }

    private synchronized void __atomicInsert(final String key,
                                             final String titolo,
                                             final String testo,
                                             final Date data,
                                             final String url,
                                             final int numero) {
        final long token = getNextToken();
        final GAE_CircolareDB_V2 ris = ofy.transact(new Work<GAE_CircolareDB_V2>() {
            @Override
            public GAE_CircolareDB_V2 run() {
                GAE_CircolareDB_V2 c = new GAE_CircolareDB_V2();
                c.set(titolo, testo, data, key, url, numero);
                c.setFlagDelete(false);
                c.setToken(token);
                c.check();
                ofy.save().entity(c);
                return c;
            }
        });

        countInsert++;

        //aggiorna gli indici interni
        final GAE_CircolareDB_V2 cclone = ris.clone();
        indexKeyCircolare.put(ris.getKey(), cclone);
        indexTokenCircolare.put(ris.getToken(), cclone);

        //aggiorna il token condiviso
        currentTokenNumber = token;
        memcache.put(TOKEN_LABEL_MEMCACHE, currentTokenNumber);

        __checkIndices();

    }

    private void __checkIndices() {
        if (indexKeyCircolare.size() != indexTokenCircolare.size()) {
            throw new IllegalArgumentException("Program error: index not aligned. By key:" + indexKeyCircolare.size() + ", by token:" + indexTokenCircolare.size());
        }
    }

    private synchronized void __atomicLogicDelete(final String key) {
        __atomicTask(key, new DbOperation() {
            @Override
            public void doTask(GAE_CircolareDB_V2 c) {
                c.setFlagDelete(true);
            }

            @Override
            public void doIncrement() {
                countDelete++;
            }
        });

    }

    private synchronized void __atomicTask(final String key, final DbOperation op) {
        //SINCRONIZZA IL DATABASE SE NECESSARIO!!!!!
        __syncWithDatabase();
        final long token = getNextToken();

        final GAE_CircolareDB_V2 c = ofy.transact(new Work<GAE_CircolareDB_V2>() {
            @Override
            public GAE_CircolareDB_V2 run() {
                final GAE_CircolareDB_V2 c = ofy.load().type(GAE_CircolareDB_V2.class).id(key).now();
                if (c == null || c.isFlagDelete()) return c;

                //rimuove dagli indici la circolare precedente
                indexKeyCircolare.remove(c.getKey());
                indexTokenCircolare.remove(c.getToken());

                op.doTask(c);
                c.setToken(token);
                c.check();

                ofy.save().entity(c);

                return c;
            }
        });

        op.doIncrement();

        //aggiorna il token condiviso
        currentTokenNumber = token;
        memcache.put(TOKEN_LABEL_MEMCACHE, currentTokenNumber);


        //aggiorna gli indici interni
        if (c != null) {
            final GAE_CircolareDB_V2 cclone = c.clone();
            indexKeyCircolare.put(c.getKey(), cclone);
            indexTokenCircolare.put(c.getToken(), cclone);
        }

        __checkIndices();
    }

    private synchronized void __atomicUpdate(final String key, final String testo) {
        __atomicTask(key, new DbOperation() {
            @Override
            public void doTask(GAE_CircolareDB_V2 c) {
                c.setTesto(testo);
            }

            @Override
            public void doIncrement() {
                countUpdate++;
            }
        });

    }

    @Override
    public synchronized boolean isActive(String key) {
        __syncWithDatabase();

        final GAE_CircolareDB_V2 c = indexKeyCircolare.get(key);
        if (c == null) return false;
        return !c.isFlagDelete();
    }

    @Override
    public synchronized Set<String> getKeys() {
        __syncWithDatabase();

        return new TreeSet<>(indexKeyCircolare.keySet());
    }

    @Override
    public synchronized void delete(String key) {
        __atomicLogicDelete(key);
    }

    @Override
    public synchronized void insert(C_CircolareDto x) {
        insert(x.getKey(), x.getTitolo(), x.getTesto(), x.getData(), x.getUrl().getUrl(), x.getNumero());
    }

    @Override
    public synchronized void insert(String key,
                                    String titolo,
                                    String testo,
                                    Date data,
                                    String url,
                                    int numero
    ) {
        __atomicInsert(key, titolo, testo, data, url, numero);

    }

    @Override
    public synchronized void update(String key,
                                    String testo
    ) {
        __atomicUpdate(key, testo);
    }

    private C_CircolareDto convert(GAE_CircolareDB_V2 c) {
        if (c == null) return null;
        C_CircolareDto ris = new C_CircolareDto();
        GAE_DtoUtil.copy(c, ris);
        return ris;
    }

    private List<C_CircolareDto> convert(Collection<GAE_CircolareDB_V2> cc) {
        List<C_CircolareDto> ris = new ArrayList<>(cc.size());
        for (GAE_CircolareDB_V2 c : cc) {
            ris.add(convert(c));
        }
        return ris;
    }

    @Override
    public synchronized String stats() {
        __syncWithDatabase();

        int activeComplete = 0;
        int active = 0;
        int delete = 0;
        for (GAE_CircolareDB_V2 x : indexTokenCircolare.values()) {
            if (!x.isFlagDelete()) {
                active++;
                if (x.getTesto() != null)
                    activeComplete++;
            } else {
                delete++;
            }
        }

        return "READ: " + countRead + ", INSERT:" + countInsert + ", UPDATE:" + countUpdate + ", DELETE:" + countDelete +
                " - Circolari: " + active + ", di cui fulltext:" + activeComplete + " - Circolari cancellate:" + delete +
                " - Dimensioni indici: token:" + indexTokenCircolare.size() + ", key:" + indexKeyCircolare.size();
    }

    private void __syncWithDatabase() {
        final long cacheVersion = __syncToken();

        if (!flagValidData || currentTokenNumber != cacheVersion) {
            final List<GAE_CircolareDB_V2> ris = ofy.load().type(GAE_CircolareDB_V2.class).list();
            __indexingCircolari(ris);


            if (DEBUG__DataStoreOptimizer) {
                System.out.println("AGGIORNAMENTO DATI DA DATABASE: " + ris.size() + " CIRCOLARI - CACHE VERSION = " + cacheVersion + ", PREC VERSION = " + currentTokenNumber);
            }

            //aggiornamento versioni
            currentTokenNumber = cacheVersion;
            flagValidData = true;
        }
    }

    private long __indexingCircolari(List<GAE_CircolareDB_V2> ris) {
        //aggiorna lista nel DB
        indexKeyCircolare.clear();
        indexTokenCircolare.clear();
        long token = 0;

        for (GAE_CircolareDB_V2 c : ris) {
            GAE_CircolareDB_V2 x;

            x = indexKeyCircolare.put(c.getKey(), c);
            if (x != null) {
                throw new IllegalArgumentException("Key " + c.getKey() + " duplicata: " + x + " - " + c);
            }

            x = indexTokenCircolare.put(c.getToken(), c);
            if (x != null) {
                throw new IllegalArgumentException("Token " + c.getToken() + " duplicato: " + x + " - " + c);
            }
            token = Math.max(token, c.getToken());

            countRead++;
        }
        return token;
    }

    @Override
    public synchronized List<C_CircolareDto> listAllCircolariAttive(long fromToken) {
        __syncWithDatabase();

        List<C_CircolareDto> ris = new ArrayList<>();
        //elenco di tutte le circolari con token maggiore di quello specificato ed attive
        final SortedMap<Long, GAE_CircolareDB_V2> m = indexTokenCircolare.tailMap(fromToken + 1);
        for (GAE_CircolareDB_V2 c : m.values()) {
            if (!c.isFlagDelete())
                ris.add(convert(c));
        }
        return ris;

    }

    @Override
    public synchronized List<C_CircolareDto> listAllCircolariAttive() {
        __syncWithDatabase();

        List<C_CircolareDto> ris = new ArrayList<>();
        //elenco di tutte le circolari con token maggiore di quello specificato ed attive
        for (GAE_CircolareDB_V2 c : indexTokenCircolare.values()) {
            if (!c.isFlagDelete())
                ris.add(convert(c));
        }
        return ris;

    }

    @Override
    public synchronized Set<String> listAllCircolariEliminate(long fromToken) {
        __syncWithDatabase();
        //se si inizia da 0, non si forniscono le circolari eliminate
        if (fromToken <= 0)
            return new TreeSet<>();

        Set<String> ris = new TreeSet<>();

        //elenco di tutte le circolari con token maggiore di quello specificato ed attive
        final SortedMap<Long, GAE_CircolareDB_V2> m = indexTokenCircolare.tailMap(fromToken + 1);
        for (GAE_CircolareDB_V2 c : m.values()) {
            if (c.isFlagDelete())
                ris.add(c.getKey());
        }
        return ris;
    }

    @Override
    public synchronized Set<String> listAllCircolariEliminate() {
        __syncWithDatabase();

        Set<String> ris = new TreeSet<>();

        //elenco di tutte le circolari con token maggiore di quello specificato ed attive
        for (GAE_CircolareDB_V2 c : indexTokenCircolare.values()) {
            if (c.isFlagDelete())
                ris.add(c.getKey());
        }
        return ris;

    }

    private GAE_CircolareDB_V2 __key(String key) {
        __syncWithDatabase();
        return indexKeyCircolare.get(key);
    }

    @Override
    public synchronized C_CircolareDto getByKey(String key) {
        __syncWithDatabase();
        final GAE_CircolareDB_V2 c = indexKeyCircolare.get(key);
        return convert(c);
    }

    public void invalidate() {
        flagValidData = false;
        indexKeyCircolare.clear();
        indexTokenCircolare.clear();
    }

    public MemcacheService getMemcache() {
        return memcache;
    }

    private interface DbOperation {
        void doTask(GAE_CircolareDB_V2 c);

        void doIncrement();
    }
}

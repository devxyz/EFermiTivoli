package it.gov.fermitivoli.server.datastore;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.LoadType;
import it.gov.fermitivoli.model.C_CircolareDto;
import it.gov.fermitivoli.server.model.GAE_CircolareDB_V2;
import it.gov.fermitivoli.server.model.GAE_Token_V2;
import it.gov.fermitivoli.server.util.GAE_DtoUtil;

import java.util.*;

/**
 * caching intermendio
 */
@Deprecated
public class DataStoreOptimizerMC2 implements IDataStoreOptimizer {
    private static final DataStoreOptimizerMC2 instance = new DataStoreOptimizerMC2();
    private static final int MAX_AWAIT = 15000;
    private static final long ID_TOKEN = 1;
    private static final boolean DEBUG = true;
    private final MemcacheEntry<Long> LABEL_LAST_TOKEN;
    private final MemcacheEntry<String> LABEL_LIST_CIRCOLARI;
    private final MemcacheSemaphore sync;
    private final MemcacheService memcache;
    private final Objectify ofy;
    private final Map<String, GAE_CircolareDB_V2> allEntities = new TreeMap<>();
    private long token = -1;
    private int nread = 0;
    private int nwrite = 0;

    public DataStoreOptimizerMC2() {
        memcache = MemcacheServiceFactory.getMemcacheService();
        ofy = ObjectifyService.ofy();
        token = 0;
        //100 letture parallele, 1 scrittura
        sync = new MemcacheSemaphore(memcache, "__MiddleMemcache_SEM");
        LABEL_LAST_TOKEN = new MemcacheEntry<>(memcache, "XX_LABEL_LAST_TOKEN", Long.class);
        LABEL_LIST_CIRCOLARI = new MemcacheEntry<>(memcache, "XX_LABEL_LIST_CIRCOLARI", String.class);
    }

    public static DataStoreOptimizerMC2 getInstance() {
        return instance;
    }

    @Override
    public Set<String> listAllCircolariEliminate() {
        return listAllCircolariEliminate(-1000);
    }

    @Override
    public Set<String> listAllCircolariEliminate(final long fromToken) {
        return __filter(true, new __DoFilter<Set<String>>() {
            @Override
            public Set<String> doFilter(Map<String, GAE_CircolareDB_V2> allEntities) {
                Set<String> ris = new TreeSet<String>();
                for (GAE_CircolareDB_V2 x : allEntities.values()) {
                    if (x.isFlagDelete() && x.getToken() >= fromToken) {
                        ris.add(x.getKey());
                    }
                }
                return ris;
            }
        });
    }

    @Override
    public List<C_CircolareDto> listAllCircolariAttive() {
        return listAllCircolariAttive(-1000);
    }

    @Override
    public List<C_CircolareDto> listAllCircolariAttive(final long fromToken) {
        return __filter(true, new __DoFilter<List<C_CircolareDto>>() {
            @Override
            public List<C_CircolareDto> doFilter(Map<String, GAE_CircolareDB_V2> allEntities) {
                List<C_CircolareDto> ris = new ArrayList<C_CircolareDto>();
                for (GAE_CircolareDB_V2 x : allEntities.values()) {
                    if (x.getToken() >= fromToken && !x.isFlagDelete()) {
                        ris.add(convert(x));
                    }
                }
                return ris;
            }
        });
    }

    @Override
    public void insert(C_CircolareDto x) {
        insert(x.getKey(), x.getTitolo(), x.getTesto(), x.getData(), x.getUrl().getUrl(), x.getNumero());
    }

    @Override
    public void invalidate() {
        allEntities.clear();
        token = -1;
        memcache.clearAll();
    }

    @Override
    public Set<String> getKeys() {
        return __filter(true, new __DoFilter<Set<String>>() {
            @Override
            public Set<String> doFilter(Map<String, GAE_CircolareDB_V2> allEntities) {
                return new TreeSet<String>(allEntities.keySet());
            }
        });
    }

    @Override
    public C_CircolareDto getByKey(final String key) {
        return __filter(true, new __DoFilter<C_CircolareDto>() {
            @Override
            public C_CircolareDto doFilter(Map<String, GAE_CircolareDB_V2> allEntities) {
                return convert(allEntities.get(key));
            }
        });
    }

    @Override
    public String stats() {
        return "Token in RAM:" + token + ", circolari in RAM:" + allEntities.size() + ", db token:" + getToken() + ", nread:" + nread + ", nwrite:" + nwrite;
    }

    public boolean isSync() {
        return token == getToken();
    }

    public long getToken() {
        return __getToken(true);
    }

    private long __getToken(boolean useSemaphore) {
        Long t = LABEL_LAST_TOKEN.get();
        if (t != null) {
            return t;
        }

        if (useSemaphore)
            sync.acquireLock_unchecked(MAX_AWAIT);

        if (DEBUG) {
            System.out.println("Token non presente in cache - Legge token da db");
        }
        try {
            //se non presente in cache lo cerca nel DB
            GAE_Token_V2 gt = ofy.load().type(GAE_Token_V2.class).id(ID_TOKEN).now();
            nread++;
            if (gt == null) {
                if (DEBUG) {
                    System.out.println("Token non presente nel DB - lettura circolari dal db");
                }

                //legge le circolari ed aggiorna il token
                long tk = 0;
                final List<GAE_CircolareDB_V2> list = ofy.load().type(GAE_CircolareDB_V2.class).list();
                nread += list.size();
                allEntities.clear();
                for (GAE_CircolareDB_V2 x : list) {
                    allEntities.put(x.getKey(), x);
                    memcache.put(x.getKey(), x);
                    tk = Math.max(tk, x.getToken());
                }
                tk = tk + 1;

                if (DEBUG) {
                    System.out.println("Chiavi non presenti in cache - Aggiornamento completo dal DB e copia in cache. " + list.size() + " circolari totali");
                }
                LABEL_LIST_CIRCOLARI.put(formatKeys(new TreeSet<>(allEntities.keySet())));

                this.token = tk;
                gt = new GAE_Token_V2(tk);
                ofy.save().entity(gt).now();
                nwrite++;
            }
            LABEL_LAST_TOKEN.put(gt.currentToken);
            if (DEBUG) {
                System.out.println("Token del DB copiato in cache " + gt);
            }
            return gt.currentToken;
        } finally {
            if (useSemaphore)
                sync.releaseLock();
        }
    }

    private Set<String> parseKeys(String s) {
        s = s.trim();
        if (s.length() == 0) return new TreeSet<>();
        return new TreeSet<>(Arrays.asList(s.split(",")));
    }

    private String formatKeys(Set<String> keys) {
        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(key);
        }
        return sb.toString();
    }

    public void sync() {
        __sync(true);

    }

    private void __sync(boolean useSemaphore) {
        long lastToken = __getToken(useSemaphore);
        if (lastToken == token) {
            return;
        }

        //AGGIORNA
        if (useSemaphore)
            sync.acquireLock_unchecked(MAX_AWAIT);
        lastToken = __getToken(false);

        if (DEBUG) {
            System.out.println("Legge token da memcache");
        }
        try {
            //carica i dati dalla cache
            final String chiaviCircolari = LABEL_LIST_CIRCOLARI.get();
            final LoadType<GAE_CircolareDB_V2> loader = ofy.load().type(GAE_CircolareDB_V2.class);

            if (chiaviCircolari == null) {
                final List<GAE_CircolareDB_V2> list = loader.list();
                nread += list.size();
                allEntities.clear();
                for (GAE_CircolareDB_V2 x : list) {
                    allEntities.put(x.getKey(), x);
                    memcache.put(x.getKey(), x);
                }
                if (DEBUG) {
                    System.out.println("Chiavi non presenti in cache - Aggiornamento completo dal DB e copia in cache. " + list.size() + " circolari totali");
                }
                LABEL_LIST_CIRCOLARI.put(formatKeys(new TreeSet<>(allEntities.keySet())));
            } else {


                int count = 0;
                final Set<String> keys = parseKeys(chiaviCircolari);
                if (DEBUG) {
                    System.out.println("Chiavi in cache " + keys.size() + " - " + chiaviCircolari);
                }
                for (String key : keys) {
                    //legge le singole entity dalla cache
                    GAE_CircolareDB_V2 c = (GAE_CircolareDB_V2) memcache.get(key);
                    if (c == null) {
                        c = loader.id(key).now();
                        nread++;
                        memcache.put(c.getKey(), c);
                        count++;
                    }
                    allEntities.put(c.getKey(), c);
                }

                if (DEBUG) {
                    System.out.println("Chiavi in cache - Aggiornamento parziale dal DB. " + keys.size() + " circolari totali");
                }

                if (DEBUG) {
                    System.out.println("Lette nel DB " + count + " circolari");
                }
            }
            token = lastToken;
        } finally {
            if (useSemaphore)
                sync.releaseLock();
        }
    }

    private void __manage(boolean useSemaphore, __DoTask t) {
        //AGGIORNA
        if (useSemaphore)
            sync.acquireLock_unchecked(MAX_AWAIT);
        try {
            //sincronizza i dati
            __sync(false);

            //avvia il task (con dati aggiornati)
            final GAE_CircolareDB_V2 c = t.doTask();

            if (DEBUG) {
                System.out.println("MANAGE " + c.getKey());
            }

            //get new token
            final GAE_Token_V2 xx = ofy.load().type(GAE_Token_V2.class).id(1).now();
            nread++;

            xx.currentToken++;
            ofy.save().entity(xx).now();
            nwrite++;
            token = xx.currentToken;

            //imposta il token
            c.setToken(token);
            c.check();
            ofy.save().entity(c).now();
            nwrite++;

            if (DEBUG) {
                System.out.println("SAVING " + c.getKey() + " token " + c.getToken());
            }


            allEntities.put(c.getKey(), c);

            LABEL_LAST_TOKEN.put(token);
            LABEL_LIST_CIRCOLARI.put(formatKeys(allEntities.keySet()));
            memcache.put(c.getKey(), c);

        } finally {
            if (useSemaphore)
                sync.releaseLock();
        }
    }

    private <T> T __filter(boolean useSemaphore, __DoFilter<T> t) {
        //AGGIORNA
        if (useSemaphore)
            sync.acquireLock_unchecked(MAX_AWAIT);
        try {
            //sincronizza i dati
            __sync(false);

            return t.doFilter(new TreeMap<String, GAE_CircolareDB_V2>(allEntities));

        } finally {
            if (useSemaphore)
                sync.releaseLock();
        }
    }


    public void update(final String key,
                       final String testo
    ) {
        __manage(true, new __DoTask() {
            @Override
            public GAE_CircolareDB_V2 doTask() {
                GAE_CircolareDB_V2 c = allEntities.get(key);
                c.setTesto(testo);
                return c;
            }
        });

    }

    @Override
    public boolean isActive(final String key) {
        final GAE_CircolareDB_V2 c = __filter(true, new __DoFilter<GAE_CircolareDB_V2>() {
            @Override
            public GAE_CircolareDB_V2 doFilter(Map<String, GAE_CircolareDB_V2> allEntities) {
                return (allEntities.get(key));
            }
        });

        if (c == null) return false;
        return !c.isFlagDelete();
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


    public void delete(final String key

    ) {
        __manage(true, new __DoTask() {
            @Override
            public GAE_CircolareDB_V2 doTask() {
                GAE_CircolareDB_V2 c = allEntities.get(key);
                c.setFlagDelete(true);
                return c;
            }
        });

    }

    public void insert(final String key,
                       final String titolo,
                       final String testo,
                       final Date data,
                       final String url,
                       final int numero) {
        __manage(true, new __DoTask() {
            @Override
            public GAE_CircolareDB_V2 doTask() {
                GAE_CircolareDB_V2 c = new GAE_CircolareDB_V2();
                c.set(titolo, testo, data, key, url, numero);
                c.setFlagDelete(false);
                return c;
            }
        });
    }


    private static interface __DoTask {
        GAE_CircolareDB_V2 doTask();
    }

    private static interface __DoFilter<T> {
        T doFilter(Map<String, GAE_CircolareDB_V2> allEntities);
    }


}

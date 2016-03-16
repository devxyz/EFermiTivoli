package it.gov.fermitivoli.server.datalayer;

import it.gov.fermitivoli.server.datalayer.impl.CacheItem;

import java.util.*;

/**
 * definisce un livello di gestione della cache
 */
public abstract class CacheLayer<K, T extends CacheItem<K>> {
    protected final CacheLayer<K, T> next;
    protected List<K> allKeys = null;

    protected CacheLayer(CacheLayer<K, T> next) {
        this.next = next;
    }

    protected CacheLayer() {
        next = null;
    }

    public TreeMap<Long, T> allEntitiesMapByTokenSortedASC() {
        TreeMap<Long, T> r = new TreeMap<>();
        final List<T> ts = allEntities();
        for (T t : ts) {
            r.put(t.getToken(), t);
        }
        return r;
    }

    public List<T> allEntitiesNotDeleted() {
        List<T> ris = new LinkedList<>();
        final List<T> ts = allEntities();
        for (T t : ts) {
            if (!t.isFlagDelete()) {
                ris.add(t);
            }
        }
        return ris;
    }

    /**
     * restituisce tutti gli oggetti con token superiore a quello specificato
     *
     * @param minToken
     * @return
     */
    public List<T> allEntities(long minToken) {
        List<T> ris = new LinkedList<>();
        final List<T> ts = allEntities();
        for (T t : ts) {
            if (t.getToken() > minToken) {
                ris.add(t);
            }
        }
        return ris;
    }

    public List<T> allEntitiesDeleted() {
        List<T> ris = new LinkedList<>();
        final List<T> ts = allEntities();
        for (T t : ts) {
            if (t.isFlagDelete()) {
                ris.add(t);
            }
        }
        return ris;
    }

    public abstract void invalidateImpl();

    public final synchronized void invalidate() {
        allKeys = null;
        invalidateImpl();
        final CacheLayer<K, T> x = nextLayer();
        if (x != null) x.invalidate();
    }

    public String toStat() {
        StringBuilder sb = new StringBuilder();

        sb.append("----------------------------\n");
        sb.append("\n > > > > CACHE LAYER ").append(getClass().getSimpleName()).append("\n");
        sb.append("\n");
        sb.append("Keys in memory: ").append((allKeys == null) ? "No keys (NULL) " : allKeys.size() + " " + " keys");
        sb.append(_toStatImpl());
        if (next != null) {
            sb.append("\n");
            sb.append(next.toStat());
        }
        return sb.toString();
    }

    protected abstract String _toStatImpl();


    public final List<T> allEntities() {
        final List<K> ks = allKeys();
        List<T> ris = new ArrayList<>(ks.size());
        for (K k : ks) {
            ris.add(get(k));
        }
        return ris;
    }

    public final Map<K, T> allEntitiesMapByKey() {
        final List<K> ks = allKeys();
        Map<K, T> ris = new TreeMap<>();
        for (K k : ks) {
            ris.put(k, get(k));
        }
        return ris;
    }

    public final synchronized int size() {
        return _sizeImpl();
    }

    protected abstract int _sizeImpl();

    /**
     * restituisce il layer successivo o null
     *
     * @return
     */
    protected final synchronized CacheLayer<K, T> nextLayer() {
        return next;
    }

    /**
     * aggiorna tutti i campi tranne quello chiave
     *
     * @param value
     */
    public final synchronized void update(T value) {
        allKeys = null;
        final K key = getKey(value);
        _updateImpl(key, value);
        final CacheLayer<K, T> x = nextLayer();
        if (x != null) x.update(value);

    }

    public final synchronized T get(K k) {
        final T val = _getImpl(k);
        if (val != null)
            return val;
        final CacheLayer<K, T> x = nextLayer();
        final T ris = x == null ? null : x.get(k);

        //propaga
        if (ris != null)
            insert(ris);
        return ris;
    }

    protected abstract T _getImpl(K key);

    public final synchronized void insert(T value) {
        allKeys = null;
        final K key = getKey(value);
        _insertImpl(key, value);
        final CacheLayer<K, T> x = nextLayer();
        if (x != null) x.insert(value);

    }

    protected abstract List<K> _allKeys();

    public synchronized final List<K> allKeys() {
        if (allKeys == null) {
            allKeys = _allKeys();
            if (allKeys == null) {
                final CacheLayer<K, T> x = nextLayer();
                if (x != null)
                    allKeys = new ArrayList<>(x.allKeys());
            }
        }

        return new ArrayList<>(allKeys);
    }

    protected abstract void _insertImpl(K key, T value);


    protected abstract void _updateImpl(K key, T value);

    protected abstract void _removeByKeyImpl(K key);

    public final synchronized void removeByValue(T value) {
        allKeys = null;
        _removeByKeyImpl(getKey(value));
        final CacheLayer<K, T> x = nextLayer();
        if (x != null) x.removeByValue(value);


    }

    public final synchronized void removeByKey(K k) {
        allKeys = null;
        _removeByKeyImpl(k);
        final CacheLayer<K, T> x = nextLayer();
        if (x != null) x.removeByKey(k);


    }

    /**
     * deve essere univoca tra tutti gli oggetti (perche' memcache e' unica)
     *
     * @param value
     * @return
     */
    public abstract K getKey(T value);

}

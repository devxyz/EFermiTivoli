package it.gov.fermitivoli.server.datalayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * definisce un livello di gestione della cache
 */
public abstract class CacheLayer<K, T> {
    protected final CacheLayer<K, T> next;
    protected List<K> allKeys = null;

    protected CacheLayer(CacheLayer<K, T> next) {
        this.next = next;
    }

    protected CacheLayer() {
        next = null;
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

        sb.append("\nCACHE LAYER ").append(getClass().getSimpleName()).append("\n");
        sb.append("----------------------------\n");
        sb.append(" Keys in memory: " + ((allKeys == null) ? "No keys (NULL)" : allKeys.size() + " keys"));
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

    public final Map<K, T> allEntitiesMap() {
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

        _removeByKeyImpl(getKey(value));
        final CacheLayer<K, T> x = nextLayer();
        if (x != null) x.removeByValue(value);

        allKeys = null;
    }

    public final synchronized void removeByKey(K k) {
        _removeByKeyImpl(k);
        final CacheLayer<K, T> x = nextLayer();
        if (x != null) x.removeByKey(k);
        allKeys = null;

    }

    /**
     * deve essere univoca tra tutti gli oggetti (perche' memcache e' unica)
     *
     * @param value
     * @return
     */
    public abstract K getKey(T value);

}

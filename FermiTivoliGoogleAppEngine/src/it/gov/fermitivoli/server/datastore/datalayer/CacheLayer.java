package it.gov.fermitivoli.server.datastore.datalayer;

import java.util.ArrayList;
import java.util.List;

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

    public final void invalidate() {
        allKeys = null;
        invalidateImpl();
        final CacheLayer<K, T> x = nextLayer();
        if (x != null) x.invalidate();
    }

    /**
     * restituisce il layer successivo o null
     *
     * @return
     */
    protected final CacheLayer<K, T> nextLayer() {
        return next;
    }

    /**
     * aggiorna tutti i campi tranne quello chiave
     *
     * @param value
     */
    public final void update(T value) {
        allKeys = null;
        final K key = getKey(value);
        _updateImpl(key, value);
        final CacheLayer<K, T> x = nextLayer();
        if (x != null) x.update(value);

    }

    public final T get(K k) {
        final T val = _getImpl(k);
        if (val != null)
            return val;
        final CacheLayer<K, T> x = nextLayer();
        final T ris = x == null ? null : x.get(k);

        //propaga
        if (ris != null)
            set(ris);
        return ris;
    }

    protected abstract T _getImpl(K key);

    public final void set(T value) {
        final K key = getKey(value);
        _setImpl(key, value);
        final CacheLayer<K, T> x = nextLayer();
        if (x != null) x.set(value);

    }

    protected abstract List<K> _allKeys();

    public List<K> allKeys() {
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

    protected abstract void _setImpl(K key, T value);


    protected abstract void _updateImpl(K key, T value);

    protected abstract void _removeByKeyImpl(K key);

    public final void removeByValue(T value) {

        _removeByKeyImpl(getKey(value));
        final CacheLayer<K, T> x = nextLayer();
        if (x != null) x.removeByValue(value);

        allKeys = null;
    }

    public final void removeByKey(K k) {
        _removeByKeyImpl(k);
        final CacheLayer<K, T> x = nextLayer();
        if (x != null) x.removeByKey(k);
        allKeys = null;

    }


    public abstract K getKey(T value);

}

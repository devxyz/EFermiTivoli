package it.gov.fermitivoli.server.datastore.datalayer;

import com.google.appengine.api.memcache.MemcacheService;

/**
 * Created by stefano on 03/03/16.
 */
public abstract class MemcacheCacheLayer<K, T> extends CacheLayer<K, T> {
    private final MemcacheService mcservice;


    protected MemcacheCacheLayer(MemcacheService mcservice, CacheLayer<K, T> next) {
        super(next);
        this.mcservice = mcservice;
    }


    @Override
    protected T _getImpl(K key) {
        final T o = (T) mcservice.get(key);
        return o;
    }

    @Override
    protected void _setImpl(K key, T value) {
        mcservice.put(key, value);
    }

    @Override
    protected void _removeByKeyImpl(K key) {
        mcservice.delete(key);
    }

    @Override
    public void invalidateImpl() {
        mcservice.clearAll();
    }
}

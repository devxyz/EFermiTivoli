package it.gov.fermitivoli.server.datalayer;

import com.google.appengine.api.memcache.MemcacheService;

import java.util.List;

/**
 * Created by stefano on 03/03/16.
 */
public abstract class MemcacheCacheLayer<K, T> extends CacheLayer<K, T> {
    protected final MemcacheService mcservice;


    protected MemcacheCacheLayer(MemcacheService mcservice, CacheLayer<K, T> next) {
        super(next);
        this.mcservice = mcservice;
    }

    @Override
    protected int _sizeImpl() {
        return -1;
    }

    @Override
    protected String _toStatImpl() {
        return "stat: " + mcservice.getStatistics();
    }

    @Override
    protected T _getImpl(K key) {
        final T o = (T) mcservice.get(key);
        return o;
    }

    @Override
    protected void _updateImpl(K key, T value) {
        _insertImpl(key, value);
    }

    @Override
    protected List<K> _allKeys() {
        return nextLayer().allKeys();
    }

    @Override
    protected void _insertImpl(K key, T value) {
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

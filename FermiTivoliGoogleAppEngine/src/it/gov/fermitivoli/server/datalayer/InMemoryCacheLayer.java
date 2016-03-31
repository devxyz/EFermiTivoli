package it.gov.fermitivoli.server.datalayer;

import java.util.HashMap;
import java.util.List;

/**
 * Created by stefano on 03/03/16.
 */
public abstract class InMemoryCacheLayer<K, T extends CacheItem<K>> extends CacheLayer<K, T > {
    private final HashMap<K, T> data = new HashMap<>();

    public InMemoryCacheLayer(CacheLayer<K, T> next) {
        super(next);
    }

    @Override
    protected String _toStatImpl() {
        return "" + data.size() + " entities in RAM";
    }

    @Override
    protected int _sizeImpl() {
        return data.size();
    }

    @Override
    protected T _getImpl(K key) {
        return data.get(key);
    }

    @Override
    protected void _insertImpl(K key, T value) {
        data.put(key, value);
    }

    @Override
    protected List<K> _allKeys() {
        return null;
    }

    @Override
    protected void _updateImpl(K key, T value) {
        data.put(key, value);
    }

    @Override
    protected void _removeByKeyImpl(K key) {
        data.remove(key);
    }

    @Override
    public void invalidateImpl() {
        data.clear();
    }
}

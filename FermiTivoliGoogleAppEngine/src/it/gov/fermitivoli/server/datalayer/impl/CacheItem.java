package it.gov.fermitivoli.server.datalayer.impl;

/**
 * Created by stefano on 16/03/16.
 */
public interface CacheItem<K> {
    public K getKey();

    public long getToken();

    public boolean isFlagDelete();
}

package it.gov.fermitivoli.server.datastore;

import com.google.appengine.api.memcache.MemcacheService;

/**
 * Created by stefano on 09/08/15.
 */
@Deprecated
public class MemcacheEntry<T> {
    private final String label;
    private MemcacheService mcservice;
    private Class<T> clazz;

    public MemcacheEntry(MemcacheService mcservice, String label, Class<T> clazz) {
        this.mcservice = mcservice;
        this.label = label;
        this.clazz = clazz;
    }

    public void put(T value) {
        mcservice.put(label, value);
    }

    public T get() {
        return (T) mcservice.get(label);
    }

    public long increment(long inc, long defaultValue) {
        if (!(clazz.equals(Long.class) || clazz.equals(long.class)))
            throw new IllegalArgumentException("Invalid class type: " + clazz + ". Must be long!");
        return mcservice.increment(label, inc, defaultValue);

    }
}

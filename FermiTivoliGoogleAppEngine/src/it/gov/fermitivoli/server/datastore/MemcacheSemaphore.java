package it.gov.fermitivoli.server.datastore;

import com.google.appengine.api.memcache.MemcacheService;

import java.util.concurrent.TimeoutException;

/**
 * Created by stefano on 06/08/15.
 */
public class MemcacheSemaphore {
    private static final boolean DEBUG = false;
    /**
     * ritorna true se il semaforo risulta acquisito dal thread corrente (evita l'acquisizione multipla)
     */
    private static final ThreadLocal<Boolean> acquired = new ThreadLocal<>();
    private final String syncKey;
    private long numResources;
    private MemcacheService memcache;

    /**
     * crea un semaforo con il numero di attese pari ad 1
     *
     * @param memcache
     * @param syncKey
     */
    public MemcacheSemaphore(MemcacheService memcache, String syncKey) {
        this(memcache, syncKey, 1);
    }

    /**
     * crea un semaforo con il numero di attese specificato
     *
     * @param memcache
     * @param syncKey
     * @param maxValue
     */
    public MemcacheSemaphore(MemcacheService memcache, String syncKey, long maxValue) {
        this.memcache = memcache;
        this.syncKey = syncKey;
        this.numResources = maxValue;
    }

    public void releaseLock() {
        if (acquired.get() != null) {
            acquired.set(null);
        } else
            throw new IllegalArgumentException("Semaforo non acquisito!");

        //decrementa
        {
            final Long increment = memcache.increment(syncKey, -1, 0L);
            if (DEBUG)
                System.out.println("SEMAFORO RELEASED: " + increment + " " + callerInfo());
        }
    }

    public void acquireLock_unchecked(long maxwait) {
        try {
            acquireLock(maxwait);
        } catch (TimeoutException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private String callerInfo() {
        Exception e = new IllegalArgumentException();
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (StackTraceElement st : e.getStackTrace()) {

            if (count > 2 && count < 6)
                sb.append(st.toString() + " ####### ");
            count++;

        }
        return sb.toString();
    }

    public void acquireLock(long maxwait) throws TimeoutException {

        if (acquired.get() != null)
            return;//semaforo gia' acquisito
        else
            acquired.set(true);

        long start = System.currentTimeMillis();
        while (true) {
            final Long increment = memcache.increment(syncKey, 1L, 0L);
            if (DEBUG)
                System.out.println("SEMAFORO: " + increment + " - n.tot: " + numResources + " " + callerInfo());
            if (increment <= numResources) {
                if (DEBUG)
                    System.out.println("SEMAFORO ACQUISITO: " + increment + " - n.tot: " + numResources + " " + callerInfo());
                return;
            }

            memcache.increment(syncKey, -1L, 0L);
            if (System.currentTimeMillis() - start > maxwait) {
                throw new TimeoutException();
            }
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
            }
        }
    }
}

package it.gov.fermitivoli.server.datalayer;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import it.gov.fermitivoli.server.datalayer.impl.circolari.InMemoryCacheLayerCircolareDB;
import it.gov.fermitivoli.server.datalayer.impl.circolari.MemcacheCacheLayerCircolareDB;
import it.gov.fermitivoli.server.datalayer.impl.circolari.OfyPersistanceLayerCircolareDB;
import it.gov.fermitivoli.server.datalayer.impl.news.InMemoryCacheLayerNewsDB;
import it.gov.fermitivoli.server.datalayer.impl.news.MemcacheCacheLayerNewsDB;
import it.gov.fermitivoli.server.datalayer.impl.news.OfyPersistanceLayerNewsDB;
import it.gov.fermitivoli.server.model.GAE_CircolareDB_V2;
import it.gov.fermitivoli.server.model.GAE_NewsDB_V2;

import java.util.List;

/**
 * Created by stefano on 13/03/16.
 */
public class DataLayerBuilder {
    private static InMemoryCacheLayerCircolareDB loaderCircolari;
    private static InMemoryCacheLayerNewsDB loaderNews;

    private static MemcacheService memcache;
    private static Objectify ofy;

    public static InMemoryCacheLayerCircolareDB getLoaderCircolari() {
        if (loaderCircolari == null) {
            init();
            loaderCircolari = new InMemoryCacheLayerCircolareDB(new MemcacheCacheLayerCircolareDB(memcache, new OfyPersistanceLayerCircolareDB(ofy)));
        }
        return loaderCircolari;
    }

    public static InMemoryCacheLayerNewsDB getLoaderNews() {
        if (loaderNews == null) {
            init();
            loaderNews = new InMemoryCacheLayerNewsDB(new MemcacheCacheLayerNewsDB(memcache, new OfyPersistanceLayerNewsDB(ofy)));
        }
        return loaderNews;
    }

    private static void init() {
        if (memcache == null)
            memcache = MemcacheServiceFactory.getMemcacheService();
        if (ofy == null)
            ofy = ObjectifyService.ofy();
    }

    public static long maxToken() {
        long max = 0;
        final List<GAE_CircolareDB_V2> gae_circolareDB_v2s = getLoaderCircolari().allEntities();
        for (GAE_CircolareDB_V2 x : gae_circolareDB_v2s) {
            max = Math.max(max, x.getToken());
        }

        final List<GAE_NewsDB_V2> gae_newsDB_v2s = getLoaderNews().allEntities();
        for (GAE_NewsDB_V2 x : gae_newsDB_v2s) {
            max = Math.max(max, x.getToken());
        }
        return max;
    }
}

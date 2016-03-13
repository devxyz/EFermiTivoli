package it.gov.fermitivoli.server.datalayer.impl.circolari;

import com.google.appengine.api.memcache.MemcacheService;
import it.gov.fermitivoli.server.datalayer.MemcacheCacheLayer;
import it.gov.fermitivoli.server.model.GAE_CircolareDB_V2;

/**
 * Created by stefano on 13/03/16.
 */
public class MemcacheCacheLayerCircolareDB extends MemcacheCacheLayer<String, GAE_CircolareDB_V2> {


    public MemcacheCacheLayerCircolareDB(MemcacheService mcservice, OfyPersistanceLayerCircolareDB next) {
        super(mcservice, next);
    }

    @Override
    public String getKey(GAE_CircolareDB_V2 value) {
        return value.getKey();
    }
}

package it.gov.fermitivoli.server.datalayer.impl.news;

import com.google.appengine.api.memcache.MemcacheService;
import it.gov.fermitivoli.server.datalayer.MemcacheCacheLayer;
import it.gov.fermitivoli.server.model.GAE_NewsDB_V2;

/**
 * Created by stefano on 13/03/16.
 */
public class MemcacheCacheLayerNewsDB extends MemcacheCacheLayer<String, GAE_NewsDB_V2> {
    public MemcacheCacheLayerNewsDB(MemcacheService mcservice, OfyPersistanceLayerNewsDB next) {
        super(mcservice, next);
    }

    @Override
    public String getKey(GAE_NewsDB_V2 value) {
        return value.getKeyTitlePubDate();
    }
}

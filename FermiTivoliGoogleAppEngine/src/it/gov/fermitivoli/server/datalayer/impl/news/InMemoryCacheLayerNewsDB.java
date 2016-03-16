package it.gov.fermitivoli.server.datalayer.impl.news;

import it.gov.fermitivoli.server.datalayer.InMemoryCacheLayer;
import it.gov.fermitivoli.server.model.GAE_NewsDB_V2;

/**
 * Created by stefano on 13/03/16.
 */
public class InMemoryCacheLayerNewsDB extends InMemoryCacheLayer<String, GAE_NewsDB_V2> {
    public InMemoryCacheLayerNewsDB(MemcacheCacheLayerNewsDB next) {
        super(next);
    }

    @Override
    public String getKey(GAE_NewsDB_V2 value) {
        return value.getKey();
    }
}

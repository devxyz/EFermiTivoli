package it.gov.fermitivoli.server.datalayer.impl.circolari;

import it.gov.fermitivoli.server.datalayer.InMemoryCacheLayer;
import it.gov.fermitivoli.server.model.GAE_CircolareDB_V2;

/**
 * Created by stefano on 13/03/16.
 */
public class InMemoryCacheLayerCircolareDB extends InMemoryCacheLayer<String, GAE_CircolareDB_V2> {
    public InMemoryCacheLayerCircolareDB(MemcacheCacheLayerCircolareDB next) {
        super(next);
    }



    @Override
    public String getKey(GAE_CircolareDB_V2 value) {
        return value.getKey();
    }
}

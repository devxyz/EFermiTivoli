package it.gov.fermitivoli.server.datalayer;

import com.googlecode.objectify.Objectify;
import it.gov.fermitivoli.server.datalayer.impl.CacheItem;

/**
 * Created by stefano on 03/03/16.
 */
public abstract class OfyPersistanceLayer<K, T extends CacheItem<K>> extends CacheLayer<K, T> {
    protected final Objectify ofy;


    protected OfyPersistanceLayer(Objectify ofy) {
        super();

        this.ofy = ofy;
    }

    @Override
    protected int _sizeImpl() {
        return -1;
    }


}

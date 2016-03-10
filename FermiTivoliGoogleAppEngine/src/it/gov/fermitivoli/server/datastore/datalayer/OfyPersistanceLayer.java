package it.gov.fermitivoli.server.datastore.datalayer;

import com.googlecode.objectify.Objectify;

/**
 * Created by stefano on 03/03/16.
 */
public abstract class OfyPersistanceLayer<K, T> extends CacheLayer<K, T> {
    private final Objectify ofy;


    protected OfyPersistanceLayer(Objectify ofy) {
        super();

        this.ofy = ofy;
    }


}

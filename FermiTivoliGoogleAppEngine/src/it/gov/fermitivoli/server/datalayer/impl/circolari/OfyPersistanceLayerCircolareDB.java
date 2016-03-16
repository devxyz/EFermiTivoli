package it.gov.fermitivoli.server.datalayer.impl.circolari;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.cmd.Loader;
import it.gov.fermitivoli.server.datalayer.OfyPersistanceLayer;
import it.gov.fermitivoli.server.model.GAE_CircolareDB_V2;
import it.gov.fermitivoli.server.util.DebugUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stefano on 13/03/16.
 */
public class OfyPersistanceLayerCircolareDB extends OfyPersistanceLayer<String, GAE_CircolareDB_V2> {
    //ultimo elenco dei dati
    private List<GAE_CircolareDB_V2> cacheList;
    private int numRead = 0;
    private int numByKey = 0;
    private int numWrite = 0;

    public OfyPersistanceLayerCircolareDB(Objectify ofy) {
        super(ofy);
    }

    @Override
    public void invalidateImpl() {
        cacheList = null;
    }

    @Override
    protected String _toStatImpl() {
        return "Entities in cache: " + (cacheList == null ? "NULL CACHE" : cacheList.size() + " entities") + "\nnumRead=" + numRead + ", numWrite=" + numWrite + ", numByKey=" + numByKey;
    }

    @Override
    protected GAE_CircolareDB_V2 _getImpl(String key) {
        if (cacheList != null) {
            for (GAE_CircolareDB_V2 x : cacheList) {
                if (x.getKey().equals(key))
                    return x;
            }
            cacheList = null;
        }

        final Loader load = ofy.load();
        DebugUtil.debug(getClass().getSimpleName(), "GET BY ID ", key);
        final Key<GAE_CircolareDB_V2> k = Key.create(GAE_CircolareDB_V2.class, key);
        numByKey++;
        return load.key(k).now();
    }

    @Override
    protected List<String> _allKeys() {
        if (cacheList == null) {
            final Loader load = ofy.load();
            cacheList = load.type(GAE_CircolareDB_V2.class).list();
            numRead++;
        }

        DebugUtil.debug(getClass().getSimpleName(), "ALLKEYS");
        List<String> ris = new ArrayList<>(cacheList.size());
        for (GAE_CircolareDB_V2 x : cacheList) {
            ris.add(x.getKey());
        }
        return ris;


    }

    @Override
    protected void _insertImpl(String key, GAE_CircolareDB_V2 value) {
        DebugUtil.debug(getClass().getSimpleName(), "SAVE BY ID ", key);
        ofy.save().entity(value).now();
        numWrite++;
        cacheList = null;
    }

    @Override
    protected void _updateImpl(String key, GAE_CircolareDB_V2 value) {
        DebugUtil.debug(getClass().getSimpleName(), "UPDATE BY ID ", key);
        ofy.save().entity(value).now();
        numWrite++;
        cacheList = null;
    }

    @Override
    protected void _removeByKeyImpl(String key) {
        DebugUtil.debug(getClass().getSimpleName(), "REMOVE BY ID ", key);
        ofy.delete().entity(get(key)).now();
        numWrite++;
        cacheList = null;
    }

    @Override
    public String getKey(GAE_CircolareDB_V2 value) {
        return value.getKey();
    }

}

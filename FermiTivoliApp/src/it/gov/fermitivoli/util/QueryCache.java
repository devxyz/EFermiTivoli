package it.gov.fermitivoli.util;

import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;
import it.gov.fermitivoli.dao.DaoSession;

/**
 * Created by stefano on 20/06/15.
 */
public abstract class QueryCache<T> {
    private Query<T> query = null;
    private DeleteQuery<T> delete = null;

    protected abstract QueryBuilder<T> queryBuilder(DaoSession session);

    protected abstract void setParameters(DaoSession session, Query<T> q, Object... parametri);
    protected abstract void setParameters(DaoSession session, DeleteQuery<T> q, Object... parametri);

    public final Query<T> getQuery(DaoSession session, Object... parametri) {
        if (query == null)
            query = queryBuilder(session).build();
        setParameters(session, query, parametri);
        return query;
    }

    public final DeleteQuery<T> delete(DaoSession session, Object... parametri) {
        if (delete == null)
            delete = queryBuilder(session).buildDelete();
        setParameters(session, delete, parametri);
        return delete;
    }
}

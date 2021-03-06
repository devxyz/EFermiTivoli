package it.gov.fermitivoli.dao;

import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.SqlUtils;
import de.greenrobot.dao.internal.DaoConfig;

import it.gov.fermitivoli.dao.NewsContieneTermineDB;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table NEWS_CONTIENE_TERMINE_DB.
*/
public class NewsContieneTermineDBDao extends AbstractDao<NewsContieneTermineDB, Long> {

    public static final String TABLENAME = "NEWS_CONTIENE_TERMINE_DB";

    /**
     * Properties of entity NewsContieneTermineDB.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property _id_news = new Property(0, long.class, "_id_news", false, "_ID_NEWS");
        public final static Property _id_termine = new Property(1, long.class, "_id_termine", false, "_ID_TERMINE");
        public final static Property Id = new Property(2, Long.class, "id", true, "_id");
        public final static Property Occorrenze = new Property(3, long.class, "occorrenze", false, "OCCORRENZE");
    };

    private DaoSession daoSession;


    public NewsContieneTermineDBDao(DaoConfig config) {
        super(config);
    }
    
    public NewsContieneTermineDBDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'NEWS_CONTIENE_TERMINE_DB' (" + //
                "'_ID_NEWS' INTEGER NOT NULL ," + // 0: _id_news
                "'_ID_TERMINE' INTEGER NOT NULL ," + // 1: _id_termine
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 2: id
                "'OCCORRENZE' INTEGER NOT NULL );"); // 3: occorrenze
        // Add Indexes
        db.execSQL("CREATE UNIQUE INDEX " + constraint + "IDX_NEWS_CONTIENE_TERMINE_DB__ID_NEWS__ID_TERMINE ON NEWS_CONTIENE_TERMINE_DB" +
                " (_ID_NEWS,_ID_TERMINE);");
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'NEWS_CONTIENE_TERMINE_DB'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, NewsContieneTermineDB entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.get_id_news());
        stmt.bindLong(2, entity.get_id_termine());
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(3, id);
        }
        stmt.bindLong(4, entity.getOccorrenze());
    }

    @Override
    protected void attachEntity(NewsContieneTermineDB entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2);
    }    

    /** @inheritdoc */
    @Override
    public NewsContieneTermineDB readEntity(Cursor cursor, int offset) {
        NewsContieneTermineDB entity = new NewsContieneTermineDB( //
            cursor.getLong(offset + 0), // _id_news
            cursor.getLong(offset + 1), // _id_termine
            cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2), // id
            cursor.getLong(offset + 3) // occorrenze
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, NewsContieneTermineDB entity, int offset) {
        entity.set_id_news(cursor.getLong(offset + 0));
        entity.set_id_termine(cursor.getLong(offset + 1));
        entity.setId(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
        entity.setOccorrenze(cursor.getLong(offset + 3));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(NewsContieneTermineDB entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(NewsContieneTermineDB entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getNewsDBDao().getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T1", daoSession.getTermineDBDao().getAllColumns());
            builder.append(" FROM NEWS_CONTIENE_TERMINE_DB T");
            builder.append(" LEFT JOIN NEWS_DB T0 ON T.'_ID_NEWS'=T0.'_id'");
            builder.append(" LEFT JOIN TERMINE_DB T1 ON T.'_ID_TERMINE'=T1.'_id'");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected NewsContieneTermineDB loadCurrentDeep(Cursor cursor, boolean lock) {
        NewsContieneTermineDB entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        NewsDB newsDB = loadCurrentOther(daoSession.getNewsDBDao(), cursor, offset);
         if(newsDB != null) {
            entity.setNewsDB(newsDB);
        }
        offset += daoSession.getNewsDBDao().getAllColumns().length;

        TermineDB termineDB = loadCurrentOther(daoSession.getTermineDBDao(), cursor, offset);
         if(termineDB != null) {
            entity.setTermineDB(termineDB);
        }

        return entity;    
    }

    public NewsContieneTermineDB loadDeep(Long key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(getSelectDeep());
        builder.append("WHERE ");
        SqlUtils.appendColumnsEqValue(builder, "T", getPkColumns());
        String sql = builder.toString();
        
        String[] keyArray = new String[] { key.toString() };
        Cursor cursor = db.rawQuery(sql, keyArray);
        
        try {
            boolean available = cursor.moveToFirst();
            if (!available) {
                return null;
            } else if (!cursor.isLast()) {
                throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
            }
            return loadCurrentDeep(cursor, true);
        } finally {
            cursor.close();
        }
    }
    
    /** Reads all available rows from the given cursor and returns a list of new ImageTO objects. */
    public List<NewsContieneTermineDB> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<NewsContieneTermineDB> list = new ArrayList<NewsContieneTermineDB>(count);
        
        if (cursor.moveToFirst()) {
            if (identityScope != null) {
                identityScope.lock();
                identityScope.reserveRoom(count);
            }
            try {
                do {
                    list.add(loadCurrentDeep(cursor, false));
                } while (cursor.moveToNext());
            } finally {
                if (identityScope != null) {
                    identityScope.unlock();
                }
            }
        }
        return list;
    }
    
    protected List<NewsContieneTermineDB> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<NewsContieneTermineDB> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}

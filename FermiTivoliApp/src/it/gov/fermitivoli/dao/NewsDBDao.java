package it.gov.fermitivoli.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import it.gov.fermitivoli.dao.NewsDB;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table NEWS_DB.
*/
public class NewsDBDao extends AbstractDao<NewsDB, Long> {

    public static final String TABLENAME = "NEWS_DB";

    /**
     * Properties of entity NewsDB.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Titolo = new Property(1, String.class, "titolo", false, "TITOLO");
        public final static Property Link = new Property(2, String.class, "link", false, "LINK");
        public final static Property PubDate = new Property(3, java.util.Date.class, "pubDate", false, "PUB_DATE");
        public final static Property Testo = new Property(4, String.class, "testo", false, "TESTO");
        public final static Property Contenuto = new Property(5, String.class, "contenuto", false, "CONTENUTO");
        public final static Property FullimageLink = new Property(6, String.class, "fullimageLink", false, "FULLIMAGE_LINK");
        public final static Property ThumbimageLink = new Property(7, String.class, "thumbimageLink", false, "THUMBIMAGE_LINK");
        public final static Property Key = new Property(8, String.class, "key", false, "KEY");
        public final static Property FlagContenutoLetto = new Property(9, boolean.class, "flagContenutoLetto", false, "FLAG_CONTENUTO_LETTO");
        public final static Property DataInserimento = new Property(10, java.util.Date.class, "dataInserimento", false, "DATA_INSERIMENTO");
        public final static Property Token = new Property(11, long.class, "token", false, "TOKEN");
    };


    public NewsDBDao(DaoConfig config) {
        super(config);
    }
    
    public NewsDBDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'NEWS_DB' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'TITOLO' TEXT NOT NULL ," + // 1: titolo
                "'LINK' TEXT NOT NULL ," + // 2: link
                "'PUB_DATE' INTEGER NOT NULL ," + // 3: pubDate
                "'TESTO' TEXT NOT NULL ," + // 4: testo
                "'CONTENUTO' TEXT," + // 5: contenuto
                "'FULLIMAGE_LINK' TEXT," + // 6: fullimageLink
                "'THUMBIMAGE_LINK' TEXT," + // 7: thumbimageLink
                "'KEY' TEXT NOT NULL ," + // 8: key
                "'FLAG_CONTENUTO_LETTO' INTEGER NOT NULL ," + // 9: flagContenutoLetto
                "'DATA_INSERIMENTO' INTEGER NOT NULL ," + // 10: dataInserimento
                "'TOKEN' INTEGER NOT NULL );"); // 11: token
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'NEWS_DB'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, NewsDB entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getTitolo());
        stmt.bindString(3, entity.getLink());
        stmt.bindLong(4, entity.getPubDate().getTime());
        stmt.bindString(5, entity.getTesto());
 
        String contenuto = entity.getContenuto();
        if (contenuto != null) {
            stmt.bindString(6, contenuto);
        }
 
        String fullimageLink = entity.getFullimageLink();
        if (fullimageLink != null) {
            stmt.bindString(7, fullimageLink);
        }
 
        String thumbimageLink = entity.getThumbimageLink();
        if (thumbimageLink != null) {
            stmt.bindString(8, thumbimageLink);
        }
        stmt.bindString(9, entity.getKey());
        stmt.bindLong(10, entity.getFlagContenutoLetto() ? 1l: 0l);
        stmt.bindLong(11, entity.getDataInserimento().getTime());
        stmt.bindLong(12, entity.getToken());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public NewsDB readEntity(Cursor cursor, int offset) {
        NewsDB entity = new NewsDB( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // titolo
            cursor.getString(offset + 2), // link
            new java.util.Date(cursor.getLong(offset + 3)), // pubDate
            cursor.getString(offset + 4), // testo
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // contenuto
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // fullimageLink
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // thumbimageLink
            cursor.getString(offset + 8), // key
            cursor.getShort(offset + 9) != 0, // flagContenutoLetto
            new java.util.Date(cursor.getLong(offset + 10)), // dataInserimento
            cursor.getLong(offset + 11) // token
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, NewsDB entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setTitolo(cursor.getString(offset + 1));
        entity.setLink(cursor.getString(offset + 2));
        entity.setPubDate(new java.util.Date(cursor.getLong(offset + 3)));
        entity.setTesto(cursor.getString(offset + 4));
        entity.setContenuto(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setFullimageLink(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setThumbimageLink(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setKey(cursor.getString(offset + 8));
        entity.setFlagContenutoLetto(cursor.getShort(offset + 9) != 0);
        entity.setDataInserimento(new java.util.Date(cursor.getLong(offset + 10)));
        entity.setToken(cursor.getLong(offset + 11));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(NewsDB entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(NewsDB entity) {
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
    
}

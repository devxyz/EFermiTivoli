package it.gov.fermitivoli.backoffice.utilities.greendao;

import de.greenrobot.daogenerator.*;

/**
 * Created by stefano on 27/04/15.
 */
public class GreenDaoGenerator {
    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(82, "it.gov.fermitivoli.dao");

        //cache file
        final Entity cacheFile = schema.addEntity("CacheFileDB");
        {
            cacheFile.addIdProperty().autoincrement();
            cacheFile.addStringProperty("url").notNull().unique().index();
            cacheFile.addStringProperty("filename").notNull();
            cacheFile.addDateProperty("insertion_date").notNull();
            cacheFile.addBooleanProperty("load_complete").notNull();
        }


        //circolare
        final Entity circolare = schema.addEntity("CircolareDB");
        {
            circolare.addIdProperty().autoincrement();
            circolare.addDateProperty("dataInserimento").notNull();


            final Property data = circolare.addDateProperty("data").notNull().getProperty();
            final Property numero = circolare.addIntProperty("numero").notNull().getProperty();
            circolare.addStringProperty("titolo").notNull();
            circolare.addStringProperty("testo");
            circolare.addLongProperty("token");
            circolare.addStringProperty("url").notNull().unique();
            circolare.addBooleanProperty("flagContenutoLetto").notNull();

            circolare.addStringProperty("key").notNull().unique();

            //index
            Index indexCircolare = new Index();
            indexCircolare.makeUnique();
            indexCircolare.addProperty(data);
            indexCircolare.addProperty(numero);
            circolare.addIndex(indexCircolare);
        }


        //termine
        final Entity termine = schema.addEntity("TermineDB");
        {
            termine.addIdProperty().autoincrement();
            termine.addStringProperty("termine").notNull().unique().index();
            termine.addStringProperty("radice").notNull();
        }



        //termine
        final Entity news = schema.addEntity("NewsDB");
        {
            news.addIdProperty().autoincrement();

            news.addStringProperty("titolo").notNull();
            news.addStringProperty("link").notNull();
            news.addDateProperty("pubDate").notNull();
            news.addStringProperty("testo").notNull();
            news.addStringProperty("contenuto");
            news.addStringProperty("fullimageLink");
            news.addStringProperty("thumbimageLink");
            news.addBooleanProperty("flagContenutoLetto").notNull();
            news.addDateProperty("dataInserimento").notNull();
        }


        //tabella intermedia

        {
            final Entity circolareTermine = schema.addEntity("CircolareContieneTermineDB");
            Property id_circolare = circolareTermine.addLongProperty("_id_circolare").notNull().getProperty();
            Property id_termine = circolareTermine.addLongProperty("_id_termine").notNull().getProperty();
            circolareTermine.addIdProperty().autoincrement();
            circolareTermine.addLongProperty("occorrenze").notNull();
            circolareTermine.addToOne(circolare, id_circolare);
            circolareTermine.addToOne(termine, id_termine);

            //index
            Index indexCircolareTermine = new Index();
            indexCircolareTermine.makeUnique();
            indexCircolareTermine.addProperty(id_circolare);
            indexCircolareTermine.addProperty(id_termine);
            circolareTermine.addIndex(indexCircolareTermine);
        }

        //tabella intermedia
        {
            final Entity newsContieneTermine = schema.addEntity("NewsContieneTermineDB");
            Property id_news = newsContieneTermine.addLongProperty("_id_news").notNull().getProperty();
            Property id_termine = newsContieneTermine.addLongProperty("_id_termine").notNull().getProperty();
            newsContieneTermine.addIdProperty().autoincrement();
            newsContieneTermine.addLongProperty("occorrenze").notNull();
            newsContieneTermine.addToOne(news, id_news);
            newsContieneTermine.addToOne(termine, id_termine);

            //index
            Index indexNewsTermine = new Index();
            indexNewsTermine.makeUnique();
            indexNewsTermine.addProperty(id_news);
            indexNewsTermine.addProperty(id_termine);
            newsContieneTermine.addIndex(indexNewsTermine);
        }



        new DaoGenerator().generateAll(schema, "/Users/stefano/DATA/scuola/insegnamento/scuola-AS-2014-15/Fermi-TIVOLI-14-15/development/EFermiTivoli/FermiTivoliApp/src");
    }
}

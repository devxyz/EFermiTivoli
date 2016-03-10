package it.gov.fermitivoli.server.model;

import com.googlecode.objectify.annotation.Id;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by stefano on 03/03/16.
 */
public class GAE_NewsDB_V2 implements Serializable, Cloneable {
    public boolean flagDelete;
    @Id
    private String titlePubDate;
    /**
     * Not-null value.
     */
    private String titolo;
    /**
     * Not-null value.
     */
    private String link;
    /**
     * Not-null value.
     */
    private java.util.Date pubDate;
    /**
     * Not-null value.
     */
    private String testo;
    private String contenuto;
    private String fullimageLink;
    private String thumbimageLink;
    private boolean flagContenutoLetto;
    /**
     * Not-null value.
     */
    private java.util.Date dataInserimento;

    public GAE_NewsDB_V2 clone() {
        try {
            return (GAE_NewsDB_V2) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public String getTitlePubDate() {
        return titlePubDate;
    }

    public void setTitlePubDate(String titlePubDate) {
        this.titlePubDate = titlePubDate;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }

    public String getContenuto() {
        return contenuto;
    }

    public void setContenuto(String contenuto) {
        this.contenuto = contenuto;
    }

    public String getFullimageLink() {
        return fullimageLink;
    }

    public void setFullimageLink(String fullimageLink) {
        this.fullimageLink = fullimageLink;
    }

    public String getThumbimageLink() {
        return thumbimageLink;
    }

    public void setThumbimageLink(String thumbimageLink) {
        this.thumbimageLink = thumbimageLink;
    }

    public boolean isFlagContenutoLetto() {
        return flagContenutoLetto;
    }

    public void setFlagContenutoLetto(boolean flagContenutoLetto) {
        this.flagContenutoLetto = flagContenutoLetto;
    }

    public Date getDataInserimento() {
        return dataInserimento;
    }

    public void setDataInserimento(Date dataInserimento) {
        this.dataInserimento = dataInserimento;
    }

    public boolean isFlagDelete() {
        return flagDelete;
    }

    public void setFlagDelete(boolean flagDelete) {
        this.flagDelete = flagDelete;
    }
}

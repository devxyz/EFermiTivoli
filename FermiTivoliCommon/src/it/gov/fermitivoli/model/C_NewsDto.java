package it.gov.fermitivoli.model;

import it.gov.fermitivoli.util.C_DateUtil;

import java.util.Date;

/**
 * Created by stefano on 13/03/16.
 */
public class C_NewsDto implements Comparable<C_NewsDto> {
    private Long id;
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

    public static String composeKey(String titolo, Date pubDate) {
        return "#N#" + titolo.toLowerCase().replaceAll("[ \n]+", "") + "#" + C_DateUtil.toDDMMYYY(pubDate);

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "C_NewsDto{" +
                "id=" + id +
                ", titolo='" + titolo + '\'' +
                ", link='" + link + '\'' +
                ", pubDate=" + pubDate +
                ", testo='" + testo + '\'' +
                ", contenuto='" + contenuto + '\'' +
                ", fullimageLink='" + fullimageLink + '\'' +
                ", thumbimageLink='" + thumbimageLink + '\'' +
                ", flagContenutoLetto=" + flagContenutoLetto +
                ", dataInserimento=" + dataInserimento +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        C_NewsDto c_newsDto = (C_NewsDto) o;

        if (flagContenutoLetto != c_newsDto.flagContenutoLetto) return false;
        if (id != null ? !id.equals(c_newsDto.id) : c_newsDto.id != null) return false;
        if (titolo != null ? !titolo.equals(c_newsDto.titolo) : c_newsDto.titolo != null) return false;
        if (link != null ? !link.equals(c_newsDto.link) : c_newsDto.link != null) return false;
        if (pubDate != null ? !pubDate.equals(c_newsDto.pubDate) : c_newsDto.pubDate != null) return false;
        if (testo != null ? !testo.equals(c_newsDto.testo) : c_newsDto.testo != null) return false;
        if (contenuto != null ? !contenuto.equals(c_newsDto.contenuto) : c_newsDto.contenuto != null) return false;
        if (fullimageLink != null ? !fullimageLink.equals(c_newsDto.fullimageLink) : c_newsDto.fullimageLink != null)
            return false;
        if (thumbimageLink != null ? !thumbimageLink.equals(c_newsDto.thumbimageLink) : c_newsDto.thumbimageLink != null)
            return false;
        return dataInserimento != null ? dataInserimento.equals(c_newsDto.dataInserimento) : c_newsDto.dataInserimento == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (titolo != null ? titolo.hashCode() : 0);
        result = 31 * result + (link != null ? link.hashCode() : 0);
        result = 31 * result + (pubDate != null ? pubDate.hashCode() : 0);
        result = 31 * result + (testo != null ? testo.hashCode() : 0);
        result = 31 * result + (contenuto != null ? contenuto.hashCode() : 0);
        result = 31 * result + (fullimageLink != null ? fullimageLink.hashCode() : 0);
        result = 31 * result + (thumbimageLink != null ? thumbimageLink.hashCode() : 0);
        result = 31 * result + (flagContenutoLetto ? 1 : 0);
        result = 31 * result + (dataInserimento != null ? dataInserimento.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(C_NewsDto o) {
        final int i = pubDate.compareTo(o.pubDate);
        if (i != 0) return i;
        return titolo.compareTo(o.titolo);
    }
}

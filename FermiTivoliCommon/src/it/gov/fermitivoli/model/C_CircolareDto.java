package it.gov.fermitivoli.model;

import it.gov.fermitivoli.util.C_CircolariUtil;

import java.util.Date;
import java.util.Formatter;

/**
 * Created by stefano on 05/05/15.
 */
public class C_CircolareDto implements Comparable<C_CircolareDto> {
    private String key;
    private String titolo;
    private String testo;
    private Date data;
    private C_NormalizedURL url;
    private int numero;
    private long token;

    /**
     * costruttore privato per GSON
     */
    public C_CircolareDto() {
    }

    public C_CircolareDto(String titolo, String testo, Date data, C_NormalizedURL linkpdf, int numero) {
        this(C_CircolariUtil.generateKey(numero, data), titolo, testo, data, linkpdf, numero);
    }

    public C_CircolareDto(String key, String titolo, String testo, Date data, C_NormalizedURL linkpdf, int numero) {
        this.key = key;
        this.titolo = titolo;
        this.testo = testo;
        this.data = data;
        this.url = linkpdf;
        this.numero = numero;
    }

    public long getToken() {
        return token;
    }

    public void setToken(long token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "C_CircolareDto{" +
                "key='" + key + '\'' +
                ", titolo='" + titolo + '\'' +
                ", testo='" + testo + '\'' +
                ", data=" + data +
                ", url=" + url +
                ", numero=" + numero +
                '}';
    }

    public C_MyDate getDataGiornoMeseAnno() {
        return new C_MyDate(data);
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        C_CircolareDto that = (C_CircolareDto) o;

        return key.equals(that.key);

    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public C_NormalizedURL getUrl() {
        return url;
    }

    public void setUrl(C_NormalizedURL url) {
        this.url = url;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    private String digest() {
        String x = new Formatter().format("%05d", numero).toString();
        return x + "#" + data + "#" + titolo + "#" + url;
    }

    @Override
    public int compareTo(C_CircolareDto another) {
        if (numero < another.numero) return -1;
        if (numero > another.numero) return 1;
        return this.digest().compareTo(another.digest());
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}

package it.gov.fermitivoli.server.model;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import it.gov.fermitivoli.server.datalayer.CacheItem;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by stefano on 01/08/15.
 */
@Entity
public class GAE_CircolareDB_V2 implements Serializable, CacheItem<String> {
    public long token;
    public String titolo;
    public String testo;
    public Date data;

    @Id
    public String key;
    public String url;
    public int numero;
    public boolean flagDelete;



    public GAE_CircolareDB_V2() {
        flagDelete = false;
    }

    private void _assert(boolean cond) {
        if (!cond)
            throw new AssertionError("ERROR");
    }

    public void check() {
        _assert(token >= 0);
        _assert(titolo != null);
        _assert(data != null);
        _assert(key != null);
        _assert(numero > 0);
    }

    @Override
    public String toString() {
        return "GAE_CircolareDB{" +
                "token=" + token +
                ", titolo='" + titolo + '\'' +
                ", testo='" + "...(NON INSERITO NELLA STAMPA)..." + '\'' +
                ", data=" + data +
                ", key='" + key + '\'' +
                ", url='" + url + '\'' +
                ", numero=" + numero +
                ", flagDelete=" + flagDelete +
                '}';
    }

    public boolean isFlagDelete() {
        return flagDelete;
    }

    public void setFlagDelete(boolean flagDelete) {
        this.flagDelete = flagDelete;
    }


    public long getToken() {
        return token;
    }

    public void setToken(long token) {
        this.token = token;
    }

    public void set(String titolo,
                    String testo,
                    Date data,
                    String key,
                    String url,
                    int numero) {

        this.titolo = titolo;
        this.testo = testo;
        this.data = data;
        this.key = key;
        this.url = url;
        this.numero = numero;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public GAE_CircolareDB_V2 clone() {
        final GAE_CircolareDB_V2 c = new GAE_CircolareDB_V2();
        c.titolo = titolo;
        c.testo = testo;
        c.data = data;
        c.key = key;
        c.numero = numero;
        c.url = url;
        c.token = token;
        return c;
    }
}

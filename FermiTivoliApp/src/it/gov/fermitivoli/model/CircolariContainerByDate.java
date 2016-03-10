package it.gov.fermitivoli.model;

import it.gov.fermitivoli.dao.CircolareDB;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by stefano on 01/08/15.
 */
public class CircolariContainerByDate {
    private final List<CircolareDB> listaCircolari;
    private final Map<C_MyDate, Set<CircolareDB>> circolariByDataApplicazione;
    private final Map<C_MyDate, Set<CircolareDB>> circolariByDataPubblicazione;

    public CircolariContainerByDate(List<CircolareDB> listaCircolari, Map<C_MyDate, Set<CircolareDB>> circolariByDataApplicazione, Map<C_MyDate, Set<CircolareDB>> circolariByDataPubblicazione) {
        this.listaCircolari = Collections.unmodifiableList(listaCircolari);
        this.circolariByDataApplicazione = Collections.unmodifiableMap(circolariByDataApplicazione);
        this.circolariByDataPubblicazione = Collections.unmodifiableMap(circolariByDataPubblicazione);
    }

    public List<CircolareDB> getListaCircolari() {
        return listaCircolari;
    }

    public Map<C_MyDate, Set<CircolareDB>> getCircolariByDataApplicazione() {
        return circolariByDataApplicazione;
    }

    public Map<C_MyDate, Set<CircolareDB>> getCircolariByDataPubblicazione() {
        return circolariByDataPubblicazione;
    }
}

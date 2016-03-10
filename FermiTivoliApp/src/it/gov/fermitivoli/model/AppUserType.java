package it.gov.fermitivoli.model;

/**
 * Created by stefano on 18/09/15.
 */
public enum AppUserType {
    DOCENTE("docente"), STUDENTE("studente"), FAMIGLIA("genitore"), ALTRO("-");

    public String getDescrizione() {
        return descrizione;
    }

    private final String descrizione;

    AppUserType(String descrizione) {
        this.descrizione = descrizione;
    }
}

package it.gov.fermitivoli.model;

/**
 * Created by stefano on 25/06/15.
 */
public class TermineInfoWeb implements Comparable<TermineInfoWeb> {
    private final String termine;
    private int occorrenze;

    public TermineInfoWeb(String termine) {
        this.termine = termine;
    }

    public String getTermine() {
        return termine;
    }

    public void aggiungiOccorrenze(int count) {
        occorrenze = count + occorrenze;

    }

    public int getOccorrenze() {
        return occorrenze;
    }

    public void setOccorrenze(int occorrenze) {
        this.occorrenze = occorrenze;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TermineInfoWeb that = (TermineInfoWeb) o;

        return !(termine != null ? !termine.equals(that.termine) : that.termine != null);

    }

    @Override
    public int hashCode() {
        return termine != null ? termine.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "TermineInfoWeb{" +
                "termine='" + termine + '\'' +
                ", occorrenze=" + occorrenze +
                '}';
    }

    @Override
    public int compareTo(TermineInfoWeb another) {
        return termine.compareTo(another.termine);
    }
}

package it.gov.fermitivoli.model;

import java.io.Serializable;

/**
 * Created by stefano on 07/06/15.
 */
public class C_NormalizedURL implements Serializable, Comparable<C_NormalizedURL> {
    private String url;

    /**
     * costruttore privato per GSON
     */

    private C_NormalizedURL() {
    }

    public C_NormalizedURL(String url) {
        this.url = url.replace(" ", "%20");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        C_NormalizedURL that = (C_NormalizedURL) o;

        return url.equals(that.url);

    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }

    @Override
    public String toString() {
        return url;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public int compareTo(C_NormalizedURL another) {
        return url.compareTo(another.getUrl());
    }


}

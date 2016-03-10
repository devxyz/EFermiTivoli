package it.gov.fermitivoli.model;

import android.content.Context;
import it.gov.fermitivoli.R;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by stefano on 30/05/15.
 * nomi (case sensitive) dei comuni gestiti da cotral
 */
public class FermateCotral {
    private final Map<String, Integer> mapComune_id;
    private final Map<String, String> mapFermata_Comune;

    public FermateCotral(Context c) {
        mapComune_id = new TreeMap<String, Integer>(new SortIgnoreCase());
        mapFermata_Comune = new TreeMap<String, String>(new SortIgnoreCase());

        //inserisce i comuni
        String[] s = c.getResources().getStringArray(R.array.cotral_map_comuni_id);
        for (String riga : s) {
            if (riga.trim().length() > 0) {
                String[] split = riga.split("#");
                String comune = split[0];
                String comuneTrimmed = comune.trim();
                mapComune_id.put(comuneTrimmed, Integer.parseInt(split[1]));
            }
        }

        s = c.getResources().getStringArray(R.array.cotral_map_fermata_comune);
        for (String riga : s) {
            if (riga.trim().length() > 0) {
                String[] split = riga.split("#");
                String comune = split[0];
                String comuneTrimmed = comune.trim();
                mapFermata_Comune.put(comuneTrimmed, split[1]);
            }
        }


    }


    public Set<String> getComuni() {
        return mapComune_id.keySet();
    }

    public Set<String> getFermate() {
        return mapFermata_Comune.keySet();
    }

    public boolean containsComune(String comune) {
        return mapComune_id.containsKey(comune.toUpperCase());
    }

    public boolean containsFermata(String fermata) {
        return mapFermata_Comune.containsKey(fermata.toUpperCase());
    }

    public Integer getIdComune(String comune) {
        return mapComune_id.get(comune);
    }

    public String getComuneFermata(String fermata) {
        return mapFermata_Comune.get(fermata.toUpperCase());
    }


    class SortIgnoreCase implements Comparator<Object> {
        public int compare(Object o1, Object o2) {
            String s1 = (String) o1;
            String s2 = (String) o2;
            return s1.toLowerCase().compareTo(s2.toLowerCase());
        }
    }


}

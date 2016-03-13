package it.gov.fermitivoli.server.util;

import it.gov.fermitivoli.model.C_Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by stefano on 13/03/16.
 */
public class MergeUtil<T1, T2> {
    /**
     * elementi di T1 che non corrispondono ad elementi di T2
     */
    public final List<T1> onlyT1 = new ArrayList<>();
    /**
     * elementi di T2 che non corrispondono ad elementi di T2
     */
    public final List<T2> onlyT2 = new ArrayList<>();

    /**
     * elementi di T1 che corrispondono a quelli di T2
     */
    public final List<C_Pair<T1, T2>> common = new ArrayList<>();


    /**
     * cerca una corrispondenza tra i valori in m1 e quelli in m2 in base alla chiave
     *
     * @param m1
     * @param m2
     * @param <K>
     * @param <T1>
     * @param <T2>
     * @return
     */
    public static <K, T1, T2> MergeUtil<T1, T2> matchKey(Map<K, T1> m1, Map<K, T2> m2) {
        MergeUtil<T1, T2> m = new MergeUtil<T1, T2>();

        for (K k : m1.keySet()) {
            if (m2.containsKey(k)) {
                m.common.add(new C_Pair<>(m1.get(k), m2.get(k)));
            } else {
                m.onlyT1.add(m1.get(k));
            }
        }

        for (K k : m2.keySet()) {
            if (!m1.containsKey(k)) {
                m.onlyT2.add(m2.get(k));
            }
        }


        return m;

    }
}

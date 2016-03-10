package it.gov.fermitivoli.server.util;

import it.gov.fermitivoli.model.C_CircolareDto;
import it.gov.fermitivoli.model.C_NormalizedURL;
import it.gov.fermitivoli.server.model.GAE_CircolareDB_V2;

/**
 * Created by stefano on 01/08/15.
 */
public class GAE_CircolariUtil {
    public static void copy(GAE_CircolareDB_V2 source, C_CircolareDto destination) {
        destination.setTitolo(source.getTitolo());
        destination.setData(source.getData());
        destination.setUrl(new C_NormalizedURL(source.getUrl()));
        destination.setNumero(source.getNumero());
        destination.setTesto(source.getTesto());
        destination.setKey(source.getKey());
        destination.setToken(source.getToken());

    }


}

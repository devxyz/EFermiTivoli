package it.gov.fermitivoli.server.datastore;

import it.gov.fermitivoli.model.C_CircolareDto;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by stefano on 05/08/15.
 */
public interface IDataStoreOptimizer {
    boolean isActive(String key);

    Set<String> getKeys();

    void delete(String key);

    void insert(C_CircolareDto x);

    void insert(String key,
                String titolo,
                String testo,
                Date data,
                String url,
                int numero
    );

    void update(String key,
                String testo
    );

    String stats();

    List<C_CircolareDto> listAllCircolariAttive(long fromToken);

    List<C_CircolareDto> listAllCircolariAttive();

    Set<String> listAllCircolariEliminate(long fromToken);

    Set<String> listAllCircolariEliminate();

    void invalidate();

    C_CircolareDto getByKey(String key);
}

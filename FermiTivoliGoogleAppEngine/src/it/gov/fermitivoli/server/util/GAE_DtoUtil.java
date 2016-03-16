package it.gov.fermitivoli.server.util;

import it.gov.fermitivoli.model.C_CircolareDto;
import it.gov.fermitivoli.model.C_NewsDto;
import it.gov.fermitivoli.model.C_NormalizedURL;
import it.gov.fermitivoli.server.model.GAE_CircolareDB_V2;
import it.gov.fermitivoli.server.model.GAE_NewsDB_V2;

/**
 * Created by stefano on 01/08/15.
 */
public class GAE_DtoUtil {

    public static void copy(GAE_CircolareDB_V2 source, C_CircolareDto destination) {
        destination.setTitolo(source.getTitolo());
        destination.setData(source.getData());
        destination.setUrl(new C_NormalizedURL(source.getUrl()));
        destination.setNumero(source.getNumero());
        destination.setTesto(source.getTesto());
        destination.setKey(source.getKey());
        destination.setToken(source.getToken());
    }

    public static void copy(C_CircolareDto source, GAE_CircolareDB_V2 destination, boolean flagDelete) {
        destination.setTitolo(source.getTitolo());
        destination.setData(source.getData());
        destination.setUrl(source.getUrl().getUrl());
        destination.setNumero(source.getNumero());
        destination.setTesto(source.getTesto());
        destination.setKey(source.getKey());
        destination.setToken(source.getToken());
        destination.setFlagDelete(flagDelete);
    }

    public static void copy(C_NewsDto source, GAE_NewsDB_V2 destination, boolean flagDelete) {
        destination.setPubDate(source.getPubDate());
        destination.setKey(source.getKey());
        destination.setTitolo(source.getTitolo());
        destination.setFlagDelete(flagDelete);
        destination.setTesto(source.getTesto());
        destination.setContenuto(source.getContenuto());
        destination.setDataInserimento(source.getDataInserimento());
        destination.setFullimageLink(source.getFullimageLink());
        destination.setThumbimageLink(source.getThumbimageLink());
        destination.setToken(source.getToken());
        destination.setLink(source.getLink());
    }

    public static void copy(GAE_NewsDB_V2 source, C_NewsDto destination) {
        destination.setPubDate(source.getPubDate());
        destination.setKey(source.getKey());
        destination.setTitolo(source.getTitolo());
        destination.setTesto(source.getTesto());
        destination.setContenuto(source.getContenuto());
        destination.setDataInserimento(source.getDataInserimento());
        destination.setFullimageLink(source.getFullimageLink());
        destination.setThumbimageLink(source.getThumbimageLink());
        destination.setToken(source.getToken());
        destination.setLink(source.getLink());
    }


}

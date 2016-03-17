package it.gov.fermitivoli.util;

import it.gov.fermitivoli.dao.CircolareDB;
import it.gov.fermitivoli.dao.NewsDB;
import it.gov.fermitivoli.model.C_CircolareDto;
import it.gov.fermitivoli.model.C_NewsDto;
import it.gov.fermitivoli.model.C_NormalizedURL;

/**
 * Created by stefano on 01/08/15.
 */
public class DtoUtil {

    public static void copy(CircolareDB source, C_CircolareDto destination) {
        destination.setTitolo(source.getTitolo());
        destination.setData(source.getData());
        destination.setUrl(new C_NormalizedURL(source.getUrl()));
        destination.setNumero(source.getNumero());
        destination.setTesto(source.getTesto());
        destination.setKey(source.getKey());
        destination.setToken(source.getToken());
    }

    public static void copy(C_CircolareDto source, CircolareDB destination) {
        destination.setTitolo(source.getTitolo());
        destination.setData(source.getData());
        destination.setUrl(source.getUrl().getUrl());
        destination.setNumero(source.getNumero());
        destination.setTesto(source.getTesto());
        destination.setKey(source.getKey());
        destination.setToken(source.getToken());
        //destination.setFlagDelete(flagDelete);
    }

    public static void copy(C_NewsDto source, NewsDB destination) {
        destination.setPubDate(source.getPubDate());
        destination.setKey(source.getKey());
        destination.setTitolo(source.getTitolo());
        //destination.setFlagDelete(flagDelete);
        destination.setTesto(source.getTesto());
        destination.setContenuto(source.getContenuto());
        destination.setDataInserimento(source.getDataInserimento());
        destination.setFullimageLink(source.getFullimageLink());
        destination.setThumbimageLink(source.getThumbimageLink());
        destination.setToken(source.getToken());
        destination.setLink(source.getLink());
    }

    public static void copy(NewsDB source, C_NewsDto destination) {
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

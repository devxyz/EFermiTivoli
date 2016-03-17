package it.gov.fermitivoli.model;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Sdo delle circolari con informazioni su quelle nuove e vecchie in base a quanto risulta nel client
 */
public class C_JSonCircolariDeltaServletResponse {
    public ArrayList<C_CircolareDto> circolariDaAggiungereAggiornare = new ArrayList<>();
    public List<C_NewsDto> newsDaAggiungereAggiornare = new ArrayList<>();
    public TreeSet<String> keyCircolariDaRimuovere = new TreeSet<>();
    public TreeSet<String> keyNewsDaRimuovere = new TreeSet<>();
    public long minToken;
    public long maxToken;


}

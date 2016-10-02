package it.gov.fermitivoli.model.menu.impl;

import it.gov.fermitivoli.R;
import it.gov.fermitivoli.model.menu.DataMenuInfo;
import it.gov.fermitivoli.model.menu.DataMenuInfoType;

public class StringsMenuPrincipale {
    public static final DataMenuInfo HOME_1 = new DataMenuInfo(1, "Home", "I.T.C.G Enrico Fermi - Tivoli", it.gov.fermitivoli.fragment.HomeFragment.class, R.drawable.ic_home, DataMenuInfoType.search(it.gov.fermitivoli.fragment.HomeFragment.class), null);
    public static final DataMenuInfo CONTATTACI_2 = new DataMenuInfo(2, "Contattaci", "Contatta la scuola", it.gov.fermitivoli.fragment.ContattiFragment.class, R.drawable.icon_info, DataMenuInfoType.search(it.gov.fermitivoli.fragment.ContattiFragment.class), null);
    public static final DataMenuInfo INDIRIZZI_DI_STUDIO_9 = new DataMenuInfo(9, "Indirizzi di studio", "Indirizzi di studio", it.gov.fermitivoli.fragment.IndirizziStudioFragment.class, R.drawable.ic_address_48, DataMenuInfoType.search(it.gov.fermitivoli.fragment.IndirizziStudioFragment.class), null);
    public static final DataMenuInfo LE_STRUTTURE_8 = new DataMenuInfo(8, "Le strutture", "Foto Gallery strutture scolastiche", it.gov.fermitivoli.fragment.PhotosExpandibleFragment.class, R.drawable.ic_photos, DataMenuInfoType.search(it.gov.fermitivoli.fragment.PhotosExpandibleFragment.class), null);
    public static final DataMenuInfo LIBRI_DI_TESTO_15 = new DataMenuInfo(15, "Libri di testo", "Libri di testo", it.gov.fermitivoli.action.LibriTestoAction.class, R.drawable.library_icon, DataMenuInfoType.search(it.gov.fermitivoli.action.LibriTestoAction.class), null);
    public static final DataMenuInfo ORARIO_SCOLASTICO_10 = new DataMenuInfo(10, "Orario Scolastico", "Orario Scolastico", it.gov.fermitivoli.action.OrarioScolasticoAction.class, R.drawable.orario_scolastico, DataMenuInfoType.search(it.gov.fermitivoli.action.OrarioScolasticoAction.class), null);
    public static final DataMenuInfo ISCRIZIONE_ONLINE_17 = new DataMenuInfo(17, "Iscrizione Online", "Iscrizione Online", it.gov.fermitivoli.action.IscrizioneOnlineAction.class, R.drawable.logo_iscrizioni_online_50x50, DataMenuInfoType.search(it.gov.fermitivoli.action.IscrizioneOnlineAction.class), null);
    public static final DataMenuInfo NEWS_4 = new DataMenuInfo(4, "News", "Ultime Notizie dalla scuola", it.gov.fermitivoli.fragment.NewsFragmentRss.class, R.drawable.id_tab_news, DataMenuInfoType.search(it.gov.fermitivoli.fragment.NewsFragmentRss.class), null);
    public static final DataMenuInfo CIRCOLARI_5 = new DataMenuInfo(5, "Circolari", "Circolari scolastiche", it.gov.fermitivoli.fragment.CircolariSearchFragment.class, R.drawable.file_search, DataMenuInfoType.search(it.gov.fermitivoli.fragment.CircolariSearchFragment.class), null);
    public static final DataMenuInfo CIRCOLARI_DI_OGGI_6 = new DataMenuInfo(6, "Circolari di Oggi", "Circolari del Giorno", it.gov.fermitivoli.fragment.CircolariGiornaliereFragment.class, R.drawable.calendar_preferences_50x50, DataMenuInfoType.search(it.gov.fermitivoli.fragment.CircolariGiornaliereFragment.class), null);
    public static final DataMenuInfo R_E_DOCENTI_12 = new DataMenuInfo(12, "R.E. Docenti", "Registro elettronico docenti", it.gov.fermitivoli.action.ReDocentiAction.class, R.drawable.logo_re_docente, DataMenuInfoType.search(it.gov.fermitivoli.action.ReDocentiAction.class), null);
    public static final DataMenuInfo R_E_FAMIGLIE_13 = new DataMenuInfo(13, "R.E. Famiglie", "Registro elettronico famiglie", it.gov.fermitivoli.action.ReStudentiAction.class, R.drawable.logo_re_famiglie, DataMenuInfoType.search(it.gov.fermitivoli.action.ReStudentiAction.class), null);
    public static final DataMenuInfo FERMATE_E_TRAGITTI_AUTOBUS_19 = new DataMenuInfo(19, "Fermate e tragitti Autobus", "Informazioni autobus Cotral", it.gov.fermitivoli.fragment.CotralFragment.class, R.drawable.bus_stop_48x48, DataMenuInfoType.search(it.gov.fermitivoli.fragment.CotralFragment.class), null);
    public static final DataMenuInfo ORARI_TRENITALIA_20 = new DataMenuInfo(20, "Orari Trenitalia", "Orari Trenitalia", it.gov.fermitivoli.action.TrenitaliaAction.class, R.drawable.train, DataMenuInfoType.search(it.gov.fermitivoli.action.TrenitaliaAction.class), null);
    public static final DataMenuInfo ORARI_COTRAL_21 = new DataMenuInfo(21, "Orari Cotral", "Orari Cotral", it.gov.fermitivoli.action.CotralAction.class, R.drawable.bus_stop_48x48, DataMenuInfoType.search(it.gov.fermitivoli.action.CotralAction.class), null);
    public static final DataMenuInfo CHIUDI_22 = new DataMenuInfo(22, "Chiudi", "Chiudi", it.gov.fermitivoli.action.CloseAction.class, R.drawable.cancel_red, DataMenuInfoType.search(it.gov.fermitivoli.action.CloseAction.class), null);
}

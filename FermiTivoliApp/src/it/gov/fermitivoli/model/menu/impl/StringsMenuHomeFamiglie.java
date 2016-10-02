package it.gov.fermitivoli.model.menu.impl;

import it.gov.fermitivoli.R;
import it.gov.fermitivoli.model.menu.DataMenuInfo;
import it.gov.fermitivoli.model.menu.DataMenuInfoType;

public class StringsMenuHomeFamiglie {
    public static final DataMenuInfo R_E_FAMIGLIE_101 = new DataMenuInfo(101, "R.E. Famiglie", "Registro elettronico studenti", it.gov.fermitivoli.action.ReStudentiAction.class, R.drawable.logo_re_famiglie, DataMenuInfoType.search(it.gov.fermitivoli.action.ReStudentiAction.class), null);
    public static final DataMenuInfo ORARIO_SCOLASTICO_4 = new DataMenuInfo(4, "Orario Scolastico", "Orario Scolastico", it.gov.fermitivoli.action.OrarioScolasticoAction.class, R.drawable.orario_scolastico, DataMenuInfoType.search(it.gov.fermitivoli.action.OrarioScolasticoAction.class), null);
    public static final DataMenuInfo FOTO_5 = new DataMenuInfo(5, "Foto", "Foto Gallery strutture scolastiche", it.gov.fermitivoli.fragment.PhotosExpandibleFragment.class, R.drawable.ic_photos, DataMenuInfoType.search(it.gov.fermitivoli.fragment.PhotosExpandibleFragment.class), null);
    public static final DataMenuInfo INDIRIZZI_DI_STUDIO_3 = new DataMenuInfo(3, "Indirizzi di studio", "Indirizzi di studio", it.gov.fermitivoli.fragment.IndirizziStudioFragment.class, R.drawable.ic_address_48, DataMenuInfoType.search(it.gov.fermitivoli.fragment.IndirizziStudioFragment.class), null);
    public static final DataMenuInfo LIBRI_DI_TESTO_102 = new DataMenuInfo(102, "Libri di testo", "Libri di testo", it.gov.fermitivoli.action.LibriTestoAction.class, R.drawable.library_icon, DataMenuInfoType.search(it.gov.fermitivoli.action.LibriTestoAction.class), null);
    public static final DataMenuInfo ISCRIZIONE_ONLINE_106 = new DataMenuInfo(106, "Iscrizione Online", "Iscrizione Online", it.gov.fermitivoli.action.IscrizioneOnlineAction.class, R.drawable.logo_iscrizioni_online_50x50, DataMenuInfoType.search(it.gov.fermitivoli.action.IscrizioneOnlineAction.class), null);
    public static final DataMenuInfo FERMATE_E_TRAGITTI_AUTOBUS_103 = new DataMenuInfo(103, "Fermate e tragitti Autobus", "Informazioni autobus Cotral", it.gov.fermitivoli.fragment.CotralFragment.class, R.drawable.bus_stop_48x48, DataMenuInfoType.search(it.gov.fermitivoli.fragment.CotralFragment.class), null);
    public static final DataMenuInfo ORARI_TRENITALIA_201 = new DataMenuInfo(201, "Orari Trenitalia", "Orari Trenitalia", it.gov.fermitivoli.action.TrenitaliaAction.class, R.drawable.train, DataMenuInfoType.search(it.gov.fermitivoli.action.TrenitaliaAction.class), null);
    public static final DataMenuInfo ORARI_COTRAL_202 = new DataMenuInfo(202, "Orari Cotral", "Orari Cotral", it.gov.fermitivoli.action.CotralAction.class, R.drawable.bus_stop_48x48, DataMenuInfoType.search(it.gov.fermitivoli.action.CotralAction.class), null);
}

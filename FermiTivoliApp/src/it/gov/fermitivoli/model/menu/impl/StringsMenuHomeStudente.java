package it.gov.fermitivoli.model.menu.impl;
import it.gov.fermitivoli.R;

import it.gov.fermitivoli.model.menu.*;
public class StringsMenuHomeStudente{
     public static final DataMenuInfo ORARIO_SCOLASTICO_4= new DataMenuInfo(4,"Orario Scolastico","Orario Scolastico",it.gov.fermitivoli.action.OrarioScolasticoAction.class,R.drawable.orario_scolastico,DataMenuInfoType.search(it.gov.fermitivoli.action.OrarioScolasticoAction.class),null     );
     public static final DataMenuInfo LIBRI_DI_TESTO_102= new DataMenuInfo(102,"Libri di testo","Libri di testo",it.gov.fermitivoli.action.LibriTestoAction.class,R.drawable.library_icon,DataMenuInfoType.search(it.gov.fermitivoli.action.LibriTestoAction.class),null     );
     public static final DataMenuInfo FERMATE_E_TRAGITTI_AUTOBUS_103= new DataMenuInfo(103,"Fermate e tragitti Autobus","Informazioni autobus Cotral",it.gov.fermitivoli.fragment.CotralFragment.class,R.drawable.bus_stop_48x48,DataMenuInfoType.search(it.gov.fermitivoli.fragment.CotralFragment.class),null     );
     public static final DataMenuInfo ORARI_TRENITALIA_201= new DataMenuInfo(201,"Orari Trenitalia","Orari Trenitalia",it.gov.fermitivoli.action.TrenitaliaAction.class,R.drawable.train,DataMenuInfoType.search(it.gov.fermitivoli.action.TrenitaliaAction.class),null     );
     public static final DataMenuInfo ORARI_COTRAL_202= new DataMenuInfo(202,"Orari Cotral","Orari Cotral",it.gov.fermitivoli.action.CotralAction.class,R.drawable.bus_stop_48x48,DataMenuInfoType.search(it.gov.fermitivoli.action.CotralAction.class),null     );
}

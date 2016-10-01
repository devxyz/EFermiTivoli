package it.gov.fermitivoli.model.menu.impl;
import it.gov.fermitivoli.R;

import it.gov.fermitivoli.model.menu.*;
public class StringsMenuHomeDocente{
     public static final DataMenuInfo CIRCOLARI_DI_OGGI_4= new DataMenuInfo(4,"Circolari di Oggi","Circolari del Giorno",it.gov.fermitivoli.fragment.CircolariGiornaliereFragment.class,R.drawable.calendar_preferences_50x50,DataMenuInfoType.search(it.gov.fermitivoli.fragment.CircolariGiornaliereFragment.class),null     );
     public static final DataMenuInfo ORARIO_SCOLASTICO_532= new DataMenuInfo(532,"Orario Scolastico","Orario Scolastico",it.gov.fermitivoli.action.OrarioScolasticoAction.class,R.drawable.orario_scolastico,DataMenuInfoType.search(it.gov.fermitivoli.action.OrarioScolasticoAction.class),null     );
     public static final DataMenuInfo REGISTRO_DOCENTI_201= new DataMenuInfo(201,"Registro Docenti","Registro elettronico docenti",it.gov.fermitivoli.action.ReDocentiAction.class,R.drawable.logo_re_docente,DataMenuInfoType.search(it.gov.fermitivoli.action.ReDocentiAction.class),null     );
     public static final DataMenuInfo LIBRI_DI_TESTO_102= new DataMenuInfo(102,"Libri di testo","Libri di testo",it.gov.fermitivoli.action.LibriTestoAction.class,R.drawable.library_icon,DataMenuInfoType.search(it.gov.fermitivoli.action.LibriTestoAction.class),null     );
}

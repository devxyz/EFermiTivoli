package it.gov.fermitivoli.layout;
import android.app.*;
import android.view.*;
import android.widget.*;
import android.webkit.*;
import it.gov.fermitivoli.R;
public class LayoutObjs_listview_indirizzi_studio_description_livello3_informazioni_xml{
  public final TextView title;
  public final ImageView arrow;

public LayoutObjs_listview_indirizzi_studio_description_livello3_informazioni_xml(Fragment f){
  View view=f.getView();
    title= (TextView)view.findViewById(R.id.title);
  arrow= (ImageView)view.findViewById(R.id.arrow);
}
public LayoutObjs_listview_indirizzi_studio_description_livello3_informazioni_xml(Activity view){
    title= (TextView)view.findViewById(R.id.title);
  arrow= (ImageView)view.findViewById(R.id.arrow);

}
public LayoutObjs_listview_indirizzi_studio_description_livello3_informazioni_xml(View view){
    title= (TextView)view.findViewById(R.id.title);
  arrow= (ImageView)view.findViewById(R.id.arrow);

}
public LayoutObjs_listview_indirizzi_studio_description_livello3_informazioni_xml(Dialog view){
    title= (TextView)view.findViewById(R.id.title);
  arrow= (ImageView)view.findViewById(R.id.arrow);

}
}
   

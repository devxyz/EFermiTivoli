package it.gov.fermitivoli.layout;
import android.app.*;
import android.view.*;
import android.widget.*;
import android.webkit.*;
import it.gov.fermitivoli.R;
public class LayoutObjs_listview_circolari_and_notizie_short_xml{
  public final TextView title;

public LayoutObjs_listview_circolari_and_notizie_short_xml(Fragment f){
  View view=f.getView();
    title= (TextView)view.findViewById(R.id.title);
}
public LayoutObjs_listview_circolari_and_notizie_short_xml(Activity view){
    title= (TextView)view.findViewById(R.id.title);

}
public LayoutObjs_listview_circolari_and_notizie_short_xml(View view){
    title= (TextView)view.findViewById(R.id.title);

}
public LayoutObjs_listview_circolari_and_notizie_short_xml(Dialog view){
    title= (TextView)view.findViewById(R.id.title);

}
}
   

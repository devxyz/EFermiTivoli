package it.gov.fermitivoli.layout;
import android.app.*;
import android.view.*;
import android.widget.*;
import android.webkit.*;
import it.gov.fermitivoli.R;
public class LayoutObjs_fragment_circolari_old_xml{
  public final ListView listView;
  public final Button buttonFulltextCircolari;

public LayoutObjs_fragment_circolari_old_xml(Fragment f){
  View view=f.getView();
    listView= (ListView)view.findViewById(R.id.listView);
  buttonFulltextCircolari= (Button)view.findViewById(R.id.buttonFulltextCircolari);
}
public LayoutObjs_fragment_circolari_old_xml(Activity view){
    listView= (ListView)view.findViewById(R.id.listView);
  buttonFulltextCircolari= (Button)view.findViewById(R.id.buttonFulltextCircolari);

}
public LayoutObjs_fragment_circolari_old_xml(View view){
    listView= (ListView)view.findViewById(R.id.listView);
  buttonFulltextCircolari= (Button)view.findViewById(R.id.buttonFulltextCircolari);

}
public LayoutObjs_fragment_circolari_old_xml(Dialog view){
    listView= (ListView)view.findViewById(R.id.listView);
  buttonFulltextCircolari= (Button)view.findViewById(R.id.buttonFulltextCircolari);

}
}
   

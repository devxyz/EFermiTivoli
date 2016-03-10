package it.gov.fermitivoli.layout;
import android.app.*;
import android.view.*;
import android.widget.*;
import android.webkit.*;
import it.gov.fermitivoli.R;
public class LayoutObjs_fragment_indirizzi_studio_xml{
  public final ListView listView3;
  public final ImageButton imageButton;

public LayoutObjs_fragment_indirizzi_studio_xml(Fragment f){
  View view=f.getView();
    listView3= (ListView)view.findViewById(R.id.listView3);
  imageButton= (ImageButton)view.findViewById(R.id.imageButton);
}
public LayoutObjs_fragment_indirizzi_studio_xml(Activity view){
    listView3= (ListView)view.findViewById(R.id.listView3);
  imageButton= (ImageButton)view.findViewById(R.id.imageButton);

}
public LayoutObjs_fragment_indirizzi_studio_xml(View view){
    listView3= (ListView)view.findViewById(R.id.listView3);
  imageButton= (ImageButton)view.findViewById(R.id.imageButton);

}
public LayoutObjs_fragment_indirizzi_studio_xml(Dialog view){
    listView3= (ListView)view.findViewById(R.id.listView3);
  imageButton= (ImageButton)view.findViewById(R.id.imageButton);

}
}
   

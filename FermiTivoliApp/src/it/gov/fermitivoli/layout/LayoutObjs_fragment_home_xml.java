package it.gov.fermitivoli.layout;
import android.app.*;
import android.view.*;
import android.widget.*;
import android.webkit.*;
import it.gov.fermitivoli.R;
public class LayoutObjs_fragment_home_xml{
  public final ImageView imageView;
  public final ListView listView4;
  public final TextView textView5;
  public final TextView txtInfo;
  public final TextView textViewTipoUtente;
  public final TextView textView14;
  public final TextView textView15;

public LayoutObjs_fragment_home_xml(Fragment f){
  View view=f.getView();
    imageView= (ImageView)view.findViewById(R.id.imageView);
  listView4= (ListView)view.findViewById(R.id.listView4);
  textView5= (TextView)view.findViewById(R.id.textView5);
  txtInfo= (TextView)view.findViewById(R.id.txtInfo);
  textViewTipoUtente= (TextView)view.findViewById(R.id.textViewTipoUtente);
  textView14= (TextView)view.findViewById(R.id.textView14);
  textView15= (TextView)view.findViewById(R.id.textView15);
}
public LayoutObjs_fragment_home_xml(Activity view){
    imageView= (ImageView)view.findViewById(R.id.imageView);
  listView4= (ListView)view.findViewById(R.id.listView4);
  textView5= (TextView)view.findViewById(R.id.textView5);
  txtInfo= (TextView)view.findViewById(R.id.txtInfo);
  textViewTipoUtente= (TextView)view.findViewById(R.id.textViewTipoUtente);
  textView14= (TextView)view.findViewById(R.id.textView14);
  textView15= (TextView)view.findViewById(R.id.textView15);

}
public LayoutObjs_fragment_home_xml(View view){
    imageView= (ImageView)view.findViewById(R.id.imageView);
  listView4= (ListView)view.findViewById(R.id.listView4);
  textView5= (TextView)view.findViewById(R.id.textView5);
  txtInfo= (TextView)view.findViewById(R.id.txtInfo);
  textViewTipoUtente= (TextView)view.findViewById(R.id.textViewTipoUtente);
  textView14= (TextView)view.findViewById(R.id.textView14);
  textView15= (TextView)view.findViewById(R.id.textView15);

}
public LayoutObjs_fragment_home_xml(Dialog view){
    imageView= (ImageView)view.findViewById(R.id.imageView);
  listView4= (ListView)view.findViewById(R.id.listView4);
  textView5= (TextView)view.findViewById(R.id.textView5);
  txtInfo= (TextView)view.findViewById(R.id.txtInfo);
  textViewTipoUtente= (TextView)view.findViewById(R.id.textViewTipoUtente);
  textView14= (TextView)view.findViewById(R.id.textView14);
  textView15= (TextView)view.findViewById(R.id.textView15);

}
}
   

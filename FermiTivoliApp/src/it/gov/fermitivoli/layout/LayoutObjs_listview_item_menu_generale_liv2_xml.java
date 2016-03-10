package it.gov.fermitivoli.layout;
import android.app.*;
import android.view.*;
import android.widget.*;
import android.webkit.*;
import it.gov.fermitivoli.R;
public class LayoutObjs_listview_item_menu_generale_liv2_xml{
  public final ImageView icon;
  public final TextView title;
  public final TextView counter;

public LayoutObjs_listview_item_menu_generale_liv2_xml(Fragment f){
  View view=f.getView();
    icon= (ImageView)view.findViewById(R.id.icon);
  title= (TextView)view.findViewById(R.id.title);
  counter= (TextView)view.findViewById(R.id.counter);
}
public LayoutObjs_listview_item_menu_generale_liv2_xml(Activity view){
    icon= (ImageView)view.findViewById(R.id.icon);
  title= (TextView)view.findViewById(R.id.title);
  counter= (TextView)view.findViewById(R.id.counter);

}
public LayoutObjs_listview_item_menu_generale_liv2_xml(View view){
    icon= (ImageView)view.findViewById(R.id.icon);
  title= (TextView)view.findViewById(R.id.title);
  counter= (TextView)view.findViewById(R.id.counter);

}
public LayoutObjs_listview_item_menu_generale_liv2_xml(Dialog view){
    icon= (ImageView)view.findViewById(R.id.icon);
  title= (TextView)view.findViewById(R.id.title);
  counter= (TextView)view.findViewById(R.id.counter);

}
}
   

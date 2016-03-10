package it.gov.fermitivoli.layout;
import android.app.*;
import android.view.*;
import android.widget.*;
import android.webkit.*;
import it.gov.fermitivoli.R;
public class LayoutObjs_listview_photolist_group_xml{
  public final ImageView thumb;
  public final TextView title;

public LayoutObjs_listview_photolist_group_xml(Fragment f){
  View view=f.getView();
    thumb= (ImageView)view.findViewById(R.id.thumb);
  title= (TextView)view.findViewById(R.id.title);
}
public LayoutObjs_listview_photolist_group_xml(Activity view){
    thumb= (ImageView)view.findViewById(R.id.thumb);
  title= (TextView)view.findViewById(R.id.title);

}
public LayoutObjs_listview_photolist_group_xml(View view){
    thumb= (ImageView)view.findViewById(R.id.thumb);
  title= (TextView)view.findViewById(R.id.title);

}
public LayoutObjs_listview_photolist_group_xml(Dialog view){
    thumb= (ImageView)view.findViewById(R.id.thumb);
  title= (TextView)view.findViewById(R.id.title);

}
}
   

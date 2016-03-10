package it.gov.fermitivoli.layout;
import android.app.*;
import android.view.*;
import android.widget.*;
import android.webkit.*;
import it.gov.fermitivoli.R;
public class LayoutObjs_fragment_photos_expandable_xml{
  public final ExpandableListView listView2;

public LayoutObjs_fragment_photos_expandable_xml(Fragment f){
  View view=f.getView();
    listView2= (ExpandableListView)view.findViewById(R.id.listView2);
}
public LayoutObjs_fragment_photos_expandable_xml(Activity view){
    listView2= (ExpandableListView)view.findViewById(R.id.listView2);

}
public LayoutObjs_fragment_photos_expandable_xml(View view){
    listView2= (ExpandableListView)view.findViewById(R.id.listView2);

}
public LayoutObjs_fragment_photos_expandable_xml(Dialog view){
    listView2= (ExpandableListView)view.findViewById(R.id.listView2);

}
}
   

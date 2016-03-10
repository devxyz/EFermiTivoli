package it.gov.fermitivoli.layout;
import android.app.*;
import android.view.*;
import android.widget.*;
import android.webkit.*;
import it.gov.fermitivoli.R;
public class LayoutObjs_activity_main_xml{
  public final android.support.v4.widget.DrawerLayout drawer_layout;
  public final FrameLayout frame_container;
  public final ListView list_slidermenu;

public LayoutObjs_activity_main_xml(Fragment f){
  View view=f.getView();
    drawer_layout= (android.support.v4.widget.DrawerLayout)view.findViewById(R.id.drawer_layout);
  frame_container= (FrameLayout)view.findViewById(R.id.frame_container);
  list_slidermenu= (ListView)view.findViewById(R.id.list_slidermenu);
}
public LayoutObjs_activity_main_xml(Activity view){
    drawer_layout= (android.support.v4.widget.DrawerLayout)view.findViewById(R.id.drawer_layout);
  frame_container= (FrameLayout)view.findViewById(R.id.frame_container);
  list_slidermenu= (ListView)view.findViewById(R.id.list_slidermenu);

}
public LayoutObjs_activity_main_xml(View view){
    drawer_layout= (android.support.v4.widget.DrawerLayout)view.findViewById(R.id.drawer_layout);
  frame_container= (FrameLayout)view.findViewById(R.id.frame_container);
  list_slidermenu= (ListView)view.findViewById(R.id.list_slidermenu);

}
public LayoutObjs_activity_main_xml(Dialog view){
    drawer_layout= (android.support.v4.widget.DrawerLayout)view.findViewById(R.id.drawer_layout);
  frame_container= (FrameLayout)view.findViewById(R.id.frame_container);
  list_slidermenu= (ListView)view.findViewById(R.id.list_slidermenu);

}
}
   

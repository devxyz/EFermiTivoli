package it.gov.fermitivoli.layout;
import android.app.*;
import android.view.*;
import android.widget.*;
import android.webkit.*;
import it.gov.fermitivoli.R;
public class LayoutObjs_activity_help_xml{
  public final RelativeLayout xyz;
  public final Button buttonOK;

public LayoutObjs_activity_help_xml(Fragment f){
  View view=f.getView();
    xyz= (RelativeLayout)view.findViewById(R.id.xyz);
  buttonOK= (Button)view.findViewById(R.id.buttonOK);
}
public LayoutObjs_activity_help_xml(Activity view){
    xyz= (RelativeLayout)view.findViewById(R.id.xyz);
  buttonOK= (Button)view.findViewById(R.id.buttonOK);

}
public LayoutObjs_activity_help_xml(View view){
    xyz= (RelativeLayout)view.findViewById(R.id.xyz);
  buttonOK= (Button)view.findViewById(R.id.buttonOK);

}
public LayoutObjs_activity_help_xml(Dialog view){
    xyz= (RelativeLayout)view.findViewById(R.id.xyz);
  buttonOK= (Button)view.findViewById(R.id.buttonOK);

}
}
   

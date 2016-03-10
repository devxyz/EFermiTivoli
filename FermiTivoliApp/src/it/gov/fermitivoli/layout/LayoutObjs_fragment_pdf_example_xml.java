package it.gov.fermitivoli.layout;
import android.app.*;
import android.view.*;
import android.widget.*;
import android.webkit.*;
import it.gov.fermitivoli.R;
public class LayoutObjs_fragment_pdf_example_xml{
  public final TextView messaggio2;
  public final Button button;

public LayoutObjs_fragment_pdf_example_xml(Fragment f){
  View view=f.getView();
    messaggio2= (TextView)view.findViewById(R.id.messaggio2);
  button= (Button)view.findViewById(R.id.button);
}
public LayoutObjs_fragment_pdf_example_xml(Activity view){
    messaggio2= (TextView)view.findViewById(R.id.messaggio2);
  button= (Button)view.findViewById(R.id.button);

}
public LayoutObjs_fragment_pdf_example_xml(View view){
    messaggio2= (TextView)view.findViewById(R.id.messaggio2);
  button= (Button)view.findViewById(R.id.button);

}
public LayoutObjs_fragment_pdf_example_xml(Dialog view){
    messaggio2= (TextView)view.findViewById(R.id.messaggio2);
  button= (Button)view.findViewById(R.id.button);

}
}
   

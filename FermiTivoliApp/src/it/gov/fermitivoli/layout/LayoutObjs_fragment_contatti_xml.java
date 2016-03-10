package it.gov.fermitivoli.layout;
import android.app.*;
import android.view.*;
import android.widget.*;
import android.webkit.*;
import it.gov.fermitivoli.R;
public class LayoutObjs_fragment_contatti_xml{
  public final TextView textViewCircolari;
  public final LinearLayout linearLayout;
  public final ImageButton imageButtonEmail;
  public final ImageButton imageButtonTelefono;
  public final ImageButton imageButtonWWW;
  public final ImageButton imageButtonMap;
  public final TextView textView2;
  public final TextView textView13;

public LayoutObjs_fragment_contatti_xml(Fragment f){
  View view=f.getView();
    textViewCircolari= (TextView)view.findViewById(R.id.textViewCircolari);
  linearLayout= (LinearLayout)view.findViewById(R.id.linearLayout);
  imageButtonEmail= (ImageButton)view.findViewById(R.id.imageButtonEmail);
  imageButtonTelefono= (ImageButton)view.findViewById(R.id.imageButtonTelefono);
  imageButtonWWW= (ImageButton)view.findViewById(R.id.imageButtonWWW);
  imageButtonMap= (ImageButton)view.findViewById(R.id.imageButtonMap);
  textView2= (TextView)view.findViewById(R.id.textView2);
  textView13= (TextView)view.findViewById(R.id.textView13);
}
public LayoutObjs_fragment_contatti_xml(Activity view){
    textViewCircolari= (TextView)view.findViewById(R.id.textViewCircolari);
  linearLayout= (LinearLayout)view.findViewById(R.id.linearLayout);
  imageButtonEmail= (ImageButton)view.findViewById(R.id.imageButtonEmail);
  imageButtonTelefono= (ImageButton)view.findViewById(R.id.imageButtonTelefono);
  imageButtonWWW= (ImageButton)view.findViewById(R.id.imageButtonWWW);
  imageButtonMap= (ImageButton)view.findViewById(R.id.imageButtonMap);
  textView2= (TextView)view.findViewById(R.id.textView2);
  textView13= (TextView)view.findViewById(R.id.textView13);

}
public LayoutObjs_fragment_contatti_xml(View view){
    textViewCircolari= (TextView)view.findViewById(R.id.textViewCircolari);
  linearLayout= (LinearLayout)view.findViewById(R.id.linearLayout);
  imageButtonEmail= (ImageButton)view.findViewById(R.id.imageButtonEmail);
  imageButtonTelefono= (ImageButton)view.findViewById(R.id.imageButtonTelefono);
  imageButtonWWW= (ImageButton)view.findViewById(R.id.imageButtonWWW);
  imageButtonMap= (ImageButton)view.findViewById(R.id.imageButtonMap);
  textView2= (TextView)view.findViewById(R.id.textView2);
  textView13= (TextView)view.findViewById(R.id.textView13);

}
public LayoutObjs_fragment_contatti_xml(Dialog view){
    textViewCircolari= (TextView)view.findViewById(R.id.textViewCircolari);
  linearLayout= (LinearLayout)view.findViewById(R.id.linearLayout);
  imageButtonEmail= (ImageButton)view.findViewById(R.id.imageButtonEmail);
  imageButtonTelefono= (ImageButton)view.findViewById(R.id.imageButtonTelefono);
  imageButtonWWW= (ImageButton)view.findViewById(R.id.imageButtonWWW);
  imageButtonMap= (ImageButton)view.findViewById(R.id.imageButtonMap);
  textView2= (TextView)view.findViewById(R.id.textView2);
  textView13= (TextView)view.findViewById(R.id.textView13);

}
}
   

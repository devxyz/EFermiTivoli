package it.gov.fermitivoli.layout;
import android.app.*;
import android.view.*;
import android.widget.*;
import android.webkit.*;
import it.gov.fermitivoli.R;
public class LayoutObjs_dialog_photos_details_xml{
  public final TextView textViewTitle;
  public final TextView textViewDescrizione;
  public final ScrollView scrollView;
  public final ImageView image;
  public final ImageButton buttonChiud;

public LayoutObjs_dialog_photos_details_xml(Fragment f){
  View view=f.getView();
    textViewTitle= (TextView)view.findViewById(R.id.textViewTitle);
  textViewDescrizione= (TextView)view.findViewById(R.id.textViewDescrizione);
  scrollView= (ScrollView)view.findViewById(R.id.scrollView);
  image= (ImageView)view.findViewById(R.id.image);
  buttonChiud= (ImageButton)view.findViewById(R.id.buttonChiud);
}
public LayoutObjs_dialog_photos_details_xml(Activity view){
    textViewTitle= (TextView)view.findViewById(R.id.textViewTitle);
  textViewDescrizione= (TextView)view.findViewById(R.id.textViewDescrizione);
  scrollView= (ScrollView)view.findViewById(R.id.scrollView);
  image= (ImageView)view.findViewById(R.id.image);
  buttonChiud= (ImageButton)view.findViewById(R.id.buttonChiud);

}
public LayoutObjs_dialog_photos_details_xml(View view){
    textViewTitle= (TextView)view.findViewById(R.id.textViewTitle);
  textViewDescrizione= (TextView)view.findViewById(R.id.textViewDescrizione);
  scrollView= (ScrollView)view.findViewById(R.id.scrollView);
  image= (ImageView)view.findViewById(R.id.image);
  buttonChiud= (ImageButton)view.findViewById(R.id.buttonChiud);

}
public LayoutObjs_dialog_photos_details_xml(Dialog view){
    textViewTitle= (TextView)view.findViewById(R.id.textViewTitle);
  textViewDescrizione= (TextView)view.findViewById(R.id.textViewDescrizione);
  scrollView= (ScrollView)view.findViewById(R.id.scrollView);
  image= (ImageView)view.findViewById(R.id.image);
  buttonChiud= (ImageButton)view.findViewById(R.id.buttonChiud);

}
}
   

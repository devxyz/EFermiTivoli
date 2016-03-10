package it.gov.fermitivoli.layout;
import android.app.*;
import android.view.*;
import android.widget.*;
import android.webkit.*;
import it.gov.fermitivoli.R;
public class LayoutObjs_fragment_cotral_fermate_xml{
  public final TextView textViewTitle;
  public final WebView textViewDescrizione;
  public final ProgressBar progressBar;

public LayoutObjs_fragment_cotral_fermate_xml(Fragment f){
  View view=f.getView();
    textViewTitle= (TextView)view.findViewById(R.id.textViewTitle);
  textViewDescrizione= (WebView)view.findViewById(R.id.textViewDescrizione);
  progressBar= (ProgressBar)view.findViewById(R.id.progressBar);
}
public LayoutObjs_fragment_cotral_fermate_xml(Activity view){
    textViewTitle= (TextView)view.findViewById(R.id.textViewTitle);
  textViewDescrizione= (WebView)view.findViewById(R.id.textViewDescrizione);
  progressBar= (ProgressBar)view.findViewById(R.id.progressBar);

}
public LayoutObjs_fragment_cotral_fermate_xml(View view){
    textViewTitle= (TextView)view.findViewById(R.id.textViewTitle);
  textViewDescrizione= (WebView)view.findViewById(R.id.textViewDescrizione);
  progressBar= (ProgressBar)view.findViewById(R.id.progressBar);

}
public LayoutObjs_fragment_cotral_fermate_xml(Dialog view){
    textViewTitle= (TextView)view.findViewById(R.id.textViewTitle);
  textViewDescrizione= (WebView)view.findViewById(R.id.textViewDescrizione);
  progressBar= (ProgressBar)view.findViewById(R.id.progressBar);

}
}
   

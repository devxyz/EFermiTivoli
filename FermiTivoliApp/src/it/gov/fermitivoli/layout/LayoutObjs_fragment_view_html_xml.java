package it.gov.fermitivoli.layout;
import android.app.*;
import android.view.*;
import android.widget.*;
import android.webkit.*;
import it.gov.fermitivoli.R;
public class LayoutObjs_fragment_view_html_xml{
  public final TextView textViewTitle;
  public final WebView textViewDescrizione;
  public final ImageButton buttonClose;
  public final ProgressBar progressBar7;

public LayoutObjs_fragment_view_html_xml(Fragment f){
  View view=f.getView();
    textViewTitle= (TextView)view.findViewById(R.id.textViewTitle);
  textViewDescrizione= (WebView)view.findViewById(R.id.textViewDescrizione);
  buttonClose= (ImageButton)view.findViewById(R.id.buttonClose);
  progressBar7= (ProgressBar)view.findViewById(R.id.progressBar7);
}
public LayoutObjs_fragment_view_html_xml(Activity view){
    textViewTitle= (TextView)view.findViewById(R.id.textViewTitle);
  textViewDescrizione= (WebView)view.findViewById(R.id.textViewDescrizione);
  buttonClose= (ImageButton)view.findViewById(R.id.buttonClose);
  progressBar7= (ProgressBar)view.findViewById(R.id.progressBar7);

}
public LayoutObjs_fragment_view_html_xml(View view){
    textViewTitle= (TextView)view.findViewById(R.id.textViewTitle);
  textViewDescrizione= (WebView)view.findViewById(R.id.textViewDescrizione);
  buttonClose= (ImageButton)view.findViewById(R.id.buttonClose);
  progressBar7= (ProgressBar)view.findViewById(R.id.progressBar7);

}
public LayoutObjs_fragment_view_html_xml(Dialog view){
    textViewTitle= (TextView)view.findViewById(R.id.textViewTitle);
  textViewDescrizione= (WebView)view.findViewById(R.id.textViewDescrizione);
  buttonClose= (ImageButton)view.findViewById(R.id.buttonClose);
  progressBar7= (ProgressBar)view.findViewById(R.id.progressBar7);

}
}
   

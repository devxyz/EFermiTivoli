package it.gov.fermitivoli.layout;
import android.app.*;
import android.view.*;
import android.widget.*;
import android.webkit.*;
import it.gov.fermitivoli.R;
public class LayoutObjs_fragment_webpage_embedded_xml{
  public final TextView messaggio;
  public final ProgressBar progressBar3;
  public final WebView webView;

public LayoutObjs_fragment_webpage_embedded_xml(Fragment f){
  View view=f.getView();
    messaggio= (TextView)view.findViewById(R.id.messaggio);
  progressBar3= (ProgressBar)view.findViewById(R.id.progressBar3);
  webView= (WebView)view.findViewById(R.id.webView);
}
public LayoutObjs_fragment_webpage_embedded_xml(Activity view){
    messaggio= (TextView)view.findViewById(R.id.messaggio);
  progressBar3= (ProgressBar)view.findViewById(R.id.progressBar3);
  webView= (WebView)view.findViewById(R.id.webView);

}
public LayoutObjs_fragment_webpage_embedded_xml(View view){
    messaggio= (TextView)view.findViewById(R.id.messaggio);
  progressBar3= (ProgressBar)view.findViewById(R.id.progressBar3);
  webView= (WebView)view.findViewById(R.id.webView);

}
public LayoutObjs_fragment_webpage_embedded_xml(Dialog view){
    messaggio= (TextView)view.findViewById(R.id.messaggio);
  progressBar3= (ProgressBar)view.findViewById(R.id.progressBar3);
  webView= (WebView)view.findViewById(R.id.webView);

}
}
   

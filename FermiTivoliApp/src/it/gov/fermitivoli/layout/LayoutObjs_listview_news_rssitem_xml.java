package it.gov.fermitivoli.layout;
import android.app.*;
import android.view.*;
import android.widget.*;
import android.webkit.*;
import it.gov.fermitivoli.R;
public class LayoutObjs_listview_news_rssitem_xml{
  public final ImageView arrow;
  public final TextView title;
  public final TextView date;

public LayoutObjs_listview_news_rssitem_xml(Fragment f){
  View view=f.getView();
    arrow= (ImageView)view.findViewById(R.id.arrow);
  title= (TextView)view.findViewById(R.id.title);
  date= (TextView)view.findViewById(R.id.date);
}
public LayoutObjs_listview_news_rssitem_xml(Activity view){
    arrow= (ImageView)view.findViewById(R.id.arrow);
  title= (TextView)view.findViewById(R.id.title);
  date= (TextView)view.findViewById(R.id.date);

}
public LayoutObjs_listview_news_rssitem_xml(View view){
    arrow= (ImageView)view.findViewById(R.id.arrow);
  title= (TextView)view.findViewById(R.id.title);
  date= (TextView)view.findViewById(R.id.date);

}
public LayoutObjs_listview_news_rssitem_xml(Dialog view){
    arrow= (ImageView)view.findViewById(R.id.arrow);
  title= (TextView)view.findViewById(R.id.title);
  date= (TextView)view.findViewById(R.id.date);

}
}
   

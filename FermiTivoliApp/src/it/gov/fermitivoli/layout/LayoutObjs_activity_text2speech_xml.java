package it.gov.fermitivoli.layout;
import android.app.*;
import android.view.*;
import android.widget.*;
import android.webkit.*;
import it.gov.fermitivoli.R;
public class LayoutObjs_activity_text2speech_xml{
  public final TextView txt;
  public final EditText sentence;
  public final TextView txtPitch;
  public final SeekBar seekPitch;
  public final TextView txtSpeed;
  public final SeekBar seekSpeed;
  public final ImageView speechImg;

public LayoutObjs_activity_text2speech_xml(Fragment f){
  View view=f.getView();
    txt= (TextView)view.findViewById(R.id.txt);
  sentence= (EditText)view.findViewById(R.id.sentence);
  txtPitch= (TextView)view.findViewById(R.id.txtPitch);
  seekPitch= (SeekBar)view.findViewById(R.id.seekPitch);
  txtSpeed= (TextView)view.findViewById(R.id.txtSpeed);
  seekSpeed= (SeekBar)view.findViewById(R.id.seekSpeed);
  speechImg= (ImageView)view.findViewById(R.id.speechImg);
}
public LayoutObjs_activity_text2speech_xml(Activity view){
    txt= (TextView)view.findViewById(R.id.txt);
  sentence= (EditText)view.findViewById(R.id.sentence);
  txtPitch= (TextView)view.findViewById(R.id.txtPitch);
  seekPitch= (SeekBar)view.findViewById(R.id.seekPitch);
  txtSpeed= (TextView)view.findViewById(R.id.txtSpeed);
  seekSpeed= (SeekBar)view.findViewById(R.id.seekSpeed);
  speechImg= (ImageView)view.findViewById(R.id.speechImg);

}
public LayoutObjs_activity_text2speech_xml(View view){
    txt= (TextView)view.findViewById(R.id.txt);
  sentence= (EditText)view.findViewById(R.id.sentence);
  txtPitch= (TextView)view.findViewById(R.id.txtPitch);
  seekPitch= (SeekBar)view.findViewById(R.id.seekPitch);
  txtSpeed= (TextView)view.findViewById(R.id.txtSpeed);
  seekSpeed= (SeekBar)view.findViewById(R.id.seekSpeed);
  speechImg= (ImageView)view.findViewById(R.id.speechImg);

}
public LayoutObjs_activity_text2speech_xml(Dialog view){
    txt= (TextView)view.findViewById(R.id.txt);
  sentence= (EditText)view.findViewById(R.id.sentence);
  txtPitch= (TextView)view.findViewById(R.id.txtPitch);
  seekPitch= (SeekBar)view.findViewById(R.id.seekPitch);
  txtSpeed= (TextView)view.findViewById(R.id.txtSpeed);
  seekSpeed= (SeekBar)view.findViewById(R.id.seekSpeed);
  speechImg= (ImageView)view.findViewById(R.id.speechImg);

}
}
   

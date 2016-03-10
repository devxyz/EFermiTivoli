package it.gov.fermitivoli.dialog;

import android.graphics.Point;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import it.gov.fermitivoli.R;
import it.gov.fermitivoli.cache.UrlImageLoader;
import it.gov.fermitivoli.api.AbstractDialog;
import it.gov.fermitivoli.api.AbstractFragment;
import it.gov.fermitivoli.layout.LayoutObjs_dialog_photos_details_xml;
import it.gov.fermitivoli.model.C_NormalizedURL;
import it.gov.fermitivoli.listener.OnClickListenerViewErrorCheck;
import it.gov.fermitivoli.model.PhotoFermiDescription;
import it.gov.fermitivoli.util.ScreenUtil;

/**
 * Created by stefano on 25/02/15.
 */
public class PhotoFermiDetailsDialog extends AbstractDialog {
    public UrlImageLoader imageLoader;
    private LayoutObjs_dialog_photos_details_xml LAYOUT_OBJs;

    public PhotoFermiDetailsDialog(final AbstractFragment fragment, final PhotoFermiDescription pp) {
        super(fragment.getMainActivity());
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_photos_details);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        //**************************
        LAYOUT_OBJs = new LayoutObjs_dialog_photos_details_xml(this);
        //**************************
        Point size = ScreenUtil.getSize(fragment.getActivity());
        imageLoader = new UrlImageLoader(fragment, size.x, size.y, R.drawable.clessidra_30x30);
        //imageLoader.clearCache();

        if (pp != null) {
            LAYOUT_OBJs.textViewTitle.setText(pp.getCategory());
            LAYOUT_OBJs.textViewDescrizione.setText(pp.getDescription());
            LAYOUT_OBJs.buttonChiud.setOnClickListener(new OnClickListenerViewErrorCheck(fragment.getActivity()) {
                @Override
                public void onClickImpl(View v) {
                    dismiss();
                }
            });


            ImageView iv = LAYOUT_OBJs.image;
            iv.setImageDrawable(fragment.getActivity().getResources().getDrawable(R.drawable.stub));
            String fullimageLink = pp.getImageUrl();
            if (fullimageLink != null && fullimageLink.trim().length() > 0) {
                imageLoader.displayImage(new C_NormalizedURL(fullimageLink), iv);
            } else {

            }


        }
        //ScreenUtil.fullWidthScreen(this);


    }




}

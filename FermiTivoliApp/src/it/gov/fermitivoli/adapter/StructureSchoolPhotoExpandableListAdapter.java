package it.gov.fermitivoli.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import it.gov.fermitivoli.R;
import it.gov.fermitivoli.cache.UrlImageLoader;
import it.gov.fermitivoli.api.AbstractFragment;
import it.gov.fermitivoli.model.C_NormalizedURL;
import it.gov.fermitivoli.model.PhotoFermiDescription;
import it.gov.fermitivoli.util.ScreenUtil;

import java.util.*;

/**
 * Created by stefano on 04/03/15.
 */

public class StructureSchoolPhotoExpandableListAdapter extends BaseExpandableListAdapter {

    //_images category-list
    private final Map<String, List<PhotoFermiDescription>> _images;
    public UrlImageLoader imageLoader;
    private Activity activity;
    private List<String> _listDataHeader;
    private LayoutObjsGroup LAYOUT_OBJsGroup;   //***************************
    private LayoutObjsItem LAYOUT_OBJsItem;   //***************************

    public StructureSchoolPhotoExpandableListAdapter(AbstractFragment e, List<PhotoFermiDescription> images) {
        this._images = new HashMap<String, List<PhotoFermiDescription>>();
        for (PhotoFermiDescription im : images) {
            String c = im.getCategory();
            if (!this._images.containsKey(c)) {
                this._images.put(c, new ArrayList<PhotoFermiDescription>());
            }
            this._images.get(c).add(im);
        }

        for (List<PhotoFermiDescription> l : _images.values()) {
            Collections.sort(l, new Comparator<PhotoFermiDescription>() {
                @Override
                public int compare(PhotoFermiDescription a, PhotoFermiDescription b) {
                    int i = a.category.toLowerCase().compareTo(b.category.toLowerCase());
                    if (i != 0)
                        return i;
                    return a.description.toLowerCase().compareTo(b.description.toLowerCase());
                }
            });
        }
        _listDataHeader = new ArrayList<String>(_images.keySet());
        Collections.sort(_listDataHeader);

        activity = e.getActivity();
        Point size = ScreenUtil.getSize(activity);
        imageLoader = new UrlImageLoader(e, size.x / 3, size.y/4, R.drawable.clessidra_30x30);
    }


    @Override
    public PhotoFermiDescription getChild(int groupPosition, int childPosititon) {
        return this._images.get(this._listDataHeader.get(groupPosition)).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final PhotoFermiDescription data = getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.listview_photolist_item, null);
        }

        LAYOUT_OBJsItem = new LayoutObjsItem(convertView);
        // Initialize the views in the layout
        ImageView iv = LAYOUT_OBJsItem.thumb;
        TextView tvTitle = LAYOUT_OBJsItem.title;

        // Set the views in the layout
        iv.setImageDrawable(activity.getResources().getDrawable(R.drawable.no_image_small));
        String fullimageLink = data.imageUrl;
        if (fullimageLink != null && fullimageLink.trim().length() > 0)
            imageLoader.displayImage(new C_NormalizedURL(fullimageLink), iv);

        tvTitle.setText(data.getDescription());

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._images.get(this._listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.listview_photolist_group, null);
        }

        LAYOUT_OBJsGroup = new LayoutObjsGroup(convertView);
        LAYOUT_OBJsGroup.title.setText(headerTitle);

        final PhotoFermiDescription data = getChild(groupPosition, 0);

        ImageView iv = LAYOUT_OBJsGroup.thumb;
        //iv.setImageDrawable(activity.getResources().getDrawable(R.drawable.no_image_small));
        String fullimageLink = data.imageUrl;

        //if (fullimageLink != null && fullimageLink.trim().length() > 0)imageLoader.displayImage(new C_NormalizedURL(fullimageLink), iv, 200, 100);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    //DECLARATION
    //***************************
    private class LayoutObjsGroup {
        protected final ImageView thumb;
        protected final TextView title;

        private LayoutObjsGroup(Fragment f) {
            View view = f.getView();
            thumb = (ImageView) view.findViewById(R.id.thumb);
            title = (TextView) view.findViewById(R.id.title);
        }

        private LayoutObjsGroup(Activity view) {
            thumb = (ImageView) view.findViewById(R.id.thumb);
            title = (TextView) view.findViewById(R.id.title);

        }

        private LayoutObjsGroup(View view) {
            thumb = (ImageView) view.findViewById(R.id.thumb);
            title = (TextView) view.findViewById(R.id.title);

        }

        private LayoutObjsGroup(Dialog view) {
            thumb = (ImageView) view.findViewById(R.id.thumb);
            title = (TextView) view.findViewById(R.id.title);

        }
    }

    //DECLARATION
    //***************************
    private class LayoutObjsItem {
        protected final ImageView thumb;
        protected final TextView title;
        protected final ImageView arrow;

        private LayoutObjsItem(Fragment f) {
            View view = f.getView();
            thumb = (ImageView) view.findViewById(R.id.thumb);
            title = (TextView) view.findViewById(R.id.title);
            arrow = (ImageView) view.findViewById(R.id.arrow);
        }

        private LayoutObjsItem(Activity view) {
            thumb = (ImageView) view.findViewById(R.id.thumb);
            title = (TextView) view.findViewById(R.id.title);
            arrow = (ImageView) view.findViewById(R.id.arrow);

        }

        private LayoutObjsItem(View view) {
            thumb = (ImageView) view.findViewById(R.id.thumb);
            title = (TextView) view.findViewById(R.id.title);
            arrow = (ImageView) view.findViewById(R.id.arrow);

        }

        private LayoutObjsItem(Dialog view) {
            thumb = (ImageView) view.findViewById(R.id.thumb);
            title = (TextView) view.findViewById(R.id.title);
            arrow = (ImageView) view.findViewById(R.id.arrow);

        }
    }

}

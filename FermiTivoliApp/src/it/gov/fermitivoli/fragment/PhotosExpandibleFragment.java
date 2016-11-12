package it.gov.fermitivoli.fragment;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import it.gov.fermitivoli.R;
import it.gov.fermitivoli.adapter.StructureSchoolPhotoExpandableListAdapter;
import it.gov.fermitivoli.api.AbstractFragment;
import it.gov.fermitivoli.dialog.PhotoFermiDetailsDialog;
import it.gov.fermitivoli.layout.LayoutObjs_fragment_photos_expandable_xml;
import it.gov.fermitivoli.model.PhotoFermiDescription;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PhotosExpandibleFragment extends AbstractFragment {
    private LayoutObjs_fragment_photos_expandable_xml LAYOUT_OBJs;   //***************************

    public PhotosExpandibleFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_photos_expandable, container, false);
        //ON CREATE method
        //**************************
        LAYOUT_OBJs = new LayoutObjs_fragment_photos_expandable_xml(rootView);
        //**************************

        TypedArray navMenuInfo = getResources().obtainTypedArray(R.array.gallery_imageurl);
        if (navMenuInfo.length() % 3 != 0) {
            throw new IllegalArgumentException("Dimensioni del vettore delle immagini non corretta: " + navMenuInfo.length() + ". Deve essere multipla di 3");
        }

        final ArrayList<PhotoFermiDescription> ff = new ArrayList<PhotoFermiDescription>();
        for (int i = 0; i < navMenuInfo.length(); ) {
            String cat = navMenuInfo.getString(i);
            i++;
            String desc = navMenuInfo.getString(i);
            i++;
            String imgUrl = navMenuInfo.getString(i);
            i++;
            ff.add(new PhotoFermiDescription(cat, desc, imgUrl));
        }
        //=============================================================
        Collections.sort(ff, new Comparator<PhotoFermiDescription>() {
            @Override
            public int compare(PhotoFermiDescription a, PhotoFermiDescription b) {
                int i = a.getCategory().toUpperCase().compareTo(b.getCategory().toUpperCase());
                if (i != 0)
                    return i;
                return a.getDescription().toUpperCase().compareTo(b.getDescription().toUpperCase());
            }
        });


        final StructureSchoolPhotoExpandableListAdapter adapter = new StructureSchoolPhotoExpandableListAdapter(this, ff);
        LAYOUT_OBJs.listView2.setAdapter(adapter);

        LAYOUT_OBJs.listView2.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                PhotoFermiDescription val = adapter.getChild(groupPosition, childPosition);
                PhotoFermiDetailsDialog d = new PhotoFermiDetailsDialog(PhotosExpandibleFragment.this, val);
                d.show();
                return false;
            }
        });

        LAYOUT_OBJs.listView2.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                //collapse other
                int groupCount = adapter.getGroupCount();
                for (int i = 0; i < groupCount; i++) {
                    if (i != groupPosition) {
                        if (LAYOUT_OBJs.listView2.isGroupExpanded(groupPosition))
                            LAYOUT_OBJs.listView2.collapseGroup(i);
                    }
                }
            }
        });

        LAYOUT_OBJs.listView2.expandGroup(0);

        return rootView;
    }

}

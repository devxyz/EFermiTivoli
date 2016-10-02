package it.gov.fermitivoli.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import it.gov.fermitivoli.R;
import it.gov.fermitivoli.layout.LayoutObjs_listview_item_menu_generale_liv1_xml;
import it.gov.fermitivoli.layout.LayoutObjs_listview_item_menu_generale_liv2_xml;
import it.gov.fermitivoli.model.menu.DataMenuInfo;
import it.gov.fermitivoli.model.menu.DataMenuInfoType;
import it.gov.fermitivoli.model.menu.DataMenuLoader;

import java.util.ArrayList;
import java.util.List;

public class MainMenuExpandibleListAdapter extends BaseAdapter implements IMenuListAdapter {

    private final Context context;
    private final ArrayList<DataMenuInfo> currentVisibleItems;
    private final ArrayList<DataMenuInfo> original;
    private DataMenuInfo lastOpen = null;
    private LayoutInflater mInflater;

    public DataMenuInfo getDataMenuInfoByMenuID(int id){
        for (DataMenuInfo x : original) {
            if (x.getMenuID()==id)return x;
        }
        return null;
    }

    public MainMenuExpandibleListAdapter(Context context, List<DataMenuInfo> navDrawerItems) {

        mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        //=============================================================
        this.context = context;
        this.original = new ArrayList<>(navDrawerItems);

        currentVisibleItems = new ArrayList<>();
        for (DataMenuInfo x : original) {
            if (!x.isSubItem()) {
                currentVisibleItems.add(x);
            }
        }
    }

    public static MainMenuExpandibleListAdapter getInstanceForMainMenu(Context context) {
        final TypedArray navMenuInfo = context.getResources().obtainTypedArray(R.array.menu_principale);
        final List<DataMenuInfo> ff = DataMenuLoader.load(navMenuInfo);
        return new MainMenuExpandibleListAdapter(context, ff);

    }

    public ArrayList<DataMenuInfo> getOriginal() {
        return new ArrayList<>(original);
    }

    public ArrayList<DataMenuInfo> getCurrentVisibleItems() {
        return new ArrayList<>(currentVisibleItems);
    }

    public void switchMenu(DataMenuInfo d) {
        if (d.isSubItem()) return;
        if (lastOpen == null) {
            open(d);
            return;
        }

        if (lastOpen.equals(d)) {
            close(d);
        } else
            open(d);
    }

    public void open(DataMenuInfo d) {
        if (d.isSubItem()) return;

        lastOpen = d;

        currentVisibleItems.clear();
        boolean addSub = false;
        for (DataMenuInfo x : original) {
            if (!x.isSubItem()) {
                currentVisibleItems.add(x);
                if (d.equals(x)) {
                    addSub = true;
                } else
                    addSub = false;
            } else {
                if (addSub) {
                    currentVisibleItems.add(x);
                }
            }
        }

        notifyDataSetChanged();
    }

    public void close(DataMenuInfo d) {
        if (d.isSubItem()) return;
        if (lastOpen != null && lastOpen.equals(d))
            closeAll();

    }

    public void closeAll() {
        lastOpen = null;
        currentVisibleItems.clear();
        for (DataMenuInfo x : original) {
            if (!x.isSubItem()) {
                currentVisibleItems.add(x);
            }
        }

        notifyDataSetChanged();
    }

    public DataMenuInfo searchByMenuID(int menuID) {
        for (DataMenuInfo m : currentVisibleItems) {
            if (m.getMenuID() == menuID) return m;
        }
        return null;
    }

    @Override
    public DataMenuInfo getDataMenuInfo(int pos) {
        return currentVisibleItems.get(pos);
    }

    @Override
    public int positionByMenu(DataMenuInfo d) {
        return currentVisibleItems.indexOf(d);
    }

    @Override
    public int getCount() {
        return currentVisibleItems.size();
    }

    @Override
    public Object getItem(int position) {
        return currentVisibleItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final DataMenuInfo item = currentVisibleItems.get(position);

        convertView=null;
        if (item.isSubItem()) {
            if (convertView == null || !convertView.getTag().equals("subitem")) {
                convertView = mInflater.inflate(R.layout.listview_item_menu_generale_liv2, null);
                convertView.setTag("subitem");

                //ON CREATE method
                //**************************

                LayoutObjs_listview_item_menu_generale_liv2_xml LAYOUT_OBJs_liv1;   //***************************

                LAYOUT_OBJs_liv1 = new LayoutObjs_listview_item_menu_generale_liv2_xml(convertView);
                //**************************


                ImageView imgIcon = LAYOUT_OBJs_liv1.icon;
                TextView txtTitle = LAYOUT_OBJs_liv1.title;
                TextView txtCount = LAYOUT_OBJs_liv1.counter;
                imgIcon.setImageResource(item.getImageId());
                txtTitle.setText(item.getMenuLabel());

                txtCount.setVisibility(View.GONE);

            }
        } else {
            if (convertView == null || !convertView.getTag().equals("item")) {
                convertView = mInflater.inflate(R.layout.listview_item_menu_generale_liv1, null);
                convertView.setTag("item");

                //ON CREATE method
                //**************************
                LayoutObjs_listview_item_menu_generale_liv1_xml LAYOUT_OBJs_liv1;
                LAYOUT_OBJs_liv1 = new LayoutObjs_listview_item_menu_generale_liv1_xml(convertView);
                //**************************


                ImageView imgIcon = LAYOUT_OBJs_liv1.icon;
                TextView txtTitle = LAYOUT_OBJs_liv1.title;
                TextView txtCount = LAYOUT_OBJs_liv1.counter;
                ImageView imgArrow = LAYOUT_OBJs_liv1.imageView3;

                if (!item.isSubItem()) {
                    if (item.type() == DataMenuInfoType.LABEL_ONLY) {

                        imgArrow.setVisibility(View.VISIBLE);
                    } else
                        imgArrow.setVisibility(View.GONE);
                }

                imgIcon.setImageResource(item.getImageId());
                txtTitle.setText(item.getMenuLabel());

                txtCount.setVisibility(View.GONE);

            }
        }


        return convertView;
    }


}

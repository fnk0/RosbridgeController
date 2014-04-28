package com.gabilheri.rosbridgecontroller.app.adapters;

import android.app.Activity;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import com.gabilheri.rosbridgecontroller.app.R;

import java.util.zip.Inflater;

/**
 * Created by marcus on 4/27/14.
 * @author Marcus Gabilheri
 * @version 1
 * @since April, 2014
 */
public class RobotsExpandableAdapter extends BaseExpandableListAdapter {

    private final SparseArray<ListRowGroup> groups;
    public LayoutInflater inflater;
    public Activity activity;

    public RobotsExpandableAdapter(Activity act, SparseArray<ListRowGroup> groups) {
        activity = act;
        this.groups = groups;
        inflater = act.getLayoutInflater();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groups.get(groupPosition).children.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final String children = (String) getChild(groupPosition, childPosition);
        TextView textTitle;
        TextView textDescription;

        if(childPosition == 4) {
            convertView = inflater.inflate(R.layout.listrow_last_child, null);
        } else {
            convertView = inflater.inflate(R.layout.listrow_details, null);
        }

        textTitle = (TextView) convertView.findViewById(R.id.titleView);
        textDescription = (TextView) convertView.findViewById(R.id.descriptionView);
        textTitle.setText(getChildString(childPosition));
        textDescription.setText(children);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return groups.get(groupPosition).children.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.listrow_group, null);
        }
        ListRowGroup group = (ListRowGroup) getGroup(groupPosition);
        TextView title = (TextView) convertView.findViewById(R.id.textView1);
        title.setText(group.robotName);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private String getChildString(int position) {
        switch (position) {
            case 0:
                return "URL: ";
            case 1:
                return "Port: ";
            case 2:
                return "Speed Details";
            case 3:
                return "Angular Speed Details";
            case 4:
                return "Select Robot";
        }
        return "";
    }
}

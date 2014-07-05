package com.ngocketit.saovietcrm.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ngocketit.saovietcrm.R;
import com.ngocketit.saovietcrm.model.ListMenuItem;

public class HomeMenuListAdapter extends ArrayAdapter<ListMenuItem> {
	
	List<ListMenuItem> mItems;

	public HomeMenuListAdapter(Context context, int resource,
			List<ListMenuItem> objects) {
		super(context, resource, objects);
		
		mItems = objects;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
        ListMenuItem item = mItems.get(position);

        // We don't have recycled view, just create a fresh one
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_home, null);
            
            TextView txtTitle = (TextView)convertView.findViewById(R.id.txtTitle);
            ImageView imgIcon = (ImageView)convertView.findViewById(R.id.imgIcon);

            viewHolder = new ViewHolder(txtTitle, imgIcon);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.txtTitle.setText(item.titleResId);
        viewHolder.imgIcon.setImageResource(item.iconResId);
        
        return convertView;
	}
	
	private class ViewHolder {
		public TextView txtTitle;
		public ImageView imgIcon;

		public ViewHolder(TextView txtTitle, ImageView imgIcon) {
			this.txtTitle = txtTitle;
			this.imgIcon = imgIcon;
		}
	}
}

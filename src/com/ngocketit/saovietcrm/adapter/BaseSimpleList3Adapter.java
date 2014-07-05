package com.ngocketit.saovietcrm.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.TextView;

import com.ngocketit.saovietcrm.R;
import com.ngocketit.saovietcrm.view.BaseSectionHeaderListItemView;

public class BaseSimpleList3Adapter extends BaseSectionHeaderCursorAdapter {
	public BaseSimpleList3Adapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int flags) {
		super(context, layout, c, from, to, flags);
	}

	@Override
	protected View createNewItemView(Context context) {
		BaseSectionHeaderListItemView view = new BaseSectionHeaderListItemView(context, null, R.layout.list_item_simple_3);
		view.setTag(new ViewHolder(view));
		
		return view;
	}
	
	@Override
	protected void setUpViewHolder(BaseSectionHeaderViewHolder viewHolder, Cursor cursor,
			boolean headerValueChanged, Object[] headerVals) {
		super.setUpViewHolder(viewHolder, cursor, headerValueChanged, headerVals);
	}
	
	protected class ViewHolder extends BaseSectionHeaderViewHolderWithCounter {
		public TextView text1;
		public TextView text2;
		public TextView text3;
		
		public ViewHolder(View view) {
			super(view);

			text1 = (TextView)view.findViewById(android.R.id.text1);
			text2 = (TextView)view.findViewById(android.R.id.text2);
			text3 = (TextView)view.findViewById(R.id.text3);
		}
	}
}

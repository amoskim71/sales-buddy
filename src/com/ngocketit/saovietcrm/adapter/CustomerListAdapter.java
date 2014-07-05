package com.ngocketit.saovietcrm.adapter;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ngocketit.saovietcrm.R;
import com.ngocketit.saovietcrm.database.CustomerTable;
import com.ngocketit.saovietcrm.database.OrderTable;
import com.ngocketit.saovietcrm.database.SalingLineTable;
import com.ngocketit.saovietcrm.util.CursorUtils;
import com.ngocketit.saovietcrm.util.GeneralUtils;
import com.ngocketit.saovietcrm.view.BaseSectionHeaderListItemView;

public class CustomerListAdapter extends BaseSectionHeaderCursorAdapter {
	public CustomerListAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int flags) {
		super(context, layout, c, from, to, flags);
	}

	@Override
	protected String getSectionHeaderColumn() {
		return SalingLineTable.COLUMN_NAME;
	}
	
	@Override
	protected View createNewItemView(Context context) {
		BaseSectionHeaderListItemView view = new BaseSectionHeaderListItemView(context, null, R.layout.list_item_customer);
		view.setTag(new ViewHolder(view));
		
		return view;
	}
	
	@Override
	protected Object[] getHeaderAggregateValues(String currentHeaderVal, int startingPos, Cursor cursor) {
		int counter = 1;
		String tmpVal = null;
		
		// Move the cursor to the starting position of the section
		cursor.moveToPosition(startingPos);
		float totalOrderVal = CursorUtils.getRecordFloatValue(cursor, "orders_value");

		while (cursor.moveToNext()) {
			tmpVal = getHeaderValue(cursor, -1); 

			if (currentHeaderVal.equals(tmpVal) || (TextUtils.isEmpty(currentHeaderVal) && TextUtils.isEmpty(tmpVal))) {
				counter++;
				totalOrderVal += CursorUtils.getRecordFloatValue(cursor, "orders_value");
			}
			else {
				break;
			}
		}
		
		// Move back to the current position after calculation
		cursor.moveToPosition(startingPos);
		
		return new Object[] {
				counter,
				totalOrderVal
		};
	}
	
	@Override
	protected void setUpViewHolder(BaseSectionHeaderViewHolder viewHolder, Cursor cursor,
			boolean headerValueChanged, Object[] headerVals) {
		super.setUpViewHolder(viewHolder, cursor, headerValueChanged, headerVals);
		
		int orderCount = CursorUtils.getRecordIntValue(cursor, "orders_count");
		float orderValue = CursorUtils.getRecordFloatValue(cursor, "orders_value");
		String orderInfo = orderCount + " - " + GeneralUtils.formatCurrency(orderValue);

		ViewHolder holder = (ViewHolder)viewHolder;
		
		// Header values are only available if header value is changed
		if (headerValueChanged) {
			int counter = (Integer)headerVals[0];
			String lineName = CursorUtils.getRecordStringValue(cursor, SalingLineTable.COLUMN_NAME);
			float totalOrderVal = (Float)headerVals[1];

			if (lineName == null) {
				lineName = getContext().getString(R.string.unlined);
			}

			holder.sectionHeaderView1.setText(lineName + " (" + counter + ")");
			holder.sectionHeaderView2.setText(String.valueOf(GeneralUtils.formatCurrency(totalOrderVal)));
		}

		holder.ratingView.setRating(CursorUtils.getRecordFloatValue(cursor, CustomerTable.COLUMN_RATE));
		holder.mainTextView.setText(CursorUtils.getRecordStringValue(cursor, CustomerTable.COLUMN_SHOP_NAME));
		holder.subTextView.setText(orderInfo);
	}
	
	private class ViewHolder extends BaseSectionHeaderViewHolderWithCounter {
		public RatingBar ratingView;
		public TextView mainTextView;
		public TextView subTextView;
		
		public ViewHolder(View view) {
			super(view);

			ratingView = (RatingBar)view.findViewById(R.id.rating);
			mainTextView = (TextView)view.findViewById(android.R.id.text1);
			subTextView = (TextView)view.findViewById(android.R.id.text2);
		}
	}
}

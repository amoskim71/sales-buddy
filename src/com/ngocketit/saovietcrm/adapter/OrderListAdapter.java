package com.ngocketit.saovietcrm.adapter;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.ngocketit.saovietcrm.app.SaovietCRMApp;
import com.ngocketit.saovietcrm.common.DateTimeBreakdown;
import com.ngocketit.saovietcrm.database.BaseTable;
import com.ngocketit.saovietcrm.database.CustomerTable;
import com.ngocketit.saovietcrm.database.OrderTable;
import com.ngocketit.saovietcrm.util.CursorUtils;
import com.ngocketit.saovietcrm.util.GeneralUtils;

public class OrderListAdapter extends BaseSimpleList3Adapter {
	// Default breakdown
	private int mBreakdown = DateTimeBreakdown.DAILY;
	private GregorianCalendar mCalendar;
	
	public OrderListAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int flags) {
		super(context, layout, c, from, to, flags);
	}
	
	public void setBreakdown(int breakdown) {
		if (breakdown != mBreakdown) {
			mBreakdown = breakdown;
			notifyDataSetChanged();
		}
	}

	@Override
	protected String getSectionHeaderColumn() {
		return BaseTable.COLUMN_CREATED_DATE;
	}
	
	private String formatCreatedDate(String raw) {
		String[] parts = raw.split("-");
		
		switch (mBreakdown) {
		case DateTimeBreakdown.DAILY:
			return parts[2] + "-" + parts[1] + "-" + parts[0];
			
		case DateTimeBreakdown.MONTHLY:
			return parts[1] + "-" + parts[0];
			
		// Yearly & weekly
		case DateTimeBreakdown.WEEKLY:
		case DateTimeBreakdown.YEARLY:
		default:
			return raw;
		}
	}
	
	@Override
	protected String getHeaderValue(Cursor cursor, int position) {
		// Header value will be in format: 2014-02-23
		String headerVal = super.getHeaderValue(cursor, position);
		Activity hostActivity = (Activity)getContext();
		SaovietCRMApp app = (SaovietCRMApp)hostActivity.getApplication();
		
		switch (mBreakdown) {
		case DateTimeBreakdown.YEARLY:
			headerVal = headerVal.substring(0, 4);
			break;
			
		case DateTimeBreakdown.WEEKLY:
			if (mCalendar == null) {
				mCalendar = new GregorianCalendar(app.getCurrentUserLocale());
			}
			String[] parts = headerVal.split("-");
			mCalendar.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) - 1, Integer.parseInt(parts[2]));
			int weekOfYear = mCalendar.get(Calendar.WEEK_OF_YEAR);
			// Header value will be in format: W12-2014
			headerVal = "W" + weekOfYear + "-" + parts[0];
			break;

		case DateTimeBreakdown.MONTHLY:
			headerVal = headerVal.substring(0, 7);
			break;
			
		case DateTimeBreakdown.DAILY:
		default:
			break;
		}
		
		return headerVal;
	}
	
	@Override
	protected Object[] getHeaderAggregateValues(String currentHeaderVal, int startingPos, Cursor cursor) {
		int counter = 1;
		String tmpVal = null;
		
		// Move the cursor to the starting position of the section
		cursor.moveToPosition(startingPos);
		float totalOrderVal = CursorUtils.getRecordFloatValue(cursor, OrderTable.COLUMN_TOTAL_PRICE);

		while (cursor.moveToNext()) {
			tmpVal = getHeaderValue(cursor, -1); 

			if (currentHeaderVal.equals(tmpVal) || (TextUtils.isEmpty(currentHeaderVal) && TextUtils.isEmpty(tmpVal))) {
				counter++;
				totalOrderVal += CursorUtils.getRecordFloatValue(cursor, OrderTable.COLUMN_TOTAL_PRICE);
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
		
		float totalPrice = CursorUtils.getRecordFloatValue(cursor, OrderTable.COLUMN_TOTAL_PRICE);
		
		ViewHolder holder = (ViewHolder)viewHolder;
		
		if (headerValueChanged) {
			int counter = (Integer)headerVals[0];
			
			// Cursor has been pointing to current position already
			String createdDate = getHeaderValue(cursor, -1);
			float orderVal = (Float)headerVals[1];

			holder.sectionHeaderView1.setText(formatCreatedDate(createdDate) + " (" + counter + ")");
			holder.sectionHeaderView2.setText(GeneralUtils.formatCurrency(orderVal));
		}

		holder.text1.setText(CursorUtils.getRecordStringValue(cursor, CustomerTable.COLUMN_SHOP_NAME));
		holder.text2.setText(CursorUtils.getRecordStringValue(cursor, OrderTable.COLUMN_QUANTITY));
		holder.text3.setText(GeneralUtils.formatCurrency(totalPrice));
	}
}

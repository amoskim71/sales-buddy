package com.ngocketit.saovietcrm.adapter;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ngocketit.saovietcrm.R;
import com.ngocketit.saovietcrm.util.CursorUtils;

public class BaseSectionHeaderCursorAdapter extends BaseCursorAdapter {

	public BaseSectionHeaderCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int flags) {
		super(context, layout, c, from, to, flags);
	}
	
	/**
	 * Get the name of the database column in cursor option for the section header
	 * 
	 * @return Name of section header column
	 */
	protected String getSectionHeaderColumn() {
		return null;
	}
	
	/**
	 * Determine if the current header value has been changed comparing to prev & next values
	 * 
	 * @param currentValue Header value of current item
	 * @param prevValue Header value of previous item
	 * @param nextValue Header value of next item
	 * @return True of header value is changed and a new section header needs to be displayed
	 */
	protected boolean isHeaderValueChanged(String currentValue, String prevValue) {
		return currentValue != null && !currentValue.equals(prevValue);
	}
	
	
	/**
	 * Create a new item view when the view is not recycled
	 * 
	 * @param context Context
	 * @return Item view object
	 */
	protected View createNewItemView(Context context) {
		return new TextView(context);
	}
	
	/**
	 * Set up the view holder and assign values to child view items. Subclasses should
	 * call this method of parent class to show/hide header view based on headerValueChanged.
	 * 
	 * @param viewHolder Item view holder
	 * @param cursor Cursor object that can be used to get other values
	 * @param headerValueChanged Whether the header value has been changed
	 */
	protected void setUpViewHolder(BaseSectionHeaderViewHolder viewHolder, Cursor cursor, boolean headerValueChanged, Object[] aggregateValues) {
		viewHolder.sectionHeaderContainerView.setVisibility(headerValueChanged ? View.VISIBLE : View.GONE);
	}
	
	
	/**
	 * Get the header value
	 * 
	 * @param cursor Cursor object
	 * @param position Position of the current item
	 * @return Header value
	 */
	protected String getHeaderValue(Cursor cursor, int position) {
		if (position >= 0) {
			cursor.moveToPosition(position);
		}
		String headerCol = getSectionHeaderColumn();
		
		return CursorUtils.getRecordStringValue(cursor, headerCol);
	}
	
	/**
	 * Get a list of aggregate values which will be displayed on the section header view
	 * 
	 * @param currentHeaderVal Header column value of current item
	 * @param setionStartPos starting position of the section
	 * @param cursor Cursor
	 * @return List of header values
	 */
	protected Object[] getHeaderAggregateValues(String currentHeaderVal, int startingPos, Cursor cursor) {
		int counter = 1;
		String tmpVal = null;
		String headerCol = getSectionHeaderColumn();
		
		// Move the cursor to the starting position of the section
		cursor.moveToPosition(startingPos);

		while (cursor.moveToNext()) {
			tmpVal = CursorUtils.getRecordStringValue(cursor, headerCol);

			if (currentHeaderVal.equals(tmpVal) || (TextUtils.isEmpty(currentHeaderVal) && TextUtils.isEmpty(tmpVal))) {
				counter++;
				// Other counter values should come here
			}
			else {
				break;
			}
		}
		
		// Move back to the current position after calculation
		cursor.moveToPosition(startingPos);
		
		return new Object[] {
				counter
		};
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Cursor cursor = getCursor();
		
		String currentHeaderVal = getHeaderValue(cursor, position);
		if (currentHeaderVal == null) {
			currentHeaderVal = "";
		}

        String prevHeaderVal = "";

        if (cursor.moveToPrevious()) {
        	prevHeaderVal = getHeaderValue(cursor, -1);
        	if (prevHeaderVal == null) {
        		prevHeaderVal = "";
        	}
        } else {
        	prevHeaderVal = null;
        }

        if (convertView == null) {
        	convertView = createNewItemView(getContext());
        }

        BaseSectionHeaderViewHolder viewHolder = (BaseSectionHeaderViewHolder)convertView.getTag();
        
        boolean headerValChanged = isHeaderValueChanged(currentHeaderVal, prevHeaderVal);
        
        // Get the number of items with the same header value
        Object[] aggregateVals = null;

        if (headerValChanged) {
        	aggregateVals = getHeaderAggregateValues(currentHeaderVal, position, cursor);
        }
        
        // Move cursor back to current position
        cursor.moveToPosition(position);
        
        // Set up the view holder, e.g, setting text, image to view items etc
        setUpViewHolder(viewHolder, cursor, headerValChanged, aggregateVals);

        return convertView;
	}
	
	protected abstract class BaseSectionHeaderViewHolder {
		public View sectionHeaderContainerView;
		
		public BaseSectionHeaderViewHolder(View view) {
			sectionHeaderContainerView = getSectionHeaderContainerView(view);
		}
		
		/**
		 * Get the container view of the section header
		 * 
		 * @param convertView The view
		 * @return Section header container view
		 */
		public abstract View getSectionHeaderContainerView(View view);
	}
	
	protected class BaseSectionHeaderViewHolderWithCounter extends BaseSectionHeaderViewHolder {
		public TextView sectionHeaderView1;
		public TextView sectionHeaderView2;

		public BaseSectionHeaderViewHolderWithCounter(View view) {
			super(view);
			getSectionHeaderViews(view);
		}

		@Override
		public View getSectionHeaderContainerView(View view) {
			return view.findViewById(R.id.listSectionHeaderContainerView);
		}

		protected void getSectionHeaderViews(View view) {
			sectionHeaderView1 = (TextView)view.findViewById(R.id.listSectionHeaderView1);
			sectionHeaderView2 = (TextView)view.findViewById(R.id.listSectionHeaderView2);
		}
	}
}

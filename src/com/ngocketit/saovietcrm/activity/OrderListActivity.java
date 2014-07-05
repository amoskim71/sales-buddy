package com.ngocketit.saovietcrm.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import com.ngocketit.saovietcrm.R;
import com.ngocketit.saovietcrm.adapter.BaseCursorAdapter;
import com.ngocketit.saovietcrm.adapter.OrderListAdapter;
import com.ngocketit.saovietcrm.common.DateTimeBreakdown;
import com.ngocketit.saovietcrm.database.CustomerTable;
import com.ngocketit.saovietcrm.database.OrderTable;
import com.ngocketit.saovietcrm.provider.SaovietCRMContentProvider;

public class OrderListActivity extends BaseListActivity {
	private static final String BREAKDOWN_TYPE = "breakdown";
	
	private int mBreakdown = DateTimeBreakdown.DAILY;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getSupportActionBar().setSelectedNavigationItem(mBreakdown);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putInt(BREAKDOWN_TYPE, mBreakdown);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		
		mBreakdown = savedInstanceState.getInt(BREAKDOWN_TYPE);
		getSupportActionBar().setSelectedNavigationItem(mBreakdown);
	}
	
	@Override
	protected Class<?> getFormActivityClass() {
		return OrderFormActivity.class;
	}
	
	@Override
	protected int getTitleResource() {
		return R.string.orders;
	}
	
	@Override
	protected SpinnerAdapter getActionBarSpinnerAdapter() {
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, 
				R.array.order_breakdowns, 
				android.R.layout.simple_spinner_dropdown_item);
		
		return adapter;
	}

	@Override
	protected BaseCursorAdapter getAdapter() {
		String[] from = new String[] { 
				OrderTable.COLUMN_QUANTITY
		};

		int[] to = new int[] { android.R.id.text1 };

		OrderListAdapter adapter = new OrderListAdapter(this, 
	    		android.R.layout.simple_list_item_2, 
	    		null, 
	    		from,
	    		to, 
	    		0);
		
		adapter.setBreakdown(mBreakdown);

		return adapter;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String selection = null;
		String[] selectionArgs = null;

		String query = getSearchQuery();

		// Search by customer name or created date
		if (!TextUtils.isEmpty(query)) {
			selection = CustomerTable.COLUMN_SHOP_NAME + " LIKE ? OR SUBSTR(O." + OrderTable.COLUMN_CREATED_AT + ", 0, 11) LIKE ? ";
			selectionArgs = new String[] { 
					"%" + query + "%", 
					"%" + query + "%" 
			};
		}

		CursorLoader cursorLoader = new CursorLoader(this,
				SaovietCRMContentProvider.ContentUri.ORDER_GROUP_BY_DATE, 
				null, 
				selection, 
				selectionArgs, 
				null);

		return cursorLoader;
	}
	
	@Override
	public boolean onNavigationItemSelected(int position, long itemId) {
		mBreakdown = position;
		
		OrderListAdapter adapter = (OrderListAdapter)getListViewAdapter();

		if (adapter != null) {
			adapter.setBreakdown(mBreakdown);
			return true;
		}
		
		return super.onNavigationItemSelected(position, itemId);
	}
}

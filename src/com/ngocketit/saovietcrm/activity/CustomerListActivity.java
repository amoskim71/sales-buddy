package com.ngocketit.saovietcrm.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.ngocketit.saovietcrm.R;
import com.ngocketit.saovietcrm.adapter.BaseCursorAdapter;
import com.ngocketit.saovietcrm.adapter.CustomerListAdapter;
import com.ngocketit.saovietcrm.database.CustomerTable;
import com.ngocketit.saovietcrm.database.SalingLineTable;
import com.ngocketit.saovietcrm.provider.SaovietCRMContentProvider;
import com.ngocketit.saovietcrm.util.CursorUtils;

public class CustomerListActivity extends BaseListActivity {
	@Override
	protected Class<?> getFormActivityClass() {
		return CustomerFormActivity.class;
	}
	
	@Override
	protected int getTitleResource() {
		return R.string.customers;
	}
	
	@Override
	protected void importItems(String filePath) {
		Toast.makeText(this, filePath, Toast.LENGTH_LONG).show();
	}
	
	@Override
	protected BaseCursorAdapter getAdapter() {
		// Fields from the database (projection)
	    // Must include the _id column for the adapter to work
	    String[] from = new String[] { 
	    		CustomerTable.COLUMN_SHOP_NAME,
	    		CustomerTable.COLUMN_RATE
	    };

	    // Fields on the UI to which we map
	    int[] to = new int[] { android.R.id.text1, android.R.id.text2 };

	    BaseCursorAdapter adapter = new CustomerListAdapter(this, android.R.layout.simple_list_item_2, null, from,
	        to, 0);
	    
	    return adapter;
	}
	
	@Override
	protected Bundle getIntentPickResult(Cursor cursor) {
		Bundle bundle = super.getIntentPickResult(cursor);
		
		bundle.putString(CustomerTable.COLUMN_SHOP_NAME, CursorUtils.getRecordStringValue(cursor, CustomerTable.COLUMN_SHOP_NAME));
		
		return bundle;
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String selection = null;
		String[] selectionArgs = null;

		String query = getSearchQuery();

		if (!TextUtils.isEmpty(query)) {
			selection = CustomerTable.COLUMN_SHOP_NAME + " LIKE ? OR " + SalingLineTable.COLUMN_NAME + " LIKE ? ";

			selectionArgs = new String[] { 
					"%" + query + "%", 
					"%" + query + "%" 
			};
		}

	    CursorLoader cursorLoader = new CursorLoader(this,
	        SaovietCRMContentProvider.ContentUri.CUSTOMER_GROUP_BY_LINE, 
	        null, selection, selectionArgs, null);

	    return cursorLoader;
	}
}

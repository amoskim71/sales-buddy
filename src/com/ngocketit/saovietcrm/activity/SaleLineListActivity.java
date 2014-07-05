package com.ngocketit.saovietcrm.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.ngocketit.saovietcrm.R;
import com.ngocketit.saovietcrm.adapter.BaseCursorAdapter;
import com.ngocketit.saovietcrm.database.SalingLineTable;
import com.ngocketit.saovietcrm.provider.SaovietCRMContentProvider;

public class SaleLineListActivity extends BaseListActivity {
	@Override
	protected Class<?> getFormActivityClass() {
		return SaleLineFormActivity.class;
	}
	
	@Override
	protected int getTitleResource() {
		return R.string.sale_lines;
	}
	
	@Override
	protected BaseCursorAdapter getAdapter() {
		// Fields from the database (projection)
	    // Must include the _id column for the adapter to work
	    String[] from = new String[] { 
	    		SalingLineTable.COLUMN_NAME,
	    		SalingLineTable.COLUMN_ADDRESS
	    };

	    // Fields on the UI to which we map
	    int[] to = new int[] { android.R.id.text1, android.R.id.text2 };

	    BaseCursorAdapter adapter = new BaseCursorAdapter(this, R.layout.list_item_simple_2, null, from,
	        to, 0);
	    
	    return adapter;
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = { 
				SalingLineTable._ID, 
				SalingLineTable.COLUMN_NAME, 
				SalingLineTable.COLUMN_ADDRESS 
		};
		String selection = null;
		String[] selectionArgs = null;

		String query = getSearchQuery();

		// Search for either name or address
		if (!TextUtils.isEmpty(query)) {
			selection = SalingLineTable.COLUMN_NAME + " LIKE ? OR " + SalingLineTable.COLUMN_ADDRESS + " LIKE ?";
			selectionArgs = new String[] { 
					"%" + query + "%", 
					"%" + query + "%" 
			};
		}

	    CursorLoader cursorLoader = new CursorLoader(this,
	        SaovietCRMContentProvider.ContentUri.SALE_LINE, 
	        projection, selection, selectionArgs, null);

	    return cursorLoader;
	}
}

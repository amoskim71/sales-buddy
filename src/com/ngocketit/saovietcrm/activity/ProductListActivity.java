package com.ngocketit.saovietcrm.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.ngocketit.saovietcrm.R;
import com.ngocketit.saovietcrm.adapter.BaseCursorAdapter;
import com.ngocketit.saovietcrm.adapter.ProductListAdapter;
import com.ngocketit.saovietcrm.database.ProductTable;
import com.ngocketit.saovietcrm.provider.SaovietCRMContentProvider;
import com.ngocketit.saovietcrm.util.CursorUtils;

public class ProductListActivity extends BaseListActivity {
	@Override
	protected Class<?> getFormActivityClass() {
		return ProductFormActivity.class;
	}
	
	@Override
	protected int getTitleResource() {
		return R.string.products;
	}
	
	@Override
	protected BaseCursorAdapter getAdapter() {
	    String[] from = new String[] { 
	    		ProductTable.COLUMN_NAME,
	    		ProductTable.COLUMN_CODE,
	    };

	    int[] to = new int[] { 
	    		android.R.id.text1, 
	    		android.R.id.text2,
	    };

	    BaseCursorAdapter adapter = new ProductListAdapter(this, android.R.layout.simple_list_item_2, null, from,
	        to, 0);
	    
	    return adapter;
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = { 
				ProductTable._ID, 
				ProductTable.COLUMN_NAME, 
				ProductTable.COLUMN_CODE 
		};
		
		if (isPickResultIntent()) {
			 projection = new String[] { 
					ProductTable._ID, 
					ProductTable.COLUMN_NAME, 
					ProductTable.COLUMN_CODE,
					ProductTable.COLUMN_UNIT_PRICE, 
					ProductTable.COLUMN_BOGO_BUY, 
					ProductTable.COLUMN_BOGO_GET, 
			};
		}

		String selection = null;
		String[] selectionArgs = null;

		String query = getSearchQuery();

		if (!TextUtils.isEmpty(query)) {
			selection = ProductTable.COLUMN_NAME + " LIKE ?";
			selectionArgs = new String[] { "%" + query + "%" };
		}

	    CursorLoader cursorLoader = new CursorLoader(this, 
	    		SaovietCRMContentProvider.ContentUri.PRODUCT_GROUP_BY_CAT, 
	    		projection, 
	    		selection, 
	    		selectionArgs, 
	    		null);

	    return cursorLoader;
	}

	@Override
	protected Bundle getIntentPickResult(Cursor cursor) {
		Bundle bundle = super.getIntentPickResult(cursor);

		bundle.putString(ProductTable.COLUMN_NAME, CursorUtils.getRecordStringValue(cursor, ProductTable.COLUMN_NAME));
		bundle.putString(ProductTable.COLUMN_CODE, CursorUtils.getRecordStringValue(cursor, ProductTable.COLUMN_CODE));
		bundle.putString(ProductTable.COLUMN_UNIT_PRICE, CursorUtils.getRecordStringValue(cursor, ProductTable.COLUMN_UNIT_PRICE));
		bundle.putString(ProductTable.COLUMN_BOGO_BUY, CursorUtils.getRecordStringValue(cursor, ProductTable.COLUMN_BOGO_BUY));
		bundle.putString(ProductTable.COLUMN_BOGO_GET, CursorUtils.getRecordStringValue(cursor, ProductTable.COLUMN_BOGO_GET));

		return bundle;
	}
}

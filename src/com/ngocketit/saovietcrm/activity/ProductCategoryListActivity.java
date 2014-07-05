package com.ngocketit.saovietcrm.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.ngocketit.saovietcrm.R;
import com.ngocketit.saovietcrm.adapter.BaseCursorAdapter;
import com.ngocketit.saovietcrm.database.BaseTable;
import com.ngocketit.saovietcrm.database.ProductCategoryTable;
import com.ngocketit.saovietcrm.provider.SaovietCRMContentProvider;

public class ProductCategoryListActivity extends BaseListActivity {
	@Override
	protected Class<?> getFormActivityClass() {
		return ProductCategoryFormActivity.class;
	}
	
	@Override
	protected int getTitleResource() {
		return R.string.product_cats;
	}
	
	@Override
	protected BaseCursorAdapter getAdapter() {
	    String[] from = new String[] { 
	    		ProductCategoryTable.COLUMN_NAME
	    };

	    int[] to = new int[] { android.R.id.text1 };

	    BaseCursorAdapter adapter = new BaseCursorAdapter(this, R.layout.list_item_simple_1, null, from,
	        to, 0);
	    
	    return adapter;
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = { 
				BaseTable._ID, 
				ProductCategoryTable.COLUMN_NAME 
		};
		String selection = null;
		String[] selectionArgs = null;

		String query = getSearchQuery();

		if (!TextUtils.isEmpty(query)) {
			selection = ProductCategoryTable.COLUMN_NAME + " LIKE ? ";
			selectionArgs = new String[] { "%" + query + "%" };
		}

	    CursorLoader cursorLoader = new CursorLoader(this,
	        SaovietCRMContentProvider.ContentUri.PRODUCT_CAT, 
	        projection, selection, selectionArgs, null);

	    return cursorLoader;
	}
}

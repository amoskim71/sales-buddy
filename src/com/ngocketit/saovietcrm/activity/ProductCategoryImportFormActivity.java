package com.ngocketit.saovietcrm.activity;

import android.content.ContentValues;
import android.os.Bundle;

import com.ngocketit.saovietcrm.R;
import com.ngocketit.saovietcrm.database.ProductCategoryTable;
import com.ngocketit.saovietcrm.provider.SaovietCRMContentProvider;

public class ProductCategoryImportFormActivity extends BaseImportFormActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.import_product_cats);
	}
	
	@Override
	protected int getTitleResource() {
		return R.string.import_product_cats;
	}

	@Override
	protected void emptyOldItems() {
		super.emptyOldItems();
		getContentResolver().delete(SaovietCRMContentProvider.ContentUri.PRODUCT_CAT, null, null);
	}

	@Override
	protected boolean importSingleItem(String[] items) {
		if (items.length == 2) {
			ContentValues values = new ContentValues();
			values.clear();

			values.put(ProductCategoryTable.COLUMN_NAME, items[0]);
			values.put(ProductCategoryTable.COLUMN_NOTE, items[1]);

			return getContentResolver().insert(SaovietCRMContentProvider.ContentUri.PRODUCT_CAT, values) != null;
		}
		
		return false;
	}
}

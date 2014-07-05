package com.ngocketit.saovietcrm.activity;

import android.content.ContentValues;
import android.os.Bundle;

import com.ngocketit.saovietcrm.R;
import com.ngocketit.saovietcrm.database.SalingLineTable;
import com.ngocketit.saovietcrm.provider.SaovietCRMContentProvider;

public class SaleLineImportFormActivity extends BaseImportFormActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.import_lines);
	}
	
	@Override
	protected void emptyOldItems() {
		super.emptyOldItems();
		
		getContentResolver().delete(SaovietCRMContentProvider.ContentUri.SALE_LINE, null, null);
	}
	
	@Override
	protected boolean importSingleItem(String[] items) {
		if (items.length == 2) {
			ContentValues values = new ContentValues();
			values.clear();

			values.put(SalingLineTable.COLUMN_NAME, items[0]);
			values.put(SalingLineTable.COLUMN_ADDRESS, items[1]);

			return getContentResolver().insert(SaovietCRMContentProvider.ContentUri.SALE_LINE, values) != null;
		}
		
		return false;
	}
}

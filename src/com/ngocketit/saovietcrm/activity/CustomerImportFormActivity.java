package com.ngocketit.saovietcrm.activity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.ngocketit.saovietcrm.R;
import com.ngocketit.saovietcrm.database.BaseTable;
import com.ngocketit.saovietcrm.database.CustomerTable;
import com.ngocketit.saovietcrm.database.SalingLineTable;
import com.ngocketit.saovietcrm.provider.SaovietCRMContentProvider;

public class CustomerImportFormActivity extends BaseImportFormActivity implements LoaderManager.LoaderCallbacks<Cursor> {
	private static final int LINE_LOADER_ID = 0x1;

	Spinner spnLines;
	Spinner spnTypes;
	SimpleCursorAdapter saleLinesAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.import_customers);
		
		spnLines = (Spinner)findViewById(R.id.spnSaleLine);
		spnTypes = (Spinner)findViewById(R.id.spnCustomerType);

		getSupportLoaderManager().initLoader(LINE_LOADER_ID, null, this);
		String[] from = {
				SalingLineTable.COLUMN_NAME
		};

		int[] to = {
				android.R.id.text1
		};

		saleLinesAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, null, from, to, 0);
		saleLinesAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
		spnLines.setAdapter(saleLinesAdapter);
	}
	
	@Override
	protected int getTitleResource() {
		return R.string.import_customers;
	}
	
	@Override
	protected void emptyOldItems() {
		super.emptyOldItems();
		getContentResolver().delete(SaovietCRMContentProvider.ContentUri.CUSTOMER, null, null);
	}

	@Override
	protected boolean importSingleItem(String[] items) {
		if (items.length == 8) {
			ContentValues values = new ContentValues();

			values.put(CustomerTable.COLUMN_SHOP_NAME, items[0]);
			values.put(CustomerTable.COLUMN_OWNER_NAME, items[1]);
			values.put(CustomerTable.COLUMN_ADDRESS, items[2]);
			values.put(CustomerTable.COLUMN_PHONE1, items[3]);
			values.put(CustomerTable.COLUMN_PHONE2, items[4]);
			values.put(CustomerTable.COLUMN_NOTE, items[5]);
			values.put(CustomerTable.COLUMN_EVALUATION, items[6]);
			values.put(CustomerTable.COLUMN_RATE, items[7]);

			values.put(CustomerTable.COLUMN_TYPE, spnTypes.getSelectedItemPosition());
			
			long lineId = spnLines.getSelectedItemId();
			if (lineId > 0) {
				values.put(CustomerTable.COLUMN_LINE_ID, lineId);
			}

			return getContentResolver().insert(SaovietCRMContentProvider.ContentUri.CUSTOMER, values) != null;
		}
		
		return false;
	}
	
	@Override
	protected int getViewExtraLayout() {
		return R.layout.form_customer_import_extra_view;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle options) {
		String[] projection = {
				BaseTable._ID,
				SalingLineTable.COLUMN_NAME
		};

		CursorLoader loader = new CursorLoader(this, 
				SaovietCRMContentProvider.ContentUri.SALE_LINE, 
				projection, 
				null, 
				null, 
				null);

		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// Insert extra item to top of the list
		MatrixCursor extras = new MatrixCursor(new String[] { 
				BaseTable._ID,
				SalingLineTable.COLUMN_NAME
		});

		extras.addRow(new String[] { "0", getString(R.string.unlined) });
		Cursor[] cursors = { extras, data };
		Cursor extendedCursor = new MergeCursor(cursors);

		saleLinesAdapter.swapCursor(extendedCursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		saleLinesAdapter.swapCursor(null);
	}
}	

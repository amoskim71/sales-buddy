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
import com.ngocketit.saovietcrm.database.ProductCategoryTable;
import com.ngocketit.saovietcrm.database.ProductTable;
import com.ngocketit.saovietcrm.provider.SaovietCRMContentProvider;

public class ProductImportFormActivity extends BaseImportFormActivity implements LoaderManager.LoaderCallbacks<Cursor> {
	private static final int CATEGORY_LOADER_ID = 0x1;
	
	Spinner spnProductCats;
	SimpleCursorAdapter productCatAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.import_products);
		
		spnProductCats = (Spinner)findViewById(R.id.spnProductCats);

		getSupportLoaderManager().initLoader(CATEGORY_LOADER_ID, null, this);
		String[] from = {
				ProductCategoryTable.COLUMN_NAME
		};

		int[] to = {
				android.R.id.text1
		};

		productCatAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, null, from, to, 0);
		productCatAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
		spnProductCats.setAdapter(productCatAdapter);
	}
	
	@Override
	protected int getTitleResource() {
		return R.string.import_products;
	}
	
	@Override
	protected void emptyOldItems() {
		super.emptyOldItems();
		getContentResolver().delete(SaovietCRMContentProvider.ContentUri.PRODUCT, null, null);
	}

	@Override
	protected boolean importSingleItem(String[] items) {
		if (items.length == 5) {
			ContentValues values = new ContentValues();

			values.put(ProductTable.COLUMN_NAME, items[0]);
			values.put(ProductTable.COLUMN_CODE, items[1]);
			values.put(ProductTable.COLUMN_UNIT_PRICE, items[2]);
			values.put(ProductTable.COLUMN_BOGO_BUY, items[3]);
			values.put(ProductTable.COLUMN_BOGO_GET, items[4]);
			
			long catId = spnProductCats.getSelectedItemId();
			if (catId > 0) {
				values.put(ProductTable.COLUMN_CATEGORY_ID, catId);
			}

			return getContentResolver().insert(SaovietCRMContentProvider.ContentUri.PRODUCT, values) != null;
		}
		
		return false;
	}
	
	@Override
	protected int getViewExtraLayout() {
		return R.layout.form_product_import_extra_view;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle options) {
		String[] projection = {
				BaseTable._ID,
				ProductCategoryTable.COLUMN_NAME
		};

		CursorLoader loader = new CursorLoader(this, 
				SaovietCRMContentProvider.ContentUri.PRODUCT_CAT, 
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
				ProductCategoryTable.COLUMN_NAME
		});

		extras.addRow(new String[] { "0", getString(R.string.uncategorized) });
		Cursor[] cursors = { extras, data };
		Cursor extendedCursor = new MergeCursor(cursors);

		productCatAdapter.swapCursor(extendedCursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		productCatAdapter.swapCursor(null);
	}
}

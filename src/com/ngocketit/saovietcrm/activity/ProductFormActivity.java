package com.ngocketit.saovietcrm.activity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.ngocketit.saovietcrm.R;
import com.ngocketit.saovietcrm.database.BaseTable;
import com.ngocketit.saovietcrm.database.ProductCategoryTable;
import com.ngocketit.saovietcrm.database.ProductTable;
import com.ngocketit.saovietcrm.provider.SaovietCRMContentProvider;
import com.ngocketit.saovietcrm.util.CursorUtils;

public class ProductFormActivity extends BaseFormActivity implements LoaderManager.LoaderCallbacks<Cursor> {
	private static final int CATEGORY_LOADER_ID = 0x1;
	
	private EditText txtName;
	private EditText txtCode;
	private EditText txtPrice;
	private EditText txtBogoBuy;
	private EditText txtBogoGet;
	
	private Spinner spnCategory;
	
	private long categoryID;
	
	private SimpleCursorAdapter mCategoryAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// NOTE: Don't findViewById here but in initContentView

		if (getItemId() > 0) {
			setTitle(R.string.edit_product);
		}
	}
	
	@Override
	protected int getTitleResource() {
		return R.string.add_product;
	}

	private void loadCategories() {
		getSupportLoaderManager().initLoader(CATEGORY_LOADER_ID, null, this);
		String[] from = {
				ProductCategoryTable.COLUMN_NAME
		};

		int[] to = {
				android.R.id.text1
		};

		mCategoryAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, null, from, to, 0);
		mCategoryAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
		spnCategory.setAdapter(mCategoryAdapter);
	}

	@Override
	protected void initContentView() {
		super.initContentView();

		txtName = (EditText)findViewById(R.id.txtName);
		txtCode = (EditText)findViewById(R.id.txtCode);
		txtPrice = (EditText)findViewById(R.id.txtPrice);
		txtBogoBuy = (EditText)findViewById(R.id.txtBogoBuy);
		txtBogoGet = (EditText)findViewById(R.id.txtBogoGet);
		
		spnCategory = (Spinner)findViewById(R.id.spnCategory);

		loadCategories();
	}
	
	@Override
	protected int getContentView() {
		return R.layout.activity_product_form;
	}
	
	@Override
	protected Uri getItemContentUri() {
		return SaovietCRMContentProvider.ContentUri.PRODUCT;
	}
	
	@Override
	protected void loadItem(long itemId) {
		super.loadItem(itemId);
		
		Cursor cursor = loadItemFromDatabase(itemId);
		
		if (cursor != null && cursor.getCount() == 1) {
			cursor.moveToFirst();

			categoryID = CursorUtils.getRecordIntValue(cursor, ProductTable.COLUMN_CATEGORY_ID);
			
			txtName.setText(CursorUtils.getRecordStringValue(cursor, ProductTable.COLUMN_NAME));
			txtCode.setText(CursorUtils.getRecordStringValue(cursor, ProductTable.COLUMN_CODE));
			txtPrice.setText(CursorUtils.getRecordStringValue(cursor, ProductTable.COLUMN_UNIT_PRICE));
			txtBogoBuy.setText(CursorUtils.getRecordStringValue(cursor, ProductTable.COLUMN_BOGO_BUY));
			txtBogoGet.setText(CursorUtils.getRecordStringValue(cursor, ProductTable.COLUMN_BOGO_GET));
			cursor.close();

		} else {
			// Do something here
		}
	}
	
	@Override
	protected boolean validateForm() {
		if (TextUtils.isEmpty(txtName.getText())) {
			showFormElementError(R.string.product_alert_name, txtName);
			return false;
		}

		if (TextUtils.isEmpty(txtCode.getText())) {
			showFormElementError(R.string.product_alert_code, txtCode);
			return false;
		}

		if (TextUtils.isEmpty(txtPrice.getText())) {
			showFormElementError(R.string.product_alert_price, txtPrice);
			return false;
		}
		
		return true;
	}
	
	@Override
	protected void saveItem() {
		super.saveItem();
		
		ContentValues values = new ContentValues();
		
		if (spnCategory.isEnabled()) {
			values.put(ProductTable.COLUMN_CATEGORY_ID, spnCategory.getSelectedItemId());
		}

		values.put(ProductTable.COLUMN_NAME, txtName.getText().toString());
		values.put(ProductTable.COLUMN_CODE, txtCode.getText().toString());
		values.put(ProductTable.COLUMN_UNIT_PRICE, txtPrice.getText().toString());
		values.put(ProductTable.COLUMN_BOGO_BUY, txtBogoBuy.getText().toString());
		values.put(ProductTable.COLUMN_BOGO_GET, txtBogoGet.getText().toString());
		
		if (!saveItemToDatabase(values)) {
			Toast.makeText(this, R.string.save_fail_alert, Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle opts) {
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

		mCategoryAdapter.swapCursor(extendedCursor);
		
		int categoryPos = -1;
		extendedCursor.moveToPosition(-1);

		while (extendedCursor.moveToNext()) {
			categoryPos++;

			int cid = CursorUtils.getRecordIntValue(extendedCursor, BaseTable._ID);
			if (cid == categoryID) {
				break;
			}
		}

		spnCategory.setSelection(categoryPos > 0 ? categoryPos : 0);
		extendedCursor.moveToFirst();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mCategoryAdapter.swapCursor(null);
	}
}


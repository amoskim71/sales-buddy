package com.ngocketit.saovietcrm.activity;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.ngocketit.saovietcrm.R;
import com.ngocketit.saovietcrm.database.ProductCategoryTable;
import com.ngocketit.saovietcrm.provider.SaovietCRMContentProvider;
import com.ngocketit.saovietcrm.util.CursorUtils;

public class ProductCategoryFormActivity extends BaseFormActivity {
	private EditText txtName;
	private EditText txtNote;
	
	@Override
	protected int getEditModeTitle() {
		return R.string.edit_product_cat;
	}
	
	@Override
	protected int getTitleResource() {
		return R.string.add_product_cat;
	}	
	
	@Override
	protected void initContentView() {
		super.initContentView();
		
		txtName = (EditText)findViewById(R.id.txtName);
		txtNote = (EditText)findViewById(R.id.txtNote);
	}
	
	@Override
	protected int getContentView() {
		return R.layout.activity_product_cat_form;
	}
	
	@Override
	protected Uri getItemContentUri() {
		return SaovietCRMContentProvider.ContentUri.PRODUCT_CAT;
	}
	
	@Override
	protected boolean validateForm() {
		if (TextUtils.isEmpty(txtName.getText())) {
			showFormElementError(R.string.line_alert_name, txtName);
			return false;
		}

		return true;
	}
	
	@Override
	protected void loadItem(long itemId) {
		Cursor cursor = loadItemFromDatabase(itemId);
		
		if (cursor != null) {
			cursor.moveToFirst();

			txtName.setText(CursorUtils.getRecordStringValue(cursor, ProductCategoryTable.COLUMN_NAME));
			txtNote.setText(CursorUtils.getRecordStringValue(cursor, ProductCategoryTable.COLUMN_NOTE));

			cursor.close();
		}
	}
	
	@Override
	protected void saveItem() {
		super.saveItem();
		
		ContentValues values = new ContentValues();
		
		values.put(ProductCategoryTable.COLUMN_NAME, txtName.getText().toString());
		values.put(ProductCategoryTable.COLUMN_NOTE, txtNote.getText().toString());
		
		if (!saveItemToDatabase(values)) {
			Toast.makeText(this, R.string.save_fail_alert, Toast.LENGTH_LONG).show();
		}
	}
}

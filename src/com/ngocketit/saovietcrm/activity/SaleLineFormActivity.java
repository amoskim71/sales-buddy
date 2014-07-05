package com.ngocketit.saovietcrm.activity;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.ngocketit.saovietcrm.R;
import com.ngocketit.saovietcrm.database.SalingLineTable;
import com.ngocketit.saovietcrm.provider.SaovietCRMContentProvider;
import com.ngocketit.saovietcrm.util.CursorUtils;

public class SaleLineFormActivity extends BaseFormActivity {
	private EditText txtName;
	private EditText txtAddress;
	
	@Override
	protected int getEditModeTitle() {
		return R.string.edit_sale_line;
	}
	
	@Override
	protected int getTitleResource() {
		return R.string.add_sale_line;
	}
	
	@Override
	protected void initContentView() {
		super.initContentView();
		
		txtName = (EditText)findViewById(R.id.txtName);
		txtAddress = (EditText)findViewById(R.id.txtAddress);
	}
	
	@Override
	protected int getContentView() {
		return R.layout.activity_sale_line_form;
	}
	
	@Override
	protected Uri getItemContentUri() {
		return SaovietCRMContentProvider.ContentUri.SALE_LINE;
	}
	
	@Override
	protected boolean validateForm() {
		if (TextUtils.isEmpty(txtName.getText())) {
			showFormElementError(R.string.line_alert_name, txtName);
			return false;
		}

		if (TextUtils.isEmpty(txtAddress.getText())) {
			showFormElementError(R.string.line_alert_address, txtAddress);
			return false;
		}
		
		return true;
	}
	
	@Override
	protected void loadItem(long itemId) {
		Cursor cursor = loadItemFromDatabase(itemId);
		
		if (cursor != null) {
			cursor.moveToFirst();

			txtName.setText(CursorUtils.getRecordStringValue(cursor, SalingLineTable.COLUMN_NAME));
			txtAddress.setText(CursorUtils.getRecordStringValue(cursor, SalingLineTable.COLUMN_ADDRESS));

			cursor.close();
		}
	}
	
	@Override
	protected void saveItem() {
		super.saveItem();
		
		ContentValues values = new ContentValues();
		
		values.put(SalingLineTable.COLUMN_NAME, txtName.getText().toString());
		values.put(SalingLineTable.COLUMN_ADDRESS, txtAddress.getText().toString());
		
		if (!saveItemToDatabase(values)) {
			Toast.makeText(this, R.string.save_fail_alert, Toast.LENGTH_LONG).show();
		}
	}
}

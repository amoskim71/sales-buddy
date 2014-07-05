package com.ngocketit.saovietcrm.activity;

import android.content.ContentValues;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.ngocketit.saovietcrm.R;
import com.ngocketit.saovietcrm.database.BaseTable;
import com.ngocketit.saovietcrm.database.CustomerTable;
import com.ngocketit.saovietcrm.database.ProductCategoryTable;
import com.ngocketit.saovietcrm.database.SalingLineTable;
import com.ngocketit.saovietcrm.provider.SaovietCRMContentProvider;
import com.ngocketit.saovietcrm.util.CursorUtils;

public class CustomerFormActivity extends BaseFormActivity implements LoaderManager.LoaderCallbacks<Cursor> {
	private static final int LINE_LOADER_ID = 0x1;
	
	private EditText txtShopName;
	private EditText txtOwnerName;
	private EditText txtAddress;
	private EditText txtMobilePhone;
	private EditText txtLandlinePhone;
	private EditText txtEvaluation;
	private EditText txtNote;

	private Spinner spnCustomerType;
	private Spinner spnLine;
	private RatingBar rateBar;
	
	private ImageButton btnCallMobile;
	private ImageButton btnCallLandline;

	private long lineID;
	
	private SimpleCursorAdapter mLineAdapter;
				
	@Override
	protected int getEditModeTitle() {
		return R.string.edit_customer;
	}
	
	@Override
	protected int getTitleResource() {
		return R.string.add_customer;
	}
	
	@Override
	protected void initContentView() {
		txtShopName = (EditText)findViewById(R.id.txtShopName);
		txtOwnerName = (EditText)findViewById(R.id.txtOwnerName);
		txtAddress = (EditText)findViewById(R.id.txtAddress);
		txtMobilePhone = (EditText)findViewById(R.id.txtMobilePhone);
		txtLandlinePhone = (EditText)findViewById(R.id.txtLandlinePhone);
		txtEvaluation = (EditText)findViewById(R.id.txtEvaluation);
		txtNote = (EditText)findViewById(R.id.txtNote);

		spnCustomerType = (Spinner)findViewById(R.id.spnCustomerType);
		spnLine = (Spinner)findViewById(R.id.spnLine);
		
		rateBar = (RatingBar)findViewById(R.id.rateBar);
		
		btnCallLandline = (ImageButton)findViewById(R.id.btnCallLandline);
		btnCallMobile = (ImageButton)findViewById(R.id.btnCallMobile);
		
		btnCallLandline.setOnClickListener(this);
		btnCallMobile.setOnClickListener(this);
		
		initAdapters();
	}
	
	private void initAdapters() {
		getSupportLoaderManager().initLoader(LINE_LOADER_ID, null, this);
		String[] from = {
				SalingLineTable.COLUMN_NAME
		};

		int[] to = {
				android.R.id.text1
		};

		mLineAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, null, from, to, 0);
		mLineAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
		spnLine.setAdapter(mLineAdapter);
	}
	
	@Override
	protected int getContentView() {
		return R.layout.activity_customer_form;
	}
	
	@Override
	protected boolean validateForm() {
		if (spnCustomerType.getSelectedItemPosition() == 0) {
			showFormElementError(R.string.customer_alert_type, spnCustomerType);
			return false;
		}
		
		if (TextUtils.isEmpty(txtShopName.getText())) {
			showFormElementError(R.string.customer_alert_shopname, txtShopName);
			return false;
		}
		
		if (TextUtils.isEmpty(txtOwnerName.getText())) {
			showFormElementError(R.string.customer_alert_ownername, txtOwnerName);
			return false;
		}
		
		if (TextUtils.isEmpty(txtAddress.getText())) {
			showFormElementError(R.string.customer_alert_address, txtOwnerName);
			return false;
		}

		if (TextUtils.isEmpty(txtMobilePhone.getText()) && TextUtils.isEmpty(txtLandlinePhone.getText())) {
			showFormElementError(R.string.customer_alert_phone, txtOwnerName);
			return false;
		}

		return true;
	}
	
	@Override
	protected Uri getItemContentUri() {
		return SaovietCRMContentProvider.ContentUri.CUSTOMER;
	}
	
	@Override
	protected void loadItem(long itemId) {
		Cursor cursor = loadItemFromDatabase(itemId);
		
		if (cursor != null && cursor.getCount() ==  1) {
			cursor.moveToFirst();

			lineID = CursorUtils.getRecordIntValue(cursor, CustomerTable.COLUMN_LINE_ID);
			spnCustomerType.setSelection(CursorUtils.getRecordIntValue(cursor, CustomerTable.COLUMN_TYPE));

			txtShopName.setText(CursorUtils.getRecordStringValue(cursor, CustomerTable.COLUMN_SHOP_NAME));
			txtOwnerName.setText(CursorUtils.getRecordStringValue(cursor, CustomerTable.COLUMN_OWNER_NAME));
			txtAddress.setText(CursorUtils.getRecordStringValue(cursor, CustomerTable.COLUMN_ADDRESS));

			txtMobilePhone.setText(CursorUtils.getRecordStringValue(cursor, CustomerTable.COLUMN_PHONE1));
			txtLandlinePhone.setText(CursorUtils.getRecordStringValue(cursor, CustomerTable.COLUMN_PHONE2));

			txtEvaluation.setText(CursorUtils.getRecordStringValue(cursor, CustomerTable.COLUMN_EVALUATION));
			txtNote.setText(CursorUtils.getRecordStringValue(cursor, CustomerTable.COLUMN_NOTE));
			
			float rate = CursorUtils.getRecordFloatValue(cursor, CustomerTable.COLUMN_RATE);
			if (rate >= 0) {
				rateBar.setRating(rate);
			}
		}
		
		cursor.close();
	}
	
	private void makePhoneCall(String number) {
		try {
			Intent callIntent = new Intent(Intent.ACTION_CALL);
			callIntent.setData(Uri.parse("tel:" + number)); 
			startActivity(callIntent);
		} catch (Exception e) {
			Toast.makeText(this, R.string.call_fail_alert, Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		if (v == btnCallLandline) {
			String landline = txtLandlinePhone.getText().toString();
			
			if (!TextUtils.isEmpty(landline)) {
				makePhoneCall(landline);
			}
		}
		else if (v == btnCallMobile) {
			String mobile = txtMobilePhone.getText().toString();

			if (!TextUtils.isEmpty(mobile)) {
				makePhoneCall(mobile);
			}
		}
	}
	
	@Override
	protected void saveItem() {
		super.saveItem();
		
		ContentValues values = new ContentValues();
		
		values.put(CustomerTable.COLUMN_TYPE, spnCustomerType.getSelectedItemPosition());
		values.put(CustomerTable.COLUMN_LINE_ID, spnLine.getSelectedItemId());
		
		values.put(CustomerTable.COLUMN_SHOP_NAME, txtShopName.getText().toString());
		values.put(CustomerTable.COLUMN_OWNER_NAME, txtOwnerName.getText().toString());
		values.put(CustomerTable.COLUMN_ADDRESS, txtAddress.getText().toString());
		values.put(CustomerTable.COLUMN_PHONE1, txtMobilePhone.getText().toString());
		values.put(CustomerTable.COLUMN_PHONE2, txtLandlinePhone.getText().toString());
		values.put(CustomerTable.COLUMN_EVALUATION, txtEvaluation.getText().toString());
		values.put(CustomerTable.COLUMN_NOTE, txtNote.getText().toString());

		values.put(CustomerTable.COLUMN_RATE, rateBar.getRating());
		
		if (!saveItemToDatabase(values)) {
			
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle opts) {
		String[] projection = {
				SalingLineTable._ID,
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

		mLineAdapter.swapCursor(extendedCursor);
		
		int currentLinePos = -1;
		extendedCursor.moveToPosition(-1);

		while (extendedCursor.moveToNext()) {
			currentLinePos++;

			int lid = CursorUtils.getRecordIntValue(extendedCursor, SalingLineTable._ID);
			if (lid == lineID) {
				break;
			}
		}

		spnLine.setSelection(currentLinePos > 0 ? currentLinePos : 0);
		extendedCursor.moveToFirst();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mLineAdapter.swapCursor(null);
	}
}

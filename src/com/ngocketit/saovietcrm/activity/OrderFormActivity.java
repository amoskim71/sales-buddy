package com.ngocketit.saovietcrm.activity;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.ngocketit.saovietcrm.R;
import com.ngocketit.saovietcrm.database.CustomerTable;
import com.ngocketit.saovietcrm.database.OrderTable;
import com.ngocketit.saovietcrm.fragment.DatePickerFragment;
import com.ngocketit.saovietcrm.provider.SaovietCRMContentProvider;
import com.ngocketit.saovietcrm.util.CursorUtils;
import com.ngocketit.saovietcrm.util.GeneralUtils;

public class OrderFormActivity extends BaseFormActivity {
	private static final int CUSTOMER_INTENT = 0x1;
	
	private ImageButton btnPickCustomer;
	private EditText txtCustomerName;
	private EditText txtQuantity;
	private Spinner spnOrderType;
	private EditText txtDiscount;
	private Button btnOrderDetails;
	
	private ImageButton btnPickDate;
	private EditText txtCreatedDate;
	
	private long customerID = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getItemId() <= 0) {
			btnOrderDetails.setVisibility(View.GONE);
		}
	}
	
	@Override
	protected int getEditModeTitle() {
		return R.string.edit_order;
	}
	
	@Override
	protected int getTitleResource() {
		return R.string.add_order;
	}
	
	@Override
	protected void initContentView() {
		super.initContentView();
		
		btnPickCustomer = (ImageButton)findViewById(R.id.btnPickCustomer);
		btnPickCustomer.setOnClickListener(this);
		
		txtCustomerName = (EditText)findViewById(R.id.txtCustomerName);
		txtQuantity = (EditText)findViewById(R.id.txtQuantity);
		txtDiscount = (EditText)findViewById(R.id.txtDiscount);
		
		spnOrderType = (Spinner)findViewById(R.id.spnOrderType);
		
		btnOrderDetails = (Button)findViewById(R.id.btnOrderDetails);
		btnOrderDetails.setOnClickListener(this);
		
		btnPickDate = (ImageButton)findViewById(R.id.btnPickDate);
		btnPickDate.setOnClickListener(this);
		txtCreatedDate = (EditText)findViewById(R.id.txtCreatedDate);
		
		// Disable editing
		txtCustomerName.setInputType(InputType.TYPE_NULL);
	}
	
	@Override
	protected int getContentView() {
		return R.layout.activity_order_form;
	}
	
	
	@Override
	protected boolean validateForm() {
		if (customerID <= 0) {
			showFormElementError(R.string.order_alert_customer, txtCustomerName);
			return false;
		}
		
		if (spnOrderType.getSelectedItemPosition() == 0) {
			showFormElementError(R.string.order_alert_type, spnOrderType);
			return false;
		}
		
		if (TextUtils.isEmpty(txtQuantity.getText().toString())) {
			showFormElementError(R.string.order_alert_quantity, txtQuantity);
			return false;
		}

		int quantity = Integer.parseInt(txtQuantity.getText().toString());
		if (quantity <= 0) {
			showFormElementError(R.string.order_alert_quantity_invalid, txtQuantity);
			return false;
		}
		
		return super.validateForm();
	}
	
	@Override
	protected Uri getItemContentUri() {
		return SaovietCRMContentProvider.ContentUri.ORDER;
	}
	
	@Override
	protected void loadItem(long itemId) {
		Cursor cursor = loadItemFromDatabase(itemId);
		
		if (cursor != null && cursor.getCount() == 1) {
			customerID = CursorUtils.getRecordIntValue(cursor, OrderTable.COLUMN_CUSTOMER_ID);
			String customerName = CursorUtils.getRecordStringValue(cursor, OrderTable.COLUMN_CUSTOMER_NAME);
			int quantity = CursorUtils.getRecordIntValue(cursor, OrderTable.COLUMN_QUANTITY);
			int type = CursorUtils.getRecordIntValue(cursor, OrderTable.COLUMN_TYPE);
			float discount = CursorUtils.getRecordFloatValue(cursor, OrderTable.COLUMN_DISCOUNT);
			String createdDate = CursorUtils.getRecordStringValue(cursor, OrderTable.COLUMN_CREATED_AT);
			
			txtCustomerName.setText(customerName);
			txtQuantity.setText(String.valueOf(quantity));
			txtDiscount.setText(String.valueOf(discount));
			spnOrderType.setSelection(type);
			
			createdDate = GeneralUtils.sqlTimestampToDate(createdDate);
			if (createdDate != null) {
				txtCreatedDate.setText(createdDate);
			}
			
			cursor.close();
		}
	}
	
	@Override
	protected void saveItem() {
		super.saveItem();

		ContentValues values = new ContentValues();

		values.put(OrderTable.COLUMN_CUSTOMER_ID, this.customerID);
		values.put(OrderTable.COLUMN_CUSTOMER_NAME, txtCustomerName.getText().toString());
		values.put(OrderTable.COLUMN_QUANTITY, txtQuantity.getText().toString());
		values.put(OrderTable.COLUMN_TYPE, spnOrderType.getSelectedItemPosition());
		values.put(OrderTable.COLUMN_DISCOUNT, txtDiscount.getText().toString());
		
		if (!TextUtils.isEmpty(txtCreatedDate.getText())) {
			String createdDate = GeneralUtils.dateToSqlTimestamp(txtCreatedDate.getText().toString());

			if (createdDate != null) {
				values.put(OrderTable.COLUMN_CREATED_AT, createdDate);
			}
		}

		if (!saveItemToDatabase(values)) {
			Toast.makeText(this, R.string.save_fail_alert, Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		if (v == btnPickCustomer) {
			Intent customerIntent = new Intent(this, CustomerListActivity.class);
			customerIntent.setAction(Intent.ACTION_PICK);
			startActivityForResult(customerIntent, CUSTOMER_INTENT);
		}
		else if (v == btnPickDate) {
			pickDate();
		}
		else if (v == btnOrderDetails) {
			Intent intent = new Intent(this, OrderLineItemListActivity.class);
			intent.putExtra(BaseFormActivity.ITEM_ID, getItemId());
			startActivity(intent);
		}
	}
	
	private void pickDate() {
		DatePickerFragment datePicker = new DatePickerFragment();

		datePicker.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
			public void onDateSet(android.widget.DatePicker view, int year,
					int monthOfYear, int dayOfMonth) {
				txtCreatedDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
			}
		});
		
		String dob = txtCreatedDate.getText().toString();

		if (!TextUtils.isEmpty(dob)) {
			String[] parts = dob.split("-");

			if (parts.length == 3) {
				int day = Integer.parseInt(parts[0]);
				int month = Integer.parseInt(parts[1]);
				int year = Integer.parseInt(parts[2]);
				
				datePicker.setInitDate(year, month - 1, day);
			}
		}
		
		datePicker.show(getSupportFragmentManager(), "datePicker");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == RESULT_OK && requestCode == CUSTOMER_INTENT) {
			Bundle result = data.getBundleExtra(BaseFormActivity.PICK_RESULT);
			long customerId = data.getLongExtra(BaseFormActivity.ITEM_ID, -1);

			if (customerId > 0) {
				this.customerID = customerId;
				String shopName = result.getString(CustomerTable.COLUMN_SHOP_NAME);
				txtCustomerName.setText(shopName);
			}
		}
	}
}

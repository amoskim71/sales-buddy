package com.ngocketit.saovietcrm.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ngocketit.saovietcrm.R;
import com.ngocketit.saovietcrm.database.OrderDetailTable;
import com.ngocketit.saovietcrm.database.ProductTable;
import com.ngocketit.saovietcrm.provider.SaovietCRMContentProvider;
import com.ngocketit.saovietcrm.util.CursorUtils;
import com.ngocketit.saovietcrm.util.GeneralUtils;

public class OrderLineItemFormActivity extends BaseFormActivity implements TextWatcher {
	private static final int PRODUCT_INTENT = 0x1;
	
	protected EditText txtProductName;
	protected EditText txtQuantity;
	protected EditText txtPrice;
	protected EditText txtBogoBuy;
	protected EditText txtBogoGet;
	private ImageButton btnPickProduct;
	protected TextView lblTotalPrice;
	protected TextView lblTotalQuantity;
	
	protected long productID;
	protected String productCode;
	private long orderID = 0;
	protected double totalPrice = 0;
	protected int totalQuantity = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle extra = getIntent().getExtras();
		if (extra != null && extra.containsKey(OrderDetailTable.COLUMN_ORDER_ID)) {
			orderID = extra.getLong(OrderDetailTable.COLUMN_ORDER_ID);
		}
	}
	
	@Override
	protected int getEditModeTitle() {
		return R.string.edit_order_line_item;
	}
	
	@Override
	protected int getTitleResource() {
		return R.string.add_order_line_item;
	}
	
	@Override
	protected int getContentView() {
		return R.layout.activity_order_line_item_form;
	}

	@Override
	protected Uri getItemContentUri() {
		return SaovietCRMContentProvider.ContentUri.ORDER_DETAIL;
	}
	
	@Override
	protected void initContentView() {
		super.initContentView();
		
		txtProductName = (EditText)findViewById(R.id.txtProductName);
		txtQuantity = (EditText)findViewById(R.id.txtQuantity);
		txtPrice = (EditText)findViewById(R.id.txtPrice);
		txtBogoBuy = (EditText)findViewById(R.id.txtBogoBuy);
		txtBogoGet = (EditText)findViewById(R.id.txtBogoGet);
		
		btnPickProduct = (ImageButton)findViewById(R.id.btnPickProduct);
		btnPickProduct.setOnClickListener(this);
		
		lblTotalPrice = (TextView)findViewById(R.id.lblTotalPrice);
		lblTotalQuantity = (TextView)findViewById(R.id.lblTotalDeliveryQuantity);
		
		txtPrice.setInputType(InputType.TYPE_NULL);
		
		txtQuantity.addTextChangedListener(this);
		txtBogoBuy.addTextChangedListener(this);
		txtBogoGet.addTextChangedListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		if (v == btnPickProduct) {
			Intent productIntent = new Intent(this, ProductListActivity.class);
			productIntent.setAction(Intent.ACTION_PICK);
			startActivityForResult(productIntent, PRODUCT_INTENT);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == PRODUCT_INTENT && resultCode == RESULT_OK) {
			Bundle result = data.getBundleExtra(BaseFormActivity.PICK_RESULT);
			long productId = data.getLongExtra(BaseFormActivity.ITEM_ID, -1);

			if (productId > 0) {
				this.productID = productId;
				this.productCode = result.getString(ProductTable.COLUMN_CODE);

				txtProductName.setText(result.getString(ProductTable.COLUMN_NAME));
				txtPrice.setText(result.getString(ProductTable.COLUMN_UNIT_PRICE));
				txtBogoBuy.setText(result.getString(ProductTable.COLUMN_BOGO_BUY));
				txtBogoGet.setText(result.getString(ProductTable.COLUMN_BOGO_GET));
				
				// So that user can start inputing
				txtQuantity.requestFocus();
			}
		}
	}
	
	@Override
	protected void loadItem(long itemId) {
		super.loadItem(itemId);

		Cursor cursor = loadItemFromDatabase(itemId);

		if (cursor != null && cursor.getCount() == 1) {
			productID = CursorUtils.getRecordIntValue(cursor, OrderDetailTable.COLUMN_PRODUCT_ID);
			productCode = CursorUtils.getRecordStringValue(cursor, ProductTable.COLUMN_CODE);
			
			txtProductName.setText(CursorUtils.getRecordStringValue(cursor, ProductTable.COLUMN_NAME));
			txtQuantity.setText(CursorUtils.getRecordStringValue(cursor, OrderDetailTable.COLUMN_QUANTITY));
			txtPrice.setText(CursorUtils.getRecordStringValue(cursor, ProductTable.COLUMN_UNIT_PRICE));
			txtBogoBuy.setText(CursorUtils.getRecordStringValue(cursor, ProductTable.COLUMN_BOGO_BUY));
			txtBogoGet.setText(CursorUtils.getRecordStringValue(cursor, ProductTable.COLUMN_BOGO_GET));
			
			double price = CursorUtils.getRecordFloatValue(cursor, OrderDetailTable.COLUMN_TOTAL_PRICE);
			lblTotalPrice.setText(GeneralUtils.formatCurrency(price));

			cursor.close();
		}
	}
	
	@Override
	protected boolean validateForm() {
		if (productID <= 0) {
			showFormElementError(R.string.order_alert_product, txtProductName);
			return false;
		}
		
		if (TextUtils.isEmpty(txtQuantity.getText())) {
			showFormElementError(R.string.order_alert_quantity, txtQuantity);
			return false;
		}
		
		int quantity = Integer.parseInt(txtQuantity.getText().toString());
		if (quantity <= 0) {
			showFormElementError(R.string.order_alert_quantity_invalid, txtQuantity);
			return false;
		}
		
		return true;
	}
	
	@Override
	protected void saveItem() {
		super.saveItem();

		ContentValues values = new ContentValues();

		values.put(OrderDetailTable.COLUMN_PRODUCT_ID, this.productID);
		values.put(OrderDetailTable.COLUMN_QUANTITY, txtQuantity.getText().toString());
		values.put(ProductTable.COLUMN_NAME, txtProductName.getText().toString());
		values.put(ProductTable.COLUMN_CODE, productCode);
		values.put(ProductTable.COLUMN_UNIT_PRICE, txtPrice.getText().toString());
		values.put(ProductTable.COLUMN_BOGO_BUY, txtBogoBuy.getText().toString());
		values.put(ProductTable.COLUMN_BOGO_GET, txtBogoGet.getText().toString());
		
		values.put(OrderDetailTable.COLUMN_TOTAL_PRICE, totalPrice);

		// Only save to database if we're adding line item for an existing order
		if (orderID > 0) {
			values.put(OrderDetailTable.COLUMN_ORDER_ID, orderID);
			
			if (!saveItemToDatabase(values)) {
				Toast.makeText(this, R.string.save_fail_alert, Toast.LENGTH_LONG).show();
			}
		}
	}
	
	protected void calculateTotalPriceAndQuantity() {
		try {
			if (TextUtils.isEmpty(txtQuantity.getText())) {
				return;
			}

			int quantity = Integer.parseInt(txtQuantity.getText().toString());
			int bogoBuy = 0;
			int bogoGet = 0;
			totalQuantity = quantity;
			
			if (!TextUtils.isEmpty(txtBogoBuy.getText())) {
				bogoBuy = Integer.parseInt(txtBogoBuy.getText().toString());
			}

			if (!TextUtils.isEmpty(txtBogoGet.getText())) {
				bogoGet = Integer.parseInt(txtBogoGet.getText().toString());
			}

			double unitPrice = Double.parseDouble(txtPrice.getText().toString());
			totalPrice = unitPrice * quantity;
			
			if (bogoBuy > 0 && bogoGet > 0) {
				totalQuantity += bogoGet * Math.floor(quantity/bogoBuy);
			}
			
			lblTotalPrice.setText(GeneralUtils.formatCurrency(totalPrice));
			lblTotalQuantity.setText(String.valueOf(totalQuantity));

		} catch (NumberFormatException e) {
			totalPrice = 0;
			lblTotalPrice.setText(R.string.unknown);
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		calculateTotalPriceAndQuantity();
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}
}

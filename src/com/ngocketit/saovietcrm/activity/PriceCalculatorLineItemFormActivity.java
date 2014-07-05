package com.ngocketit.saovietcrm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.ngocketit.saovietcrm.model.PriceCalculatorLineItem;

public class PriceCalculatorLineItemFormActivity extends
		OrderLineItemFormActivity {
	
	private PriceCalculatorLineItem mItem;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// In the base class, loadItem() is only called if the item ID
		// is passed along. Here we don't pass item ID so call this again
		loadItem(0);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void loadItem(long itemId) {
		Bundle extra = getIntent().getExtras();
		
		if (extra != null && extra.containsKey(PriceCalculatorListActivity.ORDER_LINE_ITEM)) {
			mItem = (PriceCalculatorLineItem)extra.getParcelable(PriceCalculatorListActivity.ORDER_LINE_ITEM);
			
			if (mItem != null) {
				txtProductName.setText(mItem.name);
				txtQuantity.setText(String.valueOf(mItem.quantity));
				txtBogoBuy.setText(String.valueOf(mItem.bogoBuy));
				txtBogoGet.setText(String.valueOf(mItem.bogoGet));
				txtPrice.setText(String.valueOf(mItem.price));
				lblTotalPrice.setText(String.valueOf(mItem.totalPrice));
				lblTotalQuantity.setText(String.valueOf(mItem.totalQuantity));
				
				this.productID = mItem.productId;
			}
		}
	}
	
	@Override
	protected void saveItem() {
		if (mItem == null) {
			mItem = new PriceCalculatorLineItem();
		}
		
		// Update the total price before saving
		calculateTotalPriceAndQuantity();
		
		mItem.name = txtProductName.getText().toString();
		mItem.quantity = Integer.parseInt(txtQuantity.getText().toString());
		mItem.price = Double.parseDouble(txtPrice.getText().toString());
		mItem.totalPrice = this.totalPrice;
		mItem.productId = this.productID;
		mItem.totalQuantity = this.totalQuantity;

		try {
			mItem.bogoBuy = Integer.parseInt(txtBogoBuy.getText().toString());
			mItem.bogoGet = Integer.parseInt(txtBogoGet.getText().toString());
		} 
		catch (NumberFormatException e) {
			
		}
		
		Intent data = new Intent();
		data.putExtra(PriceCalculatorListActivity.ORDER_LINE_ITEM, mItem);
		setResult(RESULT_OK, data);
		finish();
	}
}

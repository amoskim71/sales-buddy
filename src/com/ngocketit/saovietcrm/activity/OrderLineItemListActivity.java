package com.ngocketit.saovietcrm.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.ngocketit.saovietcrm.R;
import com.ngocketit.saovietcrm.adapter.BaseCursorAdapter;
import com.ngocketit.saovietcrm.database.OrderDetailTable;
import com.ngocketit.saovietcrm.database.ProductTable;
import com.ngocketit.saovietcrm.provider.SaovietCRMContentProvider;
import com.ngocketit.saovietcrm.util.CursorUtils;
import com.ngocketit.saovietcrm.util.GeneralUtils;

public class OrderLineItemListActivity extends BaseListActivity {
	private long orderID = 0;
	private TextView lblTotalPrice;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		lblTotalPrice = (TextView)findViewById(R.id.lblTotalPrice);
		findViewById(R.id.headerBar).setVisibility(View.GONE);
	}
	
	@Override
	protected void initAdapter() {
		// NOTE: initAdapter() is called in onCreate() in base class
		// we need to make sure that we have orderID before onCreateLoader
		// is called.
		Bundle extra = getIntent().getExtras();

		if (extra != null && extra.containsKey(BaseFormActivity.ITEM_ID)) {
			orderID = extra.getLong(BaseFormActivity.ITEM_ID);
		}

		super.initAdapter();
	}

	@Override
	protected Class<?> getFormActivityClass() {
		return OrderLineItemFormActivity.class;
	}
	
	@Override
	protected int getContentView() {
		return R.layout.activity_order_line_item_list;
	}

	@Override
	protected BaseCursorAdapter getAdapter() {
	    String[] from = new String[] { 
	    		ProductTable.COLUMN_NAME,
	    		OrderDetailTable.COLUMN_QUANTITY,
	    		OrderDetailTable.COLUMN_TOTAL_PRICE
	    };

	    // Fields on the UI to which we map
	    int[] to = new int[] { 
	    		android.R.id.text1, 
	    		android.R.id.text2,
	    		R.id.text3
	    };

	    BaseCursorAdapter adapter = new BaseCursorAdapter(this, R.layout.list_item_simple_3, null, from,
	        to, 0);
	    
	    adapter.setViewBinder(new ViewBinder() {
			@Override
			public boolean setViewValue(View view, Cursor cursor, int colIndex) {
				// Format the price
				if (view.getId() == R.id.text3) {
					double price = CursorUtils.getRecordFloatValue(cursor, OrderDetailTable.COLUMN_TOTAL_PRICE);
					TextView priceView = (TextView)view;
					priceView.setText(GeneralUtils.formatCurrency(price));

					return true;
				}
				return false;
			}
		});
	    
	    return adapter;
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = { 
				OrderDetailTable._ID, 
				ProductTable.COLUMN_NAME, 
				OrderDetailTable.COLUMN_QUANTITY,
	    		OrderDetailTable.COLUMN_TOTAL_PRICE
		};
		String selection = null;
		String[] selectionArgs = null;
		
		// If called from order form then order ID is passed along
		if (orderID > 0) {
			selection = OrderDetailTable.COLUMN_ORDER_ID + "=?";
			selectionArgs = new String[] {
					String.valueOf(orderID)
			};
		}

		String query = getSearchQuery();

		if (!TextUtils.isEmpty(query)) {
			selection = (selection != null ?  (selection + " AND ") : "") + ProductTable.COLUMN_NAME + " LIKE ?";
			
			if (selectionArgs == null) {
				selectionArgs = new String[] { 
						"%" + query + "%" 
				};
			}
			else {
				selectionArgs = new String[] { 
						String.valueOf(orderID),
						"%" + query + "%" 
				};
			}
		}

	    CursorLoader cursorLoader = new CursorLoader(this,
	        SaovietCRMContentProvider.ContentUri.ORDER_DETAIL, projection, selection, selectionArgs, null);

	    return cursorLoader;
	}
	
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// Get total price
		if (data.getCount() > 0) {
			double totalPrice = 0;
			data.moveToFirst();

			do {
				totalPrice += CursorUtils.getRecordFloatValue(data, OrderDetailTable.COLUMN_TOTAL_PRICE);
			} while (data.moveToNext());

			if (totalPrice > 0) {
				lblTotalPrice.setText(GeneralUtils.formatCurrency(totalPrice));
			}

			// Move back to first
			data.moveToFirst();
		}

		super.onLoadFinished(loader, data);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void addNewItem() {
		Class<?> formClass = getFormActivityClass();
		if (formClass != null) {
			Intent intent = new Intent(this, formClass);

			if (orderID > 0) {
				intent.putExtra(OrderDetailTable.COLUMN_ORDER_ID, orderID);
			}
			startActivity(intent);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Class<?> viewClass = getViewActivityClass();
		Class<?> formClass = getFormActivityClass();

		Intent intent = new Intent(this, viewClass != null ? viewClass : formClass);
		intent.putExtra(BaseFormActivity.ITEM_ID, id);
		if (orderID > 0) {
			intent.putExtra(OrderDetailTable.COLUMN_ORDER_ID, orderID);
		}

		startActivity(intent);
	}
}

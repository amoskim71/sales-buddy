package com.ngocketit.saovietcrm.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.ngocketit.saovietcrm.R;
import com.ngocketit.saovietcrm.adapter.PriceCalculatorListAdapter;
import com.ngocketit.saovietcrm.model.PriceCalculatorLineItem;
import com.ngocketit.saovietcrm.util.GeneralUtils;

public class PriceCalculatorListActivity extends LoggedInRequiredActivity implements OnItemClickListener {
	private static final int ORDER_LINE_ITEM_INTENT = 0x1;
	public static final String ORDER_LINE_ITEM = "line_item";
	private static long sItemCount = 1;
	
	private ListView mListView;
	private List<PriceCalculatorLineItem> mLineItems;
	private TextView lblTotalPrice;
	private PriceCalculatorListAdapter mAdapter;
	private EditText txtDiscount;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_order_line_item_list);

		lblTotalPrice = (TextView)findViewById(R.id.lblTotalPrice);
		txtDiscount = (EditText)findViewById(R.id.txtDiscount);
		txtDiscount.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				calculateTotalPrice();
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
			}
		});
		
		mLineItems = new ArrayList<PriceCalculatorLineItem>();
		mListView = (ListView)findViewById(R.id.listview);
		mAdapter = new PriceCalculatorListAdapter(this, 0, mLineItems);

		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		
		mListView.setEmptyView(findViewById(R.id.list_empty));

		calculateTotalPrice();
	}
	
	@Override
	protected int getTitleResource() {
		return R.string.calculator;
	}
	
	public void calculateTotalPrice() {
		double totalPrice = 0;

		for(PriceCalculatorLineItem item: mLineItems) {
			totalPrice += item.totalPrice;
		}
		
		if (!TextUtils.isEmpty(txtDiscount.getText())) {
			double discount = Double.parseDouble(txtDiscount.getText().toString());
			if (discount <= 100) {
				totalPrice *= (100 - discount)/100;
			}
		}
		
		lblTotalPrice.setText(GeneralUtils.formatCurrency(totalPrice));
	}

	@Override
	protected int getMenuResource() {
		return R.menu.menu_base_list;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(getMenuResource(), menu);

		// Search widget
		MenuItem searchItem = menu.findItem(R.id.action_search);
		searchItem.setVisible(false);

		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
			
		case R.id.action_add:
			addNewItem();
			return true;

		case R.id.action_delete:
			mAdapter.toggleDeleteMode();
			return true;
	
		default:
			return super.onOptionsItemSelected(item);
		}
	}	
	
	private void addNewItem() {
		Intent intent = new Intent(this, PriceCalculatorLineItemFormActivity.class);
		startActivityForResult(intent, ORDER_LINE_ITEM_INTENT);
	}
	
	private long getNextItemId() {
		return ++sItemCount;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent result) {
		super.onActivityResult(requestCode, resultCode, result);
		
		if (requestCode == ORDER_LINE_ITEM_INTENT && resultCode == RESULT_OK) {
			PriceCalculatorLineItem item = (PriceCalculatorLineItem)result.getParcelableExtra(ORDER_LINE_ITEM);

			if (item != null) {
				// New item
				if (item.id <= 0) {
					item.id = getNextItemId();
					mLineItems.add(item);
				} else {
					// Find existing item and update its data
					for(PriceCalculatorLineItem oneItem: mLineItems) {
						if (oneItem.id == item.id) {
							oneItem.copy(item);
							break;
						}
					}
				}

				mAdapter.notifyDataSetChanged();
				calculateTotalPrice();
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(this, PriceCalculatorLineItemFormActivity.class);
		PriceCalculatorLineItem item = mLineItems.get(position);
		intent.putExtra(ORDER_LINE_ITEM, item);
		startActivityForResult(intent, ORDER_LINE_ITEM_INTENT);
	}
}

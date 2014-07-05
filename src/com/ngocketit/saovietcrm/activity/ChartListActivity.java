package com.ngocketit.saovietcrm.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.ngocketit.saovietcrm.R;
import com.ngocketit.saovietcrm.adapter.HomeMenuListAdapter;
import com.ngocketit.saovietcrm.model.ListMenuItem;

public class ChartListActivity extends LoggedInRequiredActivity implements OnItemClickListener {
	ListView mListView;
	List<ListMenuItem> mItems;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_base_list);
		mListView = (ListView)findViewById(R.id.listview);
		mListView.setOnItemClickListener(this);
		
		initAdapter();
	}
	
	private void initAdapter() {
		mItems = new ArrayList<ListMenuItem>();

		mItems.add(new ListMenuItem(R.string.sales_revenue, 
				R.drawable.saoviet_ic_chart,
				SalesRevenueChartViewActivity.class));

		mItems.add(new ListMenuItem(R.string.sales_growth, 
				R.drawable.saoviet_ic_chart,
				null));

		mItems.add(new ListMenuItem(R.string.sales_focast, 
				R.drawable.saoviet_ic_chart,
				null));

		mItems.add(new ListMenuItem(R.string.orders_chart, 
				R.drawable.saoviet_ic_chart,
				null));

		mListView.setAdapter(new HomeMenuListAdapter(this, 0, mItems));
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		ListMenuItem item = mItems.get(position);
		
		Class<?> intentClass = item.intentClass;
		if (intentClass != null) {
			Intent intent = new Intent(this, intentClass);
			startActivity(intent);
		}
	}
}

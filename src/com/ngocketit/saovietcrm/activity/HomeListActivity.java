package com.ngocketit.saovietcrm.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.ngocketit.saovietcrm.R;
import com.ngocketit.saovietcrm.adapter.HomeMenuListAdapter;
import com.ngocketit.saovietcrm.app.SaovietCRMApp;
import com.ngocketit.saovietcrm.database.UserAccountTable;
import com.ngocketit.saovietcrm.model.ListMenuItem;
import com.ngocketit.saovietcrm.model.UserAccountCredential;
import com.ngocketit.saovietcrm.provider.SaovietCRMContentProvider;

public class HomeListActivity extends LoggedInRequiredActivity implements OnItemClickListener {
	public static HomeListActivity home;
	
	ListView mListView;
	TextView lblCurrentUser;
	
	List<ListMenuItem> mItems;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
			
	    ActionBar actionBar = getSupportActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(false);

		setContentView(R.layout.activity_home_list);
		
		lblCurrentUser = (TextView)findViewById(R.id.lblCurrentUser);
		mListView = (ListView)findViewById(R.id.listview);
		mListView.setOnItemClickListener(this);
		
		welcomeUser();
		initAdapter();
		
		home = this;
	}

	private void doLogout() {
		SaovietCRMApp app = (SaovietCRMApp)getApplication();

		ContentValues values = new ContentValues();
		values.put(UserAccountTable.COLUMN_STATUS, UserAccountTable.USER_ACCOUNT_STATUS_ACTIVE);

		// Set all users as not logged-in
		getContentResolver().update(SaovietCRMContentProvider.ContentUri.USER_ACCOUNT, values, null, null);

		// Reset the cache in application
		app.setCurrentUserCredential(null);
		
		// Show login form
		Intent loginIntent = new Intent(this, UserLoginActivity.class);
		startActivity(loginIntent);

		finish();
	}

	public void welcomeUser() {
		SaovietCRMApp app = (SaovietCRMApp)getApplication();
		UserAccountCredential cred = app.getCurrentUserCredential();

		if (cred != null) {
			lblCurrentUser.setText(cred.fullName);
		}
	}

	private void initAdapter() {
		mItems = new ArrayList<ListMenuItem>();
		
		mItems.add(new ListMenuItem(R.string.customer_management, 
				R.drawable.saoviet_ic_group,
				CustomerListActivity.class));

		mItems.add(new ListMenuItem(R.string.product_management, 
				R.drawable.saoviet_ic_lightbulb,
				ProductListActivity.class));

		mItems.add(new ListMenuItem(R.string.order_management, 
				R.drawable.saoviet_ic_list,
				OrderListActivity.class));

		mItems.add(new ListMenuItem(R.string.saleline_management, 
				R.drawable.saoviet_ic_sitemap,
				SaleLineListActivity.class));

		mItems.add(new ListMenuItem(R.string.product_cat_management, 
				R.drawable.saoviet_ic_storage,
				ProductCategoryListActivity.class));

		mItems.add(new ListMenuItem(R.string.calculator, 
				R.drawable.saoviet_ic_grid,
				PriceCalculatorListActivity.class));

		mItems.add(new ListMenuItem(R.string.charts, 
				R.drawable.saoviet_ic_chart,
				ChartListActivity.class));

		mItems.add(new ListMenuItem(R.string.settings_tools, 
				R.drawable.saoviet_ic_settings,
				SettingsToolsActivity.class));

		mItems.add(new ListMenuItem(R.string.about_help, 
				R.drawable.saoviet_ic_help,
				AboutHelpActivity.class));

		mItems.add(new ListMenuItem(R.string.logout, 
				R.drawable.saoviet_ic_signout,
				null));
		
		mListView.setAdapter(new HomeMenuListAdapter(this, 0, mItems));
	}

	@Override
	public void onItemClick(AdapterView<?> group, View view, int position, long id) {
		ListMenuItem item = mItems.get(position);
		int length = mItems.size();
		
		if (position == length - 1) {
			doLogout();
		}
		else {
			Class<?> intentClass = item.intentClass;
			if (intentClass != null) {
				Intent intent = new Intent(this, intentClass);
				startActivity(intent);
			}
		}
	}
}

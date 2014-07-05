package com.ngocketit.saovietcrm.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.ngocketit.saovietcrm.R;
import com.ngocketit.saovietcrm.adapter.HomeMenuListAdapter;
import com.ngocketit.saovietcrm.app.SaovietCRMApp;
import com.ngocketit.saovietcrm.model.ListMenuItem;
import com.ngocketit.saovietcrm.model.UserAccountCredential;

public class SettingsToolsActivity extends LoggedInRequiredActivity implements OnItemClickListener {
	ListView mListView;
	Button btnSettings;
	
	List<ListMenuItem> mItems;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
			
		setContentView(R.layout.activity_settings_tools);
		
		btnSettings = (Button)findViewById(R.id.btnSettings);
		btnSettings.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				launchSettings();
			}
		});
		
		mListView = (ListView)findViewById(R.id.listview);
		mListView.setOnItemClickListener(this);

		initAdapter();
	}
	
	@Override
	protected int getTitleResource() {
		return R.string.settings_tools;
	}

	private void launchSettings() {
		Intent settingsIntent = new Intent(this, UserSignupActivity.class);

		SaovietCRMApp app = (SaovietCRMApp)getApplication();
		UserAccountCredential currentUser = app.getCurrentUserCredential();
		settingsIntent.putExtra(BaseFormActivity.ITEM_ID, currentUser.id);

		startActivity(settingsIntent);
	}
	
	private void initAdapter() {
		mItems = new ArrayList<ListMenuItem>();
		
		mItems.add(new ListMenuItem(R.string.import_customers, 
				R.drawable.saoviet_ic_import,
				CustomerImportFormActivity.class));

		mItems.add(new ListMenuItem(R.string.import_product_cats, 
				R.drawable.saoviet_ic_import,
				ProductCategoryImportFormActivity.class));

		mItems.add(new ListMenuItem(R.string.import_products, 
				R.drawable.saoviet_ic_import,
				ProductImportFormActivity.class));

		mItems.add(new ListMenuItem(R.string.import_lines, 
				R.drawable.saoviet_ic_import,
				SaleLineImportFormActivity.class));

		mListView.setAdapter(new HomeMenuListAdapter(this, 0, mItems));
	}

	@Override
	public void onItemClick(AdapterView<?> group, View view, int position, long id) {
		ListMenuItem item = mItems.get(position);
		
		Class<?> intentClass = item.intentClass;
		if (intentClass != null) {
			Intent intent = new Intent(this, intentClass);
			startActivity(intent);
		}
	}
}

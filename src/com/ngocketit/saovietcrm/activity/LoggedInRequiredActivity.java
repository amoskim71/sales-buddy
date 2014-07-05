package com.ngocketit.saovietcrm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;

import com.ngocketit.saovietcrm.app.SaovietCRMApp;
import com.ngocketit.saovietcrm.model.UserAccountCredential;

public class LoggedInRequiredActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		checkLoginRequired();

		// Enable up navigation for the app icon
	    ActionBar actionBar = getSupportActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    actionBar.setDisplayShowTitleEnabled(true);
	}
	
	protected void checkLoginRequired() {
		// Check if user is logged in
		SaovietCRMApp app = (SaovietCRMApp)getApplication();
		UserAccountCredential currentUser = app.getCurrentUserCredential();

		if (currentUser == null) {
			Intent loginIntent = new Intent(this, UserLoginActivity.class);
			startActivity(loginIntent);
			finish();
		}	
	}
}

package com.ngocketit.saovietcrm.activity;

import java.util.Locale;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.ngocketit.saovietcrm.app.SaovietCRMApp;
import com.ngocketit.saovietcrm.model.UserAccountCredential;

public class BaseActivity extends ActionBarActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Update locale to the one chosen by current user
		setCurrentUserLocale();
		
		// Update title after locale is set
		int titleResId = getTitleResource();
		if (titleResId > 0) {
			setTitle(titleResId);
		}
	}
	
	private void setCurrentUserLocale() {
		SaovietCRMApp app = (SaovietCRMApp)getApplication();
		UserAccountCredential cred = app.getCurrentUserCredential();
		
		if (cred != null) {
			Locale locale = app.getCurrentUserLocale();

			if (locale != null) {
				Locale.setDefault(locale);

				Configuration config = new Configuration();
				config.locale = locale;
				this.getResources().updateConfiguration(config, null);
			}
		}
	}
	
	protected int getTitleResource() {
		return 0;
	}

	protected int getMenuResource() {
		return 0;
	}
}

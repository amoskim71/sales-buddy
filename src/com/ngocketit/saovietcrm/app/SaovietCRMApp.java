package com.ngocketit.saovietcrm.app;

import java.util.Locale;

import android.app.Application;
import android.content.res.Configuration;

import com.ngocketit.saovietcrm.model.UserAccountCredential;

public class SaovietCRMApp extends Application {
	// Default user locale when app starts without user's selected preference
	public static final String defaultUserLocale = "vi_VN";

	private UserAccountCredential mCurrentUser;
	private Locale mCurrentUserLocale;
	
	public static final String[] supportedLocales = {
		"en_US",
		"vi_VN"
	};
	
	public void setCurrentUserCredential(UserAccountCredential user) {
		mCurrentUser = user;
	}
	
	public UserAccountCredential getCurrentUserCredential() {
		return mCurrentUser;
	}
	
	public Locale getCurrentUserLocale() {
		return mCurrentUserLocale;
	}
	
	public void setCurrentUserLocale() {
		if (mCurrentUser != null) {
			changeLocale(mCurrentUser.locale);
		}
	}
	
	public void setDefaultLocale() {
		changeLocale(defaultUserLocale);
	}
	
	public void changeLocale(String code) {
		boolean valid = false;
		
		for(String lolCode : supportedLocales) {
			if (lolCode.equals(code)) {
				valid = true;
				break;
			}
		}
		
		if (!valid) {
			code = "vi_VN";
		}
		 
		mCurrentUserLocale = new Locale(code); 
		Locale.setDefault(mCurrentUserLocale);
		Configuration config = new Configuration();
		config.locale = mCurrentUserLocale;
		getApplicationContext().getResources().updateConfiguration(config, null);	
	}
}

package com.ngocketit.saovietcrm.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.ngocketit.saovietcrm.R;
import com.ngocketit.saovietcrm.app.SaovietCRMApp;
import com.ngocketit.saovietcrm.database.UserAccountTable;
import com.ngocketit.saovietcrm.model.UserAccountCredential;
import com.ngocketit.saovietcrm.provider.SaovietCRMContentProvider;
import com.ngocketit.saovietcrm.util.CursorUtils;

public class SplashScreenActivity extends BaseActivity {
	private static final int SPLASH_TIME_OUT = 1000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_splash_screen);
		
		setContentView(R.layout.activity_splash_screen);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				checkUserLogin();
			}
		}, SPLASH_TIME_OUT);
	}
	
	private void checkUserLogin() {
		Uri uri = SaovietCRMContentProvider.ContentUri.USER_ACCOUNT;

		String[] projection = {
				UserAccountTable._ID,
				UserAccountTable.COLUMN_FULLNAME,
				UserAccountTable.COLUMN_STATUS,
				UserAccountTable.COLUMN_PASSWORD,
				UserAccountTable.COLUMN_EMAIL,
				UserAccountTable.COLUMN_LANGUAGE
		};

		// Select user with logged in state
		String selection = UserAccountTable.COLUMN_STATUS + "=?";
		String[] selectionArgs = {
				String.valueOf(UserAccountTable.USER_ACCOUNT_STATUS_LOGGED_IN)
		};

		Cursor cursor = getContentResolver().query(uri, projection, selection, selectionArgs, null);

		SaovietCRMApp app = (SaovietCRMApp)getApplication();

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			
			String langCode = CursorUtils.getRecordStringValue(cursor, UserAccountTable.COLUMN_LANGUAGE);

			UserAccountCredential currentUser = UserAccountCredential.readFromCursor(cursor);
			app.setCurrentUserCredential(currentUser);
			
			// Change language
			app.changeLocale(langCode);

			Intent mainIntent = new Intent(this, HomeListActivity.class); 
			startActivity(mainIntent);
		} else {
			// Use vi_VN as default locale
			app.setDefaultLocale();
			
			Intent loginIntent = new Intent(this, UserLoginActivity.class); 
			startActivity(loginIntent);
		}

		finish();
	}
}

package com.ngocketit.saovietcrm.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.ngocketit.saovietcrm.R;
import com.ngocketit.saovietcrm.app.SaovietCRMApp;
import com.ngocketit.saovietcrm.database.UserAccountTable;
import com.ngocketit.saovietcrm.model.UserAccountCredential;
import com.ngocketit.saovietcrm.provider.SaovietCRMContentProvider;
import com.ngocketit.saovietcrm.util.GeneralUtils;

public class UserLoginActivity extends BaseActivity implements OnClickListener {
	private Button btnLogin;
	private Button btnSignup;
	private EditText txtEmail;
	private EditText txtPassword;
	private CheckBox chkRemember;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_login);
		
		txtEmail = (EditText)findViewById(R.id.txtEmail);
		txtPassword = (EditText)findViewById(R.id.txtPassword);
		
		chkRemember = (CheckBox)findViewById(R.id.chkRememberMe);
		
		btnLogin = (Button)findViewById(R.id.btnLogin);
		btnSignup = (Button)findViewById(R.id.btnSignUp);
		
		btnLogin.setOnClickListener(this);
		btnSignup.setOnClickListener(this);
	}
	
	@Override
	protected int getTitleResource() {
		return R.string.login;
	}

	@Override
	public void onClick(View view) {
		if (view == btnLogin) {
			doUserLogin();
		}
		else if (view == btnSignup) {
			Intent signupIntent = new Intent(this, UserSignupActivity.class);
			startActivity(signupIntent);
		}
	}
	
	private void doUserLogin() {		
		String email = txtEmail.getText().toString();
		String passd = txtPassword.getText().toString();
		
		if (TextUtils.isEmpty(email) || TextUtils.isEmpty(passd)) {
			Toast.makeText(this, getResources().getString(R.string.user_login_empty_fields), Toast.LENGTH_LONG).show();
			return;
		}
		
		// Get the hash of the password
		passd = GeneralUtils.md5(passd);
		
		Uri uri = SaovietCRMContentProvider.ContentUri.USER_ACCOUNT;

		String[] projection = {
				UserAccountTable._ID,
				UserAccountTable.COLUMN_FULLNAME,
				UserAccountTable.COLUMN_STATUS,
				UserAccountTable.COLUMN_PASSWORD,
				UserAccountTable.COLUMN_EMAIL,
				UserAccountTable.COLUMN_LANGUAGE
		};

		String selection = UserAccountTable.COLUMN_EMAIL + "=? AND " + UserAccountTable.COLUMN_PASSWORD + "=?";
		String[] selectionArgs = { email, passd };

		Cursor cursor = getContentResolver().query(uri, projection, selection, selectionArgs, null);

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			UserAccountCredential currentUser = UserAccountCredential.readFromCursor(cursor);
			
			SaovietCRMApp app = (SaovietCRMApp)getApplication();
			app.setCurrentUserCredential(currentUser);

			// Mark user status
			if (chkRemember.isChecked()) {
				// Set all users as not logged-in
				ContentValues values = new ContentValues();
				values.put(UserAccountTable.COLUMN_STATUS, UserAccountTable.USER_ACCOUNT_STATUS_ACTIVE);
				getContentResolver().update(SaovietCRMContentProvider.ContentUri.USER_ACCOUNT, values, null, null);
				
				// Mark this user as logged in. Only one user can be remembered
				values.put(UserAccountTable.COLUMN_STATUS, UserAccountTable.USER_ACCOUNT_STATUS_LOGGED_IN);
				String where = UserAccountTable._ID + "=?";
				String[] whereArgs = { String.valueOf(currentUser.id) };

				getContentResolver().update(uri, values, where, whereArgs);
			}
			
			Intent mainIntent = new Intent(this, HomeListActivity.class); 
			startActivity(mainIntent);

			// We're done with this activity
			finish();
		} else {
			Toast.makeText(this, getResources().getString(R.string.user_login_fail), Toast.LENGTH_LONG).show();
		}
	}
}

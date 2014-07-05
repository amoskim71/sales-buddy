package com.ngocketit.saovietcrm.activity;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.ngocketit.saovietcrm.R;
import com.ngocketit.saovietcrm.app.SaovietCRMApp;
import com.ngocketit.saovietcrm.database.BaseTable;
import com.ngocketit.saovietcrm.database.UserAccountTable;
import com.ngocketit.saovietcrm.fragment.DatePickerFragment;
import com.ngocketit.saovietcrm.model.UserAccountCredential;
import com.ngocketit.saovietcrm.provider.SaovietCRMContentProvider;
import com.ngocketit.saovietcrm.util.CursorUtils;

public class UserSignupActivity extends BaseFormActivity { 
	private Button btnSignup;
	private EditText txtDob;
	private EditText txtFullName;
	private EditText txtEmail;
	private EditText txtPassword;
	private EditText txtPasswordConfirm;
	private EditText txtPhone;

	private Spinner spnGender;
	private Spinner spnLanguage;
	
	private ImageButton btnPickDob;
	
	private String[] languages = {
			"en_US",
			"vi_VN"
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getItemId() > 0) {
			btnSignup.setVisibility(View.GONE);
		}
	}
	
	@Override
	protected int getEditModeTitle() {
		return R.string.settings;
	}
	
	@Override
	protected int getTitleResource() {
		return R.string.signup;
	}
	
	@Override
	protected void checkLoginRequired() {
		// Login is not required for this
		return;
	}
	
	@Override
	protected void initContentView() {
		super.initContentView();

		btnSignup = (Button)findViewById(R.id.btnSignUp);
		
		txtDob = (EditText)findViewById(R.id.txtDob);
		txtFullName = (EditText)findViewById(R.id.txtFullName);
		txtEmail = (EditText)findViewById(R.id.txtEmail);
		txtPassword = (EditText)findViewById(R.id.txtPassword);
		txtPasswordConfirm = (EditText)findViewById(R.id.txtPasswordConfirm);
		txtPhone = (EditText)findViewById(R.id.txtPhone);

		spnGender = (Spinner)findViewById(R.id.spnGender);
		spnLanguage = (Spinner)findViewById(R.id.spnLanguage);
		spnLanguage.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapter, View view,
					int position, long id) {
				if (position > 0) {
					Toast.makeText(UserSignupActivity.this, R.string.change_lang_alert, Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapter) {
			}
		});
		
		btnSignup.setOnClickListener(this);
		
		btnPickDob = (ImageButton)findViewById(R.id.btnPickDob);
		btnPickDob.setOnClickListener(this);
	}
	
	@Override
	protected int getContentView() {
		return R.layout.activity_user_signup;
	}	
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
	
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected Uri getItemContentUri() {
		return SaovietCRMContentProvider.ContentUri.USER_ACCOUNT;
	}
	
	@Override
	protected boolean validateForm() {		
		String fullName = txtFullName.getText().toString();
		String dob = txtDob.getText().toString();
		String email = txtEmail.getText().toString();
		String password = txtPassword.getText().toString();
		String passwordConfirm = txtPasswordConfirm.getText().toString();
		String phone = txtPhone.getText().toString();
		
		// Check form validity
		if (TextUtils.isEmpty(fullName)) {
			showFormElementError(R.string.user_alert_name, txtFullName);
			return false;
		}

		if (TextUtils.isEmpty(email)) {
			showFormElementError(R.string.user_alert_email, txtEmail);
			return false;
		}

		if (TextUtils.isEmpty(phone)) {
			showFormElementError(R.string.user_alert_phone, txtPhone);
			return false;
		}
		
		if (getItemId() <= 0 && TextUtils.isEmpty(password)) {
			showFormElementError(R.string.user_alert_password, txtPassword);
			return false;
		}
		
		if (!TextUtils.isEmpty(password) && password.compareTo(passwordConfirm) != 0) {
			showFormElementError(R.string.user_alert_password_confirm, txtPassword);
			return false;
		}
		
		return true;
	}

	@Override
	protected void loadItem(long itemId) {
		Cursor cursor = loadItemFromDatabase(itemId);

		if (cursor != null && cursor.getCount() == 1) {
			txtFullName.setText(CursorUtils.getRecordStringValue(cursor, UserAccountTable.COLUMN_FULLNAME));
			txtEmail.setText(CursorUtils.getRecordStringValue(cursor, UserAccountTable.COLUMN_EMAIL));
			txtDob.setText(CursorUtils.getRecordStringValue(cursor, UserAccountTable.COLUMN_DOB));
			txtPhone.setText(CursorUtils.getRecordStringValue(cursor, UserAccountTable.COLUMN_PHONE));
			
			spnGender.setSelection(CursorUtils.getRecordIntValue(cursor, UserAccountTable.COLUMN_GENDER));
			
			String langCode = CursorUtils.getRecordStringValue(cursor, UserAccountTable.COLUMN_LANGUAGE);

			int langIdx = 0;
			for(int i = 0; i < languages.length; i++) {
				if (languages[i].equals(langCode)) {
					langIdx = i + 1;
					break;
				}
			}
			spnLanguage.setSelection(langIdx);
			
			cursor.close();
		}
	}
	
	@Override
	protected void saveItem() {
		super.saveItem();
		String email = txtEmail.getText().toString();
		
		if (getItemId() <= 0 && isUserExisting(email)) {
			Toast.makeText(this, R.string.user_email_exist_alert, Toast.LENGTH_LONG).show();
			txtEmail.requestFocus();
			return;
		}
		
		String fullName = txtFullName.getText().toString();
		String dob = txtDob.getText().toString();
		String password = txtPassword.getText().toString();
		String phone = txtPhone.getText().toString();
		int gender = spnGender.getSelectedItemPosition();
		
		ContentValues values = new ContentValues();
		values.put(UserAccountTable.COLUMN_EMAIL, email);
		values.put(UserAccountTable.COLUMN_FULLNAME, fullName);
		values.put(UserAccountTable.COLUMN_PHONE, phone);
		values.put(UserAccountTable.COLUMN_DOB, dob);
		values.put(UserAccountTable.COLUMN_GENDER, gender);
		values.put(UserAccountTable.COLUMN_PASSWORD, password);
		
		int langIdx = spnLanguage.getSelectedItemPosition();
		if (langIdx == 0) {
			langIdx = 1;
		} else {
			langIdx--;
		}
		
		values.put(UserAccountTable.COLUMN_LANGUAGE, languages[langIdx]);
		
		if (!saveItemToDatabase(values)) {
			Toast.makeText(this, R.string.user_signup_fail, Toast.LENGTH_LONG).show();
		} else {
			// Update user name if it's changed
			if (getItemId() > 0) {
				SaovietCRMApp app = (SaovietCRMApp)getApplication();
				UserAccountCredential cred = app.getCurrentUserCredential();

				if (cred != null) {
					cred.fullName = fullName;
					HomeListActivity home = HomeListActivity.home;

					if (home != null) {
						home.welcomeUser();
					}
				}
			}
		}
	}
	
	private boolean isUserExisting(String email) {
		String[] projection = {
				BaseTable._ID,
				UserAccountTable.COLUMN_EMAIL
		};
		String selection = UserAccountTable.COLUMN_EMAIL + "=?";
		String[] selectionArgs = {
				email
		};

		Cursor cursor = getContentResolver().query(SaovietCRMContentProvider.ContentUri.USER_ACCOUNT, 
				projection, 
				selection, 
				selectionArgs, 
				null);
		
		if (cursor != null) {
			boolean existing = cursor.getCount() > 0;
			cursor.close();
			return existing;
		}

		return false;
	}
	
	@Override
	protected void deleteItem() {
		super.deleteItem();
		doLogout();
	}

	private void doLogout() {
		SaovietCRMApp app = (SaovietCRMApp)getApplication();

		// Reset the cache in application
		app.setCurrentUserCredential(null);

		// Show login form
		Intent loginIntent = new Intent(this, UserLoginActivity.class);
		loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(loginIntent);

		HomeListActivity.home.finish();
	}

	@Override
	public void onClick(View v) {
		if (v == btnSignup) {
			doSaveItem();
		}
		else if (v == btnPickDob) {
			doPickDoB();
		}
	}
	
	private void doPickDoB() {
		DatePickerFragment datePicker = new DatePickerFragment();

		datePicker.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
			public void onDateSet(android.widget.DatePicker view, int year,
					int monthOfYear, int dayOfMonth) {
				txtDob.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
			}
		});
		
		String dob = txtDob.getText().toString();

		if (!TextUtils.isEmpty(dob)) {
			String[] parts = dob.split("/");

			if (parts.length == 3) {
				int day = Integer.parseInt(parts[0]);
				int month = Integer.parseInt(parts[1]);
				int year = Integer.parseInt(parts[2]);
				
				datePicker.setInitDate(year, month - 1, day);
			}
		}
		
		datePicker.show(getSupportFragmentManager(), "datePicker");
	}
}

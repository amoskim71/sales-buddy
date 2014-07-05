package com.ngocketit.saovietcrm.model;

import android.database.Cursor;

import com.ngocketit.saovietcrm.database.UserAccountTable;
import com.ngocketit.saovietcrm.util.CursorUtils;

public class UserAccountCredential {
	public long id;
	public String email;
	public String password;
	public String fullName;
	public String locale;
	
	public static UserAccountCredential readFromCursor(Cursor cursor) {
		UserAccountCredential cred = new UserAccountCredential();

		cred.id = CursorUtils.getRecordIntValue(cursor, UserAccountTable._ID);
		cred.email = CursorUtils.getRecordStringValue(cursor, UserAccountTable.COLUMN_EMAIL);
		cred.password = CursorUtils.getRecordStringValue(cursor, UserAccountTable.COLUMN_PASSWORD);
		cred.fullName = CursorUtils.getRecordStringValue(cursor, UserAccountTable.COLUMN_FULLNAME);
		cred.locale = CursorUtils.getRecordStringValue(cursor, UserAccountTable.COLUMN_LANGUAGE);
		
		return cred;
	}
}

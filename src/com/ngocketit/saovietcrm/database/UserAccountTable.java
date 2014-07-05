package com.ngocketit.saovietcrm.database;

import java.util.HashMap;

import android.database.sqlite.SQLiteDatabase;

public class UserAccountTable extends BaseTable {
	// Name of the table
	public static final String TABLE_NAME = "user_accounts";
	
	// Table column definitions
	public static final String COLUMN_EMAIL = "email";
	public static final String COLUMN_FULLNAME = "full_name";
	public static final String COLUMN_PASSWORD = "password";
	public static final String COLUMN_PHONE = "phone";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_DEPARTMENT = "department";
	public static final String COLUMN_DOB = "dob";
	public static final String COLUMN_GENDER = "gender";
	public static final String COLUMN_LANGUAGE = "language";
	public static final String COLUMN_STATUS = "status";
	
	// User account status definitions
	public static final int USER_ACCOUNT_STATUS_DISABLED = -2;
	public static final int USER_ACCOUNT_STATUS_ACTIVE = 0;
	public static final int USER_ACCOUNT_STATUS_LOGGED_IN = -1;
	
	public static final int USER_GENDER_MALE = 0;
	public static final int USER_GENDER_FEMALE = 1;
	
	// Query to create table structure
	public static final String TABLE_CREATE = "CREATE TABLE "
			+ TABLE_NAME
			+ "("
			+ BASE_FIELDS_CREATE
			+ COLUMN_FULLNAME + " text not null,"
			+ COLUMN_EMAIL + " text not null,"
			+ COLUMN_PASSWORD + " text not null,"
			+ COLUMN_TITLE + " text,"
			+ COLUMN_DEPARTMENT + " text,"
			+ COLUMN_DOB + " text,"
			+ COLUMN_LANGUAGE + " text default 'vi_VN',"
			+ COLUMN_GENDER + " integer default " + USER_GENDER_MALE + ","
			+ COLUMN_STATUS + " integer default " + USER_ACCOUNT_STATUS_ACTIVE + ","
			+ COLUMN_PHONE + " text not null"
			+ ")";
	
	public static HashMap<String, String> getProjectionMap() {
		HashMap<String, String> map = BaseTable.getProjectionMap();
		
		map.put(COLUMN_FULLNAME, COLUMN_FULLNAME);
		map.put(COLUMN_EMAIL, COLUMN_EMAIL);
		map.put(COLUMN_PASSWORD, COLUMN_PASSWORD);
		map.put(COLUMN_TITLE, COLUMN_TITLE);
		map.put(COLUMN_DEPARTMENT, COLUMN_DEPARTMENT);
		map.put(COLUMN_DOB, COLUMN_DOB);
		map.put(COLUMN_GENDER, COLUMN_GENDER);
		map.put(COLUMN_LANGUAGE, COLUMN_LANGUAGE);
		map.put(COLUMN_STATUS, COLUMN_STATUS);
		map.put(COLUMN_PHONE, COLUMN_PHONE);

		return map;
	}
	
	public static void onCreate(SQLiteDatabase db) {
		BaseTable.onCreate(db);
		db.execSQL(TABLE_CREATE);
	}
	
	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		BaseTable.onUpgrade(db, oldVersion, newVersion);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}
}

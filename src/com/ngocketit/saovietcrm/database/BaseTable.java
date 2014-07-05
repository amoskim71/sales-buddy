package com.ngocketit.saovietcrm.database;

import java.util.HashMap;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

public class BaseTable implements BaseColumns {
	public static final String COLUMN_CREATED_AT = "created_at";
	public static final String COLUMN_UPDATED_AT = "updated_at";
	public static final String COLUMN_CREATED_BY = "created_by";
	public static final String COLUMN_UPDATED_BY = "updated_by";
	
	// This is not part of the schema but used as column alias in custom queries
	public static final String COLUMN_CREATED_DATE = "created_date";

	public static final String BASE_FIELDS_CREATE = _ID + " integer primary key autoincrement,"
			+ COLUMN_CREATED_AT + " text,"
			+ COLUMN_UPDATED_AT + " text,"
			+ COLUMN_CREATED_BY + " integer default 0,"
			+ COLUMN_UPDATED_BY + " integer default 0,";
	
	public static HashMap<String, String> getProjectionMap() {
		HashMap<String, String> map = new HashMap<String, String>();

		map.put(_ID, _ID);
		map.put(COLUMN_CREATED_AT, COLUMN_CREATED_AT);
		map.put(COLUMN_UPDATED_AT, COLUMN_UPDATED_AT);
		map.put(COLUMN_CREATED_BY, COLUMN_CREATED_BY);
		map.put(COLUMN_UPDATED_BY, COLUMN_UPDATED_BY);
		
		return map;
	}
	
	public static void onCreate(SQLiteDatabase db) {
	}
	
	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(BaseTable.class.getName(), "Upgrading from version " + oldVersion + " to version " + newVersion + ", which will destroy all the data");
	}
}

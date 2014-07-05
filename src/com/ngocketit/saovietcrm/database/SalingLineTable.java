package com.ngocketit.saovietcrm.database;

import java.util.HashMap;

import android.database.sqlite.SQLiteDatabase;

public class SalingLineTable extends BaseTable {
	public static final String TABLE_NAME = "saling_lines";
	
	// Table column definitions
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_ADDRESS = "address";
	
	public static final String TABLE_CREATE = "CREATE TABLE "
			+ TABLE_NAME
			+ "("
			+ BASE_FIELDS_CREATE
			+ COLUMN_NAME + " text not null,"
			+ COLUMN_ADDRESS + " text not null"
			+ ")";

	public static HashMap<String, String> getProjectionMap() {
		HashMap<String, String> map = BaseTable.getProjectionMap();
		
		map.put(COLUMN_NAME, COLUMN_NAME);
		map.put(COLUMN_ADDRESS, COLUMN_ADDRESS);
		
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

package com.ngocketit.saovietcrm.database;

import java.util.HashMap;

import android.database.sqlite.SQLiteDatabase;

public class CustomerTable extends BaseTable {
	public static final String TABLE_NAME = "customers";
	
	public static final String COLUMN_SHOP_NAME = "shop_name";
	public static final String COLUMN_OWNER_NAME = "owner_name";
	public static final String COLUMN_ADDRESS = "address";
	public static final String COLUMN_PHONE1 = "phone1";
	public static final String COLUMN_PHONE2 = "phone2";
	public static final String COLUMN_LINE_ID = "line_id";
	public static final String COLUMN_NOTE = "note";
	public static final String COLUMN_EVALUATION = "evaluation";
	public static final String COLUMN_RATE = "rate";
	public static final String COLUMN_TYPE = "type";
	
	// Customer type definitions
	public static final int CUSTOMER_TYPE_PRIVATE = 1;
	public static final int CUSTOMER_TYPE_COMPANY = 2;
	
	public static final String TABLE_CREATE = "CREATE TABLE "
			+ TABLE_NAME
			+ "("
			+ BASE_FIELDS_CREATE
			+ COLUMN_SHOP_NAME + " text,"
			+ COLUMN_OWNER_NAME + " text not null,"
			+ COLUMN_ADDRESS + " text not null,"
			+ COLUMN_PHONE1 + " text not null,"
			+ COLUMN_PHONE2 + " text,"
			+ COLUMN_LINE_ID + " integer,"
			+ COLUMN_NOTE + " text,"
			+ COLUMN_EVALUATION + " text,"
			+ COLUMN_RATE + " numeric,"
			+ COLUMN_TYPE + " int default " + CUSTOMER_TYPE_COMPANY
			+ ")";
	
	public static HashMap<String, String> getProjectionMap() {
		HashMap<String, String> map = BaseTable.getProjectionMap();

		map.put(COLUMN_SHOP_NAME, COLUMN_SHOP_NAME);
		map.put(COLUMN_OWNER_NAME, COLUMN_OWNER_NAME);
		map.put(COLUMN_ADDRESS, COLUMN_ADDRESS);
		map.put(COLUMN_PHONE1, COLUMN_PHONE1);
		map.put(COLUMN_PHONE2, COLUMN_PHONE2);
		map.put(COLUMN_LINE_ID, COLUMN_LINE_ID);
		map.put(COLUMN_NOTE, COLUMN_NOTE);
		map.put(COLUMN_EVALUATION, COLUMN_EVALUATION);
		map.put(COLUMN_RATE, COLUMN_RATE);
		map.put(COLUMN_TYPE, COLUMN_TYPE);

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

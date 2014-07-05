package com.ngocketit.saovietcrm.database;

import java.util.HashMap;

import android.database.sqlite.SQLiteDatabase;

public class OrderTable extends BaseTable {
	public static final String TABLE_NAME = "orders";
	
	// Column definitions
	public static final String COLUMN_CUSTOMER_ID = "customer_id";
	public static final String COLUMN_CUSTOMER_NAME = "customer_name";
	public static final String COLUMN_QUANTITY = "quantity";
	public static final String COLUMN_TYPE = "type";
	public static final String COLUMN_DISCOUNT = "discount_percent";
	public static final String COLUMN_TOTAL_PRICE = "total_order_value";
	
	// Order type definitions
	public static final int ORDER_TYPE_RETAIL = 1;
	public static final int ORDER_TYPE_WHOLESALE = 2;
	
	public static final String TABLE_CREATE = "CREATE TABLE "
			+ TABLE_NAME
			+ "("
			+ BASE_FIELDS_CREATE
			+ COLUMN_CUSTOMER_ID + " integer not null,"
			+ COLUMN_CUSTOMER_NAME + " text not null,"
			+ COLUMN_QUANTITY + " integer not null default 1,"
			+ COLUMN_TYPE + " integer not null default " + ORDER_TYPE_WHOLESALE + ","
			+ COLUMN_DISCOUNT + " numeric default 0.0"
			+ ")";
	
	public static HashMap<String, String> getProjectionMap() {
		HashMap<String, String> map = BaseTable.getProjectionMap();

		map.put(COLUMN_CUSTOMER_ID, COLUMN_CUSTOMER_ID);
		map.put(COLUMN_CUSTOMER_NAME, COLUMN_CUSTOMER_NAME);
		map.put(COLUMN_QUANTITY, COLUMN_QUANTITY);
		map.put(COLUMN_TYPE, COLUMN_TYPE);
		map.put(COLUMN_DISCOUNT, COLUMN_DISCOUNT);

		return map;
	}

	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_CREATE);
	}
	
	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}
}

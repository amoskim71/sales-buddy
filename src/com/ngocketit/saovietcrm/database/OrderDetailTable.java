package com.ngocketit.saovietcrm.database;

import java.util.HashMap;

import android.database.sqlite.SQLiteDatabase;

public class OrderDetailTable extends BaseTable {
	public static final String TABLE_NAME = "order_details";
	
	// Column definitions
	public static final String COLUMN_ORDER_ID = "order_id";
	public static final String COLUMN_QUANTITY = "quantity";
	public static final String COLUMN_PRODUCT_ID = "product_id";
	public static final String COLUMN_TOTAL_PRICE = "total_price";
	// Delivery quantity
	public static final String COLUMN_TOTAL_QUANTITY = "total_quantity";
	
	public static final String TABLE_CREATE = "CREATE TABLE "
			+ TABLE_NAME
			+ "("
			+ BASE_FIELDS_CREATE
			+ COLUMN_ORDER_ID + " integer not null,"
			+ COLUMN_QUANTITY + " integer not null default 1,"
			+ COLUMN_PRODUCT_ID + " integer not null,"
			+ COLUMN_TOTAL_PRICE + " numeric not null default 0.0,"
			+ COLUMN_TOTAL_QUANTITY + " integer not null default 0,"
			
			// Save product details so that changing product information
			// won't affect orders which have been made before
			+ ProductTable.COLUMN_NAME + " text not null,"
			+ ProductTable.COLUMN_CODE + " text not null,"
			+ ProductTable.COLUMN_BOGO_BUY + " integer default 0,"
			+ ProductTable.COLUMN_BOGO_GET + " integer default 0,"
			+ ProductTable.COLUMN_UNIT_PRICE + " real not null"
			+ ")";
	
	public static HashMap<String, String> getProjectionMap() {
		HashMap<String, String> map = BaseTable.getProjectionMap();

		map.put(COLUMN_ORDER_ID, COLUMN_ORDER_ID);
		map.put(COLUMN_QUANTITY, COLUMN_QUANTITY);
		map.put(COLUMN_PRODUCT_ID, COLUMN_PRODUCT_ID);
		map.put(COLUMN_TOTAL_PRICE, COLUMN_TOTAL_PRICE);
		map.put(COLUMN_TOTAL_QUANTITY, COLUMN_TOTAL_QUANTITY);

		map.put(ProductTable.COLUMN_NAME, ProductTable.COLUMN_NAME);
		map.put(ProductTable.COLUMN_CODE, ProductTable.COLUMN_CODE);
		map.put(ProductTable.COLUMN_BOGO_BUY, ProductTable.COLUMN_BOGO_BUY);
		map.put(ProductTable.COLUMN_BOGO_GET, ProductTable.COLUMN_BOGO_GET);
		map.put(ProductTable.COLUMN_UNIT_PRICE, ProductTable.COLUMN_UNIT_PRICE);

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

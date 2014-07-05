package com.ngocketit.saovietcrm.database;

import java.util.HashMap;

import android.database.sqlite.SQLiteDatabase;

public class ProductTable extends BaseTable {
	public static final String TABLE_NAME = "products";
	
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_CODE = "code";
	public static final String COLUMN_UNIT_PRICE = "unit_price";
	public static final String COLUMN_BOGO_BUY = "bogo_buy";
	public static final String COLUMN_BOGO_GET = "bogo_get";
	public static final String COLUMN_CATEGORY_ID = "category_id";
	
	public static final String TABLE_CREATE = "CREATE TABLE "
			+ TABLE_NAME
			+ "("
			+ BASE_FIELDS_CREATE
			+ COLUMN_NAME + " text not null,"
			+ COLUMN_CODE + " text not null,"
			+ COLUMN_CATEGORY_ID + " integer not null default 0,"
			+ COLUMN_BOGO_BUY + " integer default 0,"
			+ COLUMN_BOGO_GET + " integer default 0,"
			+ COLUMN_UNIT_PRICE + " real not null"
			+ ")";
	
	public static HashMap<String, String> getProjectionMap() {
		HashMap<String, String> map = BaseTable.getProjectionMap();

		map.put(COLUMN_NAME, COLUMN_NAME);
		map.put(COLUMN_CODE, COLUMN_CODE);
		map.put(COLUMN_BOGO_BUY, COLUMN_BOGO_BUY);
		map.put(COLUMN_BOGO_GET, COLUMN_BOGO_GET);
		map.put(COLUMN_UNIT_PRICE, COLUMN_UNIT_PRICE);
		map.put(COLUMN_CATEGORY_ID, COLUMN_CATEGORY_ID);

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

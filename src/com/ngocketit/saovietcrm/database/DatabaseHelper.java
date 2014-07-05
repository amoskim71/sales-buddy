package com.ngocketit.saovietcrm.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "saoviet_crm";
	private static final int DATABASE_VERSION = 1;
	
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		UserAccountTable.onCreate(database);
		SalingLineTable.onCreate(database);
		ProductTable.onCreate(database);
		OrderTable.onCreate(database);
		OrderDetailTable.onCreate(database);
		CustomerTable.onCreate(database);
		ProductCategoryTable.onCreate(database);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		UserAccountTable.onUpgrade(database, oldVersion, newVersion);
		SalingLineTable.onUpgrade(database, oldVersion, newVersion);
		ProductTable.onUpgrade(database, oldVersion, newVersion);
		OrderTable.onUpgrade(database, oldVersion, newVersion);
		OrderDetailTable.onUpgrade(database, oldVersion, newVersion);
		CustomerTable.onUpgrade(database, oldVersion, newVersion);
		ProductCategoryTable.onUpgrade(database, oldVersion, newVersion);
	}
}

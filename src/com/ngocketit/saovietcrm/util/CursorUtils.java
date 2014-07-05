package com.ngocketit.saovietcrm.util;

import android.database.Cursor;

public class CursorUtils {
	public static String getRecordStringValue(Cursor cursor, String colName) {
		int colIndex = cursor.getColumnIndexOrThrow(colName);
		return cursor.getString(colIndex);
	}

	public static int getRecordIntValue(Cursor cursor, String colName) {
		int colIndex = cursor.getColumnIndexOrThrow(colName);
		return cursor.getInt(colIndex);
	}

	public static float getRecordFloatValue(Cursor cursor, String colName) {
		int colIndex = cursor.getColumnIndexOrThrow(colName);
		return cursor.getFloat(colIndex);
	}
}

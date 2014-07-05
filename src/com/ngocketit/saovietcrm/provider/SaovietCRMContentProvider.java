package com.ngocketit.saovietcrm.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

import com.ngocketit.saovietcrm.database.BaseTable;
import com.ngocketit.saovietcrm.database.CustomerTable;
import com.ngocketit.saovietcrm.database.DatabaseHelper;
import com.ngocketit.saovietcrm.database.OrderDetailTable;
import com.ngocketit.saovietcrm.database.OrderTable;
import com.ngocketit.saovietcrm.database.ProductCategoryTable;
import com.ngocketit.saovietcrm.database.ProductTable;
import com.ngocketit.saovietcrm.database.SalingLineTable;
import com.ngocketit.saovietcrm.database.UserAccountTable;
import com.ngocketit.saovietcrm.util.GeneralUtils;


public class SaovietCRMContentProvider extends ContentProvider {
	// Content provider authority
	private static final String AUTHORITY = "com.ngocketit.saovietcrm.provider.contentprovider";
	
	private interface ContentBasePath {
		static final String USER_ACCOUNT	= "user_accounts";
		static final String SALE_LINE		= "sale_lines";
		static final String PRODUCT			= "products";
		static final String ORDER			= "orders";
		static final String ORDER_DETAIL	= "order_details";
		static final String CUSTOMER		= "customers";
		static final String PRODUCT_CAT		= "product_categories";
		
		// Extended content base paths for raw queries
		static final String CUSTOMER_GROUP_BY_LINE	= "customers_groupby_lines";
		static final String PRODUCT_GROUP_BY_CAT	= "products_groupby_cats";
		static final String ORDER_GROUP_BY_DATE		= "orders_groupby_dates";

		static final String SALES_REV_DAILY_BREAKDOWN	= "sales_daily_breakdown";
		static final String SALES_REV_WEEKLY_BREAKDOWN	= "sales_weekly_breakdown";
		static final String SALES_REV_MONTHLY_BREAKDOWN	= "sales_monthly_breakdown";
		static final String SALES_REV_YEARLY_BREAKDOWN	= "sales_yearly_breakdown";
	}
	// Content URI constant definitions for different tables
	public interface ContentUri {
		static final Uri USER_ACCOUNT	= Uri.parse("content://" + AUTHORITY + "/" + ContentBasePath.USER_ACCOUNT);
		static final Uri SALE_LINE		= Uri.parse("content://" + AUTHORITY + "/" + ContentBasePath.SALE_LINE);
		static final Uri PRODUCT		= Uri.parse("content://" + AUTHORITY + "/" + ContentBasePath.PRODUCT);
		static final Uri ORDER			= Uri.parse("content://" + AUTHORITY + "/" + ContentBasePath.ORDER);
		static final Uri ORDER_DETAIL	= Uri.parse("content://" + AUTHORITY + "/" + ContentBasePath.ORDER_DETAIL);
		static final Uri CUSTOMER		= Uri.parse("content://" + AUTHORITY + "/" + ContentBasePath.CUSTOMER);
		static final Uri PRODUCT_CAT	= Uri.parse("content://" + AUTHORITY + "/" + ContentBasePath.PRODUCT_CAT);
		
		// Extended URIs
		static final Uri CUSTOMER_GROUP_BY_LINE	= Uri.parse("content://" + AUTHORITY + "/" + ContentBasePath.CUSTOMER_GROUP_BY_LINE);
		static final Uri PRODUCT_GROUP_BY_CAT	= Uri.parse("content://" + AUTHORITY + "/" + ContentBasePath.PRODUCT_GROUP_BY_CAT);
		static final Uri ORDER_GROUP_BY_DATE	= Uri.parse("content://" + AUTHORITY + "/" + ContentBasePath.ORDER_GROUP_BY_DATE);

		static final Uri SALES_REV_DAILY_BREAKDOWN		= Uri.parse("content://" + AUTHORITY + "/" + ContentBasePath.SALES_REV_DAILY_BREAKDOWN);
		static final Uri SALES_REV_WEEKLY_BREAKDOWN		= Uri.parse("content://" + AUTHORITY + "/" + ContentBasePath.SALES_REV_WEEKLY_BREAKDOWN);
		static final Uri SALES_REV_MONTHLY_BREAKDOWN	= Uri.parse("content://" + AUTHORITY + "/" + ContentBasePath.SALES_REV_MONTHLY_BREAKDOWN);
		static final Uri SALES_REV_YEARLY_BREAKDOWN		= Uri.parse("content://" + AUTHORITY + "/" + ContentBasePath.SALES_REV_YEARLY_BREAKDOWN);
	}

	private static final String CONTENT_TYPE		= "vnd.android.cursor.dir/vnd.com.ngocketit.saovietcrm";
	private static final String CONTENT_ITEM_TYPE	= "vnd.android.cursor.item/vnd.com.ngocketit.saovietcrm";
	
	private interface QueryUriTypes {
		// User account
		static final int USER_ACCOUNTS		= 0;
		static final int USER_ACCOUNT_ID	= 1;
		
		// Sale line
		static final int SALE_LINES			= 2;
		static final int SALE_LINE_ID		= 3;
		
		// Product
		static final int PRODUCTS			= 4;
		static final int PRODUCT_ID			= 5;
		
		// Order
		static final int ORDERS				= 6;
		static final int ORDER_ID			= 7;
		
		// Order detail
		static final int ORDER_DETAILS		= 8;
		static final int ORDER_DETAIL_ID	= 9;
		
		// Customer
		static final int CUSTOMERS			= 10;
		static final int CUSTOMER_ID		= 11;

		// Product category
		static final int PRODUCT_CATS		= 12;
		static final int PRODUCT_CAT_ID		= 13;
		
		// Extended types
		static final int CUSTOMERS_GROUP_BY_LINE = 20;
		static final int PRODUCTS_GROUP_BY_CAT	 = 21;
		static final int ORDERS_GROUP_BY_DATE	 = 22;

		static final int SALES_REV_DAILY_BREAKDOWN	 	= 23;
		static final int SALES_REV_WEEKLY_BREAKDOWN	 	= 24;
		static final int SALES_REV_MONTHLY_BREAKDOWN	= 25;
		static final int SALES_REV_YEARLY_BREAKDOWN	 	= 26;
	}
	
	private interface RawQueries {
		public class SelectQuery {
			public String selectClause;
			public String whereClause;
			public String suffixClause;
			
			public SelectQuery(String select, String where, String suffix) {
				selectClause = select;
				whereClause = where;
				suffixClause = suffix;
			}
			
			public String toString() {
				String query = selectClause;

				if (whereClause != null) {
					query += " WHERE " + whereClause;
				}
				
				if (suffixClause != null) {
					query += " " + suffixClause;
				}
				
				return query;
			}

			public String toString(String extraWhereClause) {
				if (extraWhereClause == null) {
					return toString();
				}

				String oldWhere = whereClause;
				
				if (whereClause == null) {
					whereClause = extraWhereClause;
				} else {
					whereClause += " " + extraWhereClause;
				}
				
				String query = toString();
				whereClause = oldWhere;
				
				return query;
			}
		}

		// SELECT C._id, C.line_id, L.name, C.shop_name, C.rate, SUM(O.total_price) AS total_order_value
		// FROM customers AS C INNER JOIN saling_lines AS L ON C.line_id = L._id 
		// LEFT JOIN orders AS O ON O.customer_id = C._id 
		// GROUP BY C._id, C.line_id
		// ORDER BY L.name ASC, total_order_value DESC, C.rate DESC

		static final SelectQuery CUSTOMERS_GROUP_BY_LINE = new SelectQuery(
				// Select clause
				"SELECT " +
				"C." + BaseColumns._ID + "," +
				"C." + CustomerTable.COLUMN_LINE_ID + "," +
				"L." + SalingLineTable.COLUMN_NAME + "," + 
				"C." + CustomerTable.COLUMN_SHOP_NAME + "," +
				"C." + CustomerTable.COLUMN_RATE + "," +
				"SUM(I." + OrderDetailTable.COLUMN_TOTAL_PRICE + ") AS orders_value, " +
				"COUNT(DISTINCT O." + BaseColumns._ID + ") AS orders_count " +
				"FROM " + CustomerTable.TABLE_NAME + " AS C " +
				"LEFT JOIN " + SalingLineTable.TABLE_NAME + " AS L ON " +
				"C." + CustomerTable.COLUMN_LINE_ID + " = L." + BaseTable._ID + " " +
				"LEFT JOIN " + OrderTable.TABLE_NAME + " AS O ON " +
				"C." + BaseTable._ID + " = " + OrderTable.COLUMN_CUSTOMER_ID + " " +
				"LEFT JOIN " + OrderDetailTable.TABLE_NAME + " AS I ON I." + OrderDetailTable.COLUMN_ORDER_ID + "=" +
				"O." + BaseTable._ID,
				
				// Where clause
				null,
				
				// Suffix clause
				"GROUP BY " + "C." + BaseColumns._ID + ", C." + CustomerTable.COLUMN_LINE_ID + " " +
				"ORDER BY " + "L." + SalingLineTable.COLUMN_NAME + " ASC, orders_value DESC, " + 
				"C." + CustomerTable.COLUMN_RATE + " DESC "
		);
		
		// SELECT P._id, P.name, P.code, P.unit_price, C.category_name 
		// FROM products AS P INNER JOIN product_categories C ON P.category_id = C._id 
		// GROUP BY  P._id, C.category_name 
		// ORDER BY C.category_name ASC, P.name ASC;
		static final SelectQuery PRODUCTS_GROUP_BY_CAT = new SelectQuery(
				// Select clause
				"SELECT " +
				"P." + BaseTable._ID + "," +
				"P." + ProductTable.COLUMN_NAME + "," +
				"P." + ProductTable.COLUMN_CODE + "," +
				"P." + ProductTable.COLUMN_UNIT_PRICE + "," +
				"P." + ProductTable.COLUMN_BOGO_BUY + "," +
				"P." + ProductTable.COLUMN_BOGO_GET + "," +
				"C." + ProductCategoryTable.COLUMN_NAME + " " +
				"FROM " + ProductTable.TABLE_NAME + " AS P " + 
				"LEFT JOIN " + ProductCategoryTable.TABLE_NAME + " AS C " + 
				"ON P." + ProductTable.COLUMN_CATEGORY_ID + " = C." + BaseTable._ID + " ",
				
				// Where clause
				null,

				// Group by & order by clauses
				"GROUP BY P." + BaseTable._ID + ", C." + ProductCategoryTable.COLUMN_NAME + " " +
				"ORDER BY C." + ProductCategoryTable.COLUMN_NAME + " ASC, P." + ProductTable.COLUMN_NAME + " ASC "
		);
		
		// SELECT O._id, C.shop_name, substr(O.created_at, 0, 11) AS created_at, O.total_price, O.quantity 
		// FROM orders AS O INNER JOIN customers AS C ON O.customer_id = C._id 
		// GROUP BY O._id, created_at 
		// ORDER BY created_at DESC, O.total_price DESC;
		static final SelectQuery ORDERS_GROUP_BY_DATE = new SelectQuery(
				// Select clause
				"SELECT " +
				"O." + BaseTable._ID + "," +
				"O." + OrderTable.COLUMN_QUANTITY + "," +
				"C." + CustomerTable.COLUMN_SHOP_NAME + "," +
				"SUBSTR(O." + BaseTable.COLUMN_CREATED_AT + ", 0, 11) AS " + BaseTable.COLUMN_CREATED_DATE + "," +
				"SUM(I." + OrderDetailTable.COLUMN_TOTAL_PRICE + ") AS " + OrderTable.COLUMN_TOTAL_PRICE + " " +
				"FROM " + OrderTable.TABLE_NAME + " AS O INNER JOIN " + CustomerTable.TABLE_NAME + " AS C " +
				"ON O." + OrderTable.COLUMN_CUSTOMER_ID + "=" + "C." + BaseTable._ID + " " +
				"LEFT JOIN " + OrderDetailTable.TABLE_NAME + " AS I ON I." + OrderDetailTable.COLUMN_ORDER_ID + "=" +
				"O." + BaseTable._ID,
				
				// Where clause
				null,
				
				// Group by & order by clauses
				"GROUP BY O." + BaseTable._ID + ", O." + BaseTable.COLUMN_CREATED_AT + " " +
				"ORDER BY " + BaseTable.COLUMN_CREATED_DATE + " DESC, " + OrderTable.COLUMN_TOTAL_PRICE + " DESC "
		);
		
		static final SelectQuery SALES_REVENUE_DAILY_BREAKDOWN = new SelectQuery(
				// Select clause
				"SELECT " +
				"SUBSTR(O." + BaseTable.COLUMN_CREATED_AT + ", 0, 11) AS " + BaseTable.COLUMN_CREATED_DATE + "," +
				"SUM(I." + OrderDetailTable.COLUMN_TOTAL_PRICE + ") AS " + OrderTable.COLUMN_TOTAL_PRICE + " " +
				"FROM " + OrderTable.TABLE_NAME + " AS O INNER JOIN " + CustomerTable.TABLE_NAME + " AS C " +
				"ON O." + OrderTable.COLUMN_CUSTOMER_ID + "=" + "C." + BaseTable._ID + " " +
				"LEFT JOIN " + OrderDetailTable.TABLE_NAME + " AS I ON I." + OrderDetailTable.COLUMN_ORDER_ID + "=" +
				"O." + BaseTable._ID,
				
				// Where clause
				null,
				
				// Group by & order by clauses
				"GROUP BY O." + BaseTable.COLUMN_CREATED_AT + " " +
				"ORDER BY " + BaseTable.COLUMN_CREATED_DATE + " DESC, " + OrderTable.COLUMN_TOTAL_PRICE + " DESC "
		);
		
		
		static final SelectQuery SALES_REVENUE_WEEKLY_BREAKDOWN = new SelectQuery(
				// Select clause
				"SELECT " +
				"SUBSTR(O." + BaseTable.COLUMN_CREATED_AT + ", 0, 11) AS " + BaseTable.COLUMN_CREATED_DATE + "," +
				"SUM(I." + OrderDetailTable.COLUMN_TOTAL_PRICE + ") AS " + OrderTable.COLUMN_TOTAL_PRICE + " " +
				"FROM " + OrderTable.TABLE_NAME + " AS O INNER JOIN " + CustomerTable.TABLE_NAME + " AS C " +
				"ON O." + OrderTable.COLUMN_CUSTOMER_ID + "=" + "C." + BaseTable._ID + " " +
				"LEFT JOIN " + OrderDetailTable.TABLE_NAME + " AS I ON I." + OrderDetailTable.COLUMN_ORDER_ID + "=" +
				"O." + BaseTable._ID,
				
				// Where clause
				null,
				
				// Group by & order by clauses
				"GROUP BY O." + BaseTable.COLUMN_CREATED_AT + " " +
				"ORDER BY " + BaseTable.COLUMN_CREATED_DATE + " DESC, " + OrderTable.COLUMN_TOTAL_PRICE + " DESC "
		);
		
		
		static final SelectQuery SALES_REVENUE_MONTHLY_BREAKDOWN = new SelectQuery(
				// Select clause
				"SELECT " +
				"SUBSTR(O." + BaseTable.COLUMN_CREATED_AT + ", 0, 8) AS " + BaseTable.COLUMN_CREATED_DATE + "," +
				"SUM(I." + OrderDetailTable.COLUMN_TOTAL_PRICE + ") AS " + OrderTable.COLUMN_TOTAL_PRICE + " " +
				"FROM " + OrderTable.TABLE_NAME + " AS O INNER JOIN " + CustomerTable.TABLE_NAME + " AS C " +
				"ON O." + OrderTable.COLUMN_CUSTOMER_ID + "=" + "C." + BaseTable._ID + " " +
				"LEFT JOIN " + OrderDetailTable.TABLE_NAME + " AS I ON I." + OrderDetailTable.COLUMN_ORDER_ID + "=" +
				"O." + BaseTable._ID,
				
				// Where clause
				null,
				
				// Group by & order by clauses
				"GROUP BY O." + BaseTable.COLUMN_CREATED_AT + " " +
				"ORDER BY " + BaseTable.COLUMN_CREATED_DATE + " DESC, " + OrderTable.COLUMN_TOTAL_PRICE + " DESC "
		);
	}
	
	private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	private DatabaseHelper mDbHelper;
	
	// Static code
	static {
		sUriMatcher.addURI(AUTHORITY, ContentBasePath.USER_ACCOUNT, QueryUriTypes.USER_ACCOUNTS);
		sUriMatcher.addURI(AUTHORITY, ContentBasePath.USER_ACCOUNT + "/#", QueryUriTypes.USER_ACCOUNT_ID);

		sUriMatcher.addURI(AUTHORITY, ContentBasePath.SALE_LINE, QueryUriTypes.SALE_LINES);
		sUriMatcher.addURI(AUTHORITY, ContentBasePath.SALE_LINE + "/#", QueryUriTypes.SALE_LINE_ID);

		sUriMatcher.addURI(AUTHORITY, ContentBasePath.PRODUCT, QueryUriTypes.PRODUCTS);
		sUriMatcher.addURI(AUTHORITY, ContentBasePath.PRODUCT + "/#", QueryUriTypes.PRODUCT_ID);

		sUriMatcher.addURI(AUTHORITY, ContentBasePath.ORDER, QueryUriTypes.ORDERS);
		sUriMatcher.addURI(AUTHORITY, ContentBasePath.ORDER + "/#", QueryUriTypes.ORDER_ID);

		sUriMatcher.addURI(AUTHORITY, ContentBasePath.ORDER_DETAIL, QueryUriTypes.ORDER_DETAILS);
		sUriMatcher.addURI(AUTHORITY, ContentBasePath.ORDER_DETAIL + "/#", QueryUriTypes.ORDER_DETAIL_ID);

		sUriMatcher.addURI(AUTHORITY, ContentBasePath.CUSTOMER, QueryUriTypes.CUSTOMERS);
		sUriMatcher.addURI(AUTHORITY, ContentBasePath.CUSTOMER + "/#", QueryUriTypes.CUSTOMER_ID);

		sUriMatcher.addURI(AUTHORITY, ContentBasePath.PRODUCT_CAT, QueryUriTypes.PRODUCT_CATS);
		sUriMatcher.addURI(AUTHORITY, ContentBasePath.PRODUCT_CAT + "/#", QueryUriTypes.PRODUCT_CAT_ID);
		
		// Extended
		sUriMatcher.addURI(AUTHORITY, ContentBasePath.CUSTOMER_GROUP_BY_LINE, QueryUriTypes.CUSTOMERS_GROUP_BY_LINE);
		sUriMatcher.addURI(AUTHORITY, ContentBasePath.PRODUCT_GROUP_BY_CAT, QueryUriTypes.PRODUCTS_GROUP_BY_CAT);
		sUriMatcher.addURI(AUTHORITY, ContentBasePath.ORDER_GROUP_BY_DATE, QueryUriTypes.ORDERS_GROUP_BY_DATE);

		sUriMatcher.addURI(AUTHORITY, ContentBasePath.SALES_REV_DAILY_BREAKDOWN, QueryUriTypes.SALES_REV_DAILY_BREAKDOWN);
		sUriMatcher.addURI(AUTHORITY, ContentBasePath.SALES_REV_WEEKLY_BREAKDOWN, QueryUriTypes.SALES_REV_WEEKLY_BREAKDOWN);
		sUriMatcher.addURI(AUTHORITY, ContentBasePath.SALES_REV_MONTHLY_BREAKDOWN, QueryUriTypes.SALES_REV_MONTHLY_BREAKDOWN);
		sUriMatcher.addURI(AUTHORITY, ContentBasePath.SALES_REV_YEARLY_BREAKDOWN, QueryUriTypes.SALES_REV_YEARLY_BREAKDOWN);
	}
	
	private String getDatabaseTable(int uriQueryType) {
		switch (uriQueryType) {
		case QueryUriTypes.USER_ACCOUNTS:
		case QueryUriTypes.USER_ACCOUNT_ID:
			return UserAccountTable.TABLE_NAME;
			
		case QueryUriTypes.SALE_LINES:
		case QueryUriTypes.SALE_LINE_ID:
			return SalingLineTable.TABLE_NAME;

		case QueryUriTypes.PRODUCTS:
		case QueryUriTypes.PRODUCT_ID:
			return ProductTable.TABLE_NAME;

		case QueryUriTypes.ORDERS:
		case QueryUriTypes.ORDER_ID:
			return OrderTable.TABLE_NAME;

		case QueryUriTypes.ORDER_DETAILS:
		case QueryUriTypes.ORDER_DETAIL_ID:
			return OrderDetailTable.TABLE_NAME;
		
		case QueryUriTypes.CUSTOMERS:
		case QueryUriTypes.CUSTOMER_ID:
			return CustomerTable.TABLE_NAME;
			
		case QueryUriTypes.PRODUCT_CATS:
		case QueryUriTypes.PRODUCT_CAT_ID:
			return ProductCategoryTable.TABLE_NAME;

		default:
			throw new IllegalArgumentException("Unknown uri query type: " + uriQueryType);
		}
	}
	
	private HashMap<String, String> getProjectionMap(int uriQueryType) {
		switch (uriQueryType) {
		case QueryUriTypes.USER_ACCOUNTS:
		case QueryUriTypes.USER_ACCOUNT_ID:
			return UserAccountTable.getProjectionMap();
			
		case QueryUriTypes.SALE_LINES:
		case QueryUriTypes.SALE_LINE_ID:
			return SalingLineTable.getProjectionMap();

		case QueryUriTypes.PRODUCTS:
		case QueryUriTypes.PRODUCT_ID:
			return ProductTable.getProjectionMap();

		case QueryUriTypes.ORDERS:
		case QueryUriTypes.ORDER_ID:
			return OrderTable.getProjectionMap();

		case QueryUriTypes.ORDER_DETAILS:
		case QueryUriTypes.ORDER_DETAIL_ID:
			return OrderDetailTable.getProjectionMap();
		
		case QueryUriTypes.CUSTOMERS:
		case QueryUriTypes.CUSTOMER_ID:
			return CustomerTable.getProjectionMap();

		case QueryUriTypes.PRODUCT_CATS:
		case QueryUriTypes.PRODUCT_CAT_ID:
			return ProductCategoryTable.getProjectionMap();
			
		default:
			return null;
		}
	}
	
	private void notifyChange(Uri uri) {
		int uriQueryType = sUriMatcher.match(uri);
		ContentResolver cr = getContext().getContentResolver();
		
        cr.notifyChange(uri, null);
        
        // Notify also observers of custom queries
        switch(uriQueryType) {
        case QueryUriTypes.CUSTOMERS:
        case QueryUriTypes.CUSTOMER_ID:
        	cr.notifyChange(ContentUri.CUSTOMER_GROUP_BY_LINE, null);
        	break;
        	
        case QueryUriTypes.ORDERS:
        case QueryUriTypes.ORDER_ID:
        	cr.notifyChange(ContentUri.ORDER_GROUP_BY_DATE, null);
        	break;
        	
        case QueryUriTypes.PRODUCTS:
        case QueryUriTypes.PRODUCT_ID:
        	cr.notifyChange(ContentUri.PRODUCT_GROUP_BY_CAT, null);
        	break;
        	
        default:
        	break;
        }
	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		int uriQueryType = sUriMatcher.match(uri);
		String dbTable = getDatabaseTable(uriQueryType);
		
		switch (uriQueryType) {
		case QueryUriTypes.USER_ACCOUNTS:
		case QueryUriTypes.SALE_LINES:
		case QueryUriTypes.PRODUCTS:
		case QueryUriTypes.ORDERS:
		case QueryUriTypes.ORDER_DETAILS:
		case QueryUriTypes.CUSTOMERS:
		case QueryUriTypes.PRODUCT_CATS:
			return deleteMultipleItems(dbTable, uriQueryType, uri, where, whereArgs);

		case QueryUriTypes.USER_ACCOUNT_ID:
		case QueryUriTypes.SALE_LINE_ID:
		case QueryUriTypes.PRODUCT_ID:
		case QueryUriTypes.ORDER_ID:
		case QueryUriTypes.ORDER_DETAIL_ID:
		case QueryUriTypes.CUSTOMER_ID:
		case QueryUriTypes.PRODUCT_CAT_ID:
			return deleteSingleItem(dbTable, uriQueryType, uri, where, whereArgs);
			
		default:
            throw new IllegalArgumentException("Unknown URI: " + uri);
		}
	}
	
	private int deleteMultipleItems(String table, int queryType, Uri uri, String where, String[] whereArgs) {
		int count = mDbHelper.getWritableDatabase().delete(table, where, whereArgs);

		// Notify interested parties
		notifyChange(uri);
		
		return count;
	}
	
	private int deleteSingleItem(String table, int queryType, Uri uri, String where, String[] whereArgs) {
		String itemId = uri.getLastPathSegment();
		String whereClause = BaseColumns._ID + "=" + itemId + (!TextUtils.isEmpty(where) ? " AND (" + where + ")" : "");
		int count = mDbHelper.getWritableDatabase().delete(table, whereClause, whereArgs);

		// Notify interested parties
		notifyChange(uri);
		
		// If we're deleting order, also delete all respective order line items
		if (queryType == QueryUriTypes.ORDER_ID) {
			String selection = OrderDetailTable.COLUMN_ORDER_ID + "=?";
			String[] selectionArgs = { String.valueOf(itemId) };
			mDbHelper.getWritableDatabase().delete(OrderDetailTable.TABLE_NAME, selection, selectionArgs);
		}
		return count;
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case QueryUriTypes.USER_ACCOUNTS:
		case QueryUriTypes.SALE_LINES:
		case QueryUriTypes.PRODUCTS:
		case QueryUriTypes.ORDERS:
		case QueryUriTypes.ORDER_DETAILS:
		case QueryUriTypes.CUSTOMERS:
		case QueryUriTypes.PRODUCT_CATS:
			return CONTENT_TYPE;

		case QueryUriTypes.USER_ACCOUNT_ID:
		case QueryUriTypes.SALE_LINE_ID:
		case QueryUriTypes.PRODUCT_ID:
		case QueryUriTypes.ORDER_ID:
		case QueryUriTypes.ORDER_DETAIL_ID:
		case QueryUriTypes.CUSTOMER_ID:
		case QueryUriTypes.PRODUCT_CAT_ID:
			return CONTENT_ITEM_TYPE;
			
		default:
            throw new IllegalArgumentException("Unknown URI: " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// Set the current timestamp for created_at field
		if (!values.containsKey(BaseTable.COLUMN_CREATED_AT)) {
			values.put(BaseTable.COLUMN_CREATED_AT, GeneralUtils.getCurrentTimestamp());
		}
		
		switch (sUriMatcher.match(uri)) {
		case QueryUriTypes.USER_ACCOUNTS:
			return insertUserAccount(values, uri);
			
		case QueryUriTypes.SALE_LINES:
			return insertSaleLine(values, uri);
			
		case QueryUriTypes.PRODUCTS:
			return insertProduct(values, uri);

		case QueryUriTypes.PRODUCT_CATS:
			return insertProductCategory(values, uri);
			
		case QueryUriTypes.ORDERS:
			return insertOrder(values, uri);
			
		case QueryUriTypes.ORDER_DETAILS:
			return insertOrderDetail(values, uri);
			
		case QueryUriTypes.CUSTOMERS:
			return insertCustomer(values, uri);
		
		default:
			throw new IllegalArgumentException("Invalid uri: " + uri);
		}
	}
	
	private void checkRequiredFields(String[] requiredFields, ContentValues values) {
		// Check if all required columns are provided
		for(String column: requiredFields) {
			if (!values.containsKey(column)) {
				throw new IllegalArgumentException("Column " + column + " is requied");
			}
		}
	}
	
	private Uri insertItem(String table, Uri contentUri, ContentValues values) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		long id = db.insert(table, null, values);

		notifyChange(contentUri);

		if (id > 0) {
			Uri itemUri = Uri.withAppendedPath(contentUri, String.valueOf(id));
			return itemUri;
		}

		return null;
	}
	
	private Uri insertUserAccount(ContentValues values, Uri uri) {
		String[] requiredFields = new String[] {
				UserAccountTable.COLUMN_EMAIL,
				UserAccountTable.COLUMN_FULLNAME,
				UserAccountTable.COLUMN_PASSWORD,
				UserAccountTable.COLUMN_PHONE,
		};
		
		checkRequiredFields(requiredFields, values);
		
		// Hash the password
		String password = GeneralUtils.md5(values.getAsString(UserAccountTable.COLUMN_PASSWORD));
		if (password != null) {
			values.put(UserAccountTable.COLUMN_PASSWORD, password);
		}
		
		return insertItem(UserAccountTable.TABLE_NAME, uri, values);
	}

	private Uri insertSaleLine(ContentValues values, Uri uri) {
		String[] requiredFields = new String[] {
				SalingLineTable.COLUMN_NAME,
				SalingLineTable.COLUMN_ADDRESS
		};
		
		checkRequiredFields(requiredFields, values);

		return insertItem(SalingLineTable.TABLE_NAME, uri, values);
	}

	private Uri insertProductCategory(ContentValues values, Uri uri) {
		String[] requiredFields = new String[] {
				ProductCategoryTable.COLUMN_NAME
		};
		
		checkRequiredFields(requiredFields, values);

		return insertItem(ProductCategoryTable.TABLE_NAME, uri, values);
	}

	private Uri insertProduct(ContentValues values, Uri uri) {
		String[] requiredFields = new String[] {
				ProductTable.COLUMN_NAME,
				ProductTable.COLUMN_CODE,
				ProductTable.COLUMN_UNIT_PRICE
		};
		
		checkRequiredFields(requiredFields, values);
		return insertItem(ProductTable.TABLE_NAME, uri, values);
	}

	private Uri insertOrder(ContentValues values, Uri uri) {
		String[] requiredFields = new String[] {
				OrderTable.COLUMN_CUSTOMER_ID,
				OrderTable.COLUMN_CUSTOMER_NAME,
				OrderTable.COLUMN_QUANTITY,
		};
		
		checkRequiredFields(requiredFields, values);
		return insertItem(OrderTable.TABLE_NAME, uri, values);
	}

	private Uri insertOrderDetail(ContentValues values, Uri uri) {
		String[] requiredFields = new String[] {
				OrderDetailTable.COLUMN_ORDER_ID,
				OrderDetailTable.COLUMN_PRODUCT_ID,
				OrderDetailTable.COLUMN_QUANTITY
		};
		
		checkRequiredFields(requiredFields, values);
		return insertItem(OrderDetailTable.TABLE_NAME, uri, values);
	}

	private Uri insertCustomer(ContentValues values, Uri uri) {
		List<String> requiredFields = new ArrayList<String>();
		
		requiredFields.add(CustomerTable.COLUMN_OWNER_NAME);
		requiredFields.add(CustomerTable.COLUMN_ADDRESS);
		requiredFields.add(CustomerTable.COLUMN_PHONE1);
		
		// Shop name is required if customer is a company
		if (values.containsKey(CustomerTable.COLUMN_TYPE)) {
			int customerType = values.getAsInteger(CustomerTable.COLUMN_TYPE);
			
			if (customerType == CustomerTable.CUSTOMER_TYPE_COMPANY) {
				requiredFields.add(CustomerTable.COLUMN_SHOP_NAME);
			}
		}
		
		String[] fields = requiredFields.toArray(new String[0]);
		checkRequiredFields(fields, values);

		return insertItem(CustomerTable.TABLE_NAME, uri, values);
	}

	@Override
	public boolean onCreate() {
		mDbHelper = new DatabaseHelper(getContext());
		return false;
	}
	
	private String getDefaultSortOrder(int queryType) {
		String sortOrder = null;
		
		switch (queryType) {
		case QueryUriTypes.USER_ACCOUNTS:
			sortOrder = UserAccountTable.COLUMN_FULLNAME + " ASC ";
			break;

		case QueryUriTypes.SALE_LINES:
			sortOrder = SalingLineTable.COLUMN_NAME + " ASC ";
			break;

		case QueryUriTypes.PRODUCTS:
			sortOrder = ProductTable.COLUMN_NAME + " ASC ";
			break;

		case QueryUriTypes.ORDERS:
			sortOrder = OrderTable.COLUMN_CUSTOMER_NAME + " ASC ";
			break;

		case QueryUriTypes.ORDER_DETAILS:
			sortOrder = ProductTable.COLUMN_NAME + " ASC ";
			break;

		case QueryUriTypes.CUSTOMERS:
			sortOrder = CustomerTable.COLUMN_SHOP_NAME + " ASC ";
			break;
		
		case QueryUriTypes.PRODUCT_CATS:
			sortOrder = ProductCategoryTable.COLUMN_NAME + " ASC ";
			break;

		default:
			throw new IllegalArgumentException("Invalid query type");
		}
		
		return sortOrder;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		int uriQueryType = sUriMatcher.match(uri);
		HashMap<String, String> projectionMap = getProjectionMap(uriQueryType);
        
        qb.setProjectionMap(projectionMap);
        int queryType = sUriMatcher.match(uri);
        RawQueries.SelectQuery customQuery = null;
        Cursor cursor = null;
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        
        switch (queryType) {
        case QueryUriTypes.USER_ACCOUNTS:
		case QueryUriTypes.SALE_LINES:
		case QueryUriTypes.PRODUCTS:
		case QueryUriTypes.ORDERS:
		case QueryUriTypes.ORDER_DETAILS:
		case QueryUriTypes.CUSTOMERS:
		case QueryUriTypes.PRODUCT_CATS:
			qb.setTables(getDatabaseTable(uriQueryType));

			if (sortOrder == null) {
				sortOrder = getDefaultSortOrder(queryType);
			}
			break;
			
		case QueryUriTypes.CUSTOMERS_GROUP_BY_LINE:
			customQuery = RawQueries.CUSTOMERS_GROUP_BY_LINE;
			break;
			
		case QueryUriTypes.PRODUCTS_GROUP_BY_CAT:
			customQuery = RawQueries.PRODUCTS_GROUP_BY_CAT;
			break;

		case QueryUriTypes.SALES_REV_DAILY_BREAKDOWN:
			customQuery = RawQueries.SALES_REVENUE_DAILY_BREAKDOWN;
			break;
			
		case QueryUriTypes.SALES_REV_WEEKLY_BREAKDOWN:
			customQuery = RawQueries.SALES_REVENUE_WEEKLY_BREAKDOWN;
			break;

		case QueryUriTypes.SALES_REV_MONTHLY_BREAKDOWN:
			customQuery = RawQueries.SALES_REVENUE_MONTHLY_BREAKDOWN;
			break;

		case QueryUriTypes.SALES_REV_YEARLY_BREAKDOWN:
			customQuery = RawQueries.SALES_REVENUE_DAILY_BREAKDOWN;
			break;

		case QueryUriTypes.ORDERS_GROUP_BY_DATE:
			customQuery = RawQueries.ORDERS_GROUP_BY_DATE;
			break;

		case QueryUriTypes.USER_ACCOUNT_ID:
		case QueryUriTypes.SALE_LINE_ID:
		case QueryUriTypes.PRODUCT_ID:
		case QueryUriTypes.ORDER_ID:
		case QueryUriTypes.ORDER_DETAIL_ID:
		case QueryUriTypes.CUSTOMER_ID:
		case QueryUriTypes.PRODUCT_CAT_ID:
            String itemId = uri.getLastPathSegment();
            qb.appendWhere(BaseColumns._ID + "=" + itemId);
            break;
                
        default:
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        
        if (customQuery != null) {
        	String query = customQuery.toString(selection);
        	Log.d(SaovietCRMContentProvider.class.getName(), "Running custom query: " + query);
        	cursor = db.rawQuery(query, selectionArgs);
        }
        else {
        	cursor = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        }
        
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        
        return cursor;
	}
	
	
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		// Mark the update timestamp
		if (!values.containsKey(BaseTable.COLUMN_UPDATED_AT)) {
			values.put(BaseTable.COLUMN_UPDATED_AT, GeneralUtils.getCurrentTimestamp());
		}
		
		int uriQueryType = sUriMatcher.match(uri);
		String dbTable = getDatabaseTable(uriQueryType);
		
		switch (uriQueryType) {
		case QueryUriTypes.USER_ACCOUNTS:
		case QueryUriTypes.SALE_LINES:
		case QueryUriTypes.PRODUCTS:
		case QueryUriTypes.ORDERS:
		case QueryUriTypes.ORDER_DETAILS:
		case QueryUriTypes.CUSTOMERS:
		case QueryUriTypes.PRODUCT_CATS:
			return updateMultipleItems(dbTable, uri, values, selection, selectionArgs);

		case QueryUriTypes.USER_ACCOUNT_ID:
		case QueryUriTypes.SALE_LINE_ID:
		case QueryUriTypes.PRODUCT_ID:
		case QueryUriTypes.ORDER_ID:
		case QueryUriTypes.ORDER_DETAIL_ID:
		case QueryUriTypes.CUSTOMER_ID:
		case QueryUriTypes.PRODUCT_CAT_ID:
			return updateSingleItem(dbTable, uri, values, selection, selectionArgs);
			
		default:
            throw new IllegalArgumentException("Unknown URI: " + uri);
		}
	}	
	
	private int updateMultipleItems(String table, Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		int count = mDbHelper.getWritableDatabase().update(table, values, selection, selectionArgs);
		// Let observers know about the update
		notifyChange(uri);
		return count;
	}
	
	private int updateSingleItem(String table, Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		String itemId = uri.getLastPathSegment();
		String selectionClause = BaseColumns._ID + "=" + itemId + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
		int count = mDbHelper.getWritableDatabase().update(table, values, selectionClause, selectionArgs);
		
		// Let observers know about the update
		notifyChange(uri);
        
        return count;
	}
}

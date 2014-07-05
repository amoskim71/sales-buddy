package com.ngocketit.saovietcrm.activity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.ngocketit.saovietcrm.R;
import com.ngocketit.saovietcrm.app.SaovietCRMApp;
import com.ngocketit.saovietcrm.database.BaseTable;
import com.ngocketit.saovietcrm.model.UserAccountCredential;

public class BaseFormActivity extends LoggedInRequiredActivity implements OnClickListener {
	public static final String ITEM_ID		= "item_id";
	public static final String PICK_RESULT	= "pick_result";

	private long itemId = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		int contentView = getContentView();
		
		if (contentView > 0) {
			setContentView(contentView);
		}
		
		initContentView();
		
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			if (bundle.containsKey(ITEM_ID)) {
				itemId = bundle.getLong(ITEM_ID);
				Log.d(BaseFormActivity.class.getName(), "Item ID: " + itemId);
				loadItem(itemId);
			}
		}
		
		if (itemId > 0) {
			int editTitle = getEditModeTitle();
			if (editTitle > 0) {
				setTitle(editTitle);
			}
		}
	}
	
	protected int getContentView() {
		return 0;
	}
	
	protected int getEditModeTitle() {
		return 0;
	}
	
	protected void initContentView() {
		// Do something here
	}
	
	private void showFormElementError(String message, View element) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
		element.requestFocus();
	}
	
	protected void showFormElementError(int messageResource, View element) {
		String message = getResources().getString(messageResource);
		showFormElementError(message, element);
	}

	protected long getItemId() {
		return itemId;
	}
	
	protected Uri getItemContentUri() {
		return null;
	}
	
	protected void loadItem(long itemId) {
		
	}
	
	protected boolean validateForm() {
		return true;
	}
	
	protected void saveItem() {
		return;
	}
	
	protected void doDeleteItem() {
		new AlertDialog.Builder(this)
        .setTitle(R.string.delete)
        .setMessage(R.string.delete_alert)
        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	deleteItem();
            }
        })
        .setNegativeButton(android.R.string.no, null)
        .show();
	}
	
	protected void deleteItem() {
		Uri uri = getItemContentUri();
		String where = BaseTable._ID + "=?";
		String[] whereArgs = { String.valueOf(itemId) };
		
		int count = getContentResolver().delete(uri, where, whereArgs);
		
		if (count == 1) {
			finish();
		}
	}
	
	protected void doSaveItem() {
		if (validateForm()) {
			saveItem();
		}
	}
	
	protected Cursor loadItemFromDatabase(long itemId) {
		// Load product item from database
		String selection = BaseTable._ID + "=?";
		String[] selectionArgs = { String.valueOf(itemId) };

		Uri uri = getItemContentUri();
		Cursor cursor = getContentResolver().query(uri, null, selection, selectionArgs, null);
		
		// NOTE: this is important to move to the first record before reading
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
		}
		
		return cursor;
	}

	protected boolean saveItemToDatabase(ContentValues values) {
		long itemId = getItemId();
		boolean success = false;

		Uri uri = getItemContentUri();
		UserAccountCredential currentUser = ((SaovietCRMApp)getApplication()).getCurrentUserCredential();

		if (itemId > 0) {
			values.put(BaseTable._ID, itemId);
			
			if (currentUser != null) {
				values.put(BaseTable.COLUMN_UPDATED_BY, currentUser.id);
			}
		}
		
		// Mark who is creating/updating item
		else {
			if (currentUser != null) {
				values.put(BaseTable.COLUMN_CREATED_BY, currentUser.id);
			}
		}

		if (itemId <= 0) {
			Uri itemUri = getContentResolver().insert(uri, values);
			success = itemUri != null;
		}
		else {
			String where = BaseTable._ID + "=?";
			String[] whereArgs = { String.valueOf(itemId) };
			success = getContentResolver().update(uri, values, where, whereArgs) == 1;
		}
		
		if (success) {
			finish();
		}
		
		return success;
	}

	@Override
	public void onClick(View v) {
	}
	
	@Override
	protected int getMenuResource() {
		return R.menu.menu_base_form;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(getMenuResource(), menu);
		
		// Search widget
		MenuItem deleteItem = menu.findItem(R.id.action_delete);
		if (deleteItem != null && itemId <= 0) {
			deleteItem.setVisible(false);
		}
	    
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_save:
			doSaveItem();
			return true;

		case R.id.action_delete:
			doDeleteItem();
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}

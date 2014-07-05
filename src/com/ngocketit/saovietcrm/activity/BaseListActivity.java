package com.ngocketit.saovietcrm.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SpinnerAdapter;

import com.ngocketit.saovietcrm.R;
import com.ngocketit.saovietcrm.adapter.BaseCursorAdapter;

public class BaseListActivity extends LoggedInRequiredActivity implements
LoaderManager.LoaderCallbacks<Cursor>, 
SearchView.OnQueryTextListener, 
OnItemClickListener, 
ActionBar.OnNavigationListener {
	
	private static int sLoaderIDCounter = 0;
	private int mLoaderID;
	private boolean mIsResultIntent;

	private BaseCursorAdapter mAdapter;
	private ListView mListView;
	
	private String mSearchQuery;
	private SpinnerAdapter mActionBarSpinnerAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// If it's called for a result
		mIsResultIntent = Intent.ACTION_PICK.equals(getIntent().getAction());

		setContentView(getContentView());
		
		mListView = (ListView)findViewById(R.id.listview);
		if (mListView == null) {
			throw new IllegalArgumentException("List view not found");
		}
		mListView.setEmptyView(findViewById(R.id.list_empty));
		mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		
		// Enable action bar drop-down navigation
		mActionBarSpinnerAdapter = getActionBarSpinnerAdapter();
		if (mActionBarSpinnerAdapter != null) {
			getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
			getSupportActionBar().setListNavigationCallbacks(mActionBarSpinnerAdapter, this);
		}
		
		initAdapter();
	}
	
	
	protected SpinnerAdapter getActionBarSpinnerAdapter() {
		return null;
	}
	
	/**
	 * Whether the activity is called to return a result
	 * 
	 * @return True if activity is started to get result
	 */
	protected boolean isPickResultIntent() {
		return mIsResultIntent;
	}
	
	/**
	 * Get the layout resource id for the content view
	 * 
	 * @return Layout resource ID
	 */
	protected int getContentView() {
		return R.layout.activity_base_list;
	}
	
	/**
	 * Get context menu resource ID
	 * 
	 * @return Context menu resource ID
	 */
	protected int getMenuResource() {
		return R.menu.menu_base_list;
	}
	
	/**
	 * Get the search query string on action bar
	 * @return Search query
	 */
	protected String getSearchQuery() {
		return mSearchQuery;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(getMenuResource(), menu);

		// Search widget
		MenuItem searchItem = menu.findItem(R.id.action_search);
		SearchView searchView = (SearchView)MenuItemCompat.getActionView(searchItem);

		if (searchView != null) {
			searchView.setOnQueryTextListener(this);
			searchView.setQueryHint(getString(R.string.search));
		}

		// Hide import & delete actions
		menu.findItem(R.id.action_delete).setVisible(false);

		if (mIsResultIntent) {
			menu.findItem(R.id.action_add).setVisible(false);
		}
	    
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case android.R.id.home:
			// Return to caller activity if it's for a result
			if (mIsResultIntent) {
				finish();
				return true;
			}
			return false;
			
		case R.id.action_search:
			return true;

		case R.id.action_add:
			addNewItem();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	/**
	 * Get the class of the edit/add form activity which will be shown
	 * when add button or list item is clicked (when no view activity is defined)
	 * 
	 * @return Class of add/edit form activity
	 */
	protected Class<?> getFormActivityClass() {
		return null;
	}

	/**
	 * Get the class of the view activity which will be shown 
	 * when the list item is clicked
	 * 
	 * @return Class of view activity
	 */
	protected Class<?> getViewActivityClass() {
		return null;
	}
	
	protected void addNewItem() {
		Class<?> formClass = getFormActivityClass();
		if (formClass != null) {
			Intent intent = new Intent(this, formClass);
			startActivity(intent);
		}
	}
	
	/**
	 * Import multiple items from a file
	 * 
	 * @param filePath Path to the file
	 */
	protected void importItems(String filePath) {
		
	}
	
	/**
	 * Get the cursor adapter for the list view.
	 * The subclass should override this to provide
	 * its own adapter
	 * 
	 * @return Cursor adapter
	 */
	protected BaseCursorAdapter getAdapter() {
		return null;
	}
	
	public BaseCursorAdapter getListViewAdapter() {
		return mAdapter;
	}
	
	protected void initAdapter() {
		sLoaderIDCounter++;
		mLoaderID = sLoaderIDCounter;
		
		getSupportLoaderManager().initLoader(mLoaderID, null, this);
		
		mAdapter = getAdapter();

		if (mListView != null) {
			mListView.setAdapter(mAdapter);
			mListView.setOnItemClickListener(this);
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// TODO: Create CursorLoader with search query here
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		if (mAdapter != null) {
			mAdapter.swapCursor(data);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		if (mAdapter != null) {
			mAdapter.swapCursor(null);
		}
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		mSearchQuery = !TextUtils.isEmpty(newText) ? newText : null;

		// Reset loader if search text is changed
		getSupportLoaderManager().restartLoader(mLoaderID, null, this);

		return true;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		return false;
	}
	
	protected Bundle getIntentPickResult(Cursor cursor) {
		Bundle bundle = new Bundle();
		return bundle;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.d(BaseListActivity.class.getName(), "List item ID: " + id);
		
		if (mIsResultIntent) {
			BaseCursorAdapter adapter = (BaseCursorAdapter)mListView.getAdapter();
			Cursor cursor = adapter.getCursor();
			
			Intent result = new Intent();
			result.putExtra(BaseFormActivity.ITEM_ID, id);
			result.putExtra(BaseFormActivity.PICK_RESULT, getIntentPickResult(cursor));

			setResult(RESULT_OK, result);
			finish();
		} else {
			Class<?> viewClass = getViewActivityClass();
			Class<?> formClass = getFormActivityClass();

			Intent intent = new Intent(this, viewClass != null ? viewClass : formClass);
			intent.putExtra(BaseFormActivity.ITEM_ID, id);
			startActivity(intent);
		}
	}

	@Override
	public boolean onNavigationItemSelected(int position, long itemId) {
		return false;
	}
}

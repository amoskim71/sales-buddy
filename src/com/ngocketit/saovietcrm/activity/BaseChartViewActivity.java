package com.ngocketit.saovietcrm.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.achartengine.GraphicalView;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.ngocketit.saovietcrm.R;
import com.ngocketit.saovietcrm.app.SaovietCRMApp;
import com.ngocketit.saovietcrm.fragment.DatePickerFragment;
import com.ngocketit.saovietcrm.helper.ChartHelper;

public class BaseChartViewActivity extends LoggedInRequiredActivity implements OnClickListener, 
LoaderManager.LoaderCallbacks<Cursor> {
	private static final int DEFAULT_LOADER_ID = 0x1;
	
	protected GraphicalView mChartView;
	protected ChartHelper mChartHelper;
	
	protected Calendar mStartDate;
	protected Calendar mEndDate;
	
	View mDateBar;
	ImageButton mBtnPickStartDate;
	ImageButton mBtnPickEndDate;
	EditText mTxtStartDate;
	EditText mTxtEndDate;
	
	boolean mPickStartDate;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getContentView());
		
		mDateBar = findViewById(R.id.dateBar);
		mBtnPickStartDate = (ImageButton)findViewById(R.id.btnPickStartDate);
		mBtnPickEndDate = (ImageButton)findViewById(R.id.btnPickEndDate);
		
		mTxtStartDate = (EditText)findViewById(R.id.txtStartDate);
		mTxtEndDate = (EditText)findViewById(R.id.txtEndDate);
		
		mBtnPickStartDate.setOnClickListener(this);
		mBtnPickEndDate.setOnClickListener(this);
		
		mChartHelper = getChartHelper();
		
		initCursorLoader();
	}
	
	protected ChartHelper getChartHelper() {
		return new ChartHelper();
	}

	protected int getContentView() {
		return R.layout.activity_base_chart_view;
	}
	
	protected GraphicalView generateChartView(Cursor cursor) {
		return null;
	}
	
	protected void repaintChart() {
		if (mChartView != null) {
			mChartView.repaint();
		}
	}
	
	protected String getDateFormat() {
		return "dd/MM/yyyy";
	}
	
	private void updateDateRangeInfo() {
		if (mStartDate == null && mEndDate == null) return;
		SaovietCRMApp app = (SaovietCRMApp)getApplication();
		Locale userLocale = app.getCurrentUserLocale();

		if (userLocale == null) {
			userLocale = Locale.US;
		}
		
		SimpleDateFormat df = new SimpleDateFormat(getDateFormat(), userLocale);

		if (mStartDate != null) {
			mTxtStartDate.setText(df.format(mStartDate.getTime()));
		}

		if (mEndDate != null) {
			mTxtEndDate.setText(df.format(mEndDate.getTime()));
		}
	}
	
	@Override
	protected int getMenuResource() {
		return R.menu.menu_base_chart_view;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(getMenuResource(), menu);

		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_date:
			mDateBar.setVisibility(mDateBar.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private void showDatePicker() {
		DatePickerFragment datePicker = new DatePickerFragment();

		datePicker.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
			public void onDateSet(android.widget.DatePicker view, int year,
					int monthOfYear, int dayOfMonth) {
				if (mPickStartDate) {
					if (mStartDate == null) {
						mStartDate = new GregorianCalendar();
					}
				}
				else {
					if (mEndDate == null) {
						mEndDate = new GregorianCalendar();
					}
				}

				Calendar date = mPickStartDate ? mStartDate : mEndDate;
				
				date.set(Calendar.YEAR, year);
				date.set(Calendar.MONTH, monthOfYear);
				date.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				
				updateDateRangeInfo();
			}
		});
		
		Calendar initDate = mPickStartDate ? mStartDate : mEndDate;

		if (initDate != null) {
			datePicker.setInitDate(initDate.get(Calendar.YEAR), initDate.get(Calendar.MONTH), initDate.get(Calendar.DAY_OF_MONTH));
		}
		
		datePicker.show(getSupportFragmentManager(), "datePicker");
	}

	@Override
	public void onClick(View view) {
		mPickStartDate = (view == mBtnPickStartDate);
		showDatePicker();
	}
	
	protected void initCursorLoader() {
		getSupportLoaderManager().initLoader(DEFAULT_LOADER_ID, null, this);
	}
	
	protected void restartCursorLoader() {
		getSupportLoaderManager().restartLoader(DEFAULT_LOADER_ID, null, this);
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mChartView = generateChartView(data);

		if (mChartView != null) {
			LinearLayout chartContainer = (LinearLayout)findViewById(R.id.chartView);

			if (chartContainer == null) {
				throw new IllegalArgumentException("Chart view container is not defined");
			}

			chartContainer.removeAllViews();
			chartContainer.addView(mChartView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}
}

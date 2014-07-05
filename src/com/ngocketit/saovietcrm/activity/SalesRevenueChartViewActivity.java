package com.ngocketit.saovietcrm.activity;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer.Orientation;

import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import com.ngocketit.saovietcrm.R;
import com.ngocketit.saovietcrm.common.DateTimeBreakdown;
import com.ngocketit.saovietcrm.provider.SaovietCRMContentProvider;

public class SalesRevenueChartViewActivity extends BaseChartViewActivity implements ActionBar.OnNavigationListener {
	SpinnerAdapter mBreakdownAdapter;
	int mBreakdownType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mBreakdownAdapter = ArrayAdapter.createFromResource(this, 
				R.array.order_breakdowns, 
				android.R.layout.simple_spinner_dropdown_item);

		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		getSupportActionBar().setListNavigationCallbacks(mBreakdownAdapter, this);
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		mBreakdownType = position;
		restartCursorLoader();
		return true;
	}
	
	@Override
	protected String getDateFormat() {
		switch (mBreakdownType) {
		
		}
		return super.getDateFormat();
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String selection = null;
		String[] selectionArgs = null;
		
		Uri uri = null;
		
		switch (mBreakdownType) {
		case DateTimeBreakdown.DAILY:
			uri = SaovietCRMContentProvider.ContentUri.SALES_REV_DAILY_BREAKDOWN;
			break;

		case DateTimeBreakdown.WEEKLY:
			uri = SaovietCRMContentProvider.ContentUri.SALES_REV_WEEKLY_BREAKDOWN;
			break;

		case DateTimeBreakdown.MONTHLY:
			uri = SaovietCRMContentProvider.ContentUri.SALES_REV_MONTHLY_BREAKDOWN;
			break;

		case DateTimeBreakdown.YEARLY:
			uri = SaovietCRMContentProvider.ContentUri.SALES_REV_YEARLY_BREAKDOWN;
			break;
		}

		CursorLoader cursorLoader = new CursorLoader(this,
				uri,
				null, 
				selection, 
				selectionArgs, 
				null);

		return cursorLoader;
	}
	
	@Override
	protected GraphicalView generateChartView(Cursor cursor) {
	    String[] titles = new String[] { "2007", "2008" };
	    List<double[]> values = new ArrayList<double[]>();

	    values.add(new double[] { 5230, 7300, 9240, 10540, 7900, 9200, 12030, 11200, 9500, 10500,
	        11600, 13500 });

	    values.add(new double[] { 14230, 12300, 14240, 15244, 15900, 19200, 22030, 21200, 19500, 15500,
	        12600, 14000 });

	    int[] colors = new int[] { Color.CYAN, Color.BLUE };

	    XYMultipleSeriesRenderer renderer = mChartHelper.buildBarRenderer(colors);
	    renderer.setOrientation(Orientation.VERTICAL);

	    mChartHelper.setChartSettings(renderer, "Monthly sales in the last 2 years", "Month", "Units sold", 0.5,
	        12.5, 0, 24000, Color.GRAY, Color.LTGRAY);

	    renderer.setXLabels(1);
	    renderer.setYLabels(10);
	    renderer.addXTextLabel(1, "Jan");
	    renderer.addXTextLabel(3, "Mar");
	    renderer.addXTextLabel(5, "May");
	    renderer.addXTextLabel(7, "Jul");
	    renderer.addXTextLabel(10, "Oct");
	    renderer.addXTextLabel(12, "Dec");

	    int length = renderer.getSeriesRendererCount();
	    for (int i = 0; i < length; i++) {
	      SimpleSeriesRenderer seriesRenderer = renderer.getSeriesRendererAt(i);
	      seriesRenderer.setDisplayChartValues(true);
	    }
	    
	    XYMultipleSeriesDataset dataset = mChartHelper.buildBarDataset(titles, values);
	    
	    return ChartFactory.getBarChartView(this, dataset, renderer, Type.DEFAULT);
	}
}

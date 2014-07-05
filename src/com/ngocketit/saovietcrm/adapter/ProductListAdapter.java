package com.ngocketit.saovietcrm.adapter;

import android.content.Context;
import android.database.Cursor;

import com.ngocketit.saovietcrm.R;
import com.ngocketit.saovietcrm.database.ProductCategoryTable;
import com.ngocketit.saovietcrm.database.ProductTable;
import com.ngocketit.saovietcrm.util.CursorUtils;
import com.ngocketit.saovietcrm.util.GeneralUtils;

public class ProductListAdapter extends BaseSimpleList3Adapter {
	public ProductListAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int flags) {
		super(context, layout, c, from, to, flags);
	}

	@Override
	protected String getSectionHeaderColumn() {
		return ProductCategoryTable.COLUMN_NAME;
	}
	
	@Override
	protected void setUpViewHolder(BaseSectionHeaderViewHolder viewHolder, Cursor cursor,
			boolean headerValueChanged, Object[] headerVals) {
		super.setUpViewHolder(viewHolder, cursor, headerValueChanged, headerVals);
		
		float unitPrice = CursorUtils.getRecordFloatValue(cursor, ProductTable.COLUMN_UNIT_PRICE);
		
		ViewHolder holder = (ViewHolder)viewHolder;
		
		if (headerValueChanged) {
			String catName = CursorUtils.getRecordStringValue(cursor, ProductCategoryTable.COLUMN_NAME);
			int counter = (Integer)headerVals[0];

			if (catName == null) {
				catName = getContext().getString(R.string.uncategorized);
			}

			holder.sectionHeaderView1.setText(catName);
			holder.sectionHeaderView2.setText(String.valueOf(counter));
		}

		holder.text1.setText(CursorUtils.getRecordStringValue(cursor, ProductTable.COLUMN_NAME));
		holder.text2.setText(CursorUtils.getRecordStringValue(cursor, ProductTable.COLUMN_CODE));
		holder.text3.setText(GeneralUtils.formatCurrency(unitPrice));
	}
}

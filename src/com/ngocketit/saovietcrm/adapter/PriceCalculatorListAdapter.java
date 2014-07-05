package com.ngocketit.saovietcrm.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ngocketit.saovietcrm.R;
import com.ngocketit.saovietcrm.activity.PriceCalculatorListActivity;
import com.ngocketit.saovietcrm.model.PriceCalculatorLineItem;
import com.ngocketit.saovietcrm.util.GeneralUtils;

public class PriceCalculatorListAdapter extends ArrayAdapter<PriceCalculatorLineItem> {
	List<PriceCalculatorLineItem> mItems;
	private boolean mDeleteMode = false;

	public PriceCalculatorListAdapter(Context context, int resource,
			List<PriceCalculatorLineItem> objects) {
		super(context, resource, objects);
		
		mItems = objects;
	}
	
	public void toggleDeleteMode() {
		mDeleteMode = !mDeleteMode;
		notifyDataSetChanged();
	}
	
	@Override
	public long getItemId(int position) {
		PriceCalculatorLineItem item = mItems.get(position);
		return item.id;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        PriceCalculatorLineItem item = mItems.get(position);

        // We don't have recycled view, just create a fresh one
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_calculator_order_line_item, null);

            TextView text1 = (TextView)convertView.findViewById(android.R.id.text1);
            TextView text2 = (TextView)convertView.findViewById(android.R.id.text2);
            TextView priceView = (TextView)convertView.findViewById(R.id.lblPrice);
            ImageButton btnDelete = (ImageButton)convertView.findViewById(R.id.btnDelete);

            viewHolder = new ViewHolder(this, item.id, text1, text2, priceView, btnDelete);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.itemId = item.id;
        viewHolder.text1.setText(item.name);
        viewHolder.text2.setText(String.valueOf(item.quantity));
        viewHolder.totalPrice.setText(GeneralUtils.formatCurrency(item.totalPrice));
        
        viewHolder.btnDelete.setVisibility(mDeleteMode ? View.VISIBLE : View.GONE);
        
        // Change layout params depending on delete mode
        ViewGroup.LayoutParams source = (ViewGroup.LayoutParams)(viewHolder.totalPrice.getLayoutParams());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(source);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.rightMargin = (int)(8 * getContext().getResources().getDisplayMetrics().density);

        if (!mDeleteMode) {
        	params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        } else {
        	params.addRule(RelativeLayout.LEFT_OF, R.id.btnDelete);
        }

        viewHolder.totalPrice.setLayoutParams(params);

        return convertView;
	}
	
	private class ViewHolder implements OnClickListener {
		public TextView text1;
		public TextView text2;
		public TextView totalPrice;
		public ImageButton btnDelete;
		public long itemId;
		public PriceCalculatorListAdapter adapter;

		public ViewHolder(PriceCalculatorListAdapter adapter, long itemId, TextView text1, TextView text2, 
				TextView totalPrice, ImageButton btnDelete) {
			this.adapter = adapter;
			this.itemId = itemId;
			this.text1 = text1;
			this.text2 = text2;
			this.totalPrice = totalPrice;
			this.btnDelete = btnDelete;
			
			btnDelete.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			if (v == btnDelete) {
				for(PriceCalculatorLineItem item : mItems) {
					if (item.id == itemId) {
						mItems.remove(item);
						adapter.notifyDataSetChanged();

						PriceCalculatorListActivity listActivity = (PriceCalculatorListActivity)adapter.getContext();
						
						if (listActivity != null) {
							listActivity.calculateTotalPrice();
						}

						break;
					}
				}
			}
		}
	}
}

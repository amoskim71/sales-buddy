package com.ngocketit.saovietcrm.fragment;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class DatePickerFragment extends DialogFragment {
	private OnDateSetListener dateSetListener;
	private int initYear = 0;
	private int initMonth = 0;
	private int initDay = 0;
	
	public void setOnDateSetListener(OnDateSetListener listener) {
		this.dateSetListener = listener;
	}
	
	public void setInitDate(int year, int month, int day) {
		initYear = year;
		initMonth = month;
		initDay = day;
	}

	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = initYear != 0 ? initYear : c.get(Calendar.YEAR);
        int month = initMonth != 0 ? initMonth : c.get(Calendar.MONTH);
        int day = initDay != 0 ? initDay : c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this.dateSetListener, year, month, day);
    }
}

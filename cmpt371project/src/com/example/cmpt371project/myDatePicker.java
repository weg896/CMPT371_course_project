//File: myDatePicker.java
//Purpose: Allows users to pick a date and format it as YYYY-MM-DD
//Features Completed: Able to select date
//Features Incomplete: None
//Dependencies: None
//Known Bugs: None
//General Comments: None

package com.example.cmpt371project;

import android.app.DatePickerDialog;
import android.content.Context;

public class myDatePicker extends DatePickerDialog{

/**
 * @author Xingze
 * @param context
 * @param theme
 * @param callBack
 * @param year
 * @param monthOfYear
 * @param dayOfMonth
 */
	public myDatePicker(Context context, int theme, OnDateSetListener callBack,
			int year, int monthOfYear, int dayOfMonth) {
		super(context, theme, callBack, year, monthOfYear, dayOfMonth);
		
	}

	/**
	 * @author Xingze
	 * @param context
	 * @param callBack
	 * @param year
	 * @param monthOfYear
	 * @param dayOfMonth
	 */
	public myDatePicker(Context context, OnDateSetListener callBack, int year,
			int monthOfYear, int dayOfMonth) {
		super(context, callBack, year, monthOfYear, dayOfMonth);
		
	}


	protected void onStop() {

	}

	
}

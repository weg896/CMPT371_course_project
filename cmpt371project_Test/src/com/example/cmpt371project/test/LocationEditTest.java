//File: LocationTest.java
//Purpose: Test editing location function
//Features Completed: add a location first and try to edit it at very end, delete it
package com.example.cmpt371project.test;

import com.example.cmpt371project.LocalDB;
import com.example.cmpt371project.R;
import com.example.cmpt371project.childrenEdit;
import com.example.cmpt371project.locationEdit;
import com.robotium.solo.Solo;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

public class LocationEditTest extends
		ActivityInstrumentationTestCase2<locationEdit> {
			
	private Solo solo;	
	private Button save;
	private Button edit;
	private Button remove;
	private Button survey;
	private Button stat;
	
	private String id;
	private EditText locName;
	private EditText phone;
	private EditText address;
	private EditText desc;
	private EditText textViewList[];
	// Context required to get correct database
	private Context context;


	public LocationEditTest() {
		super(locationEdit.class);
	}

	@Override
	protected void setUp() throws Exception {
		Intent addIntent = new Intent();
      	addIntent.putExtra("from", "loc_add");
      	setActivityIntent(addIntent);
		solo = new Solo(getInstrumentation(),getActivity());
		super.setUp();

		textViewList = new EditText[4]; //0-firstName,1-lastName,2-phone,3-address,4-birthday,5-gender,6-postal,7- location
		save = (Button) solo.getView(R.id.edLo_save_but);
		edit = (Button)solo.getView(R.id.edCh_edit_but);
		remove = (Button)solo.getView(R.id.edLo_Remo_but);
		
		locName = (EditText)solo.getView(R.id.edLo_loca_txt);
		textViewList[0] = locName;
		address = (EditText)solo.getView(R.id.edLo_addr_txt);
		textViewList[1] = address;
		phone = (EditText)solo.getView(R.id.edLo_phon_txt);
		textViewList[2] = phone;
		desc = (EditText)solo.getView(R.id.edCh_last_txt);
		textViewList[3] = desc;

		// Get current context.
		context = getInstrumentation().getTargetContext().getApplicationContext();
	}
	
	/**
	 * this is actual test for editing location,
	 * first it add a location and try to edit it 
	 * it also check with the local database to see if the changing has been 
	 * stored or not
	 */
	public void testChildrenEdit_AddChildAndSave(){

		String[] inputList = {"GoodPlace", "Saskatoon", "0000000001", "This is a great school"};

		AddLocationAndSave(inputList);
		EditLocationAndSave(inputList);
}	
	
	/**
	 * this is a helper function which will add a location
	 * @param inputList
	 */
	private void AddLocationAndSave(String[] inputList){
		solo.assertCurrentActivity("ERR - should at activity "+locationEdit.class.toString() +", but not.", locationEdit.class);
		solo.clickOnView(edit);
		// Enter text to fields
		Log.d(Constants.TAG, "Start inputting text");
		Utils.enterTextToEditView(solo, 
				inputList,
				new EditText[]{locName,phone,address,desc});
		
		// Try to save it.
		Log.d(Constants.TAG, "Click on Save.");
		solo.clickOnView(save);
		
		
		// Get database and query for what just inputed.
		Log.d(Constants.TAG, "Try to query database");
		SQLiteDatabase db = Utils.getSQLiteDatabaseByContext(context);
		Cursor cursor = db.query(LocalDB.INSTI_TABLE, null, LocalDB.INSTI_name+"='"+inputList[0]+"'", null, null, null, null);
		// Get the record and check.
		Log.d(Constants.TAG, "Checking query result.");
		int recordCount = 0;
		while (cursor.moveToNext()) {
			//set location field number.Can not get it from project code.
			int fieldNum = 4;
			String[] valuesString = new String[fieldNum];
			for (int i=0;i<fieldNum ;i++ ) {
				valuesString[i] = cursor.getString(i);
			}			
			for (int i=0;i<fieldNum ;i++ ) {
				assertEquals("should be "+inputList[i]+" but it is "+valuesString[i]+".",inputList[i], valuesString[i]);
			}
			recordCount ++;
		}
		
		Log.d(Constants.TAG, "Assert record count:");
		// We should get only one result.		
		assertEquals("Expected only 1 result, actual " + recordCount + " results",1 ,recordCount);
		
		// Remove test row.
		Utils.removeLocationTestRecord(context, inputList[0]);

	}
	/**
	 * This is still a helping method for editing a location
	 * @param inputList: the location info
	 */
	private void EditLocationAndSave(String[] inputList){
		
		solo.clickOnView(edit);
		// Enter text to fields
		Log.d(Constants.TAG, "Start inputting text");
		Utils.enterTextToEditView(solo, 
				inputList,
				new EditText[]{locName,phone,address,desc});
		
		// Try to save it.
		Log.d(Constants.TAG, "Click on save .");
		solo.clickOnView(save);
		
		
		// Get database and query for what just inputed.
		Log.d(Constants.TAG, "Try to query database");
		SQLiteDatabase db = Utils.getSQLiteDatabaseByContext(context);
		Cursor cursor = db.query(LocalDB.INSTI_TABLE, null, LocalDB.INSTI_name+"='"+inputList[0]+"'", null, null, null, null);
		// Get the record and check.
		Log.d(Constants.TAG, "Checking query result.");
		int recordCount = 0;
		while (cursor.moveToNext()) {
			//set children field number.Can not get it from project code.
			int childrenFieldNum = 4;
			String[] valuesString = new String[childrenFieldNum];
			for (int i=0;i<childrenFieldNum ;i++ ) {
				valuesString[i] = cursor.getString(i);
			}			
			for (int i=0;i<childrenFieldNum ;i++ ) {
				assertEquals("should be "+inputList[i]+" but it is "+valuesString[i]+".",inputList[i], valuesString[i]);
			}
			recordCount ++;
		}
		
		Log.d(Constants.TAG, "Assert record count:");
		// We should get only one result.		
		assertEquals("Expected only 1 result, actual " + recordCount + " results",1 ,recordCount);
		
		// Remove test row.
		Utils.removeLocationTestRecord(context, inputList[0]);

	}
	@Override
	protected void tearDown() throws Exception{

			solo.finishOpenedActivities();
	}

}

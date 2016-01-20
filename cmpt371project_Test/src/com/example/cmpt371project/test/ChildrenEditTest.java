package com.example.cmpt371project.test;


import com.example.cmpt371project.LocalDB;
import com.example.cmpt371project.childrenEdit;
import com.robotium.solo.Solo;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.example.cmpt371project.R;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

public class ChildrenEditTest extends
		ActivityInstrumentationTestCase2<childrenEdit> {
	// Variables for widgets in ChildrenEdit UI.		
	private Solo solo;		
	private Button save;
	private Button edit;
	private Button remove;
	private Button survey;
	private Button stat;
	
	private String id;
	private EditText firstName;
	private EditText lastName;
	private EditText phone;
	private EditText address;
	private EditText birthday;
	private EditText gender;
	private EditText postal;
	private EditText location; //TODO Change to select popup
	

	

	private EditText textViewList[];


	// Context required to get correct database
	private Context context;

	public ChildrenEditTest() {
		super(childrenEdit.class);
	}

	@Override
	protected void setUp() throws Exception {
		Intent addIntent = new Intent();
      	addIntent.putExtra("from", "res_add");
      	setActivityIntent(addIntent);
		solo = new Solo(getInstrumentation(),getActivity());
		super.setUp();

		textViewList = new EditText[8]; //0-firstName,1-lastName,2-phone,3-address,4-birthday,5-gender,6-postal,7- location
		save = (Button) solo.getView(R.id.edCh_save_but);
		edit = (Button)solo.getView(R.id.edCh_edit_but);
		remove = (Button)solo.getView(R.id.edCh_Remo_but);
		stat = (Button)solo.getView(R.id.edCh_Stats_but);
		survey= (Button)solo.getView(R.id.edCh_Survey_but);
		
		firstName = (EditText)solo.getView(R.id.edCh_firs_txt);
		textViewList[0] = firstName;
		lastName = (EditText)solo.getView(R.id.edCh_last_txt);
		textViewList[1] = lastName;
		phone = (EditText)solo.getView(R.id.edCh_phon_txt);
		textViewList[2] = phone;
		address = (EditText)solo.getView(R.id.edCh_addr_txt);
		textViewList[3] = address;
		birthday = (EditText)solo.getView(R.id.edCh_birth_txt);
		textViewList[4] = birthday;
		gender = (EditText)solo.getView(R.id.edCh_gender_txt);
		textViewList[5] = gender;
		postal = (EditText)solo.getView(R.id.edCh_post_txt);
		textViewList[6] = postal;
		location = (EditText)solo.getView(R.id.edCh_location_txt);
		textViewList[7] = location;

		// Get current context.
		context = getInstrumentation().getTargetContext().getApplicationContext();

	}
	
	public void testChildrenEdit_AddChildAndSave(){
			//TODO 
			//need to use test string matrix to replace this string[].
			//that can solve the problem to test all possible cases.
			String[] inputList = {"John","Smith","3062451234","Address example","1979-01-29","Male","S7H5J7","UofS"};
			//0-firstName,1-lastName,2-phone,3-address,4-birthday,5-gender,6-postal,7- location
			AddChildAndSave(inputList);
	}

	private void AddChildAndSave(String[] inputList){
		solo.assertCurrentActivity("ERR - should at activity "+childrenEdit.class.toString() +", but not.", childrenEdit.class);
		solo.clickOnView(edit);
		// Enter text to fields
		Log.d(Constants.TAG, "Start inputting text");
		Utils.enterTextToEditView(solo, 
				inputList,
				new EditText[]{firstName,lastName,phone,address,birthday,gender,postal,location});
		
		// Try to save it.
		Log.d(Constants.TAG, "Click on Save.");
		solo.clickOnView(save);
		
		
		// Get database and query for what just inputed.
		Log.d(Constants.TAG, "Try to query database");
		SQLiteDatabase db = Utils.getSQLiteDatabaseByContext(context);
		Cursor cursor = db.query(LocalDB.CHILDREN_TABLE, null, LocalDB.CHILD_FName+"='"+inputList[0]+"'", null, null, null, null);
		// Get the record and check.
		Log.d(Constants.TAG, "Checking query result.");
		int recordCount = 0;
		while (cursor.moveToNext()) {
			//set children field number.Can not get it from project code.
			int childrenFieldNum = 8;
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
		Utils.removeChildTestRecord(context, inputList[0]);

	}
	private void EditChildAndSave(String[] inputList){
		
		solo.clickOnView(edit);
		// Enter text to fields
		Log.d(Constants.TAG, "Start inputting text");
		Utils.enterTextToEditView(solo, 
				inputList,
				new EditText[]{firstName,lastName,phone,address,birthday,gender,postal,location});
		
		// Try to save it.
		Log.d(Constants.TAG, "Click on save .");
		solo.clickOnView(save);
		
		
		// Get database and query for what just inputed.
		Log.d(Constants.TAG, "Try to query database");
		SQLiteDatabase db = Utils.getSQLiteDatabaseByContext(context);
		Cursor cursor = db.query(LocalDB.CHILDREN_TABLE, null, LocalDB.CHILD_FName+"='"+inputList[0]+"'", null, null, null, null);
		// Get the record and check.
		Log.d(Constants.TAG, "Checking query result.");
		int recordCount = 0;
		while (cursor.moveToNext()) {
			//set children field number.Can not get it from project code.
			int childrenFieldNum = 8;
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
		Utils.removeChildTestRecord(context, inputList[0]);

	}
	public void testRemoveChild() {
		//This function do not work yet.
	}
	public void testStateButton() {
		//This function do not work yet.
	}
	public void testSurveyButton() {
		//This function do not work yet.
	}
	
	
	@Override
	protected void tearDown() throws Exception{

			solo.finishOpenedActivities();
	}

}

package com.example.cmpt371project.test;

import com.example.cmpt371project.LocalDB;
import com.example.cmpt371project.R;
import com.example.cmpt371project.researcherEdit;
import com.example.cmpt371project.test.Utils.UserDetail;
import com.robotium.solo.Solo;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
/**
 * Tests for researcher edit UI.
 * 
 * File: ResearchEditTest.java<br>
 * Purpose: test researcher edit UI.<br>
 * Features Completed:<br>
 * <ul>
 * 	<li>Add a researcher with a phone number.</li>
 * 	<li>Add a researcher without a phone number.</li>
 *  <li>Add a researcher with illegal phone numbers.</li>
 * </ul>
 * Features Incompleted:<br>
 * <ul>
 * 	<li>Test with SQL Injection.</li>
 * </ul>
 * Dependencies:<br>
 * <ul>
 * 	<li>Utils.java</li>
 * 	<li>Robotium Framework</li>
 * </ul>
 * Known Bugs:<br>
 * <ul>
 * 	<li>None.</li>
 * </ul>
 * Notice:<br>
 * All values are randomly generated.
 */
public class ResearcherEditTest extends ActivityInstrumentationTestCase2<researcherEdit> {

	private Solo solo;
	
	// Variables for widgets in ResearchEdit UI.
	private Button saveButton;
	private EditText userFirstNameText;
	private EditText userLastNameText;
	private EditText userPhoneNumText;
	private EditText usernameText;
	private EditText userpasswordText;
	
	// Context required to get correct database
	private Context context;
	
	public ResearcherEditTest(){
		super(researcherEdit.class);

	}
	
	@Override
	protected void setUp() throws Exception {
		Intent addIntent = new Intent();
      	addIntent.putExtra("from", "res_add");
      	setActivityIntent(addIntent);
		solo = new Solo(getInstrumentation(),getActivity());
		super.setUp();
		
		// Find view and assign view to variables.
		saveButton = (Button) solo.getView(R.id.edRe_save_but);
		userFirstNameText = (EditText) solo.getView(R.id.edRe_firstname_txt);
		userLastNameText = (EditText) solo.getView(R.id.edRe_lastname_txt);
		userPhoneNumText = (EditText) solo.getView(R.id.edRe_phon_txt);
		usernameText = (EditText) solo.getView(R.id.edRe_userName_txt);
		userpasswordText = (EditText) solo.getView(R.id.edRe_password_txt);
		
		// Clear the EditTexts.
		Utils.clearEditViews(solo, new EditText[]{userFirstNameText, userLastNameText, userPhoneNumText, usernameText, userpasswordText});
		// Get current context.
		context = getInstrumentation().getTargetContext().getApplicationContext();
	}
	
	/**
	 * Test add a researcher with a phone number.
	 */
	public void testResearcherEdit_AddUserAndSaveWithPhone(){
		
		// Generate test values.
		UserDetail testUser = Utils.createRandomUserDetail(true);
		
		// Enter text to fields
		Log.d(Constants.TAG, "Start inputting text");
		Utils.enterTextToEditView(solo, 
				new String[]{testUser.firstName,testUser.lastName,testUser.phoneNum,testUser.username,testUser.password},
				new EditText[]{userFirstNameText, userLastNameText, userPhoneNumText, usernameText, userpasswordText});
		
		// Try to save it.
		Log.d(Constants.TAG, "Click on Save.");
		solo.clickOnView(saveButton);
		
		
		// Get database and query for what just inputed.
		Log.d(Constants.TAG, "Try to query database");
		SQLiteDatabase db = Utils.getSQLiteDatabaseByContext(context);
		Cursor cursor = db.query(LocalDB.USERS_TABLE, null, LocalDB.USER_NAME + " = '" + testUser.username +"'", null, null, null, null);
		
		// Get the record and check.
		Log.d(Constants.TAG, "Checking query result.");
		int recordCount = 0;
		while (cursor.moveToNext()) {
			// Something dirty and magic: get record. It is direct from LocalDB.java
			String[] valuesString = {
					cursor.getString(0),
					cursor.getString(1),
					cursor.getString(2),
					cursor.getString(3),
					cursor.getString(4),
					cursor.getString(5)
			};
			assertEquals("Username should be " + testUser.username,testUser.username, valuesString[0]);
			assertEquals("User password should be " + testUser.password,testUser.password, valuesString[1]);
			assertEquals("User first name should be " + testUser.firstName,testUser.firstName, valuesString[2]);
			assertEquals("User last name should be " + testUser.lastName,testUser.lastName, valuesString[3]);
			assertEquals("User phone num should be " + testUser.phoneNum,testUser.phoneNum, valuesString[4]);
			// This is a magic word. Since there is no constant or other things for privilege...
			assertEquals("User privilege should be researcher","researcher", valuesString[5]);
			recordCount ++;
		}
		
		Log.d(Constants.TAG, "Assert record count:");
		// We should get only one result.		
		assertEquals("Expected only 1 result, actual " + recordCount + " results",1 ,recordCount);
		
		// Remove test row.
		Utils.removeUserTestRecord(context, testUser.username);
	}

	/**
	 * Test add a researcher without a phone number.
	 */
	public void testResearcherEdit_AddUserAndSaveWithOutPhone(){
	
		// Generate test values without a phone number.
		UserDetail testUser = Utils.createRandomUserDetail(false);
		
		// Enter text to fields
		Log.d(Constants.TAG, "Start inputting text");
		Utils.enterTextToEditView(solo, 
				new String[]{testUser.firstName,testUser.lastName,testUser.username,testUser.password},
				new EditText[]{userFirstNameText, userLastNameText, usernameText, userpasswordText});
		
		// Try to save it.
		Log.d(Constants.TAG, "Click on Save.");
		solo.clickOnView(saveButton);
		
		
		// Get database and query for what just inputed.
		Log.d(Constants.TAG, "Try to query database");
		SQLiteDatabase db = Utils.getSQLiteDatabaseByContext(context);
		Cursor cursor = db.query(LocalDB.USERS_TABLE, null, LocalDB.USER_NAME + " = '" + testUser.username +"'", null, null, null, null);
		
		// Get the record and check.
		Log.d(Constants.TAG, "Checking query result.");
		int recordCount = 0;
		while (cursor.moveToNext()) {
			// Something dirty and magic: get record. It is direct from LocalDB.java
			String[] valuesString = {
					cursor.getString(0),
					cursor.getString(1),
					cursor.getString(2),
					cursor.getString(3),
					cursor.getString(5)
			};
			assertEquals("Username should be " + testUser.username,testUser.username, valuesString[0]);
			assertEquals("User password should be " + testUser.password,testUser.password, valuesString[1]);
			assertEquals("User first name should be " + testUser.firstName,testUser.firstName, valuesString[2]);
			assertEquals("User last name should be " + testUser.lastName,testUser.lastName, valuesString[3]);
			// This is a magic word. Since there is no constant or other things for privilege...
			assertEquals("User privilege should be researcher","researcher", valuesString[4]);
			recordCount ++;
		}
		
		Log.d(Constants.TAG, "Assert record count:");
		// We should get only one result.		
		assertEquals("Expected only 1 result, actual " + recordCount + " results",1 ,recordCount);
		
		// Remove test row.
		Utils.removeUserTestRecord(context, testUser.username);
	}
	
	/**
	 * Test use illegal values when adding user.
	 * Test value: short number, long number, wired input( include characters )
	 */
	public void testIllegalPhoneNumber(){
		Log.d(Constants.TAG, "Start inputting text");
		
		// Generate test values without a phone number.
		UserDetail testUser = Utils.createRandomUserDetail(false);
		
		// Enter text to fields
		Log.d(Constants.TAG, "Start inputting text");
		Utils.enterTextToEditView(solo, 
				new String[]{testUser.firstName,testUser.lastName,testUser.username,testUser.password},
				new EditText[]{userFirstNameText, userLastNameText, usernameText, userpasswordText});
		
		// Input a short number (4 digits), we should still on edit screen.
		
		inputPhoneNumAndSave(Utils.generateRandomString(4, false, false, true));
		solo.assertCurrentActivity("Input too short value - Should still on edit screen", researcherEdit.class);
		
		// Input a long number (20 digits), we should still on edit screen.
		
		inputPhoneNumAndSave(Utils.generateRandomString(20, false, false, true));
		solo.assertCurrentActivity("Input too long value - Should still on edit screen", researcherEdit.class);
		
		// Input an illegal value, we should still on edit screen.
		// Include number, alphabet and digit.
		
		inputPhoneNumAndSave(Utils.generateRandomString(10, true, true, true));
		solo.assertCurrentActivity("Input wired value - Should still on edit screen", researcherEdit.class);
	}
	
	@Override
	protected void tearDown() throws Exception{

			solo.finishOpenedActivities();
	}
	
	/**
	 * Internal Method: clear phone text area and input a new phone number.
	 * @param phoneNum The new phone number.
	 */
	private void inputPhoneNumAndSave(String phoneNum) {
		solo.clearEditText(userPhoneNumText);
		solo.enterText(userPhoneNumText, phoneNum);
		solo.clickOnView(saveButton);
	}
}

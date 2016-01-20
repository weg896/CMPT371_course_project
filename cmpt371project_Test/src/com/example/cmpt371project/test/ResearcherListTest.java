package com.example.cmpt371project.test;
/**
 * Tests for researcher list UI.
 * 
 * File: ResearchListTest.java<br>
 * Purpose: test researcher list UI.<br>
 * Features Completed:<br>
 * <ul>
 * 	<li>Add a researcher.</li>
 * 	<li>View a researcher.</li>
 *  <li>Edit an existed researcher.</li>
 *  <li>Remove a researcher.</li>
 *  <li>Search non-existed researcher.</li>
 *  <li>Search existed researcher that only appears once.</li>
 *  <li>Search existed researchers that only appear more than once.</li>
 * </ul>
 * Features Incompleted:<br>
 * <ul>
 * 	<li>Test in owner mode.</li>
 * </ul>
 * Dependencies:<br>
 * <ul>
 * 	<li>Utils.java</li>
 *  <li>Robotium Framework</li>
 * </ul>
 * Known Bugs:<br>
 * <ul>
 * 	<li>None.</li>
 * </ul>
 * Notice:<br>
 * All values are randomly generated.
 * This test runs in admin mode.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.EditText;
import android.widget.SearchView;

import com.example.cmpt371project.LocalDB;
import com.example.cmpt371project.R;
import com.example.cmpt371project.researcherEdit;
import com.example.cmpt371project.researcherList;
import com.example.cmpt371project.test.Utils.UserDetail;
import com.robotium.solo.Solo;

public class ResearcherListTest extends ActivityInstrumentationTestCase2<researcherList> {

	// Variables for widgets in ResearchList UI.
	private Solo solo;
	private SearchView searchText;
	
	// Context required to read correct database
	private Context context;
	
	public ResearcherListTest(){
		super(researcherList.class);

	}
	
	@Override
	protected void setUp() throws Exception {
		solo = new Solo(getInstrumentation(),getActivity());
		super.setUp();
		searchText = (SearchView) solo.getView(R.id.actionbar_searchView);
		
		// Get current context.
		context = getInstrumentation().getTargetContext().getApplicationContext();
	}
	
	/**
	 * Test add an user from Research List UI.
	 */
	public void testResearcherList_AddUserAndSave(){
		Log.d(Constants.TAG, "Begin Test: Add a user:");
		Log.d(Constants.TAG, "Try to jump to Add Researcher screen:");
		
		// Generate test values.
		UserDetail testUser = Utils.createRandomUserDetail(true);
		String[] testValues = new String[]{testUser.firstName,testUser.lastName,testUser.phoneNum,testUser.username,testUser.password};
		addOneUser(testValues);
		
		Log.d(Constants.TAG, "Search user information just added:");
		assertTrue("User not added - Could not find new user first name", solo.searchText(testUser.firstName, 1, true));
		assertTrue("User not added - Could not find new user last name", solo.searchText(testUser.lastName, 1, true));
		
		Log.d(Constants.TAG, "Confirm no duplicate:");
		assertFalse("User added but search result incorrect - duplicate result found", solo.searchText(testUser.firstName, 2, true));
		assertFalse("User added but search result incorrect - duplicate result found", solo.searchText(testUser.lastName, 2, true));
		
		Log.d(Constants.TAG,"Verify information:");
		
		// Remove test row.
		Utils.removeUserTestRecord(context, testUser.username);
	}
	
	/**
	 * Test viewing user.
	 * 
	 */
	public void testResearcherList_ViewUser(){
		Log.d(Constants.TAG, "Begin Test: View a user:");
		// Using Researcher Edit UI here, since if we directly change database, we are not triggering update.
		Log.d(Constants.TAG, "Add a new user:");
		// Generate test values.
		UserDetail testUser = Utils.createRandomUserDetail(true);
		String[] testValues = new String[]{testUser.firstName,testUser.lastName,testUser.phoneNum,testUser.username,testUser.password};
		addOneUser(testValues);
		
		// View what we just added
		Log.d(Constants.TAG, "View user information we just added:");
		Log.d(Constants.TAG, "Click on it:");
		solo.clickOnText(testUser.firstName);
		
		// Check information.
		Log.d(Constants.TAG, "Check information:");
		EditText[] views = getEditTexts();
		// Magic: it seems that EditText with android:inputType="textPassword" will change its content.
		// So we are not verify EditText, but database.
		for (int i = 0; i < views.length-1; i++) {
			String actual = views[i].getText().toString();
			StringBuilder sb = new StringBuilder("Expected:");
			sb.append(testValues[i] + "\n");
			sb.append("Actual:");
			sb.append(actual + "\n");
			Log.d(Constants.TAG, sb.toString());
			assertEquals("View User - actual text does not match expected.", testValues[i], views[i].getText().toString());
		}
		
		// Get database and verify it.
		SQLiteDatabase db = Utils.getSQLiteDatabaseByContext(context);
		Cursor cursor = db.query(LocalDB.USERS_TABLE, 
								new String[]{LocalDB.USER_PASSWORD}, 
								LocalDB.USER_NAME + " = '" + testUser.username + "'", 
								null, null, null, null);
		cursor.moveToFirst();
		assertEquals(cursor.getString(0), testUser.password);
		assertFalse("View User - should have only one user.", cursor.moveToNext());
				
		// Remove test row.
		Utils.removeUserTestRecord(context, testUser.username);
	}
	
	/**
	 * Test editing user and saving changes.
	 * 
	 */
	public void testResearcherList_EditUserAndSave(){
		Log.d(Constants.TAG, "Begin Test: Edit a user:");
		// Using Researcher Edit UI here, since if we directly change database, we are not triggering update.
		// Generate test values.
		UserDetail testUser = Utils.createRandomUserDetail(true);
		String[] testValues = new String[]{testUser.firstName,testUser.lastName,testUser.phoneNum,testUser.username,testUser.password};
		addOneUser(testValues);
		
		// View what we just added
		Log.d(Constants.TAG, "View user information we just added.");
		Log.d(Constants.TAG, "Click on it.");
		solo.clickOnText(testUser.firstName);
		
		// Start edit.
		Log.d(Constants.TAG, "Start editing:");
		solo.clickOnView(solo.getView(R.id.edRe_Edit_but));		
		Log.d(Constants.TAG, "Input new information.");
		
		// Clear EditViews
		EditText[] views = {
				(EditText) solo.getView(R.id.edRe_firstname_txt),
				(EditText) solo.getView(R.id.edRe_lastname_txt),
				(EditText) solo.getView(R.id.edRe_phon_txt),
				(EditText) solo.getView(R.id.edRe_password_txt)
		};
		Utils.clearEditViews(solo, views);

		// Input new information
		String test_FirstNameNew = Utils.generateRandomString(4, true, false, false);
		String test_LastNameNew = Utils.generateRandomString(4, true, false, false);
		String test_PhoneNumNew = Utils.generateRandomString(10, false, false, true);
		String test_PasswordNew = Utils.generateRandomString(4, true, false, false);
		String[] newTestValues = {
				test_FirstNameNew, 
				test_LastNameNew, 
				test_PhoneNumNew, 
				test_PasswordNew};
		EditText[] viewsToChange = {
				(EditText) solo.getView(R.id.edRe_firstname_txt),
				(EditText) solo.getView(R.id.edRe_lastname_txt),
				(EditText) solo.getView(R.id.edRe_phon_txt),
				(EditText) solo.getView(R.id.edRe_password_txt)
		};
		Utils.enterTextToEditView(solo, newTestValues, viewsToChange);
		
		// Save information
		Log.d(Constants.TAG, "Save user information:");
		solo.clickOnView(solo.getView(R.id.edRe_save_but));
		solo.hideSoftKeyboard();
		solo.goBack();
		
		// Verify information.
		Log.d(Constants.TAG, "View user information we just edited:");
		Log.d(Constants.TAG, "Click on it:");
		solo.clickOnText(test_FirstNameNew);
		
		
		Log.d(Constants.TAG, "Verify information:");
		String userFirstName = ((EditText) solo.getView(R.id.edRe_firstname_txt)).getText().toString();
		String userLastName = ((EditText) solo.getView(R.id.edRe_lastname_txt)).getText().toString();
		String userPhoneNum = ((EditText) solo.getView(R.id.edRe_phon_txt)).getText().toString();
		String userName = ((EditText) solo.getView(R.id.edRe_userName_txt)).getText().toString();

		
		assertEquals("Edit User - first name not changed", test_FirstNameNew, userFirstName);
		assertEquals("Edit User - last name not changed", test_LastNameNew, userLastName);
		assertEquals("Edit User - phone num not changed", test_PhoneNumNew, userPhoneNum);
		assertEquals("Edit User - user name changed", testUser.username, userName);

		// Get database and verify it.
		SQLiteDatabase db = Utils.getSQLiteDatabaseByContext(context);
		Cursor cursor = db.query(LocalDB.USERS_TABLE, 
								new String[]{LocalDB.USER_PASSWORD}, 
								LocalDB.USER_NAME + " = '" + testUser.username + "'", 
								null, null, null, null);
		cursor.moveToFirst();
		assertEquals(cursor.getString(0), test_PasswordNew);
		assertFalse("View User - should have only one user.", cursor.moveToNext());
		
		// Remove test row.
		Utils.removeUserTestRecord(context, testUser.username);
	}
	
	
	/**
	 * Test remove user
	 */
	public void testResearcherList_RemoveAnUser(){
		Log.d(Constants.TAG, "Begin Test: Delete a user:");
		// Using Researcher Edit UI here, since if we directly change database, we are not triggering update.
		Log.d(Constants.TAG, "Add a new user:");
		// Generate test values.
		UserDetail testUser = Utils.createRandomUserDetail(true);
		String[] testValues = new String[]{testUser.firstName,testUser.lastName,testUser.phoneNum,testUser.username,testUser.password};
		addOneUser(testValues);
		
		// View what we just added
		Log.d(Constants.TAG, "View user information we just added.");
		Log.d(Constants.TAG, "Click on it.");
		solo.clickOnText(testUser.firstName);
		
		// Remove the user.
		Log.d(Constants.TAG, "Remove the user.");
		solo.clickOnView(solo.getView(R.id.edRe_Edit_but));
		solo.clickOnView(solo.getView(R.id.edRe_Remo_but));
		
		// Check screen.
		assertFalse("Should not find anything in the list.", solo.searchText(testUser.firstName, 1, true, true));
	}

	
	/**
	 * Test search function when input does not match anything.
	 */
	public void testResearcherList_SearchNonExistUser(){
		Log.d(Constants.TAG, "Begin Test: Search non-existed user:");
		Log.d(Constants.TAG, "Query string: IAMNOTEXISTED");
		
		// Input query string
		EditText et = Utils.extractFromSearchView(solo, searchText);
	    solo.enterText(et, "IAMNOTEXISTED");
	    
	    // Sleep 5s, wait for refresh
	    waitForRefresh(5000, "Wait 5s for back.");

	    // We should only get two strings shown on screen. 
	    // Therefore, we require the string appearing at least 3 times, which should be impossible.
		assertFalse("There are more results than expected on screen.", solo.searchText("IAMNOTEXISTED", 3, true, true));
		
	}
	
	/**
	 * Test search function when input matches only one user.
	 */
	public void testResearcherList_SearchOnlyOneMatchingUser(){
		Log.d(Constants.TAG, "Begin Test: Search Only One Matching User:");
		// Using Researcher Edit UI here, since if we directly change database, we are not triggering update.
		Log.d(Constants.TAG, "Add a new user:");
		UserDetail testUser = Utils.createRandomUserDetail(true);
		String[] testValues = new String[]{testUser.firstName,testUser.lastName,testUser.phoneNum,testUser.username,testUser.password};
		addOneUser(testValues);
		
		Log.d(Constants.TAG, "Input keyword");
		EditText et = Utils.extractFromSearchView(solo, searchText);
	    solo.enterText(et, testUser.firstName);
	    
	    // Sleep 5s, wait for refresh
	    waitForRefresh(5000, "Wait 5s for refresh.");
	    
	    // We should only get three strings shown on screen. 
	    // Therefore, we require the string appearing at least 4 times, which should be impossible.
		assertFalse("There are more results than expected on screen.", solo.searchText(testUser.firstName, 4, true, true));
		
		// Remove test row.
		Utils.removeUserTestRecord(context, testUser.username);
	}
	
	/**
	 * Test search function when input matches some users.
	 */
	public void testResearcherList_SearchSomeUsers(){
		Log.d(Constants.TAG, "Begin Test: Search Only One Matching User:");
		// Using Researcher Edit UI here, since if we directly change database, we are not triggering update.
		// Get a random user and create new ones according to that created one. 
		Log.d(Constants.TAG, "Create users:");
		UserDetail testUser1 = Utils.createRandomUserDetail(true);
		UserDetail testUser2 = new Utils().new UserDetail(testUser1.username + "01", 
				testUser1.password, testUser1.phoneNum, testUser1.firstName + "01", testUser1.lastName);
		UserDetail testUser3 = new Utils().new UserDetail(testUser1.username + "02", 
				testUser1.password, testUser1.phoneNum, testUser1.firstName + "02", testUser1.lastName);
		
		String[] testValues1 = {testUser1.firstName, testUser1.lastName, testUser1.phoneNum, testUser1.username, testUser1.password};
		String[] testValues2 = {testUser2.firstName, testUser2.lastName, testUser2.phoneNum, testUser2.username, testUser2.password};
		String[] testValues3 = {testUser3.firstName, testUser3.lastName, testUser3.phoneNum, testUser3.username, testUser3.password};
		
		// Add test user 1
		Log.d(Constants.TAG, "Add first user.");
		addOneUser(testValues1);
		// Sleep 5s, wait for back to list screen
	    waitForRefresh(5000, "Wait 5s for back.");

	    
	    // Add test user 2
	    Log.d(Constants.TAG, "Add second user.");
	    addOneUser(testValues2);
		// Sleep 5s, wait for back to list screen
	    waitForRefresh(5000, "Wait 5s for back.");

	    
	    // Add test user 3
	    Log.d(Constants.TAG, "Add third user.");
	    addOneUser(testValues3);
		// Sleep 5s, wait for back to list screen
	    waitForRefresh(5000, "Wait 5s for back.");

	    
	    // We should only get five strings shown on screen. 
	    // Therefore, we require the string appearing at least 6 times, which should be impossible.
		assertFalse("There are more results than expected on screen.", solo.searchText(testUser1.firstName, 6, true, true));
		
		// Remove test row.
		Utils.removeUserTestRecord(context, testUser1.username);
		Utils.removeUserTestRecord(context, testUser2.username);
		Utils.removeUserTestRecord(context, testUser3.username);
		
		
	}
	
	/**
	 * Returns the EditViews to be filled in Researcher Edit UI.
	 * Sequence: first name, last name, phone num, user name, user password.
	 */
	private EditText[] getEditTexts() {
		EditText[] views = {
				(EditText) solo.getView(R.id.edRe_firstname_txt),
				(EditText) solo.getView(R.id.edRe_lastname_txt),
				(EditText) solo.getView(R.id.edRe_phon_txt),
				(EditText) solo.getView(R.id.edRe_userName_txt),
				(EditText) solo.getView(R.id.edRe_password_txt)
		};
		return views;
	}
	
	/**
	 * Add a user.
	 * @param values The details of the user. 
	 * The values should be in this sequence: first name, last name, phone num, user name, user password.
	 */
	private void addOneUser(String[] values) {
		solo.clickOnView(solo.getView(R.id.actionbar_addButton));
		assertTrue("ERR - Could not jump to Add Researcher screen in 5s", solo.waitForActivity(researcherEdit.class,5000));
		
		Log.d(Constants.TAG, "Filling in new user information.");
		EditText[] views = getEditTexts();		
		Utils.enterTextToEditView(solo, values, views);
		
		Log.d(Constants.TAG, "Save user information.");
		solo.clickOnView(solo.getView(R.id.edRe_save_but));
		solo.hideSoftKeyboard();
		solo.goBack();
	}
	
	/**
	 * Sleep for a while to wait for refreshing.
	 * @param time time to wait.
	 * @param message message to show.
	 */
	private void waitForRefresh(int time, String message) {
		Log.d(Constants.TAG, "Wait " + (time/1000) + "second for refresh");
		solo.sleep(time);
	}
	
	@Override
	protected void tearDown() throws Exception{

			solo.finishOpenedActivities();
	}
	
}

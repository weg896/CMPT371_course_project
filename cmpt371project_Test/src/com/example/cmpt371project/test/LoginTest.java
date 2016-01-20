//File: LoginTest.java
//Purpose: Test login function
//Features Completed: login as researcher and admin 


package com.example.cmpt371project.test;


import java.sql.ResultSet;

import com.example.cmpt371project.R;
import com.example.cmpt371project.Login;
import com.example.cmpt371project.admin;
import com.example.cmpt371project.researcher;
import com.robotium.solo.Solo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

public class LoginTest extends ActivityInstrumentationTestCase2<Login> {
	
	//the robotium object
	private Solo solo;
	// A text view which will let user to input username
	private EditText userID;
	// A text view which will let user to input password
	private EditText password;
	//a context which are used to grab the local database
	private Context context;

	/**
	 * Class constructor
	 */
	public LoginTest() {
		super(Login.class);
	}

	@Override
	protected void setUp() throws Exception {
		solo = new Solo(getInstrumentation());
		getActivity();
		userID = (EditText) solo.getView(R.id.userNameInput);
		password = (EditText) solo.getView(R.id.passWordInput);
		context = getInstrumentation().getTargetContext().getApplicationContext();
		super.setUp();
	}
	
	/**
	 * try to type in all kinds different stuff into text field
	 * and it should not log in
	 */
	public void testLoginInUsingInvalidInput(){
		
		//check current login activity
		solo.assertCurrentActivity("Check on login activity", Login.class);
		
		//input nothing both username and password
		Utils.clearEditViews(solo, new EditText[]{userID,password});
		solo.clickOnButton("Log in");
		solo.sleep(500);
		solo.assertCurrentActivity("Error:current activty should be login activity", Login.class);
		solo.goBackToActivity("Login");

		//input empty username 
		Utils.clearEditViews(solo, new EditText[]{userID,password});
		Utils.enterTextToEditView(solo, 
				new String[]{"123"},
				new EditText[]{password});
		solo.clickOnButton("Log in");
		solo.sleep(500);
		solo.assertCurrentActivity("Error:current activty should be login activity", Login.class);
		solo.goBackToActivity("Login");
		
		//input empty password
		Utils.clearEditViews(solo, new EditText[]{userID,password});
		Utils.enterTextToEditView(solo, 
				new String[]{"adb"},
				new EditText[]{userID});
		solo.clickOnButton("Log in");
		solo.sleep(500);
		solo.assertCurrentActivity("Error:current activty should be login activity", Login.class);
		solo.goBackToActivity("Login");
		
		//input super long character
		Utils.clearEditViews(solo, new EditText[]{userID,password});
		Utils.enterTextToEditView(solo, 
				new String[]{"abcsdflaslkdjflsjdiofelrjlsjfd","sdfsdfsdsdfsdf" },
				new EditText[]{password,userID});
		solo.clickOnButton("Log in");
		solo.sleep(500);
		solo.assertCurrentActivity("Error:current activty should be login activity", Login.class);
		solo.goBackToActivity("Login");
		
		//input non-English characters
		Utils.clearEditViews(solo, new EditText[]{userID,password});
		Utils.enterTextToEditView(solo, 
				new String[]{"速率可达减肥算了","sdfsdfsdsdfsdf" },
				new EditText[]{password,userID});
		solo.clickOnButton("Log in");
		solo.sleep(500);
		solo.assertCurrentActivity("Error:current activty should be login activity", Login.class);
		solo.goBackToActivity("Login");
		
		//input a user name who are not in database
		Utils.clearEditViews(solo, new EditText[]{userID,password});
		Utils.enterTextToEditView(solo, 
				new String[]{"yyy345","sdf"},
				new EditText[]{password,userID});
		solo.clickOnButton("Log in");
		solo.sleep(500);
		solo.assertCurrentActivity("Error:current activty should be login activity", Login.class);
		solo.goBackToActivity("Login");
			
			
	}
	
	/**
	 * Try to login as Admin
	 * Should just let correct admin username and password to log in
	 */
	public void testLoginAsAdmin(){
		//input a user name that exists in database, with wrong password
		//and it should not login
		Utils.clearEditViews(solo, new EditText[]{userID,password});
		Utils.enterTextToEditView(solo, 
				new String[]{"admin","123456"},
				new EditText[]{password,userID});
		solo.clickOnButton("Log in");
		solo.sleep(1000);
		solo.assertCurrentActivity("Should not login using invalid username and password", Login.class);
		solo.goBackToActivity("Login");	
				
		//input correct admin username and password
		//it should log in as admin
		Utils.clearEditViews(solo, new EditText[]{userID,password});
		Utils.enterTextToEditView(solo, 
				new String[]{"admin","admin"},
				new EditText[]{password,userID});
		solo.clickOnButton("Log in");
		solo.sleep(500);
		solo.assertCurrentActivity("Failed to login using correct admin username and password", admin.class);
		solo.clickOnButton("Log Out");	
	}
	
	/**
	 * test login as a researcher
	*/
	public void testLoginAsResearcher(){
		
		//input a user name that exists in database, with wrong password
		Utils.clearEditViews(solo, new EditText[]{userID,password});
		Utils.enterTextToEditView(solo, 
				new String[]{"res","123456"},
				new EditText[]{password,userID});
		solo.clickOnButton("Log in");
		solo.sleep(1000);
		solo.assertCurrentActivity("Should not login using invalid username and password", Login.class);
		solo.goBackToActivity("Login");	
				
		//input correct researcher username and password
		Utils.clearEditViews(solo, new EditText[]{userID,password});
		Utils.enterTextToEditView(solo, 
				new String[]{"res","res"},
				new EditText[]{password,userID});
		solo.clickOnButton("Log in");
		solo.sleep(500);
		solo.assertCurrentActivity("Failed to login using correct researcher username and password", researcher.class);
		solo.clickOnButton("Log Out");
	} 
	
	/**
	 * test select language button
	 * this function won't be implemented by now

	public void testSelectLanguage(){
		
	}*/
	
	
	/**
	 * test update button.. Need more info on sever database
	 * try to add a user to remote database then update.
	 * then this users info should also in our local database
	 */
	public void testUpdate(){
		//add a user to remote database
		String sqlAddUser = "INSERT INTO users (username, password) VALUES('aa1','aaa');";
		Database.modifyDatabase(sqlAddUser);
		solo.clickOnButton("Update");
		
		//now check the local database
		SQLiteDatabase db = Utils.getSQLiteDatabaseByContext(context);
		Cursor cursor = db.query("users", null, "username = 'aa1'", null, null, null, null);
		assertTrue("Failed to grab data from remote database",cursor != null);
		
		//clean up the remote database
		String sqlDelUser = "DELETE from users where username = 'aa1' ;";
		Database.modifyDatabase(sqlDelUser);
		
		//right now hit the update button then user aa1 should be in remote database again
		solo.clickOnButton("Update");
		String sqlQuery = "SELECT * from users where username = 'aa1';";
		ResultSet rs = Database.runGetFromDatabaseSQL(sqlQuery);
		assertTrue("Failed to send data to remote database",rs != null);
		
		//finally clean up the local and remote database
		Database.modifyDatabase(sqlDelUser);
		Utils.removeUserTestRecord(context, "aa1");
	}

	@Override
	protected void tearDown() throws Exception{

			solo.finishOpenedActivities();
	}
}

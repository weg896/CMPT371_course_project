package com.example.cmpt371project.test;

import com.example.cmpt371project.R;
import com.example.cmpt371project.Login;
import com.example.cmpt371project.admin;
import com.example.cmpt371project.researcher;
import com.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

//File: LoginTest.java
//Purpose: for smoke test, make sure only the admin and researcher can log in
//Features Completed: test admin log in, test research log in, test not user log in
//Features Incomplete: test "select language", "update"
//Dependencies: all development code
//Known Bugs: none
//General Comments: the features incomplete because "select language" is not developed, 
//					"update" need to stable network and need to modify the development code 
//					(database connection information)

public class LoginTest extends ActivityInstrumentationTestCase2<Login> {
	
	private Solo solo;
	private EditText userID;
	private EditText password;
	
	public LoginTest() {
		super(Login.class);
	}

	@Override
	protected void setUp() throws Exception {
		solo = new Solo(getInstrumentation());
		getActivity();
		userID = (EditText) solo.getView(R.id.userNameInput);
		password = (EditText) solo.getView(R.id.passWordInput);
		super.setUp();
	}
	
	/**
	 * try to type in all kinds different stuff into text field
	 */
	public void testLoginBasic(){
		
		//check current login activity
		solo.assertCurrentActivity("Check on login activity", Login.class);
		
		//input nothing both username and password
		solo.clearEditText(userID);
		solo.clearEditText(password);
		solo.clickOnButton("Log in");
		solo.sleep(1000);
		solo.assertCurrentActivity("Check on login activity", Login.class);

		//input correct admin username and password
		solo.clearEditText(userID);
		solo.clearEditText(password);
		solo.enterText(userID, "admin");
		solo.enterText(password, "admin");
		solo.clickOnButton("Log in");
		solo.sleep(1000);
		solo.assertCurrentActivity("Check on Admin activity", admin.class);
		solo.clickOnButton("Log Out");
		solo.assertCurrentActivity("Check on Admin activity", Login.class);
	}
	
	/**
	 * try to input currect user name and password so that go to the main screen
	 */
	public void testLogin(){
		//input correct researcher username and password
		solo.assertCurrentActivity("Check on Admin activity", Login.class);
		solo.clearEditText(userID);
		solo.clearEditText(password);
		solo.enterText(userID, "res");
		solo.enterText(password, "res");
		solo.clickOnButton("Log in");
		solo.sleep(1000);
		solo.assertCurrentActivity("Check on researcher activity", researcher.class);	
		solo.clickOnButton("Log Out");
		solo.assertCurrentActivity("Check on Admin activity", Login.class);
	}

	@Override
	protected void tearDown() throws Exception{

			solo.finishOpenedActivities();
	}
}

package com.example.cmpt371project.test;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.Button;

import com.example.cmpt371project.Login;
import com.example.cmpt371project.R;
import com.example.cmpt371project.admin;
import com.example.cmpt371project.childrenList;
import com.example.cmpt371project.locationList;
import com.example.cmpt371project.researcherList;
import com.robotium.solo.Solo;

//File: AdminTest.java
//Purpose: for smoke test, make sure admin can go to admin coordinated screen 
//Features Completed: test click "user" Button, click "children" button, 
//					click "location" button, click "log out" button
//Features Incomplete: test "stats", "choose language" "add language" "update"
//Dependencies: all development code
//Known Bugs: none
//General Comments: since some of the features are not developed, this test ignored them 

public class AdminTest extends
ActivityInstrumentationTestCase2<admin>{

	private Solo solo;
	private Button userButton;
	private Button childrenButton;
	private Button locationButton;
	private Button logOutButton;
	
	public AdminTest() {
		super(admin.class);
	}
	

	@Override
	protected void setUp() throws Exception {
		solo = new Solo(getInstrumentation(),getActivity());
		super.setUp();
		
		userButton= (Button) findViewById(R.id.admin_user_but);
		childrenButton= (Button) findViewById(R.id.admin_child_but);
		locationButton= (Button) findViewById(R.id.admin_loca_but);
		logOutButton= (Button) findViewById(R.id.admin_logO_but);
	}
	
	/**
	 * Test if the button is navigating user between activities correctly.
	 * Since the GUI is skeleton now, this button is not tested:
	 * Stats
	 */
	public void testAdmin_Navigation(){
		innerFunction(userButton, researcherList.class);
		innerFunction(childrenButton, childrenList.class);		
		innerFunction(locationButton, locationList.class);
		
		solo.clickOnView(logOutButton);
		solo.sleep(500);
		solo.assertCurrentActivity("ERR - Could not jump to login screen.", Login.class);

	}

	// the function just for help to minimize and reuse the code
	private void innerFunction(Button btn, Class cls){
		solo.clickOnView(btn);
		solo.sleep(500);
		solo.assertCurrentActivity("ERR - Could not jump to children list.", cls);
		solo.hideSoftKeyboard();
		solo.goBack();
		solo.goBack();
		solo.assertCurrentActivity("ERR - Could not jump back from children list.", admin.class);
	}

	@Override
	protected void tearDown() throws Exception{

			solo.finishOpenedActivities();
	}
	private View findViewById(int id){
		return solo.getView(id);
	}
}

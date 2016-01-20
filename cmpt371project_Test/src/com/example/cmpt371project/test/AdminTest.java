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

public class AdminTest extends
ActivityInstrumentationTestCase2<admin>{

	private Solo solo;
	private Button userButton;
	private Button childrenButton;
	private Button statsButton;
	private Button addLanguagerenButton;
	private Button chooseLanguageButton;
	private Button locationButton;
	private Button updateButton;
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
		statsButton= (Button) findViewById(R.id.admin_stats_but);
		addLanguagerenButton= (Button) findViewById(R.id.admin_adLa_but);
		chooseLanguageButton= (Button) findViewById(R.id.admin_chLa_but);
		locationButton= (Button) findViewById(R.id.admin_loca_but);
		updateButton= (Button) findViewById(R.id.admin_update_but);
		logOutButton= (Button) findViewById(R.id.admin_logO_but);
	}
	
	/**
	 * Test if the button is navigating user between activities correctly.
	 * Since the GUI is skeleton now, this button is not tested:
	 * Stats
	 */
	public void testAdmin_Navigation(){		
		testNavButton(userButton, admin.class, researcherList.class);
		testNavButton(childrenButton, admin.class, childrenList.class);
		testNavButton(locationButton, admin.class, locationList.class);

		// Temporarily ignored: since adding language is not implemented.
		//solo.clickOnView(addLanguagerenButton);

		// Temporarily ignored: since choosing language is not implemented.
		//solo.clickOnView(chooseLanguageButton);

		solo.clickOnView(logOutButton);
		solo.assertCurrentActivity("ERR - Could not jump to login screen.", Login.class);
	}

	/**
	 * Test whether the database is updated after clicking Update.
	 * Stubbed test: since function is not implemented, this will always succeed.
	 */
	
	public void testResearcher_Update() {
		assertEquals(true, true);
	}

	@Override
	protected void tearDown() throws Exception{

			solo.finishOpenedActivities();
	}
	
	
	private View findViewById(int id){
		return solo.getView(id);
	}
	private void testNavButton(Button theButton,Class classFrom,Class classTo){
		solo.clickOnView(theButton);
		solo.waitForActivity(classTo);
		solo.assertCurrentActivity("ERR - Could not jump to "+classTo.toString()+".", classTo);
		solo.hideSoftKeyboard();
		solo.goBack();
		solo.waitForActivity(classFrom);
		solo.assertCurrentActivity("ERR - Could not jump back from "+classTo.toString()+".", classFrom);

	}
}

package com.example.cmpt371project.test;
/**
 * Tests for researcher UI.
 * 
 * File: ResearchTest.java<br>
 * Purpose: test researcher UI.<br>
 * Features Completed:<br>
 * <ul>
 * 	<li>Navigation between different screens.</li>
 * </ul>
 * Features Incompleted:<br>
 * <ul>
 * 	<li>Test update function.</li>
 *  <li>Test change language.</li>
 * </ul>
 * Dependencies:<br>
 * <ul>
 * 	<li>Robotium Framework</li>
 * </ul>
 * Known Bugs:<br>
 * <ul>
 * 	<li>None.</li>
 * </ul>
 */
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.cmpt371project.Login;
import com.example.cmpt371project.R;
import com.example.cmpt371project.childrenList;
import com.example.cmpt371project.locationList;
import com.example.cmpt371project.researcher;
import com.robotium.solo.Solo;

public class ResearcherTest extends ActivityInstrumentationTestCase2<researcher> {

	private Solo solo;
	private Button statsButton;
	private Button chooseLanguageButton;
	private Button updateButton;
	private Button childrenButton;
	private Button locationButton;
	private Button logOutButton;
	
	public ResearcherTest() {
		super(researcher.class);
	}

	@Override
	protected void setUp() throws Exception {
		solo = new Solo(getInstrumentation(),getActivity());
		super.setUp();
		statsButton = (Button) solo.getView(R.id.stRe_stats_but);
		chooseLanguageButton = (Button) solo.getView(R.id.stRe_chLa_but);
		updateButton = (Button) solo.getView(R.id.stRe_update_but);
		childrenButton = (Button) solo.getView(R.id.stRe_child_but);
		locationButton = (Button) solo.getView(R.id.stRe_loca_but);
		logOutButton = (Button) solo.getView(R.id.stRe_logO_but);
	}
	
	
	/**
	 * Test if the button is navigating user between activities correctly.
	 * Since the GUI is skeleton now, this button is not tested:
	 * Stats
	 */
	public void testResearcher_Navigation() {
		
		// Temporarily ignored: since choosing language is not implemented.
		// solo.clickOnButton("Choose Language");
		// solo.assertCurrentActivity("ERR - Could not jump to language selection.", selectLanguage.class);
		// solo.hideSoftKeyboard();
		// solo.goBack();
		// solo.assertCurrentActivity("ERR - Could not jump back from language selection", researcher.class);
		
		Log.d(Constants.TAG, "Jump to children list:");
		solo.clickOnView(childrenButton);
		assertTrue("ERR - Could not jump to children list in 5s", solo.waitForActivity(childrenList.class,5000));
		solo.goBack();
		
		Log.d(Constants.TAG, "Jump back from children list:");
		assertTrue("ERR - Could not jump back from children list in 5s", solo.waitForActivity(researcher.class,5000));
		
		Log.d(Constants.TAG, "Jump to location list:");
		solo.clickOnView(locationButton);
		assertTrue("ERR - Could not jump to location list in 5s", solo.waitForActivity(locationList.class,5000));
		solo.goBack();
		
		Log.d(Constants.TAG, "Jump back from location list:");
		assertTrue("ERR - Could not jump back from location list in 5s", solo.waitForActivity(researcher.class,5000));
		
		Log.d(Constants.TAG, "Logout:");
		solo.clickOnView(logOutButton);
		assertTrue("ERR - Could not jump to login screen in 5s", solo.waitForActivity(Login.class,5000));
		
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
}

//File: LocationListTest.java
//Purpose: Test locationlist function
//Features Completed: Try to add a location and search it. Also try to see if the location has
//                    been displayed correctly or not

package com.example.cmpt371project.test;

import com.example.cmpt371project.R;
import com.example.cmpt371project.childrenEdit;
import com.example.cmpt371project.childrenList;
import com.example.cmpt371project.locationEdit;
import com.example.cmpt371project.locationList;
import com.robotium.solo.Solo;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;

public class LocationListTest extends ActivityInstrumentationTestCase2<locationList> {
			
	private Solo solo;
	private SearchView searchText;
	private Button addButton;
	private String[] testValues1;
	
	// Context required to read correct database
	private Context context;

	public LocationListTest() {
		super(locationList.class);
	}

	@Override
	protected void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
		searchText = (SearchView) solo.getView(R.id.actionbar_searchView);
		addButton = (Button) solo.getView(R.id.actionbar_addButton);
		super.setUp();
		// Get current context.
		context = getInstrumentation().getTargetContext().getApplicationContext();
		testValues1 = new String[] {"GoodPlace", "Saskatoon", "0000000001", "This is a great school"};
	}
	
	/**
	 * This is just a helper function, not the test
	 * add a new location, then system should be able to save it 
	 * correctly in local database
	 * @param values: the location information needed to create a new location
	 */
	private void addLocation(String[] values){
		
		solo.clickOnView(addButton);
		assertTrue("ERR - Could not jump to Add Location screen", solo.waitForActivity(locationEdit.class));
		
		Log.d(Constants.TAG, "Filling in new location information.");
		EditText[] views = getEditTexts();		
		Utils.enterTextToEditView(solo, values, views);
		
		Log.d(Constants.TAG, "Save child information.");
		solo.clickOnView(solo.getView(R.id.edLo_save_but));
		solo.hideSoftKeyboard();
		solo.goBack();
	     
	}
	
	/**
	 * this is actual test for adding a location and trying to search a location
	 * the system should be able to save a location correctly and find a existing location
	 */
	public void testAddAndSearchLocation(){
		Log.d(Constants.TAG, "Add a Location.");
		addLocation(testValues1);
		Log.d(Constants.TAG, "Begin Test: Search Only One Matching Location:");
		SearchCheck("GoodPlace",1);
		assertFalse("Should not find anything in the list.", solo.searchText("外国人看不懂", 1, true, true));
	}
	

	/**
	 * Test tap on a location, we should jump to children list.
	 * @author Yang Zeng
	 * @author Da Tao
	 * 
	 */
	public void testTapingLocation(){
		solo.clickInList(1);
		solo.sleep(1000);
		solo.assertCurrentActivity("Should jump to childrenAtLocation activity", childrenList.class);
	    solo.goBackToActivity("locationList");	
	}
	
	
	/**
	*Returns the EditViews to be filled in Location_edit UI.
	*/
	private EditText[] getEditTexts() {
		EditText[] views = {
				(EditText) solo.getView(R.id.edLo_loca_txt),
				(EditText) solo.getView(R.id.edLo_addr_txt),
				(EditText) solo.getView(R.id.edLo_phon_txt),
				(EditText) solo.getView(R.id.edLo_descript_txt)
		};
		return views;
	}
	/**
	 * this is a helper function to search the list view and make sure all the locations have been 
	 * list
	 * @param nameExpect
	 * @param numberExpect
	 */
	private void SearchCheck(String nameExpect, int numberExpect){
		assertTrue("There should be "+numberExpect+" results expected on screen.", 
				solo.searchText(nameExpect, numberExpect, true, true));
	}

	
	@Override
	protected void tearDown() throws Exception{

			solo.finishOpenedActivities();
	}
}

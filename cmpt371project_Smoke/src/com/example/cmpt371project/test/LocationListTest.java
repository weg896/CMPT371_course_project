package com.example.cmpt371project.test;

import com.example.cmpt371project.R;
import com.example.cmpt371project.childrenList;
import com.example.cmpt371project.locationEdit;
import com.example.cmpt371project.locationList;
import com.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

//File: LocationListTest.java
//Purpose: for smoke test,  test the location list activity with add a location, 
// 			go to the children list, and remove a location
//Features Completed: add a location, go to the children list, and remove a location
//Features Incomplete: none
//Dependencies: all development code
//Known Bugs: none
//General Comments: none

public class LocationListTest extends
		ActivityInstrumentationTestCase2<locationList> {
			
	private Solo solo;

	public LocationListTest() {
		super(locationList.class);
	}

	@Override
	protected void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
		super.setUp();
	}
	
	/**
	 * test add a location, go to the children, and remove a location
	 */
	public void testAllThingForLocationList(){
		
		solo.assertCurrentActivity("step 1 add a  location ", locationList.class);
		
		addLocationTest();
		
		solo.assertCurrentActivity("step 2 go to the children list activity", locationList.class);
		
		gotoChildrenListTest();
				
		solo.assertCurrentActivity("step 3 remove the location ", locationList.class);    
		
		removeLocationTest();
		
		solo.assertCurrentActivity("finishing error of location list smoke test", locationList.class);
	}
	
	
	// the function to add a location
	private void addLocationTest(){
		
		solo.clickOnButton("Add");
		solo.sleep(500);
		solo.assertCurrentActivity("Should jump to location_edit activity for add location", locationEdit.class);
		
		EditText name = (EditText) solo.getView(R.id.edLo_loca_txt);
		EditText address = (EditText) solo.getView(R.id.edLo_addr_txt);
		EditText phone = (EditText) solo.getView(R.id.edLo_phon_txt);
		EditText description = (EditText) solo.getView(R.id.edLo_descript_txt);
		
		solo.clickOnView(name);
		solo.typeText(name, "location");

		solo.clickOnView(address);
		solo.typeText(address, "address");
		
		solo.clickOnView(phone);
		solo.typeText(phone, "1234567890");
		
		solo.clickOnView(description);
		solo.typeText(description, "description");
		
		solo.clickOnButton("Save");
		solo.goBack();
	}
	
	// test for goto childern list
	private void gotoChildrenListTest(){
		solo.clickInList(1);
		solo.assertCurrentActivity("Should jump to location_edit activity", locationEdit.class);
		
		solo.clickOnButton("View Children List");
		solo.assertCurrentActivity("Should jump to children_list activity", childrenList.class);
		solo.hideSoftKeyboard();
		solo.goBack();
		solo.goBack();
		solo.goBack();
	}
	
	// test for remove the location
	private void removeLocationTest(){
		solo.clickInList(1);
		solo.assertCurrentActivity("Should jump to location_edit activity for remove location", locationEdit.class);
		
		solo.clickOnButton("Edit");
		solo.clickOnButton("Remove");
	}
	
	
	@Override
	protected void tearDown() throws Exception{

			solo.finishOpenedActivities();
	}
}

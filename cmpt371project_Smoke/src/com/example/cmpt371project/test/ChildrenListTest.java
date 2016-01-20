package com.example.cmpt371project.test;

import com.example.cmpt371project.childrenEdit;
import com.example.cmpt371project.childrenList;
import com.example.cmpt371project.survey;
import com.example.cmpt371project.surveyFinish;
import com.robotium.solo.Solo;
import android.widget.Button;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
import com.example.cmpt371project.R;

//File: ChildrenListTest.java
//Purpose: for smoke test, test the children list activity with add a child, take the survey, and remove a child
//Features Completed: add a child, take the survey, and remove a child
//Features Incomplete: none
//Dependencies: all development code
//Known Bugs: using ASUS ME173X android 4.2.1 would make the take survey crash when click "yes, go to result"
//General Comments: none

public class ChildrenListTest extends
		ActivityInstrumentationTestCase2<childrenList> {

	private Solo solo;

	/**
	 * author weixiong
	 * this class is for test the children list activity with add a child, take the survey, and remove a child		
	 */
	public ChildrenListTest() {
		super(childrenList.class);
	}

	protected void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
		super.setUp();
	}

	
	/**
	 * Test the add child, take survey, and remove the child
	 */
	public void testAllThingForChildrenList(){
		
	
		solo.assertCurrentActivity("Step 1 add children, starting error",childrenList.class);
		
		addChildTest();
		
		solo.assertCurrentActivity("Step 2 take the survey, starting error",childrenList.class);
		
		takeSurveyTest();
		
		solo.assertCurrentActivity("Step 3 delete the children, starting error",childrenList.class);
		
		deleteChildrenTest();
		
		solo.assertCurrentActivity("finishing error of children list smoke test ",childrenList.class);
	}
	
	// the function to add a Child
	private void addChildTest(){
		
		
		solo.clickOnButton("Add");
		solo.sleep(500);
		solo.assertCurrentActivity("Should jump to children_edit activity", childrenEdit.class);

		EditText firstName = (EditText) solo.getView(R.id.edCh_firs_txt);
		EditText lastName = (EditText) solo.getView(R.id.edCh_last_txt);
		EditText birthday = (EditText) solo.getView(R.id.edCh_birth_txt);
		EditText gender = (EditText) solo.getView(R.id.edCh_gender_txt);
		EditText location = (EditText) solo.getView(R.id.edCh_location_txt);
		EditText address = (EditText) solo.getView(R.id.edCh_addr_txt);
		EditText postal = (EditText) solo.getView(R.id.edCh_post_txt);
		EditText phone = (EditText) solo.getView(R.id.edCh_phon_txt);
		
		
		//input the information of the child
		solo.clickOnView(firstName);
		solo.typeText(firstName, "first");
		
		solo.clickOnView(lastName);
		solo.typeText(lastName, "last");
		
		solo.clickOnView(birthday);
		solo.clickOnButton("Set"); // for some device would be "Done"

		solo.clickOnView(gender);
		solo.clickInList(1);
		
		solo.clickOnView(location);
		solo.clickInList(1);
		
		solo.clickOnView(address);
		solo.typeText(address, "address");
		
		solo.clickOnView(postal);
		solo.typeText(postal, "s7s7l7");
		
		solo.clickOnView(phone);
		solo.typeText(phone, "1234567890");
		
		solo.clickOnButton("Save");
		solo.sleep(100);
		solo.goBack();
	}
	
	// the function to take survey of the children
	private void takeSurveyTest(){
		// select the first child on the children list
		solo.clickInList(1);
		solo.assertCurrentActivity("Should jump to children_edit activity", childrenEdit.class);
		
		solo.clickOnButton("Take Survey");
		solo.assertCurrentActivity("Should jump to survey activity", survey.class);
		
		// taking the survey question
		for(int i=1;i<survey.getTotalQuestionNumber();i++){
			solo.clickOnRadioButton(0);
			solo.clickOnButton("Next");
		}
		
		Button yes = (Button) solo.getView(R.id.suFi_next_but);
		
		// finish the survey
		solo.assertCurrentActivity("The survey finish screen has problem", surveyFinish.class);
		solo.clickOnView(yes);
		solo.goBackToActivity("childrenList.class");
	}
	
	// the functin for delete the first child on the children list 
	private void deleteChildrenTest(){
		solo.clickInList(1);
		solo.assertCurrentActivity("Should jump to children_edit activity", childrenEdit.class);
		
		solo.clickOnButton("Edit");
		solo.clickOnButton("Remove");
	}
	@Override
	protected void tearDown() throws Exception{

			solo.finishOpenedActivities();
	}
}

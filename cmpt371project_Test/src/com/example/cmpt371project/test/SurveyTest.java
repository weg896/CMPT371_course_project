//File: SurveyTest.java
//Purpose: Test the survey process
//Features Completed: Doing a survey, and try to jump questions and submit unanswered questions
package com.example.cmpt371project.test;

import com.example.cmpt371project.survey;
import com.example.cmpt371project.surveyFinish;
import com.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.RadioButton;

public class SurveyTest extends ActivityInstrumentationTestCase2<survey> {
	
	private Solo solo;

	
	public SurveyTest() {
		super(survey.class);
	}

	protected void setUp() throws Exception {
		solo = new Solo(getInstrumentation());
		getActivity();
		super.setUp();
	}
	
	/**
	 * test the whole survey process, it should let user to choose answers and jump to next question
	 * also it should allow user to jump back to previous question
	 * This test case, there is no jump back and missing question
	 */
	//right now, this function still has serious bug, cannot submit and check result
	public void testWholeTestProcess(){
		solo.assertCurrentActivity("Check current activity", survey.class);
		//there are 17 question, let them all choose first answer
		for(int i=0;i<17;i++){
			solo.clickOnRadioButton(0);
			solo.clickOnButton("Next");
		}
		//right now there should be a pop-up window to ask user
		// if they want to submit result
		solo.clickOnButton("yes,go to result");		
		//right now assume the next button is the last button
		solo.assertCurrentActivity("survey should be finished", surveyFinish.class);
		
		//should have a finished button
		//solo.clickOnButton("Finish");
		//solo.assertCurrentActivity("survey should be finished", surveyFinish.class);
	}
	
	/**
	 * Go through the whole process with jump back
	 */
	public void testWholeTestProcessWithJump(){
		solo.assertCurrentActivity("Check current activity", survey.class);
		//there are 17 question, let them all choose first answer
		//let it jump back and forth several times
		for(int i=0;i<5;i++){
			solo.clickOnRadioButton(0);
			solo.clickOnButton("Next");
			solo.clickOnButton("Previous");
		}
		for(int i=0;i<17;i++){
			solo.clickOnRadioButton(0);
			solo.clickOnButton("Next");
		}
		
		//right now there should be a pop-up window to ask user
		// if they want to submit result
		solo.clickOnButton("yes,go to result");		
		//right now assume the next button is the last button
		solo.assertCurrentActivity("survey should be finished", surveyFinish.class);		
	}
	
	/**
	 * Go through the whole process with trying to miss some question
	 */
	public void testWholeTestProcessWithMissQues(){
		solo.assertCurrentActivity("Check current activity", survey.class);
		//there are 17 question, let them all choose first answer
		//miss first question
		solo.clickOnButton("Next");
		assertTrue("Err: app allows user to jump to next question without answer current question",
				solo.getButton("Previous") == null);
		
		for(int i=0;i<17;i++){
			solo.clickOnRadioButton(0);
			solo.clickOnButton("Next");
		}
		
		//right now there should be a pop-up window to ask user
		// if they want to submit result
		solo.clickOnButton("yes,go to result");		
		//right now assume the next button is the last button
		solo.assertCurrentActivity("survey should be finished", surveyFinish.class);		
	}	
	
	@Override
	protected void tearDown() throws Exception{

			solo.finishOpenedActivities();
	}

}

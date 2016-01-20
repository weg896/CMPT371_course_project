//File: SurveyFinishTest.java
//Purpose: Test SurveyFinish function. Review the survey result
//Features Completed: Just test go back button right now, rest features are not completed
package com.example.cmpt371project.test;

import com.example.cmpt371project.survey;
import com.example.cmpt371project.surveyFinish;
import com.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;

public class SurveyFinishTest extends
		ActivityInstrumentationTestCase2<surveyFinish> {
			
	private Solo solo;		
			
	public SurveyFinishTest() {
		super(surveyFinish.class);
	}

	protected void setUp() throws Exception {
		solo = new Solo(getInstrumentation(),getActivity());
		
		super.setUp();
	}
	
	/**
	 * test go back button
	 */
	public void testGoback(){
		solo.assertCurrentActivity("running wrong activity", surveyFinish.class);
		
		solo.clickOnButton("No,go back");
		solo.sleep(1000);
		solo.assertCurrentActivity("should be survey activity",survey.class );
	}
	
	/**
	 * test go to result , it is not implemented yet
	 */
//    public void testGoToResult(){
//    	solo.clickOnButton("Yes,go to result");
//    	solo.assertCurrentActivity(message, activityClass);
//    }
	@Override
	protected void tearDown() throws Exception{

			solo.finishOpenedActivities();
	}
}

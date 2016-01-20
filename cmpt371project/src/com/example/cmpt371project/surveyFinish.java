//File: surveyFinish
//Purpose: calculate and record the survey results in the database
//Features Completed: calculate and record the survey result in the database
//Features Incomplete: None
//Dependencies: surveyResult.java 
//Known Bugs: Can not save the survey, this occurs on some devices and on others it works, it will crash the app 
// 	It has been tested to work on Nexus 4,7 (Android 4.3, 4.3) and Samsung S3 Android 4.3 , but crashes on ASUS ME173X (Android 4.2)
// 	This may be an OS version problem
//	When reaching the last question of the survey, the screen will flash back the first question then after 
//	1 sec it will jump to the survey_finish screen it has worked 

package com.example.cmpt371project;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.cmpt371project.R;

/**
 * survey.java calls this class
 * this class calls survey result
 * 
 * This class will calculate the survey results and store all of the values into the survey table in the database
 * @author Spencer
 *
 */
public class surveyFinish extends Activity{
	private Button preButton;
	private Button nextButton;

	private int score;


	private LocalDB surveyDB;

	private String endDate;
	private String endTime;

	@Override
	//when this class is called this method will be called
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_survey_finish	);
		//this array needs to be final inorder for the pass to work
		final int[] myAnswersArray = getIntent().getIntArrayExtra("answers");

		initialComponent();

		//use the array passed in from survey to calculate 
		score = calculateScore(myAnswersArray);

		//Previous button actions
		preButton.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent addIntent = new Intent();
				addIntent.putExtra("answers", myAnswersArray);
				addIntent.setClass(surveyFinish.this, survey.class);
				surveyFinish.this.startActivity(addIntent);

			}});

		//Next button actions
		nextButton.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {


				/*
				 * codeblock used for testing
				 */
				String childID = getIntent().getStringExtra("childID"); 
				//surveyDB.getLocationByChildID(getIntent().getStringExtra("childID"));
				String userID = getIntent().getStringExtra("UserID");
				String locationID = getIntent().getStringExtra("LocationID");
				String startDate = getIntent().getStringExtra("startDate");
				String startTime = getIntent().getStringExtra("startTime");
				//surveyDB.getLocationByChildID("1");

				//gets time
				endDate	= getEndDate();
				endTime = getEndTime();


				/*
				 * adds the survey to the database (relies on LocalDB.java classes)
				 */
				surveyDB.addSurvey(
						childID, 
						locationID,
						userID, 
						myAnswersArray,
						score, 
						endDate);

				/*
				 * hand off to the results screen
				 */
				Intent nextIntent = new Intent();
				nextIntent.putExtra("score", score);
				nextIntent.setClass(surveyFinish.this, surveyResult.class);
				surveyFinish.this.startActivity(nextIntent);
				finish();
			}});
	}

	private void initialComponent(){


		surveyDB = new LocalDB(this);

		preButton= (Button)findViewById(R.id.suFi_prev_but);
		nextButton= (Button)findViewById(R.id.suFi_next_but);


	}
	public static String getEndTime() {
		StringBuilder sb = new StringBuilder(8);
		Calendar c = Calendar.getInstance();
		sb.append(c.get(Calendar.HOUR_OF_DAY));
		sb.append(":");
		sb.append(c.get(Calendar.MINUTE));
		sb.append(":");
		sb.append(c.get(Calendar.SECOND));

		return sb.toString();
	}

	public static String getEndDate() {
		StringBuilder sb = new StringBuilder(8);
		Calendar c = Calendar.getInstance();
		sb.append(c.get(Calendar.YEAR));
		sb.append(":");
		sb.append(c.get(Calendar.MONTH));
		sb.append(":");
		sb.append(c.get(Calendar.DATE));

		return sb.toString();
	}

	//here all the answers are added together, it is done this way because of the random way 
	//the questions are weighted.
	//edit excel file for this crazy structure
	protected int calculateScore(int[] answers)
	{
		int score = 0;

		score =	calc0124(answers[2], score);
		score =	calc01234(answers[3], score);
		score =	calc0124(answers[4], score);
		score =	calc01234(answers[5], score);
		score =	calc43210(answers[6], score);
		score =	calc4210(answers[7], score);
		score =	calc4210(answers[8], score);
		score =	calc4210(answers[9], score);
		score =	calc43102(answers[10], score);
		score =	calc01234(answers[11], score);
		score =	calc43210(answers[12], score);
		score =	calc43210(answers[13], score);
		score =	calc40(answers[14], score);
		score =	calc43210(answers[15], score);
		score =	calc04(answers[16], score);
		score =	calc402(answers[17], score);


		return score;
	}

	/*
	 * every question score is added here
	 */
	protected int calc0134(int questionAnswerNumber, int score)
	{
		switch (questionAnswerNumber) {
		case 1: score += 0;
		break;
		case 2: score += 1;
		break;
		case 3: score += 3;
		break;
		case 4: score += 4;
		break;
		}
		return score;
	}
	protected int calc0124(int questionAnswerNumber, int score)
	{
		switch (questionAnswerNumber) {
		case 1: score += 0;
		break;
		case 2: score += 1;
		break;
		case 3: score += 2;
		break;
		case 4: score += 4;
		break;
		}
		return score;
	}
	protected int calc01234(int questionAnswerNumber, int score)
	{
		switch (questionAnswerNumber) {
		case 1: score += 0;
		break;
		case 2: score += 1;
		break;
		case 3: score += 2;
		break;
		case 4: score += 3;
		break;
		case 5: score += 4;
		break;
		}
		return score;
	}
	protected int calc43210(int questionAnswerNumber, int score)
	{
		switch (questionAnswerNumber) {
		case 1: score += 4;
		break;
		case 2: score += 3;
		break;
		case 3: score += 2;
		break;
		case 4: score += 1;
		break;
		case 5: score += 0;
		break;
		}
		return score;
	}
	protected int calc4210(int questionAnswerNumber, int score)
	{
		switch (questionAnswerNumber) {
		case 1: score += 4;
		break;
		case 2: score += 2;
		break;
		case 3: score += 1;
		break;
		case 4: score += 0;
		break;

		}
		return score;
	}
	protected int calc43102(int questionAnswerNumber, int score)
	{
		switch (questionAnswerNumber) {
		case 1: score += 4;
		break;
		case 2: score += 3;
		break;
		case 3: score += 1;
		break;
		case 4: score += 0;
		break;
		case 5: score += 2;
		break;
		}
		return score;
	}

	protected int calc40(int questionAnswerNumber, int score)
	{
		switch (questionAnswerNumber) {
		case 1: score += 4;
		break;
		case 2: score += 0;
		break;

		}
		return score;
	}
	protected int calc04(int questionAnswerNumber, int score)
	{
		switch (questionAnswerNumber) {
		case 1: score += 0;
		break;
		case 2: score += 4;
		break;

		}
		return score;
	}
	protected int calc402(int questionAnswerNumber, int score)
	{
		switch (questionAnswerNumber) {
		case 1: score += 4;
		break;
		case 2: score += 0;
		break;
		case 3: score += 2;
		break;
		}
		return score;
	}
}
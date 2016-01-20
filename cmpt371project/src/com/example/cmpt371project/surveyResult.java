//File: surveyResult.java
//Purpose: Displays the result after a child has taken a survey
//Features Completed: Able to calculate score points correctly
//Features Incomplete: Since the score meaning is part of the survey, and the survey is copyrighted
// the meaning of the score is not implemented.
//Dependencies: surveyFinish.java
//Known Bugs: None
//General Comments: None

package com.example.cmpt371project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class surveyResult extends Activity{
	private Button returnButton;
	private TextView scoreText;
	private Bundle mBundle; //for get value from intent
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_survey_result);
		
		returnButton = (Button)findViewById(R.id.survey_result_Return);
		scoreText = (TextView)findViewById(R.id.survey_result_score);
		
		mBundle = getIntent().getExtras();
		int score = (Integer) mBundle.get("score");
		
		scoreText.setText("Your score is   " + score);
		
		returnButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent returnIntent = new Intent();
				returnIntent.setClass(surveyResult.this, childrenList.class);
				surveyResult.this.startActivity(returnIntent);
				finish();
				
			}
		});
	}
}

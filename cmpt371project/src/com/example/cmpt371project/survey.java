//File: survey.java
//Purpose: Present and record all survey questions
//Features Completed: Radio buttons and label are present and next and back buttons work with selection remembered. 
//Features Incomplete: Client did not want us to put the question and answers in directly so currently there is only place holders
//Dependencies: surveyFinish.java
//Known Bugs: 	The survey is 17 questions but it needs an array of 20 elements in case the user has a laggy phone and can send the
//				next command multiple times after the survey is completed

package com.example.cmpt371project;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.cmpt371project.R;


/**
 * This class is called when the take survey button in editChildren is called
 * It calls surveyFinish
 * 
 * This method enters answers into an array and then sends them to surveyFinish
 * It is the same screen for all of questions, they Radio Buttons and text changes when the next or previous button is clicked
 * If there are not 5 responses it will hide questions
 * 
 * @author Spencer
 *
 */



public class survey extends Activity{


	protected static final int INVISIBLE = 0;
	private Button nextButton;
	private Button prevButton;

	private TextView questionLabel;

	private RadioGroup radGroup;

	private RadioButton answer1;
	private RadioButton answer2;
	private RadioButton answer3;
	private RadioButton answer4;
	private RadioButton answer5;


	public int[] answers; 

	private String startTime;

	// modify by weixiong, 
	// every survey has the same number of questions
	// also for smoke test to use the static method to get the number
	final private static int numberOfQuestions= 20;
	private int questionResponse;

	private int questionCounter = 1;
	private String startDate;

	private String childID;




	//Called when the class is created
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Default calls for andriod classes
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_survey);

		//initialize most variables
		initialComponent();

		//makes the previous button hidden if it is the first question
		prevVisability();

		//makes the methods for when the next and prev buttons are called
		setSaveListenerForNext();
		setSaveListenerForPrev();

		//loads the questions
		questionLabelChange();
	}

	private void initialComponent(){
		nextButton= (Button)findViewById(R.id.suQU_next_but);
		prevButton= (Button)findViewById(R.id.suQu_prev_but);



		questionLabel = (TextView)findViewById(R.id.suQu_ques_lab);


		answer1 = (RadioButton)findViewById(R.id.radio0);
		answer2 = (RadioButton)findViewById(R.id.radio1);
		answer3 = (RadioButton)findViewById(R.id.radio2);
		answer4 = (RadioButton)findViewById(R.id.radio3);
		answer5 = (RadioButton)findViewById(R.id.radio4);
		answer5.setVisibility(View.INVISIBLE);

		radGroup =(RadioGroup)findViewById(R.id.radioGroup1);
		//set to 20 because of the possibility of a laggy device going over 18 by hitting the next button a bunch
		answers = new int[20];

		//If the method is coming back from the surveyFinish screen
		if (getIntent().getIntArrayExtra("answers") != null){
			answers = getIntent().getIntArrayExtra("answers");
		}
		//if it is called from childrenEdit it will go here
		else {
			childID = getIntent().getStringExtra("childID");
			startDate = getStartDate();
			startTime = getStartTime();
		}


		clearQuestion();


	}

	//Next button
	private void setSaveListenerForNext(){
		nextButton.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {

				//converts the radio button into an integer value
				getRadioValue();
				//stores the answer int the array
				answers[questionCounter]=questionResponse;
				//changes to the next question
				questionCounter++;
				//sees what radio buttons should be hidden
				assignVisibility();
				//makes the previous button hidden if it is the first question
				prevVisability();

				//unchecks all the radio button
				clearQuestion();

				//changes the questions and answers
				questionLabelChange();

				//finishes the survey if the question is above 
				if (questionCounter==numberOfQuestions)
				{
					//goes to the surveyFinish class with passed values
					addFinishIntent();

				}





			}});
	}

	//The previous button's event handler
	protected void setSaveListenerForPrev() {

		prevButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {

				if (questionCounter > 1)
					questionCounter--;
				assignVisibility();
				prevVisability();
				questionLabelChange();
				clearQuestion();


			}});
	}

	//adds all the values it needs 
	protected void addFinishIntent()
	{
		Intent addIntent = new Intent();
		//addIntent.getIntArrayExtra("answer",answers);
		addIntent.putExtra("answers", answers);
		addIntent.putExtra("startDate", startDate);
		addIntent.putExtra("startTime", startTime);
		addIntent.putExtra("childID", childID);
		addIntent.putExtra("UserID", (getIntent().getStringExtra("UserID")));
		addIntent.putExtra("LocationID", (getIntent().getStringExtra("LocationID")));
		addIntent.setClass(survey.this, surveyFinish.class);
		survey.this.startActivity(addIntent);
		finish();
	}

	//gets the radi
	protected void getRadioValue()
	{
		if (answer1.isChecked())  questionResponse=1;
		else if (answer2.isChecked())  questionResponse=2;
		else if (answer3.isChecked())  questionResponse=3;
		else if (answer4.isChecked())  questionResponse=4;
		else if (answer5.isChecked())  questionResponse=5;
		//Ensures that the question is answered before moving on
		else questionCounter--;
	}

	//question counter must be positive
	protected void prevVisability()
	{

		if (questionCounter == 1)
			prevButton.setVisibility(View.INVISIBLE);
		else if (questionCounter > 1)
			prevButton.setVisibility(View.VISIBLE);
		else;
		//Throw error TO DO
	}

	//unchecks all the radio buttons
	protected void clearQuestion()
	{
		if (questionCounter <numberOfQuestions){

			switch (answers[questionCounter])
			{
			case 1: answer1.setChecked(true); break;
			case 2: answer2.setChecked(true); break;
			case 3: answer3.setChecked(true); break;
			case 4: answer4.setChecked(true); break;
			case 5: answer5.setChecked(true); break;
			default: radGroup.clearCheck();
			}

		}
	}

	//If a question only has a few responses this class will hide the unnecessary radio buttons
	protected void assignVisibility()
	{
		answer3.setVisibility(View.VISIBLE);
		answer4.setVisibility(View.VISIBLE);
		answer5.setVisibility(View.VISIBLE);
		if (questionCounter == 14 || questionCounter == 16)
			answer3.setVisibility(View.INVISIBLE);
		if (questionCounter == 17 || questionCounter == 14 || questionCounter == 16)
			answer4.setVisibility(View.INVISIBLE);
		if (questionCounter == 1 || questionCounter == 2 || questionCounter == 4|| questionCounter == 7|| questionCounter == 8|| questionCounter == 9|| questionCounter == 14|| questionCounter == 16|| questionCounter == 17)
			answer5.setVisibility(View.INVISIBLE);
	}


	public static String getStartTime() {
		StringBuilder sb = new StringBuilder(8);
		Calendar c = Calendar.getInstance();
		sb.append(c.get(Calendar.HOUR_OF_DAY));
		sb.append(":");
		sb.append(c.get(Calendar.MINUTE));
		sb.append(":");
		sb.append(c.get(Calendar.SECOND));

		return sb.toString();
	}
	public static String getStartDate() {
		StringBuilder sb = new StringBuilder(8);
		Calendar c = Calendar.getInstance();
		sb.append(c.get(Calendar.YEAR));
		sb.append(":");
		sb.append(c.get(Calendar.MONTH));
		sb.append(":");
		sb.append(c.get(Calendar.DATE));

		return sb.toString();
	}

	//where the questions will be stored !!!!! Use excel to enter in these values, drag the questions down and enter the fields. Don't waste time typing unnecessarily !!!!!! )
	protected void questionLabelChange()
	{
		switch (questionCounter) {
		case 	1	: 	answer1.setText("	test	");	answer2.setText("	ftfjvbxcb	");	answer3.setText("	zvcxcvzx	");	questionLabel.setText("	Question1	");	break;
		case 	2	: 	answer1.setText("	test	");	answer2.setText("	aesf	");	answer3.setText("	asdf	");	questionLabel.setText("	Question2	");	break;
		case 	3	: 	answer1.setText("	test	");	answer2.setText("	ftfjvbxcb	");	answer3.setText("	zvcxcvzx	");	questionLabel.setText("	Question3	");	break;
		case 	4	: 	answer1.setText("	test	");	answer2.setText("	aesf	");	answer3.setText("	Question4	"); break;
		case 	5	: 	answer1.setText("	test	");	answer2.setText("	ftfjvbxcb	");	answer3.setText("	zvcxcvzx	");	questionLabel.setText("	Question5	");	break;
		case 	6	: 	answer1.setText("	test	");	answer2.setText("	ftfjvbxcb	");	answer3.setText("	zvcxcvzx	");	questionLabel.setText("	Question6	");	break;
		case 	7	: 	answer1.setText("	test	");	answer2.setText("	ftfjvbxcb	");	answer3.setText("	zvcxcvzx	");	questionLabel.setText("	Question7	");	break;
		case 	8	: 	answer1.setText("	test	");	answer2.setText("	ftfjvbxcb	");	answer3.setText("	zvcxcvzx	");	questionLabel.setText("	Question8	");	break;
		case 	9	: 	answer1.setText("	test	");	answer2.setText("	ftfjvbxcb	");	answer3.setText("	zvcxcvzx	");	questionLabel.setText("	Question9	");	break;
		case 	10	: 	answer1.setText("	test	");	answer2.setText("	ftfjvbxcb	");	answer3.setText("	zvcxcvzx	");	questionLabel.setText("	Question10	");	break;
		case 	11	: 	answer1.setText("	test	");	answer2.setText("	ftfjvbxcb	");	answer3.setText("	zvcxcvzx	");	questionLabel.setText("	Question11	");	break;
		case 	12	: 	answer1.setText("	test	");	answer2.setText("	ftfjvbxcb	");	answer3.setText("	zvcxcvzx	");	questionLabel.setText("	Question12	");	break;
		case 	13	: 	answer1.setText("	test	");	answer2.setText("	ftfjvbxcb	");	answer3.setText("	zvcxcvzx	");	questionLabel.setText("	Question13	");	break;
		case 	14	: 	answer1.setText("	test	");	answer2.setText("	ftfjvbxcb	");	answer3.setText("	zvcxcvzx	");	questionLabel.setText("	Question14	");	break;
		case 	15	: 	answer1.setText("	test	");	answer2.setText("	ftfjvbxcb	");	answer3.setText("	zvcxcvzx	");	questionLabel.setText("	Question15	");	break;
		case 	16	: 	answer1.setText("	test	");	answer2.setText("	ftfjvbxcb	");	answer3.setText("	zvcxcvzx	");	questionLabel.setText("	Question16	");	break;
		case 	17	: 	answer1.setText("	test	");	answer2.setText("	ftfjvbxcb	");	answer3.setText("	zvcxcvzx	");	questionLabel.setText("	Question17	");	break;
		case 	18	: 	answer1.setText("	test	");	answer2.setText("	ftfjvbxcb	");	answer3.setText("	zvcxcvzx	");	questionLabel.setText("	Question18	");	break;
		case 	19	: 	answer1.setText("	test	");	answer2.setText("	ftfjvbxcb	");	answer3.setText("	zvcxcvzx	");	questionLabel.setText("	Question19	");	break;
		case 	20	: 	answer1.setText("	test	");	answer2.setText("	ftfjvbxcb	");	answer3.setText("	zvcxcvzx	");	questionLabel.setText("	Question20	");	break;
		
		}
	}
	
	/**
	 * The method written by weixiong,
	 * for getting the total questions number 
	 * return the number of the questions
	 */
	public static int getTotalQuestionNumber(){
		int temp = numberOfQuestions;
		return temp;
	}
}

//File: [surveyDetail.java]
//Purpose: [Show the detail information of a survey, including the answer for each question,locaion name and researcher name]
//Features Completed: [Show answers for each question, show researcher name, show location name]
//Features Incomplete: [Can not record researcher name if he was login as an admin account]
//Dependencies: [localDB]
//Known Bugs: [None]
//General Comments: [None]

package com.example.cmpt371project;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;


public class surveyDetail extends Activity {
	private ListView listView;
	private simpleListAdapter thisAdapter;
	private LocalDB surveyDB;
	private TextView resText;
	private TextView locationText;
	private Bundle mBundle; //for get value from intent
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		surveyDB = new LocalDB(this);
		setContentView(R.layout.activity_survey_detail);
		listView = (ListView)findViewById(R.id.survey_detail_list);
		resText = (TextView)findViewById(R.id.survey_detail_restext);
		locationText= (TextView)findViewById(R.id.survey_detail_locationtext);
	
		//get intent 
		mBundle = getIntent().getExtras();
		//get childID
		String locID = (String) mBundle.get("locID");
		String resID = (String) mBundle.get("resID");
		
		resText.setText(surveyDB.getResNameByID(resID));
		locationText.setText(surveyDB.getLocationNameByID(locID));
		thisAdapter = this.initializeListAdapter();
		
		//set listView
		listView.setAdapter(thisAdapter);
		listView.setTextFilterEnabled(true);
		
	}
	
	/**
	 * @author Xingze
	 * @return simpleListAdapter 
	 * initial list adapter
	 */
		private simpleListAdapter  initializeListAdapter(){
			ArrayList<HashMap<String,Object>> listItem = new ArrayList<HashMap<String,Object>>();
			
			//put the answers to an arraylist 
			for(int i=1;i<21;i++){
				HashMap<String,Object> newAnswer = new HashMap<String,Object>();
				newAnswer.put("question","Question"+i);
				newAnswer.put("answer", mBundle.get("q"+String.valueOf(i)));
				listItem.add(newAnswer);
			}
			simpleListAdapter newAdapter = new simpleListAdapter(this, listItem, R.layout.list_for_survey, 
					new String[]{"question","answer"}, new int[]{R.id.list_for_survey_date,R.id.list_for_survey_score});

			return newAdapter;		
		}
}			


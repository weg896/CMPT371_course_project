//File: [surveyForAAchildren]
//Purpose: [show each survey time and score of a child in a listview]
//Features Completed: [Show survey and score of a child in listview ]
//Features Incomplete: [None]
//Dependencies: [LocalDB]
//Known Bugs: [None]
//General Comments: [None]

package com.example.cmpt371project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.HashMap;



public class surveyForAChildren extends Activity {
	private ListView listView;
	private simpleListAdapter thisAdapter;
	private LocalDB surveyDB;
	private Bundle mBundle; //for get value from intent
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		surveyDB = new LocalDB(this);
		setContentView(R.layout.activity_survey_forchild);
		listView = (ListView)findViewById(R.id.survey_child_list);
		
		//initial all component

		
		//get intent 
		mBundle = getIntent().getExtras();
		//get childID
		String childID = (String) mBundle.get("childID");
		
		thisAdapter = this.initializeListAdapter(childID);
		
		//set listView
		listView.setAdapter(thisAdapter);
		listView.setTextFilterEnabled(true);
		
		
		//set on click
		this.setListListener();
		
	}
	
	/**
	 * @author Xingze
	 * @return simpleListAdapter 
	 * initial list adapter
	 */
		private simpleListAdapter  initializeListAdapter(String childID){
			ArrayList<HashMap<String,Object>> listItem = new ArrayList<HashMap<String,Object>>();
			listItem = surveyDB.getSurveyForAChild(childID);
			simpleListAdapter newAdapter = new simpleListAdapter(this, listItem, R.layout.list_for_survey, 
					new String[]{"endDate","score"}, new int[]{R.id.list_for_survey_date,R.id.list_for_survey_score});

			return newAdapter;		
		}
		
		/**
		 * @author Xingze
		 * set onclicklistener for list
		 */
		private void setListListener(){
			listView.setOnItemClickListener(new OnItemClickListener() {
				
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int position,
						long id) {
					//read which item be clicked
					int itemId = (int)id;
					HashMap<String,Object> listItem = (HashMap<String, Object>) thisAdapter.getItem(itemId);
								
			       	//go to survey detail activity
			       	Intent viewIntent = new Intent();
			       	viewIntent.setClass(surveyForAChildren.this, surveyDetail.class);
			       			       	       	
			       	viewIntent.putExtra("locID", (String)listItem.get("locID"));
			       	viewIntent.putExtra("resID", (String)listItem.get("resID"));
			       	//get answers for 20 questions
			       	for(int i=1;i<21;i++){
			       		viewIntent.putExtra("q"+String.valueOf(i), (String)listItem.get("q"+String.valueOf(i)));
			       	}
			       	
			       	surveyForAChildren.this.startActivity(viewIntent);
			       	finish();
				}
			});
		}
}			


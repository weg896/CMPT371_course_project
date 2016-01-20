//File: researcher.java
//Purpose: Displays all the options when a researcher logs in
//Features Completed: Children, Researcher, Log out button are functional.
//Features Incomplete: Update button has been connected to update methods unlike Login screen.
//					   Have not removed Stats, Change Language button, they are no longer needed
//Dependencies: Login.java
//Known Bugs: None
//General Comments: None

package com.example.cmpt371project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.cmpt371project.R;

public class researcher extends Activity{
	private Button childrenButton;
	private Button locationButton;
	private Button updateButton;
	private Button statsButton;
	private Button changeLanguageButton;
	private Button logOutButton;
	private Bundle mBundle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_researcher);
		childrenButton=(Button)findViewById(R.id.stRe_child_but);
		locationButton=(Button)findViewById(R.id.stRe_loca_but);
		updateButton=(Button)findViewById(R.id.stRe_update_but);
		statsButton=(Button)findViewById(R.id.stRe_stats_but);
		changeLanguageButton=(Button)findViewById(R.id.stRe_chLa_but);
		logOutButton=(Button)findViewById(R.id.stRe_logO_but);

		//set onlick listener for children button
		childrenButton.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent addIntent = new Intent();
				
				//pass user id to next screen
				addIntent.putExtra("UserID", getIntent().getStringExtra("userName"));
				addIntent.putExtra("isResearcher", "true");
				
				addIntent.setClass(researcher.this, childrenList.class);
				researcher.this.startActivity(addIntent);

			}});

		//set onlick listener for location button
		locationButton.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent addIntent = new Intent();
				addIntent.setClass(researcher.this, locationList.class);
				researcher.this.startActivity(addIntent);

			}});

		//set onlick listener for log out button
		logOutButton.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent addIntent = new Intent();
				addIntent.setClass(researcher.this, Login.class);
				researcher.this.startActivity(addIntent);
				finish(); 
			}});
	}


}

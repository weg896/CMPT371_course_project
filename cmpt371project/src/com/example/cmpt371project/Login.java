//File: Login.java
//Purpose: Allows users to update the app's internal database or allows admin or researchers to log in
//Features Completed: Log in works with administrator and researcher privilege, update partially working
//Features Incomplete: No NutriSTEP log, as requested by our client
//Dependencies: None
//Known Bugs: Update button sometimes work other times crashes the program. 
//General Comments: None

package com.example.cmpt371project;

import com.example.cmpt371project.R;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity {

	private EditText userName;
	private EditText password;
	private Button logInButton;
	private Button upDateButton;
	private LocalDB testDB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		userName = (EditText) findViewById(R.id.userNameInput);
		password = (EditText) findViewById(R.id.passWordInput);
		logInButton= (Button) findViewById(R.id.login);
		upDateButton= (Button) findViewById(R.id.update);

		//Initializes the internal local database
		testDB = new LocalDB(this);

		logInButton.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				String inputId = userName.getText().toString();
				String inputPassword = password.getText().toString();
				String password = testDB.readPassword(inputId);
				
				/*
				 * Cannot Refactor code below
				 * If a new method is created 
				 * private void startScreen(Login login, Class<admin> class1)
				 * Only startScreen(Login.this, admin.class); can call this method
				 * startScreen(Login.this, researcher.class); will not be able to call this method
				 */
				if(password.compareTo(inputPassword)==0 && !inputPassword.equals("")){
					String privilege = testDB.getPrivilege(inputId);
					
					// If the user is enters the valid credentials as an administrator they will go the admin screen and passes in the username
					if(privilege.compareTo("administrator")==0){
						Intent addIntent = new Intent();
						addIntent.putExtra("userName", inputId);
						addIntent.setClass(Login.this, admin.class);
						Login.this.startActivity(addIntent);
						finish();
					}
					// If the user is enters the valid credentials as an administrator they will go the researcher screen and passes in the username
					else if(privilege.compareTo("researcher")==0){
						Intent addIntent = new Intent();
						addIntent.putExtra("userName", inputId);
						addIntent.setClass(Login.this, researcher.class);
						Login.this.startActivity(addIntent);
						finish(); 
					}
					// If the user is enters the valid credentials as an administrator they will go the owner screen and passes in the username
					// TODO Implement general owner screen. We did not have time to implement 
					else if(privilege.compareTo("owner")==0){

						finish(); 
						
					}
					//Default case if login credentials is user
					// TODO Implement general user screen. We did not have time to implement 
					else{

					}

				}
				else{
					// If password or username entered is invalid, a short message will alert the user will pop up
					// Message is shown using Android built in class "Toast"
					Context context = getApplicationContext();
					CharSequence text = "Invalid username or password";
					int duration = Toast.LENGTH_SHORT;

					Toast toast = Toast.makeText(context, text, duration);
					toast.show();
				}

			}

		});

		
		/*
		 * Update button will sync the internal local database (SQLite) with the remote database (PostgresSQL)
		 * This is done using PHP scripts on a webservice, where the script will run through an algorithm and determine
		 * what transaction will be performed and will send back a success value and the android device will handle it accordingly
		 */
		upDateButton.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			public void onClick(View v) {
				if(haveNetworkConnection()){
					Log.d("Network Connection","Has connection");
					testDB.syncChildrenTable();
					testDB.syncUserTable();
					testDB.syncInstitutionsTable();
				}
				else {
					Log.d("Network Connection","NO connection");
					AlertDialog alertDialog = new AlertDialog.Builder(
							Login.this).create();

					// Setting Dialog Title
					alertDialog.setTitle("No network connection detected");

					// Setting Dialog Message
					alertDialog.setMessage("Please connect to a network connection before updating");


					// Setting OK Button
					alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

						}
					});

					// Showing Alert Message
					alertDialog.show();	 
				}

			}

		});
	}
	
	/**
	 * Checks if the device has a network connection whether it be Wifi or Mobile
	 * @return true there is an network connection, false otherwise
	 */
	private boolean haveNetworkConnection() {
		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			if (ni.getTypeName().equalsIgnoreCase("WIFI")){
				if (ni.isConnected()){
					haveConnectedWifi = true;
				}
			}
			if (ni.getTypeName().equalsIgnoreCase("MOBILE")){
				if (ni.isConnected()){
					haveConnectedMobile = true;
				}
			}
		}
		return haveConnectedWifi || haveConnectedMobile;
	}

	@Override
	/**
	 * Inflate the menu; this adds items to the action bar if it is present.
	 */	
	public boolean onCreateOptionsMenu(Menu menu) {	
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

}

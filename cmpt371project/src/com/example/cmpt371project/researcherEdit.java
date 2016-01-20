//File: researcherEdit.java
//Purpose: Allow admin to add new user or edit already exist user info
//Features Completed: Able to create a user with researcher privilege and able to edit already existing user info
//Features Incomplete: Only allow deletion when connected directly to the internet
//Dependencies: childrenList.java
//Known Bugs: None
//General Comments: None

package com.example.cmpt371project;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cmpt371project.R;

public class researcherEdit extends Activity{

	private Button save;
	private Button edit;
	private EditText firstName;
	private EditText lastName;
	private EditText phone;
	private EditText userName;
	private EditText passWord;
	private Button remove;
	private LocalDB resDB;
	private String inFirstName;
	private String inLastName;
	private String inPhone;
	private String inUserName;
	private String inPassword;
	private EditText textViewList[];
	private Bundle mBundle; //for get value from intent
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		resDB = new LocalDB(this);
		setContentView(R.layout.activity_researchers_edit);
		
		//initial all component
		initialComponent();
		
		//get intent 
		//if intent = res_add, open add function
		//if intent = res_view,open view function
		mBundle = getIntent().getExtras();
		String neededFunction = (String) mBundle.get("from");
			
		//get intent, if value is res_add
		//add function
		if(neededFunction.compareTo("res_add")==0){
			//do not show remove in add function
			remove.setVisibility(View.INVISIBLE);
			edit.setVisibility(View.INVISIBLE);
			
			//save button for add function	
			this.setSaveListenerForAdd();
		} else if(neededFunction.compareTo("res_view")==0){		
			//get intent, if value is res_view
			//edit function		
			
			//set textview with the value get from intent
			this.setTextFromIntent();

					
			//all data can not be changed
			save.setVisibility(View.INVISIBLE);
			remove.setVisibility(View.INVISIBLE);
			setUnEditable();
			
			
			//click edit, set all fields editable, and set save button visible
			this.setEditListener();
			
			//save button
			this.setSaveListenerForEdit();
			
			//remove button
			this.setRemoveListener();

		}
		else {
			//if the value is neither res_view or res_add, output bug log
			Log.e("error","Intent from researcherList is neither res_view or res_add");
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent backIntent = new Intent();
   	 	backIntent.setClass(researcherEdit.this, researcherList.class);
   	 	researcherEdit.this.startActivity(backIntent);
	}

	/**
	 * @author Xingze
	 * initial all components
	 */
	private void initialComponent(){
		textViewList = new EditText[5]; //0-firstName,1-lastName,2-phone,3-userName,4-passWord
		save = (Button)findViewById(R.id.edRe_save_but);
		edit = (Button)findViewById(R.id.edRe_Edit_but);
		remove = (Button)findViewById(R.id.edRe_Remo_but);
		firstName = (EditText)findViewById(R.id.edRe_firstname_txt);
		textViewList[0] = firstName;
		lastName = (EditText)findViewById(R.id.edRe_lastname_txt);
		textViewList[1] = lastName;
		phone = (EditText)findViewById(R.id.edRe_phon_txt);
		textViewList[2] = phone;
		userName = (EditText)findViewById(R.id.edRe_userName_txt);
		textViewList[3] = userName;
		passWord = (EditText)findViewById(R.id.edRe_password_txt);
		textViewList[4] = passWord;
		
	}
	
	/**
	 * @author Xingze
	 * read user input
	 */
	private void readUserInput(){
		inFirstName = firstName.getText().toString();
		inLastName = lastName.getText().toString();
		inPhone = phone.getText().toString();
		inUserName = userName.getText().toString();
		inPassword = passWord.getText().toString();
	}
	/**
	 * @author Xingze
	 * Check if user have inputed all the fields excepts phone number 
	 * @return true if user input all the fields excepts phone number , false otherwise 
	 */
	private boolean checkUserInput(){
		if(	inFirstName.compareTo("")==0
				||inLastName.compareTo("")==0 
				||inUserName.compareTo("")==0
				||inPassword.compareTo("")==0)
			return false;
		else 
			return true;
	}
	
	/**
	 * @author Xingze
	 * make a toast show input error
	 */
	private void reportInputError(String type){
		Context context = getApplicationContext();
		CharSequence text="";
		if(type.compareTo("blank")==0){
			text = getResources().getString(R.string.inputError);
			}
		else if (type.compareTo("phone")==0){
			text = getResources().getString(R.string.phone_not_valid);
			}else {
				//if the value is neither res_view or res_add, output bug log
				Log.e("error","reportInputError get neither blank or phone");
			}
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}
	
	/**
	 * @author Xingze
	 * clear all the text fields
	 */
	private void clearAllField(){
		for(EditText i: textViewList){
			i.setText("");
		}
	}
	
	/**
	 * @author Xingze
	 * set all text fields uneditable
	 */
	private void setUnEditable(){
		for(EditText i: textViewList){
			i.setFocusable(false);
			i.setFocusableInTouchMode(false);
		}
	}
	
	/**
	 * @author Xingze
	 * set all text fields editable
	 */
	private void setEditable(){
		for(EditText i: textViewList){
			i.setFocusable(true);
			i.setFocusableInTouchMode(true);
			i.requestFocus();
		}
	}
	
	/**
	 * @author Xingze
	 * set onclicklistener for save button for add function
	 */
	private void setSaveListenerForAdd(){
		save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				readUserInput();
				
				//if user do not input all the fields
				if(!checkUserInput()){
					//report warring when user forget input anything
					reportInputError("blank");
					//check if the phone number is valid
				} else if(!checkPhoneNumber(inPhone)){
					reportInputError("phone");
				}
				else{
					//if user input all the fields
					resDB.addNewUser(inUserName, inPassword, inFirstName, inLastName, inPhone, "researcher");
					clearAllField();
				}				
			}
		});
	}
	/**
	 * @author Xingze
	 * Set onclicker listener for save button for edit function
	 */
	private void setSaveListenerForEdit(){
		save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				readUserInput();
				
				if(!checkUserInput()){
					reportInputError("blank");
				}else if(!checkPhoneNumber(inPhone)){
					reportInputError("phone");
					}
					else{
					resDB.updateUsers(inUserName, inPassword, inFirstName, inLastName, inPhone);
					
					//change button status
					save.setVisibility(View.INVISIBLE);
					edit.setVisibility(View.VISIBLE);
					
					
					//all data can not be changed
					setUnEditable();
				}
				
			}
		});
	}
	
	/**
	 * @author Xingze
	 * set onclicklistener for remove button
	 */
	private void setRemoveListener(){
		remove.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String deleteUser = userName.getText().toString();
				resDB.deleteUsers(new String[]{deleteUser});
				
				Intent backIntent = new Intent();
		   	 	backIntent.setClass(researcherEdit.this, researcherList.class);
		   	 	researcherEdit.this.startActivity(backIntent);
			}
		});
	}
	
	/**
	 * @author Xingze
	 * set onclicklistener for edit button
	 */
	private void setEditListener(){
		edit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//set fields editable
				setEditable();
				
				save.setVisibility(View.VISIBLE);
				remove.setVisibility(View.VISIBLE);
				edit.setVisibility(View.INVISIBLE);
			}
		});
	}
	
	/**
	 * @author Xingze
	 * set textview with the value get from intent
	 */
	private void setTextFromIntent(){
		firstName.setText((String) mBundle.get("userFirstName"));
		lastName.setText((String) mBundle.get("userLastName"));
		phone.setText((String) mBundle.get("userPhoneNum"));
		userName.setText((String) mBundle.get("userID"));
		passWord.setText((String) mBundle.get("userFirstName"));
	}
	
	
	/**
	 * @author Xingze
	 * @param number input phone number
	 * @return true if number is 10 digits, false otherwise
	 */
	private boolean checkPhoneNumber(String number){
//		CharSequence tempNumber = String.valueOf(number);
		//form of phone number: 10 or 11(include country code) digits
		//if user do not input phone number, return true
		if(number.compareTo("")==0)
			return true;
		else{
			String expression = "^\\d{10}$";
			Pattern pattern = Pattern.compile(expression);
			Matcher matcher = pattern.matcher(number);
			return matcher.matches();
		}
	}
}

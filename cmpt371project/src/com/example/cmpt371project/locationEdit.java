//File: locationEdit.java
//Purpose: GUI to allow users to add new institutions or edit already existing ones
//Features Completed: Able to create new institutions or edit already existing ones
//Features Incomplete: Only allow deletion when connected directly to internet.
//Dependencies: LocalDB.java
//Known Bugs: when remove a location it will affect the children who are in this location 
//and then the location textbox of those children on children screen will be empty


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

public class locationEdit extends Activity{

	private Button save;
	private Button edit;
	private EditText name;
	private EditText phone;
	private EditText address;
	private EditText description;
	private Button remove;
	private Button viewChildren;
	private LocalDB locationDB;
	private String id;
	private String inName;
	private String inPhone;
	private String inAddress;
	private String inDescription;
	private EditText textViewList[];
	private Bundle mBundle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		locationDB = new LocalDB(this);
		setContentView(R.layout.activity_location_edit);
		
		//initial all component
		initialComponent();
		
		//get intent 
		//if intent = res_add, open add function
		//if intent = res_edit,open edit function
		mBundle = getIntent().getExtras();
		String neededFunction = (String) mBundle.get("from");
			
		//add function
		if(neededFunction.compareTo("location_add")==0){
			//do not show remove in add function
			remove.setVisibility(View.INVISIBLE);
			edit.setVisibility(View.INVISIBLE);
			viewChildren.setVisibility(View.INVISIBLE);
			
			//save button for add function
			this.setSaveListenerForAdd();

		} else if(neededFunction.compareTo("location_view")==0){
			//edit function
			setTextFromIntent();

			id = (String) mBundle.get("itemID");
			
//	       	viewIntent.putExtra("itemID", itemID);
			
			//all data can not be changed
			save.setVisibility(View.INVISIBLE);
			remove.setVisibility(View.INVISIBLE);
			setUnEditable();
			
			
			//click edit, set all fields editable, and set save button visible
			this.setEditListenerForView();
			
			//save button
			this.setSaveListenerForView();
			
			//remove button
			this.setRemoveListenerForVie();

			
		//viewChildren button
			this.setViewChildrenListenerForView();
		}
		else{
			//if the value is neither res_view or res_add, output bug log
			Log.e("error","Intent from researcherList is neither res_view or res_add");
		}
		
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Intent backIntent = new Intent();
   	 	backIntent.setClass(locationEdit.this, locationList.class);
   	 	locationEdit.this.startActivity(backIntent);
	}

	/**
	 * @author Xingze
	 * initial all components
	 */
	private void initialComponent(){
		textViewList = new EditText[4]; //0-name,1-phone,2-address,3-description
		save = (Button)findViewById(R.id.edLo_save_but);
		edit = (Button)findViewById(R.id.edLo_edit_but);
		viewChildren = (Button)findViewById(R.id.edLo_ViewChildren_but);
		remove = (Button)findViewById(R.id.edLo_Remo_but);
		name = (EditText)findViewById(R.id.edLo_loca_txt);
		textViewList[0] = name;
		phone = (EditText)findViewById(R.id.edLo_phon_txt);
		textViewList[1] = phone;
		address = (EditText)findViewById(R.id.edLo_addr_txt);
		textViewList[2] = address;
		description = (EditText)findViewById(R.id.edLo_descript_txt);
		textViewList[3] = description;
		
	}
	
	/**
	 * @author Xingze
	 * read user input
	 */
	private void readUserInput(){
		inName = name.getText().toString();
		inPhone = phone.getText().toString();
		inAddress = address.getText().toString();
		inDescription = description.getText().toString();
	}
	/**
	 * @author Xingze
	 * Check if user have inputed all the fields
	 * @return true if user input all the fields, false otherwise
	 */
	private boolean checkUserInput(){
		if(	inName.compareTo("")==0
				||inAddress.compareTo("")==0
				||inDescription.compareTo("")==0)
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
	 * set text fields value from intent
	 */
	private void setTextFromIntent(){
		name.setText((String) mBundle.get("name"));
		address.setText((String) mBundle.get("address"));
		phone.setText((String) mBundle.get("phone"));
		description.setText((String) mBundle.get("des"));
	}
	
	/**
	 * @author Xingze
	 * set on click listener for add function for save button
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
					locationDB.addNewInstitution(inName, inAddress, inPhone, inDescription);
					clearAllField();
				}
			}
		});	
	}
	
	/**
	 * @author Xingze
	 * set on click listener for view function for edit button
	 */
	private void setEditListenerForView(){
		edit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//set fields editable
				setEditable();
				
				save.setVisibility(View.VISIBLE);
				remove.setVisibility(View.VISIBLE);
				edit.setVisibility(View.INVISIBLE);
				viewChildren.setVisibility(View.INVISIBLE);
			}
		});
	}
	
	/**
	 * @author Xingze
	 * set on click listener for view function for save button
	 */
	private void setSaveListenerForView(){
		save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				readUserInput();
				
				//if user do not input all the fields
				if(!checkUserInput()){
					//report warring when user forget input anything
					reportInputError("blank");
				}else if(!checkPhoneNumber(inPhone)){
					//check if the phone number is valid
					reportInputError("phone");}
				
				else{
					locationDB.updateLocation(id, inName, inAddress, inPhone, inDescription);
					//change button status
					save.setVisibility(View.INVISIBLE);
					edit.setVisibility(View.VISIBLE);
					remove.setVisibility(View.INVISIBLE);
					viewChildren.setVisibility(View.VISIBLE);
					
					//all data can not be changed
					setUnEditable();
				}			
			}
		});
	}
	
	/**
	 * @author Xingze
	 * set on click listener for view function for remove button
	 */
	private void setRemoveListenerForVie(){
		remove.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				String deleteUser = userName.getText().toString();
				locationDB.deleteLocation(new String[]{id});
				
				Intent backIntent = new Intent();
		   	 	backIntent.setClass(locationEdit.this, locationList.class);
		   	 	locationEdit.this.startActivity(backIntent);
			}
		});
	}
	
	/**
	 * @author Xingze
	 * set on click listener for view function for viewChildren button
	 */
	private void setViewChildrenListenerForView(){
		viewChildren.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				Intent childrenIntent = new Intent();
				childrenIntent.putExtra("itemID", id);
				childrenIntent.setClass(locationEdit.this, childrenList.class);
		   	 	locationEdit.this.startActivity(childrenIntent);
			}
		});
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

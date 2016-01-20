//File: childrenEdit.java
//Purpose: Allow users to add new child or edit already exist child info
//Features Completed: Able to create a child and able to edit already existing child info
//Features Incomplete: Making child's first and last name not editable after it is saved
//		Only allow deletion when connected directly to the internet
//Dependencies: childrenList.java
//Known Bugs: “view status” button will crash the app
//General Comments: None

package com.example.cmpt371project;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.cmpt371project.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View.OnClickListener;

public class childrenEdit extends Activity{

	private Button save;
	private Button edit;
	private Button remove;
	private Button survey;
	private Button stat;
	
	private String id;
	private EditText firstName;
	private EditText lastName;
	private EditText phone;
	private EditText address;
	private EditText birthday;
	private EditText gender;
	private EditText postal;
	private EditText location; //TODO Change to select popup
	
	private String inFirstName;
	private String inLastName;
	private String inPhone;
	private String inAddress;
	private String inBirthday;
	private String inGender;
	private String inPostal;
	private String inLocationID;
	
	private Bundle mBundle; //for get value from intent
	private EditText textViewList[];
	private LocalDB childDB;
	private CharSequence[] locationName;
	private CharSequence[] locationID;
	private boolean isResearcher;
	
    /**
     * The dialog currently shown to the user. A reference is kept to this 
     * instance so it can easily be cancelled and referenced after it is created.
     */
    private Dialog currentlyShownDialog;
	@Override
	/**
	 * When save button is saved the text entries are inserted into the internal database
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_children_edit);
		childDB = new LocalDB(this);
		
		//initial all component
		initialComponent();
		
		//get intent 
		//if intent = children_add, open add function
		//if intent = children_view,open edit function
		mBundle = getIntent().getExtras();
		String neededFunction = (String) mBundle.get("from");
		
		//set is researcher
		mBundle = getIntent().getExtras();
		if(mBundle!=null){
		String isRes = (String) mBundle.get("isResearcher");
		
		if(isRes!=null){
			if(isRes.compareTo("true")==0)
				isResearcher = true;
			else
				isResearcher = false;
		}
		}
		
		//get intent, if value is res_add
		//add function
		if(neededFunction.compareTo("children_add")==0){
			//do not show remove,edit,survey,stat button in add function
			remove.setVisibility(View.INVISIBLE);
			edit.setVisibility(View.INVISIBLE);
			survey.setVisibility(View.INVISIBLE);
			stat.setVisibility(View.INVISIBLE);
			
			
			//when user click birthday textfield, open a date select popup
			setBirthdayListener();
			
			//when user click location textfield, open a location select popup
			setLocationListener();
			
			//when user click gender textfield, open a gender select popup
			this.setGenderListener();
			//save button for add function	
			this.setSaveListenerForAdd();
		} else if(neededFunction.compareTo("children_view")==0){
			//get intent, if value is res_view
			//edit function		
			
			//set textview with the value get from intent
			this.setTextFromIntent();
	
			//save and remove button are invisible
			save.setVisibility(View.INVISIBLE);
			remove.setVisibility(View.INVISIBLE);
			
			//survey and stat button are visible
			survey.setVisibility(View.VISIBLE);
			stat.setVisibility(View.VISIBLE);
			
			//all data can not be changed
			setUnEditable();
			
			
			//click edit, set all fields editable, and set save button visible
			this.setEditListener();
			
			//save button
			this.setSaveListenerForEdit();
			
			//if researcher is using this gui, the child can not be removed,but researcher can sue survey function
			//if admin is using this gui, the survey function is unclickable.
			if(!isResearcher){
				//remove button
				this.setRemoveListener();
			}

				//survey Button
				this.setSurveyListener();
				remove.setVisibility(View.INVISIBLE);

			

			
			//stat button
			this.setStatListener();

		}
		else {
			//if the value is neither res_view or res_add, output bug log
			Log.e("error","Intent from researcherList is neither res_view or res_add");
		}
		
	}
	@Override
	public void onBackPressed(){
		finish();
		Intent addIntent = new Intent();
		addIntent.setClass(childrenEdit.this, childrenList.class);
		childrenEdit.this.startActivity(addIntent);
	}

	/**
	 * @author Xingze
	 * initial all components
	 */
	private void initialComponent(){
		textViewList = new EditText[8]; //0-firstName,1-lastName,2-phone,3-address,4-birthday,5-gender,6-postal
		save = (Button) findViewById(R.id.edCh_save_but);
		edit = (Button)findViewById(R.id.edCh_edit_but);
		remove = (Button)findViewById(R.id.edCh_Remo_but);
		stat = (Button)findViewById(R.id.edCh_Stats_but);
		survey= (Button)findViewById(R.id.edCh_Survey_but);
		
		firstName = (EditText)findViewById(R.id.edCh_firs_txt);
		textViewList[0] = firstName;
		lastName = (EditText)findViewById(R.id.edCh_last_txt);
		textViewList[1] = lastName;
		phone = (EditText)findViewById(R.id.edCh_phon_txt);
		textViewList[2] = phone;
		address = (EditText)findViewById(R.id.edCh_addr_txt);
		textViewList[3] = address;

		postal = (EditText)findViewById(R.id.edCh_post_txt);
		textViewList[4] = postal;
		location = (EditText)findViewById(R.id.edCh_location_txt);
		textViewList[5] = location;


		birthday = (EditText)findViewById(R.id.edCh_birth_txt);
		// birthday.setEnabled(false);
		textViewList[6] = birthday;
		gender = (EditText)findViewById(R.id.edCh_gender_txt);
		// gender.setEnabled(false);
		textViewList[7] = gender;
	}
	
	/**
	 * @author Xingze
	 * read user input
	 */
	private void readUserInput(){
		
		inFirstName = firstName.getText().toString();
		inLastName = lastName.getText().toString();
		inPhone = phone.getText().toString();
		inAddress = address.getText().toString();
		inBirthday = birthday.getText().toString();
		inGender = gender.getText().toString();
		inPostal = postal.getText().toString();
//		inLocation = location.getText().toString();
	}
	
	/**
	 * @author Xingze
	 * Check if user have inputed all the fields excepts phone number 
	 * @return true if user input all the fields excepts phone number , false otherwise 
	 */
	private boolean checkUserInput(){
		if(	inFirstName.compareTo("")==0
				||inLastName.compareTo("")==0
				||inBirthday.compareTo("")==0
				||inGender.compareTo("")==0)


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
			}else if (type.compareTo("postal")==0){
				text = getResources().getString(R.string.postal_not_valid);
				}else{
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
			i.setEnabled(false);
			i.setTextColor(Color.WHITE);
		}
	}
	
	/**
	 * @author Xingze
	 * set all text fields editable
	 */
	private void setEditable(){
		for(EditText i: textViewList){
			i.setEnabled(true);
		}
	}
	
	/**
	 * @author Xingze
	 * set textview with the value get from intent
	 */
	private void setTextFromIntent(){
		id = (String) mBundle.get("childID");
		firstName.setText((String) mBundle.get("childFName"));
		lastName.setText((String) mBundle.get("childLName"));
		phone.setText((String) mBundle.get("childPhone"));
		address.setText((String) mBundle.get("childAddress"));
		birthday.setText((String) mBundle.get("childBirth"));
		gender.setText((String) mBundle.get("childGender"));
		postal.setText((String) mBundle.get("childPostal"));
		location.setText((String) mBundle.get("childLocationName"));
		
		//set inLocationID 
		inLocationID = (String) mBundle.get("childLocation");
		
//		viewIntent.putExtra("childID", (String) listItem.get("childID"));

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
				} else if(!checkPostal(inPostal)){
					reportInputError("postal");
				}
				else{
					//if user input all the fields
					childDB.addNewChild(inFirstName, inLastName, inGender, inBirthday, inAddress, inPostal, inPhone, inLocationID);
					clearAllField();

				}				
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
				
				stat.setVisibility(View.INVISIBLE);
				survey.setVisibility(View.INVISIBLE);
				//when user click birthday textfield, open a date select popup
				setBirthdayListener();
				
				//when user click location textfield, open a location select popup
				setLocationListener();
				
				//when user click gender textfield, open a gender select popup
				setGenderListener();
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
				birthday.clearFocus();
				gender.clearFocus();
				if(!checkUserInput()){
					reportInputError("blank");
				}else if(!checkPhoneNumber(inPhone)){
					reportInputError("phone");
					}
					else{
//					childDB.updateUsers(inUserName, inPassword, inFirstName, inLastName, inPhone);
					childDB.updateChildren(id, inFirstName, inLastName, inGender, inBirthday, inAddress, inPostal, inPhone, inLocationID);
					//change button status

					save.setVisibility(View.INVISIBLE);
					edit.setVisibility(View.VISIBLE);
					
					stat.setVisibility(View.VISIBLE);
					survey.setVisibility(View.VISIBLE);
							
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
//				String deleteUser = userName.getText().toString();
				childDB.deleteChildren(new String[]{id});
				
				Intent backIntent = new Intent();
		   	 	backIntent.setClass(childrenEdit.this, childrenList.class);
		   	 	childrenEdit.this.startActivity(backIntent);
			}
		});
	}
	
	private void setSurveyListener(){
		survey.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				String deleteUser = userName.getText().toString();
				
				Intent survIntent = new Intent();
				survIntent.putExtra("childID", id);
				survIntent.putExtra("LocationID",inLocationID);
				survIntent.putExtra("UserID", (getIntent().getStringExtra("UserID")));
				
				
				survIntent.setClass(childrenEdit.this, survey.class);
		   	 	childrenEdit.this.startActivity(survIntent);
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
//		if(number.compareTo("")==0)
//			return true;
//		else{
			String expression = "^\\d{10}$";
			Pattern pattern = Pattern.compile(expression);
			Matcher matcher = pattern.matcher(number);
			return matcher.matches();
//		}
	}
	
	/**
	 * @author Xingze
	 * @param Postal input Postal
	 * @return true if Postal is valid, false otherwise
	 */
	private boolean checkPostal(String Postal){

			String expression = "^[A-Za-z]\\d[A-Za-z]\\s?\\d[A-Za-z]\\d$";
			Pattern pattern = Pattern.compile(expression);
			Matcher matcher = pattern.matcher(Postal);
			return matcher.matches();
//		}
	}
	
	/**
	 * @author Xingze
	 * show date select popup
	 */
	public void dateShow(){
		final Calendar tempC = Calendar.getInstance();
//		tempC.setTimeInMillis(System.currentTimeMillis());
        int mYear = tempC.get(Calendar.YEAR);
        int mMonth = tempC.get(Calendar.MONTH);
        int mDay = tempC.get(Calendar.DAY_OF_MONTH);
		new myDatePicker(this,new DatePickerDialog.OnDateSetListener(){

			@Override
			public void onDateSet(DatePicker arg0, int y, int m, int d) {
				tempC.set(Calendar.YEAR, y);
				tempC.set(Calendar.MONTH, m);
				tempC.set(Calendar.DAY_OF_MONTH, d);
				
				SimpleDateFormat s= new SimpleDateFormat("yyyy-MM-dd");
				inBirthday = s.format(tempC.getTime());
				birthday.setText(inBirthday.toString());

			}
		}, mYear,mMonth,mDay).show();
	}
	
	
    /**
     * @author Xingze
     * select location popup
     */
    private void selectLocationPopup(){
//    	Dialog locationDialog = null;
    	//read location list 
		ArrayList<HashMap<String,Object>> locationList = new ArrayList<HashMap<String,Object>>();
		locationList = childDB.getAllLocation();
		
//initial the container for locationName and locationID
		locationName = new CharSequence[locationList.size()];
		locationID = new CharSequence[locationList.size()];
		
		//set locationID and locationName to the container
		for(int i=0;i<locationList.size();i++){
			locationName[i]=(CharSequence) locationList.get(i).get("locationName");
			locationID[i]=(CharSequence) locationList.get(i).get("locationID");
		}

    	AlertDialog.Builder builder = new AlertDialog.Builder(this);  

    	builder.setTitle(R.string.select_location);  
    	
    	builder.setSingleChoiceItems(locationName, -1, new DialogInterface.OnClickListener() {  
    	    public void onClick(DialogInterface dialog, int item) {  
//    	    	
    	    	location.setText(locationName[item]);
    	    	inLocationID = (String) locationID[item];
    	        Toast.makeText(getApplicationContext(), locationName[item], Toast.LENGTH_SHORT).show();
    	        cancelDialog();
    	    }  
    	});  
    	
    	
    	AlertDialog alert = builder.create(); 
    	currentlyShownDialog=builder.show();
    	

    }
    
    /**
     * @author Xingze
     * on foucus change listener for birthday edittext field
     */
    private void setBirthdayListener(){
		birthday.setOnTouchListener(new OnTouchListener() {			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					dateShow();
				}
				
				return true;
			}
		}); 
    }
    
    
    /**
     * @author Xingze
     * on foucus change listener for gender edittext field
     */
    private void setGenderListener(){
    	gender.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_UP) {
						genderShow();
					}
					
					return true;
            }
		});

    }
    
    /**
     * @author Xingze
     * on foucus change listener for location edittext field
     */
    private void setLocationListener(){
    	location.setOnTouchListener(new View.OnTouchListener() {			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_UP) {
						selectLocationPopup();
					}
					
					return true;
            }
		});

    }
    
    /**
     * @author Xingze
     * show gender selection popup
     */
    private void genderShow(){
    	
		final CharSequence[] items = {"Male","Female"};
  
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);  

    	builder.setTitle(R.string.select_location);  
  
    	AlertDialog alert = builder.create(); 
    	
    	builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {  
    	    public void onClick(DialogInterface dialog, int item) {  
    	    	gender.setText(items[item]);
    	    	inGender = (String) items[item];
    	    	cancelDialog();
    	    }  	
    	});
    	
    	currentlyShownDialog=builder.show();
    }
 

    
    
    private void cancelDialog(){
    	currentlyShownDialog.dismiss();
    }
    
    /**
     * @author Xingze
     * set onclick listener for stat button
     */
    private void setStatListener(){
    	stat.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent statIntent = new Intent();
				statIntent.putExtra("childID", id);
				statIntent.setClass(childrenEdit.this, surveyForAChildren.class);
		   	 	childrenEdit.this.startActivity(statIntent);
				
			}
		});
    }
}

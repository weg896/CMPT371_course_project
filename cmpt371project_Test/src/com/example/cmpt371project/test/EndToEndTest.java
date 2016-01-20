package com.example.cmpt371project.test;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.EditText;

import com.example.cmpt371project.LocalDB;
import com.example.cmpt371project.Login;
import com.example.cmpt371project.R;
import com.example.cmpt371project.childrenList;
import com.example.cmpt371project.childrenEdit;
import com.example.cmpt371project.locationEdit;
import com.example.cmpt371project.locationList;
import com.example.cmpt371project.researcher;
import com.robotium.solo.Solo;

/**
 * This is a end to end test
 * Right now it just go through all the procedure as a researcher(NO ADMIN FOR NOW)
 * test will go follow sequence below:
 * Researcher login -> Choose English as language -> Update -> location -> add a location
 * -> edit location -> check children list ->  edit a child -> start survey 
 * cancel survey -> restart survey-> finish survey -> save survey
 * -> check this survey result -> logout
 */
public class EndToEndTest extends ActivityInstrumentationTestCase2<Login>{
	// Variables for system.
	private Solo solo;
	private Context context;
	// Variables for Login UI.
	private EditText userID;
	private EditText password;
	private Button logInButton;
	// Variables for researcher UI.
	private Button updateButton;
	private Button childrenButton;
	private Button locationButton;
	private Button changeLanguageButton;
	private Button logOutButton;
	// Variables for location UI.
	private Button locationAddButton;
	private EditText locationName;
	private EditText[] locationTextViewList;
	private EditText locationPhone;
	private EditText locationAddress;
	private EditText locationDescription;
	private Button locationSave;
	private Button locationEdit;
	// Variables for children UI.
	private EditText childrenFirstName;
	private EditText[] childrenTextViewList;
	private EditText childrenLastName;
	private EditText childrenPhone;
	private EditText childrenAddress;
	private EditText childrenBirthday;
	private EditText childrenGender;
	private EditText childrenPostal;
	private EditText childrenLocation;
	private Button childrenSave;
	private Button childrenEdit;
	private Button childrenAddButton;
	
	
	/**
	 * constructor
	 * @param activityClass 
	 */
	public EndToEndTest(Class<Login> activityClass) {
		super(activityClass);
	}
	@Override
	protected void setUp() throws Exception {
		solo = new Solo(getInstrumentation());
		getActivity();
		context = getInstrumentation().getTargetContext().getApplicationContext();
		initAllButtonsAndViews();
		super.setUp();
	}
	/**
	 * main method for End to End test.
	 */
	public void testWithResearcherScenario() {
		//DATA-AREA
		//setup all the data maybe used in testing.
		//after test string matrix built, this block should be replaced.
				String username="res";
				String password="res";
				String language="English";
				//example of location
				//0-Name,1-phoneNumber,2-Address,3-description
				String[] TestLocation1 = {"Paris","3061234567","France","a far away place."};
				String[] TestLocation2 = {"London","3067654321","UK","also a far away place."};
				//example of child
				//0-firstName,1-lastName,2-phone,3-address,4-birthday,5-gender,6-postal,7- location
				String[] TestChild1 = {"John","Smith","3062451234","Address example","1979-01-29","Male","S7H5J7","UofS"};
				String[] TestChild2 = {"Mark","Smith","3062451234","Address example","1979-01-29","Male","S7H5J7","UofS"};

		//METHOD-AREA
		//test will go follow sequence below:
		//Researcher login -> Choose English as language -> Update -> location -> add a location
		//-> edit location -> check children list ->  edit a child -> start survey 
		//cancel survey -> restart survey-> finish survey -> save survey
		// -> check this survey result -> logout

		Login(researcher.class,username,password);
		chooseLanguage(language);
		Update();
		TransitBetweenPage(locationButton,researcher.class,locationList.class);
		addLocation(TestLocation1);
		editLocation(TestLocation2);
		TransitBetweenPage(childrenButton,researcher.class,childrenList.class);
		addAChild(TestChild1);
		editChild(TestChild2);
		startSurvey();
		cancelSurvey();
		startSurvey();
		saveSurvey();
		checkSurveyResult();
		logoutFrom(researcher.class);	
	}
	
	/**
	 * logout from current page to login page.
	 */
	private void logoutFrom(Class pageFrom) {
		solo.assertCurrentActivity("ERR - before click logout button, should at activity "+pageFrom.toString() +", but not.", researcher.class);
		solo.clickOnView(logOutButton);
		solo.assertCurrentActivity("ERR: Should jump to login activity but not.", Login.class);
	}
	/**
	 * check the result of survey.
	 */
	private void checkSurveyResult() {
		// TODO this function in app is not implement yet.		
		
	}
	private void saveSurvey() {
		// TODO this function in app is not implement yet.		
		
	}
	private void cancelSurvey() {
		// TODO this function in app is not implement yet.		
		
	}
	private void startSurvey() {
		// TODO this function in app is not implement yet.		
		
	}
	/**
	 * edit a child already in children list.
	 * @param childInfo 0-firstName,1-lastName,2-phone,3-address,4-birthday,5-gender,6-postal,7- location
	 */
	private void editChild(String[] childInfo) {
		//click on the first item in the list.
		solo.clickOnText(childInfo[0]); 
		solo.assertCurrentActivity("ERR - before edit a child, should at activity "+childrenEdit.class.toString() +", but not.", childrenEdit.class);
		solo.clickOnView(childrenEdit);
		for (int i = 0; i < childInfo.length; i++) {
			solo.enterText(childrenTextViewList[i], childInfo[i]);
		}
		solo.clickOnView(childrenSave);
		solo.assertCurrentActivity("ERR: after edit a child, Should jump back to "+childrenEdit.class.toString() +"activity but not.", childrenList.class);
		// Get database and query for what just inputed.
		SQLiteDatabase db = Utils.getSQLiteDatabaseByContext(context);
		Cursor cursor = db.query(LocalDB.CHILDREN_TABLE, null, LocalDB.CHILD_FName+"='"+childInfo[0]+"'", null, null, null, null);
		// Get the record and check.
		while (cursor.moveToNext()) {
			//set children field number.Can not get it from project code.
			int childrenFieldNum = childInfo.length;
			String[] valuesString = new String[childrenFieldNum];
			for (int i=0;i<childrenFieldNum ;i++ ) {
				valuesString[i] = cursor.getString(i);
			}			
			for (int i=0;i<childrenFieldNum ;i++ ) {
				assertEquals("should be "+childInfo[i]+" but it is "+valuesString[i]+".",childInfo[i], valuesString[i]);
			}
		}
		
	}
	/**
	 * add a child into children list
	 * @param childInfo 0-firstName,1-lastName,2-phone,3-address,4-birthday,5-gender,6-postal,7- location
	 */
	private void addAChild(String[] childInfo) {
		TransitBetweenPage(childrenAddButton, childrenList.class, childrenEdit.class);
		//write location information to textview.
		for (int i = 0; i < childInfo.length; i++) {
			solo.enterText(locationTextViewList[i], childInfo[i]);
		}
		solo.clickOnView(childrenSave);
		solo.assertCurrentActivity("ERR: after add a child, Should jump back to "+childrenEdit.class.toString() +" activity but not.", childrenList.class);
		// Get database and query for what just inputed.
		SQLiteDatabase db = Utils.getSQLiteDatabaseByContext(context);
		Cursor cursor = db.query(LocalDB.CHILDREN_TABLE, null, LocalDB.CHILD_FName+"='"+childInfo[0]+"'", null, null, null, null);
		// Get the record and check.
		while (cursor.moveToNext()) {
			//set children field number.Can not get it from project code.
			int childrenFieldNum = childInfo.length;
			String[] valuesString = new String[childrenFieldNum];
			for (int i=0;i<childrenFieldNum ;i++ ) {
				valuesString[i] = cursor.getString(i);
			}			
			for (int i=0;i<childrenFieldNum ;i++ ) {
				assertEquals("should be "+childInfo[i]+" but it is "+valuesString[i]+".",childInfo[i], valuesString[i]);
			}
		}
		
	}
	/**
	 * go into location list, and edit the location.
	 * @param locationInfo 0-Name,1-phoneNumber,2-Address,3-description
	 */
	private void editLocation(String[] locationInfo) {
		//click on the first item in the list. 
		solo.clickOnText(locationInfo[0]); 
		solo.assertCurrentActivity("ERR - before edit a location, should at activity "+locationEdit.class.toString() +", but not.", locationEdit.class);
		solo.clickOnView(locationEdit);
		for (int i = 0; i < locationInfo.length; i++) {
			solo.enterText(locationTextViewList[i], locationInfo[i]);
		}
		solo.clickOnView(locationSave);
		solo.assertCurrentActivity("ERR: after edit a location, Should jump back to "+locationList.class.toString() +" activity but not.", locationList.class);
		// Get database and query for what just inputed.
		SQLiteDatabase db = Utils.getSQLiteDatabaseByContext(context);
		Cursor cursor = db.query(LocalDB.INSTI_TABLE, null, LocalDB.INSTI_name+"='"+locationInfo[0]+"'", null, null, null, null);
		// Get the record and check.
		while (cursor.moveToNext()) {
			int fieldNum = locationInfo.length;
			String[] valuesString = new String[fieldNum];
			for (int i=0;i<fieldNum ;i++ ) {
				valuesString[i] = cursor.getString(i);
			}			
			for (int i=0;i<fieldNum ;i++ ) {
				assertEquals("should be "+locationInfo[i]+" but it is "+valuesString[i]+".",locationInfo[i], valuesString[i]);
			}
		}
		
	}
	/**
	 * add a location to location list
	 * @param locationInfo 0-Name,1-phoneNumber,2-Address,3-description
	 */
	private void addLocation(String[] locationInfo) {
		//transit to edit page.
		TransitBetweenPage(locationAddButton, locationList.class, locationEdit.class);
		//write location information to textview.
		for (int i = 0; i < locationInfo.length; i++) {
			solo.enterText(locationTextViewList[i], locationInfo[i]);
		}
		//save information.
		solo.clickOnView(locationSave);
		solo.assertCurrentActivity("ERR: after save a location, Should jump back to "+locationList.class.toString() +" activity but not.", locationList.class);
		// Get database and query for what just inputed.
		SQLiteDatabase db = Utils.getSQLiteDatabaseByContext(context);
		Cursor cursor = db.query(LocalDB.INSTI_TABLE, null, LocalDB.INSTI_name+"='"+locationInfo[0]+"'", null, null, null, null);
		// Get the record and check.
		while (cursor.moveToNext()) {
			int fieldNum = locationInfo.length;
			String[] valuesString = new String[fieldNum];
			for (int i=0;i<fieldNum ;i++ ) {
				valuesString[i] = cursor.getString(i);
			}			
			for (int i=0;i<fieldNum ;i++ ) {
				assertEquals("should be "+locationInfo[i]+" but it is "+valuesString[i]+".",locationInfo[i], valuesString[i]);
			}
		}

	}
	/**
	 * update local database to online database.
	 */
	private void Update() {
		//nothing should changed, so here have nothing to assert.
		solo.clickOnView(updateButton);
	}
	/**
	 * choose language.
	 *  @param Language 
	 */
	private void chooseLanguage(String Language) {
		// TODO this function in App is not implement yet.
		
	}
	/**
	 * login as a researcher.
	 * @param username 
	 * @param password
	 */
	private void Login(Class accountType, String usernameInput, String passwordInput) {
		//input correct researcher userName and password
		Utils.clearEditViews(solo, new EditText[]{userID,password});
		Utils.enterTextToEditView(solo, 
				new String[]{usernameInput,passwordInput},
				new EditText[]{password,userID});
		solo.clickOnView(logInButton);
		solo.waitForActivity(accountType);
		solo.assertCurrentActivity("ERR: fail on login as "+accountType.toString(), accountType);

		
	}
	/**
	 * test follow things: click a button, jump from one page to another.
	 * @param theButton the button be click.
	 * @param pageFrom the page user should stay before button be clicked.
	 * @param pageTo the page user should reach after button be clicked.
	 */
	private void TransitBetweenPage(Button theButton,Class pageFrom,Class pageTo){
		solo.assertCurrentActivity("ERR - when Click button "+theButton.toString()+", should transit to activity "+pageFrom.toString() +", but failed.", pageFrom);
		solo.clickOnView(theButton);
		solo.waitForActivity(pageTo);
		solo.assertCurrentActivity("ERR - when click back button, Could not jump to "+pageTo.toString()+"from "+pageFrom.toString() +".", pageTo);
		solo.hideSoftKeyboard();
	}
	/**
	 * initialize all the buttons and views.
	 */
	private void initAllButtonsAndViews() {
		//initialize button and view for login UI
		initButtonAndViewForLogin();
		//initialize button and view for researcher UI.
		initButtonAndViewForResearcher();
		//initialize button and view for location UI
		initButtonAndViewForLocation();
		//initialize button and view for children UI
		initButtonAndViewForChildren();		
	}
	
	private void initButtonAndViewForChildren() {
		childrenFirstName = (EditText)solo.getView(R.id.edCh_firs_txt);
		childrenTextViewList[0] = childrenFirstName;
		childrenLastName = (EditText)solo.getView(R.id.edCh_last_txt);
		childrenTextViewList[1] = childrenLastName;
		childrenPhone = (EditText)solo.getView(R.id.edCh_phon_txt);
		childrenTextViewList[2] = childrenPhone;
		childrenAddress = (EditText)solo.getView(R.id.edCh_addr_txt);
		childrenTextViewList[3] = childrenAddress;
		childrenBirthday = (EditText)solo.getView(R.id.edCh_birth_txt);
		childrenTextViewList[4] = childrenBirthday;
		childrenGender = (EditText)solo.getView(R.id.edCh_gender_txt);
		childrenTextViewList[5] = childrenGender;
		childrenPostal = (EditText)solo.getView(R.id.edCh_post_txt);
		childrenTextViewList[6] = childrenPostal;
		childrenLocation = (EditText)solo.getView(R.id.edCh_location_txt);
		childrenTextViewList[7] = childrenLocation;	
		childrenAddButton = (Button) solo.getView(R.id.actionbar_addButton);
		childrenSave = (Button) solo.getView(R.id.edCh_save_but);
		childrenEdit = (Button)solo.getView(R.id.edCh_edit_but);	
	}
	private void initButtonAndViewForLocation() {
		locationName = (EditText)solo.getView(R.id.edLo_loca_txt);
		locationTextViewList[0] = locationName;
		locationPhone = (EditText)solo.getView(R.id.edLo_phon_txt);
		locationTextViewList[1] = locationPhone;
		locationAddress = (EditText)solo.getView(R.id.edLo_addr_txt);
		locationTextViewList[2] = locationAddress;
		locationDescription = (EditText)solo.getView(R.id.edLo_descript_txt);
		locationTextViewList[3] = locationDescription;
		locationAddButton = (Button) solo.getView(R.id.actionbar_addButton);
		locationSave = (Button)solo.getView(R.id.edLo_save_but);
		locationEdit = (Button)solo.getView(R.id.edLo_edit_but);

		
	}
	private void initButtonAndViewForResearcher() {
		childrenButton=(Button)solo.getView(R.id.stRe_child_but);
		locationButton=(Button)solo.getView(R.id.stRe_loca_but);
		updateButton=(Button)solo.getView(R.id.stRe_update_but);
		changeLanguageButton=(Button)solo.getView(R.id.stRe_chLa_but);
		logOutButton=(Button)solo.getView(R.id.stRe_logO_but);	
	}
	private void initButtonAndViewForLogin() {
		userID = (EditText) solo.getView(R.id.userNameInput);
		password = (EditText) solo.getView(R.id.passWordInput);
		logInButton= (Button) solo.getView(R.id.login);
		
	}
}

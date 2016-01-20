//File: LocalDB.java
//Purpose: Handles all the local and remote database transaction
//Features Completed: Add/edit users/children/institution from local db, remove from only local database
//Features Incomplete: Unable to remove records from remote database
//Dependencies: admin.java, researcher.java, children/Edit/List/Option.java, locationEdit/List.java, Login.java, reseacherEdit/List.java,
//survey.java, survey/Detail/Finish/Option/Result/.java, surveyForAChildren.java
//Known Bugs: addNewInstitution() is inserting in the wrong columns
//General Comments: We are genuinely sorry and embarassed that this code is a monolith and that we did not have time to fix it. 


package com.example.cmpt371project;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.*;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.util.Log;


public class LocalDB extends SQLiteOpenHelper{
	public final static String DATABASE_NAME = "localDB";  
	public final static int DATABASE_VERSION = 1;  

	private final static String EXISTS="existsinremotedb";
	private final static String LASTMODIFIED="lastmodified";
	
	//Variables needed for users table
	public final static String USERS_TABLE = "users";  
	public final static String USER_PASSWORD = "user_Password"; 
	public final static String USER_NAME="username";
	public final static String USER_PRIVILEGE="privilege";
	public final static String USER_FIRSTNAME="firstname";
	public final static String USER_LASTNAME="lastname";
	public final static String USER_PHONENUM="phonenum";
	ArrayList<HashMap<String, Object>> userlist;

	//Variables needed for ChildInfo table
	public final static String CHILDREN_TABLE = "children";
	public final static String CHILD_ID = "child_id";
	public final static String CHILD_FName = "child_firstname";
	public final static String CHILD_LName = "child_lastname";
	public final static String CHILDREN_GENDER = "child_gender";
	public final static String CHILDREN_BIRTH = "child_birthdate";
	public final static String CHILDREN_PHONE = "child_phonenum";
	public final static String CHILDREN_POSTAL = "child_postalcode";
	public static final String CHILD_ADDRESS = "address";
	public static final String CHILDREN_LOCATION = "location";
	public static final String TAG_CHILDREN="children";
	ArrayList<HashMap<String, Object>> childrenList;

	//Variables needed for Institutions table
	public static final String INSTI_TABLE="institutions";
	public static final String INSTI_ID="institution_id";
	public static final String INSTI_name="institution_name";
	public static final String INSTI_address="institution_address";
	public static final String INSTI_phoneNum="institution_phonenum";
	public static final String INSTI_Descipt="institution_description";
	public static final String TAG_INSTITUTIONS="institutions";
	ArrayList<HashMap<String,Object>> institutionList;

	//Variables needed for survey table
	public static final String SURV_TABLE="survey";
	public static final String SURV_RESP_ID="surv_id_resp";
	public static final String SURV_ID_LOC="surv_id_loca";
	public static final String SURV_ID_RESEARCHER="surv_id_rese";
	public static final String SURV_Q1="surv_q1";
	public static final String SURV_Q2="surv_q2";
	public static final String SURV_Q3="surv_q3";
	public static final String SURV_Q4="surv_q4";
	public static final String SURV_Q5="surv_q5";
	public static final String SURV_Q6="surv_q6";
	public static final String SURV_Q7="surv_q7";
	public static final String SURV_Q8="surv_q8";
	public static final String SURV_Q9="surv_q9";
	public static final String SURV_Q10="surv_q10";
	public static final String SURV_Q11="surv_q11";
	public static final String SURV_Q12="surv_q12";
	public static final String SURV_Q13="surv_q13";
	public static final String SURV_Q14="surv_q14";
	public static final String SURV_Q15="surv_q15";
	public static final String SURV_Q16="surv_q16";
	public static final String SURV_Q17="surv_q17";
	public static final String SURV_Q18="surv_q18";
	public static final String SURV_Q19="surv_q19";
	public static final String SURV_Q20="surv_q20";
	public static final String SURV_SCORE="surv_score";
	public static final String SURV_DATE_END="surv_date_end";
	ArrayList<HashMap<String,Object>> surveyList;

	// JSON Node names
	public static final String TAG_SUCCESS = "success";
	public static final String TAG_username = "username";
	public static final String TAG_password = "password";
	public static final String TAG_users="users";
	private SQLiteDatabase thisDB;

	// users JSONArray
	JSONArray users = null;
	JSONArray children=null;
	JSONArray institutions=null;

	public LocalDB(Context context) {
		super(context,DATABASE_NAME,null,DATABASE_VERSION);	

	}

	@Override

	public void onCreate(SQLiteDatabase db){
		SQLiteStatement statement;
		//Creating institution table
		String institutionTable="CREATE TABLE "+INSTI_TABLE+" ("
				+INSTI_ID+" TEXT,"
				+INSTI_name+" TEXT,"
				+INSTI_address+" TEXT,"
				+INSTI_phoneNum+" TEXT,"
				+INSTI_Descipt+" TEXT,"
				+EXISTS+ " TEXT,"
				+LASTMODIFIED+" DATETIME,"
				+TAG_INSTITUTIONS+" TEXT,"
				+"UNIQUE("+INSTI_ID+")ON CONFLICT ABORT);";
		db.execSQL(institutionTable);
		statement = db.compileStatement("INSERT into "+INSTI_TABLE +"("
				+INSTI_ID +","
				+INSTI_name+","
				+INSTI_address+","
				+INSTI_phoneNum+","
				+INSTI_Descipt+","
				+TAG_INSTITUTIONS+","
				+EXISTS+","
				+LASTMODIFIED+")VALUES(?,?,?,?,?,?,?,?);");

		statement.bindString(1,"9999"); //TEMP ID
		statement.bindString(2,"UofS");
		statement.bindString(3,"SK");
		statement.bindString(4,"1234567890");
		statement.bindString(5,"UofS");
		statement.bindString(6,"institutions");
		statement.bindString(7,"False");
		statement.bindString(8,getDateTime());
		statement.executeInsert();
		statement.close();	
		
		//Creating user's table
		String userTable = "CREATE TABLE "+USERS_TABLE+" ("
				+USER_NAME +" TEXT NOT NULL, "
				+USER_PASSWORD+ " TEXT NOT NULL,"
				+USER_FIRSTNAME+ " TEXT,"
				+USER_LASTNAME+" TEXT,"
				+USER_PHONENUM+" TEXT,"
				+USER_PRIVILEGE+" TEXT NOT NULL,"
				+EXISTS+ " TEXT,"
				+LASTMODIFIED+" DATETIME,"
				+"UNIQUE("+USER_NAME+")ON CONFLICT IGNORE);";
		db.execSQL(userTable);
		
		//Creating children table
		String childTable = "CREATE TABLE "+CHILDREN_TABLE+" ("
				+CHILD_ID+" TEXT, "
				+CHILD_FName+" TEXT,"
				+CHILD_LName+" TEXT,"
				+CHILDREN_BIRTH+ " DATE,"
				+CHILDREN_GENDER+ " VARCHAR(12),"
				+CHILD_ADDRESS+" TEXT,"
				+CHILDREN_POSTAL+ " VARCHAR(7),"
				+CHILDREN_PHONE+ " TEXT,"
				+CHILDREN_LOCATION+ " TEXT,"
				+EXISTS+ " TEXT,"
				+LASTMODIFIED+" DATETIME,"
				+TAG_CHILDREN+ " TEXT,"
				+"FOREIGN KEY("+CHILDREN_LOCATION+") REFERENCES "+INSTI_TABLE+"("+INSTI_ID+"),"
				+"UNIQUE("+CHILD_ID+")ON CONFLICT ABORT);";

		Log.d("CHILD TABLE", childTable.toString());
		db.execSQL(childTable);

		statement = db.compileStatement("INSERT into "+USERS_TABLE+"(username,user_Password,privilege,firstname,lastname) VALUES(?,?,?,?,?);");
		statement = db.compileStatement
				("INSERT into "+USERS_TABLE+"(username,user_Password,Privilege,firstname,lastname,existsInRemoteDB, phonenum, lastmodified) VALUES(?,?,?,?,?,?,?,?);");
		statement.bindString(1, "res");
		statement.bindString(2, "res");
		statement.bindString(3,"researcher");
		statement.bindString(4,"res");
		statement.bindString(5,"eacher");
		statement.bindString(6,"False");
		statement.bindString(7,"5555555555");
		statement.bindString(8, "2014-03-15 23:49:24");
		statement.executeInsert();
		statement.close();		

		statement = db.compileStatement
				("INSERT into "+USERS_TABLE+"(username,user_Password,Privilege,firstname,lastname,existsInRemoteDB, phonenum, lastmodified) VALUES(?,?,?,?,?,?,?,?);");
		statement.bindString(1, "admin");
		statement.bindString(2, "admin");
		statement.bindString(3,"administrator");
		statement.bindString(4, "admin");
		statement.bindString(5,"istrator");
		statement.bindString(6,"False");
		statement.bindString(7,"5555555555");
		statement.bindString(8, getDateTime());
		statement.executeInsert();
		statement.close();	
		
		statement = db.compileStatement
				("INSERT into "+USERS_TABLE+"(username,user_Password,Privilege,firstname,lastname,existsInRemoteDB, phonenum, lastmodified) VALUES(?,?,?,?,?,?,?,?);");
		statement.bindString(1, "test");
		statement.bindString(2, "a");
		statement.bindString(3,"owner");
		statement.bindString(4, "admin");
		statement.bindString(5,"istrator");
		statement.bindString(6,"False");
		statement.bindString(7,"5555555555");
		statement.bindString(8, getDateTime());
		statement.executeInsert();
		statement.close();	

		statement = db.compileStatement("INSERT into "+CHILDREN_TABLE +"("
				+CHILD_ID +","
				+CHILD_FName+","
				+CHILD_LName+","
				+CHILDREN_BIRTH+","
				+CHILDREN_GENDER+","
				+CHILD_ADDRESS+","
				+CHILDREN_POSTAL+ ","
				+CHILDREN_PHONE+","
				+EXISTS+","
				+LASTMODIFIED+","
				+TAG_CHILDREN+","
				+CHILDREN_LOCATION+")VALUES(?,?,?,?,?,?,?,?,?,?,?,?);");

		statement.bindString(1,"0"); //TEMP ID
		statement.bindString(2,"David");
		statement.bindString(3,"Thai");
		statement.bindString(4,"2014-03-29");
		statement.bindString(5,"Male");
		statement.bindString(6,"Home");
		statement.bindString(7,"S7M4L4");
		statement.bindString(8,"5555555555");
		statement.bindString(9,"False");
		statement.bindString(10,getDateTime());
		statement.bindString(11,"children_num");
		statement.bindString(12,"UofS");
		statement.executeInsert();
		statement.close();	

		
		
		String surveyTable = "CREATE TABLE "+SURV_TABLE+"("
				+SURV_RESP_ID+" INTEGER, "
				+SURV_ID_LOC+" INTEGER,"
				+SURV_ID_RESEARCHER+" INTEGER,"
				+SURV_Q1+ " INTEGER,"
				+SURV_Q2+ " INTEGER,"
				+SURV_Q3+ " INTEGER,"
				+SURV_Q4+ " INTEGER,"
				+SURV_Q5+ " INTEGER,"
				+SURV_Q6+ " INTEGER,"
				+SURV_Q7+ " INTEGER,"
				+SURV_Q8+ " INTEGER,"
				+SURV_Q9+ " INTEGER,"
				+SURV_Q10+ " INTEGER,"
				+SURV_Q11+ " INTEGER,"
				+SURV_Q12+ " INTEGER,"
				+SURV_Q13+ " INTEGER,"
				+SURV_Q14+ " INTEGER,"				
				+SURV_Q15+ " INTEGER,"
				+SURV_Q16+ " INTEGER,"
				+SURV_Q17+ " INTEGER,"
				+SURV_Q18+ " INTEGER,"
				+SURV_Q19+ " INTEGER,"
				+SURV_Q20+ " INTEGER,"
				+SURV_SCORE+" INTEGER,"
				+SURV_DATE_END+" DATE, "
				+"FOREIGN KEY("+SURV_RESP_ID+")REFERENCES "+CHILDREN_TABLE+"("+CHILD_ID+")," 
				+"FOREIGN KEY("+SURV_ID_LOC+")REFERENCES "+INSTI_TABLE+"("+INSTI_ID+"),"
				+"FOREIGN KEY("+SURV_ID_RESEARCHER+")REFERENCES "+USERS_TABLE+"("+USER_NAME+"));";

		Log.d("Survey TABLE", surveyTable.toString());
		db.execSQL(surveyTable);

	} 

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sql = "DROP TABLE IF EXISTS " + USERS_TABLE; 
		db.execSQL(sql);
		thisDB=db;
		onCreate(db);

	}

	/**
	 * Gets the password of user
	 * @param username
	 * @return Returns password for comparison for login
	 */
	public String readPassword(String username){
		//to do 
		//the password need to be encrypt
		String password = new String();
		Cursor DBcursor = this.getWritableDatabase().rawQuery("Select user_Password from "+USERS_TABLE +" WHERE username='" + username + "'",null);

		while(DBcursor.moveToNext()){	
			password = DBcursor.getString(0);	
		}
		DBcursor.close(); 
		this.getWritableDatabase().close();
		return password;
	}
	
	/**
	 * Gets the privilege of the user
	 * @param username The username of user
	 * @return returns the level of privilege of the user
	 */
	public String getPrivilege(String username){
		String privilege = new String();
		Cursor DBcursor = this.getWritableDatabase().rawQuery("Select privilege from "+USERS_TABLE +" WHERE username='" + username + "'",null);

		while(DBcursor.moveToNext()){	
			privilege = DBcursor.getString(0);	
		}
		DBcursor.close(); 
		this.getWritableDatabase().close();
		return privilege;
	}


	/**
	 * Adding a new user to the local database
	 * @author Xingze
	 * @param username The username
	 * @param password Password for the user
	 * @param privilege Privilege level for the user
	 */
	public void addNewUser(String username, String password, String firstname, String lastname, String phonenum, String privilege){
		thisDB=this.getWritableDatabase();
		ContentValues userData = new ContentValues();

		userData.put(USER_NAME, username);
		userData.put(USER_PASSWORD, password);  
		userData.put(USER_FIRSTNAME, firstname);  
		userData.put(USER_LASTNAME, lastname);
		userData.put(USER_PHONENUM, phonenum);
		userData.put(USER_PRIVILEGE, privilege);
		thisDB.insert(USERS_TABLE, null, userData);  

	}

	/**
	 * @author Xingze
	 * @return an arrayList of all users data
	 * get all users in DB, return an arrayList
	 */
	public ArrayList<HashMap<String,Object>> getAllUsers(){

		ArrayList<HashMap<String,Object>> allUsers = new ArrayList<HashMap<String,Object>>();
		Cursor DBcursor = this.getWritableDatabase().rawQuery("Select " + USER_NAME +","
				+ USER_PASSWORD +","
				+ USER_FIRSTNAME +","
				+ USER_LASTNAME +","
				+ USER_PHONENUM +","
				+ USER_PRIVILEGE 
				+ " from " + USERS_TABLE,null);
		while(DBcursor.moveToNext()){	
			String userID = DBcursor.getString(0);	
			String userPassword = DBcursor.getString(1);
			String userFirstName = DBcursor.getString(2);
			String userLastName = DBcursor.getString(3);
			String userPhoneNum = DBcursor.getString(4);
			String userPrivilege = DBcursor.getString(5);


			HashMap<String,Object> oneSurvey = new HashMap<String,Object>();

			//set data in hash map
			oneSurvey.put("userID", userID);
			oneSurvey.put("userPassword", userPassword);
			oneSurvey.put("userFirstName", userFirstName);
			oneSurvey.put("userLastName", userLastName);
			oneSurvey.put("userPhoneNum", userPhoneNum);
			oneSurvey.put("userPrivilege", userPrivilege);    			
			allUsers.add(oneSurvey);

		}
		DBcursor.close(); 
		this.getWritableDatabase().close();		
		return allUsers; 

	}
	/**
	 * @author Xingze
	 * update users in DB
	 * @param username  the key for search in DB
	 */
	public void updateUsers(String username, String password, String firstname, String lastname, String phonenum){
		SQLiteDatabase db = this.getWritableDatabase();

		db.execSQL("UPDATE "+ USERS_TABLE +" SET "+ USER_FIRSTNAME +"='" + firstname + "' WHERE "+ USER_NAME+ "='" + username  + "'");
		db.execSQL("UPDATE "+ USERS_TABLE +" SET "+ USER_LASTNAME +"='" + lastname + "' WHERE "+ USER_NAME+ "='" + username  + "'");
		db.execSQL("UPDATE "+ USERS_TABLE +" SET "+ USER_PASSWORD +"='" + password + "' WHERE "+ USER_NAME+ "='" + username  + "'");
		db.execSQL("UPDATE "+ USERS_TABLE +" SET "+ USER_PHONENUM +"='" + phonenum + "' WHERE "+ USER_NAME+ "='" + username  + "'");


		db.close();
	}
	/**
	 * @author Xingze
	 * @param the element deleted from database
	 */
	public void deleteUsers(String[] name){
		String where = USER_NAME+" = ?";  
		String[] whereValues = name;   
		this.getWritableDatabase().delete(USERS_TABLE, where, whereValues);  
		this.getWritableDatabase().close();  

	}


	/**
	 * getUserTableFromRemoteDB 
	 * when user table in the local database needs to be updated from remote database
	 * this method will be called. This method calls getChildrenTableFromRemoteDB
	 */
	public void getUserTableFromRemoteDB(){
		userlist = new ArrayList<HashMap<String, Object>>();
		thisDB=this.getWritableDatabase();
		pullUserFromRemoteDB task = new pullUserFromRemoteDB();
		task.execute();
	}

	/**
	 * pullUserFromRemoteDB 
	 * when children table in the local database needs to be updated from remote database
	 * this method will be called. This method calls getChildrenTableFromRemoteDB
	 */
	private class pullUserFromRemoteDB extends AsyncTask<String, String, String>{

		JSONParser jParser = new JSONParser();
		// url to get all user list
		private static final String url_all_users = "http://cmpt371g2.usask.ca:8084/get_all_users.php";

		@Override
		/*
		 * getting all users from url
		 */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(url_all_users, "GET", params);
			assert(!json.equals(null));
			// Check your log cat for JSON reponse
			Log.d("All users: ", json.toString());

			try {
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// users found
					// Getting Array of Users
					users = json.getJSONArray(TAG_users);

					//looping through All Users
					for (int i = 0; i < users.length(); i++) {
						JSONObject c = users.getJSONObject(i);

						// Storing each json item in variable
						String username = c.getString(USER_NAME);
						String password = c.getString(USER_PASSWORD);
						String firstname = c.getString(USER_FIRSTNAME);
						String lastname = c.getString(USER_LASTNAME);
						String phonenum = c.getString(USER_PHONENUM);
						String privilege = c.getString(USER_PRIVILEGE);
						String existsinremotedb = c.getString(EXISTS);
						String lastmodified=c.getString(LASTMODIFIED);
						
						// Inserting the values into the table
						SQLiteStatement statement = thisDB.compileStatement("INSERT into "+USERS_TABLE +"("
								+USER_NAME +","
								+USER_PASSWORD+ ","
								+USER_FIRSTNAME+ ","
								+USER_LASTNAME+","
								+USER_PHONENUM+","
								+USER_PRIVILEGE+","
								+EXISTS+","
								+LASTMODIFIED+")VALUES(?,?,?,?,?,?,?,?);");	

						statement.bindString(1,username); //TEMP USER ID
						statement.bindString(2,password);
						statement.bindString(3,firstname);
						statement.bindString(4,lastname);
						statement.bindString(5,phonenum);
						statement.bindString(6,privilege);
						statement.bindString(7,existsinremotedb);
						statement.bindString(8,lastmodified);
						statement.executeInsert();
						statement.close();	

						// creating new HashMap
						HashMap<String, Object> map = new HashMap<String, Object>();

						// adding each child node to HashMap key => value
						map.put(USER_NAME, username);
						map.put(USER_PASSWORD, password);
						map.put(USER_FIRSTNAME, firstname);
						map.put(USER_LASTNAME, lastname);
						map.put(USER_PHONENUM, phonenum);
						map.put(USER_PRIVILEGE, privilege);
						map.put(EXISTS, existsinremotedb);
						map.put(LASTMODIFIED, lastmodified);
						// adding HashList to ArrayList
						userlist.add(map);
					}
				} else {
					throw new Exception();
				}
			} catch (JSONException e) {
				Log.d("JSON EXCEPTION",e.toString());
				e.printStackTrace();
			} catch (IOException e) {
				Log.d("IO EXCEPTION",e.toString());
				e.printStackTrace();
			} catch (Exception e) {
				Log.d("Exception Tag", e.toString());
				e.printStackTrace();
			} 
			return null;
		}
	}



	//=====================CHILDREN TABLE RELATED=====================

	/**
	 * Returns a list of all children in the child table from the local database 
	 * @return childrenList ArrayList<HashMap<String,Object>>
	 */
	public ArrayList<HashMap<String, Object>> getListofChildren(){
		ArrayList<HashMap<String, Object>> childrenList= new ArrayList<HashMap<String, Object>>();
		SQLiteDatabase db = this.getWritableDatabase();
		String query = "SELECT * FROM "+CHILDREN_TABLE;
		HashMap<String,Object> map = new HashMap<String,Object>();
		Cursor cursor = db.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			do {
				map.put("location", cursor.getString(1)+" "+cursor.getString(2));
				childrenList.add(map);
				Log.d("Map",cursor.getString(1));
			} while (cursor.moveToNext());
		}

		return childrenList;

	}

	/**
	 * getChildrenTableFromRemoteDB 
	 * when children table in the local database needs to be updated from remote database
	 * this method will be called. This method calls getChildrenTableFromRemoteDB
	 */
	public void getChildrenTableFromRemoteDB(){
		childrenList= new ArrayList<HashMap<String, Object>>();
		thisDB=this.getWritableDatabase();
		pullChildrenFromRemoteDB task = new pullChildrenFromRemoteDB();
		task.execute();
	}

	/**
	 * pullChildrenFromRemoteDB class that calls to a php webservice
	 * which is done in the background. The php script queries the remote database and
	 * returns the result in a JSON string which will be parsed to retrieve each of the table elements.
	 */
	private class pullChildrenFromRemoteDB extends AsyncTask<String, String, String>{

		JSONParser jParser = new JSONParser();
		// url to get all user list
		public static final String url_all_children = "http://cmpt371g2.usask.ca:8084/get_all_children.php";

		/*
		 * getting all children from url
		 */
		@Override
		protected String doInBackground(String... args) {

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(url_all_children, "GET", params);
			assert(!json.equals(null));
			// Check your log cat for JSON reponse
			Log.d("All children: ", json.toString());

			try {
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// users found
					// Getting Array of Users
					children = json.getJSONArray(TAG_CHILDREN);

					//looping through All Users
					for (int i = 0; i < children.length(); i++) {
						JSONObject c = children.getJSONObject(i);

						// Storing each json item in variable
						String child_id = c.getString(CHILD_ID);
						String child_firstName = c.getString(CHILD_FName);
						String child_lastName = c.getString(CHILD_LName);
						String child_gender = c.getString(CHILDREN_GENDER);
						String child_birthdate = c.getString(CHILDREN_BIRTH);
						String child_phonenum = c.getString(CHILDREN_PHONE);
						String child_postalcode = c.getString(CHILDREN_POSTAL);
						String child_address=c.getString(CHILD_ADDRESS);
						String existsinremotedb = c.getString(EXISTS);
						String lastmodified=c.getString(LASTMODIFIED);

						// Inserting the values into the table
						SQLiteStatement statement = thisDB.compileStatement("INSERT into "+CHILDREN_TABLE +"("
								+CHILD_ID +","
								+CHILD_FName+","
								+CHILD_LName+","
								+CHILDREN_BIRTH+","
								+CHILDREN_GENDER+","
								+CHILD_ADDRESS+","
								+CHILDREN_POSTAL+ ","
								+CHILDREN_PHONE+","
								+EXISTS+","
								+LASTMODIFIED+")VALUES(?,?,?,?,?,?,?,?,?,?);");

						statement.bindString(1,child_id);
						statement.bindString(2,child_firstName);
						statement.bindString(3,child_lastName);
						statement.bindString(4,child_gender);
						statement.bindString(5,child_birthdate);
						statement.bindString(6,child_address);
						statement.bindString(7,child_postalcode);
						statement.bindString(8,child_phonenum);
						statement.bindString(9,existsinremotedb);
						statement.bindString(10,lastmodified);

						statement.executeInsert();
						statement.close();				

						// creating new HashMap
						HashMap<String, Object> map = new HashMap<String, Object>();

						// adding each child node to HashMap key => value
						map.put(CHILD_ID,child_id);
						map.put(CHILD_FName,child_firstName);
						map.put(CHILD_LName,child_lastName);
						map.put(CHILDREN_BIRTH,child_birthdate);
						map.put(CHILDREN_GENDER,child_gender);
						map.put(CHILD_ADDRESS, child_address);
						map.put(CHILDREN_POSTAL,child_postalcode);
						map.put(CHILDREN_PHONE,child_phonenum);
						map.put(EXISTS,existsinremotedb);
						map.put(LASTMODIFIED,lastmodified);


						// adding HashList to ArrayList
						childrenList.add(map);
					}
				} else {
					throw new Exception();
				}
			} catch (JSONException e) {
				Log.d("JSON EXCEPTION",e.toString());
				e.printStackTrace();
			} catch (IOException e) {
				Log.d("IO EXCEPTION",e.toString());
				e.printStackTrace();
			} catch (Exception e) {
				Log.d("Exception Tag", e.toString());
				e.printStackTrace();
			} 
			return null;
		}
	}

	/**
	 * Adding a new child to the database or modify already existing child. Passes the parameters to 
	 * insertChildToLocalDB to insert into the table in the local database to be performed in a background thread
	 * @author Xingze
	 * @param firstName Child's first name
	 * @param lastName Child's last name
	 * @param gender Child's gender
	 * @param birthdate Child's Date of birth
	 * @param address Child's current home address
	 * @param postal Child's postal code
	 * @param phoneNum Child's phone number
	 * @param locationID Child's location
	 */
	public void addNewChild(String firstName, String lastName, String gender, String birthdate, String address, String postal, String phoneNum, String locationID){
		thisDB=this.getWritableDatabase();
		ContentValues childData = new ContentValues();


		//read current number, then set number+1 as new ID 
		Cursor DBcursor = this.getWritableDatabase().rawQuery("Select " + CHILD_ID
				+ " from " + CHILDREN_TABLE + " WHERE " + TAG_CHILDREN+ "='children_num'",null);
		String currentNumber = "";
		while(DBcursor.moveToNext()){	
			currentNumber = DBcursor.getString(0);
		}
		DBcursor.close();

		int thisNumber = Integer.parseInt(currentNumber);
		thisNumber++;

		//update current number
		thisDB.execSQL("UPDATE "+ CHILDREN_TABLE +" SET "+ CHILD_ID +"='" + String.valueOf(thisNumber) + "' WHERE "+ TAG_CHILDREN+ "='children_num'");
		//finished get number

		childData.put(CHILD_ID, String.valueOf(currentNumber));
		childData.put(CHILD_FName, firstName);  
		childData.put(CHILD_LName, lastName);  
		childData.put(CHILDREN_BIRTH, birthdate);
		childData.put(CHILDREN_GENDER, gender);
		childData.put(CHILD_ADDRESS, address);
		childData.put(CHILDREN_POSTAL, postal);
		childData.put(CHILDREN_PHONE, phoneNum);
		childData.put(CHILDREN_LOCATION, locationID);
		childData.put(TAG_CHILDREN, "child");
		thisDB.insert(CHILDREN_TABLE, null, childData); 
		//finished insert new location

		thisDB.close();
	}


	/**
	 * @author Xingze
	 * @return an arrayList of all children data
	 * get all location in DB, return an arrayList  
	 */
	public ArrayList<HashMap<String,Object>> getAllChildren(){

		ArrayList<HashMap<String,Object>> allChildren = new ArrayList<HashMap<String,Object>>();
		Cursor DBcursor = this.getWritableDatabase().rawQuery("Select " + CHILD_ID +","
				+ CHILD_FName +","
				+ CHILD_LName +","
				+ CHILDREN_BIRTH +","
				+ CHILDREN_GENDER +","
				+ CHILD_ADDRESS +","
				+ CHILDREN_POSTAL +","
				+ CHILDREN_PHONE +","
				+ CHILDREN_LOCATION
				+ " from " + CHILDREN_TABLE+ " WHERE " + TAG_CHILDREN+ "='child'",null);
		while(DBcursor.moveToNext()){	
			String childID = DBcursor.getString(0);	
			String childFName = DBcursor.getString(1);
			String childLName = DBcursor.getString(2);
			String childBirth = DBcursor.getString(3);
			String childGender = DBcursor.getString(4);
			String childAddress = DBcursor.getString(5);
			String childPostal = DBcursor.getString(6);
			String childPhone = DBcursor.getString(7);
			String childLocation = DBcursor.getString(8);
			String childLocationName = this.getLocationName(childLocation);

			HashMap<String,Object> oneSurvey = new HashMap<String,Object>();

			//set data in hash map
			oneSurvey.put("childID", childID);
			oneSurvey.put("childFName", childFName);
			oneSurvey.put("childLName", childLName);
			oneSurvey.put("childBirth", childBirth);
			oneSurvey.put("childGender", childGender);  
			oneSurvey.put("childAddress", childAddress);
			oneSurvey.put("childPostal", childPostal);
			oneSurvey.put("childPhone", childPhone); 
			oneSurvey.put("childLocation", childLocation);
			oneSurvey.put("childLocationName", childLocationName);
			allChildren.add(oneSurvey);

		}
		DBcursor.close(); 
		this.getWritableDatabase().close();		
		return allChildren; 

	}



	/**
	 * @author Xingze
	 * @return an arrayList of all children data
	 * get all location in DB, return an arrayList  
	 */
	public ArrayList<HashMap<String,Object>> getAllChildrenAtLocation(String aLocation){

		ArrayList<HashMap<String,Object>> allChildren = new ArrayList<HashMap<String,Object>>();
		Cursor DBcursor = this.getWritableDatabase().rawQuery("Select " + CHILD_ID +","
				+ CHILD_FName +","
				+ CHILD_LName +","
				+ CHILDREN_BIRTH +","
				+ CHILDREN_GENDER +","
				+ CHILD_ADDRESS +","
				+ CHILDREN_POSTAL +","
				+ CHILDREN_PHONE +","
				+ CHILDREN_LOCATION
				+ " from " + CHILDREN_TABLE+ " WHERE " + TAG_CHILDREN+ "='child' and " + CHILDREN_LOCATION + " = '" +aLocation+"'" ,null);
		while(DBcursor.moveToNext()){	
			String childID = DBcursor.getString(0);	
			String childFName = DBcursor.getString(1);
			String childLName = DBcursor.getString(2);
			String childBirth = DBcursor.getString(3);
			String childGender = DBcursor.getString(4);
			String childAddress = DBcursor.getString(5);
			String childPostal = DBcursor.getString(6);
			String childPhone = DBcursor.getString(7);
			String childLocation = DBcursor.getString(8);

			HashMap<String,Object> oneSurvey = new HashMap<String,Object>();

			//set data in hash map
			oneSurvey.put("childID", childID);
			oneSurvey.put("childFName", childFName);
			oneSurvey.put("childLName", childLName);
			oneSurvey.put("childBirth", childBirth);
			oneSurvey.put("childGender", childGender);  
			oneSurvey.put("childAddress", childAddress);
			oneSurvey.put("childPostal", childPostal);
			oneSurvey.put("childPhone", childPhone); 
			oneSurvey.put("childLocation", childLocation);
			allChildren.add(oneSurvey);

		}
		DBcursor.close(); 
		this.getWritableDatabase().close();		
		return allChildren; 

	}

	/**
	 * @author Xingze
	 * @param the element deleted from database
	 */
	public void deleteChildren(String[] name){
		String where = CHILD_ID+" = ?";  
		String[] whereValues = name;   
		this.getWritableDatabase().delete(CHILDREN_TABLE, where, whereValues);  
		this.getWritableDatabase().close();  

	}

	/**
	 * @author Xingze
	 * update location in DB
	 * @param   id the key for search in DB
	 */  
	public void updateChildren(String key,String firstName, String lastName, String gender, String birthdate, String address, String postal, String phoneNum, String locationID){
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("UPDATE "+ CHILDREN_TABLE +" SET "+ CHILD_FName +"='" + firstName + "' WHERE "+ CHILD_ID+ "='" + key  + "'");
		db.execSQL("UPDATE "+ CHILDREN_TABLE +" SET "+ CHILD_LName +"='" + lastName + "' WHERE "+ CHILD_ID+ "='" + key  + "'");
		db.execSQL("UPDATE "+ CHILDREN_TABLE +" SET "+ CHILDREN_BIRTH +"='" + gender + "' WHERE "+ CHILD_ID+ "='" + key  + "'");
		db.execSQL("UPDATE "+ CHILDREN_TABLE +" SET "+ CHILDREN_GENDER +"='" + birthdate + "' WHERE "+ CHILD_ID+ "='" + key  + "'");
		db.execSQL("UPDATE "+ CHILDREN_TABLE +" SET "+ CHILD_ADDRESS +"='" + address + "' WHERE "+ CHILD_ID+ "='" + key  + "'");
		db.execSQL("UPDATE "+ CHILDREN_TABLE +" SET "+ CHILDREN_POSTAL +"='" + postal + "' WHERE "+ CHILD_ID+ "='" + key  + "'");
		db.execSQL("UPDATE "+ CHILDREN_TABLE +" SET "+ CHILDREN_PHONE +"='" + phoneNum + "' WHERE "+ CHILD_ID+ "='" + key  + "'");

		db.close();
	}


	//=====================INSTITUTION TABLE RELATED=====================
	/**
	 * @author Xingze
	 * @param institutionName
	 * @param address
	 * @param phoneNum
	 * @param description
	 */
	public void addNewInstitution(String institutionName, String address, String phoneNum, String description){
		thisDB=this.getWritableDatabase();
		ContentValues userData = new ContentValues();


		//read current number, then set number+1 as new ID 
		Cursor DBcursor = this.getWritableDatabase().rawQuery("Select " + INSTI_ID
				+ " from " + INSTI_TABLE + " WHERE " + TAG_INSTITUTIONS+ "='inistitution_num'",null);
		String currentNumber = "";
		while(DBcursor.moveToNext()){	
			currentNumber = DBcursor.getString(0);
		}
		DBcursor.close();
		
		int thisNumber;
		if(currentNumber.compareTo("")!=0)
			thisNumber = Integer.parseInt(currentNumber);
		else 
			thisNumber = 0;
		
		thisNumber++;

		//update current number
		thisDB.execSQL("UPDATE "+ INSTI_TABLE +" SET "+ INSTI_ID +"='" + String.valueOf(thisNumber) + "' WHERE "+ TAG_INSTITUTIONS+ "='inistitution_num'");
		//finished get number

		userData.put(INSTI_ID, String.valueOf(currentNumber));
		userData.put(INSTI_name, institutionName);  
		userData.put(INSTI_address, address);  
		userData.put(INSTI_phoneNum, phoneNum);
		userData.put(INSTI_Descipt, description);
		userData.put(TAG_INSTITUTIONS, "institutions");
		thisDB.insert(INSTI_TABLE, null, userData);  
		//finished insert new location

		thisDB.close();
	}

	/**
	 * @author Xingze
	 * @return location name
	 * get location name from location ID
	 */
	
	public String getLocationName(String locationID){

		String locationName="";
		Cursor DBcursor = this.getWritableDatabase().rawQuery("Select " + INSTI_name
				+ " from " + INSTI_TABLE+ " WHERE " + INSTI_ID+ "='"+locationID+"'",null);
		while(DBcursor.moveToNext()){	
			locationName = DBcursor.getString(0);
		}
		DBcursor.close(); 
		this.getWritableDatabase().close();		
		return locationName; 

	}

	/**
	 * @author Xingze
	 * @return an arrayList of all location data
	 * get all location in DB, return an arrayList
	 */
	public ArrayList<HashMap<String,Object>> getAllLocation(){

		ArrayList<HashMap<String,Object>> allLocation = new ArrayList<HashMap<String,Object>>();
		Cursor DBcursor = this.getWritableDatabase().rawQuery("Select " + INSTI_ID +","
				+ INSTI_name +","
				+ INSTI_address +","
				+ INSTI_phoneNum +","
				+ INSTI_Descipt
				+ " from " + INSTI_TABLE+ " WHERE " + TAG_INSTITUTIONS+ "='institutions'",null);
		while(DBcursor.moveToNext()){	
			String locationID = DBcursor.getString(0);	
			String locationName = DBcursor.getString(1);
			String locationAddress = DBcursor.getString(2);
			String locationPhoneNum = DBcursor.getString(3);
			String locationDescipt = DBcursor.getString(4);

			HashMap<String,Object> oneSurvey = new HashMap<String,Object>();

			//set data in hash map
			oneSurvey.put("locationID", locationID);
			oneSurvey.put("locationName", locationName);
			oneSurvey.put("locationAddress", locationAddress);
			oneSurvey.put("locationPhoneNum", locationPhoneNum);
			oneSurvey.put("locationDescipt", locationDescipt);  			
			allLocation.add(oneSurvey);

		}
		DBcursor.close(); 
		this.getWritableDatabase().close();		
		return allLocation; 

	}


	/**
	 * @author Xingze
	 * update location in DB
	 * @param   id the key for search in DB
	 * @param firstName Child's first name
	 * @param lastName Child's last name
	 * @param gender Child's gender
	 * @param birthdate Child's Date of birth
	 * @param address Child's current home address
	 * @param postal Child's postal code
	 * @param phoneNum Child's phone number
	 * @param locationID Child's location
	 */
	public void updateLocation(String key,String institutionName, String address, String phoneNum, String description){
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("UPDATE "+ INSTI_TABLE +" SET "+ INSTI_name +"='" + institutionName + "' WHERE "+ INSTI_ID+ "='" + key  + "'");
		db.execSQL("UPDATE "+ INSTI_TABLE +" SET "+ INSTI_address +"='" + address + "' WHERE "+ INSTI_ID+ "='" + key  + "'");
		db.execSQL("UPDATE "+ INSTI_TABLE +" SET "+ INSTI_phoneNum +"='" + phoneNum + "' WHERE "+ INSTI_ID+ "='" + key  + "'");
		db.execSQL("UPDATE "+ INSTI_TABLE +" SET "+ INSTI_Descipt +"='" + description + "' WHERE "+ INSTI_ID+ "='" + key  + "'");


		db.close();
	}


	/**
	 * @author Xingze
	 * @param the element deleted from database
	 */
	public void deleteLocation(String[] name){
		String where = INSTI_ID+" = ?";  
		String[] whereValues = name;   
		this.getWritableDatabase().delete(INSTI_TABLE, where, whereValues);  
		this.getWritableDatabase().close();  

	}


	/**
	 * getInsitutionTableFromRemoteDB 
	 * when institution table in the local database needs to be updated from remote database
	 * this method will be called. This method calls pullInstitutionFromRemoteDB
	 */
	public void getInsitutionTableFromRemoteDB(){
		institutionList= new ArrayList<HashMap<String, Object>>();
		thisDB=this.getWritableDatabase();
		pullInstitutionFromRemoteDB task = new pullInstitutionFromRemoteDB();
		task.execute();
	}

	/**
	 * pullInstitutionFromRemoteDB class that calls to a php webservice
	 * which is done in the background. The php script queries the remote database and
	 * returns the result in a JSON string which will be parsed to retrieve each of the table elements.
	 */
	private class pullInstitutionFromRemoteDB extends AsyncTask<String, String, String>{

		JSONParser jParser = new JSONParser();
		// url to get all user list
		public static final String url_all_institution = "http://cmpt371g2.usask.ca:8084/get_all_institutions.php";

		/*
		 * getting all institutions from url
		 */
		@Override
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(url_all_institution, "GET", params);
			assert(!json.equals(null));
			// Check your log cat for JSON reponse
			Log.d("All institutions: ", json.toString());

			try {
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) { // institutions found

					// Getting Array of institutions
					institutions = json.getJSONArray(TAG_INSTITUTIONS);

					//looping through All institutions
					for (int i = 0; i < institutions.length(); i++) {
						JSONObject c = institutions.getJSONObject(i);

						// Storing each json item in variable
						String institutionId = c.getString(INSTI_ID);
						String institutionName = c.getString(INSTI_name);
						String institutionAddress=c.getString(INSTI_address);
						String institutionphonenumber = c.getString(INSTI_phoneNum);
						String institutionDescription=c.getString(INSTI_Descipt);
						String existsinremotedb = c.getString(EXISTS);
						String lastmodified=c.getString(LASTMODIFIED);

						// Inserting the values into the table
						SQLiteStatement statement = thisDB.compileStatement("INSERT into "+INSTI_TABLE +"("
								+INSTI_ID+","
								+INSTI_name+","
								+INSTI_address+","
								+INSTI_phoneNum+","
								+INSTI_Descipt+","
								+EXISTS+","
								+LASTMODIFIED+") VALUES(?,?,?,?,?,?,?);");
					
						statement.bindString(1, institutionId);
						statement.bindString(2, institutionName);
						statement.bindString(3,institutionAddress);
						statement.bindString(4,institutionphonenumber);
						statement.bindString(5,institutionDescription);
						statement.bindString(6,existsinremotedb);
						statement.bindString(7,lastmodified);
						
						statement.executeInsert();
						statement.close();				

						// creating new HashMap
						HashMap<String, Object> map = new HashMap<String, Object>();

						// adding each institutions node to HashMap key => value
						map.put(INSTI_ID, institutionId);
						map.put(INSTI_name, institutionName);
						map.put(INSTI_address, institutionAddress);
						map.put(INSTI_phoneNum, institutionphonenumber);
						map.put(INSTI_Descipt, institutionDescription);
						map.put(EXISTS, existsinremotedb);
						map.put(LASTMODIFIED, lastmodified);

						// adding HashList to ArrayList
						institutionList.add(map);
					}
				} else {
					throw new Exception();
				}
			} catch (JSONException e) {
				Log.d("JSON EXCEPTION",e.toString());
				e.printStackTrace();
			} catch (IOException e) {
				Log.d("IO EXCEPTION",e.toString());
				e.printStackTrace();
			} catch (Exception e) {
				Log.d("Exception Tag", e.toString());
				e.printStackTrace();
			} 
			return null;
		}

	}
	public void syncUserTable(){
		thisDB=this.getWritableDatabase();
		syncUserTableTask task = new syncUserTableTask();
		task.execute();
	}

	private class syncUserTableTask extends AsyncTask<String, String, String>{

		private static final String test_url = "http://cmpt371g2.usask.ca:8084/syncUsers.php";

		/*
		 * Creating users
		 */
		JSONParser jsonParser = new JSONParser();
		protected String doInBackground(String... args) {
			String query = "SELECT * FROM "+USERS_TABLE;
			Cursor cursor = thisDB.rawQuery(query, null);
			String[] result = new String[8];
			while(cursor.moveToNext()){

				result[0]=cursor.getString(0);
				result[1]=cursor.getString(1);
				result[2]=cursor.getString(5);
				result[3]=cursor.getString(3);
				result[4]=cursor.getString(4);
				result[5]=cursor.getString(5);
				result[6]=cursor.getString(6);
				result[7]=cursor.getString(7);

				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair(USER_NAME, result[0].toString()));
				params.add(new BasicNameValuePair(USER_PASSWORD, result[1].toString()));
				params.add(new BasicNameValuePair(USER_FIRSTNAME, result[2].toString()));
				params.add(new BasicNameValuePair(USER_LASTNAME, result[3].toString()));
				params.add(new BasicNameValuePair(USER_PHONENUM, result[4].toString()));
				params.add(new BasicNameValuePair(USER_PRIVILEGE, result[5].toString()));
				params.add(new BasicNameValuePair(EXISTS, result[6].toString()));
				params.add(new BasicNameValuePair(LASTMODIFIED, result[7].toString()));  

				// getting JSON Object
				// Note that create user url accepts POST method
				JSONObject json = jsonParser.makeHttpRequest(test_url,"POST", params);

				// check log cat fro response
				Log.d("Create Response", json.toString());

				// check for success tag
				try {
					int success = json.getInt(TAG_SUCCESS);

					if (success == 1) {
						// successfully created product
						thisDB.execSQL("UPDATE "+ USERS_TABLE +" SET "+ EXISTS +"='True' WHERE "+ USER_NAME+ "='" + result[0].toString() +"'");
					} 
					else if(success == 2){
						String where = USER_NAME+"= ?";  
						String[] whereValues = new String[]{ result[0].toString()};    
						thisDB.delete(USERS_TABLE, where, whereValues);
					}
					else if(success==3){

					}
					// RemoteDB is newer
					else if(success==4){
						JSONArray test=json.getJSONArray(USERS_TABLE);
						updateLocalDBUsers(test);

					}
					else {
						// failed to create product
					}
				} catch (JSONException e) {
					Log.d("JSON Exception", e.toString());
					e.printStackTrace();
				}


			}
			getUserTableFromRemoteDB();
			return null;
		}

	}
	private void updateLocalDBUsers(JSONArray array){
		JSONObject c;
		try {
			// Storing each json item in variable
			c = array.getJSONObject(0);
			String username = c.getString("username");
			String password = c.getString("password");
			String firstname = c.getString("firstname");
			String lastname = c.getString("lastname");
			String phonenum = c.getString("phonenum");
			String privilege = c.getString("privilege");
			String existsinremotedb = c.getString("existsinremotedb");
			String lastmodified=c.getString("lastmodified");

			String sqlUpdate="UPDATE "+ USERS_TABLE +" SET "
					+USER_PASSWORD+"='"+password+"'"
					+","+USER_PHONENUM+"='"+phonenum+"'"
					+","+USER_PRIVILEGE+"='"+privilege+"'"
					+","+EXISTS +"="+existsinremotedb+"'"
					+","+LASTMODIFIED+"='"+lastmodified+"'"
					+" WHERE "+ USER_NAME+ "='"+username+"'"
					+" and "+USER_FIRSTNAME+"='"+firstname+"'"
					+" and "+USER_LASTNAME+"='"+lastname+"'";
			thisDB.execSQL(sqlUpdate);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void syncChildrenTable(){
		thisDB=this.getWritableDatabase();
		syncChildrenTableTask task = new syncChildrenTableTask();
		task.execute();
	}

	private class syncChildrenTableTask extends AsyncTask<String, String, String>{

		private static final String test_url = "http://cmpt371g2.usask.ca:8084/syncChildren.php";

		/*
		 * Creating users
		 */
		JSONParser jsonParser = new JSONParser();
		protected String doInBackground(String... args) {
			String query = "SELECT * FROM "+CHILDREN_TABLE;
			Cursor cursor = thisDB.rawQuery(query, null);
			String[] result = new String[11];
			while(cursor.moveToNext()){

				result[0]=cursor.getString(0);
				result[1]=cursor.getString(1);
				result[2]=cursor.getString(5);
				result[3]=cursor.getString(3);
				result[4]=cursor.getString(4);
				result[5]=cursor.getString(5);
				result[6]=cursor.getString(6);
				result[7]=cursor.getString(7);
				result[8]=cursor.getString(8);
				result[9]=cursor.getString(9);
				result[10]=cursor.getString(10);
				
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair(CHILD_ID, result[0].toString()));
				params.add(new BasicNameValuePair(CHILD_FName, result[1].toString()));
				params.add(new BasicNameValuePair(CHILD_LName, result[2].toString()));
				params.add(new BasicNameValuePair(CHILDREN_BIRTH, result[3].toString()));
				params.add(new BasicNameValuePair(CHILDREN_GENDER, result[4].toString()));
				params.add(new BasicNameValuePair(CHILD_ADDRESS, result[5].toString()));
				params.add(new BasicNameValuePair(CHILDREN_POSTAL, result[6].toString()));
				params.add(new BasicNameValuePair(CHILDREN_PHONE, result[7].toString()));
				params.add(new BasicNameValuePair(CHILDREN_LOCATION, result[8].toString()));
				params.add(new BasicNameValuePair(EXISTS, result[9].toString()));
				params.add(new BasicNameValuePair(LASTMODIFIED, result[10].toString()));  

				// getting JSON Object
				// Note that create user url accepts POST method
				JSONObject json = jsonParser.makeHttpRequest(test_url,"POST", params);

				// check log cat fro response
				Log.d("Create Response", json.toString());

				// check for success tag
				try {
					int success = json.getInt(TAG_SUCCESS);

					if (success == 1) {
						// successfully created product
						thisDB.execSQL("UPDATE "+ CHILDREN_TABLE +" SET "+ EXISTS +"='True' WHERE "+ CHILD_ID+ "='" + result[0].toString() +"'");
					} 
					else if(success == 2){
						String where = CHILD_ID+"= ?";  
						String[] whereValues = new String[]{ result[0].toString()};    
						thisDB.delete(CHILDREN_TABLE, where, whereValues);
					}
					else if(success==3){

					}
					// RemoteDB is newer
					else if(success==4){
						JSONArray test=json.getJSONArray(CHILDREN_TABLE);
						updateLocalDBUser(test);

					}
					else {
						// failed to create product
					}
				} catch (JSONException e) {
					Log.d("JSON Exception", e.toString());
					e.printStackTrace();
				}


			}
			getChildrenTableFromRemoteDB();
			return null;
		}

	}
	private void updateLocalDBUser(JSONArray test){
		JSONObject c;
		try {
			// Storing each json item in variable
			c = test.getJSONObject(0);

			String child_id = c.getString(CHILD_ID);
			String firstname = c.getString(CHILD_FName);
			String lastname = c.getString(CHILD_LName);
			String datebirth= c.getString(CHILDREN_BIRTH);
			String gender = c.getString(CHILDREN_GENDER);
			String address = c.getString(CHILD_ADDRESS);
			String postal = c.getString(CHILDREN_POSTAL);
			String phonenum = c.getString(CHILDREN_PHONE);
			String location = c.getString(CHILDREN_LOCATION);
			String existsinremotedb = c.getString(EXISTS);
			String lastmodified=c.getString(LASTMODIFIED);

			String sqlUpdate="UPDATE "+ CHILDREN_TABLE +" SET "
					+CHILD_FName+"='"+firstname+"'"
					+","+CHILD_LName+"='"+lastname+"'"
					+","+CHILDREN_BIRTH+"='"+datebirth+"'"
					+","+CHILDREN_GENDER+"='"+gender+"'"
					+","+CHILD_ADDRESS+"='"+address+"'"
					+","+CHILDREN_POSTAL+"='"+postal+"'"
					+","+CHILDREN_PHONE+"='"+phonenum+"'"
					+","+CHILDREN_LOCATION+"='"+location+"'"
					+","+EXISTS +"='"+existsinremotedb+"'"
					+","+LASTMODIFIED+"='"+lastmodified+"'"
					+" WHERE "+ CHILD_ID+ "='"+child_id+"'";
			thisDB.execSQL(sqlUpdate);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void syncInstitutionsTable(){
		thisDB=this.getWritableDatabase();
		syncInstitutionsTableTask task = new syncInstitutionsTableTask();
		task.execute();
	}

	private class syncInstitutionsTableTask extends AsyncTask<String, String, String>{

		private static final String test_url = "http://cmpt371g2.usask.ca:8084/syncInstitutions.php";

		/*
		 * Creating users
		 */
		JSONParser jsonParser = new JSONParser();
		protected String doInBackground(String... args) {
			String query = "SELECT * FROM "+INSTI_TABLE;
			Cursor cursor = thisDB.rawQuery(query, null);
			String[] result = new String[7];
			while(cursor.moveToNext()){

				result[0]=cursor.getString(0);
				result[1]=cursor.getString(1);
				result[2]=cursor.getString(5);
				result[3]=cursor.getString(3);
				result[4]=cursor.getString(4);
				result[5]=cursor.getString(5);
				result[6]=cursor.getString(6);


				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair(INSTI_ID, result[0].toString()));
				params.add(new BasicNameValuePair(INSTI_name, result[1].toString()));
				params.add(new BasicNameValuePair(INSTI_address, result[2].toString()));
				params.add(new BasicNameValuePair(INSTI_phoneNum, result[3].toString()));
				params.add(new BasicNameValuePair(INSTI_Descipt, result[4].toString()));
				params.add(new BasicNameValuePair(EXISTS, result[5].toString()));
				params.add(new BasicNameValuePair(LASTMODIFIED, result[6].toString()));  

				// getting JSON Object
				// Note that create user url accepts POST method
				JSONObject json = jsonParser.makeHttpRequest(test_url,"POST", params);

				// check log cat fro response
				Log.d("Create Response", json.toString());

				// check for success tag
				try {
					int success = json.getInt(TAG_SUCCESS);

					if (success == 1) {
						// successfully created product
						thisDB.execSQL("UPDATE "+ INSTI_TABLE +" SET "+ EXISTS +"='True' WHERE "+ INSTI_ID+ "='" + result[0].toString() +"'");
					} 
					else if(success == 2){
						String where = INSTI_ID+"= ?";  
						String[] whereValues = new String[]{ result[0].toString()};    
						thisDB.delete(INSTI_TABLE, where, whereValues);
					}
					else if(success==3){

					}
					// RemoteDB is newer
					else if(success==4){
						JSONArray test=json.getJSONArray(CHILDREN_TABLE);
						updateLocalDBInstitution(test);

					}
					else {
						// failed to create product
					}
				} catch (JSONException e) {
					Log.d("JSON Exception", e.toString());
					e.printStackTrace();
				}


			}
			getInsitutionTableFromRemoteDB();
			return null;
		}

	}
	private void updateLocalDBInstitution(JSONArray test){
		JSONObject c;
		try {
			// Storing each json item in variable
			c = test.getJSONObject(0);

			String institution_id = c.getString(INSTI_ID);
			String institution_name = c.getString(INSTI_name);
			String institution_address = c.getString(INSTI_address);
			String institution_phonenum= c.getString(INSTI_phoneNum);
			String institution_description = c.getString(INSTI_Descipt);
			String existsinremotedb = c.getString(EXISTS);
			String lastmodified=c.getString(LASTMODIFIED);

			String sqlUpdate="UPDATE "+ INSTI_TABLE +" SET "
					+INSTI_name+"='"+institution_name+"'"
					+","+INSTI_address+"='"+institution_address+"'"
					+","+INSTI_phoneNum+"='"+institution_phonenum+"'"
					+","+INSTI_Descipt+"='"+institution_description+"'"
					+","+EXISTS +"='"+existsinremotedb+"'"
					+","+LASTMODIFIED+"='"+lastmodified+"'"
					+" WHERE "+ INSTI_ID+ "='"+institution_id+"'";
			thisDB.execSQL(sqlUpdate);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Author Spencer
	 * Keep formating 
	 * you make export to excel is extremely easily
	 */
	public void addSurvey(String surv_id_resp, String surv_id_loca, String surv_id_rese, int[] surv_q, int surv_score,String surv_date_end){

		thisDB=this.getWritableDatabase();
		ContentValues surveyData = new ContentValues();

		surveyData.put(SURV_RESP_ID, surv_id_resp);
		System.out.println(surv_id_resp+"input id is");
		surveyData.put(SURV_ID_LOC, surv_id_loca);  
		surveyData.put(SURV_ID_RESEARCHER, surv_id_rese);  
		surveyData.put(SURV_Q1, surv_q[0]);
		surveyData.put(SURV_Q2, surv_q[1]);
		surveyData.put(SURV_Q3, surv_q[2]);
		surveyData.put(SURV_Q4, surv_q[3]);
		surveyData.put(SURV_Q5, surv_q[4]);
		surveyData.put(SURV_Q6, surv_q[5]);
		surveyData.put(SURV_Q7, surv_q[6]);
		surveyData.put(SURV_Q8, surv_q[7]);
		surveyData.put(SURV_Q9, surv_q[8]);
		surveyData.put(SURV_Q10, surv_q[9]);
		surveyData.put(SURV_Q11, surv_q[10]);
		surveyData.put(SURV_Q12, surv_q[11]);
		surveyData.put(SURV_Q13, surv_q[12]);
		surveyData.put(SURV_Q14, surv_q[13]);
		surveyData.put(SURV_Q15, surv_q[14]);
		surveyData.put(SURV_Q16, surv_q[15]);
		surveyData.put(SURV_Q17, surv_q[16]);
		surveyData.put(SURV_Q18, surv_q[17]);
		surveyData.put(SURV_Q19, surv_q[18]);
		surveyData.put(SURV_Q20, surv_q[19]);
		surveyData.put(SURV_SCORE, surv_score);
		surveyData.put(SURV_DATE_END, surv_date_end);

		thisDB.insert(SURV_TABLE, null, surveyData);  
		thisDB.close();

	}
	
	/**
	 * @author Xingze
	 * @param id: child id
	 * @return an arrayList of all survey data for a child
	 * get all location in DB, return an arrayList  
	 */
	public ArrayList<HashMap<String,Object>> getSurveyForAChild(String id){

		ArrayList<HashMap<String,Object>> surveyResult = new ArrayList<HashMap<String,Object>>();
		Cursor DBcursor = this.getWritableDatabase().rawQuery("Select " + SURV_ID_LOC +","
				+ SURV_ID_RESEARCHER +","
				+ SURV_DATE_END +","
				+ SURV_Q1 +","
				+ SURV_Q2 +","
				+ SURV_Q3 +","
				+ SURV_Q4 +","
				+ SURV_Q5 +","
				+ SURV_Q6 +","
				+ SURV_Q7 +","
				+ SURV_Q8 +","
				+ SURV_Q9 +","
				+ SURV_Q10 +","
				+ SURV_Q11 +","
				+ SURV_Q12 +","
				+ SURV_Q13 +","
				+ SURV_Q14 +","
				+ SURV_Q15 +","
				+ SURV_Q16 +","
				+ SURV_Q17 +","
				+ SURV_Q18 +","
				+ SURV_Q19 +","
				+ SURV_Q20 +","
				+ SURV_SCORE
				+ " from " + SURV_TABLE+ " WHERE " + SURV_RESP_ID+ "='"+id+"'",null);
		while(DBcursor.moveToNext()){	
			String locID = DBcursor.getString(0);
			String resID = DBcursor.getString(1);	
			String endDate = DBcursor.getString(2);
			String q1 = DBcursor.getString(3);
			String q2 = DBcursor.getString(4);
			String q3 = DBcursor.getString(5);
			String q4 = DBcursor.getString(6);
			String q5 = DBcursor.getString(7);
			String q6 = DBcursor.getString(8);
			String q7 = DBcursor.getString(9);
			String q8 = DBcursor.getString(10);
			String q9 = DBcursor.getString(11);
			String q10 = DBcursor.getString(12);
			String q11 = DBcursor.getString(13);
			String q12 = DBcursor.getString(14);
			String q13 = DBcursor.getString(15);
			String q14 = DBcursor.getString(16);
			String q15 = DBcursor.getString(17);
			String q16 = DBcursor.getString(18);
			String q17 = DBcursor.getString(19);
			String q18 = DBcursor.getString(20);
			String q19 = DBcursor.getString(21);			
			String q20 = DBcursor.getString(22);
			String score = DBcursor.getString(23);

			HashMap<String,Object> oneSurvey = new HashMap<String,Object>();

			//set data in hash map
			oneSurvey.put("locID", locID);
			oneSurvey.put("resID", resID);
			oneSurvey.put("endDate", endDate);
			oneSurvey.put("q1", q1);
			oneSurvey.put("q2", q2);
			oneSurvey.put("q3", q3);
			oneSurvey.put("q4", q4);
			oneSurvey.put("q5", q5);
			oneSurvey.put("q6", q6);
			oneSurvey.put("q7", q7);
			oneSurvey.put("q8", q8);
			oneSurvey.put("q9", q9);
			oneSurvey.put("q10", q10);
			oneSurvey.put("q11", q11);
			oneSurvey.put("q12", q12);
			oneSurvey.put("q13", q13);
			oneSurvey.put("q14", q14);
			oneSurvey.put("q15", q15);
			oneSurvey.put("q16", q16);
			oneSurvey.put("q17", q17);
			oneSurvey.put("q18", q18);
			oneSurvey.put("q19", q19);
			oneSurvey.put("q20", q20);
			oneSurvey.put("score", score);

			surveyResult.add(oneSurvey);

		}
		DBcursor.close(); 
		this.getWritableDatabase().close();		
		return surveyResult; 

	}

	private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
	}
	
	/**
	 * @author Xingze
	 * @param childID
	 * @return location id
	 */
	protected String getLocationByChildID(String childID){
		String childLocation = "";
		Cursor DBcursor = this.getWritableDatabase().rawQuery("Select " +CHILDREN_LOCATION
				+ " from " + CHILDREN_TABLE+ " WHERE "+CHILD_ID + " = '" +childID+"'" ,null);
		while(DBcursor.moveToNext()){	
			childLocation = DBcursor.getString(0);
		}
		return childLocation;
	}
	
	/**
	 * @author Xingze
	 * @param locationID
	 * @return location name of this id
	 */
	protected String getLocationNameByID(String locationID){
		String childLocation = "";
		Cursor DBcursor = this.getWritableDatabase().rawQuery("Select " +INSTI_name
				+ " from " + INSTI_TABLE+ " WHERE "+INSTI_ID + " = '" +locationID+"'" ,null);
		while(DBcursor.moveToNext()){
			childLocation = DBcursor.getString(0);
		}
		return childLocation;
	}
	
	/**
	 * @author Xingze
	 * @param researcher ID
	 * @return researcher name of this id
	 */
	protected String getResNameByID(String resID){
		String fullName = "";
		Cursor DBcursor = this.getWritableDatabase().rawQuery("Select " + USER_FIRSTNAME +","
				+ USER_LASTNAME
				+ " from " + USERS_TABLE+ " WHERE " + USER_NAME+ "='" +resID+"'",null);
		while(DBcursor.moveToNext()){
			String firstName = DBcursor.getString(0);
			String lastName = DBcursor.getString(1);
			fullName = firstName + " " + lastName;
		}
		return fullName;
	}
}



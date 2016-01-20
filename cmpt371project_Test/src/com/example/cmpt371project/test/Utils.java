package com.example.cmpt371project.test;

import com.example.cmpt371project.LocalDB;
import com.robotium.solo.Solo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.EditText;
import android.widget.SearchView;

/**
 * Utilities for testing.
 * 
 * File: Utils.java<br>
 * Purpose: provide utility functions for testing..<br>
 * Features Completed:<br>
 * <ul>
 * 	<li>Get application's database</li>
 * 	<li>Enter a group of values to EditViews</li>
 * 	<li>Clear a group of EditViews</li>
 * 	<li>Extract EditView from SearchView</li>
 * 	<li>Remove a user record by his username</li>
 * 	<li>Remove a child record by his first name</li>
 * 	<li>Generate random string</li>
 * 	<li>Get the tables in application's database</li>
 * 	<li>Create a random user detail</li>
 * </ul>
 * Features Incompleted:<br>
 * <ul>
 * 	<li>Create a random child detail</li>
 *  <li>Build more flexible remove function.</li>
 * </ul>
 * Dependencies:<br>
 * <ul>
 * 	<li>Robotium Framework</li>
 * </ul>
 * Known Bugs:<br>
 * <ul>
 * 	<li>None.</li>
 * </ul>
 * Notice:<br>
 * This class contains a data structure for user details.
 */
public class Utils{	
	
	/**
	 * Alphabet for generating random string.
	 */
	private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	
	/**
	 * Numbers for generating random string.
	 */
	private static final String NUMBERS = "0123456789";
	
	/**
	 * Special characters for generating random string.
	 */
	private static final String SPECIAL_CHARACTERS = "~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
	
	/**
	 * Source for generating a string with alphabet and numbers.
	 */
	private static final String ALPHABET_AND_NUMBERS = Utils.ALPHABET + Utils.NUMBERS;
	
	/**
	 * Source for generating a string with alphabets and special characters.
	 */
	private static final String ALPHABET_AND_SPECIALS = Utils.ALPHABET + Utils.SPECIAL_CHARACTERS;
	
	/**
	 * Source for generating a string with numbers and special characters.
	 */
	private static final String NUMBERS_AND_SPECIALS = Utils.NUMBERS + Utils.SPECIAL_CHARACTERS;
	
	/**
	 * Source for generating a string with alphabets, numbers and special characters.
	 */
	private static final String ALPHABET_NUMBERS_SPECIALS = Utils.ALPHABET + Utils.NUMBERS + Utils.SPECIAL_CHARACTERS;
	
	/**
	 * Returns writable SQLite database by given context.
	 * @param context Current context to get the database.
	 * @return A writable SQLite Database.
	 */
	public static SQLiteDatabase getSQLiteDatabaseByContext(Context context) {
		SQLiteDatabase db = new LocalDB(context).getWritableDatabase();
		return db;
	}
	
	/**
	 * Enter the given texts to given EditViews.
	 * The count of texts must be equal to the count of the EditViews. Otherwise an IllegalArgumentException will be thrown.
	 * The first text will be entered in the first EditView.
	 * The second text will be entered in the second EditView, and so on.
	 * @param solo The solo instance from test case.
	 * @param texts Texts to be entered.
	 * @param views EditViews to be filled.
	 */
	public static void enterTextToEditView(Solo solo, String[] texts, EditText[] views) {
		if (texts.length != views.length) {
			throw new IllegalArgumentException("Texts and EditViews must have the same connts.");
		}
		for (int i = 0; i < texts.length; i++) {
			solo.enterText(views[i], texts[i]);
		}
	}
	
	/**
	 * Clear given EditViews.
	 * @param solo The solo instance from test case.
	 * @param views EditViews to be cleared.
	 */
	public static void clearEditViews(Solo solo, EditText[] views) {
		for (int i = 0; i < views.length; i++) {
			solo.clearEditText(views[i]);
		}
	}
	
	/**
	 * Extract the EditText from given SearchView.
	 * The identifier is hard-encoded as "android:id/search_src_text"
	 * @param solo The solo instance from test case.
	 * @param sv The SearchView.
	 * @return The EditText in SearchView.
	 */
	public static EditText extractFromSearchView(Solo solo, SearchView sv) {
		int etId = sv.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
		return (EditText) solo.getView(etId);
	}
	
	/**
	 * Remove a test record in user database.
	 * This method is based on {@linkplain Utils.getSQLiteDatabaseByContext}
	 * @param context Current context to get the database.
	 * @param username The username of the record to be removed.
	 */
	public static void removeUserTestRecord(Context context, String username) {
		// Remove test row.
		Log.d(Constants.TAG, "Clean user database:");
		int recordRemoved = removeRecordFromDatabase(context, username, LocalDB.USERS_TABLE, LocalDB.USER_NAME);
		String message = "Removed " + recordRemoved + " record(s)";
		Log.d(Constants.TAG, message);
	}
	
	/**
	 * Remove a test record in children database.
	 * This method is based on {@linkplain Utils.getSQLiteDatabaseByContext}
	 * @param context Current context to get the database.
	 * @param username The child's first name of the record to be removed.
	 */
	public static void removeChildTestRecord(Context context, String childFirstName) {
		// Remove test row.
		Log.d(Constants.TAG, "Clean children database:");	
		int recordRemoved = removeRecordFromDatabase(context, childFirstName, LocalDB.CHILDREN_TABLE, LocalDB.CHILD_FName);
		String message = "Removed " + recordRemoved + " record(s)";
		Log.d(Constants.TAG, message);
	}
	
	/**
	 * Remove a test record in location database.
	 * This method is based on {@linkplain Utils.getSQLiteDatabaseByContext}
	 * @param context Current context to get the database.
	 * @param username The child's first name of the record to be removed.
	 */
	public static void removeLocationTestRecord(Context context, String locName) {
		// Remove test row.
		Log.d(Constants.TAG, "Clean location database:");	
		int recordRemoved = removeRecordFromDatabase(context, locName, LocalDB.INSTI_TABLE, LocalDB.INSTI_name);
		String message = "Removed " + recordRemoved + " record(s)";
		Log.d(Constants.TAG, message);
	}
	
	/**
	 * Generate a random string.
	 * @param length The random string's length.
	 * @param includeAlphabet Should the string include alphabets.
	 * @param includeSpecial Should the string include special characters.
	 * @param includeNumber Should the string include numbers.
	 * @return a random string
	 * @throws IllegalArgumentException if all options are false.
	 */
	public static String generateRandomString(int length,boolean includeAlphabet, boolean includeSpecial, boolean includeNumber) 
			throws IllegalArgumentException{
		StringBuilder sb = new StringBuilder();
		String source;
		if (includeAlphabet && includeNumber && includeSpecial) {
			source = Utils.ALPHABET_NUMBERS_SPECIALS;
		}
		else if (includeAlphabet && includeNumber) {
			source = Utils.ALPHABET_AND_NUMBERS;
		}
		else if (includeAlphabet && includeSpecial) {
			source = Utils.ALPHABET_AND_SPECIALS;
		}
		else if (includeNumber && includeSpecial) {
			source = Utils.NUMBERS_AND_SPECIALS;
		} 
		else if (includeAlphabet) {
			source = Utils.ALPHABET;
		}
		else if (includeNumber) {
			source = Utils.NUMBERS;
		}
		else if (includeSpecial) {
			source = Utils.SPECIAL_CHARACTERS;
		}
		else {
			throw new IllegalArgumentException("You should at least specify one option.");
		}
		for (int i = 0; i < length; i++) {
			int randomIndex = (int) Math.floor(Math.random() * source.length());
			sb.append(source.substring(randomIndex, randomIndex + 1));
		}
		return sb.toString();
	}
	
	/**
	 * Return a {@link Cursor}, which contains all records in specified table.
	 * This method invokes {@linkplain Utils.getSQLiteDatabaseByContext} to get and query database.
	 * @param context Current context to get the database.
	 * @param tableName The table's name.
	 * @return A {@link Cursor} contains all records in specified table.
	 */
	public static Cursor getTableInDatabase(Context context, String tableName) {
		SQLiteDatabase db = Utils.getSQLiteDatabaseByContext(context);
		return db.query(tableName, null, null, null, null, null, null);
		
	}
	
	/**
	 * Create a {@linkplain Utils.userDetail} class to use.
	 * All the fields are final.
	 * The returned class contains:
	 * User name: 4 characters
	 * Password: 4 characters
	 * PhoneNum: 10 digits, if needed.
	 * First name: 4 characters
	 * Last name: 4 characters
	 * @param isPhoneNumNeeded Should the class have a phone number.
	 * @return A {@linkplain Utils.userDetail} class as described above.
	 */
	public static UserDetail createRandomUserDetail(boolean isPhoneNumNeeded) {
		String test_FirstName = Utils.generateRandomString(4, true, false, false);
		String test_LastName = Utils.generateRandomString(4, true, false, false);
		String test_PhoneNum = Utils.generateRandomString(10, false, false, true);
		String test_Username = Utils.generateRandomString(4, true, false, false);
		String test_Password = Utils.generateRandomString(4, true, false, false);
		if (!isPhoneNumNeeded) {
			test_PhoneNum = null;
		}
		return new Utils().new UserDetail(test_Username, test_Password, test_PhoneNum, test_FirstName, test_LastName);
	}
	
	
	
	/**
	 * Internal method: this method is to execute record removal.
	 * @param context Current context to get the database.
	 * @param keyword The keyword of the record.
	 * @param tableName The table name.
	 * @param columnName The keyword's column's name.
	 * @return The count of removed rows.
	 */
	private static int removeRecordFromDatabase(Context context, String keyword, String tableName, String columnName) {
		SQLiteDatabase db = Utils.getSQLiteDatabaseByContext(context);
		int recordRemoved = db.delete(tableName, columnName + " = '" + keyword + "'", null);
		db.close();
		return recordRemoved;
	}
	
	/**
	 * Data class for user details.
	 * @author Da Tao
	 *
	 */
	public final class UserDetail{
		
		/**
		 * Username
		 */
		public final String username;
		/**
		 * User's password
		 */
		public final String password;
		/**
		 * User's phone number
		 */
		public final String phoneNum;
		/**
		 * User's first name
		 */
		public final String firstName;
		/**
		 * User's last name
		 */
		public final String lastName;
		
		/**
		 * Construct a user by given username, password, phone number, first name and last name.
		 * @param username Username
		 * @param password User's password
		 * @param phoneNum User's phone number
		 * @param firstName User's first name
		 * @param lastName User's last name
		 */
		public UserDetail(String username, String password, String phoneNum,
				String firstName, String lastName) {
			super();
			this.username = username;
			this.password = password;
			this.phoneNum = phoneNum;
			this.firstName = firstName;
			this.lastName = lastName;
		}
	}


}

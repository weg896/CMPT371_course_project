package com.example.cmpt371project.test;
/**
 * Tests for children list UI.
 * All the tests run in admin mode.
 */
import java.util.ArrayList;
import java.util.HashMap;

import com.example.cmpt371project.LocalDB;
import com.example.cmpt371project.R;
import com.example.cmpt371project.childrenEdit;
import com.example.cmpt371project.childrenList;
import com.example.cmpt371project.simpleListAdapter;
import com.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;

public class ChildrenListTest extends
		ActivityInstrumentationTestCase2<childrenList> {
			
	private Solo solo;
	// Variables for widgets in ChildrenList UI.
	private Button locationButton;
	private Button addButton;
	private ListView listView;;
	private SearchView  searchView;
	private int clickPosition;
	private ArrayList<String> listContent = new ArrayList<String>(); 
	private HashMap<String,Object> clickItem;  //the item be clicked in the list 
	private LocalDB childDB;
	private simpleListAdapter thisAdapter;
	private HashMap<String,Object> listItem;
	private String[] testValues1;
	private String[] testValues2;

	public ChildrenListTest() {
		super(childrenList.class);
	}

	@Override
	protected void setUp() throws Exception {

		solo = new Solo(getInstrumentation(), getActivity());
		super.setUp();
		addButton = (Button) solo.getView(R.id.actionbar_addButton);
		listView = (ListView)solo.getView(R.id.alCh_chil_lst);
		locationButton=(Button)solo.getView(R.id.alCh_loca_but);
        searchView = (SearchView) solo.getView(R.id.actionbar_searchView);
        testValues1 = new String[] {"John", "Smith", "0000000001", "NeverLand 25", "1999-01-01","Male", "S7H5J7", "UofS"};
  		testValues2 = new String[] {"Mark", "Smith", "0000000001", "NeverLand 25", "1999-01-01","Male", "S7H5J7", "UofS"};

	}
	
	/**
	 * test add children and search field
	 */
	public void testAddAndSearchChildren(){

		Log.d(Constants.TAG, "Add a Child.");
		addOneChild(testValues1);
		Log.d(Constants.TAG, "Add a Child.");
		addOneChild(testValues2);
		Log.d(Constants.TAG, "Begin Test: Search Only One Matching Child:");
		SearchCheck("John",1);
		Log.d(Constants.TAG, "Begin Test: Search More Than One Matching Children:");
		SearchCheck("Smith",2);

		assertFalse("Should not find anything in the list.", solo.searchText("外国人看不懂", 1, true, true));
		
		//TODO
		//need to delete this child. but this function not work yet.
	}
	/**
	 * test LongPress on a Children button
	 */
	public void testLongPressChildren(){
		//TODO
		//function not implentment yet.
	}
	/**
	*	check if there are right number of string in screen.
	*/
	private void SearchCheck(String nameExpect, int numberExpect){
		assertTrue("There should be "+numberExpect+" results expected on screen.", solo.searchText(nameExpect, numberExpect, true, true));

	}

	/**
	*Returns the EditViews to be filled in ChildrenEdit UI.
	*/
	private EditText[] getEditTexts() {
		EditText[] views = {
				(EditText) solo.getView(R.id.edCh_firs_txt),
				(EditText) solo.getView(R.id.edCh_last_txt),
				(EditText) solo.getView(R.id.edCh_phon_txt),
				(EditText) solo.getView(R.id.edCh_addr_txt),
				(EditText) solo.getView(R.id.edCh_birth_txt),
				(EditText) solo.getView(R.id.edCh_gender_txt),
				(EditText) solo.getView(R.id.edCh_post_txt),
				(EditText) solo.getView(R.id.edCh_location_txt),
		};
		return views;
	}
	/**
	 * Add a child.
	 * @param values The details of the child. 
	 */
	private void addOneChild(String[] values) {
		solo.clickOnView(addButton);
		assertTrue("ERR - Could not jump to Add Children screen", solo.waitForActivity(childrenEdit.class));
		
		Log.d(Constants.TAG, "Filling in new child information.");
		EditText[] views = getEditTexts();		
		Utils.enterTextToEditView(solo, values, views);
		
		Log.d(Constants.TAG, "Save child information.");
		solo.clickOnView(solo.getView(R.id.edRe_save_but));
		solo.hideSoftKeyboard();
		solo.goBack();
	}
	@Override
	protected void tearDown() throws Exception{

			solo.finishOpenedActivities();
	}
}

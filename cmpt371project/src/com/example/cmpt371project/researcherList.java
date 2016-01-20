//File: researcherList.java
//Purpose: Displays a list of all users that are in the database only to admins
//Features Completed: List display correctly and can select a researcher
//Features Incomplete: None
//Dependencies: admin.java
//Known Bugs: None
//General Comments: None

package com.example.cmpt371project;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SearchView.OnQueryTextListener;

import com.example.cmpt371project.R;

public class researcherList extends Activity implements SearchView.OnQueryTextListener{
private Button addButton;
private SearchView  searchView;
private ListView listView;
private int clickPosition;
private ArrayList<String> listContent = new ArrayList<String>();  
private HashMap<String,Object> clickItem;  //the item be clicked in the list 
private LocalDB resDB;
private simpleListAdapter thisAdapter;
private HashMap<String,Object> listItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_researchers_list);
		resDB = new LocalDB(this);
		
		
//		ArrayList<HashMap<String,Object>> testRes = resDB.getAllUsers();
	
		initActionbar();  //for acthion bar	    
		listView = (ListView) findViewById(R.id.alRe_researcher_lst);	        
		addButton = (Button) findViewById(R.id.actionbar_addButton);
		thisAdapter = this.initializeListAdapter();
		
		//set listView
		listView.setAdapter(thisAdapter);
		listView.setTextFilterEnabled(true);
		
		//set on click
		listView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) {
				//read which item be clicked
				int itemId = (int)id;
				listItem = (HashMap<String, Object>) thisAdapter.getItem(itemId);
				
		       	//go to researcher edit
				//put list items in extra and give it to edit activity
				Intent viewIntent = new Intent();
				viewIntent.setClass(researcherList.this, researcherEdit.class);
	       	
				viewIntent.putExtra("from", "res_view");
				
				viewIntent.putExtra("userID", (String) listItem.get("userID"));
				viewIntent.putExtra("userPassword", (String) listItem.get("userPassword"));
				viewIntent.putExtra("userFirstName", (String) listItem.get("userFirstName"));
				viewIntent.putExtra("userLastName", (String) listItem.get("userLastName"));
				viewIntent.putExtra("userPhoneNum", (String) listItem.get("userPhoneNum"));
	       	
				researcherList.this.startActivity(viewIntent);
				finish();
			}
		});
		
		//set searchView
		searchView.setOnQueryTextListener(this); 
		searchView.setSubmitButtonEnabled(false);  

		
		addButton.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				//go to add activity
		       	 Intent addIntent = new Intent();
		       	 addIntent.putExtra("from", "res_add");
		       	 addIntent.setClass(researcherList.this, researcherEdit.class);
		       	 researcherList.this.startActivity(addIntent);
		       	 finish();
			}});
	}
	
	/**
	 * @author Xingze
	 * initialize Actionbar
	 */
    private void initActionbar() {  
        getActionBar().setDisplayShowHomeEnabled(false);  
        getActionBar().setDisplayShowTitleEnabled(false);  
        getActionBar().setDisplayShowCustomEnabled(true);  
        LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        View mTitleView = mInflater.inflate(R.layout.actionbar_search,  
                null);  
        getActionBar().setCustomView(  
                mTitleView,  
                new ActionBar.LayoutParams(LayoutParams.MATCH_PARENT,  
                        LayoutParams.WRAP_CONTENT));  
        searchView = (SearchView) mTitleView.findViewById(R.id.actionbar_searchView);  
    }  
    
/**
 * @author Xingze
 * @return simpleListAdapter 
 * initial list adapter
 */
	private simpleListAdapter  initializeListAdapter(){
		ArrayList<HashMap<String,Object>> listItem = new ArrayList<HashMap<String,Object>>();
		listItem = resDB.getAllUsers();
		
		simpleListAdapter newAdapter = new simpleListAdapter(this, listItem, R.layout.list_for_name, 
				new String[]{"userFirstName","userLastName"}, new int[]{R.id.list_for_name_first,R.id.list_for_name_last});

		return newAdapter;		
	}

    @Override  
    public boolean onQueryTextChange(String newText) {  
        if (TextUtils.isEmpty(newText)) {  
            // Clear the text filter.  
            listView.clearTextFilter();  
        } else {  
            // Sets the initial value for the text filter.  
            listView.setFilterText(newText.toString());  
        }  
        return false;  
    }  

	@Override
	public boolean onQueryTextSubmit(String query) {
		return false;
	}
	
}




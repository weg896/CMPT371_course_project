//File: locationList.java
//Purpose: Displays a list of all institutions to the user.
//Features Completed: Able to retrieve a list of all institutions and each institution can be selected to be edited
//Features Incomplete None based on client's requirements.
//Dependencies: locationEdit.java
//Known Bugs: On the location list screen, when location is selected and click "edit",then click "remove" and go back it is still there
//On location list screen, after add a new location, then click that location, the phone number and the address information are in wrong text fields

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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.AdapterView.OnItemClickListener;
import com.example.cmpt371project.R;

public class locationList extends Activity implements SearchView.OnQueryTextListener{

private Button addButton;
private SearchView  searchView;
private ListView listView;
private int clickPosition;
private ArrayList<String> listContent = new ArrayList<String>();  
private HashMap<String,Object> clickItem;  //the item be clicked in the list 
private LocalDB locationDB;
private simpleListAdapter thisAdapter;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_list);
		locationDB = new LocalDB(this);
		
		initActionbar();  //for acthion bar	
		listView=(ListView)findViewById(R.id.alLo_Loca_lst);
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
				HashMap<String,Object> listItem = (HashMap<String, Object>) thisAdapter.getItem(itemId);
							
		       	//go to location edit activity
		       	Intent viewIntent = new Intent();
		       	viewIntent.setClass(locationList.this, locationEdit.class);
		       	
		       	viewIntent.putExtra("from", "location_view");
		       	       	
		       	viewIntent.putExtra("itemID", (String)listItem.get("locationID"));
		       	viewIntent.putExtra("name", (String)listItem.get("locationName"));
		       	viewIntent.putExtra("address", (String)listItem.get("locationAddress"));
		       	viewIntent.putExtra("phone", (String)listItem.get("locationPhoneNum"));
		       	viewIntent.putExtra("des", (String)listItem.get("locationDescipt"));
		       	
		       	locationList.this.startActivity(viewIntent);
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
		       	 addIntent.putExtra("from", "location_add");
		       	 addIntent.setClass(locationList.this, locationEdit.class);
		       	locationList.this.startActivity(addIntent);
				finish();
			}});
		
	}

	/**
	 * @author Xingze
	 * @return simpleListAdapter 
	 * initial list adapter
	 */
		private simpleListAdapter  initializeListAdapter(){
			ArrayList<HashMap<String,Object>> listItem = new ArrayList<HashMap<String,Object>>();
			listItem = locationDB.getAllLocation();
			
			simpleListAdapter newAdapter = new simpleListAdapter(this, listItem, R.layout.list, 
					new String[]{"locationName"}, new int[]{R.id.list_textview});

			return newAdapter;		
		}

	
	/**
	 * @author Xingze
	 * initialize Actionbar
	 */
    public void initActionbar() {  
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
		// TODO Auto-generated method stub
		return false;
	}  
}

//File: childrenList.java
//Purpose: Displays a list of all children that are in the database
//Features Completed: List display correctly and can select a child
//Features Incomplete: None
//Dependencies: admin.java, researcher.java
//Known Bugs: None
//General Comments: None

package com.example.cmpt371project;

import com.example.cmpt371project.R;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;



public class childrenList extends Activity implements SearchView.OnQueryTextListener{
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
private Bundle mBundle; //for get value from intent

//true if researcher is usering this gui, otherwise false
private boolean isResearcher;

/**
 * The dialog currently shown to the user. A reference is kept to this 
 * instance so it can easily be cancelled and referenced after it is created.
 */
private Dialog currentlyShownDialog;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		childDB = new LocalDB(this);
		setContentView(R.layout.activity_children_list);
		initActionbar();  //for acthion bar	
		//TODO modify location button
		locationButton=(Button)findViewById(R.id.alCh_loca_but);
		//
		addButton = (Button) findViewById(R.id.actionbar_addButton);
		listView = (ListView)findViewById(R.id.alCh_chil_lst);
		thisAdapter = this.initializeListAdapter(this.readAllLocation());
		
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
		
		//list adapter
//		final simpleListAdapter thisAdapter = this.initializeListAdapter();
		listView.setAdapter(thisAdapter);
		
		//set on click
		listView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) {
				//read which item be clicked
				int itemId = (int)id;
				listItem = (HashMap<String, Object>) thisAdapter.getItem(itemId);
				
//				this.getListItem

		       	//go to Child view
				//put list items in extra and give it to edit activity
				goToChildView();
			}
		});
		
		//set searchView
		searchView.setOnQueryTextListener(this); 
		searchView.setSubmitButtonEnabled(false);
		
		///
		locationButton.setOnClickListener(new View.OnClickListener(){
	       	
			@Override
			public void onClick(View v) {
				
				selectLocationPopup();
		       	 	
			}});
		
		addButton.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent addIntent = new Intent();
				addIntent.putExtra("from", "children_add");
				addIntent.putExtra("isResearcher", String.valueOf(isResearcher));
				addIntent.setClass(childrenList.this, childrenEdit.class);
				childrenList.this.startActivity(addIntent);
				finish();
			}});
	}

	
	/**
	 * @author Xingze
	 * @return simpleListAdapter 
	 * initial list adapter
	 */
		private simpleListAdapter  initializeListAdapter(ArrayList<HashMap<String,Object>> listItem){

			
			simpleListAdapter newAdapter = new simpleListAdapter(this, listItem, R.layout.list_for_name, 
					new String[]{"childFName","childLName"}, new int[]{R.id.list_for_name_first,R.id.list_for_name_last});

			return newAdapter;		
		}
	
	/**
	 * @author Xingze
	 * read children list at all location
	 */
		
		private ArrayList<HashMap<String,Object>> readAllLocation(){
			ArrayList<HashMap<String,Object>> listItem = new ArrayList<HashMap<String,Object>>();
			listItem = childDB.getAllChildren();
			return listItem;
		}
		
	/**
	 * @author Xingze
	 * read children list at selected location
	 */
			
		private ArrayList<HashMap<String,Object>> readSelectedLocation(String aLocation){
				ArrayList<HashMap<String,Object>> listItem = new ArrayList<HashMap<String,Object>>();
				listItem = childDB.getAllChildrenAtLocation(aLocation);
				return listItem;
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
    
    /**
     * @author Xingze
     * go to childEdit activity
     */
    private void goToChildView(){
		Intent viewIntent = new Intent();
		viewIntent.setClass(childrenList.this, childrenEdit.class);
   	
		viewIntent.putExtra("from", "children_view");
		
		viewIntent.putExtra("UserID", (getIntent().getStringExtra("UserID")));
		viewIntent.putExtra("isResearcher", String.valueOf(isResearcher));
		viewIntent.putExtra("childID", (String) listItem.get("childID"));
		viewIntent.putExtra("childFName", (String) listItem.get("childFName"));
		viewIntent.putExtra("childLName", (String) listItem.get("childLName"));
		viewIntent.putExtra("childBirth", (String) listItem.get("childBirth"));
		viewIntent.putExtra("childGender", (String) listItem.get("childGender"));
		viewIntent.putExtra("childAddress", (String) listItem.get("childAddress"));
		viewIntent.putExtra("childPostal", (String) listItem.get("childPostal"));
		viewIntent.putExtra("childPhone", (String) listItem.get("childPhone"));  
		viewIntent.putExtra("childLocation", (String) listItem.get("childLocation"));
		viewIntent.putExtra("childLocationName", (String) listItem.get("childLocationName"));
		
		childrenList.this.startActivity(viewIntent);
		finish();
    }
    
    /**
     * @author Xingze
     * select location popup
     */
    private void selectLocationPopup(){
    	
    	//read location list 
		ArrayList<HashMap<String,Object>> locationList = new ArrayList<HashMap<String,Object>>();
		locationList = childDB.getAllLocation();
		final CharSequence[] items = new CharSequence[locationList.size()+1];
		final CharSequence[] itemsID = new CharSequence[locationList.size()+1];
		//set the first element of items as 'all location'
		items[0] = this.getString(R.string.all_location);
		itemsID[0] ="";
		
		for(int i=1;i<locationList.size()+1;i++){
			items[i]=(CharSequence) locationList.get(i-1).get("locationName");
			itemsID[i]=(CharSequence) locationList.get(i-1).get("locationID");
		}

    	AlertDialog.Builder builder = new AlertDialog.Builder(this);  

    	builder.setTitle(R.string.select_location);  
    	builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {  
    	    public void onClick(DialogInterface dialog, int item) {
    	    	
    	    	listView.removeAllViewsInLayout();
    	    	ArrayList<HashMap<String,Object>> newChildList = new ArrayList<HashMap<String,Object>>();
    	    	
    	    	//if select all location, show all children at all location
    	    	if(item==0){
    	    		newChildList = childDB.getAllChildren();
    	    		
    	    	}
    	    	else{
    	    		//if select a location , show the children at the location
    	    		newChildList = childDB.getAllChildrenAtLocation(itemsID[item].toString());
    	 
    	    	}
    	    	
    	    	//set new listadapter
    	    	thisAdapter=initializeListAdapter(newChildList);
    	    	listView.setAdapter(thisAdapter);
    	    	
    	    	//set text in location button
    	    	locationButton.setText(items[item]);
    	    	
    	    	//refresh adapter
    	    	thisAdapter.notifyDataSetChanged();
    	    	
    	    	
    	        Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();  
    	        cancelDialog();
    	    }  
    	});  
    	AlertDialog alert = builder.create(); 
    	currentlyShownDialog=builder.show();
    }
    
    /**
     * @author Xingze
     * cancel current dialog
     */
    private void cancelDialog(){
    	currentlyShownDialog.dismiss();
    }
}

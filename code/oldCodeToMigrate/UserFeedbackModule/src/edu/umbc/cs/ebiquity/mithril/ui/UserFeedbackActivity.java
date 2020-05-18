package edu.umbc.cs.ebiquity.mithril.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import edu.umbc.cs.ebiquity.mithril.MithrilApplication;
import edu.umbc.cs.ebiquity.mithril.R;
import edu.umbc.cs.ebiquity.mithril.data.util.MithrilDBHelper;

/**
 * This class needs massive changes have to include all the information to be displayed to the user from the static rule information
 * @author Prajit
 *
 */
public class UserFeedbackActivity extends Activity {
	private Button mBtnChangeRule;
	private Button mBtnCreateNewRule;
	
	private static MithrilDBHelper mithrilDBHelper;
	private static SQLiteDatabase mithrilDB;
	
	private int currentlyDisplayedPolicyId;
	private String currentlyDisplayedPolicyName;
	private Intent intentReceivedFromParent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_feedback);
		
		intentReceivedFromParent = getIntent();
		
		initDB();
		initView();
		addOnClickListener();
	}

	private void initDB() {
		/**
		 * Database creation and default data insertion, happens only once.
		 */
		mithrilDBHelper = new MithrilDBHelper(this);
		mithrilDB = mithrilDBHelper.getWritableDatabase();		
	}
	
	private void initView() {
		currentlyDisplayedPolicyId = intentReceivedFromParent.getIntExtra(MithrilApplication.getConstKeyPolicyRuleId(),-1);
		currentlyDisplayedPolicyName = intentReceivedFromParent.getStringExtra(MithrilApplication.getConstKeyPolicyRuleName());
		
		mBtnChangeRule = (Button) findViewById(R.id.btnChangeRule);
		mBtnCreateNewRule = (Button) findViewById(R.id.btnCreateNewRule);
	}

	private void addOnClickListener() {
		mBtnChangeRule.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(), ChangeRuleActivity.class);
				myIntent.putExtra(MithrilApplication.getConstKeyPolicyRuleId(), currentlyDisplayedPolicyId);
				myIntent.putExtra(MithrilApplication.getConstKeyPolicyRuleName(), currentlyDisplayedPolicyName);
				startActivity(myIntent);
				finish();
			}
		});

		mBtnCreateNewRule.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(), CreateNewRuleActivity.class);
				myIntent.putExtra(MithrilApplication.getConstKeyPolicyRuleId(), currentlyDisplayedPolicyId);
				myIntent.putExtra(MithrilApplication.getConstKeyPolicyRuleName(), currentlyDisplayedPolicyName);
				startActivity(myIntent);
			}
		});
	}
	
	public void showPopup(View v) {
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_feedback_settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch(item.getItemId()) {
			case R.id.actionDeleteRule:
				/**
				 * Delete Rule if the user clicks on the settings menu to do that
				 * Delete all violations related to the policy rule
				 */
				Log.v(MithrilApplication.getConstDebugTag(), "Policy Id to delete is: "+currentlyDisplayedPolicyId);
				mithrilDBHelper.deletePolicyRule(mithrilDB, mithrilDBHelper.findPolicyByID(mithrilDB, currentlyDisplayedPolicyId));
				mithrilDBHelper.deleteAllViolationsForPoliCyRuleId(mithrilDB, currentlyDisplayedPolicyId);
				Toast.makeText(this, currentlyDisplayedPolicyName+MithrilApplication.getConstToastMessageRuleDeleted(), Toast.LENGTH_LONG).show();
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.actionRuleOK:
				/**
				 * Mark rules as correct so that future violations are considered to be real violations
				 */
				mithrilDBHelper.updateViolationAsTrue(mithrilDB, mithrilDBHelper.findViolationByPolRulId(mithrilDB, currentlyDisplayedPolicyId));
				Toast.makeText(this, currentlyDisplayedPolicyName+MithrilApplication.getConstToastMessageTrueViolationNoted(), Toast.LENGTH_LONG).show();
				NavUtils.navigateUpFromSameTask(this);
				return true;
			default: 
				return super.onOptionsItemSelected(item);
		}
	}
}
/**
 * Previous code used expandable lists. We have changed that above.
 * @author Prajit
public class UserFeedbackActivity extends Activity {
	private ExpandableListAdapter mExpandableListAdapter;
	private ExpandableListView mExpandableListView;
	private Button mSaveButtonUserFeedback;

	private List<String> listDataHeader;
	private HashMap<String, List<String>> listDataChild;
	private PolicyRule clickedViolationAction;

	private int lastExpandedPosition = -1;
	
	private Map<String, Integer> mapForSelectedDataPointer = new HashMap<String, Integer>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_feedback);
		Gson GSONObject = new Gson();
		String src = getIntent().getStringExtra(MithrilApplication.getDebugTag());
		clickedViolationAction = GSONObject.fromJson(src, PolicyRule.class);
        // get the listview
		mExpandableListView = (ExpandableListView) findViewById(R.id.expandable_listview_for_displaying_modifiable_options);
 
        // preparing list data
        prepareListData();
 
        mExpandableListAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
 
        // setting list adapter
        mExpandableListView.setAdapter(mExpandableListAdapter);
        
        for(String groupName : mapForSelectedDataPointer.keySet()) {
        	mExpandableListView.setSelectedChild(listDataHeader.indexOf(groupName), mapForSelectedDataPointer.get(groupName), true);
        }
        
        mSaveButtonUserFeedback = (Button) findViewById(R.id.save_button_user_feedback);        
        addOnClickListener();
	}

    private void addOnClickListener() {
		mSaveButtonUserFeedback.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		// Listview Group click listener
    	mExpandableListView.setOnGroupClickListener(new OnGroupClickListener() {
    		@Override
    		public boolean onGroupClick(ExpandableListView parent, View v,
    				int groupPosition, long id) {
//	    		Toast.makeText(getApplicationContext(), "Group Clicked " + listDataHeader.get(groupPosition), Toast.LENGTH_SHORT).show();
//	    		lastExpandedPosition = groupPosition;
	    		return false;
    		}
    	});
    	// Listview Group expanded listener
    	mExpandableListView.setOnGroupExpandListener(new OnGroupExpandListener() {
    		@Override
    		public void onGroupExpand(int groupPosition) {
//    			Toast.makeText(getApplicationContext(),
//    					listDataHeader.get(groupPosition) + " Expanded",
//    					Toast.LENGTH_SHORT).show();
    			if(groupPosition != lastExpandedPosition )
    	            mExpandableListView.collapseGroup(lastExpandedPosition);
    			lastExpandedPosition = groupPosition;
    		}
    	});
    	// Listview Group collasped listener
    	mExpandableListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {
	    	@Override
	    	public void onGroupCollapse(int groupPosition) {
//	    	Toast.makeText(getApplicationContext(),
//	    			listDataHeader.get(groupPosition) + " Collapsed",
//	    			Toast.LENGTH_SHORT).show();
	    	}
	    });
    	// Listview on child click listener
    	mExpandableListView.setOnChildClickListener(new OnChildClickListener() {
	    	@SuppressWarnings("unused")
			@Override
	    	public boolean onChildClick(ExpandableListView parent, View v,
	    			int groupPosition, int childPosition, long id) {
	    		if(groupPosition == 4) {
		    		String modifiedLocationOption = listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).toString();
//		    		PolicyRule modifiedUserAction = Mithril.getMDefaultPolicy().getActions().get(0);
//		    		modifiedUserAction.getCurrentContext().getUserLocation().setLocation(modifiedLocationOption);
//		    		Mithril.getMDefaultPolicy().replaceOldActionWthNewActionInPolicy(0, modifiedUserAction);
//		    		Log.d(Mithril.DEBUG_TAG, listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).toString());
//		    		Toast.makeText(Mithril.getSingleton().getApplicationContext(), "Changed policy!", Toast.LENGTH_LONG).show();	
	    		}
//		    	childPosition)
		    	// TODO Auto-generated method stub
//		    	Toast.makeText(
//		    	getApplicationContext(),
//		    	listDataHeader.get(groupPosition)
//		    	+ " : "
//		    	+ listDataChild.get(
//		    	listDataHeader.get(groupPosition)).get(
//		    	childPosition), Toast.LENGTH_SHORT)
//		    	.show();
		    	return false;
	    	}
	    });	    	
    }

	private void prepareListData() {
    	listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
 
        addHeaderInfo();
        
        listDataChild.put(listDataHeader.get(0), addPresenceInfo()); // Header, Child data
        listDataChild.put(listDataHeader.get(1), addRequesterInfo());
        listDataChild.put(listDataHeader.get(2), addUserActivityData());
        listDataChild.put(listDataHeader.get(3), addUserIdentityData());
        listDataChild.put(listDataHeader.get(4), addUserLocationData());
        listDataChild.put(listDataHeader.get(5), addUserTimeData());
    }
	
    private List<String> addUserTimeData() {
    	ArrayList<String> tempDataList = new ArrayList<String>();
//    	tempDataList.add(clickedViolationAction.getCurrentContext().getUserTime().getTime());
//    	mapForSelectedDataPointer.put(Mithril.USER_TIME_HEADER, 0);
		return tempDataList;
	}

	private List<String> addUserLocationData() {
    	ArrayList<String> tempDataList = new ArrayList<String>();
//    	tempDataList.add("ITE 332");
//    	tempDataList.add("ITE Building");
//    	tempDataList.add("Olive Garden");
//    	tempDataList.add(clickedViolationAction.getCurrentContext().getUserLocation().getLocation());
//    	mapForSelectedDataPointer.put(Mithril.USER_LOCATION_HEADER, 2);
    	tempDataList.add("Restaurant in Columbia");
    	tempDataList.add("Columbia");
    	tempDataList.add("Maryland");
    	tempDataList.add("USA");
		return tempDataList;
	}

	private List<String> addUserIdentityData() {
    	ArrayList<String> tempDataList = new ArrayList<String>();
//    	tempDataList.add(clickedViolationAction.getCurrentContext().getUserIdentity().getIdentity());
//    	mapForSelectedDataPointer.put(Mithril.USER_IDENTITY_HEADER, 0);
		return tempDataList;
	}

	private List<String> addUserActivityData() {
    	ArrayList<String> tempDataList = new ArrayList<String>();
//    	tempDataList.add(clickedViolationAction.getCurrentContext().getUserActivity().getActivity());
//    	mapForSelectedDataPointer.put(Mithril.USER_ACTIVITY_HEADER, 0);
		return tempDataList;
	}

	private List<String> addRequesterInfo() {
    	ArrayList<String> tempDataList = new ArrayList<String>();
//    	tempDataList.add(clickedViolationAction.getCurrentContext().getRequesterInfo().getRequesterName());
//    	mapForSelectedDataPointer.put(Mithril.REQUESTER_INFO_HEADER, 0);
		return tempDataList;
	}

	private List<String> addPresenceInfo() {
    	ArrayList<String> tempDataList = new ArrayList<String>();
    	for(Identity usersInTheVicinity : clickedViolationAction.getContext().getPresenceInfo().getListOfUsersInTheVicinity())
    		tempDataList.add(usersInTheVicinity.getIdentity());
		return tempDataList;
	}

	private void addHeaderInfo() {
        // Adding header info
        listDataHeader.add(MithrilApplication.getPresenceInfoHeader());
        listDataHeader.add(MithrilApplication.getRequesterInfoHeader());
        listDataHeader.add(MithrilApplication.getUserActivityHeader());
        listDataHeader.add(MithrilApplication.getUserIdentityHeader());
        listDataHeader.add(MithrilApplication.getUserLocationHeader());
        listDataHeader.add(MithrilApplication.getUserTimeHeader());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.global, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
 */
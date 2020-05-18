package edu.umbc.cs.ebiquity.mithril.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;
import edu.umbc.cs.ebiquity.mithril.MithrilApplication;
import edu.umbc.cs.ebiquity.mithril.R;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.PolicyRule;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.UserContext;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces.DeviceTime;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces.Identity;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces.InferredActivity;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces.InferredLocation;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces.PresenceInfo;
import edu.umbc.cs.ebiquity.mithril.data.util.MithrilDBHelper;

public class ChangeRuleActivity extends Activity {
	private ToggleButton mChangeRuleTimeContextToggleButton;
	private ToggleButton mChangeRuleLocationContextToggleButton;
	private ToggleButton mChangeRuleActivityContextToggleButton;
	private ToggleButton mChangeRulePresenceInfoContextToggleButton;
//	private ToggleButton mChangeRuleDynamicContextToggleButton;
	
	private Spinner mChangeRuleTimeContextSpinner;
	private Spinner mChangeRuleLocationContextSpinner;
	private Spinner mChangeRuleActivityContextSpinner;
	private Spinner mChangeRulePresenceInfoContextSpinner;
//	private Spinner mChangeRuleDynamicContextHeaderSpinner;
//	private Spinner mChangeRuleDynamicContextValueSpinner;
	
	private Button mBtnDoneChangingRule;
	
	private String[] timeContextStringArray;
	private String[] locationContextStringArray;
	private String[] activityContextStringArray;
	private String[] presenceInfoContextStringArray;
//	private String[] dynamicContextHeaderStringArray;
	
	private static MithrilDBHelper mithrilDBHelper;
	private static SQLiteDatabase mithrilDB;
	
	private int currentlyDisplayedPolicyId;
//	private String currentlyDisplayedPolicyName;
	private Intent intentReceivedFromParent;
	
	private UserContext userContextHolder;
	private PolicyRule policyRuleHolder;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_rule);
		
		intentReceivedFromParent = getIntent();
		
		initDB();
		initView();
		setOnItemSelectedListener();
		setOnCheckedChangeListener();
		addOnButtonClickListener();
	}
	
	private void initDB() {
		/**
		 * Database creation and default data insertion, happens only once.
		 */
		mithrilDBHelper = new MithrilDBHelper(this);
		mithrilDB = mithrilDBHelper.getWritableDatabase();		
	}
	
	private void initView() {
		/**
		 * A floating action button would be great but is not functional at the moment
		 */
//		addFAB();
		currentlyDisplayedPolicyId = intentReceivedFromParent.getIntExtra(MithrilApplication.getConstKeyPolicyRuleId(),-1);
//		currentlyDisplayedPolicyName = intentReceivedFromParent.getStringExtra(MithrilApplication.getConstKeyPolicyRuleName());
		
		policyRuleHolder = mithrilDBHelper.findPolicyByID(mithrilDB, currentlyDisplayedPolicyId);
		userContextHolder = policyRuleHolder.getContext();

//		Check and set the toggle button
		mChangeRuleTimeContextToggleButton = (ToggleButton) findViewById(R.id.changeRuleTimeContextToggleButtonId);
		mChangeRuleTimeContextToggleButton.setChecked(true);
		mChangeRuleLocationContextToggleButton = (ToggleButton) findViewById(R.id.changeRuleLocationContextToggleButtonId);
		mChangeRuleLocationContextToggleButton.setChecked(true);
		mChangeRuleActivityContextToggleButton = (ToggleButton) findViewById(R.id.changeRuleActivityContextToggleButtonId);
		mChangeRuleActivityContextToggleButton.setChecked(true);
		mChangeRulePresenceInfoContextToggleButton = (ToggleButton) findViewById(R.id.changeRulePresenceInfoContextToggleButtonId);
		mChangeRulePresenceInfoContextToggleButton.setChecked(true);
//		mChangeRuleDynamicContextToggleButton = (ToggleButton) findViewById(R.id.changeRuleDynamicContextToggleButtonId);
//		mChangeRuleDynamicContextToggleButton.setChecked(true);
		
//		Set up spinner views will be populated later
		mChangeRuleTimeContextSpinner = (Spinner) findViewById(R.id.changeRuleTimeContextSpinnerId);
		mChangeRuleTimeContextSpinner.setActivated(true);
		mChangeRuleLocationContextSpinner = (Spinner) findViewById(R.id.changeRuleLocationContextSpinnerId);
		mChangeRuleLocationContextSpinner.setActivated(true);
		mChangeRuleActivityContextSpinner = (Spinner) findViewById(R.id.changeRuleActivityContextSpinnerId);
		mChangeRuleActivityContextSpinner.setActivated(true);
		mChangeRulePresenceInfoContextSpinner = (Spinner) findViewById(R.id.changeRulePresenceInfoContextSpinnerId);
		mChangeRulePresenceInfoContextSpinner.setActivated(true);
//		mChangeRuleDynamicContextHeaderSpinner = (Spinner) findViewById(R.id.changeRuleDynamicContextHeaderSpinnerId);
//		mChangeRuleDynamicContextHeaderSpinner.setActivated(true);
//		mChangeRuleDynamicContextValueSpinner = (Spinner) findViewById(R.id.changeRuleDynamicContextValueSpinnerId);
//		mChangeRuleDynamicContextValueSpinner.setActivated(false);
		
		mBtnDoneChangingRule = (Button) findViewById(R.id.btnDoneChangingRule);

		populateSpinnerOptions();
	}

//	private void addFAB() {
//		FloatingActionButton fabButton = new FloatingActionButton.Builder(this)
//        .withDrawable(getResources().getDrawable(R.drawable.fab_icon))
//        .withButtonColor(Color.WHITE)
//        .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
//        .withMargins(0, 0, 5, 5)
//        .create();
//	}

	private void populateSpinnerOptions() {
//		Populate spinner views
		populateSpinnerOptionsTimeContext();
		populateSpinnerOptionsLocationContext();
		populateSpinnerOptionsActivityContext();
		populateSpinnerOptionsPresenceInfoContext();
//		populateSpinnerOptionsDynamicContextHeader();
	}

	private void populateSpinnerOptionsTimeContext() {
		//Get the context piece instances from the MithrilApplication class
		int pos = getTimeContextPieceInstances();
		//Only if the context information for this context piece is available in the database for this rule we will populate this spinner
		if(pos == -1) {
			mChangeRuleTimeContextToggleButton.setChecked(false);
			mChangeRuleTimeContextSpinner.setActivated(false);
		}
		else {
			// Create an ArrayAdapter using the string array and a default spinner layout
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
			        android.R.layout.simple_spinner_item, timeContextStringArray);
			// Specify the layout to use when the list of choices appears
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// Apply the adapter to the spinner
			mChangeRuleTimeContextSpinner.setAdapter(adapter);
			mChangeRuleTimeContextSpinner.setSelection(pos);
		}
	}

	private void populateSpinnerOptionsLocationContext() {
		//Get the context piece instances from the MithrilApplication class
		int pos = getLocationContextPieceInstances();
		//Only if the context information for this context piece is available in the database for this rule we will populate this spinner
		if(pos == -1) {
			mChangeRuleLocationContextToggleButton.setChecked(false);
			mChangeRuleLocationContextSpinner.setActivated(false);
		}
		else {
			// Create an ArrayAdapter using the string array and a default spinner layout
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
			        android.R.layout.simple_spinner_item, locationContextStringArray);
			// Specify the layout to use when the list of choices appears
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// Apply the adapter to the spinner
			mChangeRuleLocationContextSpinner.setAdapter(adapter);
			mChangeRuleLocationContextSpinner.setSelection(pos);
		}
	}

	private void populateSpinnerOptionsActivityContext() {
		//Get the context piece instances from the MithrilApplication class
		int pos = getActivityContextPieceInstances();
		//Only if the context information for this context piece is available in the database for this rule we will populate this spinner
		if(pos == -1) {
			mChangeRuleActivityContextToggleButton.setChecked(false);
			mChangeRuleActivityContextSpinner.setActivated(false);
		}
		else {
			// Create an ArrayAdapter using the string array and a default spinner layout
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
			        android.R.layout.simple_spinner_item, activityContextStringArray);
			// Specify the layout to use when the list of choices appears
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// Apply the adapter to the spinner
			mChangeRuleActivityContextSpinner.setAdapter(adapter);
			mChangeRuleActivityContextSpinner.setSelection(pos);
		}
	}

	private void populateSpinnerOptionsPresenceInfoContext() {
		//Get the context piece instances from the MithrilApplication class
		int pos = getPresenceInfoContextPieceInstances();
		//Only if the context information for this context piece is available in the database for this rule we will populate this spinner
		if(pos == -1) {
			mChangeRulePresenceInfoContextToggleButton.setChecked(false);
			mChangeRulePresenceInfoContextSpinner.setActivated(false);
		}
		else {
			// Create an ArrayAdapter using the string array and a default spinner layout
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
			        android.R.layout.simple_spinner_item, presenceInfoContextStringArray);
			// Specify the layout to use when the list of choices appears
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// Apply the adapter to the spinner
			mChangeRulePresenceInfoContextSpinner.setAdapter(adapter);
			mChangeRulePresenceInfoContextSpinner.setSelection(pos);
		}
	}

//	private void populateSpinnerOptionsDynamicContextHeader() {
//		//Get the context piece instances from the MithrilApplication class
//		int pos = getDynamicContextHeaderInstances();
//		// Create an ArrayAdapter using the string array and a default spinner layout
//		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//		        android.R.layout.simple_spinner_item, dynamicContextHeaderStringArray);
//		// Specify the layout to use when the list of choices appears
//		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//		// Apply the adapter to the spinner
//		mChangeRuleDynamicContextHeaderSpinner.setAdapter(adapter);
//		//Always start with the first context piece
//		mChangeRuleDynamicContextHeaderSpinner.setSelection(pos);
//	}

	/**
	 * Ideally the following lists should be a obtained from the database but right now we are making sure it just works
	 * @return
	 */
	private int getTimeContextPieceInstances() {
		timeContextStringArray = MithrilApplication.getConstArrayTime();
		int index = -1;
		for(String aContextPiece:timeContextStringArray) {
			if(aContextPiece.equals(userContextHolder.getTime().toString()))
				return ++index;
			++index;
		}
		return -1;
	}

	private int getLocationContextPieceInstances() {
		locationContextStringArray = MithrilApplication.getConstArrayLocation();
		int index = -1;
		for(String aContextPiece:locationContextStringArray) {
			if(aContextPiece.equals(userContextHolder.getLocation().toString())) 
				return ++index;
			++index;
		}
		return -1;
	}

	private int getActivityContextPieceInstances() {
		activityContextStringArray = MithrilApplication.getConstArrayActivity();
		int index = -1;
		for(String aContextPiece:activityContextStringArray) {
			if(aContextPiece.equals(userContextHolder.getActivity().toString())) 
				return ++index;
			++index;
		}
		return -1;
	}

	private int getPresenceInfoContextPieceInstances() {
		presenceInfoContextStringArray = MithrilApplication.getConstArrayPresenceInfoIdentity();
		int index = -1;
		for(String aContextPiece:presenceInfoContextStringArray) {
			if(aContextPiece.equals(userContextHolder.getPresenceInfo().toString())) 
				return ++index;
			++index;
		}
		return -1;
	}

//	private int getDynamicContextHeaderInstances() {
//		dynamicContextHeaderStringArray = new String[4];
//		dynamicContextHeaderStringArray[0] = new String("Location");
//		dynamicContextHeaderStringArray[1] = new String("Activity");
//		dynamicContextHeaderStringArray[2] = new String("Time");
//		dynamicContextHeaderStringArray[3] = new String("Presence");
//		return 0;
//	}

	private void setOnItemSelectedListener() {
		mChangeRuleTimeContextSpinner.setOnItemSelectedListener(new SpinnerActivity("time"));
		mChangeRuleLocationContextSpinner.setOnItemSelectedListener(new SpinnerActivity("location"));
		mChangeRuleActivityContextSpinner.setOnItemSelectedListener(new SpinnerActivity("activity"));
		mChangeRulePresenceInfoContextSpinner.setOnItemSelectedListener(new SpinnerActivity("presence"));
//		mChangeRuleDynamicContextHeaderSpinner.setOnItemSelectedListener(new SpinnerActivity("dynamic"));
	}
	
	public class SpinnerActivity extends Activity implements OnItemSelectedListener {
		private String contextPiece;

		public SpinnerActivity(String contextPiece) {
			this.contextPiece = contextPiece;
		}

		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			// An item was selected. You can retrieve the selected item using
			// parent.getItemAtPosition(pos)
			if(contextPiece.equals("time"))
				userContextHolder.setTime(new DeviceTime(parent.getItemAtPosition(pos).toString()));
			else if(contextPiece.equals("location"))
				userContextHolder.setLocation(new InferredLocation(parent.getItemAtPosition(pos).toString()));
			else if(contextPiece.equals("activity"))
				userContextHolder.setActivity(new InferredActivity(parent.getItemAtPosition(pos).toString()));
			else if(contextPiece.equals("presence")) {
				List<Identity> tempIdentityList = new ArrayList<Identity>();
				tempIdentityList.add(new Identity(parent.getItemAtPosition(pos).toString()));
				userContextHolder.setPresenceInfo(new PresenceInfo(tempIdentityList));
			}
//			else if(contextPiece.equals("dynamic")) {
//				populateSpinnerOptionsDynamicContextValue(parent.getItemAtPosition(pos).toString());
//			}
		}

		public void onNothingSelected(AdapterView<?> parent) {
			// Another interface callback
		}
	}

	/*
	private void populateSpinnerOptionsDynamicContextValue(String dynamicContextPiece) {
		if(dynamicContextPiece.equals("Location")) {
			// Create an ArrayAdapter using the string array and a default spinner layout
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
			        android.R.layout.simple_spinner_item, locationContextStringArray);
			// Specify the layout to use when the list of choices appears
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// Apply the adapter to the spinner
			mChangeRuleDynamicContextValueSpinner.setAdapter(adapter);
			//Always start with the first context piece
			mChangeRuleDynamicContextValueSpinner.setSelection(0);
		}
		else if(dynamicContextPiece.equals("Activity")) {
			// Create an ArrayAdapter using the string array and a default spinner layout
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
			        android.R.layout.simple_spinner_item, activityContextStringArray);
			// Specify the layout to use when the list of choices appears
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// Apply the adapter to the spinner
			mChangeRuleDynamicContextValueSpinner.setAdapter(adapter);
			//Always start with the first context piece
			mChangeRuleDynamicContextValueSpinner.setSelection(0);
		}
		else if(dynamicContextPiece.equals("Time")) {
			// Create an ArrayAdapter using the string array and a default spinner layout
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
			        android.R.layout.simple_spinner_item, timeContextStringArray);
			// Specify the layout to use when the list of choices appears
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// Apply the adapter to the spinner
			mChangeRuleDynamicContextValueSpinner.setAdapter(adapter);
			//Always start with the first context piece
			mChangeRuleDynamicContextValueSpinner.setSelection(0);
		}
		else if(dynamicContextPiece.equals("Presence")) {
			// Create an ArrayAdapter using the string array and a default spinner layout
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
			        android.R.layout.simple_spinner_item, presenceInfoContextStringArray);
			// Specify the layout to use when the list of choices appears
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// Apply the adapter to the spinner
			mChangeRuleDynamicContextValueSpinner.setAdapter(adapter);
			//Always start with the first context piece
			mChangeRuleDynamicContextValueSpinner.setSelection(0);
		}
	}
*/
	private void setOnCheckedChangeListener() {
		mChangeRuleTimeContextToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
		        if (isChecked) {
					mChangeRuleTimeContextSpinner.setEnabled(true);
					userContextHolder.setTime(new DeviceTime(MithrilApplication.getConstContextDefaultTime()));
					mChangeRuleTimeContextSpinner.setSelection(0);
		        } else { 
					mChangeRuleTimeContextSpinner.setEnabled(false);
		        	//Hacky method but it works. This step is essentially setting an empty string for the context and will 
		        	//match with nothing and therefore will be able to set the list the context as null
					userContextHolder.setTime(new DeviceTime(""));
		        }
			}
		});

		mChangeRuleLocationContextToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
		        if (isChecked) {
					mChangeRuleLocationContextSpinner.setEnabled(true);
					userContextHolder.setLocation(new InferredLocation(MithrilApplication.getConstContextDefaultLocation()));
					mChangeRuleLocationContextSpinner.setSelection(0);
		        } else { 
					mChangeRuleLocationContextSpinner.setEnabled(false);
		        	//Hacky method but it works. This step is essentially setting an empty string for the context and will 
		        	//match with nothing and therefore will be able to set the list the context as null
					userContextHolder.setLocation(new InferredLocation(""));
		        }
			}
		});

		mChangeRuleActivityContextToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
		        if (isChecked) {
					mChangeRuleActivityContextSpinner.setEnabled(true);
					userContextHolder.setActivity(new InferredActivity(MithrilApplication.getConstContextDefaultActivity()));
					mChangeRuleActivityContextSpinner.setSelection(0);
		        } else { 
		        	mChangeRuleActivityContextSpinner.setEnabled(false);
		        	//Hacky method but it works. This step is essentially setting an empty string for the context and will 
		        	//match with nothing and therefore will be able to set the list the context as null
					userContextHolder.setActivity(new InferredActivity(""));
		        }
			}
		});

		mChangeRulePresenceInfoContextToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
		        if (isChecked) {
					mChangeRulePresenceInfoContextSpinner.setEnabled(true);
					List<Identity> tempIdentityList = new ArrayList<Identity>();
					tempIdentityList.add(new Identity(MithrilApplication.getConstContextDefaultIdentity()));
					userContextHolder.setPresenceInfo(new PresenceInfo(tempIdentityList));
					mChangeRulePresenceInfoContextSpinner.setSelection(0);
		        } else { 
		        	mChangeRulePresenceInfoContextSpinner.setEnabled(false);
					userContextHolder.setPresenceInfo(new PresenceInfo());
		        }
			}
		});
	}

	private void addOnButtonClickListener() {
		mBtnDoneChangingRule.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.v(MithrilApplication.getConstDebugTag(), userContextHolder.toString());
				userContextHolder.setId(-1);
				long insertedRowId = mithrilDBHelper.addContext(mithrilDB, userContextHolder);
				userContextHolder.setId((int) insertedRowId);
				policyRuleHolder.setContext(userContextHolder);
//				Log.v(MithrilApplication.getDebugTag(), policyRuleHolder.toString());
				mithrilDBHelper.updatePolicyRuleContextId(mithrilDB, policyRuleHolder);
				Toast.makeText(v.getContext(), "Policy "+policyRuleHolder.getName()+" was updated", Toast.LENGTH_LONG).show();
				finish();
			}
		});
	}

/**
 * 	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.change_rule_settings, menu);
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
*/
}
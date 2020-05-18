package edu.umbc.cs.ebiquity.mithril.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import edu.umbc.cs.ebiquity.mithril.MithrilApplication;
import edu.umbc.cs.ebiquity.mithril.R;
import edu.umbc.cs.ebiquity.mithril.data.adapters.ViolationAdapter;
import edu.umbc.cs.ebiquity.mithril.data.model.Violation;
import edu.umbc.cs.ebiquity.mithril.data.util.MithrilDBHelper;

public class ViolationWarningActivity extends ListActivity {
	private TextView mTextViewViolationsReport;
//	private ListView mViolationListView;

	private ViolationAdapter violationAdapter;
//	private List<String> violationStringViewList;
	
	private List<Violation> violationList;
	
	private static MithrilDBHelper mithrilDBHelper;
	private static SQLiteDatabase mithrilDB;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_violation_list_launcher);

		initDB();
		initView();		
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
		 * Initiate everything
		 */
		mTextViewViolationsReport = (TextView) findViewById(R.id.violation_report_metadata);
		violationList = new ArrayList<Violation>();

		/**
		 * Initiating variables complete, work with logic now
		 * The Markers unset means that the violations have not been responded to by the user
		 */		
		violationList = mithrilDBHelper.findAllViolationsWithMarkerUnset(mithrilDB);
		Log.v(MithrilApplication.getConstDebugTag(), "Violations list size is: "+violationList.size());

		int numberOfViolations = violationList.size();
		mTextViewViolationsReport.setText("Number of potential policy violations = " + Integer.toString(numberOfViolations) + "\nAnd the violated policy rules are:");
		
		violationAdapter = new ViolationAdapter(ViolationWarningActivity.this, R.layout.violation_list_item, violationList);
		setListAdapter(violationAdapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_activity_settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch(item.getItemId()) {
			case R.id.actionReloadDatabaseWithDefaultData:
				/**
				 * Reload the data into the database in case a experiment restart is required.
				 * Use a pop up to ask if they really want to do this.
				 */
				executeCustomDialog();
				return true;
			default: 
				return super.onOptionsItemSelected(item);
		}
	}

	private void executeCustomDialog() {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_reload_data)
               .setPositiveButton(R.string.dialog_resp_delete, new DialogInterface.OnClickListener() {
            	   public void onClick(DialogInterface dialog, int id) {
            		   mithrilDBHelper.deleteAllData(mithrilDB);
            		   /**
            		    * Reload the main activity screen for showing reloaded data
            		    */
            		   Intent intent = new Intent(getApplicationContext(),ViolationWarningActivity.class);
            		   finish();
            		   startActivity(intent);
            	   }
               })
               .setNegativeButton(R.string.dialog_resp_NO, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   Toast.makeText(getApplicationContext(), 
                			   MithrilApplication.getConstToastMessageDatabaseNotReloaded(), 
                			   Toast.LENGTH_LONG).show();
                   }
               });

		// create alert dialog
		AlertDialog alertDialog = builder.create();

		// show it
		alertDialog.show();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Violation aViolation = violationList.get(position);
		try {
			Intent myIntent = new Intent(v.getContext(), UserFeedbackActivity.class);
			myIntent.putExtra(MithrilApplication.getConstKeyPolicyRuleName(), aViolation.toString());
			myIntent.putExtra(MithrilApplication.getConstKeyPolicyRuleId(), aViolation.getPolicyId());
			startActivity(myIntent);
		} catch(ActivityNotFoundException e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		} catch(Exception e) {
			e.printStackTrace();
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
	
//	private void addOnClickListener() {
//		mViolationListView.setOnItemClickListener(new OnItemClickListener() {
//			  @Override
//			  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				  Intent myIntent = new Intent(view.getContext(), UserFeedbackActivity.class);
///**
// * The GSON jar is creating an issue. We will switch to text based computing for now.
// */
////				  Gson GSONObject = new Gson();
////				  String data = GSONObject.toJson(violationList.get(position));
//				  myIntent.putExtra(MithrilApplication.getConstKeyPolicyRuleName(), violationList.get(position).toString());
//				  myIntent.putExtra(MithrilApplication.getConstKeyPolicyRuleId(), violationList.get(position).getPolicyId());
//				  startActivity(myIntent);
////				  restartActivity();
//			  }
//		});
//	}
}
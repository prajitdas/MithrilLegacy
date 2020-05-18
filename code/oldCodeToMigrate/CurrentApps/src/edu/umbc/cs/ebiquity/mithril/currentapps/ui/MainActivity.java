package edu.umbc.cs.ebiquity.mithril.currentapps.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import edu.umbc.cs.ebiquity.mithril.currentapps.CurrentAppsApplication;
import edu.umbc.cs.ebiquity.mithril.currentapps.R;
import edu.umbc.cs.ebiquity.mithril.currentapps.data.AppContextData;
import edu.umbc.cs.ebiquity.mithril.currentapps.service.CurrentAppsInstalledService;

public class MainActivity extends Activity {
	private Intent mServiceIntent;
	
	private TextView mCurrentAppsDataCollectionAgreementTxtView;
	private Button mAcceptAgreementBtn;
	private Button mStartSvcBtn;
	
	private SharedPreferences preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		init();
		setOnClickListeners();
		test();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		IntentFilter currentAppsServiceIntentFilter = new IntentFilter(CurrentAppsApplication.getConstDataCollectionComplete());
		LocalBroadcastManager.getInstance(this).registerReceiver(onEvent, currentAppsServiceIntentFilter);
	}
	@Override
		public void onPause() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(onEvent);
		super.onPause();
	}
	
	private BroadcastReceiver onEvent = new BroadcastReceiver() {
		public void onReceive(Context ctxt, Intent i) {
	//		b.setEnabled(true);
			Toast.makeText(getApplicationContext(), "Data collection complete", Toast.LENGTH_LONG).show();
		}
	};

	private void init() {
		preferences = PreferenceManager.getDefaultSharedPreferences(this);

		mCurrentAppsDataCollectionAgreementTxtView = (TextView) findViewById(R.id.currentAppsDataCollectionAgreementTxtView);
		mCurrentAppsDataCollectionAgreementTxtView.setText(R.string.agreementText);

		mAcceptAgreementBtn = (Button) findViewById(R.id.acceptAgreementBtn);		
		mStartSvcBtn = (Button) findViewById(R.id.startCurrentAppsSvcBtn);
		
		boolean acceptedOrNot;
		if(preferences.contains(CurrentAppsApplication.getConstAcceptDecisionKey())) {
			acceptedOrNot = preferences.getBoolean(CurrentAppsApplication.getConstAcceptDecisionKey(), false);
			if(acceptedOrNot) {
				mAcceptAgreementBtn.setEnabled(false);
				mStartSvcBtn.setEnabled(true);
			} else {
				mAcceptAgreementBtn.setEnabled(true);
				mStartSvcBtn.setEnabled(false);
			}
		}
		else {
			mAcceptAgreementBtn.setEnabled(true);
			mStartSvcBtn.setEnabled(false);
		}
	}

	private void setOnClickListeners() {
		mAcceptAgreementBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Editor editor = preferences.edit();
		        editor.putBoolean(CurrentAppsApplication.getConstAcceptDecisionKey(), true);
		        editor.commit();

		        mStartSvcBtn.setEnabled(true);
				mAcceptAgreementBtn.setEnabled(false);//.setVisibility(View.GONE);
			}
		});

		mStartSvcBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startCurrentAppsService();
			}
		});
	}

	private void startCurrentAppsService() {
		/*
		 * Creates a new Intent to start the RSSPullService
		 * IntentService. Passes a URI in the
		 * Intent's "data" field.
		 */
		mServiceIntent = new Intent(this, CurrentAppsInstalledService.class);
//		mServiceIntent.setData();
		
		// Starts the IntentService
		this.startService(mServiceIntent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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

	private void test() {
//		activityManagerMethodOfCollectingAppData();
//		usageStatsManagerMethodOfCollectingAppData();
		readLoggerMethodOfCollectingAppData();
	}
	
	private List<String> readLoggerMethodOfCollectingAppData() {
		try {
			Process su = Runtime.getRuntime().exec("su -c busybox ps aux");
			su = Runtime.getRuntime().exec("su -c strace");
			BufferedReader reader = new BufferedReader(new InputStreamReader(su.getInputStream()));
			String line = new String();
			StringBuffer output = new StringBuffer();
			while((line = reader.readLine()) != null) {
				output.append(line + "\n");
			}
			Log.d(CurrentAppsApplication.getDebugTag(), output.toString());
	        Toast.makeText(this, output.toString(), Toast.LENGTH_LONG).show();	
	        Toast.makeText(this, System.getProperty("os.arch"), Toast.LENGTH_LONG).show();	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		List<String> appsRunning = new ArrayList<String>();
//		AppContextData appContextData = new AppContextData("John.Doe@gmail.com", "work", "research", "morning", "personal", appsRunning);
		List<String> listOfInstalledApps = new ArrayList<String>();
//		StringBuffer output = new StringBuffer();
//
//		final UsageStatsManager usageStatsManager = (UsageStatsManager) getApplicationContext().getSystemService("usagestats");
//	    final List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, 0,  System.currentTimeMillis());
//	 
//	    for(UsageStats aQueryUusageStat:queryUsageStats) {
//			listOfInstalledApps.add(aQueryUusageStat.getPackageName());
//			output.append(aQueryUusageStat.getPackageName());
//			output.append(",");
//        	appContextData.getAppsRunning().add(aQueryUusageStat.getPackageName());
//        	listOfInstalledApps.add(aQueryUusageStat.getPackageName());
//		}
//        Toast.makeText(this, Integer.toString(listOfInstalledApps.size()), Toast.LENGTH_LONG).show();
//        Toast.makeText(this, output, Toast.LENGTH_LONG).show();
////		Log.i(CurrentApps.getCurrentAppsDebugTag(), output.toString());
		return listOfInstalledApps;
	}

	@SuppressWarnings("unused")
	private List<String> usageStatsManagerMethodOfCollectingAppData() {
		List<String> appsRunning = new ArrayList<String>();
		AppContextData appContextData = new AppContextData("John.Doe@gmail.com", "work", "research", "morning", "personal", appsRunning);
		List<String> listOfInstalledApps = new ArrayList<String>();
		StringBuffer output = new StringBuffer();

		final UsageStatsManager usageStatsManager = (UsageStatsManager) getApplicationContext().getSystemService("usagestats");
	    final List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, 0,  System.currentTimeMillis());
	 
	    for(UsageStats aQueryUusageStat:queryUsageStats) {
			listOfInstalledApps.add(aQueryUusageStat.getPackageName());
			output.append(aQueryUusageStat.getPackageName());
			output.append(",");
        	appContextData.getAppsRunning().add(aQueryUusageStat.getPackageName());
        	listOfInstalledApps.add(aQueryUusageStat.getPackageName());
		}
        Toast.makeText(this, Integer.toString(listOfInstalledApps.size()), Toast.LENGTH_LONG).show();
        Toast.makeText(this, output, Toast.LENGTH_LONG).show();
//		Log.i(CurrentApps.getCurrentAppsDebugTag(), output.toString());
		return listOfInstalledApps;
	}

	@SuppressWarnings({ "unused", "deprecation" })
	private List<String> activityManagerMethodOfCollectingAppData() {
		List<String> appsRunning = new ArrayList<String>();
		AppContextData appContextData = new AppContextData("John.Doe@gmail.com", "work", "research", "morning", "personal", appsRunning);
		List<String> listOfInstalledApps = new ArrayList<String>();
		ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Activity.ACTIVITY_SERVICE);
		List<RunningTaskInfo> listOfTasks = activityManager.getRunningTasks(Integer.MAX_VALUE);
//        StringBuilder output = new StringBuilder(); 
        for(RunningTaskInfo taskInfo:listOfTasks) {
        	String appPackageName = taskInfo.baseActivity.getPackageName();
        	appContextData.getAppsRunning().add(appPackageName);
        	listOfInstalledApps.add(appPackageName);
        	
        	int currentIndex = appContextData.getAppsRunning().size()-1;
        	
//        	output.append(appContextData.getAppsRunning().get(currentIndex));
//        	Log.d(CurrentApps.getCurrentAppsDebugTag(), "Running task: " + appContextData.getAppsRunning().get(currentIndex) + "\n");
        }
        Toast.makeText(this, Integer.toString(listOfInstalledApps.size()), Toast.LENGTH_LONG).show();
        return listOfInstalledApps;
	}
}
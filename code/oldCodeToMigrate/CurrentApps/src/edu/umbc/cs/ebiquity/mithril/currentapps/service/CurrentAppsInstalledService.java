package edu.umbc.cs.ebiquity.mithril.currentapps.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.IntentService;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Patterns;
import android.widget.Toast;
import edu.umbc.cs.ebiquity.mithril.currentapps.CurrentAppsApplication;
import edu.umbc.cs.ebiquity.mithril.currentapps.data.AppContextData;

public class CurrentAppsInstalledService extends IntentService {
	private AppContextData appContextData;
	private List<String> appList;
	
	public CurrentAppsInstalledService() {
		super("CurrentAppsInstalledService");
		
		appList = new ArrayList<String>();
		//This is a default contextual situation, ideally we should be able to use real context
		appContextData = new AppContextData("John.Doe@gmail.com", "work", "research", "morning", "personal", appList);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		collectTheData();
		sendTheData();
        
        /* Task complete now return */
        Intent intentOnCompletionOfDataCollection = new Intent(CurrentAppsApplication.getConstDataCollectionComplete());
//        intentOnCompletionOfDataCollection.putExtra(CONST_LIST_OF_RUNNING_APPS_EXTRA, output.toString());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentOnCompletionOfDataCollection);
	}

	/**
	 * Collect running app info
	 * @return 
	 */
	private List<String> collectTheData() {
//		return activityManagerMethodOfCollectingAppData();
		return packageManagerMethodOfCollectingAppData();
	}
	
	private List<String> packageManagerMethodOfCollectingAppData() {
		List<String> listOfInstalledApps = new ArrayList<String>();
		StringBuffer output = new StringBuffer(); 
		PackageManager packageManager = getPackageManager();
		for(ApplicationInfo appInfo:packageManager.getInstalledApplications(PackageManager.GET_META_DATA)) {
			listOfInstalledApps.add(appInfo.name);
			output.append(appInfo.name);
			output.append(",");
		}
//		Log.i(CurrentApps.getCurrentAppsDebugTag(), output.toString());
		return listOfInstalledApps;
	}

	@SuppressWarnings({ "unused", "deprecation" })
	private List<String> activityManagerMethodOfCollectingAppData() {
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
        return listOfInstalledApps;
	}

/**
 * 												Data upload portion
 * ------------------------------------------------------------------------------------------------------------------------
 */

	/**
	 * Send the data to the server
	 */
	private void sendTheData() {
		if(isOnline())
			new SendDataToServerAsyncTask().execute();// for older method CurrentApps.getConstWebserviceUri());
	}

	private class SendDataToServerAsyncTask extends AsyncTask<String, String, String> {
		private String resp;
		// Do the long-running work in here
		@Override
	    protected String doInBackground(String... params) {
//			Log.d(CurrentApps.getCurrentAppsDebugTag(), "Loading contents...");
			publishProgress("Loading contents..."); // Calls onProgressUpdate()
			try {
//				Log.d(CurrentApps.getCurrentAppsDebugTag(), "Loading SOAP...");
				String reqXMLPrefix = "<?xml version=\"1.0\" ?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Body><ns2:printString xmlns:ns2=\"http://webservice.hma.mithril.android.ebiquity.cs.umbc.edu/\"><arg0>";
				String reqXMLPostfix = "</arg0></ns2:printString></S:Body></S:Envelope>";
				
				String request = reqXMLPrefix+writeDataToStream()+reqXMLPostfix;
				
				URL url;		
				HttpURLConnection httpURLConnection = null;
				try {
					//Create connection
					url = new URL(CurrentAppsApplication.getConstWebserviceUri());
					httpURLConnection = (HttpURLConnection)url.openConnection();
					httpURLConnection.setRequestMethod("POST");
					httpURLConnection.setRequestProperty("Content-type", "text/xml; charset=utf-8");
					httpURLConnection.setRequestProperty("SOAPAction", "http://eb4.cs.umbc.edu:1234/ws/datamanager#printString");
					httpURLConnection.setChunkedStreamingMode(0);

					httpURLConnection.setUseCaches (false);
					httpURLConnection.setDoInput(true);
					httpURLConnection.setDoOutput(true);
					httpURLConnection.connect();
					
//					Log.d(CurrentApps.getCurrentAppsDebugTag(), "Hardcoded call starts...");
					//Send request
					BufferedOutputStream out = new BufferedOutputStream(httpURLConnection.getOutputStream());
					out.write(request.getBytes());
//					Log.d(CurrentApps.getCurrentAppsDebugTag(), out.toString());
					out.flush();
					out.close();
//					Log.d(CurrentApps.getCurrentAppsDebugTag(), "Hardcoded call ends...");

					//Get Response	
					InputStream in = new BufferedInputStream(httpURLConnection.getInputStream());
//					Log.d(CurrentApps.getCurrentAppsDebugTag(), "Input stream reading...");
					resp = convertInputStreamToString(in);
//					Log.d(CurrentApps.getCurrentAppsDebugTag(), "Read from server: "+resp);
				} catch (IOException e) {
					// writing exception to log
					e.printStackTrace();//(CurrentApps.getCurrentAppsDebugTag(), e.getStackTrace().toString());
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				} finally {
					if(httpURLConnection != null) {
						httpURLConnection.disconnect(); 
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				resp = e.getMessage();
			}
			return resp;
		}			
	    // This is called when doInBackground() is finished
	    @Override
	    protected void onPostExecute(String resp) {
	    	Toast.makeText(getApplicationContext(), "This data was received: "+resp, Toast.LENGTH_LONG).show();
	    }

		  	/**
			 * For older method
			 * return HTTPPOST(params[0]);
			 */
	}

	private String writeDataToStream() throws JSONException, IOException {
//		Log.d(CurrentApps.getCurrentAppsDebugTag(), "Loading JSON...");
		setRealData();
		// Add your data
		//Create JSONObject here 
		JSONObject jsonParam = new JSONObject();
		jsonParam.put("identity", appContextData.getIdentity());
//		jsonParam.put("location", appContextData.getLocation());
//		jsonParam.put("activity", appContextData.getActivity());
//		jsonParam.put("time", appContextData.getTime());
//		jsonParam.put("purpose", appContextData.getPurpose());

		JSONArray jsonArray = new JSONArray();
		for(String appPackageName:collectTheData())
		jsonArray.put(appPackageName);
//		        	jsonArray.put("Facebook");
//		        	jsonArray.put("Twitter");
//		        	jsonArray.put("G+");
		jsonParam.put("appsInstalled",jsonArray);
		
		return jsonParam.toString();
	}

	private void setRealData() {
		setRealIdentity();
		setRealLocation();
		setRealActivity();
		setRealTime();
	}

	private void setRealTime() {
		// TODO Auto-generated method stub
		
	}

	private void setRealActivity() {
		// TODO Auto-generated method stub
		
	}

	private void setRealLocation() {
		// TODO Auto-generated method stub
		
	}

	private void setRealIdentity() {
		Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
	    Account[] accounts = AccountManager.get(this).getAccounts();
	    for (Account account : accounts)
	        if (emailPattern.matcher(account.name).matches())
	        	appContextData.setIdentity(account.name);//possibleEmail
//	    Log.i(CurrentApps.getCurrentAppsDebugTag(),appContextData.getIdentity());
	}

	private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;
        inputStream.close();
//		Log.d(CurrentApps.getCurrentAppsDebugTag(), "Input stream reading complete...");
        return result;
    }

    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
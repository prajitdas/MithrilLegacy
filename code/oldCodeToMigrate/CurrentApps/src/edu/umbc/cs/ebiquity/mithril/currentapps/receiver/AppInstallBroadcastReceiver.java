package edu.umbc.cs.ebiquity.mithril.currentapps.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import edu.umbc.cs.ebiquity.mithril.currentapps.R;
import edu.umbc.cs.ebiquity.mithril.currentapps.ui.NotificationView;

public class AppInstallBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String message = new String();
		if(intent.getAction() == "android.intent.action.PACKAGE_ADDED")
			message = "New app installed: "+intent.getPackage();
		else if(intent.getAction() == "android.intent.action.PACKAGE_CHANGED")
			message = "Component changed (enabled or disabled): "+intent.getPackage();
		else if(intent.getAction() == "android.intent.action.PACKAGE_INSTALL")
			message = "Trigger the download and eventual installation of package: "+intent.getPackage();
		else if(intent.getAction() == "android.intent.action.PACKAGE_REMOVED")
			message = "Existing application package: "+intent.getPackage()+" has been removed from the device";
		else if(intent.getAction() == "android.intent.action.PACKAGE_REPLACED")
			message = "A new version of application package: "+intent.getPackage()+"has been installed";
		Notification(context, message);
	}

	public void Notification(Context context, String message) {
		// Open NotificationView Class on Notification Click
		Intent intent = new Intent(context, NotificationView.class);
		// Send data to NotificationView Class
		intent.putExtra("title", message);
		// Open NotificationView.java Activity
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
 
		// Create Notification using NotificationCompat.Builder
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				context)
				// Set Icon
				.setSmallIcon(R.drawable.logosmall)
				// Set Ticker Message
				.setTicker(message)
				// Set Title
				.setContentTitle(context.getString(R.string.notificationtitle))
				// Set Text
				.setContentText(message)
				// Add an Action Button below Notification
				.addAction(R.drawable.ic_launcher, "Action Button", pIntent)
				// Set PendingIntent into Notification
				.setContentIntent(pIntent)
				// Dismiss Notification
				.setAutoCancel(true);
 
		// Create Notification Manager
		NotificationManager notificationmanager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		// Build Notification with Notification Manager
		notificationmanager.notify(0, builder.build());
 
	}
}
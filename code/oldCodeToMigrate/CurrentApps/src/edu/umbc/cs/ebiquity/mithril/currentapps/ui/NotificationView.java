package edu.umbc.cs.ebiquity.mithril.currentapps.ui;

import edu.umbc.cs.ebiquity.mithril.currentapps.R;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
 
public class NotificationView extends Activity {
    // Declare Variable
	private String title;
	private TextView txttitle;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notificationview);
 
        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Dismiss Notification
        notificationmanager.cancel(0);
 
        // Retrive the data from MainActivity.java
        Intent i = getIntent();
 
        title = i.getStringExtra("title");
 
        // Locate the TextView
        txttitle = (TextView) findViewById(R.id.title);
 
        // Set the data into TextView
        txttitle.setText(title);
    }
}
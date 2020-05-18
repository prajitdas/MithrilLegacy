package edu.umbc.cs.ebiquity.mithril.currentapps.data;

import java.util.List;

public class AppContextData {
	private String activity;
	private String location;
	private String purpose;
	private String time;
	private String identity;
	private List<String> appsRunning;
	
	public AppContextData(String identity, String location, String activity, String time, String purpose, List<String> appsRunning) {
		this.identity = identity;
		this.location = location;
		this.activity = activity;
		this.time = time;
		this.purpose = purpose;
		this.appsRunning = appsRunning;
	}

	public String getActivity() {
		return activity;
	}

	public List<String> getAppsRunning() {
		return appsRunning;
	}

	public String getLocation() {
		return location;
	}

	public String getPurpose() {
		return purpose;
	}

	public String getTime() {
		return time;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public void setAppsRunning(List<String> appsRunning) {
		this.appsRunning = appsRunning;
	}

	public void setLocationLabel(String locationLabel) {
		this.location = locationLabel;
	}
	
	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String toStringForAppsRunning() {
		StringBuilder temp = new StringBuilder();
		for(String appRunning : appsRunning) {
			temp.append(appRunning);
			temp.append(",");
		}
		return temp.toString();
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}
}
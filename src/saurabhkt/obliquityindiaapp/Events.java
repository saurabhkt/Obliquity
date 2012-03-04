package saurabhkt.obliquityindiaapp;

import com.google.gson.annotations.SerializedName;

public class Events {

	@SerializedName("title")
	public String title;
	
	@SerializedName("description")
	public String description;
	
	@SerializedName("time")
	public String time;
	
	@SerializedName("eventId")
	public int eventId;
}

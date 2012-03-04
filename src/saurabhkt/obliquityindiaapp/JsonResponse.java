package saurabhkt.obliquityindiaapp;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class JsonResponse {
	
	public List<Events> events;
	public List<Feeds> feeds;
	
	@SerializedName("lasteId")
	public int lastEventId;
	
	@SerializedName("lastfId")
	public int lastFeedId;
}
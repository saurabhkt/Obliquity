package saurabhkt.obliquityindiaapp;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class DataHandler {
	
	private static JsonResponse response;
	private static List<Feeds> feeds;
	private static List<Events> events;
	private static int lasteID;
	private static int lastfID;
	
	private static boolean completed = false;
	private static boolean success = false;
	private static String errorText;
	
	private static String url ="http://aeonphpfile.site88.net/obliquity/helpers/jsonResult.php";
	private static String TAG = "DataHandlerTAG";
	
	// PUBLIC GETTERS
	public List<Feeds> getFeeds() { return feeds; }
	public List<Events> getEvents() { return events; }
	public int getLastEventId() { return lasteID; }
	public int getLastFeedId() { return lastfID; }
	
	public boolean completed() { return completed; }
	public boolean isSuccess() { return success; }
	public String getErrorText() { return errorText; }
	
	// CONSTRUCTOR
	public static void downloadOnThrad() {
		
		final Handler handler = new Handler() {
			
			@Override
			public void handleMessage(Message message) {
				Log.i(TAG, "Handler Message Recieved");
				completed = true;
		        feeds = response.feeds;
		        events = response.events;
		        lasteID = response.lastEventId;
		        lastfID = response.lastFeedId;
			}
		};
		
		Thread thread = new Thread() {
			public void run() {
				try {
					Thread.sleep(4000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.i("DataHandlerClass", "Thread sleep complete");
				refreshResponse();
				Log.i("DataHandlerClass", "Download Complete");
				handler.sendEmptyMessage(0);
			}
		};
		
		thread.start();
		
	}
	
	// Parse JSON
	private static void refreshResponse() {
		
		InputStream source = retrieveStream(url);
		Reader reader = new InputStreamReader(source);	
	    Gson gson = new Gson();
	    try {
	    	response = gson.fromJson(reader, JsonResponse.class);
	    } 
	    catch (JsonSyntaxException e) {
	    	Log.w(TAG, "JsonSyntaxException :" + e.toString());
	    	success = false;
	    	errorText = "Json Syntax Error";
	    }
	    catch (JsonIOException e) {
	    	Log.w(TAG, "JsonIOException :" + e.toString());
	    	success = false;
	    	errorText = "Json IO Error";
	    }
	}
	
	// Download the data
	private static InputStream retrieveStream(String url) {

    	HttpClient client = new DefaultHttpClient();
    	HttpGet getRequest = new HttpGet(url);

    	try {
    		HttpResponse getResponse = client.execute(getRequest);
    		final int statusCode = getResponse.getStatusLine().getStatusCode();

    		if(statusCode != HttpStatus.SC_OK) {
    			Log.w("Error in class FEED", "Error " + statusCode + "for URL" + url);
    			errorText = "Server unavailable at the moment";
    			success = false;
    			return null;
    		}
    		
    		HttpEntity getResponseEntity = getResponse.getEntity();
    		success = true;
    		return getResponseEntity.getContent();
    	} catch (IOException e) {
    		getRequest.abort();
    		Log.w("ObliquityHTTPError", "Error IOException thrown" + e.getMessage());
    		errorText = "Internet Access not available";
    		success = false;
    	}
    	
    	success = false;
    	return null;
    	
    }
}

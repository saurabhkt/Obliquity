package saurabhkt.obliquityindiaapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.content.SharedPreferences;
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
	
	private static Context context;
	private static String rawJSON;
	
	private static boolean completed = false;
	private static boolean success = false;
	private static String errorText = "Connection Problem";
	
	private static final String url ="http://aeonphpfile.site88.net/obliquity/helpers/jsonResult.php";
	private static final String TAG = "DataHandlerTAG";
	private static final String PREFS_NAME = "ObliquityPreferences";
	private static final String JSON_CACHE = "jsonCache";
	
	// PUBLIC GETTERS
	public List<Feeds> getFeeds() { return feeds; }
	public List<Events> getEvents() { return events; }
	public int getLastEventId() { return lasteID; }
	public int getLastFeedId() { return lastfID; }
	
	public boolean completed() { return completed; }
	public boolean isSuccess() { return success; }
	public String getErrorText() { return errorText; }
	
	// CONSTRUCTOR
	public DataHandler(Context mContext) {
		context = mContext;
	}
	
	public static void setResponseVariables() {
		Log.i(TAG, "Setting Response Variables Success : "+ success);
        feeds = response.feeds;
        events = response.events;
        lasteID = response.lastEventId;
        lastfID = response.lastFeedId;
	}
	
	public static void downloadOnThrad() {
		
		final Handler handler = new Handler() {
			
			@Override
			public void handleMessage(Message message) {
				Log.i(TAG, "download on thread handler starts");
				completed = true;
				if(success) {
					setResponseVariables();
				}
			}
		};
		
		Thread thread = new Thread() {
			public void run() {
				Log.i("DataHandlerClass", "Thread sleep complete");
				refreshResponse(null);
				Log.i("DataHandlerClass", "Download Complete");
				handler.sendEmptyMessage(0);
			}
		};
		
		thread.start();
		
	}
	
	public static boolean loadFromMemory() {
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		String json = settings.getString(JSON_CACHE, null);
		
		if(json == null) {
			Log.i(TAG, "Loaded from memory : " + json);
			return false;
		} else {
			refreshResponse(json);
			return true;
		}
	}
	
	// Parse JSON
	private static void refreshResponse(String memCache) {
		
		Log.i(TAG, "Refreshing response with memcache : " + memCache);
		String source = null;
		
		if(memCache == null) {
			try {
				source = retrieveJson(url);
			} catch (ConnectTimeoutException e) {
				Log.i(TAG, "Connect Timeout Exception thrown");
				success = false;
				e.printStackTrace();
				return;
			}
		}
		else
			source = memCache;
		
		
		if(source != null) {
		    Gson gson = new Gson();
		    try {
		    	Log.i(TAG, "Parsing JSON : " + source);
		    	response = gson.fromJson(source, JsonResponse.class);
		    	success = true;
		    	setResponseVariables();
		    	return;
		    } 
		    catch (JsonSyntaxException e) {
		    	Log.w(TAG, "JsonSyntaxException :" + e.toString());
		    	errorText = "Json Syntax Error";
		    }
		    catch (JsonIOException e) {
		    	Log.w(TAG, "JsonIOException :" + e.toString());
		    	errorText = "Json IO Error";
		    }
		}
		
	    success = false;
	}
	
	// Download the data
	private static String retrieveJson(String url) throws ConnectTimeoutException {
    	
    	HttpParams httpParameters = new BasicHttpParams();
    	int timeoutConnection = 6000;
    	HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
    	int timeoutSocket = 5000;
    	HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
    	
    	HttpClient client = new DefaultHttpClient(httpParameters);
    	HttpGet getRequest = new HttpGet(url);

    	try {
    		Log.i(TAG, "Starting HTTP Request");
    		HttpResponse getResponse = client.execute(getRequest);
    		final int statusCode = getResponse.getStatusLine().getStatusCode();

    		if(statusCode != HttpStatus.SC_OK) {
    			Log.w("Error in class FEED", "Error " + statusCode + "for URL" + url);
    			errorText = "Server unavailable at the moment";
    			completed = true;
    			success = false;
    			return null;
    		}
    		
    		String line = "";
    		StringBuilder total = new StringBuilder();
    		
    		HttpEntity getResponseEntity = getResponse.getEntity();
    		
    		BufferedReader reader = new BufferedReader(new InputStreamReader(getResponseEntity.getContent()));	
    		
    		while((line = reader.readLine()) != null) {
    			total.append(line);
    		}
    		
    		line = total.toString();
    		Log.i(TAG, "downloaded json : " + line);
    		rawJSON = line;
    		success = true;
    		return line;
    	
    	} catch (IOException e) {
    		getRequest.abort();
    		Log.w(TAG, "Error IOException thrown : " + e.getMessage());
    		errorText = "Internet Access not available.";
    	} catch (Exception e) {
     		Log.w(TAG, "HTTP Exception : " + e.toString());
    	}
 	
    	completed = true;
    	success = false;
    	return null;
    	
    }
	
	public static void saveData() {
		if(rawJSON == null)
			return;
		
		Log.i(TAG, "Saving JSON data");
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(JSON_CACHE, rawJSON);
		
		editor.commit();
	}
}

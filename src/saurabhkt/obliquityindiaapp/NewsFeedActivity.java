package saurabhkt.obliquityindiaapp;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

public class NewsFeedActivity extends Activity {
	
	List<String> values;
	DataHandler dHandler;
	
	boolean answer;
	List<Feeds> feeds;
	
	private String TAG = "NewsFeedActivityTAG";
	TextView statusText;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.newsfeed);
        
		feeds = new ArrayList<Feeds>();
        
        dHandler = new DataHandler(this.getApplicationContext());
        statusText = (TextView)findViewById(R.id.newsFeedStatus);
        statusText.setText("Downloading Data...");
        setValues();
    
    }
    
    public void setValues() {
    	values = new ArrayList<String>();     
    	
    	if(dHandler.completed()) {
    		downloadCallback(false);
    		return;
    	}
    	
    	final Handler handler = new Handler() {
			
			@Override
			public void handleMessage(Message message) {
				Log.i(TAG, "Download completed, Thread returns");
				downloadCallback(false);
			}
		};
		
		Thread thread = new Thread() {
			public void run() {
				while(!dHandler.completed()) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				handler.sendEmptyMessage(0);
			}
		};
		
		thread.start();
    	
    	
    }
    
    public void downloadCallback(boolean memory) {
    	
    	Log.i(TAG, "DownloadCallback initiated with memory : " + memory);
    	
    	if(dHandler.isSuccess() || memory) {
    		
			if(dHandler.isSuccess()) {
				DataHandler.saveData();
				feeds = dHandler.getFeeds();
				Log.i(TAG, "feeds set from getFeeds");
			} else {
				
				AlertDialog alertDialog = new AlertDialog.Builder(NewsFeedActivity.this).create();
				
				alertDialog.setTitle("Retry ?");
				alertDialog.setMessage("Download Unsuccessful. Load previous feeds?");
				
				alertDialog.setButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Log.i(TAG, "Yes selected");
						
						boolean success = DataHandler.loadFromMemory();
						
						Log.i(TAG, "Loading from memory status : " + success);
						if(success) {
							feeds = dHandler.getFeeds(); //successfully loaded from memory
							setUpAdapter();
						} else {
							//nothing to load in memory
							statusText.setText("There is a problem with your internet connection and there was no saved data to load");
							statusText.setTextSize(15);
							return;
						}
					}
				});
				
				alertDialog.setButton2("Retry", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Log.i(TAG, "Retry selected");
						setValues();
					}
				});
				
				alertDialog.show();
			}
		    
			setUpAdapter();
    	
    	} else {
    		statusText.setText(dHandler.getErrorText());
    		downloadCallback(true);
    	}
    }
    
    public void setUpAdapter() {
    	
		for(Feeds feed : feeds)
	    {
	    	values.add(feed.message);
	    }
	        
	    FeedListAdapter adapter = new FeedListAdapter(NewsFeedActivity.this, values);
		ListView lView = (ListView)findViewById(R.id.list);
		lView.setAdapter(adapter);	
		
		statusText.setVisibility(View.GONE);
    }

	private boolean askRetry() {
		
		AlertDialog alertDialog = new AlertDialog.Builder(NewsFeedActivity.this).create();
		
		alertDialog.setTitle("Retry ?");
		alertDialog.setMessage("Download Unsuccessful. Load previous feeds?");
		
		alertDialog.setButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				answer = true;
			}
		});
		
		alertDialog.setButton("Retry", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				answer = false;
			}
		});
		
		alertDialog.show();
		
		Log.i(TAG, "Returning askRetry : " + answer);
		
		return answer;
	}
    
}
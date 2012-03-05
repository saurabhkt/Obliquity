package saurabhkt.obliquityindiaapp;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
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
	
	private String TAG = "NewsFeedActivityTAG";
	TextView statusText;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.newsfeed);
        
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
    		
			List<Feeds> feeds = new ArrayList<Feeds>();
    		
			if(dHandler.isSuccess()) {
				feeds = dHandler.getFeeds();
				Log.i(TAG, "feeds set from getFeeds");
			} else {
				
				boolean success = DataHandler.loadFromMemory();
				Log.i(TAG, "Loading from memory status : " + success);
				if(success) {
					feeds = dHandler.getFeeds();
				} else {
					statusText.setText("There is a problem with your internet connection and there was no saved data to load");
					statusText.setTextSize(15);
					return;
				}
			}
		    
		        for(Feeds feed : feeds)
		        {
		        	values.add(feed.message);
		        }
		        
		    FeedListAdapter adapter = new FeedListAdapter(NewsFeedActivity.this, values);
			ListView lView = (ListView)findViewById(R.id.list);
			lView.setAdapter(adapter);	
			
			statusText.setVisibility(View.GONE);
    	
    	} else {
    		statusText.setText(dHandler.getErrorText());
    		downloadCallback(true);
    	}
    }
    
}
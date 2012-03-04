package saurabhkt.obliquityindiaapp;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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
        
        dHandler = new DataHandler();
        statusText = (TextView)findViewById(R.id.newsFeedStatus);
        statusText.setText("Downloading Data...");
        setValues();
    
    }
    
    public void setValues() {
    	values = new ArrayList<String>();     
    	
    	if(dHandler.completed()) {
    		downloadCallback();
    		return;
    	}
    	
    	final Handler handler = new Handler() {
			
			@Override
			public void handleMessage(Message message) {
				Log.i(TAG, "Download completed, Thread returns");
				downloadCallback();
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
    
    public void downloadCallback() {
    	
    	List<Feeds> feeds = new ArrayList<Feeds>();
        feeds = dHandler.getFeeds();
        
	        for(Feeds feed : feeds)
	        {
	        	values.add(feed.message);
	        }
	        
        FeedListAdapter adapter = new FeedListAdapter(NewsFeedActivity.this, values);
		ListView lView = (ListView)findViewById(R.id.list);
		lView.setAdapter(adapter);	
    }
    
}
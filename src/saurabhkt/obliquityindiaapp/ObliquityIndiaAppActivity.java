package saurabhkt.obliquityindiaapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class ObliquityIndiaAppActivity extends Activity {
    /** Called when the activity is first created. */
	
	Context mContext;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        
        DataHandler.downloadOnThrad();
        
        mContext = getApplicationContext();
        
        final Button bfeed = (Button) findViewById(R.id.idnewsfeedbutton);
        bfeed.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent intentfeedact = new Intent(ObliquityIndiaAppActivity.this, NewsFeedActivity.class);
            	startActivity(intentfeedact);
            }
        });
        
        final Button bcalendar = (Button) findViewById(R.id.idcalendarbutton);
        bcalendar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent intentcalendaract = new Intent(mContext, SimpleCalendarViewActivity.class);
            	startActivity(intentcalendaract);           	
            }
        });
            
        final Button baboutobliquity = (Button) findViewById(R.id.idaboutobliquitybutton);
        baboutobliquity.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent intentaboutobliquityact = new Intent(mContext, AboutObliquityActivity.class);
            	startActivity(intentaboutobliquityact);
            }
        });
        
        final Button baboutxd = (Button) findViewById(R.id.idaboutxdbutton);
        baboutxd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent intentaboutxdact = new Intent(mContext, AboutXDActivity.class);
            	startActivity(intentaboutxdact);	
            }
        });
    }	
}
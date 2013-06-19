package com.example.gpics;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;

public class GetPicture extends Activity {
	private static final int CAMERA_PIC_REQUEST = 1337;
	private ImageView imageView;
	private TextView textView;
	private static boolean picFlag = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent intent = getIntent();
        String coordinates = intent.getStringExtra(MainActivity.GPS_COORDINATES);
        System.out.println("Coordinates: " + coordinates);
                        
        setContentView(R.layout.activity_get_picture);
        
        this.textView = (TextView)this.findViewById(R.id.gpsText);
        textView.setTextSize(17);
        textView.setText(coordinates);
        
        this.imageView = (ImageView)this.findViewById(R.id.photoResultView);
        Button photoButton = (Button) this.findViewById(R.id.takePicButton);
        photoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            	Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
            	startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST); 
            }
        });
//        if(!picFlag){
//        	picFlag = true;
//        	photoButton.performClick();
//    	}
        //photoButton.callOnClick();
    }
    
    

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        if (requestCode == CAMERA_PIC_REQUEST && resultCode == RESULT_OK) {  
            Bitmap photo = (Bitmap) data.getExtras().get("data"); 
            imageView.setImageBitmap(photo);
        }  
    }

//	@SuppressLint("NewApi")
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		
//		//Get the picture from the intent
//		//Intent intent = getIntent();
//		
//		
//		//ImageView photoResultView = new ImageView(this);
//		
//		//setContentView(photoResultView);
//		
//		 setContentView(R.layout.activity_get_picture);
//	     this.imageView = (ImageView)this.findViewById(R.id.photoResultView);
//		
//		//setContentView(R.layout.activity_get_picture);
//		// Show the Up button in the action bar.
//		//setupActionBar();
//	}
//
////	/**
////	 * Set up the {@link android.app.ActionBar}, if the API is available.
////	 */
////	@SuppressLint("NewApi")
////	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
////	private void setupActionBar() {
////		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
////			getActionBar().setDisplayHomeAsUpEnabled(true);
////		}
////	}
////
////	@Override
////	public boolean onCreateOptionsMenu(Menu menu) {
////		// Inflate the menu; this adds items to the action bar if it is present.
////		getMenuInflater().inflate(R.menu.get_picture, menu);
////		return true;
////	}
////
////	@Override
////	public boolean onOptionsItemSelected(MenuItem item) {
////		switch (item.getItemId()) {
////		case android.R.id.home:
////			// This ID represents the Home or Up button. In the case of this
////			// activity, the Up button is shown. Use NavUtils to allow users
////			// to navigate up one level in the application structure. For
////			// more details, see the Navigation pattern on Android Design:
////			//
////			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
////			//
////			NavUtils.navigateUpFromSameTask(this);
////			return true;
////		}
////		return super.onOptionsItemSelected(item);
////	}
//		
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
//        if (requestCode == CAMERA_PIC_REQUEST) {  
//        	Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
//        	ImageView image = (ImageView) findViewById(R.id.photoResultView);  
//        	image.setImageBitmap(thumbnail); 
//        } 
//        else
//        	System.out.println("Failure :(");
//    } 

}

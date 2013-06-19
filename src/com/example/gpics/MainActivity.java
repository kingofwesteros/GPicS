package com.example.gpics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Timer;


import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener, SensorEventListener, LocationListener{

	
	Button 										m_button__getButton, m_button__resetButton;
	//CheckBox									m_checkBox__accelerometer, m_checkBox__orientation, m_checkBox__compass, 
	//											m_checkBox__gps, m_checkBox__cell_id, m_checkBox__wifi;
	
	CheckBox									m_checkBox__gps;

	TextView									m_textView__about;
	Timer 										m_timer__sensorTrigger;
	BufferedWriter								m_bufferedWriter__output_xml;
	long										m_l__file_open_time, m_l__max_file_open_time;
	
//	float										m_f__acc_x, m_f__acc_y, m_f__acc_z;
//	float										m_f__compass_x, m_f__compass_y, m_f__compass_z;
//	float										m_f__orientation_x, m_f__orientation_y, m_f__orientation_z;
	double										m_d__longitude, m_d__latitude, m_d__altitude;
	
//	boolean										m_bool__accelerometer, m_bool__orientation, m_bool__compass,
//												m_bool__gps, m_bool__cell_id, m_bool__wifi;;
												
	boolean										m_bool__gps;											
	
	
	Handler										m_handler__main;	
	int											m_i__polling_short_interval, m_i__polling_medium_interval, m_i__polling_long_interval;
	CSensorReadingTaskForShortIntervals			m_cSensorReadingTaskForShortIntervals__main;
	CSensorReadingTaskForMediumIntervals		m_cSensorReadingTaskForMediumIntervals__main;
	CSensorReadingTaskForLongIntervals			m_cSensorReadingTaskForLongIntervals__main;
	
	SensorManager					 			m_sensorManager__main;
    LocationManager 							m_locationManager__main;
	TelephonyManager							m_telephonyManager__main;
	WifiManager									m_wifiManager__main;
	private boolean registerListener;
	
	public final static String GPS_COORDINATES = "com.example.gpics.COORDINATES";
	public static double GPS_LATITUDE = 0.00;
	public static double GPS_LONGITUDE = 0.00;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate( savedInstanceState );
		
		m_handler__main = new Handler();
		m_i__polling_short_interval = 1;															// 1 ms
		m_i__polling_medium_interval = 5 * 1000;													// 10 s
		m_i__polling_long_interval = 5 * 60 * 1000;												// 10 mins
		m_l__max_file_open_time = 20 * 60 * 1000;
		m_cSensorReadingTaskForShortIntervals__main = new CSensorReadingTaskForShortIntervals();
		m_cSensorReadingTaskForMediumIntervals__main = new CSensorReadingTaskForMediumIntervals();
		m_cSensorReadingTaskForLongIntervals__main = new CSensorReadingTaskForLongIntervals();
        m_sensorManager__main = (SensorManager)getSystemService( SENSOR_SERVICE );					// get reference to SensorManager
        m_locationManager__main = (LocationManager)getSystemService( LOCATION_SERVICE );
        m_telephonyManager__main = (TelephonyManager)getSystemService( TELEPHONY_SERVICE );
		m_wifiManager__main = (WifiManager)getSystemService( WIFI_SERVICE );
	
		setContentView( R.layout.activity_main );
		
		m_button__getButton = (Button) findViewById( R.id.button__getButton );
		m_button__resetButton = (Button) findViewById( R.id.button__resetButton );
		m_textView__about = (TextView) findViewById( R.id.textView__about );
		//m_checkBox__accelerometer = (CheckBox) findViewById( R.id.checkBox__accelerometer );
		//m_checkBox__orientation = (CheckBox) findViewById( R.id.checkBox__orientation );
		//m_checkBox__compass = (CheckBox) findViewById( R.id.checkBox__compass );
		//m_checkBox__cell_id = (CheckBox) findViewById( R.id.checkBox__cellid );
		m_checkBox__gps = (CheckBox) findViewById( R.id.checkBox__gps );
		//m_checkBox__wifi = (CheckBox) findViewById( R.id.checkBox__wifi );
		
		m_button__getButton.setOnClickListener( this );
		m_button__resetButton.setOnClickListener( this );
		
		m_d__longitude = 0;
		m_d__latitude = 0;
		m_d__altitude = 0;
		
		return;
	} //method

	
	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	public void onClick( View view__clickedItem )
	{
		String 		s__filename, s__path, s__full_filename;
		int			i__c1;
		File		file__op_xml;
		
		
		// "Start Sensing" clicked
		if( m_button__getButton == view__clickedItem )
		{
			//m_bool__accelerometer = m_checkBox__accelerometer.isChecked();
			//m_bool__orientation = m_checkBox__orientation.isChecked();
			//m_bool__compass = m_checkBox__compass.isChecked();
			
			// TODO Check this line here
			m_checkBox__gps.setChecked(true);
			m_bool__gps = m_checkBox__gps.isChecked();
			
			//m_bool__cell_id = m_checkBox__cell_id.isChecked();
			//m_bool__wifi = m_checkBox__wifi.isChecked();
			
			try
			{
				s__path = "/sdcard/sdcard/RawDataCollector/";
				s__filename = "datafile";
				i__c1 = 1001;
				do
				{
					file__op_xml = new File( s__path + s__filename + i__c1 + ".xml" );
					i__c1++;
				} while ( file__op_xml.exists() );
				(new File( s__path )).mkdirs();
				m_bufferedWriter__output_xml = new BufferedWriter( new FileWriter( file__op_xml ) );
				m_l__file_open_time = System.currentTimeMillis();
				
				m_bufferedWriter__output_xml.write( "<r>\n" );
				
			}
			catch (Exception e)
			{
				m_textView__about.setText( "Error: " + e.getMessage() );
			}
			
			m_button__getButton.setVisibility( 4 );		// INVISBLE
			m_button__resetButton.setVisibility( 0 );		// VISIBLE
			m_handler__main.removeCallbacks( m_cSensorReadingTaskForShortIntervals__main );
			m_handler__main.postDelayed( m_cSensorReadingTaskForShortIntervals__main, m_i__polling_short_interval );
			m_handler__main.removeCallbacks( m_cSensorReadingTaskForMediumIntervals__main );
			m_handler__main.postDelayed( m_cSensorReadingTaskForMediumIntervals__main, m_i__polling_medium_interval );
			m_handler__main.removeCallbacks( m_cSensorReadingTaskForLongIntervals__main );
			m_handler__main.postDelayed( m_cSensorReadingTaskForLongIntervals__main, m_i__polling_long_interval );
		}
		
		// "Stop Sensing" clicked
		if( m_button__resetButton == view__clickedItem )
		{
			m_button__getButton.setVisibility( 0 );		// VISIBLE
			m_button__resetButton.setVisibility( 4 );		// INVISBLE
			m_handler__main.removeCallbacks( m_cSensorReadingTaskForShortIntervals__main );
			m_handler__main.removeCallbacks( m_cSensorReadingTaskForMediumIntervals__main );
			m_handler__main.removeCallbacks( m_cSensorReadingTaskForLongIntervals__main );
			m_checkBox__gps.setChecked(false);

			
			try
			{
				if( m_bufferedWriter__output_xml != null )
					m_bufferedWriter__output_xml.write( "</r>" );
					m_bufferedWriter__output_xml.close();
			}
			catch (Exception e)
			{
				m_textView__about.setText( "Error: " + e.getMessage() );
			}

		}		
		
		return;	
	} //method



	private static final int matrix_size = 16;
	float[] RotationMatrix = new float[matrix_size];
	float[] outR = new float[matrix_size];
	float[] I = new float[matrix_size];
	float[] values = new float[3];

	float[] mags;
	float[] accels;
	float[] orient;
	
	String output;
	boolean isReady;
	
	/// SensorListner Interface - Start
	public void onSensorChanged(SensorEvent event) {
	
//		if( event.sensor.getType() == Sensor.TYPE_ORIENTATION && m_checkBox__orientation.isChecked())
//		{
//			m_f__orientation_x = event.values[0];
//			m_f__orientation_y = event.values[1];
//			m_f__orientation_z = event.values[2];		
//			
//			String output = "O Z: " + m_f__orientation_x + ", \nO X: " + m_f__orientation_y + ", \nO Y: " + m_f__orientation_z;
//			m_textView__about.setText( output);
//
//		}
//		if( event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && m_checkBox__accelerometer.isChecked())
//		{
//			m_f__acc_x = event.values[0];
//			m_f__acc_y = event.values[1];
//			m_f__acc_z = event.values[2];
//			
//			String output = "Accel X: " + m_f__acc_x + ", \nAccel Y: " + m_f__acc_y + ", \nAccel Z: " + m_f__acc_z;
//			m_textView__about.setText( output);
//			
//		}
		
//		if(  event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD && m_checkBox__compass.isChecked())
//		{
//			m_f__compass_x = event.values[0];
//			m_f__compass_y = event.values[1];
//			m_f__compass_z = event.values[2];
//			m_textView__about.setText( "Mag X: " + m_f__compass_x + ", \nMag Y: " + m_f__compass_y + ", \nMag Z: " + m_f__compass_z );
//		}	

			

		return;
	} //method

	
	
	public void onAccuracyChanged( int i__sensor, int i__accuracy )
	{
		return;
	} //method
	/// SensorListner Interface - End

	
	
	
	/// LocationListner Interface - Start
	//@Override
	public void onLocationChanged( Location location__in )
	{
		if( location__in != null && m_checkBox__gps.isChecked())
		{
			m_textView__about.setText( "Latitude=\t" + location__in.getLatitude() + 
									   "\nLongitude=\t" + location__in.getLongitude() ); 
//									   "\nHas speed=\t" + location__in.hasSpeed() + 
//									   "\nSpeed=\t" + location__in.getSpeed());
									   
			m_d__longitude = location__in.getLongitude();
			m_d__latitude = location__in.getLatitude();
			
			GPS_LATITUDE = m_d__latitude;
			GPS_LONGITUDE = m_d__longitude;
		}
		return;
	}

	
	
	//@Override
	public void onProviderDisabled( String provider )
	{
		// TODO Auto-generated method stub
		return;
	}

	//@Override
	public void onProviderEnabled( String provider )
	{
		// TODO Auto-generated method stub
		return;
	}

	//@Override
	public void onStatusChanged( String provider, int status, Bundle extras)
	{
		// TODO Auto-generated method stub
		return;
	}
	/// LocationListner Interface - End
	

	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		// register this class as a listener for the orientation and accelerometer sensors
		
		//m_sensorManager__main.registerListener(this, m_sensorManager__main.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
		//m_sensorManager__main.registerListener(this, m_sensorManager__main.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_FASTEST);
		//m_sensorManager__main.registerListener(this, m_sensorManager__main.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_FASTEST);
		
        m_locationManager__main.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, this );

		return;	
	} //method
    
	
	@Override
	protected void onStop()
	{
		// unregister listener
		m_sensorManager__main.unregisterListener(this);
		super.onStop();
		return;
	} //method




    //class SensorReadingTask extends TimerTask
	class CSensorReadingTaskForShortIntervals implements Runnable
	{
		public void run()
		{
			String 		s__entry;
			
			m_handler__main.removeCallbacks( this );
			m_handler__main.postDelayed( this, m_i__polling_short_interval );
			//m_timer__sensorTrigger.schedule( new SensorReadingTask(),	m_i__polling_interval );

			if( m_bufferedWriter__output_xml != null )
			{
				s__entry = "<datapoint time=\"" + System.currentTimeMillis() + "\">\n";
//				if( m_bool__accelerometer )
//					s__entry += "\t<acccelerometer x=\"" + m_f__acc_x + "\" y=\"" + m_f__acc_y + "\" z=\"" + m_f__acc_z + "\" />\n";
//				if( m_bool__orientation )
//					s__entry += "\t<orientation x=\"" + m_f__orientation_x + "\" y=\"" + m_f__orientation_y + "\" z=\"" + m_f__orientation_z + "\" />\n";
//				if( m_bool__compass )
//					s__entry += "\t<compass x=\"" + m_f__compass_x + "\" y=\"" + m_f__compass_y + "\" z=\"" + m_f__compass_z + "\" />\n";
//				s__entry += "</datapoint>\n";

				try
				{
					m_bufferedWriter__output_xml.write( s__entry );
				}
				catch (Exception e)
				{
					m_textView__about.setText( "Error: " + e.getMessage() );
				}
			}
	
			return;
        } //method
    }//class


	class CSensorReadingTaskForMediumIntervals implements Runnable
	{
		public void run()
		{
			List<ScanResult>				list_scanResult__wifi;
			ScanResult						scanResult__t1;
			String 							s__entry;
			int								i__c1;

			
			
			
			m_handler__main.removeCallbacks( this );
			m_handler__main.postDelayed( this, m_i__polling_medium_interval );

			if( m_bufferedWriter__output_xml != null )
			{
				s__entry = "<datapoint time=\"" + System.currentTimeMillis() + "\">\n";
//				if( m_bool__wifi )
//				{
//					/*m_wifiManager__main.startScan();
//					list_scanResult__wifi = m_wifiManager__main.getScanResults();
//					s__entry += "\t<Wifi ssid=\"" + m_wifiManager__main.getConnectionInfo().getSSID() + "\">\n";
//					for( i__c1=0; i__c1<list_scanResult__wifi.size(); i__c1++ )
//					{
//						scanResult__t1 = list_scanResult__wifi.get( i__c1 );
//						s__entry += "\t\t<AP ssid=\"" + scanResult__t1.SSID + "\" bssid=\"" + scanResult__t1.BSSID + "\" rssi=\"" + scanResult__t1.level + "\"/>\n";
//					}
//					s__entry += "\t</Wifi>\n";*/
//				}
				
				s__entry += "</datapoint>\n";

				try
				{
					m_bufferedWriter__output_xml.write( s__entry );
				}
				catch (Exception e)
				{
					m_textView__about.setText( "Error: " + e.getMessage() );
				}
			}
	
			return;
        } //method
    }//class




	class CSensorReadingTaskForLongIntervals implements Runnable
	{
		public void run()
		{
			GsmCellLocation 				gsmCellLocation__t1;
			//CdmaCellLocation 				cdmaCellLocation__t1;
			String 							s__entry;
			String 							s__filename, s__path, s__full_filename;
			int								i__c1;
			File							file__op_xml;
			
			
			
			m_handler__main.removeCallbacks( this );
			m_handler__main.postDelayed( this, m_i__polling_long_interval );
			//m_timer__sensorTrigger.schedule( new SensorReadingTask(),	m_i__polling_interval );
						
			
			
			
			if( m_bufferedWriter__output_xml != null )
			{
				if( (System.currentTimeMillis() - m_l__file_open_time) > m_l__max_file_open_time )
				{
					m_handler__main.removeCallbacks( m_cSensorReadingTaskForShortIntervals__main );
					m_handler__main.removeCallbacks( m_cSensorReadingTaskForMediumIntervals__main );
					m_handler__main.removeCallbacks( m_cSensorReadingTaskForLongIntervals__main );
						
					try
					{
						m_bufferedWriter__output_xml.close();
						s__path = "/sdcard/sdcard/RawDataCollector/";
						s__filename = "datafile";
						i__c1 = 1001;
						do
						{
							file__op_xml = new File( s__path + s__filename + i__c1 + ".xml" );
							i__c1++;
						} while ( file__op_xml.exists() );
						(new File( s__path )).mkdirs();
						m_bufferedWriter__output_xml = new BufferedWriter( new FileWriter( file__op_xml ) );
						m_l__file_open_time = System.currentTimeMillis();
					}
					catch (Exception e)
					{
						m_textView__about.setText( "Error: " + e.getMessage() );
					}

					m_handler__main.postDelayed( m_cSensorReadingTaskForShortIntervals__main, m_i__polling_short_interval );
					m_handler__main.postDelayed( m_cSensorReadingTaskForMediumIntervals__main, m_i__polling_medium_interval );
					m_handler__main.postDelayed( m_cSensorReadingTaskForLongIntervals__main, m_i__polling_long_interval );			
				}
			
			
			
				s__entry = "<datapoint time=\"" + System.currentTimeMillis() + "\">\n";
				if( m_bool__gps )
					s__entry += "\t<GPS long=\"" + m_d__longitude + "\" lat=\"" + m_d__latitude + "\" alt=\"" + m_d__altitude + "\" />\n";
//				if( m_bool__cell_id )
//				{
//					if( m_telephonyManager__main.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM )
//					{
//						gsmCellLocation__t1 = (GsmCellLocation)m_telephonyManager__main.getCellLocation();
//						s__entry += "\t<CellID cid=\"" + gsmCellLocation__t1.getCid() + "\" lac=\"" + gsmCellLocation__t1.getLac() + "\" />\n";
//					}
//					/*
//					else if( m_telephonyManager__main.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA )
//					{
//						cdmaCellLocation = (CdmaCellLocation)m_telephonyManager__main.getCellLocation();
//						s__entry += "\t<CellID basestationid=\"" + cdmaCellLocation__t1.getBaseStationId() + "\" />\n";
//					}
//					*/
//				}
				
				s__entry += "</datapoint>\n";

				try
				{
					m_bufferedWriter__output_xml.write( s__entry );
				}
				catch (Exception e)
				{
					m_textView__about.setText( "Error: " + e.getMessage() );
				}
			}
	
			return;
        } //method
    }//class


	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
	
    private static final int CAMERA_PIC_REQUEST = 1337;  
    
	public void takePicture(View view) {
 
		Intent cameraIntent = new Intent(this, GetPicture.class);

		String latStr = String.valueOf(GPS_LATITUDE);
		String lngStr = String.valueOf(GPS_LONGITUDE);
		
		String coordinates = new String();
		if(GPS_LATITUDE + GPS_LONGITUDE != 0)
			coordinates = latStr + ", " + lngStr;
		else
			coordinates = "No coordinates found";
		//String coordinates = "Testing 00.00";
		
		cameraIntent.putExtra(GPS_COORDINATES, coordinates);
	    startActivity(cameraIntent);
	     
	}
	 


}

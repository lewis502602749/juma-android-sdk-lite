package com.seed.project;

import java.util.UUID;

import juma.sdk.lite.JumaDevice;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		JumaDevice device = new JumaDevice() {
			
			@Override
			public void onUpdateFirmware(int status) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStopScan() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onSend(int status) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onMessage(byte type, byte[] message) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onError(Exception e, int status) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onDiscover(String name, UUID uuid, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onDisconnect(UUID uuid, int status) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onConnect(UUID uuid, int status) {
				// TODO Auto-generated method stub
				
			}
		};
		
		device.init(getApplicationContext());
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}

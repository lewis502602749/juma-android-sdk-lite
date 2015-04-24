package com.juma.demo;

import java.text.SimpleDateFormat;
import java.util.UUID;

import juma.sdk.lite.Discover;
import juma.sdk.lite.JumaSocket;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MainActivity extends Activity {

	public static final String DEVICE_NAME = "ECHO DEMO";

	public static final String MSG_TYPE_HEX = "HEX";

	public static final String MSG_TYPE_ASCII = "ASCII";

	private static final byte messageType = 0x01;

	private ListView lvMessage = null;

	private RadioGroup rgInput = null;

	private EditText etConnect = null, etSend = null;

	private Button btnConnect = null, btnSend = null;

	private ArrayAdapter<String> listAdapter = null;

	private String inputMsgType = null;

	private String deviceName = null;

	private JumaSocket socket = null;

	private Discover discover = null;

	private UUID deviceUuid = null;
	
	private boolean isConnected = false;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initView();

		setViewListener();

		initMsgType();

		initDiscover();

		initSocket();

	}

	private void initView(){

		lvMessage = (ListView) findViewById(R.id.lvMessage);
		lvMessage.setDivider(null);

		rgInput = (RadioGroup) findViewById(R.id.rgInput);		

		etConnect = (EditText) findViewById(R.id.etConnect);
		etSend = (EditText) findViewById(R.id.etSend);

		btnConnect = (Button) findViewById(R.id.btnConnect);
		btnSend = (Button) findViewById(R.id.btnSend);

		listAdapter = new ArrayAdapter<String>(this, R.layout.list_item);
		lvMessage.setAdapter(listAdapter);
		

	}

	private void setViewListener(){
		
		rgInput.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

				switch (rgInput.getCheckedRadioButtonId()) {
				case R.id.rbInputAscii:
					inputMsgType = MSG_TYPE_ASCII;
					break;
				case R.id.rbInputHex:
					inputMsgType = MSG_TYPE_HEX;
					break;
				}

			}
		});

		btnConnect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if(isConnected){
					socket.close();
				}else {
					
				deviceName = etConnect.getText().toString();

				if(deviceName != null && !deviceName.equals("")){
					discover.start(MainActivity.this, deviceName);
				}
				
				etConnect.setText("");
				
				}
				
				hideSoftInput(MainActivity.this);

			}
		});

		btnSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				String data = etSend.getText().toString();

				if(inputMsgType.equals(MSG_TYPE_HEX)){
					try {
						socket.send(messageType, hexToByte(data));
					} catch (Exception e) {
						return;
					}
				}else if(inputMsgType.equals(MSG_TYPE_ASCII)){
					try {
						socket.send(messageType, data.getBytes());
					} catch (Exception e) {
						return;
					}
				}

				etSend.setText("");
				
				hideSoftInput(MainActivity.this);

			}
		});
		
	}
	

	private void initMsgType(){

		switch (rgInput.getCheckedRadioButtonId()) {
		case R.id.rbInputAscii:
			inputMsgType = MSG_TYPE_ASCII;
			break;
		case R.id.rbInputHex:
			inputMsgType = MSG_TYPE_HEX;
			break;
		}

	}

	private void initDiscover(){
		discover = new Discover() {

			@Override
			public void onError(Exception exception) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onEnd() {
				// TODO Auto-generated method stub
				if(deviceUuid != null)
					socket.connect(getApplicationContext(), deviceUuid);

			}

			@Override
			public void onDiscovered(UUID uuid, String name, int rssi) {

				deviceUuid = uuid;

			}
		};
	}

	private void initSocket(){
		socket = new JumaSocket() {

			@Override
			public void onOpen() {
				// TODO Auto-generated method stub
			}

			@Override
			public void onMessage(byte messageType, byte[] messageData) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onError(Exception exception) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onClose(int arg0, String arg1, boolean arg2) {
				// TODO Auto-generated method stub
			}
		};
	}

	public static void hideSoftInput(Activity activity){
		((InputMethodManager)activity.getApplication().getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(activity.getCurrentFocus()
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	@Override
	protected void onStart() {

		super.onStart();

		LocalBroadcastManager.getInstance(this).registerReceiver(receiver, getIntentFilter());

	}

	@Override
	protected void onDestroy() {

		super.onDestroy();

		LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);

	}

	private final BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(final Context context, final Intent intent) {

			String action = intent.getAction();

			if(action.equals(Discover.ACTION_DISCOVER_START)){
				runOnUiThread(new Runnable() {

					@Override
					public void run() {

						String currentDate = getCurrentData(context);

						StringBuffer sb = new StringBuffer();
						sb.append("[");
						sb.append(currentDate);
						sb.append("] : discover start");

						listAdapter.add(sb.toString());
						lvMessage.smoothScrollByOffset(listAdapter.getCount() - 1);

					}
				});
			}

			if(action.equals(Discover.ACTION_DISCOVER_END)){
				runOnUiThread(new Runnable() {

					@Override
					public void run() {

						String currentDate = getCurrentData(context);

						StringBuffer sb = new StringBuffer();
						sb.append("[");
						sb.append(currentDate);
						sb.append("] : discover end");

						listAdapter.add(sb.toString());
						lvMessage.smoothScrollByOffset(listAdapter.getCount() - 1);

					}
				});
			}

			if(action.equals(Discover.ACTION_DEVICE_DISCOVERED)){
				runOnUiThread(new Runnable() {

					@Override
					public void run() {

						String currentDate = getCurrentData(context);

						String uuid = intent.getStringExtra(Discover.UUID_STR);
						String name = intent.getStringExtra(Discover.NAME_STR);
						int rssi = intent.getIntExtra(Discover.RSSI_STR, 0);

						StringBuffer sb = new StringBuffer();
						sb.append("[");
						sb.append(currentDate);
						sb.append("] : device discovered ");
						sb.append("\nname : ");
						sb.append(name);
						sb.append("\nuuid : ");
						sb.append(uuid);
						sb.append("\nrssi : ");
						sb.append(rssi);

						listAdapter.add(sb.toString());
						lvMessage.smoothScrollByOffset(listAdapter.getCount() - 1);

					}
				});
			}

			if(action.equals(JumaSocket.ACTION_DEVICE_CONNECTED)){
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						
						isConnected = true;
						
						btnConnect.setText("Disconnect");

						String currentDate = getCurrentData(context);

						String uuid = intent.getStringExtra(JumaSocket.UUID_STR);
						String name = intent.getStringExtra(JumaSocket.NAME_STR);

						StringBuffer sb = new StringBuffer();
						sb.append("[");
						sb.append(currentDate);
						sb.append("] : device connected : ");
						sb.append("\nname : ");
						sb.append(name);
						sb.append("\nuuid : ");
						sb.append(uuid);

						listAdapter.add(sb.toString());
						lvMessage.smoothScrollByOffset(listAdapter.getCount() - 1);

					}
				});
			}

			if(action.equals(JumaSocket.ACTION_DEVICE_DISCONNECTED)){
				runOnUiThread(new Runnable() {

					@Override
					public void run() {

						isConnected = false;
						
						btnConnect.setText("Connect");
						
						String currentDate = getCurrentData(context);

						String uuid = intent.getStringExtra(JumaSocket.UUID_STR);
						String name = intent.getStringExtra(JumaSocket.NAME_STR);

						StringBuffer sb = new StringBuffer();
						sb.append("[");
						sb.append(currentDate);
						sb.append("] : device disconnected : ");
						sb.append("\nname : ");
						sb.append(name);
						sb.append("\nuuid : ");
						sb.append(uuid);

						listAdapter.add(sb.toString());
						lvMessage.smoothScrollByOffset(listAdapter.getCount() - 1);

					}
				});
			}

			if(action.equals(JumaSocket.ACTION_SERVICE_DISCOVERED)){
				runOnUiThread(new Runnable() {

					@Override
					public void run() {

						String currentDate = getCurrentData(context);

						StringBuffer sb = new StringBuffer();
						sb.append("[");
						sb.append(currentDate);
						sb.append("] : service discovered ");

						listAdapter.add(sb.toString());
						lvMessage.smoothScrollByOffset(listAdapter.getCount() - 1);

					}
				});
			}

			if(action.equals(JumaSocket.ACTION_SEND_MESSAGE)){
				runOnUiThread(new Runnable() {

					@Override
					public void run() {

						String currentDate = getCurrentData(context);

						byte[] message = intent.getByteArrayExtra(JumaSocket.MESSAGE_STR);

						StringBuffer sb = new StringBuffer();
						sb.append("[");
						sb.append(currentDate);
						sb.append("] : send message : ");	
						if(inputMsgType.equals(MSG_TYPE_HEX))
							sb.append(byteToHex(message));
						else if(inputMsgType.equals(MSG_TYPE_ASCII))
							sb.append(byteToAscii(message));

						listAdapter.add(sb.toString());
						lvMessage.smoothScrollByOffset(listAdapter.getCount() - 1);

					}
				});
			}

			if(action.equals(JumaSocket.ACTION_RECEIVE_MESSAGE)){
				runOnUiThread(new Runnable() {

					@Override
					public void run() {

						String currentDate = getCurrentData(context);

						byte[] message = intent.getByteArrayExtra(JumaSocket.MESSAGE_STR);

						StringBuffer sb = new StringBuffer();
						sb.append("[");
						sb.append(currentDate);
						sb.append("] : receive message : ");	
						if(inputMsgType.equals(MSG_TYPE_HEX)){
							try {
								sb.append(byteToHex(message));
							} catch (Exception e) {
								return;
							}
						}else if(inputMsgType.equals(MSG_TYPE_ASCII)){
							try {
								sb.append(byteToAscii(message));
							} catch (Exception e) {
								return;
							}
						}

						listAdapter.add(sb.toString());
						lvMessage.smoothScrollByOffset(listAdapter.getCount() - 1);

					}
				});
			}


		}
	};

	private IntentFilter getIntentFilter(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(Discover.ACTION_DISCOVER_START);
		filter.addAction(Discover.ACTION_DISCOVER_END);
		filter.addAction(Discover.ACTION_DEVICE_DISCOVERED);
		filter.addAction(JumaSocket.ACTION_DEVICE_CONNECTED);
		filter.addAction(JumaSocket.ACTION_DEVICE_DISCONNECTED);
		filter.addAction(JumaSocket.ACTION_SERVICE_DISCOVERED);
		filter.addAction(JumaSocket.ACTION_SEND_MESSAGE);
		filter.addAction(JumaSocket.ACTION_RECEIVE_MESSAGE);
		return filter;
	}

	@SuppressLint("SimpleDateFormat")
	private static String getCurrentData(Context context){
		SimpleDateFormat sdf=new SimpleDateFormat("hh:mm:ss");    
		return sdf.format(new java.util.Date());
	}

	@SuppressLint("DefaultLocale")
	public static String byteToHex(byte[] b) {  
		StringBuffer hexString = new StringBuffer();  
		for (int i = 0; i < b.length; i++) {  
			String hex = Integer.toHexString(b[i] & 0xFF);  
			if (hex.length() == 1) {  
				hex = '0' + hex;  
			}  
			hexString.append(hex.toUpperCase());  
		}  
		return hexString.toString();  
	}

	@SuppressLint("UseValueOf")
	public static final byte[] hexToByte(String hex)
			throws IllegalArgumentException {
		if (hex.length() % 2 != 0) {
			throw new IllegalArgumentException();
		}
		char[] arr = hex.toCharArray();
		byte[] b = new byte[hex.length() / 2];
		for (int i = 0, j = 0, l = hex.length(); i < l; i++, j++) {
			String swap = "" + arr[i++] + arr[i];
			int byteint = Integer.parseInt(swap, 16) & 0xFF;
			b[j] = new Integer(byteint).byteValue();
		}
		return b;
	}

	public static String byteToAscii(byte[] bytearray) {
		String result = "";
		char temp;

		int length = bytearray.length;
		for (int i = 0; i < length; i++) {
			temp = (char) bytearray[i];
			result += temp;
		}
		return result;
	}

	public static byte[]  asciiToByte(String ascIIString)
	{
		int asc = Integer.parseInt(ascIIString);
		byte[] b = new byte[ascIIString.length()];
		for (int i = 0; i < b.length; i++) {
			b[i] = (byte) (asc >> i*8);
		}
		return b;
	}

}

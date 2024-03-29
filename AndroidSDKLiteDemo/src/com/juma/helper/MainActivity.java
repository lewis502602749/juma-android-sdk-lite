package com.juma.helper;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.UUID;

import juma.sdk.lite.JumaDevice;
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
import android.view.View.OnLongClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.juma.helper.CustomDialog.MessageCallback;
import com.juma.helper.CustomDialog.ScanCallback;

public class MainActivity extends Activity implements OnClickListener, OnLongClickListener{

	public static final String ACTION_START_SCAN = "com.juma.helper.ACTION_START_SCAN";
	public static final String ACTION_STOP_SCAN = "com.juma.helper.ACTION_STOP_SCAN";
	public static final String ACTION_DEVICE_DISCOVERED = "com.juma.helper.ACTION_DEVICE_DISCOVERED";
	public static final String ACTION_CONNECT = "com.juma.helper.ACTION_CONNECT";
	public static final String ACTION_CONNECTED = "com.juma.helper.ACTION_CONNECTED";
	public static final String ACTION_DISCONNECT = "com.juma.helper.ACTION_DISCONNECT";
	public static final String ACTION_DISCONNECTED = "com.juma.helper.ACTION_DISCONNECTED";
	public static final String ACTION_SEND_MESSAGE = "com.juma.helper.ACTION_SEND_MESSAGE";
	public static final String ACTION_RECEIVER_MESSAGE = "com.juma.helper.ACTION_RECEIVER_MESSAGE";
	public static final String ACTION_ERROR = "com.juma.helper.ACTION_ERROR";

	public static final String NAME_STR = "name";
	public static final String UUID_STR = "uuid";
	public static final String RSSI_STR = "rssi";
	public static final String MESSAGE_STR = "message";
	public static final String ERROR_STR = "error";
	public static final String STATUS_STR = "status";
	public static final String ERROR_CODE_STR = "errorCode";
	public static final String TYPE_STR = "type";

	private ArrayAdapter<String> listAdapter = null;
	private JumaDevice device = null;
	private TextView tvName = null,tvUuid = null;
	private View vStateLine = null;
	private ListView lvMessage = null;
	private GridView gvKeyboard = null;
	private CustomGridViewAdapter gvAdapter = null;
	private String deviceName = null;
	private UUID deviceUuid = null;
	private HashMap<Integer, byte[]> messages = null;
	private int[] keyIds = {R.string.key_1,R.string.key_2, R.string.key_3, R.string.key_4, R.string.key_5, R.string.key_6, 
			R.string.key_7, R.string.key_8, R.string.key_9, R.string.scan, R.string.connect, R.string.send};


	@SuppressLint("UseSparseArrays")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		messages = new HashMap<Integer, byte[]>();

		initView();

		initDevice();

	}

	private void initView(){

		tvName = (TextView) findViewById(R.id.tvName);
		tvUuid = (TextView) findViewById(R.id.tvUuid);

		vStateLine = findViewById(R.id.vStateLine);

		lvMessage = (ListView) findViewById(R.id.lvMessage);
		lvMessage.setDivider(null);

		gvKeyboard = (GridView) findViewById(R.id.gvKeyboard);

		listAdapter = new ArrayAdapter<String>(this, R.layout.message_list_item);
		lvMessage.setAdapter(listAdapter);

		gvAdapter = new CustomGridViewAdapter(getApplicationContext(), keyIds, this, this);
		gvKeyboard.setAdapter(gvAdapter);

	}

	private void initDevice(){

		device = new JumaDevice() {

			@Override
			public void onUpdateFirmware(int status) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStopScan() {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.ACTION_STOP_SCAN);
				sendBroadcast(MainActivity.this, intent);
			}

			@Override
			public void onSend(int status) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onMessage(byte type, byte[] message) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.ACTION_RECEIVER_MESSAGE);
				intent.putExtra(MainActivity.TYPE_STR, type);
				intent.putExtra(MainActivity.MESSAGE_STR, message);
				sendBroadcast(MainActivity.this, intent);
			}

			@Override
			public void onError(Exception e, int errorCode) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.ACTION_ERROR);
				intent.putExtra(MainActivity.ERROR_STR, e.getMessage());
				intent.putExtra(MainActivity.ERROR_CODE_STR, errorCode);
				sendBroadcast(MainActivity.this, intent);
			}

			@Override
			public void onDiscover(String name, UUID uuid, int rssi) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.ACTION_DEVICE_DISCOVERED);
				intent.putExtra(MainActivity.NAME_STR, name);
				intent.putExtra(MainActivity.UUID_STR, uuid.toString());
				intent.putExtra(MainActivity.RSSI_STR, rssi);
				sendBroadcast(MainActivity.this, intent);
			}

			@Override
			public void onDisconnect(UUID uuid, int status) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.ACTION_DISCONNECTED);
				intent.putExtra(MainActivity.STATUS_STR, status);
				intent.putExtra(MainActivity.NAME_STR, deviceName);
				intent.putExtra(MainActivity.UUID_STR, uuid.toString());
				sendBroadcast(MainActivity.this, intent);
			}

			@Override
			public void onConnect(UUID uuid, int status) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.ACTION_CONNECTED);
				intent.putExtra(MainActivity.STATUS_STR, status);
				intent.putExtra(MainActivity.NAME_STR, deviceName);
				intent.putExtra(MainActivity.UUID_STR, uuid.toString());
				sendBroadcast(MainActivity.this, intent);
			}
		};

		device.init(getApplicationContext());

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.string.key_1:
			sendMessage((byte) 0x00, messages.get(v.getId()));
			break;
		case R.string.key_2:
			sendMessage((byte) 0x01, messages.get(v.getId()));
			break;
		case R.string.key_3:
			sendMessage((byte) 0x02, messages.get(v.getId()));
			break;
		case R.string.key_4:
			sendMessage((byte) 0x03, messages.get(v.getId()));
			break;
		case R.string.key_5:
			sendMessage((byte) 0x04, messages.get(v.getId()));
			break;
		case R.string.key_6:
			sendMessage((byte) 0x05, messages.get(v.getId()));
			break;
		case R.string.key_7:
			sendMessage((byte) 0x06, messages.get(v.getId()));
			break;
		case R.string.key_8:
			sendMessage((byte) 0x07, messages.get(v.getId()));
			break;
		case R.string.key_9:
			sendMessage((byte) 0x08, messages.get(v.getId()));
			break;
		case R.string.scan:

			final CustomDialog scanDialog = new CustomDialog(MainActivity.this, CustomDialog.DIALOG_TYPE_SCAN);
			scanDialog.setScanCallback(new ScanCallback() {

				@Override
				public void onName(String name) {

					device.scan(name);

				}

				@Override
				public void onDevice(final UUID uuid, final String name) {

					device.stopScan();

					deviceUuid = uuid;
					deviceName = name;

					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							tvName.setText(name);
							tvUuid.setText(uuid.toString());	
						}
					});

				}
			});

			scanDialog.setNegativeButton(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					scanDialog.dismiss();

					device.stopScan();

				}
			});

			scanDialog.show();

			break;
		case R.string.connect:

			if(gvAdapter.getItem(R.string.connect).getText().equals(MainActivity.this.getResources().getString(R.string.disconnect))){
				device.disconnect(deviceUuid);
			}else{
				if(deviceUuid != null){
					device.connect(deviceUuid);

					Intent intent = new Intent(ACTION_CONNECT);
					intent.putExtra(MainActivity.NAME_STR, deviceName);
					intent.putExtra(MainActivity.UUID_STR, deviceUuid.toString());
					sendBroadcast(MainActivity.this, intent);

				}
			}

			break;
		case R.string.send:

			CustomDialog sendDialog = new CustomDialog(MainActivity.this, CustomDialog.DIALOG_TYPE_SEND_MESSAGE);
			sendDialog.setMessageCallback(new MessageCallback() {

				@Override
				public void onMessage(byte[] message, int id) {

					if(message.length > 0)
						sendMessage((byte) 0x09, message);

				}
			});

			sendDialog.show();

			break;
		}
	}

	@Override
	public boolean onLongClick(View v) {
		switch (v.getId()) {
		case R.string.key_1:
			editMessage(v.getId());
			break;
		case R.string.key_2:
			editMessage(v.getId());
			break;
		case R.string.key_3:
			editMessage(v.getId());
			break;
		case R.string.key_4:
			editMessage(v.getId());
			break;
		case R.string.key_5:
			editMessage(v.getId());
			break;
		case R.string.key_6:
			editMessage(v.getId());
			break;
		case R.string.key_7:
			editMessage(v.getId());
			break;
		case R.string.key_8:
			editMessage(v.getId());
			break;
		case R.string.key_9:
			editMessage(v.getId());
			break;
		}
		return true;
	}

	private void editMessage(int id){
		CustomDialog editDialog = new CustomDialog(MainActivity.this, CustomDialog.DIALOG_TYPE_EDIT_MESSAGE);

		editDialog.setId(id);

		editDialog.setMessageCallback(new MessageCallback() {

			@Override
			public void onMessage(final byte[] message, final int id) {

				messages.put(id, message);

			}
		});

		editDialog.show();

	}

	private void sendMessage(byte type, byte[] message){
		if(message != null){

			Intent intent = new Intent(MainActivity.ACTION_SEND_MESSAGE);
			intent.putExtra(TYPE_STR, type);
			intent.putExtra(MainActivity.MESSAGE_STR, message);
			sendBroadcast(MainActivity.this, intent);

			device.send(type, message);
		}
	}

	public static void hideSoftInput(Activity activity){
		((InputMethodManager)activity.getApplication().getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(activity.getCurrentFocus()
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	boolean isFrist = true;

	@Override
	protected void onStart() {

		super.onStart();

		LocalBroadcastManager.getInstance(this).registerReceiver(receiver, getIntentFilter());

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
	}

	private final BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(final Context context, final Intent intent) {

			String action = intent.getAction();

			if(action.equals(MainActivity.ACTION_CONNECT)){
				runOnUiThread(new Runnable() {

					@Override
					public void run() {

						String currentDate = getCurrentData(context);

						String uuid = intent.getStringExtra(MainActivity.UUID_STR);
						String name = intent.getStringExtra(MainActivity.NAME_STR);

						StringBuffer sb = new StringBuffer();
						sb.append("[");
						sb.append(currentDate);
						sb.append("] : Connect device : ");
						sb.append("\nname : ");
						sb.append(name);
						sb.append("\nuuid : ");
						sb.append(uuid);

						listAdapter.add(sb.toString());
						lvMessage.smoothScrollByOffset(listAdapter.getCount() - 1);

					}
				});
			}
			if(action.equals(MainActivity.ACTION_CONNECTED)){
				runOnUiThread(new Runnable() {

					@Override
					public void run() {

						String currentDate = getCurrentData(context);

						String uuid = intent.getStringExtra(MainActivity.UUID_STR);
						String name = intent.getStringExtra(MainActivity.NAME_STR);

						StringBuffer sb = new StringBuffer();
						sb.append("[");
						sb.append(currentDate);

						int status = intent.getIntExtra(MainActivity.STATUS_STR, -1);

						switch (status) {
						case JumaDevice.STATE_BUSY:
							sb.append("] : Device connect is busy: ");
							break;
						case JumaDevice.STATE_ERROR:
							sb.append("] : Device connect is failed : ");
							break;
						case JumaDevice.STATE_SUCCESS:
							vStateLine.setBackgroundColor(context.getResources().getColor(R.color.green));

							gvAdapter.getItem(R.string.scan).setEnabled(false);
							keyIds[10] = R.string.disconnect;
							gvAdapter.notifyDataSetChanged();

							sb.append("] : Device connected : ");

							break;
						}

						sb.append("\nname : ");
						sb.append(name);
						sb.append("\nuuid : ");
						sb.append(uuid);

						listAdapter.add(sb.toString());
						lvMessage.smoothScrollByOffset(listAdapter.getCount() - 1);

					}
				});
			}
			if(action.equals(MainActivity.ACTION_DISCONNECT)){
				runOnUiThread(new Runnable() {

					@Override
					public void run() {

						String currentDate = getCurrentData(context);

						String uuid = intent.getStringExtra(MainActivity.UUID_STR);
						String name = intent.getStringExtra(MainActivity.NAME_STR);

						StringBuffer sb = new StringBuffer();
						sb.append("[");
						sb.append(currentDate);
						sb.append("] : Disconnect device : ");
						sb.append("\nname : ");
						sb.append(name);
						sb.append("\nuuid : ");
						sb.append(uuid);

						listAdapter.add(sb.toString());
						lvMessage.smoothScrollByOffset(listAdapter.getCount() - 1);

					}
				});
			}

			if(action.equals(MainActivity.ACTION_DISCONNECTED)){
				runOnUiThread(new Runnable() {

					@Override
					public void run() {

						String currentDate = getCurrentData(context);

						String uuid = intent.getStringExtra(MainActivity.UUID_STR);
						String name = intent.getStringExtra(MainActivity.NAME_STR);

						StringBuffer sb = new StringBuffer();
						sb.append("[");
						sb.append(currentDate);

						int status = intent.getIntExtra(MainActivity.STATUS_STR, -1);

						switch (status) {
						case JumaDevice.STATE_BUSY:
							sb.append("] : Device disconnect is busy: ");
							break;
						case JumaDevice.STATE_ERROR:
							sb.append("] : Device disconnect is failed : ");
							break;
						case JumaDevice.STATE_SUCCESS:
							vStateLine.setBackgroundColor(context.getResources().getColor(R.color.red));

							gvAdapter.getItem(R.string.scan).setEnabled(true);
							keyIds[10] = R.string.connect;
							gvAdapter.notifyDataSetChanged();

							sb.append("] : Device disconnected : ");

							break;
						}

						sb.append("\nname : ");
						sb.append(name);
						sb.append("\nuuid : ");
						sb.append(uuid);

						listAdapter.add(sb.toString());
						lvMessage.smoothScrollByOffset(listAdapter.getCount() - 1);


					}
				});
			}

			if(action.equals(MainActivity.ACTION_SEND_MESSAGE)){
				runOnUiThread(new Runnable() {

					@Override
					public void run() {

						String currentDate = getCurrentData(context);

						byte type = intent.getByteExtra(MainActivity.TYPE_STR, (byte) 0x00);

						byte[] message = intent.getByteArrayExtra(MainActivity.MESSAGE_STR);

						StringBuffer sb = new StringBuffer();
						sb.append("[");
						sb.append(currentDate);
						sb.append("] : Send message : ");	
						sb.append("\nType : "+byteToHex(new byte[]{type}));
						sb.append("\nMessage : "+byteToHex(message));

						listAdapter.add(sb.toString());
						lvMessage.smoothScrollByOffset(listAdapter.getCount() - 1);

					}
				});
			}

			if(action.equals(MainActivity.ACTION_RECEIVER_MESSAGE)){
				runOnUiThread(new Runnable() {

					@Override
					public void run() {

						String currentDate = getCurrentData(context);

						byte type = intent.getByteExtra(MainActivity.TYPE_STR, (byte) 0x00);

						byte[] message = intent.getByteArrayExtra(MainActivity.MESSAGE_STR);

						StringBuffer sb = new StringBuffer();
						sb.append("[");
						sb.append(currentDate);
						sb.append("] : Receiver message : ");	
						sb.append("\nType : "+byteToHex(new byte[]{type}));
						sb.append("\nMessage : "+byteToHex(message));

						listAdapter.add(sb.toString());
						lvMessage.smoothScrollByOffset(listAdapter.getCount() - 1);

					}
				});
			}
			if(action.equals(MainActivity.ACTION_ERROR)){
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						
						String currentDate = getCurrentData(context);
						
						String errorMessage = intent.getStringExtra(MainActivity.ERROR_STR);
						
						StringBuffer sb = new StringBuffer();
						sb.append("[");
						sb.append(currentDate);
						sb.append("] : Error : ");

						int errorCode = intent.getIntExtra(MainActivity.ERROR_CODE_STR, -1);

						if(errorCode == JumaDevice.ERROR_DISCONNECT){
							vStateLine.setBackgroundColor(context.getResources().getColor(R.color.red));

							gvAdapter.getItem(R.string.scan).setEnabled(true);
							keyIds[10] = R.string.connect;
							gvAdapter.notifyDataSetChanged();
						}
						
						sb.append(errorMessage);

						listAdapter.add(sb.toString());
						lvMessage.smoothScrollByOffset(listAdapter.getCount() - 1);
					}
				});
			}
		}
	};

	private IntentFilter getIntentFilter(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(MainActivity.ACTION_START_SCAN);
		filter.addAction(MainActivity.ACTION_STOP_SCAN);
		filter.addAction(MainActivity.ACTION_DEVICE_DISCOVERED);
		filter.addAction(MainActivity.ACTION_CONNECT);
		filter.addAction(MainActivity.ACTION_CONNECTED);
		filter.addAction(MainActivity.ACTION_DISCONNECT);
		filter.addAction(MainActivity.ACTION_DISCONNECTED);
		filter.addAction(MainActivity.ACTION_SEND_MESSAGE);
		filter.addAction(MainActivity.ACTION_RECEIVER_MESSAGE);
		filter.addAction(MainActivity.ACTION_ERROR);
		return filter;
	}

	private void sendBroadcast(Context context, Intent intent){
		LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
	}

	@SuppressLint("SimpleDateFormat")
	private static String getCurrentData(Context context){
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");    
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
	public static final byte[] hexToByte(String hex)throws IllegalArgumentException {
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
	
    public static String bytetoAsciiString(byte[] bytearray) {
        String result = "";
        char temp;

        int length = bytearray.length;
        for (int i = 0; i < length; i++) {
            temp = (char) bytearray[i];
            result += temp;
        }
        return result;
    }

}

package com.juma.demo;

import java.util.HashMap;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

public class CustomGridViewAdapter extends BaseAdapter{

	private Context context = null;
	
	private OnClickListener clickListener = null;
	
	private  OnLongClickListener longClickListener = null;

	private int[] keyId = {R.string.key_1,R.string.key_2, R.string.key_3, R.string.key_4, R.string.key_5, R.string.key_6, 
			R.string.key_7, R.string.key_8, R.string.key_9, R.string.scan, R.string.connect, R.string.send};

	private HashMap<Integer, Button> keys = null;
	
	public CustomGridViewAdapter(Context context, OnClickListener clickListener, OnLongClickListener longClickListener) {

		this.context = context;
		
		this.clickListener = clickListener;
		
		this.longClickListener = longClickListener;
		
		keys = new HashMap<Integer, Button>();
		
	}

	@Override
	public int getCount() {
		return keyId.length;
	}

	@Override
	public Button getItem(int id) {
		return keys.get(id);
	}

	@Override
	public long getItemId(int id) {
		return id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Button btnKey = null;

		if(convertView == null){
			btnKey = new Button(context);
			btnKey.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));  
			btnKey.setTextSize(18);
			btnKey.setOnClickListener(clickListener);
			btnKey.setOnLongClickListener(longClickListener);
			btnKey.setId(keyId[position]);
			btnKey.setBackgroundResource(R.drawable.button_background);
			keys.put(keyId[position], btnKey);
		}else {
			btnKey = (Button) convertView;
		}

		btnKey.setText(keyId[position]);

		return btnKey;
	}


}

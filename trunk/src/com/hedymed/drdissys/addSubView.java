package com.hedymed.drdissys;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hedymed.uart.uartUtils;

public class addSubView extends LinearLayout {
	private static final int MAS_DIS_LENGTH = 6;
	private TextView mValueText, mUnit;
	private Button mAdd, mSub;
	private double mValue;
	private String mFunction;
	private Map<String, String> mUnitMap;

	public addSubView(Context context, AttributeSet attrs) {
		this(context, attrs, null);
		mUnitMap = new HashMap<String, String>();
		mUnitMap.put("KV", "kV");
		mUnitMap.put("MA", "mA");
		mUnitMap.put("MAS", "mAs");
		mUnitMap.put("MS", "ms");
		setValue(0);
	}

	public addSubView(Context context, AttributeSet attrs, String text) {
		super(context, attrs);
		View rootView = LayoutInflater.from(context).inflate(
				R.layout.add_sub_layout, this, true);
		
		mValueText = (TextView) rootView.findViewById(R.id.value_dis);
		mUnit = (TextView) rootView.findViewById(R.id.value_unit);
		mAdd = (Button) rootView.findViewById(R.id.add);
		mSub = (Button) rootView.findViewById(R.id.sub);

		mAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("addSubView", mFunction + "+ button is clicked");
				uartUtils.sendToSendThread(mFunction + '+');
			}
		});
		
		mAdd.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				Log.i("addSubView", mFunction + "+ button is longclicked");
				uartUtils.sendToSendThread(mFunction + "++");
				return true;
			}
		});
		
		mSub.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("addSubView", mFunction + "- button is clicked");
				uartUtils.sendToSendThread(mFunction + '-');
			}
		});
		
		mSub.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				Log.i("addSubView", mFunction + "- button is longclicked");
				uartUtils.sendToSendThread(mFunction + "--");
				return true;
			}
		});
		
	}

	public void setValue(String text) {
		mValue = Double.parseDouble(text);
		mValueText.setText(text);
	}
	
	public void setValue(int value) {
		this.mValue = value;
		mValueText.setText(String.valueOf(value));
	}
	
	public void setValue(double value) {
		this.mValue = value;
		String str = String.valueOf(value);
		if(str.length() > MAS_DIS_LENGTH)
			str = str.substring(0, MAS_DIS_LENGTH);
		
		mValueText.setText(str);
	}

	public String getValue() {
		return String.valueOf(mValue);
	}

	public void setFunctoin(String function) {
		this.mFunction = function;
		setUnit(mUnitMap.get(function));
	}

	private void setUnit(String text) {
		mUnit.setText(text);
	}
	
	public void setEnabled(boolean enable){
		if(enable){
			mAdd.setVisibility(VISIBLE);
			mSub.setVisibility(VISIBLE);
		} else {
			mAdd.setVisibility(INVISIBLE);
			mSub.setVisibility(INVISIBLE);
		}
	}

}

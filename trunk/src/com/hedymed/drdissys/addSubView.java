package com.hedymed.drdissys;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hedymed.uart.uartUtils;

public class addSubView extends LinearLayout {
	private static final int MAS_DIS_LENGTH = 6;
	private TextView valueText, unit;
	private Button add, sub;
	private double value;
	private String function;
	private Map<String, String> unitMap;

	public addSubView(Context context, AttributeSet attrs) {
		this(context, attrs, null);
		unitMap = new HashMap<String, String>();
		unitMap.put("KV", "kV");
		unitMap.put("MA", "mA");
		unitMap.put("MAS", "mAs");
		unitMap.put("MS", "ms");
		setValue(0);
	}

	public addSubView(Context context, AttributeSet attrs, String text) {
		super(context, attrs);
		View rootView = LayoutInflater.from(context).inflate(
				R.layout.add_sub_layout, this, true);
		
		valueText = (TextView) rootView.findViewById(R.id.value_dis);
		unit = (TextView) rootView.findViewById(R.id.value_unit);
		add = (Button) rootView.findViewById(R.id.add);
		sub = (Button) rootView.findViewById(R.id.sub);

		add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				uartUtils.sendToSendThread(function + '+');
			}
		});
		
		add.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				uartUtils.sendToSendThread(function + "++");
				return true;
			}
		});
		
		sub.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				uartUtils.sendToSendThread(function + '-');
			}
		});
		
		sub.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				uartUtils.sendToSendThread(function + "--");
				return true;
			}
		});
	}

	public void setValue(String text) {
		value = Double.parseDouble(text);
		valueText.setText(text);
	}
	
	public void setValue(int value) {
		this.value = value;
		valueText.setText(String.valueOf(value));
	}
	
	public void setValue(double value) {
		this.value = value;
		String str = String.valueOf(value);
		if(str.length() > MAS_DIS_LENGTH)
			str = str.substring(0, MAS_DIS_LENGTH);
		
		valueText.setText(str);
	}

	public String getValue() {
		return String.valueOf(value);
	}

	public void setFunctoin(String function) {
		this.function = function;
		setUnit(unitMap.get(function));
	}

	private void setUnit(String text) {
		unit.setText(text);
	}
	
	public void setEnabled(boolean enable){
		if(enable){
			add.setVisibility(VISIBLE);
			sub.setVisibility(VISIBLE);
		} else {
			add.setVisibility(INVISIBLE);
			sub.setVisibility(INVISIBLE);
		}
	}

}

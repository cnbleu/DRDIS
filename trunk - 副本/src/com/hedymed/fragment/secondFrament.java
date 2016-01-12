package com.hedymed.fragment;

import static com.hedymed.db.AppDataStruct.ALIGN_BOT;
import static com.hedymed.db.AppDataStruct.ALIGN_MID;
import static com.hedymed.db.AppDataStruct.ALIGN_TOP;
import static com.hedymed.db.AppDataStruct.appData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.hedymed.db.AppDataStruct;
import com.hedymed.drdissys.MainActivity;
import com.hedymed.drdissys.R;
import com.hedymed.uart.uartUtils;

public class secondFrament extends android.support.v4.app.Fragment {
	private RadioButton mTopAlignBtn, mMidAlignBtn, mBotAlignbtn;
	private ToggleButton mFieldTraceSelector;
	private Spinner mLightFieldSelecter;
	private Spinner mDoseClassSelecter;//Dose 选择
	private Spinner mCompenClassSelecter;//曝光补偿
	private boolean mLightFieldSelecterInit, mDoseClassSelecterInit, mCompenClassSelecterInit;
	private ToggleButton mAecLeftField;
	private ToggleButton mAecRightField;
	private ToggleButton mAecTopField;
	private EditText mCustomSOD;
	private View mRootView;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setRetainInstance(true);
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(mRootView == null) {
			mRootView = inflater.inflate(R.layout.second_frament, container, false);
			mTopAlignBtn = (RadioButton)mRootView.findViewById(R.id.radio_top_align);
			mMidAlignBtn = (RadioButton)mRootView.findViewById(R.id.radio_mid_align);
			mBotAlignbtn = (RadioButton)mRootView.findViewById(R.id.radio_bottom_align);
			
			mFieldTraceSelector = (ToggleButton)mRootView.findViewById(R.id.field_trace_button);
			mLightFieldSelecter = (Spinner)mRootView.findViewById(R.id.light_field_selecter);
			mDoseClassSelecter = (Spinner)mRootView.findViewById(R.id.dose_selecter);
			mCompenClassSelecter = (Spinner)mRootView.findViewById(R.id.level_selecter);
			mAecLeftField = (ToggleButton)mRootView.findViewById(R.id.aec_left_top);
			mAecRightField = (ToggleButton)mRootView.findViewById(R.id.aec_right_top);
			mAecTopField = (ToggleButton)mRootView.findViewById(R.id.aec_bottom);
			mCustomSOD = (EditText)mRootView.findViewById(R.id.sod_editor);
			
			mLightFieldSelecterInit = false;
			mDoseClassSelecterInit = false;
			mCompenClassSelecterInit = false;
			
			setLightField();
			setDoseComplement();
			setDoseLevel();
			registerEventMonitor();
		}
		
		return mRootView;
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	private void registerEventMonitor(){
		mLightFieldSelecter.setOnItemSelectedListener(new customSpinnerListener());
		mDoseClassSelecter.setOnItemSelectedListener(new customSpinnerListener());
		mCompenClassSelecter.setOnItemSelectedListener(new customSpinnerListener());
		
		mTopAlignBtn.setOnCheckedChangeListener(new customRaidoButtonListener());
		mMidAlignBtn.setOnCheckedChangeListener(new customRaidoButtonListener());
		mBotAlignbtn.setOnCheckedChangeListener(new customRaidoButtonListener());
		mFieldTraceSelector.setOnCheckedChangeListener(new customToggleButtonListener()); 
		mAecLeftField.setOnCheckedChangeListener(new customToggleButtonListener());
		mAecRightField.setOnCheckedChangeListener(new customToggleButtonListener());
		mAecTopField.setOnCheckedChangeListener(new customToggleButtonListener());
		mCustomSOD.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				try{
					int argument = 0;
					int value = Integer.parseInt(v.getText().toString());
					if((value <= 180) && (value >= 0 )) {
						argument = value;
						Log.i("secondFragment", String.format("SOD Set to %d", argument));
						if(appData.get("SOD") != argument) {
							appData.put("SOD", argument);
							uartUtils.sendToSendThread("SOD" + argument);
						}
					} else
						Toast.makeText(getActivity(), "value error", Toast.LENGTH_SHORT).show();
				}
				catch (NumberFormatException e){
					return false;
				}
				
				return false;
			}
		});
	}
	
	class customRaidoButtonListener implements CompoundButton.OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if(buttonView instanceof RadioButton) {
				RadioButton rb = (RadioButton)buttonView;
				if(isChecked) {
					if(rb == mTopAlignBtn) {
						Log.i("secondFragment", "Select Top Align");
						if(appData.get("AS") != ALIGN_TOP) {
							appData.put("AS", (int) ALIGN_TOP);
							uartUtils.sendToSendThread("AS" + ALIGN_TOP);
						}
					} else if(mMidAlignBtn == rb) {
						Log.i("secondFragment", "Select Middle Align");
						if(appData.get("AS") != ALIGN_MID) {
							appData.put("AS", (int) ALIGN_MID);
							uartUtils.sendToSendThread("AS" + ALIGN_MID);
						}
					} else if(mBotAlignbtn == rb) {
						Log.i("secondFragment", "Select Bottom Align");
						if(appData.get("AS") != ALIGN_BOT) {
							appData.put("AS", (int) ALIGN_BOT);
							uartUtils.sendToSendThread("AS" + ALIGN_BOT);
						}
					}
				}
			}
		}
	}
	
	 class customToggleButtonListener implements CompoundButton.OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			 if(buttonView instanceof ToggleButton) {
				ToggleButton button = (ToggleButton)buttonView;
				int argument = 0;
				
				if(mFieldTraceSelector == button) {
					if(isChecked) {
						Log.i("secondFragment", "Field Trace ON");
						argument = AppDataStruct.FIELD_TRACE_ON;
					} else {
						Log.i("secondFragment", "Field Trace OFF");
						argument = AppDataStruct.FIELD_TRACE_OFF;
					}
					
					if(appData.get("TRA") != argument) {
						appData.put("TRA", argument);
						uartUtils.sendToSendThread("TRA" + argument);
					}
				} else {
					argument = appData.get("AEC");
					
					if(mAecLeftField == button) {
						if(isChecked)
							argument |= AppDataStruct.AEC_LEFT_FIELD_MASK;
						else
							argument &= ~AppDataStruct.AEC_LEFT_FIELD_MASK;
					} else if(mAecRightField == button) {
						if(isChecked)
							argument |= AppDataStruct.AEC_RIGHT_FIELD_MASK;
						else
							argument &= ~AppDataStruct.AEC_RIGHT_FIELD_MASK;
					} else if(mAecTopField == button) {
						if(isChecked)
							argument |= AppDataStruct.AEC_TOP_FIELD_MASK;
						else
							argument &= ~AppDataStruct.AEC_TOP_FIELD_MASK;
					}
					Log.i("secondFragment", getAECLogMessage(argument)); 
					if(appData.get("AEC") != argument) {
						appData.put("AEC", argument);
						uartUtils.sendToSendThread("AEC" + argument);
					}
				}
			}
		}	
	}
	 
	 private String getAECLogMessage(int argument) {
		 String left = null, right = null, top = null;
		 
		 if((argument & AppDataStruct.AEC_LEFT_FIELD_MASK) != 0)
			 left = "AEC Left Field is selected, ";
		 else
			 left = "AEC Left Field NOT selected, ";
		 
		 if((argument & AppDataStruct.AEC_RIGHT_FIELD_MASK) != 0)
			 right = "AEC Right Field is selected, ";
		 else
			 right = "AEC Right Field NOT selected, ";
		 
		 if((argument & AppDataStruct.AEC_TOP_FIELD_MASK) != 0)
			 top = "AEC Top Field is selected.";
		 else
			 top = "AEC Top Field NOT selected.";
		 
		 return left + right + top;
	 }
	 
	
	class customSpinnerListener implements AdapterView.OnItemSelectedListener {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			Spinner spinner = (Spinner)parent;
			int curPosition = spinner.getSelectedItemPosition();
			
			if(spinner == mLightFieldSelecter) {
				if(!mLightFieldSelecterInit)
					mLightFieldSelecterInit = true;
				else {
					Log.i("secondFragment", String.format("mLightFieldSelecter selected position is %d", position));
					if(appData.get("FIE") != curPosition) {
						appData.put("FIE", curPosition);
						uartUtils.sendToSendThread("FIE" + position);
					}
				}
			} else if(spinner == mDoseClassSelecter) {
				if(!mDoseClassSelecterInit)
					mDoseClassSelecterInit = true;
				else {
					Log.i("secondFragment", String.format("mDoseClassSelecter selected position is %d", position));
					if(appData.get("DC") != curPosition) {
						appData.put("DC", curPosition);
						uartUtils.sendToSendThread("DC" + position);
					}
				}
			} else if (spinner == mCompenClassSelecter) {
				if(!mCompenClassSelecterInit)
					mCompenClassSelecterInit = true;
				else {
					Log.i("secondFragment", String.format("mCompenClassSelecter selected position is %d", position));
					if(appData.get("EC") != curPosition) {
						appData.put("EC", curPosition);
						uartUtils.sendToSendThread("EC" + position);
					}
				}
			} 
		}
		
		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
		}
	}
	
	//for initialize when parse configure xml.
	public void refreshFragment() {
		try {
			int tempValue;
			//上中下对齐
			tempValue = appData.get("AS");
			if(AppDataStruct.ALIGN_TOP == tempValue)
				mTopAlignBtn.setChecked(true);
			else if(AppDataStruct.ALIGN_MID == tempValue)
				mMidAlignBtn.setChecked(true);
			else if(AppDataStruct.ALIGN_BOT == tempValue)
				mBotAlignbtn.setChecked(true);
			
			tempValue = appData.get("TRA");
			if(AppDataStruct.FIELD_TRACE_OFF == tempValue)
				mFieldTraceSelector.setChecked(false);
			else if(AppDataStruct.FIELD_TRACE_ON == tempValue)
				mFieldTraceSelector.setChecked(true);
			
			MainActivity.setSpinnerSelect(mLightFieldSelecter, appData.get("FIE"));
			MainActivity.setSpinnerSelect(mDoseClassSelecter, appData.get("DC"));
			MainActivity.setSpinnerSelect(mCompenClassSelecter, appData.get("EC"));
			
			mCustomSOD.setText(String.valueOf(appData.get("SOD")));
			
			tempValue = appData.get("AEC");
			//AEC 选择
			if(0 != (tempValue & AppDataStruct.AEC_LEFT_FIELD_MASK))
				mAecLeftField.setChecked(true);
			else
				mAecLeftField.setChecked(false);
			
			if(0 != (tempValue & AppDataStruct.AEC_RIGHT_FIELD_MASK))
				mAecRightField.setChecked(true);
			else
				mAecRightField.setChecked(false);
			
			if(0 != (tempValue & AppDataStruct.AEC_TOP_FIELD_MASK))
				mAecTopField.setChecked(true);
			else
				mAecTopField.setChecked(false);
		}
		catch(NullPointerException e) {
			Log.i("secondFragment:refreshFragment", "first refresh secondFragment will cause nullException");
		}
	}
	
	public void MVC_control_handler(String[] cmdAndArg) {
//		if(isVisible())
			MVC_view_handler(cmdAndArg);
	}
	
	private void MVC_view_handler(String[] cmdAndArg) {
		try{
			int mapValue;
			switch(cmdAndArg[0]) {
			case "AS":
				mapValue = appData.get("AS");
				if(AppDataStruct.ALIGN_TOP == mapValue)
					mTopAlignBtn.setChecked(true);
				else if(AppDataStruct.ALIGN_MID == mapValue)
					mMidAlignBtn.setChecked(true);
				else if(AppDataStruct.ALIGN_BOT == mapValue)
					mBotAlignbtn.setChecked(true);
				break;
				
			case "TRA":
				mapValue = appData.get("TRA");
				if(AppDataStruct.FIELD_TRACE_OFF == mapValue)
					mFieldTraceSelector.setChecked(false);
				else if(AppDataStruct.FIELD_TRACE_ON == mapValue)
					mFieldTraceSelector.setChecked(true);
				break;
			
			case "FIE":
				MainActivity.setSpinnerSelect(mLightFieldSelecter, appData.get("FIE"));
				break;
			
			case "DC":
				MainActivity.setSpinnerSelect(mDoseClassSelecter, appData.get("DC"));
				break;
				
			case "EC":
				MainActivity.setSpinnerSelect(mCompenClassSelecter, appData.get("EC"));
				break;
				
			case "AEC":
				mapValue = appData.get("AEC");
				if(0 != (mapValue & AppDataStruct.AEC_LEFT_FIELD_MASK))
					mAecLeftField.setChecked(true);
				else
					mAecLeftField.setChecked(false);
				
				if(0 != (mapValue & AppDataStruct.AEC_RIGHT_FIELD_MASK))
					mAecRightField.setChecked(true);
				else
					mAecRightField.setChecked(false);
				
				if(0 != (mapValue & AppDataStruct.AEC_TOP_FIELD_MASK))
					mAecTopField.setChecked(true);
				else
					mAecTopField.setChecked(false);
				break;
				
			case "SOD":
				mCustomSOD.setText(String.valueOf(appData.get("SOD")));
				break;
			
			default:
				break;
			}
		} 
		catch (NullPointerException e) {
			Log.i("secondFrament:handleCmd", "This Fragment NOT Attach Now");
		}
	}
	
	private void setLightField() {
		int [] imageID = {R.drawable.light_field_8x10, R.drawable.light_field_10x8, R.drawable.light_field_10x12,
							R.drawable.light_field_12x10, R.drawable.light_field_14x17, R.drawable.light_field_17x14,
							R.drawable.light_field_14x14, R.drawable.light_field_17x17};
		
		String[] str = {"8 X 10", "10 X 8", "10 X 12", "12 X 10", "14 X 17", "17 X 14", "14 X 14", "17 X 17"};
		mLightFieldSelecter.setAdapter(getSimpleAdapter(mLightFieldSelecter, str, imageID));
	}
	
	private void setDoseComplement() {
		String[] str = {"1600", "1200", "800", "400"};
		int []imageID = {R.drawable.dose_pic, R.drawable.dose_pic, R.drawable.dose_pic, R.drawable.dose_pic};
		mDoseClassSelecter.setAdapter(getSimpleAdapter(mDoseClassSelecter, str, imageID));
	}
	
	private void setDoseLevel() {
		String[] str = {"2", "1", "0", "-1"};
		int []imageID = {R.drawable.dose_level_pic, R.drawable.dose_level_pic, R.drawable.dose_level_pic, R.drawable.dose_level_pic};
		mCompenClassSelecter.setAdapter(getSimpleAdapter(mCompenClassSelecter, str, imageID));
	}
	
	
	
	private SimpleAdapter getSimpleAdapter(final Spinner spinner, final String[] str, final int[] values) {
		List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
		for(int i = 0; i < str.length; i++) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("letter", str[i]);
			item.put("pic", values[i]);
			items.add(item);
		}
		
		return (new SimpleAdapter(getActivity(), items, 
				R.layout.spinner_adapter_layout_vertical,
				new String[] {"letter", "pic"},
				new int[] {R.id.spinner_text_vertical, R.id.spinner_image_vertical}) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if(convertView == null) {
					View root = getActivity().getLayoutInflater().inflate(R.layout.spinner_adapter_layout_vertical, parent, false);
					int disWidth = getActivity().getResources().getDimensionPixelSize(R.dimen.spinner_vertical_view_width);
					((LinearLayout)root).setLayoutParams(new LinearLayout.LayoutParams(disWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
					((TextView)root.findViewById(R.id.spinner_text_vertical)).setText(str[position]);
					((ImageView)root.findViewById(R.id.spinner_image_vertical)).setImageResource(values[position]);
					return root;
				} else
					return convertView;
			}
			
			@Override
			public View getDropDownView(int position, View convertView, ViewGroup parent) {
				View view = super.getDropDownView(position, convertView, parent);
				LinearLayout layout = (LinearLayout) view.findViewById(R.id.spinner_layout_vertical);
				if (spinner.getSelectedItemPosition() == position) {
					layout.setBackgroundResource(R.drawable.spinner_select_item_large);
				}
				return view;
			}
		});
	}
}

package com.hedymed.fragment;

import static com.hedymed.db.AppDataStruct.appData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.hedymed.drdissys.MainActivity;
import com.hedymed.drdissys.R;
import com.hedymed.uart.uartUtils;

public class thirdFragment extends Fragment{
	public static final String TAG = "thirdFragment";
	public static final String RECEIVER_ACTION = "com.hedymed.drdissys.thirdFragment.recvData";
	public static final int VIDEO_REQUEST_CODE = 3;
	public static final int MOVE_FOLLOW_OFF = 0;
	public static final int MOVE_FOLLOW_ON = 1;
	public static final int FREE_BOX_MODE = 3;
	
	private ToggleButton mMoveFollow;
	private Spinner mSampleMode;
	private Spinner mFilterSelecter;
	private Button mReset, mVideoPlay;
	//skip do something in OnItemSelectedListener when Spinner instancing
	private Boolean mSampleModeFlag, mFilterselecterFlag;
	private View mRootView;
	private thirdFragmentReceiver mReceiver;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		registerMainFragmentReceiver();
//		setRetainInstance(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(mRootView == null) {
			mRootView = inflater.inflate(R.layout.third_fragment, container, false);
			mMoveFollow = (ToggleButton)mRootView.findViewById(R.id.move_follow_button);
			mSampleMode = (Spinner)mRootView.findViewById(R.id.sample_mode_selecter);
			mFilterSelecter = (Spinner)mRootView.findViewById(R.id.filter_selecter);
			mReset = (Button)mRootView.findViewById(R.id.reset_button);
			mVideoPlay = (Button)mRootView.findViewById(R.id.video_play_button);
			
			mSampleModeFlag = false;
			mFilterselecterFlag = false;
			
			setSampleModeSpinner();
			setFilterLevelSpinner();
			registerEventMonitor();
			refreshFragment();
		}
		
		return mRootView;
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		getActivity().unregisterReceiver(mReceiver);
	}
	
	private void registerEventMonitor(){
		mSampleMode.setOnItemSelectedListener(new customSpinnerListener());
		mFilterSelecter.setOnItemSelectedListener(new customSpinnerListener());
		mMoveFollow.setOnCheckedChangeListener(new customToggleButtonListener()); 
		mVideoPlay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);  
			    intent.setType("video/*");  
			    intent.addCategory(Intent.CATEGORY_OPENABLE);  
			    try {  
			        getActivity().startActivityForResult(Intent.createChooser(intent, null), VIDEO_REQUEST_CODE);  
			    } 
			    catch (android.content.ActivityNotFoundException ex) {  
			        Toast.makeText(getActivity(), "请安装文件管理器", Toast.LENGTH_SHORT).show();  
			    }  
			}
		});
		
	}

	 class customToggleButtonListener implements CompoundButton.OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			 if(buttonView instanceof ToggleButton) {
				ToggleButton button = (ToggleButton)buttonView;
				int argument = 0;
				
				if(mMoveFollow == button) {
					if(isChecked) {
						Log.i("thirdFragment", "Move Follow ON");
						argument = MOVE_FOLLOW_ON;
					} else {
						Log.i("thirdFragment", "Move Follow OFF");
						argument = MOVE_FOLLOW_OFF;
					}
					
					if(appData.get("FOLL") != argument) {
						appData.put("FOLL", argument);
						uartUtils.sendToSendThread("FOLL" + argument);
					}
				}
			}
		}	
	}
	
	class customSpinnerListener implements AdapterView.OnItemSelectedListener {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			Spinner spinner = (Spinner)parent;
			int curPosition = spinner.getSelectedItemPosition();
			
			if(spinner == mSampleMode) {
				if(!mSampleModeFlag)
					mSampleModeFlag = true;
				else {
					Log.i("thirdFragment", String.format("mSampleMode selected position is %d", position));
					if(curPosition == FREE_BOX_MODE) {
						appData.put("ASENABLE", 0);
						appData.put("AECENABLE", 0);
					} else {
						appData.put("ASENABLE", 1);
						appData.put("AECENABLE", 1);
					}
					sendRefreshBroadcast(secondFrament.RECEIVER_ACTION);
					
					if(appData.get("SMOD") != curPosition) {
						appData.put("SMOD", curPosition);
						
						uartUtils.sendToSendThread("SMOD" + position);
					}
				}
			} else if(spinner == mFilterSelecter) {
				if(!mFilterselecterFlag)
					mFilterselecterFlag = true;
				else {
					Log.i("thirdFragment", String.format("mFilterSelecter selected position is %d", position));
					if(appData.get("FILT") != curPosition) {
						appData.put("FILT", curPosition);
						uartUtils.sendToSendThread("FILT" + position);
					}
				}
			} 
		}
		
		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
		}
	}
	
	private void sendRefreshBroadcast(String action) {
		Intent intent = new Intent();
		intent.setAction(action);
		Bundle bundle = new Bundle();
		bundle.putString("action", "refresh");
		intent.putExtras(bundle);
		getActivity().sendBroadcast(intent);
	}
	
	public void registerMainFragmentReceiver() {
		IntentFilter filter = new IntentFilter(RECEIVER_ACTION);
		mReceiver = new thirdFragmentReceiver();
		getActivity().registerReceiver(mReceiver, filter);
	}
	
	public class thirdFragmentReceiver extends BroadcastReceiver 
	{
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			switch((String)bundle.getCharSequence("action")) {
			case "refresh":
				refreshFragment();
				break;
			case "uartCMD":
				MVC_control_handler((String)bundle.getCharSequence("cmd"));
				break;
			}
		}
	}
	
	//for initialize when parse configure xml.
	public void refreshFragment() {
		try {
			int tempValue = appData.get("FOLL");
			if(tempValue == MOVE_FOLLOW_OFF)
				mMoveFollow.setChecked(false);
			else if(tempValue == MOVE_FOLLOW_ON)
				mMoveFollow.setChecked(true);
			
			MainActivity.setSpinnerSelect(mSampleMode, appData.get("SMOD"));
			MainActivity.setSpinnerSelect(mFilterSelecter, appData.get("FILT"));
		}
		catch(NullPointerException e) {
			Log.i("secondFragment:refreshFragment", "first refresh secondFragment will cause nullException");
		}
	}
	
	public void MVC_control_handler(String cmd) {
//		if(isVisible())
			MVC_view_handler(cmd);
	}
	
	private void MVC_view_handler(String cmd) {
		try{
			int mapValue;
			switch(cmd) {
			case "FOLL":
				mapValue = appData.get("FOLL");
				if(mapValue == MOVE_FOLLOW_OFF)
					mMoveFollow.setChecked(false);
				else if(mapValue == MOVE_FOLLOW_ON)
					mMoveFollow.setChecked(true);
				break;
			
			case "SMOD":
				MainActivity.setSpinnerSelect(mSampleMode, appData.get("SMOD"));
				break;
			
			case "FILT":
				MainActivity.setSpinnerSelect(mFilterSelecter, appData.get("FILT"));
				break;
				
			default:
				break;
			}
		} 
		catch (NullPointerException e) {
			Log.i("ThirdFrament:handleCmd", "This Fragment NOT Attach Now");
		}
	}
	
	
	private void setSampleModeSpinner() {
		String[] bodyPosStr = getActivity().getResources().getStringArray(R.array.sample_mode_string_array);
		int[] imageID = { R.drawable.sample_bed, R.drawable.sample_chest_shelf,
				R.drawable.sample_free_panel,  R.drawable.sample_free_box };
		mSampleMode.setAdapter(getHorizontalSimpleAdapter(mSampleMode, bodyPosStr, imageID));
	}
	
	private void setFilterLevelSpinner() {
		String[] str = {"0.1", "0.2", "0.3"};
		int []imageID = {R.drawable.filter_tiny_pic, R.drawable.filter_tiny_pic, R.drawable.filter_tiny_pic};
		mFilterSelecter.setAdapter(getVerticalSimpleAdapter(mFilterSelecter, str, imageID));
	}
	
	
	
	private SimpleAdapter getVerticalSimpleAdapter(final Spinner spinner, final String[] str, final int[] values) {
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
	
	private SimpleAdapter getHorizontalSimpleAdapter(final Spinner spinner, final String[] str, final int[] values) {
		List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < str.length; i++) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("letter", str[i]);
			item.put("pic", values[i]);
			items.add(item);
		}

		return (new SimpleAdapter(getActivity(), items, R.layout.spinner_adapt_layout,
				new String[] { "letter", "pic" }, new int[] { R.id.spinner_text, R.id.spinner_image }) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if(convertView == null) {
					View root = getActivity().getLayoutInflater().inflate(R.layout.spinner_adapt_layout, parent, false);
					int disWidth = getActivity().getResources().getDimensionPixelSize(R.dimen.spinner_view_width);
					((LinearLayout)root).setLayoutParams(new LinearLayout.LayoutParams(disWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
					((TextView)root.findViewById(R.id.spinner_text)).setText(str[position]);
					((ImageView)root.findViewById(R.id.spinner_image)).setImageResource(values[position]);
					return root;
				} else
					return convertView;
			}
			
			@Override
			public View getDropDownView(int position, View convertView, ViewGroup parent) {
				View view = super.getDropDownView(position, convertView, parent);
				LinearLayout layout = (LinearLayout) view.findViewById(R.id.spinner_layout);

				if (spinner.getSelectedItemPosition() == position) {
					layout.setBackgroundResource(R.drawable.spinner_select_item);
				}
				return view;
			}
		});
	}
}


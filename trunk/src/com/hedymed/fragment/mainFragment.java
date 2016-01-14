package com.hedymed.fragment;

import static com.hedymed.db.AppDataStruct.FOCUS_BIG;
import static com.hedymed.db.AppDataStruct.FOCUS_SMALL;
import static com.hedymed.db.AppDataStruct.appData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.hedymed.db.AppDataStruct;
import com.hedymed.drdissys.MainActivity;
import com.hedymed.drdissys.R;
import com.hedymed.pieces.addSubView;
import com.hedymed.uart.uartUtils;

public class mainFragment extends Fragment {
	private addSubView mHVGVolatge;
	private addSubView mHVGmA;
	private addSubView mHVGmAs;
	private addSubView mHVGms;
	private Spinner mHvgArgSelecter;
	private Spinner mPositionSelecter;
	private Spinner mAgeSelecter;
	private Spinner mBodyTypeSelecter;
	private boolean mHvgArgSelecterInit, mPositionSelecterInit, mAgeSelecterInit, mBodyTypeSelecterInit;
	private RadioButton mBigFocusButton, mSmallFocusButton;
	private View rootView;
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setRetainInstance(true);
	}
	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i("mainFragment", "in onCreateView");
		if(rootView == null) {
			rootView = inflater.inflate(R.layout.main_fragment, container, false);
			mHVGVolatge = (addSubView) rootView.findViewById(R.id.hvg_voltage);
			mHVGmA = (addSubView) rootView.findViewById(R.id.hvg_current);
			mHVGmAs = (addSubView) rootView.findViewById(R.id.hvg_mas);
			mHVGms = (addSubView) rootView.findViewById(R.id.hvg_ms);
			
			mHvgArgSelecter = (Spinner) rootView.findViewById(R.id.HVG_arg_selecter);
			mPositionSelecter = (Spinner) rootView.findViewById(R.id.position_selecter);
			mAgeSelecter = (Spinner) rootView.findViewById(R.id.age_selecter);
			mBodyTypeSelecter = (Spinner) rootView.findViewById(R.id.body_type_selecter);
			mBigFocusButton = (RadioButton)rootView.findViewById(R.id.big_focus);
			mSmallFocusButton = (RadioButton)rootView.findViewById(R.id.small_focus);
			
			mHvgArgSelecterInit = false;
			mPositionSelecterInit = false;
			mAgeSelecterInit = false;
			mBodyTypeSelecterInit = false;
			
			setHVGArgSelecter();//设置adapter
			setPositionSelecter();//设置adapter
			setAgeSelecter();//设置adapter
			setBodyType();//设置adapter
			setFunction();//设置 KV、MA、MAS、MS
			registerEventMonitor();
		}
		
		return rootView;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Log.i("mainFragment", "in onResume" + getActivity().toString());
	}
	
	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
		Log.i("mainFragment", "in onDetach");
	}

	private void setAddSubViewEnable(int mode){
		switch(mode){
		case 0:
			mHVGVolatge.setEnabled(true);
			mHVGmA.setEnabled(false);
			mHVGmAs.setEnabled(true);
			mHVGms.setEnabled(false);
			break;
		case 1:
			mHVGVolatge.setEnabled(true);
			mHVGmA.setEnabled(false);
			mHVGmAs.setEnabled(false);
			mHVGms.setEnabled(false);
			break;
		case 2:
			mHVGVolatge.setEnabled(true);
			mHVGmA.setEnabled(true);
			mHVGmAs.setEnabled(false);
			mHVGms.setEnabled(false);
			break;
		case 3:
			mHVGVolatge.setEnabled(true);
			mHVGmA.setEnabled(true);
			mHVGmAs.setEnabled(true);
			mHVGms.setEnabled(true);
			break;
		case 4:
			mHVGVolatge.setEnabled(true);
			mHVGmA.setEnabled(false);
			mHVGmAs.setEnabled(true);
			mHVGms.setEnabled(true);
			break;
		default:
			break;
		}
	}
	
	class customSpinnerListener implements AdapterView.OnItemSelectedListener {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			Spinner spinner = (Spinner)parent;
			int curPosition = spinner.getSelectedItemPosition();
			
			if(spinner == mHvgArgSelecter) {
				if(!mHvgArgSelecterInit)
					mHvgArgSelecterInit = true;
				else {
					Log.i("mainFragment", String.format("mHvgArgSelecter selected position is %d", position));
					setAddSubViewEnable(curPosition);//put this position for power up
					if(appData.get("ET") != curPosition) {
						appData.put("ET", curPosition);
						uartUtils.sendToSendThread("ET" + curPosition);
					}
				}
			} else if(spinner == mPositionSelecter) {
				if(!mPositionSelecterInit)
					mPositionSelecterInit = true;
				else {
					Log.i("mainFragment", String.format("mPositionSelecter selected position is %d", position));
					if(appData.get("POS") != curPosition) {
						appData.put("POS", curPosition);
						uartUtils.sendToSendThread("POS" + curPosition);
					}
				}
			} else if (spinner == mAgeSelecter) {
				if(!mAgeSelecterInit)
					mAgeSelecterInit = true;
				else {
					Log.i("mainFragment", String.format("mAgeSelecter selected position is %d", position));
					if(appData.get("AG") != curPosition) {
						appData.put("AG", curPosition);
						uartUtils.sendToSendThread("AG" + curPosition);
					}
				}
			} else if(spinner == mBodyTypeSelecter) {
				if(!mBodyTypeSelecterInit)
					mBodyTypeSelecterInit = true;
				else {
					Log.i("mainFragment", String.format("mBodyTypeSelecter selected position is %d", position));
					if(appData.get("BOD") != curPosition) {
						appData.put("BOD", curPosition);
						uartUtils.sendToSendThread("BOD" + curPosition);
					}
				}
			}
		}
		
		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
		}
	}
	
	
	private void registerEventMonitor(){
		mHvgArgSelecter.setOnItemSelectedListener(new customSpinnerListener());
		mPositionSelecter.setOnItemSelectedListener(new customSpinnerListener());
		mAgeSelecter.setOnItemSelectedListener(new customSpinnerListener());
		mBodyTypeSelecter.setOnItemSelectedListener(new customSpinnerListener());
		
		mBigFocusButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					Log.i("mainFragment", "mBigFocusButton isChecked");
					if(appData.get("FOC") != FOCUS_BIG) {
						appData.put("FOC", (int) FOCUS_BIG);
						uartUtils.sendToSendThread("FOC" + FOCUS_BIG);
					}
				}
			}
		});
		
		mSmallFocusButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					Log.i("mainFragment", "mSmallFocusButton isChecked");
					if(appData.get("FOC") != FOCUS_SMALL) {
						appData.put("FOC", (int) FOCUS_SMALL);
						uartUtils.sendToSendThread("FOC" + FOCUS_SMALL);
					}
				}
			}
		});
		
	}
	
	private void setPositionSelecter() {
		String[] bodyPosStr = getActivity().getResources().getStringArray(R.array.body_pos_string);
		int[] imageID = { R.drawable.pos_head_up, R.drawable.pos_head_down };
		mPositionSelecter.setAdapter(getSimpleAdapter(mPositionSelecter, bodyPosStr, imageID));
	}

	private void setAgeSelecter() {
		String[] bodyPosStr = getActivity().getResources().getStringArray(R.array.age_string);
		int[] imageID = { R.drawable.age_infant, R.drawable.age_child, R.drawable.age_adult };
		mAgeSelecter.setAdapter(getSimpleAdapter(mAgeSelecter, bodyPosStr, imageID));
	}

	private void setBodyType() {
		String[] bodyPosStr = getActivity().getResources().getStringArray(R.array.body_size_string);
		int[] imageID = { R.drawable.body_fat, R.drawable.body_mid, R.drawable.body_hide };
		mBodyTypeSelecter.setAdapter(getSimpleAdapter(mBodyTypeSelecter, bodyPosStr, imageID));
	}

	private SimpleAdapter getSimpleAdapter(final Spinner spinner, final String[] str, final int[] values) {
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

	// 设置高压参数spinner
	private void setHVGArgSelecter() {
		final String[] hvgArgArray = this.getResources().getStringArray(R.array.hvg_args_string);
		mHvgArgSelecter.setAdapter(getArrayAdapt(hvgArgArray));
	}

	private ArrayAdapter<String> getArrayAdapt(final String[] values) {
		return (new ArrayAdapter<String>(getActivity(), R.layout.spinner_adapt_layout, R.id.spinner_text, values) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if(convertView == null) {
					View root = getActivity().getLayoutInflater().inflate(R.layout.spinner_view_h, parent, false);
					((TextView)root.findViewById(R.id.spinner_view_text)).setText(values[position]);
					return root;
				} else
					return convertView;
			}
			
			@Override
			public View getDropDownView(int position, View convertView, ViewGroup parent) {
				View view = super.getDropDownView(position, convertView, parent);
				LinearLayout layout = (LinearLayout) view.findViewById(R.id.spinner_layout);
				if (mHvgArgSelecter.getSelectedItemPosition() == position) {
					layout.setBackgroundResource(R.drawable.spinner_select_item);
				}
				return view;
			}
		});
	}

	public void refreshFragment() {
		try{
			MainActivity.setSpinnerSelect(mHvgArgSelecter, appData.get("ET"));
			MainActivity.setSpinnerSelect(mPositionSelecter, appData.get("POS"));
			MainActivity.setSpinnerSelect(mAgeSelecter, appData.get("AG"));
			MainActivity.setSpinnerSelect(mBodyTypeSelecter, appData.get("BOD"));
			
			mHVGVolatge.setValue(appData.get("KV"));
			mHVGmA.setValue(appData.get("MA") / 10);
			mHVGmAs.setValue(appData.get("MAS") / 10.0);
			mHVGms.setValue(appData.get("MS") / 10);
	
			if (AppDataStruct.FOCUS_BIG == appData.get("FOC"))
				mBigFocusButton.setChecked(true);
			else if (AppDataStruct.FOCUS_SMALL == appData.get("FOC"))
				mSmallFocusButton.setChecked(true);
		}
		catch(NullPointerException e) {
			Log.i("mainFragment:refreshFragment", "first refresh mainFragment will cause nullException");
		}
	}
	
	public void MVC_control_handler(String[] cmdAndArg) {
		MVC_view_handler(cmdAndArg);
	}
	
	private void MVC_view_handler(String[] cmdAndArg) {
		try {
			switch(cmdAndArg[0]) {
			case "ET":
				MainActivity.setSpinnerSelect(mHvgArgSelecter, appData.get("ET"));
				break;
				
			case "POS":
				MainActivity.setSpinnerSelect(mPositionSelecter, appData.get("POS"));
				break;
			
			case "AG":
				MainActivity.setSpinnerSelect(mAgeSelecter, appData.get("AG"));
				break;
			
			case "BOD":
				MainActivity.setSpinnerSelect(mBodyTypeSelecter, appData.get("BOD"));
				break;
				
			case "FOC":
				int mapValue = appData.get("FOC");
				if (AppDataStruct.FOCUS_BIG == mapValue)
					mBigFocusButton.setChecked(true);
				else if (AppDataStruct.FOCUS_SMALL == mapValue)
					mSmallFocusButton.setChecked(true);
				break;
			
			case "KV":
				mHVGVolatge.setValue(appData.get("KV"));
				break;
			
			case "MA":
				mHVGmA.setValue(appData.get("MA") / 10);
				break;
				
			case "MAS":
				mHVGmAs.setValue(appData.get("MAS") / 10.0);
				break;
			
			case "MS":
				mHVGms.setValue(appData.get("MS") / 10);
				break;
				
			default:
				break;
			}
		} 
		catch (NullPointerException e) {
			Log.i("mainFrament:handleCmd", "This Fragment NOT Attach Now");
		}
	}
	
	private void setFunction() {
		mHVGVolatge.setFunctoin("KV");
		mHVGmA.setFunctoin("MA");
		mHVGmAs.setFunctoin("MAS");
		mHVGms.setFunctoin("MS");
	}

	public String getKv() {
		return mHVGVolatge.getValue();
	}

	public String getmA() {
		return mHVGmA.getValue();
	}

	public String getKmAs() {
		return mHVGmAs.getValue();
	}

	public String getms() {
		return mHVGms.getValue();
	}
	
//	@Override
//	public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
//		// TODO Auto-generated method stub
//		return super.onCreateAnimation(transit, enter, nextAnim);
//	}
//	@Override
//	public Animation onCreateAnimator(int transit, boolean enter, int nextAnim) {
////		super.onCreateAnimation(transit, enter, nextAnim);
//		Animator animator = null;
//		final int animID = nextAnim;
//		
//		if(nextAnim == R.animator.fragment_slide_out_right || nextAnim == R.animator.fragment_slide_out_left ||
//					nextAnim == R.animator.fragment_slide_in_left || nextAnim == R.animator.fragment_slide_in_right) {
//			animator = AnimatorInflater.loadAnimator(getActivity(), nextAnim);
//		}
//			
//		if (animator != null && enter) {
//			animator.addListener(new Animator.AnimatorListener() {
//				
//				@Override
//				public void onAnimationStart(Animator animation) {
//					// TODO Auto-generated method stub
//					
//				}
//				
//				@Override
//				public void onAnimationRepeat(Animator animation) {
//					// TODO Auto-generated method stub
//					
//				}
//				
//				@Override
//				public void onAnimationEnd(Animator animation) {
//					Activity activity = getActivity();
//					MainActivity mainActivity = null;
//					
//					if(activity instanceof MainActivity) {
//						mainActivity = (MainActivity)activity;
//						mainActivity.setSwitchFragmentEnable(true);
//						
//						if(animID == R.animator.fragment_slide_out_right || animID == R.animator.fragment_slide_out_left) 
//							mainActivity.switchPageDot("secondFragment");
//						else 
//							mainActivity.switchPageDot("mainFragment");
//					}
//				}
//				
//				@Override
//				public void onAnimationCancel(Animator animation) {
//					// TODO Auto-generated method stub
//					
//				}
//			});
//        }
//		return animator;
//	}

}

package com.hedymed.drdissys;

import static com.hedymed.drdissys.AppDataStruct.appData;
import static com.hedymed.drdissys.AppDataStruct.appStringData;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hedymed.net.netRecvThread;
import com.hedymed.uart.uartUtils;

public class MainActivity extends Activity {
	private static final int[] parityFlag = { 'n', 'o', 'e', 'm', 's' };
	private static final String PACK_UP_METHOD = "onDetachedFromWindow";//Spinner类中protected类型的方法，其作用是使其下拉菜单隐藏。
	public static final String UI_HANDLER_KEY = "ui_hander_key";
	enum enumFragment{
		MAIN_FRA, SECOND_FRA;
	}
	
	private ImageView mBodyPicture;//体位图
	private TextView mNameText;//姓名
	private TextView mSexText;//性别
	private TextView mAgeText;//年龄
	private TextView mIdText;//ID
	private ImageView mPageSwitchPic;//fragment 切换指示
	private TextView mErrDisText;//错误提示
	private ImageView mExpouseStatusPic;//曝光状态图标
	private ImageView mAlignmentStatusPic;//对齐状态图标
	private ImageView mFpdDirection;//平板方向图标
	private ImageView mThermalPicture;//热容量图标
	
	private TextView mRhaText;
	private TextView mRvaText;
	private TextView mSidText;
	private TextView mGridSid;
	private TextView mGridRate;
	private TextView mGridMaterial;
	private TextView mThermalText;
	private Button mHelpButton;
	
	private mainFragment mMainFra;
	private secondFrament mSecondFra;
	private static enumFragment mCurFragment;
	public static Handler mUiHandler;
	private static int[] mBodyPicResourceIDArray;
	private boolean mSwitchFragmentEnable = true;
	uartUtils mUart = new uartUtils(this);
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		findAllWidget();
		initHander();
		
//		mDetector = new GestureDetector(this, new customGestureListener());
		//打开串口 8O1, 115200bps
		mUart.uartOpen(0, 115200, 8, 1, parityFlag[1]);
		
		if(mBodyPicResourceIDArray == null)
			mBodyPicResourceIDArray = findBodyPosPicArray();
		
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		if(savedInstanceState != null) {
			mMainFra = (mainFragment) getFragmentManager().findFragmentByTag("mainFragment");
			mSecondFra = (secondFrament) getFragmentManager().findFragmentByTag("secondFragment");
			
			if(mCurFragment == enumFragment.MAIN_FRA && mSecondFra !=null)
				ft.hide(mSecondFra).commit();
			else if(mCurFragment == enumFragment.SECOND_FRA && mMainFra != null)	
				ft.hide(mMainFra).commit();
		} else {
			mMainFra = new mainFragment();
			mSecondFra = new secondFrament();
			mCurFragment = enumFragment.MAIN_FRA;
			
			ft.add(R.id.fragment_frame, mSecondFra, "secondFragment");
			ft.hide(mSecondFra).add(R.id.fragment_frame, mMainFra, "mainFragment");
			ft.commit();
		}
		
		new netRecvThread(this, "netRecvThread").start();
	}
	
//	@Override
//	public void onConfigurationChanged(Configuration newConfig) {
//		super.onConfigurationChanged(newConfig);
//		Log.i("activity", "go to here");
//		setRequestedOrientation(newConfig.orientation);
//	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mUart.uartClose();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
//		return mDetector.onTouchEvent(event);
		return true;
	}
	
	public void FragmentSlip(boolean direction) {
		if(mSwitchFragmentEnable) {
			mSwitchFragmentEnable = false;
			if(mCurFragment == enumFragment.MAIN_FRA) { // switch to second Fragment
				mCurFragment = enumFragment.SECOND_FRA;
				switchFragment(mMainFra, mSecondFra, "secondFragment", direction);
//				mPageSwitchPic.setImageResource(R.drawable.page_2);
			} else {// switch to main Fragment
				mCurFragment = enumFragment.MAIN_FRA;
				switchFragment(mSecondFra, mMainFra, "mainFragment", direction);
//				mPageSwitchPic.setImageResource(R.drawable.page_1);
			}
		}
	}
	
	private void switchFragment(Fragment from, Fragment to, String tag, boolean SlipDirectionRight) {
        if (from != to) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            
            if(SlipDirectionRight)
            	transaction.setCustomAnimations(R.animator.fragment_slide_in_left, R.animator.fragment_slide_out_right); 
            else
            	transaction.setCustomAnimations(R.animator.fragment_slide_in_right, R.animator.fragment_slide_out_left); 
            
            if (!to.isAdded()) {    // 先判断是否被add过
                transaction.hide(from).add(R.id.fragment_frame, to, tag).commit(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                transaction.hide(from).show(to).commit(); // 隐藏当前的fragment，显示下一个
            }
        }
    }
	
	public void switchPageDot(String fragmentTag) {
		if("mainFragment".equals(fragmentTag))
			mPageSwitchPic.setImageResource(R.drawable.page_1);
		else if("secondFragment".equals(fragmentTag))
			mPageSwitchPic.setImageResource(R.drawable.page_2);
	}
	
	public void setSwitchFragmentEnable(boolean enableFlag) {
		mSwitchFragmentEnable = enableFlag;
	}
	
	private int[] findBodyPosPicArray(){
		TypedArray ar = this.getResources().obtainTypedArray(R.array.body_pose_pic);
		int len = ar.length();     
		int[] resIds = new int[len];     
		for (int i = 0; i < len; i++)     
		    resIds[i] = ar.getResourceId(i, 0);
		
		ar.recycle();
		return resIds;
	}
	
	private byte[] hexString2ByteArray(String str) {
		int loopTimes = (str.length() + 1) / 2;
		byte[] hexArray = new byte[loopTimes];
		for(int index = 0; index < loopTimes; index++) {
			int endIndex = (index * 2 + 2) <= str.length() ? (index * 2 + 2) : str.length();
			byte temp = (byte)Short.parseShort(str.substring(index * 2, endIndex), 16);
			hexArray[index] = temp;
		}
		return hexArray;
	}
	
	public static void sendToUiThread(String[] cmdStr) {
		Message msg = Message.obtain(mUiHandler);
		Bundle bundle = new Bundle();
		bundle.putCharSequenceArray(UI_HANDLER_KEY, cmdStr);
		msg.setData(bundle);
		mUiHandler.sendMessage(msg);
	}
	
	private void initHander() {
		mUiHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				String[] cmdString = (String[]) msg.getData().getCharSequenceArray(UI_HANDLER_KEY);
				MVC_model_handler(cmdString);
				MVC_view_handler(cmdString);
			}
		};
	}
	
	private void MVC_model_handler(String[] cmdAndArg) {
		try{
			switch(cmdAndArg[0]) {
			case "GRID":
				String sidStr = cmdAndArg[1].substring(0, 3);
				String rateStr = cmdAndArg[1].substring(3, 6);
				String materialStr = cmdAndArg[1].substring(6);
				
				appData.put("GRID_SID", Integer.parseInt(sidStr));
				appData.put("GRID_RATE", Integer.parseInt(rateStr));
				appData.put("GRID_MATERIAL", Integer.parseInt(materialStr));
				break;
			
			case "APR":
				int picIndex = bodyPoseArray.getBodyPicNum(cmdAndArg[1]);
				if(picIndex >= 0)
					appData.put("APR", picIndex);
				break;
				
			case "NAM":
			case "SEX":
			case "ID":
				appStringData.put(cmdAndArg[0], cmdAndArg[1]);
				break;
			
			default:
				appData.put(cmdAndArg[0], Integer.parseInt(cmdAndArg[1]));
				break;
			}
		} 
		catch (NumberFormatException e) {
			Toast.makeText(this, cmdAndArg[0] + 
					" cmd argument is " +  cmdAndArg[1] + ". it is error", Toast.LENGTH_LONG).show();
		}
	}
	
	private void MVC_view_handler(String[] cmdAndArg) {
		try {
			switch(cmdAndArg[0]) {
			case "PIC":
				//Toast.makeText(MainActivity.this, (String)msg.obj, Toast.LENGTH_LONG).show();
				Intent intent = new Intent().setClassName(MainActivity.this, "com.hedymed.drdissys.disPictureActivity");
				//Bundle bundle = new Bundle()
				//intent.putExtras(extras);
				startActivity(intent);
				break;
				
			case "NAM":
				try {
					byte[] array = hexString2ByteArray(appStringData.get("NAM"));
					String str = new String(array, "UTF-16BE");
					mNameText.setText(str);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				break;
				
			case "SEX":
				try {
					byte[] array = hexString2ByteArray(appStringData.get("SEX"));
					String str = new String(array, "UTF-16BE");
					mSexText.setText(str);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				break;
				
			case "ID":
				mIdText.setText(appStringData.get("ID"));
				break;
				
//			case NEW_errDisText:
//				//查询数据库
//				break;
				
			case "APR":
				mBodyPicture.setImageResource(mBodyPicResourceIDArray[appData.get("APR")]);
				break;
				
			case "AGE":
				mAgeText.setText(String.valueOf(appData.get("AGE")));
				break;
				
			case "ES":
				int mapValue = appData.get("ES");
				if(mapValue == AppDataStruct.expose_status.DISABLE_EXPOSE.ordinal())
					mExpouseStatusPic.setImageResource(R.drawable.expose_disable_pic);
				else if(mapValue == AppDataStruct.expose_status.ENABLE_EXPOSE.ordinal())
					mExpouseStatusPic.setImageResource(R.drawable.expose_enable_pic);
				else if(mapValue == AppDataStruct.expose_status.PREP_PHASE.ordinal())
					mExpouseStatusPic.setImageResource(R.drawable.expose_prep_pic);
				else if(mapValue == AppDataStruct.expose_status.EXPOSE_PHASE.ordinal())
					mExpouseStatusPic.setImageResource(R.drawable.exposing_pic);
				
				break;
			
			case "AI":
				mapValue = appData.get("AI");
				if(mapValue == AppDataStruct.alignment_status.HAVE_ALIGNMENT.ordinal())
					mAlignmentStatusPic.setImageResource(R.drawable.alignment_pic);
				else if(mapValue == AppDataStruct.alignment_status.NOT_ALIGNMENT.ordinal())
					mAlignmentStatusPic.setImageResource(R.drawable.alignment_no_pic);
				break;
				
			case "FPD":
				mapValue = appData.get("FPD");
				if(mapValue < 0x10) {//未插到位
					if(AppDataStruct.FPD_WS_H_INSERT == (mapValue & 0x0F))
						mFpdDirection.setImageResource(R.drawable.fpd_direction_land_yellow);//WS 横插
					else if(AppDataStruct.FPD_WS_V_INSERT == (mapValue & 0x0F))
						mFpdDirection.setImageResource(R.drawable.fpd_direction_land_yellow);//WS 竖插
					else if(AppDataStruct.FPD_FT_H_INSERT == (mapValue & 0x0F))
						mFpdDirection.setImageResource(R.drawable.fpd_direction_stand_yellow);//FT 横插
					else if(AppDataStruct.FPD_FT_V_INSERT == (mapValue & 0x0F))
						mFpdDirection.setImageResource(R.drawable.fpd_direction_stand_yellow);//FT 竖插
				} else {
					if(AppDataStruct.FPD_WS_H_INSERT == (mapValue & 0x0F))
						mFpdDirection.setImageResource(R.drawable.fpd_direction_land);//WS 横插
					else if(AppDataStruct.FPD_WS_V_INSERT == (mapValue & 0x0F))
						mFpdDirection.setImageResource(R.drawable.fpd_direction_land);//WS 竖插
					else if(AppDataStruct.FPD_FT_H_INSERT == (mapValue & 0x0F))
						mFpdDirection.setImageResource(R.drawable.fpd_direction_stand);//FT 横插
					else if(AppDataStruct.FPD_FT_V_INSERT == (mapValue & 0x0F))
						mFpdDirection.setImageResource(R.drawable.fpd_direction_stand);//FT 竖插
				}
				break;
				
			case "RHA":
				mRhaText.setText(appData.get("RHA") + "°");
				break;
			
			case "RVA":
				mRvaText.setText(appData.get("RVA") + "°");
				break;
			
			case "SID":
				mSidText.setText(appData.get("SID") + "cm");
				break;
				
			case "GRID":
				mGridSid.setText(String.valueOf(appData.get("GRID_SID")));//设置滤线栅焦距
				mGridRate.setText(appData.get("GRID_RATE") + ":1");//设置滤线栅密度
				//设置滤线栅材质
				mapValue = appData.get("GRID_MATERIAL");
				if(mapValue == AppDataStruct.GRID_MATERIAL_AL)
					mGridMaterial.setText(R.string.grid_mateial_al);
				else if(mapValue == AppDataStruct.GRID_MATERIAL_CARBON)
					mGridMaterial.setText(R.string.grid_mateial_carbon);
				break;
			
			case "TC":
				mapValue = appData.get("TC");
				mThermalText.setText(mapValue + "%");
				if(mapValue <= 25)
					mThermalPicture.setImageResource(R.drawable.thermal_pic_25);
				else if(mapValue <= 50)
					mThermalPicture.setImageResource(R.drawable.thermal_pic_50);
				else if(mapValue <= 75)
					mThermalPicture.setImageResource(R.drawable.thermal_pic_75);
				else if(mapValue <= 100)
					mThermalPicture.setImageResource(R.drawable.thermal_pic_100);
				break;
			
			case "ANG":
				mapValue = appData.get("ANG");
				if(mapValue < -50)
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
				else if(mapValue > -45 && mapValue < 45)
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
				if(mapValue > 50)
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				break;
				
			default:
				mMainFra.MVC_control_handler(cmdAndArg);
				mSecondFra.MVC_control_handler(cmdAndArg);
				break;
			}
		} catch (NumberFormatException e) {
			Toast.makeText(MainActivity.this, cmdAndArg[0] + 
					" cmd argument is " +  cmdAndArg[1] + ". it is error", Toast.LENGTH_LONG).show();
		}
	}
	
	public static void packUpSpinnerPopup(Spinner mSpinner){
	    try {
	        Class<? extends Spinner> spinner = mSpinner.getClass();//mSpinner是该类中实例化的Spinner对象
	        Method method = spinner.getDeclaredMethod(PACK_UP_METHOD);//该方法是无参方法
	        method.setAccessible(true);//使该方法可以被调用
	        method.invoke(mSpinner);//mSpinner是该类中实例化的Spinner对象
	    } catch (Exception e) {
	        Log.i("info", e.toString());
	        e.printStackTrace();
	    }
	}
	
	//return true, skip uart send one times.
	public static void setSpinnerSelect(Spinner spinner, int count) {
		if((count < spinner.getAdapter().getCount()) && (count >= 0)) {
			spinner.setSelection(count);
			MainActivity.packUpSpinnerPopup(spinner);
		}
	}
	
	private void findAllWidget() {
		mBodyPicture = (ImageView)findViewById(R.id.body_picture);
		mNameText = (TextView)findViewById(R.id.name_text);
		mSexText = (TextView)findViewById(R.id.sex_text);
		mAgeText = (TextView)findViewById(R.id.age_text);
		mIdText = (TextView)findViewById(R.id.id_text);
		mPageSwitchPic = (ImageView)findViewById(R.id.page_switch_pic); 
		mErrDisText = (TextView)findViewById(R.id.err_dis_text);
		mExpouseStatusPic = (ImageView)findViewById(R.id.expouse_status_pic);
		mAlignmentStatusPic = (ImageView)findViewById(R.id.alignment_status_pic);
		mFpdDirection = (ImageView)findViewById(R.id.fpd_direction);
		mThermalPicture = (ImageView)findViewById(R.id.thermal_picture);
		
		mRhaText = (TextView)findViewById(R.id.rha_text);
		mRvaText = (TextView)findViewById(R.id.rva_text);
		mSidText = (TextView)findViewById(R.id.sid_text);
		mGridSid = (TextView)findViewById(R.id.grid_sid);
		mGridRate = (TextView)findViewById(R.id.grid_rate);
		mGridMaterial = (TextView)findViewById(R.id.gridMaterial);
		mThermalText = (TextView)findViewById(R.id.thermal_text);
		mHelpButton = (Button)findViewById(R.id.help_button);
		
		mHelpButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Configuration config = getResources().getConfiguration();
				if(config.orientation == Configuration.ORIENTATION_LANDSCAPE)
					MainActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
				else if(config.orientation == Configuration.ORIENTATION_PORTRAIT)
					MainActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			}
		});
		
	}

	public void setErrDisText(String errStr) {
		if(errStr == null)
			mErrDisText.setText("");
		
		mErrDisText.setText(errStr);
	}
}



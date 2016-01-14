package com.hedymed.drdissys;

import static com.hedymed.db.AppDataStruct.appData;
import static com.hedymed.db.AppDataStruct.appStringData;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
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

import com.hedymed.db.AppDataStruct;
import com.hedymed.db.DBUtil;
import com.hedymed.db.SQLHelper;
import com.hedymed.db.bodyPoseArray;
import com.hedymed.engineer.EngineerActivity;
import com.hedymed.engineer.preferenceInterface;
import com.hedymed.fragment.MyFragmentPagerAdapter;
import com.hedymed.fragment.fragmentViewPager;
import com.hedymed.fragment.mainFragment;
import com.hedymed.fragment.secondFrament;
import com.hedymed.fragment.thirdFragment;
import com.hedymed.pieces.MyPopupWindow;
import com.hedymed.uart.uartUtils;

public class MainActivity extends FragmentActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
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
	
	public mainFragment mMainFra;
	public secondFrament mSecondFra;
	public thirdFragment mThirdFra;
	private static int[] mBodyPicResourceIDArray;
	public MyHandler mUiHandler;
	private uartUtils mUart;
	public static SharedPreferences mPreferences;
	private preferenceInterface.localIPChangelistener mLocalIPChangeListener;
	public SQLHelper mExposeArgSQLHelper;
	public DBUtil mExposeArgDB;
	private SQLHelper mExposeArgHelper;
	private fragmentViewPager mViewPager;
	private MyPopupWindow mPopupWindow;
	
	public MainActivity() {
		mUart = new uartUtils(this);
		mExposeArgHelper = new SQLHelper(this);
		mUiHandler = new MyHandler(this); 
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("mainActivity", "in onCreate");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		mExposeArgDB = DBUtil.getInstance(mExposeArgHelper.getReadableDatabase());
		//start Log Service
		startLogService();
		
		findAllWidget();
		initFragmentViewPager();
		
		mPreferences = getSharedPreferences("com.hedymed.drdissys_preferences", Context.MODE_PRIVATE);
		mPreferences.registerOnSharedPreferenceChangeListener(this);
		mPopupWindow = new MyPopupWindow(this);
		
		//打开串口 8O1, 115200bps
		mUart.uartOpen(0, 115200, 8, 1, parityFlag[1]);
		
		if(mBodyPicResourceIDArray == null)
			mBodyPicResourceIDArray = findBodyPosPicArray();
		
		refreshActivity();
//		new netRecvThread(this, "netRecvThread").start();
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
		mExposeArgHelper.close();
		Log.i("mainActivity", "in onDestroy");
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {  
			if(requestCode == thirdFragment.VIDEO_REQUEST_CODE){
				Uri uri = data.getData();  
			    Intent intent = new Intent(Intent.ACTION_VIEW);
			    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			    intent.setDataAndType(uri, "video/*");
			    startActivity(intent);
			}
		}  
		super.onActivityResult(requestCode, resultCode, data);  
	}
	
	
	private void initFragmentViewPager() {
		mViewPager.setPageMargin(20);
		MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), mViewPager);
		mViewPager.setAdapter(adapter);
		mMainFra = adapter.getMainFragment();
		mSecondFra = adapter.getSecondFragment();
		mThirdFra = adapter.getThirdFragment();
	}
	
	
	public boolean isServiceRunning(String serviceName) {
		ActivityManager am =  (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> infos = am.getRunningServices(Integer.MAX_VALUE);
		for(RunningServiceInfo info:infos) {
			//包名+类名
			String myServiceName  = info.service.getClassName();
			if(myServiceName.equals(serviceName)) {
				return true;
			}
		}
		return false;
	}
	
	private void startLogService() {
		if(!isServiceRunning("com.hedymed.log.log")) {//if log service is not running
			Intent intent = new Intent();
			intent.setAction("com.hedymed.drdissys.LOG_SERVICE");
			startService(intent);
		}
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		switch(key) {
		case "localIP":
			if(mLocalIPChangeListener != null)
				mLocalIPChangeListener.getChangedIP("localIP");
			break;
			
		case "rotation_angle":
			Boolean rotationEnable = readPreferencesBoolean("rotation_enable");
			if(rotationEnable != null && rotationEnable == true) {
				String angle = readPreferencesString("rotation_angle");
				if("0".equals(angle))
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
				else if("90".equals(angle))
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				else if("-90".equals(angle))
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
			}
			break;	
				
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
//		return mDetector.onTouchEvent(event);
		return true;
	}
		
	
	public void setPageDot(int position) {
		if(position == 0)
			mPageSwitchPic.setImageResource(R.drawable.page_1);
		else if(position == 1)
			mPageSwitchPic.setImageResource(R.drawable.page_2);
		else if(position == 2)
			mPageSwitchPic.setImageResource(R.drawable.page_3);
	}
	
//	public void setSwitchFragmentEnable(boolean enableFlag) {
//		mSwitchFragmentEnable = enableFlag;
//	}
	
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
	
	public void sendToUiThread(String[] cmdStr) {
		Message msg = Message.obtain();
		Bundle bundle = new Bundle();
		bundle.putCharSequenceArray(UI_HANDLER_KEY, cmdStr);
		msg.setData(bundle);
		mUiHandler.sendMessage(msg);
	}
	
	static class MyHandler extends Handler {
        WeakReference<MainActivity> mActivity;

        MyHandler(MainActivity activity) {
                mActivity = new WeakReference<MainActivity>(activity);
        }

        @Override
		public void handleMessage(Message msg) {
        	MainActivity theActivity = mActivity.get();
			String[] cmdString = (String[]) msg.getData().getCharSequenceArray(UI_HANDLER_KEY);
			theActivity.MVC_model_handler(cmdString);
			theActivity.MVC_view_handler(cmdString);
			Log.i("Recv CMD", String.format("CMD is %s, ARG is %s", cmdString[0], cmdString[1]));
		}
}

	public void initExposeArg(String APRarg) {
		try {
			Cursor cursor = mExposeArgHelper.getReadableDatabase().rawQuery(
					"SELECT ID, KV, MA, MS, MAS FROM exposeARG WHERE ID = ?", new String[]{APRarg});
			while(cursor.moveToNext()) {
				int colCount = cursor.getColumnCount();
				for(int j = 0; j < colCount; j++) {
					String str = cursor.getColumnName(j);
					if("ID".equals(str))
						continue;
					appData.put(str, cursor.getInt(j));
				}
			}
		} catch(SQLException e) {
			Log.i("query database", e.getMessage());
		}
		mMainFra.refreshFragment();
	}
	
	
	private void refreshActivity() {
		MVC_view_handler(new String[]{"NAM", null});
		MVC_view_handler(new String[]{"SEX", null});
		MVC_view_handler(new String[]{"ID", null});
		MVC_view_handler(new String[]{"APR", null});
		MVC_view_handler(new String[]{"AGE", null});
		MVC_view_handler(new String[]{"ES", null});
		MVC_view_handler(new String[]{"AI", null});
		MVC_view_handler(new String[]{"FPD", null});
		MVC_view_handler(new String[]{"RHA", null});
		MVC_view_handler(new String[]{"RVA", null});
		MVC_view_handler(new String[]{"SID", null});
		MVC_view_handler(new String[]{"GRID", null});
		MVC_view_handler(new String[]{"TC", null});
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
			case "NAM":
				try {
					if(appStringData.get("NAM") != null) {
						byte[] array = hexString2ByteArray(appStringData.get("NAM"));
						String str = new String(array, "UTF-16BE");
						mNameText.setText(str);
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				break;
				
			case "SEX":
				try {
					if(appStringData.get("SEX") != null) {
						byte[] array = hexString2ByteArray(appStringData.get("SEX"));
						String str = new String(array, "UTF-16BE");
						mSexText.setText(str);
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				break;
				
			case "ID":
				if(appStringData.get("ID") != null)
					mIdText.setText(appStringData.get("ID"));
				break;
				
//			case NEW_errDisText:
//				//查询数据库
//				break;
				
			case "APR":
				String APRarg = appStringData.get("APR");
				int picIndex = bodyPoseArray.getBodyPicNum(APRarg);
				if(picIndex >= 0) {
					mBodyPicture.setImageResource(mBodyPicResourceIDArray[picIndex]);
					initExposeArg(APRarg);
				}
				break;
				
			case "AGE":
				mAgeText.setText(String.valueOf(appData.get("AGE")));
				break;
				
			case "ES":
				int mapValue = appData.get("ES");
				if(mapValue == AppDataStruct.expose_status.DISABLE_EXPOSE.ordinal())
					mExpouseStatusPic.setImageResource(R.drawable.expose_disable_pic);
				else if(mapValue == AppDataStruct.expose_status.ENABLE_EXPOSE.ordinal()) {
					mExpouseStatusPic.setImageResource(R.drawable.expose_enable_pic);
					mPopupWindow.dismiss();
				}
				else if(mapValue == AppDataStruct.expose_status.PREP_PHASE.ordinal())
					mExpouseStatusPic.setImageResource(R.drawable.expose_prep_pic);
				else if(mapValue == AppDataStruct.expose_status.EXPOSE_PHASE.ordinal()) {
					mExpouseStatusPic.setImageResource(R.drawable.exposing_pic);
					mPopupWindow.show();
				}
				
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
						mFpdDirection.setImageResource(R.drawable.fpd_ws_land_yellow);//WS 横插
					else if(AppDataStruct.FPD_WS_V_INSERT == (mapValue & 0x0F))
						mFpdDirection.setImageResource(R.drawable.fpd_ws_port_yellow);//WS 竖插
					else if(AppDataStruct.FPD_FT_H_INSERT == (mapValue & 0x0F))
						mFpdDirection.setImageResource(R.drawable.fpd_ft_land_yellow);//FT 横插
					else if(AppDataStruct.FPD_FT_V_INSERT == (mapValue & 0x0F))
						mFpdDirection.setImageResource(R.drawable.fpd_ft_port_yellow);//FT 竖插
				} else {
					if(AppDataStruct.FPD_WS_H_INSERT == (mapValue & 0x0F))
						mFpdDirection.setImageResource(R.drawable.fpd_ws_land);//WS 横插
					else if(AppDataStruct.FPD_WS_V_INSERT == (mapValue & 0x0F))
						mFpdDirection.setImageResource(R.drawable.fpd_ws_port);//WS 竖插
					else if(AppDataStruct.FPD_FT_H_INSERT == (mapValue & 0x0F))
						mFpdDirection.setImageResource(R.drawable.fpd_ft_land);//FT 横插
					else if(AppDataStruct.FPD_FT_V_INSERT == (mapValue & 0x0F))
						mFpdDirection.setImageResource(R.drawable.fpd_ft_port);//FT 竖插
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
				mThirdFra.MVC_control_handler(cmdAndArg);
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
		mViewPager = (fragmentViewPager)findViewById(R.id.fragment_viewpager);
		
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
				Intent intent = new Intent(MainActivity.this, EngineerActivity.class);
				startActivity(intent);
			}
		});
		
	}

	public void setErrDisText(String errStr) {
		if(errStr == null)
			mErrDisText.setText("");
		
		mErrDisText.setText(errStr);
	}

	public void setLocalIPChangeListener(preferenceInterface.localIPChangelistener mLocalIPChangeListener) {
		this.mLocalIPChangeListener = mLocalIPChangeListener;
	}
	
	public static String readPreferencesString(String key) {
		if(mPreferences != null) {
			if(mPreferences.contains(key))
				return mPreferences.getString(key, null);
		}
		return null;
	}  
	
	public static Boolean readPreferencesBoolean(String key) {
		if(mPreferences != null) {
			if(mPreferences.contains(key))
				return mPreferences.getBoolean(key, false);
		}
		return null;
	} 
}



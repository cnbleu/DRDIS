package com.hedymed.fragment;

import com.hedymed.drdissys.MainActivity;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;


public class custom_event_lineLayout extends LinearLayout {
	int mSlipPointID;
	float mTouchDownX;
	boolean mScrolling, mSwitchFlag;
	int mTouchSlop;
	MainActivity mAttachActivity;
	
	public custom_event_lineLayout(Context context) {
		super(context);
	}

	public custom_event_lineLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		if(context instanceof MainActivity)
			mAttachActivity = (MainActivity)context;
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
	}

	public boolean dispatchTouchEvent(MotionEvent ev) {
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch(event.getActionMasked()){
			case MotionEvent.ACTION_DOWN:
				mTouchDownX = event.getX();
				mScrolling = false;
				mSwitchFlag = false;
				break;
				
			case MotionEvent.ACTION_MOVE:
				if(Math.abs(mTouchDownX - event.getX()) >= mTouchSlop){
					mScrolling = true;
				}
				else{
					mScrolling = false;
				}
				break;
				
			case MotionEvent.ACTION_UP:
				//如果ACTION_MOVE判断是点击就会进入ACTION_UP，是滚动则不会进入
				//返回false以便子控件接收ACTION_UP事件
				mScrolling = false; 
				break;
		}
		
		return mScrolling;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch(event.getActionMasked()){
			case MotionEvent.ACTION_DOWN:
				break;
				
			case MotionEvent.ACTION_MOVE:
//				if(!mSwitchFlag) {
//					//如果没有点击在子控件上
//					if(!mScrolling && Math.abs(mTouchDownX - event.getX()) < mTouchSlop)
//						break;
//					
//					mSwitchFlag = true;
////					if(mTouchDownX > event.getX())
////						mAttachActivity.FragmentSlip(false);
////					else
////						mAttachActivity.FragmentSlip(true);
//				}
				break;
				
			case MotionEvent.ACTION_UP:
				break;
				
			default:
				break;
		}
		return true;
	}

}

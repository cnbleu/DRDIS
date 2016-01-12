package com.hedymed.fragment;

import com.hedymed.drdissys.MainActivity;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;


public class custom_event_lineLayout extends LinearLayout {
	int mSlipPointID;
	float mTouchDownX, mTouchDownY;
	int mViewStartX, mViewStartY;
	boolean mScrolling;
	int mTouchSlop;
	float mLastTouchpointX;
	MainActivity mAttachActivity;
	boolean isMoving = false;
	boolean mScaleAnimationFlag;
	
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
				mLastTouchpointX = mTouchDownX;
				mTouchDownY = event.getY();
				mViewStartX = this.getLeft();
				mViewStartY = this.getTop();
				mScrolling = false;
				mScaleAnimationFlag = false;
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
				//���ACTION_MOVE�ж��ǵ���ͻ����ACTION_UP���ǹ����򲻻����
				//����false�Ա��ӿؼ�����ACTION_UP�¼�
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
				//���û�е�����ӿؼ���
				if(!mScrolling && Math.abs(mTouchDownX - event.getX()) < mTouchSlop)
					break;
				if(!isMoving) {
					float moveToX;
					moveToX = mViewStartX + event.getX() - mLastTouchpointX;
					FragmentSlip(mViewStartX, moveToX, mViewStartY, mViewStartY);
					Log.i("Move test", String.format("startX is %d, endX is %f, startY is %d, endY is %d", mViewStartX, moveToX, mViewStartY, mViewStartY));
					mViewStartX = (int)moveToX;
					mLastTouchpointX = event.getX();
				}
				break;
				
			case MotionEvent.ACTION_UP:
				int width = getWidth();
				float distance = Math.abs(event.getX() - mTouchDownX);
				
				if(distance / width > 0.1) {// �϶��㹻���룬�л�Fragment
					mAttachActivity.FragmentSlip(false);
					
				} else {// û���϶��㹻���룬�ع�
					TranslateAnimation moveAnimation = new TranslateAnimation(mViewStartX, 0, 0, 0);
					
					float pivotX = getWidth() / 2;
					float pivotY = getHeight() / 2;
					ScaleAnimation moveScaleAnimation = new ScaleAnimation(0.9f, 1.0f, 0.9f, 1.0f, pivotX, pivotY);
					
					AnimationSet moveAnimationSet = new AnimationSet(true);
					moveAnimationSet.addAnimation(moveAnimation);
					moveAnimationSet.addAnimation(moveScaleAnimation);
					moveAnimationSet.setDuration(300);
					moveAnimationSet.setFillAfter(true);
					moveAnimationSet.setAnimationListener(new myAnimationListerer());
					this.startAnimation(moveAnimationSet);
					Log.i("move test", "up");
				}
				break;
				
			default:
				break;
		}
		return true;
	}
	
	
	public void FragmentSlip(float fromXDelta, float toXDelta, float fromYDelta, float toYDelta) {
		AnimationSet moveAnimationSet = new AnimationSet(true);
		
		TranslateAnimation moveAnimation = new TranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta);
		moveAnimationSet.addAnimation(moveAnimation);
		
		float pivotX = getWidth() / 2;
		float pivotY = getHeight() / 2;
		ScaleAnimation moveScaleAnimation;
		if(!mScaleAnimationFlag) {
			mScaleAnimationFlag = true;
			moveScaleAnimation = new ScaleAnimation(1.0f, 0.9f, 1.0f, 0.9f, pivotX, pivotY);
		} else
			moveScaleAnimation = new ScaleAnimation(0.9f, 0.9f, 0.9f, 0.9f, pivotX, pivotY);
			
		moveAnimationSet.addAnimation(moveScaleAnimation);
		
		moveAnimationSet.setDuration(10);
		moveAnimationSet.setFillAfter(true);
		moveAnimationSet.setAnimationListener(new myAnimationListerer());
		this.startAnimation(moveAnimationSet);
		
	}
	
	class myAnimationListerer implements Animation.AnimationListener{
		@Override
		public void onAnimationStart(Animation animation) {
			isMoving = true;
			Log.i("move test", "begin moving");
		}
		
		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onAnimationEnd(Animation animation) {
			isMoving = false;
			mAttachActivity.switchPageDot("secondFragment");	
			Log.i("move test", "end moving");
		}
	}
	
	
}

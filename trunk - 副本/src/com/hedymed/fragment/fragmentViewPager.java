package com.hedymed.fragment;

import java.util.HashMap;
import java.util.LinkedHashMap;

import com.nineoldandroids.view.ViewHelper;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


public class fragmentViewPager extends ViewPager {
	private static final float SCALE_MAX = 0.5f;
	private static final String TAG = "fragmentViewPager";
	private HashMap<Integer, Fragment> mChildrenViews = new LinkedHashMap<Integer, Fragment>();
	private Fragment mLeft;
	private Fragment mRight;

	public fragmentViewPager(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels)
	{
		//滑动特别小的距离时，我们认为没有动，可有可无的判断
		float effectOffset = isSmall(positionOffset) ? 0 : positionOffset;
		
		//获取左边的View
		mLeft = findViewFromObject(position);
		//获取右边的View
		mRight = findViewFromObject(position + 1);
		
		// 添加切换动画效果
//		animateStack(mLeft, mRight, effectOffset, positionOffsetPixels);
		super.onPageScrolled(position, positionOffset, positionOffsetPixels);
	}

	public void setObjectForPosition(Fragment fragment, int position)
	{
		mChildrenViews.put(position, fragment);
	}

	/**
	 * 通过过位置获得对应的View
	 * 
	 * @param position
	 * @return
	 */
	public Fragment findViewFromObject(int position)
	{
		return mChildrenViews.get(position);
	}

	private boolean isSmall(float positionOffset)
	{
		return Math.abs(positionOffset) < 0.0001;
	}

//	protected void animateStack(Fragment left, Fragment right, float effectOffset,
//			int positionOffsetPixels)
//	{
//		if (right != null)
//		{
//			/**
//			 * 缩小比例 如果手指从右到左的滑动（切换到后一个）：0.0~1.0，即从一半到最大
//			 * 如果手指从左到右的滑动（切换到前一个）：1.0~0，即从最大到一半
//			 */
//			float mScale = (1 - SCALE_MAX) * effectOffset + SCALE_MAX;
//			/**
//			 * x偏移量： 如果手指从右到左的滑动（切换到后一个）：0-720 如果手指从左到右的滑动（切换到前一个）：720-0
//			 */
//			int mTrans = -getWidth() - getPageMargin() + positionOffsetPixels;
//			ViewHelper.setScaleX(right, mScale);
//			ViewHelper.setScaleY(right, mScale);
//			ViewHelper.setTranslationX(right, mTrans);
//			Log.i(TAG, "trans is " + mTrans);
//		} else 
//			Log.i(TAG, "right is null ");
//		if (left != null)
//		{
//			left.bringToFront();
//		}
//	}
}

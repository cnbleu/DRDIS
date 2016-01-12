package com.hedymed.fragment;

import java.util.HashMap;
import java.util.LinkedHashMap;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;

import com.hedymed.drdissys.MainActivity;
import com.nineoldandroids.view.ViewHelper;


public class fragmentViewPager extends ViewPager {
	private static final float SCALE_MAX = 0.5f;
	private HashMap<Integer, Fragment> mChildrenViews = new LinkedHashMap<Integer, Fragment>();
	private Fragment mLeft;
	private Fragment mRight;
	private Context mContext;

	public fragmentViewPager(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		mContext = context;
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels)
	{
		//滑动特别小的距离时，我们认为没有动，可有可无的判断
		float effectOffset = isSmall(positionOffset) ? 0 : positionOffset;
		
		mLeft = findViewFromObject(position);
		mRight = findViewFromObject(position + 1);
		animateStack(mLeft, mRight, effectOffset, positionOffsetPixels);
		if(mContext instanceof MainActivity)
			((MainActivity)mContext).setPageDot(getCurrentItem());
		
		super.onPageScrolled(position, positionOffset, positionOffsetPixels);
	}

	public void setObjectForPosition(Fragment fragment, int position)
	{
		mChildrenViews.put(position, fragment);
	}

	public int getObjectNum() {
		return mChildrenViews.size();
	}
	
	public Fragment findViewFromObject(int position)
	{
		return mChildrenViews.get(position);
	}

	private boolean isSmall(float positionOffset)
	{
		return Math.abs(positionOffset) < 0.0001;
	}

	protected void animateStack(Fragment left, Fragment right, float effectOffset,
			int positionOffsetPixels)
	{
		if (right != null)
		{
			/**
			 * 缩小比例 如果手指从右到左的滑动（切换到后一个）：0.0~1.0，即从一半到最大
			 * 如果手指从左到右的滑动（切换到前一个）：1.0~0，即从最大到一半
			 */
			float mScale = (1 - SCALE_MAX) * effectOffset + SCALE_MAX;
			/**
			 * x偏移量： 如果手指从右到左的滑动（切换到后一个）：0-720 如果手指从左到右的滑动（切换到前一个）：720-0
			 */
			int mTrans = -getWidth() - getPageMargin() + positionOffsetPixels;
			ViewHelper.setScaleX(right.getView(), mScale);
			ViewHelper.setScaleY(right.getView(), mScale);
			ViewHelper.setTranslationX(right.getView(), mTrans);
		} 
		
		if (left != null)
		{
			left.getView().bringToFront();
		}
	}
}

package com.hedymed.fragment;

import static com.hedymed.db.AppDataStruct.appData;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import com.hedymed.drdissys.MainActivity;
import com.nineoldandroids.view.ViewHelper;

public class fragmentViewPager extends ViewPager {
	private static final float SCALE_MAX = 0.5f;
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
		appData.put("CURR_FRAGMENT", getCurrentItem());
		//滑动特别小的距离时，我们认为没有动，可有可无的判断
		float effectOffset = isSmall(positionOffset) ? 0 : positionOffset;
		
		mLeft = findViewFromObject(position);
		mRight = findViewFromObject(position + 1);
//		animateStack(mLeft, mRight, effectOffset, positionOffsetPixels);
		if(mContext instanceof MainActivity)
			((MainActivity)mContext).setPageDot(getCurrentItem());
		
		super.onPageScrolled(position, positionOffset, positionOffsetPixels);
	}
	 
	public Fragment findViewFromObject(int position)
	{
		MyFragmentPagerAdapter fragmentAdapter;
		PagerAdapter adapter = getAdapter();
		
		if(adapter instanceof MyFragmentPagerAdapter) {
			fragmentAdapter = (MyFragmentPagerAdapter)adapter;
			int count = adapter.getCount();
			return fragmentAdapter.getItem(position >= count ? count - 1 : position);
		}
		return null;
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

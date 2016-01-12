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
		//�����ر�С�ľ���ʱ��������Ϊû�ж������п��޵��ж�
		float effectOffset = isSmall(positionOffset) ? 0 : positionOffset;
		
		//��ȡ��ߵ�View
		mLeft = findViewFromObject(position);
		//��ȡ�ұߵ�View
		mRight = findViewFromObject(position + 1);
		
		// ����л�����Ч��
//		animateStack(mLeft, mRight, effectOffset, positionOffsetPixels);
		super.onPageScrolled(position, positionOffset, positionOffsetPixels);
	}

	public void setObjectForPosition(Fragment fragment, int position)
	{
		mChildrenViews.put(position, fragment);
	}

	/**
	 * ͨ����λ�û�ö�Ӧ��View
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
//			 * ��С���� �����ָ���ҵ���Ļ������л�����һ������0.0~1.0������һ�뵽���
//			 * �����ָ�����ҵĻ������л���ǰһ������1.0~0���������һ��
//			 */
//			float mScale = (1 - SCALE_MAX) * effectOffset + SCALE_MAX;
//			/**
//			 * xƫ������ �����ָ���ҵ���Ļ������л�����һ������0-720 �����ָ�����ҵĻ������л���ǰһ������720-0
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

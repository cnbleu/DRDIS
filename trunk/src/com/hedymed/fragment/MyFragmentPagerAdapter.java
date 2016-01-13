package com.hedymed.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
	private fragmentViewPager mViewPager;
	private mainFragment mMainFragment;
	private secondFrament mSecondFragment;
	private thirdFragment mThirdFragment;
	
	public MyFragmentPagerAdapter(FragmentManager fm, fragmentViewPager viewPager) {
		super(fm);
		mViewPager = viewPager;
		
		mMainFragment = new mainFragment();	
		mSecondFragment = new secondFrament();
		mThirdFragment = new thirdFragment();
		
		mViewPager.setObjectForPosition(mMainFragment, 0);
		mViewPager.setObjectForPosition(mSecondFragment, 1);
		mViewPager.setObjectForPosition(mThirdFragment, 2);
    }
	
	public mainFragment getMainFragment() {
		return mMainFragment;
	}
	
	public secondFrament getSecondFragment() {
		return mSecondFragment;
	}
	
	public thirdFragment getThirdFragment() {
		return mThirdFragment;
	}
	
    @Override
    public int getCount() {
        return mViewPager.getObjectNum();
    }

    @Override
    public Fragment getItem(int arg0) {
    	return mViewPager.findViewFromObject(arg0);
    }
}

package com.hedymed.fragment;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
	private static MyFragmentPagerAdapter mAdapter;
	private mainFragment mMainFragment;
	private secondFrament mSecondFragment;
	private thirdFragment mThirdFragment;
	private List<Fragment> mChildrenViews = new ArrayList<Fragment>();
	
	public MyFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
		mMainFragment = new mainFragment();	
		mSecondFragment = new secondFrament();
		mThirdFragment = new thirdFragment();
		
		mChildrenViews.add(mMainFragment);
		mChildrenViews.add(mSecondFragment);
		mChildrenViews.add(mThirdFragment);
    }
	
	public static MyFragmentPagerAdapter getInstance(FragmentManager fm) {
		if(mAdapter == null)
			mAdapter = new MyFragmentPagerAdapter(fm);
		
		return mAdapter;
	}
	
    @Override
    public int getCount() {
        return mChildrenViews.size();
    }

    @Override
    public Fragment getItem(int position) {
    	return mChildrenViews.get(position);
    }
}

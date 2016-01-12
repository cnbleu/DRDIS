package com.hedymed.fragment;

import com.hedymed.drdissys.MainActivity;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
	MainActivity mActivity;
	Fragment[] fragmentArray;
	fragmentViewPager mViewPager;
	
	public MyFragmentPagerAdapter(Context context, FragmentManager fm, fragmentViewPager viewPager) {
		super(fm);
		mViewPager = viewPager;
		if(context instanceof MainActivity)
			mActivity = (MainActivity)context;
		
		fragmentArray = new Fragment[]{
			new mainFragment(),
			new secondFrament()
		};
    }

    @Override
    public int getCount() {
        return fragmentArray.length;
    }

    @Override
    public Fragment getItem(int arg0) {
    	return fragmentArray[arg0];
    }
    
//    @Override
//    public boolean isViewFromObject(View view, Object object) {
//    	return view == object;
//    }
//    
//   @Override
//   public Object instantiateItem(ViewGroup container, int position) {
//	   Fragment fragment;
//		if(position == 0) {
//			fragment = new mainFragment();
//		} else if (position == 1) {
//			fragment = new secondFrament();
//		}
//		container.addView(fragment);
//		mViewPager.setObjectForPosition(fragment, position);
//		return fragment;
//   }
}

package com.luxs.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

public class BusinessFraAdapter extends FragmentPagerAdapter {

	 private List<Fragment> shenagwFragments;
	 private String[] CONTENT; 
     public BusinessFraAdapter(FragmentManager fm, List<Fragment> shenagwFragments,String[] CONTENT) {
         super(fm);
         this.shenagwFragments=shenagwFragments;
         this.CONTENT=CONTENT;
     }

     @Override
     public Fragment getItem(int position) {
     	Log.i("BusinessFraAdapter", "======="+position+"======");
         return shenagwFragments.get(position);
     }

     @Override
     public CharSequence getPageTitle(int position) {
         return CONTENT[position%CONTENT.length].toUpperCase();
     }

     @Override
     public int getCount() {
       return shenagwFragments.size();
     }
 

}

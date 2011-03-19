package com.zrd.zr;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
	private Context mContext;
	public final static int mBlockWidth = 36;
	public final static int mBlockPadding = 2;
	private ArrayList<Integer> mThumbIds = new ArrayList<Integer>();
	
	public ImageAdapter (Context c) {
		this.mContext = c;
		Integer iColors[] = {
			R.drawable.block_gray, R.drawable.block_brown, R.drawable.block_green, R.drawable.block_purple,
			R.drawable.pineapple, R.drawable.pineapple
		};
		int iTotal = VanicrossActivity.getNumColumns() * VanicrossActivity.getNumRows();
		Random random = new Random((int) Math.random() * 100);
		for (int i = 0; i < iTotal; i++) {
			int m = Math.abs(random.nextInt());
			int n = iColors.length;
			int l = m % n;
			mThumbIds.add(iColors[l]);
		}
	}

	public ArrayList<Integer> getThumbIds() {
		return mThumbIds;
	}
	
	public void setThumbIdAt(int idx, Integer id) {
		mThumbIds.set(idx, id);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mThumbIds.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ImageView imageView; 
        if (convertView == null) { 
            imageView = new ImageView(mContext); 
            imageView.setLayoutParams(
            	new GridView.LayoutParams(mBlockWidth, mBlockWidth)
            );//set width & height for ImageView
            imageView.setAdjustViewBounds(false); 
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setPadding(mBlockPadding, mBlockPadding, mBlockPadding, mBlockPadding);
        } else { 
            imageView = (ImageView) convertView; 
        } 
 
        imageView.setImageResource(mThumbIds.get(position)); 
 
        return imageView;
	}

}

package com.zrd.zr.game;

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
	public final static int mBlockWidth = VanicrossActivity.convertDip2Pix(36);
	private ArrayList<Integer> mThumbIds = new ArrayList<Integer>();
	private Integer mColors[];
	private int mColorsCurIndex = 0;
	
	public ImageAdapter (Context c) {
		this.mContext = c;
		mColorsCurIndex = getRandomColorIndex();
		changeThumbIds(mColorsCurIndex);
	}

	public ArrayList<Integer> getThumbIds() {
		return mThumbIds;
	}
	
	public int getRandomColorIndex() {
		int idx = 0;
		Random random = new Random((int) (Math.random() * 100));
		int m = Math.abs(random.nextInt());
		int l = changeThumbIds(-1).length;
		idx = m % l;
		return idx;
	}
	
	public int getColorsCurIndex() {
		return mColorsCurIndex;
	}

	public Integer getThumbIdAt(int idx) {
		return mThumbIds.get(idx);
	}
	
	public void setThumbIdAt(int idx, Integer id) {
		mThumbIds.set(idx, id);
	}
	
	public void renewThumbIds() {
		int iTotal = VanicrossActivity.getNumColumns() * VanicrossActivity.getNumRows();
		Random random = new Random((int) (Math.random() * 100));
		mThumbIds.clear();
		for (int i = 0; i < iTotal; i++) {
			int m = Math.abs(random.nextInt());
			int n = mColors.length;
			int l = m % n;
			mThumbIds.add(mColors[l]);
		}
	}
	
	public Integer[][] changeThumbIds(int idx) {
		Integer colors[][] = {
			{
				R.drawable.ball_black, R.drawable.ball_blue, R.drawable.ball_green, R.drawable.ball_purple,
				R.drawable.ball_red, R.drawable.ball_skyblue, R.drawable.ball_yellow,
				R.drawable.pineapple, R.drawable.pineapple
			},
			{
				R.drawable.block_darkgreen, R.drawable.block_lightgreen, R.drawable.block_orange, R.drawable.block_dark,
				R.drawable.block_purple, R.drawable.block_red, R.drawable.block_skyblue, R.drawable.block_yellow,
				R.drawable.pineapple, R.drawable.pineapple
			},
			{
				R.drawable.heart_darksky, R.drawable.heart_gray, R.drawable.heart_green, R.drawable.heart_lightbrown,
				R.drawable.heart_orange, R.drawable.heart_pink, R.drawable.heart_purple, R.drawable.heart_red,
				R.drawable.pineapple, R.drawable.pineapple
			},
			{
				R.drawable.dice_blue, R.drawable.dice_dark, R.drawable.dice_green,
				R.drawable.dice_orange, R.drawable.dice_purple, R.drawable.dice_red,
				R.drawable.pineapple, R.drawable.pineapple
			},
			{
				R.drawable.flower_001, R.drawable.flower_002, R.drawable.flower_003, R.drawable.flower_004,
				R.drawable.flower_005, R.drawable.flower_006, R.drawable.flower_007, R.drawable.flower_008,
				R.drawable.pineapple, R.drawable.pineapple
			}
		};
		if (idx < 0) {
			idx = mColorsCurIndex;
			return colors;
		} else {
			idx = idx % colors.length;
		}
		mColors = colors[idx];
		mColorsCurIndex = idx;
		renewThumbIds();
		return colors;
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
            imageView.setBackgroundResource(R.drawable.pineapple);
            imageView.setAdjustViewBounds(false); 
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setPadding(2, 2, 2, 2);
        } else { 
            imageView = (ImageView) convertView; 
        } 
 
        imageView.setImageResource(mThumbIds.get(position)); 
 
        return imageView;
	}

}

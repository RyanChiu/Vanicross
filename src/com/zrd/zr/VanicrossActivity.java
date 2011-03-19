package com.zrd.zr;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class VanicrossActivity extends Activity {
	static DisplayMetrics mDisplayMetrics = new DisplayMetrics();;
	GridView mGridCross;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        mGridCross = (GridView) findViewById(R.id.gridViewCross);
        mGridCross.setNumColumns(VanicrossActivity.getNumColumns());
        mGridCross.setAdapter(new ImageAdapter(this));
        
        mGridCross.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				// TODO Auto-generated method stub
				ArrayList<Integer> lst = ((ImageAdapter) mGridCross.getAdapter()).getThumbIds();
				if (lst.get(position) != R.drawable.pineapple) return;
				int[] blks = get4Blocks(position);
				
				ArrayList<Integer> vanBlks = getVanBlocks(blks);
				
				/*//for debug
				Toast.makeText(VanicrossActivity.this,
					"left:" + blks[0] + ", top:" + blks[1] + ", right:" + blks[2] + ", bottom:" + blks[3],
					Toast.LENGTH_SHORT
				).show();
				*/
				
				if (vanBlks.size() == 0) {//no blocks should be vanished
					Toast.makeText(VanicrossActivity.this,
						"oooops...\nNo same color blocks at the corss.",
						Toast.LENGTH_LONG
					).show();
					return;
				}
				for (int i = 0; i < vanBlks.size(); i++) {
					ImageView iv;
					iv = (ImageView) mGridCross.getChildAt(vanBlks.get(i));
					iv.setImageResource(R.drawable.pineapple);
					((ImageAdapter) mGridCross.getAdapter()).setThumbIdAt(vanBlks.get(i), R.drawable.pineapple);
				}
				return;
			}
        });
    }
    
    public int[] get4Blocks(int position) {
    	int[] xy;
    	int[] blks = new int[4];//0 left, 1 top, 2 right, 3 bottom
    	ArrayList<Integer> lst = ((ImageAdapter) mGridCross.getAdapter()).getThumbIds();
    	int i, j;
    	xy = getXY(position);
		for (i = 0; (j = getPosition(xy)) >= 0; i++) {//check left
			if (lst.get(j) != R.drawable.pineapple) {
				blks[0] = j;
				break;
			}
			xy[0]--;
		}
		if (j < 0) blks[0] = -1;
		xy = getXY(position);
		for (i = 0; (j = getPosition(xy)) >= 0; i++) {//check top
			if (lst.get(j) != R.drawable.pineapple) {
				blks[1] = j;
				break;
			}
			xy[1]--;
		}
		if (j < 0) blks[1] = -1;
		xy = getXY(position);
		for (i = 0; (j = getPosition(xy)) >= 0; i++) {//check right
			if (lst.get(j) != R.drawable.pineapple) {
				blks[2] = j;
				break;
			}
			xy[0]++;
		}
		if (j < 0) blks[2] = -1;
		xy = getXY(position);
		for (i = 0; (j = getPosition(xy)) >= 0; i++) {//check bottom
			if (lst.get(j) != R.drawable.pineapple) {
				blks[3] = j;
				break;
			}
			xy[1]++;
		}
		if (j < 0) blks[3] = -1;
		return blks;
    }
    
    public ArrayList<Integer> getVanBlocks(int[] blks) {
    	ArrayList<Integer> lst = ((ImageAdapter) mGridCross.getAdapter()).getThumbIds();
    	ArrayList<Integer> lstBlks = new ArrayList<Integer>();
		for (int i = 0; i < blks.length; i++) {
			if (blks[i] > 0) lstBlks.add(blks[i]);
		}
		ArrayList<Integer> lstColors = new ArrayList<Integer>();
		for (int i = 0; i < lstBlks.size(); i++) {
			lstColors.add(lst.get(lstBlks.get(i)));
		}
		ArrayList<Integer> lstRepeat = new ArrayList<Integer>();
		for (int i = 0; i < lstColors.size(); i++) {
			int n = 0;
			for (int j = 0; j < lstColors.size(); j++) {
				if (lstColors.get(i) == lstColors.get(j)) {
					n++;
				}
			}
			lstRepeat.add(n);
		}
		ArrayList<Integer> vanBlks = new ArrayList<Integer>();
		for (int i = 0; i < lstRepeat.size(); i++) {
			if (lstRepeat.get(i) > 1) vanBlks.add(lstBlks.get(i));
		}
		return vanBlks;
    }
    
    public int[] getXY(int position) {
    	int[] xy = {-1, -1};
    	if (position < 0) return xy;
    	int x = (position % getNumColumns());
		int y = (int) Math.floor(position / getNumColumns());
		xy = new int[] {x, y};
		return xy;
    }
    
    public int getPosition(int[] xy) {
    	if (xy.length != 2) return -1;
    	if (xy[0] < 0 || xy[0] >= getNumColumns() || xy[1] < 0 || xy[1] >= getNumRows()) {
    		return -1;
    	}
    	int x = xy[0], y = xy[1];
    	int position = x + y * getNumColumns();
    	return position;
    }
    
    public static int getNumColumns() {
    	if (mDisplayMetrics.widthPixels == 0) return 12;
    	return (int) Math.floor(mDisplayMetrics.widthPixels / (ImageAdapter.mBlockWidth + ImageAdapter.mBlockPadding * 2));
    }
    
    public static int getNumRows() {
    	if (mDisplayMetrics.heightPixels == 0) return 9;
    	return (int) Math.floor(mDisplayMetrics.heightPixels / (ImageAdapter.mBlockWidth + ImageAdapter.mBlockPadding * 2));
    }
    
    public static int convertDip2Pix(int dp) {
    	if (mDisplayMetrics.widthPixels == 0) return 0;
    	return (int)(dp / mDisplayMetrics.density);
    }
}
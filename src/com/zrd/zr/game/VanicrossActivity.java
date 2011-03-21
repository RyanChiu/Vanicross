package com.zrd.zr.game;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class VanicrossActivity extends Activity {
	static DisplayMetrics mDisplayMetrics = new DisplayMetrics();;
	GridView mGridCross;
	AlertDialog mMenuDialog;
	AlertDialog mTipsDialog;
	AlertDialog mQuitDialog;
	private SharedPreferences mPreferences = null;
	private final String mCfgDoNotShowTips = "DoNotShowTips";
	int mScore = 0;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mPreferences = getPreferences(VanicrossActivity.MODE_PRIVATE);
        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        mGridCross = (GridView) findViewById(R.id.gridViewCross);
        mGridCross.setNumColumns(VanicrossActivity.getNumColumns());
        mGridCross.setAdapter(new ImageAdapter(this));
        CharSequence[] items = {
			"Refresh...",
			"Next theme...",
			"Random theme...",
			"Show tips..."
		};
        mMenuDialog = new AlertDialog.Builder(this).
			setSingleChoiceItems(
				items, 0,
				new DialogInterface.OnClickListener() {
	
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						ImageAdapter ia = ((ImageAdapter) mGridCross.getAdapter());
						switch (which) {
						case 0:
							ia.renewThumbIds();
							mGridCross.setAdapter(ia);
							mScore = 0;
							setTitle("Score: " + mScore);
							break;
						case 1:
							ia.changeThumbIds(ia.getColorsCurIndex() + 1);
							mGridCross.setAdapter(ia);
							mScore = 0;
							setTitle("Score: " + mScore);
							break;
						case 2:
							ia.changeThumbIds(ia.getRandomColorIndex());
							mGridCross.setAdapter(ia);
							mScore = 0;
							setTitle("Score: " + mScore);
							break;
						case 3:
							mTipsDialog.show();
							break;
						}
						dialog.dismiss();
					}
					
				}
			).create();
        mMenuDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
		        	new DialogInterface.OnClickListener() {
		
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			
			}
		);
        mMenuDialog.setTitle("Menu");
        mTipsDialog = new AlertDialog.Builder(this)
        	.setMultiChoiceItems(new CharSequence[] {"I knew long click to menu."}, null,
        		new DialogInterface.OnMultiChoiceClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int which,
							boolean isChecked) {
						// TODO Auto-generated method stub
						switch (which) {
						case 0:
							SharedPreferences.Editor editor = mPreferences.edit();
							if (isChecked) {
								editor.putBoolean(mCfgDoNotShowTips, true);
								editor.commit();
							} else {
								editor.putBoolean(mCfgDoNotShowTips, false);
								editor.commit();
							}
							break;
						default:
							break;
						}
					}
        		
        		}
        	)
        	.create();
        mTipsDialog.setTitle("Tips");
        //mTipsDialog.setMessage("You could \"LONG CLICK\" the screen to get menu.");
        mTipsDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
        	new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
        	
        	}
        );
        
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
						Toast.LENGTH_SHORT
					).show();
					return;
				}
				/*
				 * vanish the blocks
				 */
				for (int i = 0; i < vanBlks.size(); i++) {
					ImageView iv;
					iv = (ImageView) mGridCross.getChildAt(vanBlks.get(i));
					iv.setImageResource(R.drawable.pineapple);
					((ImageAdapter) mGridCross.getAdapter()).setThumbIdAt(vanBlks.get(i), R.drawable.pineapple);
				}
				/*
				 * show scores
				 */
				String title = "Score: " + mScore;
				VanicrossActivity.this.setTitle(title);
				return;
			}
        });
        
        mGridCross.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				mMenuDialog.show();
				return true;
			}
        	
        });
        
        if (!mPreferences.getBoolean(mCfgDoNotShowTips, false)) {
        	mTipsDialog.show();
        }
    }
    
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			mQuitDialog = new AlertDialog.Builder(VanicrossActivity.this).create();
			mQuitDialog.setTitle("Are you sure to quit?");
			mQuitDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						android.os.Process.killProcess(android.os.Process.myPid());
					}
				
				}
			);
			mQuitDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
					}
				
				}
			);
			mQuitDialog.show();
			return true;
		}
		return super.onKeyDown(keyCode, event);
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
    
    private ArrayList<Integer> getVanBlocks(int[] blks) {
    	ArrayList<Integer> lst = ((ImageAdapter) mGridCross.getAdapter()).getThumbIds();
    	ArrayList<Integer> lstBlks = new ArrayList<Integer>();
		for (int i = 0; i < blks.length; i++) {
			if (blks[i] >= 0) lstBlks.add(blks[i]);
		}
		ArrayList<Integer> lstRepeat = new ArrayList<Integer>();
		for (int i = 0; i < lstBlks.size(); i++) {
			int n = 0;
			for (int j = 0; j < lstBlks.size(); j++) {
				if (lst.get(lstBlks.get(i)) == lst.get(lstBlks.get(j))) {
					n++;
				}
			}
			lstRepeat.add(n);
		}
		
		int k = 0;
		int score = 0;
		for (int i = 0; i < lstRepeat.size(); i++) {
			k += lstRepeat.get(i);
		}
		if (lstRepeat.size() < 2) score = 0;
		if (lstRepeat.size() == 2) {
			if (k == (1 + 1)) score = 0;
			if (k == (2 + 2)) score = 2;
		}
		if (lstRepeat.size() == 3) {
			if (k == (1 + 1 + 1)) score = 0;
			if (k == (1 + 2 + 2)) score = 2;
			if (k == (3 + 3 + 3)) score = 4;
		}
		if (lstRepeat.size() == 4) {
			if (k == (1 + 1 + 1 + 1)) score = 0;
			if (k == (1 + 1 + 2 + 2)) score = 2;
			if (k == (1 + 3 + 3 + 3)) score = 4;
			if (k == (4 + 4 + 4 + 4)) score = 8;
			if (k == (2 + 2 + 2 + 2)) score = 10;
		}
		mScore += score;
		
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
    	return (int)(dp * mDisplayMetrics.density);
    }
}
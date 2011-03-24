package com.zrd.zr.game;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.codec.binary.Base64;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.text.method.SingleLineTransformationMethod;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class VanicrossActivity extends Activity {
	static DisplayMetrics mDisplayMetrics = new DisplayMetrics();
	static LinearLayout mLayoutTop;
	TextView mTextScoreLabel;
	TextView mTextScore;
	Button mBtnRefresh;
	GridView mGridCross;
	AlertDialog mMenuDialog;
	AlertDialog mTipsDialog;
	AlertDialog mQuitDialog;
	AlertDialog mScoreNameDialog;
	AlphaAnimation fadeinAnim = new AlphaAnimation(0.1f, 1.0f);
	AlphaAnimation fadeoutAnim = new AlphaAnimation(1.0f, 0.1f);
	private SharedPreferences mPreferences = null;
	private final String mCfgDoNotShowTips = "DoNotShowTips";
	private final String mCfgScoresBulletin = "ScoreBulletin";
	ArrayList<ScoreRecord> mScores = new ArrayList<ScoreRecord>();
	int mScore = 0;
	private final int mTops = 10;
	EditText mEditScoreName = null;
	private boolean mShowMenuAfter = false;
	private AudioManager mAudioManager;
	private SoundPool mSoundPool;
	private HashMap<Integer, Integer> mSoundMap = new HashMap<Integer, Integer>();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mPreferences = getPreferences(Context.MODE_PRIVATE);
        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        mLayoutTop = (LinearLayout) findViewById(R.id.linearLayoutTop);
        mBtnRefresh = (Button) findViewById(R.id.btnRefresh);
        mTextScoreLabel = (TextView) findViewById(R.id.tvScoreLabel);
        mTextScore = (TextView) findViewById(R.id.tvScore);
        mGridCross = (GridView) findViewById(R.id.gridViewCross);
        mGridCross.setNumColumns(VanicrossActivity.getNumColumns());
        mGridCross.setAdapter(new ImageAdapter(this));
        mEditScoreName = new EditText(this);
        mEditScoreName.setTransformationMethod(SingleLineTransformationMethod.getInstance());
        mAudioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
        mSoundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 100);
        mSoundMap.put(R.raw.error, mSoundPool.load(this, R.raw.error, 0));
        mSoundMap.put(R.raw.vanished2, mSoundPool.load(this, R.raw.vanished2, 0));
        mSoundMap.put(R.raw.vanished3, mSoundPool.load(this, R.raw.vanished3, 0));
        mSoundMap.put(R.raw.vanished4, mSoundPool.load(this, R.raw.vanished4, 0));
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        readScores();
        CharSequence[] items = {
			"Refresh...",
			"Next theme...",
			"Random theme...",
			"Score bulletin...",
			"Show tips..."
		};
        mMenuDialog = new AlertDialog.Builder(this).
			setItems(
				items,
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
							mTextScore.setText("" + mScore);
							break;
						case 1:
							ia.changeThumbIds(ia.getColorsCurIndex() + 1);
							mGridCross.setAdapter(ia);
							mScore = 0;
							mTextScore.setText("" + mScore);
							break;
						case 2:
							ia.changeThumbIds(ia.getRandomColorIndex());
							mGridCross.setAdapter(ia);
							mScore = 0;
							mTextScore.setText("" + mScore);
							break;
						case 3://score bulletin
							showScoreBulletin();
							break;
						case 4:
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
        	.setMultiChoiceItems(new CharSequence[] {"I knew LONG CLICK could get menu."}, null,
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
        
        mScoreNameDialog = new AlertDialog.Builder(this)
		.setTitle("Congratulations.\nPlease enter your name.")
		.setView(mEditScoreName)
		.setPositiveButton("OK",
			new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog,
						int which) {
					// TODO Auto-generated method stub
					if (mEditScoreName.getText().toString().equals("")) {
						Toast.makeText(VanicrossActivity.this,
							"Please enter a name.",
							Toast.LENGTH_SHORT
						).show();
					}
					String name = mEditScoreName.getText().toString();
					boolean isScoreRecorded = recordScore(new ScoreRecord(name, mScore));
					Toast.makeText(VanicrossActivity.this,
						(isScoreRecorded ? "Scored." : "Not scored."),
						Toast.LENGTH_LONG
					).show();
					mShowMenuAfter = true;
					showScoreBulletin();
					mShowMenuAfter = false;
				}
			
			}
		)
		//.setNegativeButton("Cancel", null)
		.create();
        
        mGridCross.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				// TODO Auto-generated method stub
				ArrayList<Integer> lst = ((ImageAdapter) mGridCross.getAdapter()).getThumbIds();
				if (lst.get(position) != R.drawable.pineapple) return;
				int[] blks = get4Blocks(position);
				
				ArrayList<Integer> vanBlks = getVanBlocks(blks, true);
				
				/*//for debug
				Toast.makeText(VanicrossActivity.this,
					"left:" + blks[0] + ", top:" + blks[1] + ", right:" + blks[2] + ", bottom:" + blks[3],
					Toast.LENGTH_SHORT
				).show();
				*/
				
				if (vanBlks.size() == 0) {//no blocks should be vanished
					playSound(R.raw.error);
					Toast.makeText(VanicrossActivity.this,
						"oooops...\nNo same color blocks at the corss.",
						Toast.LENGTH_SHORT
					).show();
					return;
				}
				/*
				 * vanish the blocks
				 */
				int i;
				for (i = 0; i < vanBlks.size(); i++) {
					ImageAdapter ia = (ImageAdapter) mGridCross.getAdapter();
					ImageView iv = (ImageView) mGridCross.getChildAt(vanBlks.get(i));
					iv.setImageResource(R.drawable.pineapple);
					fadeinAnim.setDuration(100);
					iv.startAnimation(fadeinAnim);
					(ia).setThumbIdAt(vanBlks.get(i), R.drawable.pineapple);
				}
				if (i == 2) playSound(R.raw.vanished3);
				if (i == 3) playSound(R.raw.vanished2);
				if (i == 4) playSound(R.raw.vanished4);
				/*
				 * show scores' change
				 */
				mTextScore.setText("" + mScore);
				/*
				 * check if any blocks could be vanished
				 */
				ArrayList<Integer> alVanishableBlocks = getVanishableBlocks();
				if (alVanishableBlocks.size() != 0) {
					/*
					 * show how many blank block could be click to vanish blocks at cross
					 */
					/*
					Toast.makeText(VanicrossActivity.this,
						alVanishableBlocks.size() + " more.",
						Toast.LENGTH_SHORT
					).show();
					*/
				} else {
					Toast.makeText(VanicrossActivity.this,
						"oooops...\nNo more blocks could be vanished.",
						Toast.LENGTH_LONG
					).show();
					if (couldBeInBulletin(mScore)) {
						/*
						 * popup a dalog to let client input his/her name
						 */
						mScoreNameDialog.show();
					}
				}
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
        
        mBtnRefresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ImageAdapter ia = ((ImageAdapter) mGridCross.getAdapter());
				ia.renewThumbIds();
				mGridCross.setAdapter(ia);
				mScore = 0;
				mTextScore.setText("" + mScore);
			}
        	
        });
        
        mTextScoreLabel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showScoreBulletin();
			}
        	
        });
        
        mTextScore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showScoreBulletin();
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

	/*
	 * the return value includes 4 positions of items of ImageAdapter.mThumbIds
	 */
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
    
    private ArrayList<Integer> getVanBlocks(int[] blks, boolean setScore) {
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
		if (setScore) mScore += score;
		
		ArrayList<Integer> vanBlks = new ArrayList<Integer>();
		for (int i = 0; i < lstRepeat.size(); i++) {
			if (lstRepeat.get(i) > 1) vanBlks.add(lstBlks.get(i));
		}
		return vanBlks;
    }
     
    /*
     * the return value holds all blocks' positions that could point to vanish other blocks at the cross
     */
    public ArrayList<Integer> getVanishableBlocks() {
    	ArrayList<Integer> blks = new ArrayList<Integer>();
    	ArrayList<Integer> lst = ((ImageAdapter) mGridCross.getAdapter()).getThumbIds();
    	for (int i = 0; i < lst.size(); i++) {
    		if (lst.get(i) == R.drawable.pineapple) {
    			if (getVanBlocks(get4Blocks(i), false).size() != 0) {
    				blks.add(i);
    			}
    		}
    	}
    	return blks;
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
    	if (mDisplayMetrics.widthPixels == 0) return 9;
    	int num = (int) Math.floor(mDisplayMetrics.widthPixels / (ImageAdapter.mBlockWidth + 1 * 2));
    	return num;
    }
    
    public static int getNumRows() {
    	if (mDisplayMetrics.heightPixels == 0) return 12;
    	int num = (int) Math.floor((mDisplayMetrics.heightPixels - convertDip2Pix(32)) / (ImageAdapter.mBlockWidth + 1 * 2));
    	return num;
    }
    
    public static int convertDip2Pix(int dp) {
    	if (mDisplayMetrics.widthPixels == 0) return 0;
    	return (int)(dp * mDisplayMetrics.density);
    }
    
    public boolean couldBeInBulletin(int score) {
    	int i;
    	for (i = 0; i < mScores.size(); i++) {
    		if (score > mScores.get(i).mScore) break;
    	}
    	if (i == 0) return true;
    	return (i < mTops);
    }
    
    public boolean recordScore(ScoreRecord score) {
    	int i, mTops = 10;
    	for (i = 0; i < mScores.size(); i++) {
    		if (mScores.get(i).mScore < score.mScore) {
    			mScores.add(i, score);
    			break;
    		}
    	}
    	if (i == mScores.size()) {
    		mScores.add(mScores.size(), score);
    	}
    	if (mScores.size() > mTops) {
    		for (i = mTops; i < mScores.size(); i++) {
    			mScores.remove(mTops);
    		}
    	}
    	if (mScores.indexOf(score) == -1) {
    		return false;
    	}
        try {
        	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        	ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(mScores);
			String sScores = new String(Base64.encodeBase64(baos.toByteArray()));
			SharedPreferences.Editor editor = mPreferences.edit();
			editor.putString(mCfgScoresBulletin, sScores);
			editor.commit();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
    }
    
    @SuppressWarnings("unchecked")
	public void readScores() {
    	String sScores = "";
		try {
			sScores = mPreferences.getString(mCfgScoresBulletin, "");
			ByteArrayInputStream bais = new ByteArrayInputStream(
				Base64.decodeBase64(sScores.getBytes())
			);
			ObjectInputStream ois = new ObjectInputStream(bais);
			mScores = (ArrayList<ScoreRecord>)ois.readObject();
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /*
     * show Score Bulletin Dialog
     */
    public void showScoreBulletin() {
    	readScores();
		AlertDialog dlg = new AlertDialog.Builder(VanicrossActivity.this)
			.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if (mShowMenuAfter) {
							if (mMenuDialog != null) {
								mMenuDialog.show();
							}
						}
					}
				
				}
			)
			.create();
		dlg.setTitle("Score bulletin");
		String msg = mScores.size() == 0 ? "Get your scores here!" : "";
		for (int i = 0; i < mScores.size(); i++) {
			ScoreRecord sr = mScores.get(i);
			msg += sr.mName + ": \t" + sr.mScore;
			if (i != mScores.size() - 1) {
				msg += "\n";
			}
		}
		dlg.setMessage(msg);
		dlg.show();
    }
    
    /*
     * try to play sound here
     */
    public void playSound(int soundId) {
    	mSoundPool.play(
    		mSoundMap.get(soundId),
    		mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC),
    		mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC),
    		1, 0, 0.6f
    	);
    }
}
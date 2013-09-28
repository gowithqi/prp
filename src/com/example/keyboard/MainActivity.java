package com.example.keyboard;

import java.util.ArrayList;
import java.lang.Thread;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Button;
import android.widget.RelativeLayout.LayoutParams;

public class MainActivity extends Activity {
	private MainActivity me;
	private char[][] defaultMeaning = {{'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p'},
			                           {'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l'},
			                           {' ', 'z', 'x', 'c', 'v', 'b', 'n', 'm', ' '},
			                           {',', ' ', '.', ' '}};
	
	//keys physical change
	private class PhyChange {
		public PhyChange() {
			this(0, 0, 0, 0);
		}
		public PhyChange(int i, int j, int k, int l) {
			sizeX = i;
			sizeY = j;
			posiX = k;
			posiY = l;
		}
		public int sizeX = 0;
		public int sizeY = 0;
		public int posiX = 0;
		public int posiY = 0;
	}
	
	private class Key {
		public Button btn;
		public RelativeLayout.LayoutParams defaultParam;
		public PhyChange phyChange;
		public int meaning;
	}
	
	private class Row {
		public ArrayList<Key> keys;
		public int startXPosition;
	}
	
	private ArrayList<Row> rows;
	private Thread mthread = null;
	private Handler mHandler0 = new Handler () {
		@Override
		public void handleMessage(Message msg) {
			if (msg.getData().getInt("thread") == 0) {
				changePhy();
			}
			super.handleMessage(msg);
		}
	};
	private Handler mHandler1 = new Handler () {
		 @Override
		public void handleMessage(Message msg) {
			if (msg.getData().getInt("thread") == 1) {
				setDefaultParam();
			}
			super.handleMessage(msg);
		}
	};
	
	private void initKeyboard () {
		rows = new ArrayList<Row> (4);
		for (int i = 0; i < 4; ++i) rows.add(new Row());
		rows.get(0).keys = new ArrayList<Key> (10);
		for (int i = 0; i < 10; ++i) {rows.get(0).keys.add(new Key());}
		rows.get(0).startXPosition = 10;
		rows.get(1).keys = new ArrayList<Key> (9);
		for (int i = 0; i < 9; ++i) rows.get(1).keys.add(new Key());
		rows.get(1).startXPosition = 30;
		rows.get(2).keys = new ArrayList<Key> (9);
		for (int i = 0; i < 9; ++i) rows.get(2).keys.add(new Key());
		rows.get(2).startXPosition = 10;
		rows.get(3).keys = new ArrayList<Key> (4);
		for (int i = 0; i < 4; ++i) rows.get(3).keys.add(new Key());
		rows.get(3).startXPosition = 30;
		
		for (int i = 0; i < rows.size(); ++i) {
			Log.d("AA", "rows size: "+Integer.toString(rows.get(i).keys.size()));
			int keysSize = rows.get(i).keys.size();
			for (int j = 0; j < keysSize; j++) {
				//Log.d("AA", Integer.toString(i)+","+Integer.toString(j));
				Key tmp = new Key(); //rows.get(i).keys.get(j);
				tmp.btn = new Button(this);
				tmp.defaultParam = new RelativeLayout.LayoutParams(40, 45);
				tmp.defaultParam.setMargins(rows.get(i).startXPosition+j*40, 200+50*i,0, 0);
				tmp.btn.setText(Character.toString(defaultMeaning[i][j]));
				tmp.btn.setTextSize(7);
				tmp.btn.setId(i*10+j);
				tmp.btn.setOnClickListener(btnClick);
				tmp.btn.setLayoutParams(tmp.defaultParam);
				tmp.phyChange = new PhyChange();
				rows.get(i).keys.set(j, tmp);
			}
		}
		rows.get(3).keys.get(1).defaultParam.width = 200;
		rows.get(3).keys.get(2).defaultParam.leftMargin = 30+6*40;
		rows.get(3).keys.get(3).defaultParam.leftMargin = 30+7*40;
}
	
	public void changePhy() {
		for (int i = 0; i < rows.size(); ++i) {
			for (int j = 0; j < rows.get(i).keys.size(); ++j) {
				Key key = rows.get(i).keys.get(j);
				RelativeLayout.LayoutParams tmp = new RelativeLayout.LayoutParams(key.defaultParam.width+key.phyChange.sizeX,
																			      key.defaultParam.height+key.phyChange.sizeY);
				tmp.setMargins(key.defaultParam.leftMargin+key.phyChange.posiX,
							   key.defaultParam.topMargin+key.phyChange.posiY, 
							   0, 
							   0);
				key.btn.setLayoutParams(tmp);
			}
		}
	}
	
	public void setDefaultParam() {
		for (int i = 0; i < rows.size(); ++i) {
			for (int j = 0; j < rows.get(i).keys.size(); ++j) {
				Key key = rows.get(i).keys.get(j);
				key.btn.setLayoutParams(key.defaultParam);
			}
		}
	}
	private class cPosiThread extends Thread{		
		public void run() {
			try {
				Log.d("THREAD", "thread start");

				//set position and size
				Message msg0 = new Message();
				msg0.setTarget(mHandler0);
				Bundle b0 = new Bundle();
				b0.putInt("thread", 0);
				msg0.setData(b0);
				msg0.sendToTarget();
				
				sleep(30000);
				Log.d("THREAD", "have slept 3s");
				
				//huifu
				Message msg1 = new Message();
				msg1.setTarget(mHandler1);
				Bundle b1 = new Bundle();
				b1.putInt("thread", 1);
				msg1.setData(b1);
				msg1.sendToTarget();
			}
			catch (InterruptedException e) {
				/*
				Message msg1 = new Message();
				msg1.setTarget(mHandler1);
				Bundle b1 = new Bundle();
				b1.putInt("thread", 1);
				msg1.setData(b1);
				msg1.sendToTarget();
				*/
			}
		}
	};
	
	private void setParam(ArrayList<ArrayList<RelativeLayout.LayoutParams>> paras) {
		for (int i = 0; i < paras.size(); ++i) {
			for (int j = 0; j < paras.get(i).size(); ++j) {
				buttons.get(i).get(j).setLayoutParams(paras.get(i).get(j));
			}
		}
	}
	
	private Button.OnClickListener btnClick = new Button.OnClickListener() {
		public void onClick(View v) {
			Log.d("MM", "click");
			for (int i = 0; i < rows.size(); ++i) {
				for (int j = 0; j < rows.get(i).keys.size(); ++j) {
					if (v.getId() == rows.get(i).keys.get(j).btn.getId()) {
						Button tmp = rows.get(i).keys.get(j).btn;
						Log.d("MM", "Click Button ID: " + Integer.toString(v.getId()));
						calParam(v.getId());
						if (mthread != null) {
							try {
								mthread.interrupt();
								mthread.join();
							} catch (InterruptedException e) {
								//do noting
								Log.d("MM", "can not stop the thread");
							}
						}
						mthread = new cPosiThread();
						mthread.start();
						return;
					}
				}
			}
		}
	};
	
	//TODO
	private void rePosi(int x, int y) {
		
	}
	
	private boolean calParam(int id) {
		for (int i = 0; i < rows.size(); ++i) {
			for (int j = 0; j < rows.get(i).keys.size(); ++j) {
				Key key = rows.get(i).keys.get(j);
				key.phyChange = new PhyChange();
			}
		}
		//rows.get(id/10).keys.get(id%10).phyChange.posiY += -100;
		rows.get(id/10).keys.get(id%10).phyChange.sizeX += 30;
		rows.get(id/10).keys.get(id%10).phyChange.sizeY += 30;
		return true;
		/*
		if (weight.length != 26) return false;
		for (int i = 0; i < rows.size()-1; ++i) {
			for (int j = 0; j < rows.get(i).keys.size(); ++j) {
				if (weight[rows.get(i).keys.get(j).btn.getText().charAt(0)-'a'] != 0) {
					rePosi(i, j);
				}
			}
		}
		return true;
		*/
	}
	
	private ArrayList<ArrayList<Button>> buttons;
	private ArrayList<ArrayList<RelativeLayout.LayoutParams>> params_d;
	private boolean flag = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		RelativeLayout layout = new RelativeLayout(this);
		Log.d("MM", "start");
		initKeyboard();
		for (int i = 0; i < rows.size(); ++i) {
			for (int j = 0; j < rows.get(i).keys.size(); ++j){
				layout.addView(rows.get(i).keys.get(j).btn);
			}
		}
		setContentView(layout);
		me = this;
		//setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}

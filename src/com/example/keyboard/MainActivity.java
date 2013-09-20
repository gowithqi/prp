package com.example.keyboard;

import java.util.ArrayList;
import java.lang.Thread;
import android.os.Bundle;
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
		private MainActivity act;
		public cPosiThread(MainActivity a) {
			this.act = a;
		}
		
		public void run() {
			try {
				Log.d("THREAD", "thread start");
				act.changePhy();
				sleep(30000);
			}
			catch (InterruptedException e) {
				act.setDefaultParam();
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
						calParam(null);
						if (mthread != null) {
							try {
								mthread.interrupt();
								mthread.join();
							} catch (InterruptedException e) {
								//do noting
								Log.d("MM", "can not stop the thread");
							}
						}
						mthread = new cPosiThread(me);
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
	
	private boolean calParam(int[] weight) {
		rows.get(0).keys.get(4).phyChange.posiY += -100;
		rows.get(0).keys.get(4).phyChange.sizeX += 30;
		rows.get(0).keys.get(4).phyChange.sizeY += 30;
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
	/*
	private ArrayList<ArrayList<RelativeLayout.LayoutParams>> calParam(int x, int y) {
		ArrayList<ArrayList<RelativeLayout.LayoutParams>> res = new ArrayList<ArrayList<RelativeLayout.LayoutParams>>(4);
		for (int i = 0; i < 4; ++i) {
			res.add(new ArrayList<RelativeLayout.LayoutParams>(10));
			for (int j = 0; j < 10; ++j) {
				res.get(i).add((RelativeLayout.LayoutParams)buttons.get(i).get(j).getLayoutParams());
			}
		}
		RelativeLayout.LayoutParams para = new RelativeLayout.LayoutParams(res.get(x).get(y).width+10, 
																		   res.get(x).get(y).height+10);
		para.setMargins(res.get(x).get(y).leftMargin, res.get(x).get(y).topMargin+100, 0 , 0);
		res.get(x).set(y, para);
		return res;
	}
	*/
	
	/*
	private void setParam(ArrayList<ArrayList<RelativeLayout.LayoutParams>> paras) {
		for (int i = 0; i < paras.size(); ++i) {
			for (int j = 0; j < paras.get(i).size(); ++j) {
				buttons.get(i).get(j).setLayoutParams(paras.get(i).get(j));
			}
		}
	}
	*/
	/*
	private Button.OnClickListener btnClick = new Button.OnClickListener() {
		public void onClick(View v) {
			Log.d("MM", "click");
			if (flag) setParam(calParam(3,5));
			else setParam(params_d);
			flag = !flag;
			//Log.d("MM", "start sleep");
			//SystemClock.sleep(1000);
			//Log.d("MM", "end sleep");
			//setParam(params_d);
			
			RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(50, 50);
			buttons.get(0).get(0).setLayoutParams(param);
			buttons.get(0).get(0).setText("1");
			buttons.get(0).get(0).setTextSize(10);
			
		}
	};
	
	
	private void initKeyboard () {
		params_d = new ArrayList<ArrayList<RelativeLayout.LayoutParams>>(4);
		buttons = new ArrayList<ArrayList<Button>> (4);
		for (int i = 0; i < 4; ++i) {
			params_d.add(new ArrayList<RelativeLayout.LayoutParams>(10));
			buttons.add(new ArrayList<Button>(10));
			for (int j = 0; j < 10; ++j) {
				Button tmpb = new Button(this);
				tmpb.setId(i*10+j);
				RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(40, 45);
				param.setMargins(10+j*45,200+50*i, 0, 0);
				Log.d("AA", Integer.toString(param.height));
				tmpb.setLayoutParams(param);
				tmpb.setText(Integer.toString(j));
				tmpb.setTextSize(7);
				tmpb.setOnClickListener(btnClick);
				params_d.get(i).add(param);
				buttons.get(i).add(tmpb);
			}
		}
	}
	*/
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

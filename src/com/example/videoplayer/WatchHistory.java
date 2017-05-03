package com.example.videoplayer;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ActionBar;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class WatchHistory extends Activity {
	DBHelperActivity dbHelper;
	private ListView lvWatchHistory;
	
	private ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String,Object>>();
	private HashMap<String, Object> item;
	private SimpleAdapter sdapter;
	private ImageView imgView;
	private Bitmap bitmap;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_watch_history);
		System.out.println("44444");
		ActionBar actionbar = getActionBar();
		actionbar.setDisplayShowHomeEnabled(false);
		//imgView = (ImageView)findViewById(R.id.imageView1);
		this.lvWatchHistory = (ListView)this.findViewById(R.id.activity_watch_history_lvHistory);
		dbHelper = new DBHelperActivity(this, "MyVideoDB", null, 1);
		diaplayWatchHisory();
	}

	public boolean diaplayWatchHisory(){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		Cursor c = db.rawQuery("SELECT * FROM video WHERE videotime !='"
						+ "未播放" +"'", null);
		if(0 == c.getCount())
		{
			Toast.makeText(this, "没有播放记录", Toast.LENGTH_LONG).show();
			return false;
		}
		while(c.moveToNext()){
			item = new HashMap<String, Object>();
			for(int i=0; i<c.getColumnCount(); i++){
				item.put("name", c.getString(0));
				System.out.println("viewAllRecords()name"+c.getString(0));
				item.put("path",c.getString(1));
				System.out.println("viewAllRecords()path"+c.getString(1));
				item.put("duration", c.getString(2));
				System.out.println("viewAllRecords()duration"+c.getString(2));
				item.put("time", c.getString(3));
	        }
			items.add(item);
		}
		
		sdapter = new SimpleAdapter(this, items, 
	    		R.layout.listitem_history, 
	    		new String[]{"name", "duration", "time"},
	    		new int[]{R.id.listitem_history_name, R.id.videoTimeView, R.id.playTimeView
	    		});
		lvWatchHistory.setAdapter(sdapter);
		return true;
	}
	
	
}

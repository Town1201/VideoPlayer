package com.example.videoplayer;

import java.util.TimeZone;

import android.app.Activity;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.Time;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;


public class PlayVideoActivity extends Activity {
	private static final OnClickListener OnClickListener = null;
	DBHelperActivity dbHelper;
	VideoView videoView;
	MediaController controller;
	Bundle bundle;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
						
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.activity_play_video);
		videoView = new VideoView(this);		
		setContentView(videoView);
		bundle = this.getIntent().getExtras();
		String path = bundle.getString("mypath");
		System.out.println(path);
		videoView.setVideoURI(Uri.parse(path));
		videoView.setMediaController(new MediaController(this));
		videoView.start();
		
		dbHelper = new DBHelperActivity(this, "MyVideoDB", null, 1); 
		updateWatchCount(path);

	}
	
	  @Override  
      public void onBackPressed() {  
          super.onBackPressed();  
          //System.out.println("按下了back键   onBackPressed()");
          Intent intent = new Intent(PlayVideoActivity.this, MainActivity.class);
          startActivity(intent);
	  }
	
	public void updateWatchCount(String path){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		System.out.println("path&&&&&& "+path);
		
		Cursor c = db.rawQuery("SELECT * FROM video WHERE videopath = '"
				+ path + "'", null);
				
		if(c != null)
		{
			System.out.println("c is not null");
		}
		System.out.println("c.getColumnCount()"+c.getColumnCount());
		if(c.moveToFirst()){
				TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");TimeZone.setDefault(tz);
				Time time = new Time("GMT+8");
				time.setToNow();
				String date;
				int day = time.monthDay;
				System.out.println(day);
				int hour = time.hour;
				if (hour>=16)
				{
					hour = hour+8-24;
					day++;
				}else{
					hour+=8;
				}
				int min = time.minute;
				System.out.println("213123"+min);
				if(hour<10)
				{
					date = "上次播放：" +String.valueOf(day)+"日0"+String.valueOf(hour)+":"+String.valueOf(min);
				}else 
				{
					if(min<10)
					{
						System.out.println("21313sssssssssssssss23"+min);
						date = "上次播放：" +String.valueOf(day)+"日"+String.valueOf(hour)+":"+String.valueOf(min);
					}
					else
						date = "上次播放：" +String.valueOf(day)+"日"+String.valueOf(hour)+":"+String.valueOf(min);
				}
				
				db.execSQL("UPDATE video SET videotime = '" + date
						+ "'WHERE videopath = '"
						+ path +"'");
		}
	}
	

	
}

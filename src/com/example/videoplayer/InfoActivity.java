package com.example.videoplayer;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;


public class InfoActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setTitle("关于软件");
	}
	
}

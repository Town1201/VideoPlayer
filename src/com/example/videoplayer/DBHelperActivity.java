package com.example.videoplayer;

import android.app.Activity;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelperActivity extends SQLiteOpenHelper {

	

	public DBHelperActivity(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
		db.execSQL("CREATE TABLE IF NOT EXISTS video("
				+ "videoname VARCHAR UNIQUE, "
				+ "videopath VARVHAR, "
				+ "videoduration VARCHAR, "
				+ "videotime VARCHAR,"
				+ "videoimage BLOB)");
		//System.out.println("onCreate");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		System.out.println("onUpgrade");
	}

	
}

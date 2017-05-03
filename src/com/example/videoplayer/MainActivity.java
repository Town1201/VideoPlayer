package com.example.videoplayer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.Toast;

public class MainActivity extends Activity implements OnItemClickListener{
	DBHelperActivity dbHelper;
	private ListView lvDisplayVideo; 
	private static final int IDPlayVideoActivity = 1;
	private int pos = -1;
	Thread thread;
	ImageView imageView;
	ProgressBar progressBar;
	int j=0;
	 private Dialog mDialog;
	private ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String,Object>>();
	private HashMap<String, Object> item;
	private HashMap<String, Object> item1;
	private SimpleAdapter sdapter;
	String nameToRename = null;
	final static String newNameString = "";	
	boolean isExit=false;
	Handler mHandler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		ActionBar actionbar = getActionBar();
		getActionBar().setTitle("本地列表");
		actionbar.setDisplayHomeAsUpEnabled(false);
		actionbar.setDisplayShowHomeEnabled(false);
		this.lvDisplayVideo = (ListView)this.findViewById(R.id.activity_main_lvDidplayVideo);
		this.lvDisplayVideo.setOnCreateContextMenuListener(this);/*lvDisplayVideo上下文菜单监听*/
		this.lvDisplayVideo.setOnItemClickListener(this);	
		registerForContextMenu(this.lvDisplayVideo);
		dbHelper = new DBHelperActivity(this, "MyVideoDB", null, 1);
		new ImageLoader().execute();
		Loading();
		mHandler = new Handler() {
		@Override
			public void handleMessage(Message msg) {
		    	super.handleMessage(msg);
		        isExit = false;
		 	}
		};
	}
	
	 @Override
	    public boolean onKeyDown(int keyCode, KeyEvent event) {
	        if (keyCode == KeyEvent.KEYCODE_BACK) {
	            exit();
	            return false;
	        }
	        return super.onKeyDown(keyCode, event);
	    }
	 
	 private void exit() {
	        if (!isExit) {
	            isExit = true;
	            Toast.makeText(getApplicationContext(), "再按一次退出程序",
	                    Toast.LENGTH_SHORT).show();
	            // 利用handler延迟发送更改状态信息
	            mHandler.sendEmptyMessageDelayed(0, 2000);
	        } else {
	            finish();
	            System.exit(0);
	        }
	    }
	
	public void Loading()
    {
        mDialog = new AlertDialog.Builder(MainActivity.this).create();
        System.out.println("1111");
        System.out.println("1111");
        mDialog.show();
        j=1;
        mDialog.setContentView(R.layout.loading_process_dialog_color);
        System.out.println("1111");
    }
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		if (j==1) {
			mDialog.dismiss();
		}
		
	}

	/*创建上下文菜单，*/
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.lv_display_video_contextmenu, menu);
		/*.inflate的作用是将xml定义的而一个布局找出来
		第一个参数是布局，第二个参数是菜单。*/
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
		
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		pos = (int) menuInfo.id;/*获得点击的item的位置*/
		System.out.println("pos= "+pos);
		
		switch(item.getItemId()){
		case R.id.lv_display_contextmenu_delete:
			final CheckBox cb = new CheckBox(this);
			cb.setText("同时删除本地视频文件");
			System.out.println("lv_display_contextmenu_delete");
			
			new AlertDialog.Builder(this).setTitle("是否删除").setView(cb).setPositiveButton("是", new OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(cb.isChecked()){
						deleteEmulateVideo(pos);/*delete the video record from the device*/
					}
					deleteVideoRecord(pos);/*delete the record from user database*/
				}
				
			}).setNegativeButton("否", null).show();
			
			break;
		case R.id.lv_display_contextmenu_rename:	
			renameVideo(pos);
			break;
		}
		
		return super.onContextItemSelected(item);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		Intent intent;
		switch(id)
		{
		case R.id.menu_main_scan:
			scanVideo();
			Loading();
			new ImageLoader().execute();
			break;
		case R.id.menu_main_history:
			System.out.println("menu_main_history");
			
			intent = new Intent(MainActivity.this, WatchHistory.class);	
			startActivity(intent);
			break;
		case R.id.menu_main_search:
			System.out.println("menu_main_search");
			searchToPlay();
			break;
		case R.id.menu_main_more:
			intent = new Intent(MainActivity.this, InfoActivity.class);	
			startActivity(intent);
			break;
		
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void renameVideo(int pos){
		
		nameToRename = items.get(pos).get("name").toString();
		if(nameToRename == null)
			return;
		System.out.println("nameToRename "+nameToRename);
		
		final EditText et = new EditText(this);;
		final ContentValues newName = new ContentValues();
			
		new AlertDialog.Builder(this).setTitle("请输入修改的名称：").setView(et).setPositiveButton("确定", new OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				try{
					Cursor c = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, 	null, null, null, null);
					newName.put(MediaStore.Video.Media.TITLE, et.getText().toString());
					
					while(c.moveToNext()){
						
						getContentResolver().update(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, newName, MediaStore.Video.Media.TITLE +"='"+ nameToRename+"'", null);
						getContentResolver().delete(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,  MediaStore.Video.Media.TITLE +"='"+ nameToRename+"'", null);																				
					}
			
				}catch(Exception e){
					System.out.print("try rename in my phone");
					e.printStackTrace();
				}
				
				SQLiteDatabase db = dbHelper.getReadableDatabase();
				Cursor cur = db.rawQuery("SELECT * FROM video WHERE videoname = '"
								+ nameToRename + "'"
								, null);
				if(cur.moveToFirst())
				{
					db.execSQL("UPDATE video SET videoname = '" + et.getText().toString()
							+ "'WHERE videoname = '"
							+ nameToRename +"'");
							
				}
				Loading();
				new ImageLoader().execute();
			}
			
		}).setNegativeButton("取消", null).show();/*don't forget ".show()"*/
		
	}
	
	/*删除device本地文件（彻底删除）*/
	public void deleteEmulateVideo(int pos){

		String nameToDel = items.get(pos).get("name").toString();
		System.out.println("nameToDel "+nameToDel);
		System.out.println("Uri.parse(nameToDel) "+Uri.parse(nameToDel));
		
		try{
			Cursor c = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, 	null, null, null, null);
			while(c.moveToNext()){
								
				System.out.println("哈喽0 "+c.getString(c.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)));
				System.out.println("哈喽1 "+nameToDel);
				
				getContentResolver().delete(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,  MediaStore.Video.Media.TITLE +"='"+ nameToDel+"'", null);
			
			}
			
		}catch(Exception e){
			System.out.print("try delete in my phone");
			e.printStackTrace();
		}
	}
	
	/*删除表video中的记录*/
	public void deleteVideoRecord(int pos){
		
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		Cursor c = db.rawQuery("SELECT * FROM video WHERE videoname = '"
				+ items.get(pos).get("name") + "'"/*be attention: here is name,not videoname (because when you add redord to adapter, you named it "name" not "videoname")*/
				, null);
		
		if(c.moveToFirst())
		{
			db.execSQL("DELETE FROM video WHERE videoname = '"
					+ items.get(pos).get("name") +"'");
		}
		items.remove(pos);
		sdapter.notifyDataSetChanged();
	}
	
	public void searchToPlay(){

		final EditText et = new EditText(this);
		new AlertDialog.Builder(this).setTitle("请输入搜索路径").setView(et).setPositiveButton("确定", new OnClickListener(){

			@Override					
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if(et.getText().toString().trim().length() == 0){
					
					return;
				}
				
				String input = et.getText().toString();
				if(input != null){
					Intent i = new Intent(MainActivity.this, PlayVideoActivity.class);	
					i.putExtra("mypath", input);
					startActivityForResult(i, IDPlayVideoActivity);
				}
			}
			
		}).setNegativeButton("取消", null).show();
	}
	
	public void scanVideo(){ 		
		Cursor c = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, 	null, null, null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);
		if(c == null)
		{
			System.out.println("MediaStore.Video.Media.EXTERNAL_CONTENT_URI is null");
			Toast.makeText(this, "没有本地视频", Toast.LENGTH_LONG).show();
			return;
		}		

		while (c.moveToNext()) {
			item = new HashMap<String, Object>();
			String title = c.getString(
					c.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)); 
			System.out.println("AAAAAA "+c.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
			String displayName = c.getString(
					c.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)); 
			String path = c.getString(
				c.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)); 	
			long duration = c.getLong(
					c.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)); 
					
			SimpleDateFormat sf=new SimpleDateFormat( "mm:ss"); 
			String dur = "时长："+ sf.format(duration);

			SQLiteDatabase db = dbHelper.getReadableDatabase();
			db.execSQL("INSERT OR IGNORE INTO video(videoname, videopath, videoduration, videotime) VALUES('"+title+"','" +path+"','" +dur+"','" +"未播放"+"')");			
					
	}			
	
}
	
	public boolean viewAllRecords(){
		
		items.removeAll(items);/*清空上一次items的内容，防止重复输出多次*/
		ImageView img;
		Bitmap bitmap;
		byte[] in = null;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		final Cursor c = db.rawQuery("SELECT * FROM video", null);
		img = (ImageView)findViewById(R.id.imageView1);
		if(0 == c.getCount())
		{
			Toast.makeText(this, "视频列表为空", Toast.LENGTH_LONG).show();
			return false;
		}
		while(c.moveToNext()){
			item = new HashMap<String, Object>();
			for(int i=0; i<c.getColumnCount(); i++){/*读出一行的数据*/
				item.put("name", c.getString(0));
				System.out.println("viewAllRecords()name"+c.getString(0));
				item.put("path",c.getString(1));
				System.out.println("viewAllRecords()path"+c.getString(1));
				item.put("duration", c.getString(2));
				System.out.println("viewAllRecords()duration"+c.getString(2));
			
				item.put("time", c.getString(3));
	        }
			
			
			
			items.add(item);
			lvDisplayVideo.setAdapter(sdapter);
		}
		
		
		sdapter = new SimpleAdapter(this, items, 
	    		R.layout.listitem_simple, 
	    		new String[]{"name", "duration", "time"},
	    		new int[]{R.id.listitem_simple_name, R.id.listitem_simple_path, R.id.listitem_simple_time
	    		});
	
		lvDisplayVideo.setAdapter(sdapter);
		return true;
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent;
		Bundle bundle = new Bundle();
				
		bundle.putString("mypath", items.get(position).get("path").toString());
		intent = new Intent(MainActivity.this, PlayVideoActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
		
	}
	
	//异步加载缩略图
	class ImageLoader extends AsyncTask<String, Bitmap, Bitmap> {
		@Override
		protected void onPreExecute() {
			items.removeAll(items);
			
		}
		@Override
		protected Bitmap doInBackground(String... params) {
			String path;
			Bitmap bitmap = null;
			MediaMetadataRetriever mmr = new MediaMetadataRetriever();
			File file;
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			final Cursor c = db.rawQuery("SELECT * FROM video", null);
	
			while(c.moveToNext()){
				item = new HashMap<String, Object>();
				for(int i=0; i<c.getColumnCount(); i++){
					path = c.getString(1);
					file = new File(path);
					mmr.setDataSource(file.getAbsolutePath());
					bitmap = mmr.getFrameAtTime();
					item.put("name", c.getString(0));
					System.out.println("viewAllRecords()name"+c.getString(0));
					item.put("path",c.getString(1));
					System.out.println("viewAllRecords()path"+c.getString(1));
					item.put("duration", c.getString(2));
					System.out.println("viewAllRecords()duration"+c.getString(2));
					
					item.put("time", c.getString(3));
					item.put("image", bitmap);					
		        }
				items.add(item);
				System.out.println("+++++++++++++++++++++1++");
				
				System.out.println("+++++++++++++++++++++++");
			}
			
			return bitmap;
		}
	
		@Override
		protected void onProgressUpdate(Bitmap... values) {
			imageView=(ImageView)findViewById(R.id.imageView1);		
			imageView.setImageBitmap(values[0]);
		}
		@Override
		protected void onPostExecute(Bitmap result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		imageSet();
		
		}
	}
	
	public void imageSet()
	{
		sdapter = new SimpleAdapter(this, items, 
	    		R.layout.listitem_simple, 
	    		new String[]{"name", "duration", "time","image"},
	    		new int[]{R.id.listitem_simple_name, R.id.listitem_simple_path, R.id.listitem_simple_time,R.id.imageView1
	    		});
		sdapter.setViewBinder(new ViewBinder() {
	       	 
            @Override
            public boolean setViewValue(View view, Object data,
                    String textRepresentation) {
            	if(view instanceof ImageView && data instanceof Bitmap){  
                    ImageView i = (ImageView)view;  
                    i.setImageBitmap((Bitmap) data);  
                    return true;  
                }
				return false;  
            } 
		});
		
		lvDisplayVideo.setAdapter(sdapter);
		Toast toast=Toast.makeText(getApplicationContext(), "加载成功", Toast.LENGTH_SHORT);
		toast.show();
		j=2;
		mDialog.dismiss();
	}

	
}

package com.sunteorum.pinktoru;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sunteorum.pinktoru.adapter.GameListAdapter;
import com.sunteorum.pinktoru.entity.GameEntity;
import com.sunteorum.pinktoru.helper.ImageLoader;
import com.sunteorum.pinktoru.helper.LoadImageThread;
import com.sunteorum.pinktoru.util.Common;
import com.sunteorum.pinktoru.util.HttpUtils;
import com.sunteorum.pinktoru.view.FlippingImageView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class GameListActivity extends BaseActivity implements OnScrollListener {
	private ProgressDialog progd;
	private ListView lstView = null;
	private BaseAdapter adapter = null;
	private ArrayList<GameEntity> gelist;
	private ImageLoader imgLoader;
	
	Handler mHandler = new Handler();
	boolean hasnext = true;
	int curpage = 1; 
	int lastItemIndex, count;
	View moreView;
	
	PinkToru app;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.common_list);
		
		app = (PinkToru) this.getApplication();
		//String action = this.getIntent().getAction();
		
		if (this.getIntent().hasExtra("game_list"))
			gelist = this.getIntent().getParcelableArrayListExtra("game_list");
		if (gelist != null) count = gelist.size();
		
		lstView = (ListView) findViewById(android.R.id.list);
		
		imgLoader = new ImageLoader(this, app.getAppCacheDir());
		
		//moreView = getLayoutInflater().inflate(R.layout.pull_to_refresh_foot, null);
		moreView = View.inflate(this, R.layout.pull_to_refresh_foot, null);
		
		lstView.setCacheColorHint(Color.TRANSPARENT);
		lstView.addFooterView(moreView);
		lstView.setOnScrollListener(this);
		lstView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> l, View v, int position, long id) {
				showInfo(gelist.get(position));
			}
			
		});
		
		moreView.bringToFront();
		moreView.setVisibility(View.GONE);
		
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				refresh(1, null);
			}
			
		}, 200);
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, "刷新");
		menu.add(0, 2, 0, "返回");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			if (hasnext) {
				adapter.notifyDataSetInvalidated();
				return false;
			}
			
			hasnext = true;
			curpage = 1;
			lstView.addFooterView(moreView);
			moreView.setVisibility(View.GONE);
			gelist.clear();
			adapter = null;
			refresh(1, null);
			break;
		case 2:
			finish();
			break;
		
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	protected void refresh(final int page, final Handler.Callback callback) {
		if (!HttpUtils.getConnectState(this)) {
			//网络未连接
		}

	 	final Timer atimer = new Timer();
	 	final TimerTask totask = new TimerTask() {

			@Override
			public void run() {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						lstView.setAdapter(adapter);
						if (atimer != null) {
							atimer.cancel();
						}
						progd = null;
					}
					
				});
				
			}
			
		};
		
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (progd != null && progd.isShowing()) {
					progd.setCancelable(true);
					
				}
				
			}
			
		}, 10000);
		
		if (progd != null && progd.isShowing()) progd.dismiss();
		progd = new ProgressDialog(this);
		progd.show();
		progd.setContentView(R.layout.common_loading);
		FlippingImageView mFivIcon = (FlippingImageView) progd.findViewById(R.id.fiv_loading_icon);
		
		progd.setCancelable(false);
		progd.setMessage("正在刷新列表，请稍候...");
		
		if (mFivIcon != null) mFivIcon.startAnimation();
		
		app.putAsyncTask(new AsyncTask<Void, String, Boolean>() {
			
			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
			}

			@Override
			protected Boolean doInBackground(Void... arg0) {
				boolean success = false;
				String purl = "";
				try {
					String r = HttpUtils.getConnectResult(purl);
					JSONArray jsa = new JSONArray(r);
					ArrayList<GameEntity> ges = new ArrayList<GameEntity>();
					for (int i = 0; i < jsa.length(); i++) {
						JSONObject jso = jsa.optJSONObject(i);
						GameEntity pe = new GameEntity(jso.toString());
						/*pe.setPrizeId(jso.optInt("id"));
						pe.setPrizeName(jso.optString("name"));
						pe.setPrizeIconUrl(jso.optString("img"));
						pe.setPrizeRemainNum(jso.optInt("qty"));
						pe.setPrizePrice(jso.optInt("price"));
						pe.setPrizeLvJson(jso.get("game").toString());
						if (jso.has("detail")) pe.setPrizeDesc(jso.optString("detail"));
						if (jso.has("mode_icon")) pe.setPrizeGMUrl(jso.optString("mode_icon"));*/
						
						ges.add(pe);
					}
					
					if (page == 1) {
						gelist = ges;
						
					} else {
						gelist.addAll(ges);
						
					}
					
					if (adapter == null) adapter = new GameListAdapter(GameListActivity.this, gelist, imgLoader);
					//else adapter.notifyDataSetChanged();
					
					count = gelist.size();
					success = true;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					hasnext = false;
				} finally {
					if (progd != null && progd.isShowing()) progd.dismiss();
					atimer.schedule(totask, 500);
					Message msg = mHandler.obtainMessage();
					
					if (callback != null) callback.handleMessage(msg);
				}
				
				return success;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				if (!result) Common.showToast(GameListActivity.this, "刷新列表出错");
				if (gelist.size() == 0) {
					((TextView) findViewById(android.R.id.empty)).setText(R.string.no_item_in_list);
				} else {
					((TextView) findViewById(android.R.id.empty)).setText(null);
				}
			}

			@Override
			protected void onProgressUpdate(String... values) {
				// TODO Auto-generated method stub
				super.onProgressUpdate(values);
			}

			@Override
			protected void onCancelled() {
				// TODO Auto-generated method stub
				super.onCancelled();
			}
			
		});
		
	}
	
	
	
	protected void showInfo(GameEntity ge) {
		//LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
		//View layout = inflater.inflate(R.layout.fragment_game_detail, null);
		View layout = View.inflate(this, R.layout.fragment_game_detail, null);
		
		ImageButton imgClose = (ImageButton) layout.findViewById(R.id.imgClose);
		ImageView imgAd = (ImageView) layout.findViewById(R.id.imageView_ad);
		final ImageView icoAd = (ImageView) layout.findViewById(R.id.imageIcon_ad);
		Button btnAd = (Button) layout.findViewById(R.id.button_ad);
		final TextView txtAd = (TextView) layout.findViewById(R.id.textView_ad);
		TextView textView = (TextView) layout.findViewById(R.id.txtView_desc);
		btnAd.setText(R.string.new_game);
		
		textView.setMovementMethod(android.text.method.ScrollingMovementMethod.getInstance()); 
		
		imgAd.setScaleType(ImageView.ScaleType.FIT_XY);
		
		if (ge != null) {
			txtAd.setText(ge.getGameName());
			textView.setText(ge.getGameDesc());
			new LoadImageThread(app, ge.getGameIconUrl(), icoAd, R.drawable.umlc_h0, null).start();
		}
		
		final PopupWindow mPop = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		mPop.setAnimationStyle(android.R.style.Animation_Translucent);
		mPop.setBackgroundDrawable(getResources().getDrawable(R.drawable.backgroud_1));
	 	mPop.setOutsideTouchable(true);
	 	mPop.setFocusable(true);
	 	mPop.showAtLocation(lstView, android.view.Gravity.CENTER, 0, 0);
	 	mPop.update();
	 	
	 	OnClickListener clickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				switch (v.getId()) {
				case R.id.imgClose:
					mHandler.post(new Runnable() {
						
						@Override
						public void run() {
							mPop.dismiss();
						}
						
					});
					break;
				case R.id.button_ad:
					/*Intent i = new Intent();
					Bundle b = new Bundle();
	        		b.putParcelable("start_game", pe);
	        		i.putExtras(b);
	        		i.setClass(PrizeList.this, HomeActivity.class);
	        		i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
	        		startActivity(i);*/
					
					Intent itn = new Intent();
					itn.setClass(GameListActivity.this, LoginActivity.class);
					
					startActivity(itn);
					overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
					
					break;
				}
				
				
				
			}
	 	};
	 	
	 	btnAd.setOnClickListener(clickListener);
		imgClose.setOnClickListener(clickListener);
		
		
	}

	protected void doDelay() {
		curpage++;
		
		refresh(curpage, new Handler.Callback() {
			
			@Override
			public boolean handleMessage(Message msg) {
				adapter.notifyDataSetChanged();
				moreView.setVisibility(View.GONE);
				if (!hasnext) lstView.removeFooterView(moreView);
				
				return false;
			}
		});
		
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		lastItemIndex = firstVisibleItem + visibleItemCount - 1;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		//System.out.println("lastItemIndex: " + lastItemIndex + " count: " + count);
		if(lastItemIndex == count  && scrollState == OnScrollListener.SCROLL_STATE_IDLE && hasnext) {
			moreView.setVisibility(View.VISIBLE);
			//System.out.println("onScrollStateChanged");
			postCheckForLongIdle();
			
		}
		
		state = scrollState;
	}
	
	public boolean mHasPerformedLongIdle = false; 
	private CheckForLongIdle mPendingCheckForLongIdle;
	private int state;
	
	class CheckForLongIdle implements Runnable {
	    
	    @Override
		public void run() {
	        if (lstView.hasWindowFocus() && state == 0 && mHasPerformedLongIdle) {
				
	        	mHasPerformedLongIdle = false;
				
	        	doDelay();
	        }
	    }
	    
	}
	
	private void postCheckForLongIdle() {
	    mHasPerformedLongIdle = true;
	    if (mPendingCheckForLongIdle == null) {
	        mPendingCheckForLongIdle = new CheckForLongIdle();
	    }
	    
	    mHandler.postDelayed(mPendingCheckForLongIdle, 300);
	}

}

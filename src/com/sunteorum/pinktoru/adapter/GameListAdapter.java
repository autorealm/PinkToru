package com.sunteorum.pinktoru.adapter;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import com.sunteorum.pinktoru.R;
import com.sunteorum.pinktoru.entity.GameEntity;
import com.sunteorum.pinktoru.helper.ImageLoader;
import com.sunteorum.pinktoru.inc.ColorGenerator;
import com.sunteorum.pinktoru.inc.TextDrawable;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GameListAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<GameEntity> gameList;
	private ImageLoader imageLoader;
	private TextDrawable.IBuilder drawableBuilder;
	

	public GameListAdapter(Context c, ArrayList<GameEntity> list) {
		this.context = c;
		this.gameList = list;
		
		drawableBuilder = TextDrawable.builder()
				.beginConfig()
					.withBorder(2)
					
				.endConfig()
				.roundRect(8);
		
		imageLoader = new ImageLoader(c, null);
		
	}

	public GameListAdapter(Context c, ArrayList<GameEntity> list, ImageLoader loader) {
		this.context = c;
		this.gameList = list;
		this.imageLoader = loader;
		
		drawableBuilder = TextDrawable.builder()
				.beginConfig()
					.withBorder(4)
					
				.endConfig()
				.roundRect(10);
		
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return gameList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return gameList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.sample_game_grid_item, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		final String title = gameList.get(position).getGameName();
		holder.txtTitle.setText(title);
		holder.txtSub1.setText("");
		holder.txtSub2.setText("奖励点数：" + gameList.get(position).getGameReward());
		holder.txtSub3.setText("");
		
		try {
			JSONArray jsa = new JSONArray(gameList.get(position).getGameLevel());
			holder.txtSub3.setText("关卡数：" + jsa.length());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		String url = gameList.get(position).getGameIconUrl();
		if (TextUtils.isEmpty(url)) url = gameList.get(position).getGameImageUrl();
		if (!TextUtils.isEmpty(url)) {
			TextDrawable drawable = drawableBuilder.build(String.valueOf(title.charAt(0)),
					ColorGenerator.MATERIAL.getColor(title));
			holder.imgIcon.setImageDrawable(drawable);
			
			imageLoader.download(url, new ImageLoader.onLoadedListener() {
				
				@Override
				public void onLoaded(String url, Bitmap bitmap) {
					if (bitmap == null) return;
					
					holder.imgIcon.setImageBitmap(bitmap);
					holder.imgIcon.postInvalidate();
					
				}
			});
		}
		
		return convertView;
	}
	
	private class ViewHolder {
		
		protected ImageView imgIcon;
		protected TextView txtTitle;
		protected TextView txtSub1;
		protected TextView txtSub2;
		protected TextView txtSub3;
	
		private ViewHolder(View view) {
			imgIcon = (ImageView) view.findViewById(R.id.img_icon);
			txtTitle = (TextView) view.findViewById(R.id.txt_title);
			txtSub1 = (TextView) view.findViewById(R.id.txt_sub1);
			txtSub2 = (TextView) view.findViewById(R.id.txt_sub2);
			txtSub3 = (TextView) view.findViewById(R.id.txt_sub3);
		}
		
	}

}

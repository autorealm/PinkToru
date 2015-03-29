package com.sunteorum.pinktoru.adapter;

import java.util.List;
import java.util.Map;

import com.sunteorum.pinktoru.R;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DragGridAdapter extends BaseAdapter {
	/** TAG*/
	private final static String TAG = "DragAdapter";
	/** 是否显示底部的ITEM */
	private boolean isItemShow = false;
	private Context context;
	/** 控制的postion */
	private int holdPosition;
	/** 是否改变 */
	private boolean isChanged = false;
	/** 列表数据是否改变 */
	private boolean isListChanged = false;
	/** 是否可见 */
	boolean isVisible = true;
	/** 可以拖动的列表（即用户选择的频道列表） */
	public List<Map<String, Object>> mList;
	/** TextView 频道内容 */
	private TextView item_text;
	/** 要删除的position */
	public int remove_position = -1;

	public DragGridAdapter(Context context, List<Map<String, Object>> channelList) {
		this.context = context;
		this.mList = channelList;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList == null ? 0 : mList.size();
	}

	@Override
	public Map<String, Object> getItem(int position) {
		// TODO Auto-generated method stub
		if (mList != null && mList.size() != 0) {
			return mList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//if (convertView == null)
			convertView = View.inflate(context, R.layout.sample_stage_item, null);
		
		item_text = (TextView) convertView.findViewById(R.id.txt_title);
		Map<String, Object> channel = getItem(position);
		item_text.setText(channel.get("name").toString());
		if ((position == 0) || (position == 1)){
			//item_text.setTextColor(context.getResources().getColor(R.color.black));
			item_text.setEnabled(false);
		}
		if (isChanged && (position == holdPosition) && !isItemShow) {
			item_text.setText("");
			item_text.setSelected(true);
			item_text.setEnabled(true);
			isChanged = false;
		}
		if (!isVisible && (position == -1 + mList.size())) {
			item_text.setText("");
			item_text.setSelected(true);
			item_text.setEnabled(true);
		}
		if(remove_position == position){
			item_text.setText("");
		}
		
		return convertView;
	}

	/** 添加列表 */
	public void addItem(Map<String, Object> channel) {
		mList.add(channel);
		isListChanged = true;
		notifyDataSetChanged();
	}

	/** 拖动变更排序 */
	public void exchange(int dragPostion, int dropPostion) {
		holdPosition = dropPostion;
		Map<String, Object> dragItem = getItem(dragPostion);
		Log.d(TAG, "startPostion=" + dragPostion + "; endPosition=" + dropPostion);
		if (dragPostion < dropPostion) {
			mList.add(dropPostion + 1, dragItem);
			mList.remove(dragPostion);
		} else {
			mList.add(dropPostion, dragItem);
			mList.remove(dragPostion + 1);
		}
		isChanged = true;
		isListChanged = true;
		notifyDataSetChanged();
	}
	
	/** 获取列表 */
	public List<Map<String, Object>> getChannnelLst() {
		return mList;
	}

	/** 设置删除的position */
	public void setRemove(int position) {
		remove_position = position;
		notifyDataSetChanged();
	}

	/** 删除列表 */
	public void remove() {
		mList.remove(remove_position);
		remove_position = -1;
		isListChanged = true;
		notifyDataSetChanged();
	}
	
	/** 设置列表 */
	public void setListDate(List<Map<String, Object>> list) {
		mList = list;
	}
	
	/** 获取是否可见 */
	public boolean isVisible() {
		return isVisible;
	}
	
	/** 排序是否发生改变 */
	public boolean isListChanged() {
		return isListChanged;
	}
	
	/** 设置是否可见 */
	public void setVisible(boolean visible) {
		isVisible = visible;
	}

	/** 显示放下的ITEM */
	public void setShowDropItem(boolean show) {
		isItemShow = show;
	}

}
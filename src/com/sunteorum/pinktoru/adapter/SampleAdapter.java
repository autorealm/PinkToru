package com.sunteorum.pinktoru.adapter;

import java.util.List;
import java.util.Map;

import com.sunteorum.pinktoru.R;
import com.sunteorum.pinktoru.inc.ColorGenerator;
import com.sunteorum.pinktoru.inc.TextDrawable;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SampleAdapter extends BaseAdapter {
	private Context mContext;
	private List<Object> mDataSource;
	private TextDrawable.IBuilder mDrawableBuilder;
	
	public SampleAdapter(Context context, List<Object> list) {
		super();
		mContext = context;
		mDataSource = list;
		
		mDrawableBuilder = TextDrawable.builder()
				.beginConfig()
					.withBorder(4)
				.endConfig()
				.roundRect(10);
		
	}

	@Override
	public int getCount() {
		return mDataSource.size();
	}

	@Override
	public Object getItem(int position) {
		return mDataSource.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.sample_list_item, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (SampleItem.class.isInstance(getItem(position))) {
			final SampleItem item = (SampleItem) getItem(position);;
			final Drawable drawable = item.getDrawable();
			holder.imageView.setImageDrawable(drawable);
			holder.textView.setText(item.getLabel());
			
			if (item.hasSub()) {
				holder.textView.setCompoundDrawablesWithIntrinsicBounds(null, null,
						mContext.getResources().getDrawable(R.drawable.ic_action_next_item), null);
			} else {
				holder.textView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
			}

			updateCheckedState(convertView, holder, item.isChecked());
			
			// fix for animation not playing for some below 4.4 devices
			if (drawable instanceof AnimationDrawable) {
				holder.imageView.post(new Runnable() {
					@Override
					public void run() {
						((AnimationDrawable) drawable).stop();
						((AnimationDrawable) drawable).start();
					}
				});
			}
			
		} else if (Map.class.isInstance(getItem(position))) {
			
		} else {
			String data = getItem(position).toString();
			TextDrawable drawable = mDrawableBuilder.build(String.valueOf(data.charAt(0)),
					ColorGenerator.MATERIAL.getColor(data));
			holder.imageView.setImageDrawable(drawable);
			holder.textView.setText(data);
		}
		
		

		return convertView;
	}

	private void updateCheckedState(View view, ViewHolder holder, Boolean isChecked) {
		if (isChecked) {
			view.setBackgroundColor(0x999be6ff);
			holder.checkIcon.setVisibility(View.VISIBLE);
		} else {
			view.setBackgroundColor(Color.TRANSPARENT);
			holder.checkIcon.setVisibility(View.GONE);
		}
	}

	private class ViewHolder {
	
		private ImageView imageView;
		private ImageView checkIcon;
		private TextView textView;
	
		private ViewHolder(View view) {
			imageView = (ImageView) view.findViewById(R.id.iv_icon);
			checkIcon = (ImageView) view.findViewById(R.id.iv_check);
			textView = (TextView) view.findViewById(R.id.tv_label);
		}
	}

	public static class SampleItem {
	    private String label;
	    private Drawable drawable;
	    private boolean sub = false;
	    private boolean checked = false;
	    
		public SampleItem(String label, Drawable drawable) {
			this.label = label;
			this.drawable = drawable;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public Drawable getDrawable() {
			return drawable;
		}

		public void setDrawable(Drawable drawable) {
			this.drawable = drawable;
		}

		public boolean hasSub() {
			return sub;
		}

		public boolean isChecked() {
			return checked;
		}

		public void setChecked(boolean checked) {
			this.checked = checked;
		}
	    
	}
}

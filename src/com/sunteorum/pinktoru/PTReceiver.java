package com.sunteorum.pinktoru;

import com.sunteorum.pinktoru.util.Common;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

public class PTReceiver extends BroadcastReceiver {
	private long did;
	private DownloadManager downloadManager;

	public PTReceiver() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PTReceiver(long id) {
		this.did = id;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		String action = intent.getAction();
		if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
			//Toast.makeText(context, "���������....", Toast.LENGTH_LONG).show();
			
			long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
			//TODO �ж����id��֮ǰ��id�Ƿ���ȣ�������˵����֮ǰ���Ǹ�Ҫ���ص��ļ�
			if (id != did) return;
			
			Query query = new Query();
			query.setFilterById(id);
			downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
			Cursor cursor = downloadManager.query(query);
			
			int columnCount = cursor.getColumnCount();
			String path = null;
			//TODO ��������е��ж���ӡһ�£���ʲô���󣬾���ô����,�ļ��ı���·������path
			while(cursor.moveToNext()) {
				for (int j = 0; j < columnCount; j++) {
					String columnName = cursor.getColumnName(j);
					String string = cursor.getString(j);
					if(columnName.equals("local_uri")) {
						path = string;
						
					}
					if(string != null) {
						System.out.println(columnName+": "+ string);
						if(columnName.equals("local_filename")) {
							if (path == null) path = string;
							
						}
					} else {
						System.out.println(columnName+": null");
						
					}
				}
			}
			cursor.close();
			if (path == null) return;
			//���sdcard������ʱ�����������ļ�����ô���ｫ��һ�������ṩ�ߵ�·���������ӡ��������ʲô�������ô������
			if(path.startsWith("content:")) {
               cursor = context.getContentResolver().query(Uri.parse(path), null, null, null, null);
               columnCount = cursor.getColumnCount();
               while (cursor.moveToNext()) {
                    for (int j = 0; j < columnCount; j++) {
                        String columnName = cursor.getColumnName(j);
                        String string = cursor.getString(j);
                        if(string != null) {
                            System.out.println(columnName+": "+ string);
                            if(columnName.equals("_data")) {
                            	path = string;
                            	
        					}
						} else {
							System.out.println(columnName+": null");
							
						}
                        
                    }
				}
				cursor.close();
			}
			
			System.out.println("APK_PATH: " + path);
			Common.setupApp(context, path, true);
			
			
		} else if (action.equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)) {
			//Toast.makeText(context, "���<span style='font-family: ����; '>֪ͨ</span>
			//<span style='font-size: 10.5pt; text-indent: 21pt; font-family: ����; '>��....</span>", Toast.LENGTH_LONG).show();
		}
	}
}


package com.sunteorum.pinktoru.util;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class HttpUtils {
	
	public static interface ResultCallBack {
		void onResult(String url, int code, String result);
	}
	
	public static boolean getConnectState(Context mContext) {
		ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netinfo = manager.getActiveNetworkInfo();
		
		return ((netinfo == null)?false:netinfo.isConnected());
	}
	
	public static String getConnectResult(String url) {
		HttpGet httpget = new HttpGet(url);
		String result = "";
		try {
			HttpClient client = new DefaultHttpClient();
			HttpResponse resp = client.execute(httpget);
			if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				result = EntityUtils.toString(resp.getEntity());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * 以POST方式发送请求并返回服务器回应的字符串
	 * @param url 请求地址
	 * @param pmap 请求的参数键值对
	 * @return 回应字符串
	 */
	public static String postHttpRequest(String url, Map<String, String> pmap) {
		String strResult = "";
		if (pmap == null || url == null) return null;
		//pmap.put("_t", String.valueOf(System.currentTimeMillis()));
		
        HttpPost httpRequest = new HttpPost(url); 
        
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        for (String k:pmap.keySet()) {
        	params.add(new BasicNameValuePair(k, pmap.get(k))); 
        }
        
        try {
            httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpClient client = new DefaultHttpClient();
            HttpResponse httpResponse = client.execute(httpRequest);
            
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                strResult = EntityUtils.toString(httpResponse.getEntity());
                
            } else {
            	Log.i("HttpPost", "Error Response:" + httpResponse.getStatusLine().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return strResult;
        
	}

	/**
	 * 在线程中以GET方式请求服务器数据
	 * @param RequestURL 请求Url地址
	 * @param callBack 回调函数
	 * @return 该线程
	 */
	public static Thread requestHttpGet(final String RequestURL, final ResultCallBack callBack) {
		if (RequestURL == null || RequestURL.length() < 1) return null;
		final Handler mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if (callBack != null) callBack.onResult(RequestURL, msg.arg1, msg.obj.toString());
				System.out.println("RequestURL: " + RequestURL);
				System.out.println("ResponseCode: " + msg.arg1);
				System.out.println("ResultString: " + msg.obj);
			}
			
		};
		
		Thread mThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				HttpURLConnection conn = null;
				int rc = 0;
				String result = "";
				try {
					URL url = new URL(RequestURL);
					conn = (HttpURLConnection) url.openConnection();
					conn.setConnectTimeout(8*1000);
					conn.setRequestMethod("GET");
					conn.setDoInput(true);
					
					conn.connect();
					rc = conn.getResponseCode();
					if (rc == HttpStatus.SC_OK) {
						InputStream input = conn.getInputStream();
						StringBuffer sbuffer = new StringBuffer();
						int ss;
						while ((ss = input.read()) != -1) {
							sbuffer.append((char) ss);
						}
						result = sbuffer.toString();
						//System.out.println(result);
					} else {
						result = conn.getResponseMessage();
					}
					
				} catch (Exception e) {
					result = e.getMessage();
					rc = 0;
				} finally {
					if (conn != null) conn.disconnect();
					Message msg = Message.obtain();
					msg.arg1 = rc;
					msg.arg2 = 0;
					msg.obj = result;
					mHandler.sendMessage(msg);
				}
				
			}
			
		});
		
		mThread.setPriority(Thread.NORM_PRIORITY - 1);
		mThread.start();
		
		return mThread;
		
	}


	/**
	 * 上传文件到服务器
	 * @param file 需要上传的文件
	 * @param RequestURL 请求的URL
	 * @param name 文件对应的键名
	 * @return 返回响应的内容
	 */
	public static String uploadFile(File file, String RequestURL, String name) {
		if (file == null || RequestURL == null || RequestURL.trim().length() == 0) return null;
		if (name == null || name.trim().length() == 0) name = "file";
		final String TAG = "uploadFile";
		final int TIME_OUT = 6 * 1000;
		final String CHARSET = "utf-8"; // 设置编码
		String result = null;
		String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
		String PREFIX = "--", LINE_END = "\r\n";
		String CONTENT_TYPE = "multipart/form-data"; // 内容类型
		HttpURLConnection conn = null;
		
		try {
			URL url = new URL(RequestURL);
			conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIME_OUT);
			conn.setConnectTimeout(TIME_OUT);
			conn.setDoInput(true); // 允许输入流
			conn.setDoOutput(true); // 允许输出流
			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestMethod("POST"); // 请求方式
			conn.setRequestProperty("Charset", CHARSET); // 设置编码
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="+ BOUNDARY);
			
			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
			StringBuffer sb = new StringBuffer();
			sb.append(PREFIX);
			sb.append(BOUNDARY);
			sb.append(LINE_END);
			
			sb.append("Content-Disposition: form-data; name=\"" + name + "\"; filename=\""
					+ file.getName() + "\"" + LINE_END);
			sb.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINE_END);
			sb.append(LINE_END);
			dos.write(sb.toString().getBytes());
			InputStream is = new FileInputStream(file);
			byte[] bytes = new byte[1024];
			int len = 0;
			while ((len = is.read(bytes)) != -1) {
				dos.write(bytes, 0, len);
			}
			is.close();
			dos.write(LINE_END.getBytes());
			byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
			dos.write(end_data);
			dos.flush();
			
			int rc = conn.getResponseCode();
			Log.e(TAG, "response code : " + rc);
			if (rc == HttpStatus.SC_OK) {
				Log.e(TAG, "request success");
				InputStream input = conn.getInputStream();
				StringBuffer sbuffer = new StringBuffer();
				int ss;
				while ((ss = input.read()) != -1) {
					sbuffer.append((char) ss);
				}
				result = sbuffer.toString();
				Log.e(TAG, "result : " + result);
				return result;
			} else {
				Log.e(TAG, "request error");
				return null;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) conn.disconnect();
		}
		
		return null;
	}
	
}

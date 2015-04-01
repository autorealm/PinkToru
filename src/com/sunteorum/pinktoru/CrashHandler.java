package com.sunteorum.pinktoru;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

/**
 * 全局异常处理器
 * @author KYO
 *
 */
public class CrashHandler implements UncaughtExceptionHandler {
	private final String TAG = "CrashHandler";
	private final boolean DEBUG = true;
	private static CrashHandler mCatcher;
	private static UncaughtExceptionHandler eHandler;
	private Context context;
	private Properties proper;
	
	private Map<String, String> infos;
	private DateFormat formatter;
	
	protected CrashHandler() {
		this.proper = new Properties();
		this.infos = new HashMap<String, String>();
		this.formatter = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault());
		
	}
	
	public static CrashHandler getInstance() {
		if (mCatcher == null) mCatcher = new CrashHandler();
		
		return mCatcher;
	}
	
	public void init(Context context) {
		this.context = context;
		eHandler = Thread.getDefaultUncaughtExceptionHandler();
		
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				infos.put("versionName", pi.versionName);
                infos.put("versionCode", "" + pi.versionCode);
			}
			//使用反射来收集设备信息.在Build类中包含各种设备信息
			Field[] fields = Build.class.getDeclaredFields();
			for (Field field : fields) {
				//设置Accessible属性为true,才能对私有变量进行访问
				field.setAccessible(true);
				infos.put(field.getName(), field.get(null).toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//设置CrashHandler为程序的默认处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
	}
	
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && eHandler != null) {
			//如果用户没有处理则让系统默认的异常处理器来处理
			eHandler.uncaughtException(thread, ex);
		} else {
			
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(9);
		}
		
	}
	
	private boolean handleException(final Throwable ex) {
		if (ex == null) return false;
		
		final String msg = ex.getLocalizedMessage();
		if(msg == null) return false;
		
		if (DEBUG) Log.e(TAG, ex.getLocalizedMessage());
		ex.printStackTrace();
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Looper.prepare();
				
				Toast toast = Toast.makeText(context, "非常抱歉，程序出现异常，将被强制退出：\r\n" + msg, Toast.LENGTH_LONG);
				toast.setGravity(android.view.Gravity.CENTER, 0, 0);
				toast.show();
				/*
				new AlertDialog.Builder(context)
						.setTitle("提示")
						.setCancelable(false)
						.setMessage("程序崩溃了...")
						.setNegativeButton("", null)
						.create()
						.show();
				*/
				Looper.loop();
				
				saveCrashInfo(ex);
			}
			
		}).start();
		
		return true;
	}

	private String saveCrashInfo(Throwable ex) {
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		
		if (DEBUG) ex.printStackTrace();
		
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		
		printWriter.close();
		String result = writer.toString();
		infos.put("EXCEPTION", ex.getLocalizedMessage());
		infos.put("STACK_TRACE", result);
		proper.putAll(infos);
		//proper.store(trace, "");
		
		String time = formatter.format(new Date());
		String saveName = "crash-" + time;
		
		
		SharedPreferences sdf = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
		SharedPreferences.Editor edt = sdf.edit();
		edt.putString(saveName, proper.toString());
		edt.commit();
		
		return result;
	}
	
	
}

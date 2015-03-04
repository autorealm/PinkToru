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
 * ȫ���쳣������
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
			//ʹ�÷������ռ��豸��Ϣ.��Build���а��������豸��Ϣ
			Field[] fields = Build.class.getDeclaredFields();
			for (Field field : fields) {
				//����Accessible����Ϊtrue,���ܶ�˽�б������з���
				field.setAccessible(true);
				infos.put(field.getName(), field.get(null).toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//����CrashHandlerΪ�����Ĭ�ϴ�����
		Thread.setDefaultUncaughtExceptionHandler(this);
	}
	
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && eHandler != null) {
			//����û�û�д�������ϵͳĬ�ϵ��쳣������������
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
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Looper.prepare();
				
				Toast toast = Toast.makeText(context, "�ǳ���Ǹ����������쳣������ǿ���˳���\r\n" + msg, Toast.LENGTH_LONG);
				toast.setGravity(android.view.Gravity.CENTER, 0, 0);
				toast.show();
				/*
				new AlertDialog.Builder(context)
						.setTitle("��ʾ")
						.setCancelable(false)
						.setMessage("���������...")
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

package com.sunteorum.pinktoru;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebSettings.RenderPriority;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class WebActivity extends BaseActivity implements OnClickListener,DownloadListener   {
	WebView webView;
	ProgressBar progressbar;
	
	@SuppressLint("SetJavaScriptEnabled")
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_web);
		
		progressbar = (ProgressBar)  findViewById(R.id.progBar);
        
        
		webView = (WebView) findViewById(R.id.webViewer);
		WebSettings ws = webView.getSettings();
        ws.setJavaScriptEnabled(true); // ����֧��javascript�ű�
        ws.setJavaScriptCanOpenWindowsAutomatically(true);
        ws.setPluginState(PluginState.ON);
        //ws.setPluginsEnabled(true);
        ws.setDomStorageEnabled(true);
        ws.setLoadWithOverviewMode(true);
        ws.setUseWideViewPort(true);
        ws.setAllowFileAccess(true); // ��������ļ�
        ws.setBuiltInZoomControls(true); // ������ʾ���Ű�ť
        ws.setSupportZoom(true); //֧������
        ws.setRenderPriority(RenderPriority.HIGH);
        
        ws.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
        //ws.setDefaultTextEncodingName("utf-8"); //�����ı�����
        //ws.setBlockNetworkImage(true);
        ws.setAppCacheEnabled(true);
        ws.setAppCachePath(this.getApplicationContext().getDir("cache", MODE_PRIVATE).getPath());
        ws.setAppCacheMaxSize(1024 * 1024 * 10);
        ws.setCacheMode(WebSettings.LOAD_NO_CACHE);//���û���ģʽ
        
        ws.setUserAgentString("Mozilla/5.0 (Linux; U; Android 4.3; en-us; SM-N900T Build/JSS15J) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30");
        
        try {
			webView.getClass().getMethod("onResume").invoke(webView,(Object[])null);
			webView.getClass().getMethod("onPause").invoke(webView,(Object[])null);
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        webView.addJavascriptInterface(new InJavaScriptLocalObj(), "js_method");
        webView.setDownloadListener(this);
		webView.setWebViewClient(new MyWebViewClient());
		webView.setWebChromeClient(new MyWebChromeClient());
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		String url = "about:blank";
		Bundle bundle = getIntent().getExtras();
		if (bundle != null && bundle.containsKey("url")) {
			url = bundle.getString("url");
		} else if (getIntent().getData() != null) {
			url = getIntent().getDataString();
			System.out.println("LoadURL:" + url);
			if (url != null && url.startsWith("http://pt.939j.com/game_award.php"))
				;
			else 
				webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
		}
		
		try {
			webView.loadUrl(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//webView.loadUrl("file:///android_asset/unti.html");
		/*String htmlString = "<!doctype html><html><head><meta charset='utf-8'><title>ƴͼ��Ϸ - ��Ʒ����</title></head>"
				+ "<body><h1>��ȡ��Ʒ����ҳ��</h1><p>������һ��˵��<br /><i>��ϲ���ý�Ʒ����ȷ��</i><br />������ս���Ȩ��ƴͼ��Ϸ�ٷ�����</p><script type='text/javascript'>"
				+ "setTimeout(function(){"
				+ "var r=confirm('���ȷ��������ȡ��Ʒ'); if (r==true){document.write('����ʲôҲû�У�')}else{document.write('ȡ����û�н�Ʒ�ˣ�')}},1500)</script>"
				+ "</body></html>";*/
		//webView.loadData(htmlString, "text/html", "utf-8");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
			//webView.clearView();
			webView.goBack();
            return true; 
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, "ˢ��");
		menu.add(0, 2, 0, "����");
		//menu.add(0, 3, 0, "��ҳ");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			break;
		case 1: webView.reload();
			break;
		case 2:
			webView.stopLoading();
			if (webView.canGoBack()) webView.goBack();
			else finish();
			break;
		case 3:
			final EditText edt = new EditText(this);
			//edt.setSingleLine(true);
			
			Builder builder = new AlertDialog.Builder(WebActivity.this);
			builder.setIcon(android.R.drawable.ic_menu_search);
			builder.setTitle("�����ַ");
			builder.setView(edt);
			builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					String url = edt.getText().toString();
					
					webView.loadUrl(url);
				}
				
			});
			builder.setNegativeButton("ȡ��", null);
			builder.show();
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	final class MyWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			 if (url.indexOf(".3gp")!=-1 || url.indexOf(".mp4")!=-1 || url.indexOf(".flv")!=-1) {
				 Intent intent=new Intent("android.intent.action.VIEW",Uri.parse(url));
				 view.getContext().startActivity(intent);
				 return true;
	         } else {
	        	 view.loadUrl(url); 
	        	 return true; 
	         }
			
			//return super.shouldOverrideUrlLoading(view, url);
		}
		
		@Override  
        public void onPageFinished(WebView view, String url) {
			//view.loadUrl("javascript:window.js_method.showSource(document.getElementById('backtopage').innerHTML);");
			//webView.getSettings().setBlockNetworkImage(false);
            super.onPageFinished(view, url);
            view.loadUrl("javascript:var btp = document.getElementById('backtopage'); "
            		+ "if (btp != undefined) {window.js_method.gotoPage(btp.innerHTML, btp.getAttribute('value'));}");

		}
		
	}
	
	final class MyWebChromeClient extends WebChromeClient {

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			// TODO Auto-generated method stub
			super.onProgressChanged(view, newProgress);
			 if (newProgress == 100) {
				 progressbar.setVisibility(8);
				 
			 } else {
				 progressbar.setVisibility(0);
				 progressbar.setProgress(newProgress);
			 }
		}

		@Override
		public void onReceivedIcon(WebView view, Bitmap icon) {
			// TODO Auto-generated method stub
			super.onReceivedIcon(view, icon);
			
		}

		@Override
		public void onReceivedTitle(WebView view, String title) {
			// TODO Auto-generated method stub
			super.onReceivedTitle(view, title);
			setTitle(title);
		}

		@Override
		public void onReceivedTouchIconUrl(WebView view, String url,
				boolean precomposed) {
			// TODO Auto-generated method stub
			super.onReceivedTouchIconUrl(view, url, precomposed);
		}

		@Override
		public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
			Builder builder = new AlertDialog.Builder(WebActivity.this);
			builder.setTitle(null);
			builder.setMessage(message);
			builder.setCancelable(false);
			//builder.setView(new EditText(this));
			builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					result.confirm();
					System.out.println("=====================ȷ����ȡ======================");
				}
				
			});
			builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					result.cancel();
					Toast.makeText(WebActivity.this, "�ܿ�ϧ���������˸ý�Ʒ", Toast.LENGTH_SHORT).show();
					finish();
				}
				
			});
			builder.show();
			
			return true;
			//return super.onJsConfirm(view, url, message, result);
		}

		@Override
		public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
	        builder.setMessage(message).setPositiveButton("ȷ��", null);
	        builder.setCancelable(false);
	        AlertDialog dialog = builder.create();
	        dialog.show();
	        result.confirm();// ��Ϊû�а��¼�����Ҫǿ��confirm,����ҳ�������ʾ�������ݡ�  
	        
	        return true;
			//return super.onJsAlert(view, url, message, result);
		}

		@Override
		public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
			
			return super.onJsBeforeUnload(view, url, message, result);
		}

		@Override
		public void onShowCustomView(View view, CustomViewCallback callback) {
			if (myCallback != null) {
				myCallback.onCustomViewHidden();
				myCallback = null ;
				return;
			}
               
			//long id = Thread.currentThread().getId();
			
			ViewGroup parent = (ViewGroup) webView.getParent();
			//String s = parent.getClass().getName();
			parent.removeView(webView);
			parent.addView(view);
			myCallback = callback; 
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			
		}

		private View myView = null;
		private CustomViewCallback myCallback = null;
        
        
		public void onHideCustomView() {
			if (myView != null) {
				if (myCallback != null) {
					myCallback.onCustomViewHidden();
					myCallback = null ;
				}
                
	            ViewGroup parent = (ViewGroup) myView.getParent();
	            parent.removeView(myView);
	            parent.addView(webView);
	            myView = null;
	            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
		}
         
	}
	
	public class InJavaScriptLocalObj {
		@JavascriptInterface
        public void showSource(String html) {
            //System.out.println("<!--HTML-->"+html);
        	//if (html.length() > 0) finish();
            Log.i("webviewer", "" + html);
        }
		
		@SuppressLint("DefaultLocale") @JavascriptInterface
        public void gotoPage(String page, String value) {
			page = page.toLowerCase();
			Intent i = new Intent();
        	if (page.equals("choosepic")) {
        		//i.setClass(WebActivity.this, ChoosePic.class);
        	} else if (page.equals("setting")) {
        		//i.setClass(WebActivity.this, SystemSetting.class);
        	} else if (page.equals("login")) {
        		//i.setClass(WebActivity.this, Login.class);
        	} else if (page.equals("register")) {
        		//i.setClass(WebActivity.this, Register.class);
        	} else if (page.equals("startgame")) {
        		//i.setClass(WebActivity.this, HomeActivity.class);
        		Bundle b = new Bundle();
        		b.putString("start_game", value);
        		i.putExtras(b);
        		i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        		
        	} else {
        		//i.setClass(WebActivity.this, HomeActivity.class);
        	}
        	
        	startActivity(i);
            Log.i("webviewer", "gotoPage:" + page);
            finish();
        }
		
		@JavascriptInterface
        public void closePage() {
        	finish();
            Log.i("webviewer", "close");
        }
		
    }
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDownloadStart(String url, String userAgent,
			String contentDisposition, String mimetype, long contentLength) {
		// TODO Auto-generated method stub
		Log.d("--------------------------", mimetype);
		Log.d("--------------------------", url);
		Uri uri = Uri.parse(url);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}

}

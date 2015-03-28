package com.sunteorum.pinktoru.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

public class ImageUtils {
	static String tag = "ImageUtils";
	
	/**
	 * Drawable转换为Bitmap
	 * @param drawable
	 * @return
	 */
	public static Bitmap DrawableToBitmap(Drawable drawable) {
		if (null == drawable) return null;
		
		BitmapDrawable bd = (BitmapDrawable)drawable;
		return bd.getBitmap();
	}
	
	/**
	 * Bitmap转换为Drawable
	 * @param bitmap
	 * @return
	 */
	public static Drawable BitmapToDrawable(Context context, Bitmap bitmap) {
		if (null == bitmap) return null;
		
		return new BitmapDrawable(context.getResources(), bitmap);
	}

	public static Bitmap readBitmap(Context context, int resId, int width, int height){
		Bitmap bitmap = readBitmap(context, resId);
		return Bitmap.createScaledBitmap(bitmap, width, height, true);
	}
	
	/**
	 * 创建图片Bitmap
	 * @param path 图片路径
	 * @param width 最大宽
	 * @param height 最大高
	 * @return
	 */
	public static Bitmap createBitmap(String path, int width, int height) {
		try {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
			
			BitmapFactory.decodeFile(path, opts);
			int srcWidth = opts.outWidth; //获取图片的原始宽度
			int srcHeight = opts.outHeight; //获取图片原始高度
			int destWidth = 0;
			int destHeight = 0;
			
			float ratio = 0; //缩放的比例
			if (width > 0 && height > 0) {
				if (srcWidth < width && srcHeight < height) {
					destWidth = srcWidth;
					destHeight = srcHeight;
				} else if (srcWidth > srcHeight) { //按比例计算缩放后的图片大小
					ratio = (float) srcWidth / (float) width;
					destWidth = width;
					destHeight = Math.round(srcHeight / ratio);
				} else {
					ratio = (float) srcHeight / (float) height;
					destHeight = height;
					destWidth = Math.round(srcWidth / ratio);
				}
			} else {
				destWidth = srcWidth;
				destHeight = srcHeight;
			}
			
			BitmapFactory.Options newOpts = new BitmapFactory.Options();
			newOpts.inSampleSize = (int) (ratio + 0.5f);
			newOpts.inJustDecodeBounds = false;
			
			newOpts.outHeight = destHeight;
			newOpts.outWidth = destWidth;
			
			return BitmapFactory.decodeFile(path, newOpts);
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		
		return null;
	}

	/**
	 * 压缩显示图案(长宽不超过最大限制)
	 * @param uri 该图片的地址
	 * @return 位图
	 */
	public static Bitmap compressBitmap(String uri, int MAX_IMAGE_SIZE) throws Exception {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
		
		File file = new File(uri);
		if (!file.exists()) {
			file = new File(Uri.parse(uri).getPath());
			if (!file.exists())
				return BitmapFactory.decodeStream(new java.net.URL(uri).openStream());
		}
		
		BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
		
		int h = opts.outHeight;
		int w = opts.outWidth;
		
		float ratio = 1f;
		if (w >= h && w > MAX_IMAGE_SIZE) {
			ratio = (float) w / MAX_IMAGE_SIZE;
		} else if (w < h && h > MAX_IMAGE_SIZE) {
			ratio = (float) h / MAX_IMAGE_SIZE;
		}

		if (ratio < 1) {
			ratio = 1f;
		} else if (ratio > 1) {
			ratio = (float) ((Math.ceil(ratio)) * 2);
		}
		
		opts.inJustDecodeBounds = false;
		opts.inSampleSize = (int) ratio;
		
		Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
		
		Log.i(tag, "inSampleSize:" + ratio + " w- " + w + " h- " + h + " @" + file.getName());
		//return bmp;
		
		return new java.lang.ref.WeakReference<Bitmap>(bmp).get();
		
	}

	/**
	 * 保存图片数据到文件
	 * @param bitmap 图片
	 * @param tofile 文件
	 * @param delx 图片文件存在时是否删除后保存，否则不保存
	 * @param compressFormat 图片压缩格式(null 则为JPG格式)
	 * @return 保存是否成功
	 */
	public static boolean saveBitmap(Bitmap bitmap, File tofile, Boolean delx, Bitmap.CompressFormat compressFormat) {
		if (bitmap == null || tofile == null) return false;
		FileOutputStream fileOutputStream = null;
		if (tofile.exists()) {
			if (delx) tofile.delete(); else return true;
		}
		
		File dir = tofile.getParentFile();
		if (dir != null && !dir.exists()) {
			dir.mkdirs();
		}
		
		try {
			if (!tofile.createNewFile()) return false;
			if (!tofile.exists()) return false;
			fileOutputStream = new FileOutputStream(tofile);
			if (compressFormat == null) compressFormat = CompressFormat.JPEG;
			bitmap.compress(compressFormat, 100, fileOutputStream);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				fileOutputStream.flush();
				fileOutputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		
		return true;
	}
	
	public static Drawable readDrawable(Context context, int resId, int width, int height) {
		Bitmap bitmap = readBitmap(context, resId);
		bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
		return BitmapToDrawable(context, bitmap);
	}
	
	public static Drawable readDrawable(Context context, String imagePath, int width, int height) {
		Bitmap bitmap = createBitmap(imagePath, width, height);
		if (bitmap == null) return null;
		return BitmapToDrawable(context, bitmap);
	}
	
	@SuppressWarnings("deprecation")
	public static Bitmap readBitmap(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		
		InputStream is = context.getResources().openRawResource(resId);
		
		return BitmapFactory.decodeStream(is, null, opt);
	}
	
	@SuppressWarnings("deprecation")
	public static Bitmap readBitmap(String imagePath, int size) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		
		if (size > 0 ) opt.inSampleSize = size;
		
		return BitmapFactory.decodeFile(imagePath, opt);
	}
	

	/**
	 * 缩放图像
	 * @param bitmap
	 * @param w
	 * @param h
	 * @return
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
		if (bitmap == null) return null;
		
		Bitmap newbmp = null;
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidht = ((float) width / w);
		float scaleHeight = ((float) height / h);
		matrix.postScale(scaleWidht, scaleHeight);
		newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
		
		return newbmp;
	}
	
	/**
	 * 剪切图片的一部分并返回
	 * @param bmp
	 * @param w
	 * @param h
	 * @param flag  0：居中，1：居上/居左， 2：居右/居下。
	 * @return
	 */
	public static Bitmap cutBitmap(Bitmap bmp, int w, int h, int flag) {
		if (bmp == null) return null;
		int x = 0, y = 0;
		Bitmap bitmap = null;
		try {
			int width = bmp.getWidth();
			int height = bmp.getHeight();
			if (((float)width / (float)height) > ((float)w / (float)h)) {
				int w2 = Math.round(w  * height / h);
				if (w2 + ((width - w2) / 2) > width) w2 = width;
				if (flag == 1) {x = 0; y = 0;}
				else if (flag == 2) {x = width - w2; y = 0;}
				else {x = (width - w2) / 2; y = 0;}
				bitmap = Bitmap.createBitmap(bmp, x, y, w2, height);
			} else {
				int h2 = Math.round(h  * width / w);
				if (h2 + ((height - h2) / 2) > height) h2 = height;
				if (flag == 1) {x = 0; y = 0;}
				else if (flag == 2) {x = 0; y = height - h2;}
				else {x = 0; y = (height - h2) / 2;}
				bitmap = Bitmap.createBitmap(bmp, x, y, width, h2);
				
			}
			if (bitmap != null) bitmap = zoomBitmap(bitmap, w, h);
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError oom) {
			oom.printStackTrace();
		}
		
		return bitmap;
	}
	
	public static Bitmap createRoundedImage(Bitmap bitmap, float roundPx) {
		if (bitmap == null) return null;
		
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		
		return output;
	}
	
	public static Bitmap createReflectedImage(Context context, int resId) {
		Bitmap originalImage = readBitmap(context, resId);
		return createReflectedImage(originalImage);
	}
	
	public static Bitmap createReflectedImage(String imgPath) {
		Bitmap originalImage = readBitmap(imgPath, 0);
		return createReflectedImage(originalImage);
	}
	
	public static Bitmap createReflectedImage(Bitmap originalImage) {
		if (originalImage == null) return null;
		int reflectionGap = 4;
		
		int width = originalImage.getWidth();   //原图宽
		int height = originalImage.getHeight();   //原图高
		
		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);
		
		//获取原图下半张图片
		Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, height / 2, width, height / 2, matrix, false);

		//创建1.5倍高的透明图片
		Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height / 2), Config.ARGB_8888);
		
		Canvas canvas = new Canvas(bitmapWithReflection);
		//绘制原图
		canvas.drawBitmap(originalImage, 0, 0, null);
		
		Paint paint = new Paint();
		//绘制原图和水印的间隙
		canvas.drawRect(0, height, width, height + reflectionGap, paint);

		//绘制下半张图片倒影
		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
		
		LinearGradient shader = new LinearGradient(0, height, 0,
				(height + height/2) + reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		canvas.drawRect(0, height, width, (height + height/2) + reflectionGap, paint);
		
		return bitmapWithReflection;
	}
	
	public static Bitmap drawGridInBitmap(Bitmap src, int line, int row, int color) {
		Bitmap bmp = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		canvas.drawBitmap(src, 0, 0, null);

		Paint paint = new Paint();
		paint.setColor(color);
		paint.setStrokeWidth(1.2f);
		paint.setAntiAlias(true);
		//paint.setAlpha(128);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.LIGHTEN));
		
		float w = ((float) src.getWidth() / line);
		float h = ((float) src.getHeight() / row);
		for (int i=1; i<line; i++) {
			canvas.drawLine(w*i, 0, w*i, src.getHeight(), paint);
		}
		for (int i=1; i<row; i++) {
			canvas.drawLine(0, h*i, src.getWidth(), h*i, paint);
			
		}
		
		return bmp;
	}
	
	
	/*
	 * This method was copied from http://stackoverflow.com/a/10028267/694378.
	 * The only modifications I've made are to remove a couple of Log
	 * statements which could slow things down slightly.
	 */
	public static Bitmap fastBlur(Bitmap sentBitmap, int radius) {

		// Stack Blur v1.0 from
		// http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html
		//
		// Java Author: Mario Klingemann <mario at quasimondo.com>
		// http://incubator.quasimondo.com
		// created Feburary 29, 2004
		// Android port : Yahel Bouaziz <yahel at kayenko.com>
		// http://www.kayenko.com
		// ported april 5th, 2012

		// This is a compromise between Gaussian Blur and Box blur
		// It creates much better looking blurs than Box Blur, but is
		// 7x faster than my Gaussian Blur implementation.
		//
		// I called it Stack Blur because this describes best how this
		// filter works internally: it creates a kind of moving stack
		// of colors whilst scanning through the image. Thereby it
		// just has to add one new block of color to the right side
		// of the stack and remove the leftmost color. The remaining
		// colors on the topmost layer of the stack are either added on
		// or reduced by one, depending on if they are on the right or
		// on the left side of the stack.
		//
		// If you are using this algorithm in your code please add
		// the following line:
		//
		// Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com>

		Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

		if (radius < 1) {
			return (null);
		}

		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		int[] pix = new int[w * h];
		bitmap.getPixels(pix, 0, w, 0, 0, w, h);

		int wm = w - 1;
		int hm = h - 1;
		int wh = w * h;
		int div = radius + radius + 1;

		int r[] = new int[wh];
		int g[] = new int[wh];
		int b[] = new int[wh];
		int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
		int vmin[] = new int[Math.max(w, h)];

		int divsum = (div + 1) >> 1;
		divsum *= divsum;
		int dv[] = new int[256 * divsum];
		for (i = 0; i < 256 * divsum; i++) {
			dv[i] = (i / divsum);
		}

		yw = yi = 0;

		int[][] stack = new int[div][3];
		int stackpointer;
		int stackstart;
		int[] sir;
		int rbs;
		int r1 = radius + 1;
		int routsum, goutsum, boutsum;
		int rinsum, ginsum, binsum;

		for (y = 0; y < h; y++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			for (i = -radius; i <= radius; i++) {
				p = pix[yi + Math.min(wm, Math.max(i, 0))];
				sir = stack[i + radius];
				sir[0] = (p & 0xff0000) >> 16;
				sir[1] = (p & 0x00ff00) >> 8;
				sir[2] = (p & 0x0000ff);
				rbs = r1 - Math.abs(i);
				rsum += sir[0] * rbs;
				gsum += sir[1] * rbs;
				bsum += sir[2] * rbs;
				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
				}
			}
			stackpointer = radius;

			for (x = 0; x < w; x++) {

				r[yi] = dv[rsum];
				g[yi] = dv[gsum];
				b[yi] = dv[bsum];

				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;

				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];

				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];

				if (y == 0) {
					vmin[x] = Math.min(x + radius + 1, wm);
				}
				p = pix[yw + vmin[x]];

				sir[0] = (p & 0xff0000) >> 16;
				sir[1] = (p & 0x00ff00) >> 8;
				sir[2] = (p & 0x0000ff);

				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];

				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;

				stackpointer = (stackpointer + 1) % div;
				sir = stack[(stackpointer) % div];

				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];

				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];

				yi++;
			}
			yw += w;
		}
		for (x = 0; x < w; x++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			yp = -radius * w;
			for (i = -radius; i <= radius; i++) {
				yi = Math.max(0, yp) + x;

				sir = stack[i + radius];

				sir[0] = r[yi];
				sir[1] = g[yi];
				sir[2] = b[yi];

				rbs = r1 - Math.abs(i);

				rsum += r[yi] * rbs;
				gsum += g[yi] * rbs;
				bsum += b[yi] * rbs;

				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
				}

				if (i < hm) {
					yp += w;
				}
			}
			yi = x;
			stackpointer = radius;
			for (y = 0; y < h; y++) {
				// Preserve alpha channel: ( 0xff000000 & pix[yi] )
				pix[yi] = ( 0xff000000 & pix[yi] ) | ( dv[rsum] << 16 ) | ( dv[gsum] << 8 ) | dv[bsum];

				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;

				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];

				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];

				if (x == 0) {
					vmin[y] = Math.min(y + r1, hm) * w;
				}
				p = x + vmin[y];

				sir[0] = r[p];
				sir[1] = g[p];
				sir[2] = b[p];

				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];

				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;

				stackpointer = (stackpointer + 1) % div;
				sir = stack[stackpointer];

				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];

				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];

				yi += w;
			}
		}

		bitmap.setPixels(pix, 0, w, 0, 0, w, h);

		return (bitmap);
	}


}

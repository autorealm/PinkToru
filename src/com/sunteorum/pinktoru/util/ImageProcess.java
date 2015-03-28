package com.sunteorum.pinktoru.util;

import android.graphics.Bitmap;
import android.graphics.Point;

public class ImageProcess {

	public ImageProcess() {
		// TODO Auto-generated constructor stub
	}


	/**
	 * 四象（八象）水漫填充法
	 * @param bitmap 目标图片（直接操作）
	 * @param point 点相对于图片的坐标
	 * @param sColor 原始颜色
	 * @param dColor 填充颜色
	 */
	public static void floodFill(Bitmap bitmap, Point point, int sColor, int dColor) {
		if (sColor == dColor) return;
		if (bitmap == null || point == null) return;
		
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		int x = point.x;
		int y = point.y;
		
		int pColor = bitmap.getPixel(x, y);
		if (pColor == dColor || pColor != sColor) return;
		
		if ((x >= 0 && x < w) && (y >= 0 && y < h) && (pColor == sColor)) {
			bitmap.setPixel(x, y, dColor);
			
			floodFill (bitmap, new Point (x + 1, y), sColor, dColor);
			floodFill (bitmap, new Point (x - 1, y), sColor, dColor);
			floodFill (bitmap, new Point (x, y + 1), sColor, dColor);
			floodFill (bitmap, new Point (x, y - 1), sColor, dColor);
			
			floodFill (bitmap, new Point (x + 1, y + 1), sColor, dColor);
			floodFill (bitmap, new Point (x + 1, y - 1), sColor, dColor);
			floodFill (bitmap, new Point (x - 1, y + 1), sColor, dColor);
			floodFill (bitmap, new Point (x - 1, y - 1), sColor, dColor);
		}
		
		
	}
	
	/**
	 * 扫描线（左右方式）水漫填充法
	 * @param bitmap 目标图片（直接操作）
	 * @param x 点相对于图片的X坐标
	 * @param y 点相对于图片的Y坐标
	 * @param oldColor 旧颜色
	 * @param newColor 新颜色
	 */
	public static void floodFill(Bitmap bitmap , int x, int y, int oldColor, int newColor) {
		if (oldColor == newColor) return;
		if (bitmap == null) return;
		
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		
		int y1 = y;
		while (y1 < h && bitmap.getPixel(x, y1) == oldColor) {
			bitmap.setPixel(x, y1, newColor);
			y1++;
		}    
		
		y1 = y - 1;
		while (y1 >= 0 && bitmap.getPixel(x, y1) == oldColor) {
			bitmap.setPixel(x, y1, newColor);
			y1--;
		}
		
		
		y1 = y;
		while (y1 < h && bitmap.getPixel(x, y1) == newColor) {
			if (x > 0 && bitmap.getPixel(x - 1, y1) == oldColor) {
				floodFill(bitmap, x - 1, y1, oldColor, newColor);
			}
			y1++;
		}
		
		y1 = y - 1;
		while (y1 >= 0 && bitmap.getPixel(x, y1) == newColor) {
			if (x > 0 && bitmap.getPixel(x - 1, y1) == oldColor) {
				floodFill(bitmap, x - 1, y1, oldColor, newColor);
			}
			y1--;
		} 
		
		y1 = y;
		while (y1 < h && bitmap.getPixel(x, y1) == newColor) {
			if (x < w - 1 && bitmap.getPixel(x + 1, y1) == oldColor) {           
				floodFill(bitmap, x + 1, y1, oldColor, newColor);
			} 
			y1++;
		}
		
		y1 = y - 1;
		while (y1 >= 0 && bitmap.getPixel(x, y1) == newColor) {
			if (x < w - 1 && bitmap.getPixel(x + 1, y1) == oldColor) {
				floodFill(bitmap, x + 1, y1, oldColor, newColor);
			}
			y1--;
		}
		
	}
	
	
}

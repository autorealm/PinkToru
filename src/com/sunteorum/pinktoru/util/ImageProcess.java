package com.sunteorum.pinktoru.util;

import android.graphics.Bitmap;
import android.graphics.Point;

public class ImageProcess {

	public ImageProcess() {
		// TODO Auto-generated constructor stub
	}


	/**
	 * ���󣨰���ˮ����䷨
	 * @param bitmap Ŀ��ͼƬ��ֱ�Ӳ�����
	 * @param point �������ͼƬ������
	 * @param sColor ԭʼ��ɫ
	 * @param dColor �����ɫ
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
	 * ɨ���ߣ����ҷ�ʽ��ˮ����䷨
	 * @param bitmap Ŀ��ͼƬ��ֱ�Ӳ�����
	 * @param x �������ͼƬ��X����
	 * @param y �������ͼƬ��Y����
	 * @param oldColor ����ɫ
	 * @param newColor ����ɫ
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

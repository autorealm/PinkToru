package com.sunteorum.pinktoru.entity;

import java.util.ArrayList;
import java.util.Vector;

import com.sunteorum.pinktoru.PinkToru;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.Region.Op;

public class PieceFactory {

	Context mContext;
	private int PIECE_CUT_FLAG = 0;
	private int SHADOW_OFFSET = 3;
	private int RENDER_FLAG = 1;
	private int PIECE_EDGE_WIDTH = 16;
	private int KOCH_CURVE_N = 2;
	private boolean QUAD_WAY = true;
	
	private int _D = 12; //比率系数，默认12
	private int _W = 4; //碎片凹凸系数，占边界长度的百分比，默认4
	
	private int SHADOW_COLOR = Color.argb(60, 60, 60, 60);
	
	public enum Place {
		Right, Feet;
	}
	
	private Bitmap mBitmap;
	
	private Canvas canvas = new Canvas();
	
	private Path dotPath = new Path();
	private Paint noPicPaint = new Paint();
	private Paint edgePaint = new Paint();
	private Paint alphaPaint = new Paint();
	
	private Vector<Piece> allPiece = new Vector<Piece>();
	
	private int _imageWidth;
	private int _imageHeight;
	
	private int _row;
	private int _line;
	
	private int _pieceWidth;
	private int _pieceHeight;
	private int _pieceD;
	
	//内切矩形宽高
	private int _pieceOW;
	private int _pieceOH;
	
	
	
	public PieceFactory(Context context) {
		super();
		this.mContext = context;
		
	}
	
	public void setImage(Bitmap bitmap) {
		mBitmap = bitmap;
		_imageWidth = mBitmap.getWidth();
		_imageHeight = mBitmap.getHeight();
		
	}
	
	public void setImage(int resId) {
		mBitmap = BitmapFactory.decodeResource(mContext.getResources(), resId);
		_imageWidth = mBitmap.getWidth();
		_imageHeight = mBitmap.getHeight();
		
	}
	
	public void setRowAndLine(int row, int line) {
		_row = row;
		_line = line;
		
		if (PIECE_CUT_FLAG == 1) {
			if (KOCH_CURVE_N > 2) _W = 6;
			else _W = 4;
		}
		
		bitmapCut();
	}
	
	private void pieceSet() {
		_pieceWidth = Math.round((float) _imageWidth / (float) _line);
		_pieceHeight = Math.round((float) _imageHeight / (float) _row);
		int minWH = Math.min(_pieceWidth, _pieceHeight);
		_pieceD = minWH / _D;
		_pieceOW = minWH / _W;
		_pieceOH = minWH / _W;
		
		noPicPaint.setColor(Color.DKGRAY);
		noPicPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		noPicPaint.setStyle(Paint.Style.FILL_AND_STROKE);  //实心填充
		noPicPaint.setStrokeWidth(1f); //外框宽度
		noPicPaint.setAntiAlias(true);  //抗锯齿
		noPicPaint.setFilterBitmap(true);
		noPicPaint.setShadowLayer(SHADOW_OFFSET, 0f, 0f, SHADOW_COLOR);
		//edgePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
		//noPicPaint.setPathEffect(new CornerPathEffect(5f));
		
		edgePaint.setColor(Color.LTGRAY);
		//edgePaint.setARGB(120, 60, 60, 60);
		edgePaint.setStyle(Paint.Style.STROKE);
		edgePaint.setStrokeWidth(PIECE_EDGE_WIDTH);
		edgePaint.setAntiAlias(true);
		if (RENDER_FLAG != 0) edgePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
		else edgePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.LIGHTEN));
		
		//alphaPaint.setPathEffect(new CornerPathEffect(10));
		alphaPaint.setColor(Color.TRANSPARENT);
		alphaPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		alphaPaint.setStrokeWidth(1f);
		alphaPaint.setAntiAlias(true);
		
		/*
		////////////////
		edgePaint.setColor(Color.BLACK);
		edgePaint.setAlpha(108);
		edgePaint.setStyle(Paint.Style.STROKE);
		edgePaint.setStrokeWidth(6);
		edgePaint.setAntiAlias(true);
		//////////图片效果
		//设置光源的方向
		float[] direction = new float[]{ 1, 1, 1 };
		//设置环境光亮度
		float light = 1.6f;
		//选择要应用的反射等级
		float specular = 5;
		//向mask应用一定级别的模糊
		float blur = 1.5f;
		EmbossMaskFilter emboss = new EmbossMaskFilter(direction, light, specular, blur);
		//应用mask
		edgePaint.setMaskFilter(emboss);
		*/
		
		
	}
	
	private void bitmapCut() {
		pieceSet();
		
		//透明模板
		//Bitmap copyBitPic = Bitmap.createBitmap(_imageW, _imageH, Config.ARGB_8888);
		
		for (int i=0; i<_row; i++) {
			for (int j=0; j<_line; j++) {
				Piece piece = new Piece();
				
				Point id = new Point(i, j);  //保存碎片的矩阵点
				piece.setId(id);
				
				Point key = new Point(j*_pieceWidth, i*_pieceHeight);  ///
				piece.setKey(key);
				piece.setLineWidth(_pieceWidth);
				piece.setRowHeight(_pieceHeight);

				//得出碎片的关键边界点
				setAllDotArray(piece);
				
				//得出左上角点位和右下角点位，用于切取小块碎片图片
				setMinAndMaxPoint(piece);
				
				setPieceBitmap(piece);
				
				//addEdge(piece);
			}
		}
				
	}
	

	private boolean isNearPoint(ArrayList<Point> dotArray, Point p) {
		boolean isnear = false;
		for (int i = 0; i < dotArray.size(); i++) {
			Point dot = (Point) dotArray.get(i);
			if (Math.abs(dot.x - p.x) < 5 && Math.abs(dot.y - p.y) < 5) {
				isnear = true;
				break;
			}
				
		}
		return isnear;
	}
	
	private void kochCurve(Point point, double angle, double length, int n, ArrayList<Point> dotArray) {
		if (dotArray == null) dotArray = new ArrayList<Point>();
		int rnd = ((int)(Math.random()*10)%2 ==0) ? 1 : -1;
		int x0 = point.x;
		int y0 = point.y;
        if (n == 0) {
            int x1 = (int) (x0 + Math.round(length * Math.cos(angle)));
            int y1 = (int) (y0 - Math.round(length * Math.sin(angle)));
            Point p = new Point(x1, y1);
            if (!dotArray.contains(point)) {
            	if (!isNearPoint(dotArray, point)) dotArray.add(point);
            }
            if (!dotArray.contains(p)) {
            	if (!isNearPoint(dotArray, p)) dotArray.add(p);
            }
            
        } else {
            length /= 3;
            n--;
            kochCurve(new Point(x0, y0), angle, length, n, dotArray);
 
            x0 += length * Math.cos(angle);
            y0 -= length * Math.sin(angle);
            angle += rnd * Math.PI / 3;
            kochCurve(new Point(x0, y0), angle, length, n, dotArray);
 
            x0 += length * Math.cos(angle);
            y0 -= length * Math.sin(angle);
            angle -= rnd * Math.PI * 2 / 3;
            kochCurve(new Point(x0, y0), angle, length, n, dotArray);
 
            x0 += length * Math.cos(angle);
            y0 -= length * Math.sin(angle);
            angle += rnd * Math.PI / 3;
            kochCurve(new Point(x0, y0), angle, length, n, dotArray);
        }
    }
	
	private ArrayList<Point> getKochDotArray(Piece piece, Place position) {
		//int rnd = ((int)(Math.random()*10)%2 ==0) ? 1 : -1;
		ArrayList<Point> kochDotArray = new ArrayList<Point>();
		Point key = piece.getKey();
		
		switch (position) {
		case Right:
			Point r1 = new Point(key.x + _pieceWidth, key.y);
			//Point r2 = new Point(r1.x, r1.y + _pieceHeight);
			kochCurve(r1, -Math.PI / 2, _pieceHeight, KOCH_CURVE_N, kochDotArray);
			//Collections.sort(kochDotArray, new pyComparator());
			break;
		case Feet:
			Point f1 = new Point(key.x + _pieceWidth, key.y + _pieceHeight);
			//Point f2 = new Point(f1.x - _pieceWidth, f1.y);
			kochCurve(f1, Math.PI, _pieceWidth, KOCH_CURVE_N, kochDotArray);
			//Collections.sort(kochDotArray, new pxComparator());
			break;
		}
		
		//System.out.println(kochDotArray.size());
		return kochDotArray;
	}
	
	private ArrayList<Point> getRectDotArray(Piece piece, Place position) {
		ArrayList<Point> rectDotArray = new ArrayList<Point>();
		Point key = piece.getKey();
		Point r1 = null, r2 = null;
		switch(position){
		case Right:
			r1 = new Point(key.x + _pieceWidth, key.y);
			r2 = new Point(r1.x, r1.y + _pieceHeight);
			
			break;
		case Feet:
			r1 = new Point(key.x + _pieceWidth, key.y + _pieceHeight);
			r2 = new Point(r1.x - _pieceWidth, r1.y);
			
			break;
		}
		rectDotArray.add(r1);
		rectDotArray.add(r2);
		
		return rectDotArray;
		
	}
	
	private void aotuBlock(Point point, double angle, double length, int n, ArrayList<Point> dotArray) {
		if (dotArray == null) dotArray = new ArrayList<Point>();
		int rnd = ((int)(Math.random()*10)%2 ==0) ? 1 : -1;
		int x0 = point.x;
		int y0 = point.y;
        if (n == 0) {
            int x1 = (int) (x0 + Math.round(length * Math.cos(angle)));
            int y1 = (int) (y0 - Math.round(length * Math.sin(angle)));
            Point p = new Point(x1, y1);
            if (!dotArray.contains(point)) {
            	if (!isNearPoint(dotArray, point)) dotArray.add(point);
            }
            if (!dotArray.contains(p)) {
            	if (!isNearPoint(dotArray, p)) dotArray.add(p);
            }
            
        } else {
            length /= 3;
            n--;
            aotuBlock(new Point(x0, y0), angle, length, n, dotArray);
 
            x0 += length * Math.cos(angle);
            y0 -= length * Math.sin(angle);
            angle += rnd * Math.PI / 2;
            aotuBlock(new Point(x0, y0), angle, length, n, dotArray);
 
            x0 += length * Math.cos(angle);
            y0 -= length * Math.sin(angle);
            angle -= rnd * Math.PI / 2;
            aotuBlock(new Point(x0, y0), angle, length, n, dotArray);

            x0 += length * Math.cos(angle);
            y0 -= length * Math.sin(angle);
            angle -= rnd * Math.PI / 2;
            aotuBlock(new Point(x0, y0), angle, length, n, dotArray);
            
            x0 += length * Math.cos(angle);
            y0 -= length * Math.sin(angle);
            angle += rnd * Math.PI / 2;
            aotuBlock(new Point(x0, y0), angle, length, n, dotArray);
        }
    }
	
	//取得凹凸点集
	public ArrayList<Point> getAotuDotArray(Piece piece, Place position) {
		ArrayList<Point> dotArray = new ArrayList<Point>();
		Point key = piece.getKey();
		switch(position){
		case Right:
			Point r1 = new Point(key.x + _pieceWidth, key.y);
			aotuBlock(r1, -Math.PI / 2, _pieceHeight, KOCH_CURVE_N, dotArray);
			
			break;
		case Feet:
			Point f1 = new Point(key.x + _pieceWidth, key.y + _pieceHeight);
			aotuBlock(f1, Math.PI, _pieceWidth, KOCH_CURVE_N, dotArray);
			
			break;
		}
		
		return dotArray;
	}

	private int getRndD() {
		//返回与边界错开的高度
		return _pieceD - (int)Math.random() * 2 * _pieceD;
	}
	
	//顺时针取椭圆点位，右边界和下边界
	private ArrayList<Point> getOvalDotArray(Piece piece, Place position) {
		int rnd = ((int)(Math.random()*10)%2 ==0) ? 1 : -1;
		ArrayList<Point> circleDotArray = new ArrayList<Point>();
		Point key = piece.getKey();
		
		switch(position){
		case Right:
			Point r0 = new Point(key.x + _pieceWidth, key.y);
			Point r1 = new Point(r0.x + getRndD(), r0.y + (_pieceHeight - _pieceOW)/2);
			Point r2 = new Point(r1.x + rnd * _pieceOH/2, r1.y - _pieceOW/4);
			Point r3 = new Point(r1.x + rnd * _pieceOH, r1.y);
			Point r4 = new Point(r3.x + rnd * _pieceOH/4, r3.y + _pieceOW/2);
			Point r5 = new Point(r3.x, r3.y + _pieceOW);
			Point r6 = new Point(r2.x, r5.y + _pieceOW/4);
			Point r7 = new Point(r1.x, r5.y);
			Point r8 = new Point(r0.x, r0.y + _pieceHeight);
			
			circleDotArray.add(r0);
			circleDotArray.add(r1);
			circleDotArray.add(r2);
			circleDotArray.add(r3);
			circleDotArray.add(r4);
			circleDotArray.add(r5);
			circleDotArray.add(r6);
			circleDotArray.add(r7);
			circleDotArray.add(r8);
			
			break;
	
		case Feet:
			Point f0 = new Point(key.x + _pieceWidth, key.y + _pieceHeight);
			Point f1 = new Point(f0.x - (_pieceWidth - _pieceOW)/2, f0.y + getRndD());
			Point f2 = new Point(f1.x + _pieceOW/4, f1.y + rnd * _pieceOH/2);
			Point f3 = new Point(f1.x, f1.y +  rnd * _pieceOH);
			Point f4 = new Point(f3.x - _pieceOW/2, f3.y + rnd * _pieceOH/4);
			Point f5 = new Point(f3.x - _pieceOW, f3.y);
			Point f6 = new Point(f5.x - _pieceOW/4, f2.y);
			Point f7 = new Point(f5.x, f1.y);
			Point f8 = new Point(f0.x - _pieceWidth, f0.y);
			
			circleDotArray.add(f0);
			circleDotArray.add(f1);
			circleDotArray.add(f2);
			circleDotArray.add(f3);
			circleDotArray.add(f4);
			circleDotArray.add(f5);
			circleDotArray.add(f6);
			circleDotArray.add(f7);
			circleDotArray.add(f8);
			break;
		}
		
		return circleDotArray;
	}
	
	private void setAllDotArray(Piece piece) {
		//ArrayList<Point> allDotArray = new ArrayList<Point>();
		//top,right,feet,left四面
		ArrayList<Point> top = new ArrayList<Point>();
		ArrayList<Point> right = new ArrayList<Point>();
		ArrayList<Point> feet = new ArrayList<Point>();
		ArrayList<Point> left = new ArrayList<Point>();
		
		Point id = piece.getId();
		Point key = piece.getKey();
		if(id.x == 0){
			//top边界为直线
			Point tp1 = new Point(key.x, key.y);
			Point tp2 = new Point(key.x + _pieceWidth, key.y);
			top.add(tp1);
			top.add(tp2);
		}else{  //top边界为曲线，则曲线点为上一块碎片的feet边界
			Piece tmpPiece = (Piece) allPiece.get(_line * (id.x - 1) + id.y);
			//Log.i("top", "top bian " + id.y + " " + id.y + " " + (_line * (id.x - 1) + id.y));
			ArrayList<Point> tmpFeet = tmpPiece.getApFeet();
			for(int i=tmpFeet.size()-1; i>=0; i--){
				top.add(tmpFeet.get(i));
			}
		}
		
		if (id.y == 0) {
			//left边界为直线
			Point lp1 = new Point(key.x, key.y + _pieceHeight);
			Point lp2 = new Point(key.x, key.y);
			left.add(lp1);
			left.add(lp2);
		} else {  //left边界为曲线，则曲线点为左边一块碎片的right边界
			Piece tmpPiece = (Piece) allPiece.get(_line * id.x + id.y - 1);
			//Log.i("left", "left bian " + id.y + " " + id.y + " " + (_line * id.x + id.y - 1));
			ArrayList<Point> tmpRight = tmpPiece.getApRight();
			for(int i=tmpRight.size()-1; i>=0; i--){
				left.add(tmpRight.get(i));
			}
		}
		
		if (id.x == _row-1) {
			//feet边界为直线
			Point fp1 = new Point(key.x + _pieceWidth, key.y + _pieceHeight);
			Point fp2 = new Point(key.x, key.y + _pieceHeight);
			feet.add(fp1);
			feet.add(fp2);
		} else {
			if (PIECE_CUT_FLAG == 1) feet = getOvalDotArray(piece, Place.Feet);
			else if (PIECE_CUT_FLAG == 2) feet = getKochDotArray(piece, Place.Feet);
			else if (PIECE_CUT_FLAG == 3) feet = getAotuDotArray(piece, Place.Feet);
			else feet = getRectDotArray(piece, Place.Feet);
		}
		
		if (id.y == _line-1) {
			//right边界为直线
			Point rp1 = new Point(key.x + _pieceWidth, key.y);
			Point rp2 = new Point(key.x + _pieceWidth, key.y + _pieceHeight);
			right.add(rp1);
			right.add(rp2);
		} else {
			if (PIECE_CUT_FLAG == 1) right = getOvalDotArray(piece, Place.Right);
			else if (PIECE_CUT_FLAG == 2) right = getKochDotArray(piece, Place.Right);
			else if (PIECE_CUT_FLAG == 3) right = getAotuDotArray(piece, Place.Right);
			else right = getRectDotArray(piece, Place.Right);
		}
		
		piece.setApTop(top);
		piece.setApRight(right);
		piece.setApFeet(feet);
		piece.setApLeft(left);
		
		allPiece.add(piece);

	}
	
	//得出碎片的左上角和右下角坐标点位
	private void setMinAndMaxPoint(Piece piece) {
		int minx = _imageWidth;
		int miny = _imageHeight;
		int maxx = 0;
		int maxy = 0;
		
		ArrayList<Point> left = piece.getApLeft();
		for (int i=0; i<left.size(); i++) {
			Point lp = (Point) left.get(i);
			if(lp.x < minx){
				minx = lp.x;
			}
		}
		
		ArrayList<Point> top = piece.getApTop();
		for (int i=0; i<top.size(); i++){
			Point tp = (Point) top.get(i);
			if(tp.y < miny){
				miny = tp.y;
			}
		}
		//Log.i("getMinAndMaxPoint", "min point: (" + minx + ", " + miny + ")");
		piece.setMinp(new Point(minx, miny));   // 左上角点位
		
		ArrayList<Point> right = piece.getApRight();
		for (int i=0; i<right.size(); i++) {
			Point rp = (Point) right.get(i);
			if (rp.x > maxx) {
				maxx = rp.x;
			}
		}
		
		ArrayList<Point> feet = piece.getApFeet();
		for (int i=0; i<feet.size(); i++) {
			Point fp = (Point) feet.get(i);
			if (fp.y > maxy) {
				maxy = fp.y;
			}
		}
		
		//Log.i("getMinAndMaxPoint", "max point: (" + maxx + ", " + maxy + ")");
		piece.setMaxp(new Point(maxx, maxy));   // 右下角点位
		piece.setPieceWidth(maxx-minx);
		piece.setPieceHeight(maxy-miny);
		
	}
	
	private int changeColorToLight(int color, double contrast, int light) {
		int red = Color.red(color);
		int green = Color.green(color);
		int blue = Color.blue(color);
		
		int r = (int) (red * contrast + light);
		int g = (int) (green * contrast + light);
		int b = (int) (blue * contrast + light);
		
		if (r > 255) {
			r = 255;
		} else if (r < 0) {
			r = 0;
		}
		
		if (g > 255) {
			g = 255;
		} else if (g < 0) {
			g = 0;
		}
		
		if (b > 255) {
			b = 255;
		} else if (b < 0) {
			b = 0;
		}
		
		return Color.rgb(r, g, b);
	}
	
	/**
	 * 给每个piece蒙版填充像素，得到拼图碎片piece
	 */
	private void fillPieceWithBitmap(Piece piece, Bitmap pieceBit) {
		//Bitmap pieceBit = piece.getBmpPiece();
		Point minp = piece.getMinp();
		Point maxp = piece.getMaxp();
		
		int w = maxp.x - minp.x;
		int h = maxp.y - minp.y;
		
		//拼图碎片的宽高
		int tpieceW = pieceBit.getWidth();
		int tpieceH = pieceBit.getHeight();
		
		int _d = (PIECE_CUT_FLAG != 1 && SHADOW_OFFSET > 1) ? 1 : SHADOW_OFFSET;
		_d = 0;
		
		//绘制碎片的边缘
		Bitmap pieceEdge = Bitmap.createBitmap(tpieceW, tpieceH, Config.ARGB_8888);
		canvas.setBitmap(pieceEdge);
		canvas.drawPath(dotPath, edgePaint);
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		
		for (int i = 0; i < w - 0; i++) {
			for (int j = 0; j < h - 0; j++) {
				try {
				if (pieceBit.getPixel(i + _d, j + _d) == Color.DKGRAY) {
					int mBitPicPixelColor = mBitmap.getPixel(minp.x + i, minp.y + j);
					
					if (pieceEdge.getPixel(i + _d, j + _d) == Color.LTGRAY) {
						mBitPicPixelColor = changeColorToLight(mBitPicPixelColor, 0.6, 80);
					}
					
					pieceBit.setPixel(i + _d, j + _d, mBitPicPixelColor);
				}
				} catch (Exception e) {
				}
				
			}
		}
        
		
	}
	
	//获取每块切片的图形
	private Point setPieceBitmap(Piece piece) {
		dotPath.reset();
		
		Point minp = piece.getMinp();
		Point maxp = piece.getMaxp();
		
		Point diff = minp;
		int w = maxp.x - minp.x;
		int h = maxp.y - minp.y;
		
		Point key = (Point) piece.getKey();
		dotPath.moveTo(key.x - diff.x, key.y - diff.y);
		
		ArrayList<Point> top = piece.getApTop();
		changeDotPath(top, dotPath, diff);
		
		ArrayList<Point> right = piece.getApRight();
		changeDotPath(right, dotPath, diff);
		
		ArrayList<Point> feet = piece.getApFeet();
		changeDotPath(feet, dotPath, diff);
		
		ArrayList<Point> left = piece.getApLeft();
		changeDotPath(left, dotPath, diff);
		
		
		int _d = (PIECE_CUT_FLAG != 1 && SHADOW_OFFSET > 1) ? 1 : SHADOW_OFFSET;
		_d = 0;
		dotPath.offset(_d, _d);
		
		//以百分比分配边宽度
		if (PIECE_EDGE_WIDTH > 1)
			edgePaint.setStrokeWidth(Math.min(_pieceWidth, _pieceHeight) / 2 * PIECE_EDGE_WIDTH / 100);
		
		/////根据碎片的大小，创建透明图片，在画布上每次绘制一个碎片，然后保存
		Bitmap pieceBitmap = Bitmap.createBitmap(w + _d*2, h + _d*2, Config.ARGB_8888);
		canvas.setBitmap(pieceBitmap);
		canvas.drawPath(dotPath, noPicPaint);
		
		if (RENDER_FLAG != 0) {
			PaintFlagsDrawFilter dfd = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);  
			canvas.setDrawFilter(dfd);
			canvas.clipPath(dotPath, Op.REPLACE);//使用路径剪切画布  
			canvas.drawBitmap(mBitmap, new Rect(minp.x, minp.y, maxp.x, maxp.y), new Rect(_d, _d, w + _d, h + _d), noPicPaint); 
			canvas.drawPath(dotPath, edgePaint);
			
		}

		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		
		if (RENDER_FLAG == 0) {
			fillPieceWithBitmap(piece, pieceBitmap);
		}
		
		piece.setBmpPiece(pieceBitmap);
		piece.setPieceHeight(pieceBitmap.getHeight());
		piece.setPieceWidth(pieceBitmap.getWidth());
		
		
		return diff;   //返回点位相差距离
		
	}
	
	/**
	 * 根据minp点，将绝对点位转化为相对点位
	 * @param dotList
	 * @param dotPath
	 * @param diff
	 */
	private void changeDotPath(ArrayList<Point> dotList, Path dotPath, Point diff) {
		int len = dotList.size();
		float cx, cy;
		ArrayList<Point> tempDot = dotList;
		
		for (int i = 0; i < len; i++) {
			if(tempDot.size() == 2) {
				Point p0 = (Point)tempDot.get(i);
				dotPath.lineTo(p0.x-diff.x, p0.y-diff.y);
			} else if (i + 1 < tempDot.size()) {
				Point p0 = (Point) tempDot.get(i);
				Point p1 = (Point) tempDot.get(i+1);
				if (QUAD_WAY) {
					if (i + 1 == tempDot.size() - 1) {
						//直接连接终点
						dotPath.quadTo(p0.x-diff.x, p0.y-diff.y, p1.x-diff.x, p1.y-diff.y);
					} else {
						//以中间为控制点画曲线
						cx = (p0.x + p1.x) / 2;
			            cy = (p0.y + p1.y) / 2;
			            dotPath.quadTo(p0.x-diff.x, p0.y-diff.y, cx-diff.x, cy-diff.y);
					}
				} else {
					dotPath.quadTo(p0.x-diff.x, p0.y-diff.y, p1.x-diff.x, p1.y-diff.y);
				}
			}
		}
		
	}
	
	
	public Vector<Piece> getAllPiece() {
		return allPiece;
	}

	public void setAllPiece(Vector<Piece> allPiece) {
		this.allPiece = allPiece;
	}
	
	public void setPintuValue(PinkToru app) {
		PIECE_CUT_FLAG = app.getPieceCutFlag();
		SHADOW_OFFSET = app.getPieceShadowOffset();
		RENDER_FLAG = app.getPieceRenderFlag();
		PIECE_EDGE_WIDTH = app.getPieceEdgeWidth();
		KOCH_CURVE_N = app.getPieceKochCurveN();
		QUAD_WAY = app.isWithquad();
	}
	
	public void setPieceCutFlag(int flag) {
		PIECE_CUT_FLAG = flag;
	}
	
	public void setShadowOffset(int offset) {
		SHADOW_OFFSET = offset;
	}
	
	public void setShadowColor(int color) {
		SHADOW_COLOR = color;
	}
	
	public void setRenderFlag(int flag) {
		RENDER_FLAG = flag;
	}
	
	public void setPieceEdgeWidth(int width) {
		PIECE_EDGE_WIDTH = width;
	}

	public void setKochCurveN(int n) {
		KOCH_CURVE_N = n;
	}
	
	public void setQuadWay(boolean q) {
		QUAD_WAY = q;
	}
}

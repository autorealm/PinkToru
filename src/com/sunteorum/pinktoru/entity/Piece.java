package com.sunteorum.pinktoru.entity;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Path;
import android.graphics.Point;

public class Piece {
	private Point id;  //��¼����ƴͼʱ�ĵ�λ
	private Point key; //���ڼ�¼ÿ��ƴͼ�����ĵ㣬�����ж�ʹ��
	private Point minp;  //���Ͻǵĵ�λ
	private Point maxp;  //���½ǵĵ�λ
	
	private int offset;  //ͼƬƫ��
	
	private int lineWidth;  //��������͹�Ŀ��
	private int rowHeight;  //��������͹�ĸ߶�
	
	private int pieceWidth;  //������͹�Ŀ��
	private int pieceHeight;  //������͹�ĸ߶�
	
	private ArrayList<Point> apTop = new ArrayList<Point>(4);
	private ArrayList<Point> apRight = new ArrayList<Point>(4);
	private ArrayList<Point> apFeet = new ArrayList<Point>(4);
	private ArrayList<Point> apLeft = new ArrayList<Point>(4);

	private Bitmap bmpPiece;
	private Bitmap obmPiece;
	
	private Path piecePath; //��ƬԴͼ·��
	
	private int x = 0, y = 0;
	private PieceGroup group = null;
	
	public Point getId() {
		return id;
	}

	public void setId(Point id) {
		this.id = id;
	}

	public Point getKey() {
		return key;
	}

	public void setKey(Point key) {
		this.key = key;
	}

	public Point getMinp() {
		return minp;
	}

	public void setMinp(Point minp) {
		this.minp = minp;
	}

	public Point getMaxp() {
		return maxp;
	}

	public void setMaxp(Point maxp) {
		this.maxp = maxp;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getLineWidth() {
		return lineWidth;
	}

	public void setLineWidth(int lineWidth) {
		this.lineWidth = lineWidth;
	}

	public int getRowHeight() {
		return rowHeight;
	}

	public void setRowHeight(int rowHeight) {
		this.rowHeight = rowHeight;
	}

	public int getPieceWidth() {
		return pieceWidth;
	}

	public void setPieceWidth(int pieceWidth) {
		this.pieceWidth = pieceWidth;
	}

	public int getPieceHeight() {
		return pieceHeight;
	}

	public void setPieceHeight(int pieceHeight) {
		this.pieceHeight = pieceHeight;
	}

	public ArrayList<Point> getApTop() {
		return apTop;
	}

	public void setApTop(ArrayList<Point> apTop) {
		this.apTop = apTop;
	}

	public ArrayList<Point> getApRight() {
		return apRight;
	}

	public void setApRight(ArrayList<Point> apRight) {
		this.apRight = apRight;
	}

	public ArrayList<Point> getApFeet() {
		return apFeet;
	}

	public void setApFeet(ArrayList<Point> apFeet) {
		this.apFeet = apFeet;
	}

	public ArrayList<Point> getApLeft() {
		return apLeft;
	}

	public void setApLeft(ArrayList<Point> apLeft) {
		this.apLeft = apLeft;
	}

	public Bitmap getBmpPiece() {
		return bmpPiece;
	}

	public void setBmpPiece(Bitmap bmpPiece) {
		this.bmpPiece = bmpPiece;
	}

	public Bitmap getObmPiece() {
		return obmPiece;
	}

	public void setObmPiece(Bitmap bmp) {
		this.obmPiece = bmp;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public PieceGroup getGroup() {
		return group;
	}

	public void setGroup(PieceGroup group) {
		this.group = group;
	}

	public Path getPiecePath() {
		return piecePath;
	}

	public void setPiecePath(Path piecePath) {
		this.piecePath = piecePath;
	}
	
	
}

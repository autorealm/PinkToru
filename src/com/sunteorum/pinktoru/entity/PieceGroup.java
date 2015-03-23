package com.sunteorum.pinktoru.entity;

import java.util.HashSet;

public class PieceGroup {

	private static int idSource = 0;
	private final int serial = ++idSource;

	private HashSet<Piece> group = new HashSet<Piece>();

	public PieceGroup(Piece p) {
		group.add(p);
	}
	
	public void addPiece(Piece piece) {
		group.add(piece);
		piece.setGroup(this);
	}

	public void translate(int x, int y) {
		for (Piece p : group) {
			int xD = p.getX() - x;
			int yD = p.getY() - y;

			p.setX(xD);
			p.setY(yD);
		}
	}

	public void addGroup(PieceGroup oldGroup) {
		for (Piece p : oldGroup.getGroup()) {
			this.addPiece(p);
		}
	}

	public HashSet<Piece> getGroup() {
		return group;
	}

	public boolean sameGroup(Piece a, Piece b) {
		return a.getGroup().getSerial() == b.getGroup().getSerial();
	}

	public int getSerial() {
		return serial;
	}
	
}

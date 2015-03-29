package com.sunteorum.pinktoru;

import java.util.Vector;

import com.sunteorum.pinktoru.entity.Piece;
import com.sunteorum.pinktoru.view.PieceView;

public interface IPintuGame {
	
	/**
	 * ��ʼ����setContentView �������ݽ��棬findViewById �ҳ��ؼ�
	 */
	void init();
	
	/**
	 * ���ڼ�����Ϸ�����з�ͼ����
	 * @param pieces
	 */
	void onNewGame(final Vector<Piece> pieces);

	/**
	 * �Ѵ���һ��Ƭ
	 * @param pv
	 * @param index
	 */
	void OnCreatePiece(PieceView pv, int index);
	
	/**
	 * ��Ϸ��ȫ�������ʱ
	 */
	void onStartGame();
	
	/**
	 * ƴͼʧ��ʱ����ת���ɼ�����
	 */
	void onFailed();
	
	/**
	 * ƴͼ���ʱ����ת���ɼ�����
	 */
	void onCompleted();
	
}

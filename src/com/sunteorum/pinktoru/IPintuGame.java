package com.sunteorum.pinktoru;

import java.util.Vector;

import com.sunteorum.pinktoru.entity.Piece;
import com.sunteorum.pinktoru.view.PieceView;

public interface IPintuGame {
	
	/**
	 * 初始化，setContentView 设置内容界面，findViewById 找出控件
	 */
	void init();
	
	/**
	 * 正在加载游戏，进行分图处理
	 * @param pieces
	 */
	void onNewGame(final Vector<Piece> pieces);

	/**
	 * 已创建一碎片
	 * @param pv
	 * @param index
	 */
	void OnCreatePiece(PieceView pv, int index);
	
	/**
	 * 游戏完全加载完成时
	 */
	void onStartGame();
	
	/**
	 * 拼图失败时，跳转到成绩界面
	 */
	void onFailed();
	
	/**
	 * 拼图完成时，跳转到成绩界面
	 */
	void onCompleted();
	
}

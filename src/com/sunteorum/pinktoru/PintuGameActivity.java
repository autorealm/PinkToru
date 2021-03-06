package com.sunteorum.pinktoru;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sunteorum.pinktoru.entity.Piece;
import com.sunteorum.pinktoru.inc.ColorThief;
import com.sunteorum.pinktoru.util.ImageUtils;
import com.sunteorum.pinktoru.view.MultiDirectionSlidingDrawer;
import com.sunteorum.pinktoru.view.PieceView;
import com.sunteorum.pinktoru.view.RippleView;

public class PintuGameActivity extends BaseGameActivity {

	ArrayList<PieceView> movePieces = new ArrayList<PieceView>();
	int lastX;
	int lastY;
	int lastId = 0;
	int dsts = 0;
	boolean duoMove = false;
	boolean skip = false;
	boolean trainmove = true;
	boolean hasc = false;
	
	RippleView rv_space;
	
	@Override
	public boolean onTouch(final View v, MotionEvent event) {
		if (!(v instanceof PieceView)) return true;
		
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			
			if (lastId == 0)  lastId = v.getId();
			
			PieceView pv = (PieceView) v;
			Bitmap bmp = pv.getDrawingCache();
			
			if (bmp != null && bmp.getPixel((int)event.getX(), (int)event.getY()) == 0) {
				skip = true;
			} else skip = false;
			
			//安卓3.0之前的版本不传递Touch事件?
			//System.out.println("Touch Down - ID : " + lastId + " | " + skip);
			if (android.os.Build.VERSION.SDK_INT < 11) skip = false;
			
			if (skip) return false;
			
			lastX = (int) event.getRawX();
			lastY = (int) event.getRawY();
			
			//把该视图置于其他所有子视图之上
			//space.bringChildToFront(v);
			displayFront((PieceView)v);
			v.requestFocus();
			
			//如果开始了顶部栏则关闭它
			if (drawer != null && drawer.isOpened()) drawer.animateClose();
			
			break;
		case MotionEvent.ACTION_MOVE:
			int dx = 0, dy = 0;
			
			if (skip == true) return false;
			
			dx =(int) event.getRawX() - lastX;
			dy =(int) event.getRawY() - lastY;
			
			dsts += (Math.abs(dx) + Math.abs(dy)) / 2;
			
			movePieces.clear();
			checkMove((PieceView)v, dx, dy, movePieces);
			if (trainmove) setOpacity(movePieces);
			
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					
					moveSomePieces(movePieces);
					//movePieces.clear();   //重置移动的标志，清空可移动记录
					cleanPath();
					if (app.isAbsinmove()) {
						PieceView firstPiece = checkAbsorb((PieceView)v);
						cleanPath();
						absorb(firstPiece);
					} else absorb((PieceView)v);
					
					displayLast(stage);
				}
				
			});
			
			lastX = (int) event.getRawX();
			lastY = (int) event.getRawY();
			
			break;
		case MotionEvent.ACTION_UP:
			lastId = 0;
			
			if (skip == true) return false;
			
			//先取得碎片吸附的路径，然后移动碎片
			cleanPath();
			
			PieceView firstPiece = checkAbsorb((PieceView)v);
			cleanPath();
			absorb(firstPiece);
			
			displayLast(stage);
			//吸附后，显示到前端
			//displayFront(firstPiece);
			
			if (trainmove) setOpacity(allPieces);
			
			game_status = " " + dsts + " ";
			setGameStatus();
			
			//判断是否完成
			hasComplete();
			
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			duoMove = true;
			
			return false;
			
		case MotionEvent.ACTION_POINTER_UP:
			duoMove = false;
			
			return false;
			
		}

		return true;
	
	}

	@SuppressWarnings("deprecation")
	@Override
	public void init() {
		puzzle = (FrameLayout) View.inflate(this, R.layout.activity_game, null);
		
		setContentView(puzzle);
		
		space = (FrameLayout) findViewById(R.id.space);
		ipin = (android.widget.ImageView) findViewById(R.id.ipin);
		layGameStatus = (LinearLayout) this.findViewById(R.id.layGameStatus);
		drawer = (MultiDirectionSlidingDrawer) this.findViewById(R.id.drawer);
		handle = (View) this.findViewById(R.id.handle);
		tvGameLevel = ((TextView) findViewById(R.id.tvGameLevel));
		tvGameTime = ((TextView) findViewById(R.id.tvGameTime));
		tvGameStatus = ((TextView) findViewById(R.id.tvGameStatus));
		
		handle.setBackgroundResource(R.drawable.backgroud_2);
		puzzle.setBackgroundDrawable(background_drawalbe);
		puzzle.setKeepScreenOn(app.isKeepon());
		
		trainmove = app.isTrainmove();
		
		rv_space = (RippleView) findViewById(R.id.rv_space);
		
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStartGame() {
		
		//将背景模糊处理
		Drawable dg = puzzle.getBackground();
		Bitmap blurbg = ImageUtils.fastBlur(ImageUtils.DrawableToBitmap(dg), 10);
		if (blurbg != null) dg = ImageUtils.BitmapToDrawable(this, blurbg);
		if (dg != null) {
			dg.setAlpha(160);
			rv_space.setBackgroundDrawable(dg);
			//puzzle.getBackground().setAlpha(120);
		}
		
		int themeColor = Color.WHITE;
		try {
			List<int[]> result = ColorThief.compute(ImageUtils.DrawableToBitmap(dg), 3);
			int[] dc = result.get(2);
			themeColor = Color.rgb((int) (dc[0]), (int) (dc[1]), (int) (dc[2]));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		rv_space.setRippleColor(themeColor);
		
		puzzle.setBackgroundColor(Color.DKGRAY);
		//puzzle.getChildAt(0).setVisibility(0);
		
		Animation anim = AnimationUtils.loadAnimation(this, R.anim.show_2);
		LayoutAnimationController lac = new LayoutAnimationController(anim);
		
		lac.setOrder(LayoutAnimationController.ORDER_RANDOM);
		lac.setDelay(1);
		
		//space.setLayoutAnimation(lac);
		
		//将碎片可视化
		int piececount = allPieces.size();
		for (int i = 0; i < piececount; i++) {
			PieceView pv = (PieceView) allPieces.get(i);
			pv.setVisibility(0);
			
		}
		
	}

	@Override
	public void onNewGame(Vector<Piece> pieces) {
		// TODO Auto-generated method stub

	}


	@Override
	public void OnCreatePiece(PieceView pv, int index) {
		//pv.setPadding(4, 4, 4, 4);
		pv.setVisibility(8);
		
		space.addView(pv);
		
	}

	@Override
	public void onFailed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCompleted() {
		// TODO Auto-generated method stub
		super.onCompleted();
		
	}

	/**
	 * 使碎片前置
	 * @param curPiece
	 */
	private void displayFront(PieceView curPiece) {
		space.bringChildToFront(curPiece);   //把该视图置于其他所有子视图之上
		curPiece.postInvalidate();
		
		int id = curPiece.getId();
		int curRow = id / line;
		int curLine = id % line;
		
		//top
		if (curPiece.isHasTop()) {
			PieceView topPiece = (PieceView) allPieces.get((curRow - 1) * line + curLine);
			if (!topPiece.isTraverse()) {
				topPiece.setTraverse(true);
				displayFront(topPiece);
			}
			
		}
		
		//right
		if (curPiece.isHasRight()) {
			PieceView rightPiece = (PieceView) allPieces.get(id + 1);
			if (!rightPiece.isTraverse()) {
				rightPiece.setTraverse(true);
				displayFront(rightPiece);
			}
			
		}
		
		//feet
		if (curPiece.isHasFeet()) {
			PieceView feetPiece = (PieceView) allPieces.get((curRow + 1) * line + curLine);
			if (!feetPiece.isTraverse()) {
				feetPiece.setTraverse(true);
				displayFront(feetPiece);
			}
			
		}
		
		//left
		if (curPiece.isHasLeft()) {
			PieceView leftPiece = (PieceView) allPieces.get(id - 1);
			if (!leftPiece.isTraverse()) {
				leftPiece.setTraverse(true);
				displayFront(leftPiece);
			}
			
		}
		
	}

	/**
	 * 判断需一起移动的碎片，会循环调用
	 * @param curPIB
	 * @param dx
	 * @param dy
	 * @param movePieces
	 */
	private void checkMove(PieceView curPIB, int dx, int dy, ArrayList<PieceView> movePieces) {
		int l = curPIB.getLeft() + dx;
		int t = curPIB.getTop() + dy;
		curPIB.setLocation(new Point(l, t));

		/*
		if (l < 0) {
			l = 0;
			r = l + curPIB.getWidth();
		}
		if (r > screenWidth) {
			r = screenWidth;
			l = r - curPIB.getWidth();
		}
		if (t < 0) {
			t = 0;
			b = t + curPIB.getHeight();
		}
		if (b > screenHeight) {
			b = screenHeight;
			t = b - curPIB.getHeight();
		}
		
		curPIB.layout(l, t, r, b);*/
		
		movePieces.add(curPIB);
		
		int id = curPIB.getId();
		int curRow = id / line;
		int curLine = id % line;
		
		//top
		if (curPIB.isHasTop()) {
			PieceView topPIB = (PieceView) allPieces.get((curRow - 1) * line + curLine);
			if (!topPIB.isTraverse()) {
				topPIB.setTraverse(true);
				checkMove(topPIB, dx, dy, movePieces);
			}
			
		}
		
		//right
		if (curPIB.isHasRight()) {
			PieceView rightPIB = (PieceView) allPieces.get(id + 1);
			if (!rightPIB.isTraverse()) {
				rightPIB.setTraverse(true);
				checkMove(rightPIB, dx, dy, movePieces);
			}
			
		}
		
		//feet
		if (curPIB.isHasFeet()) {
			PieceView feetPIB = (PieceView) allPieces.get((curRow + 1) * line + curLine);
			if (!feetPIB.isTraverse()) {
				feetPIB.setTraverse(true);
				checkMove(feetPIB, dx, dy, movePieces);
			}
			
		}
		
		//left
		if (curPIB.isHasLeft()) {
			PieceView leftPIB = (PieceView) allPieces.get(id - 1);
			if (!leftPIB.isTraverse()) {
				leftPIB.setTraverse(true);
				checkMove(leftPIB, dx, dy, movePieces);
			}
			
		}
		
	}

	/**
	 * 一起移动碎片
	 * @param absorbPieces
	 */
	private void moveSomePieces(final ArrayList<PieceView> absorbPieces) {
		
		for (int i = 0; i < absorbPieces.size(); i++) {
			
			PieceView piece = (PieceView) absorbPieces.get(i);
			Point loc = piece.getLocation();
			
			if (piece.getParent() instanceof FrameLayout) {
				FrameLayout.LayoutParams alp = (FrameLayout.LayoutParams) piece.getLayoutParams();
				alp.gravity = android.view.Gravity.TOP|android.view.Gravity.LEFT;
				alp.leftMargin = loc.x;
				alp.topMargin = loc.y;
				alp.width = piece.getWidth();
				alp.height = piece.getHeight();
				
				piece.setLayoutParams(alp);
			}
			
			piece.layout(loc.x, loc.y, loc.x + piece.getWidth(), loc.y + piece.getHeight());
			
			piece.postInvalidate();
		}
		
	}

	/**
	 * 拼合碎片
	 * @param curPiece
	 */
	private void absorb(PieceView curPiece) {
		Point curMinp = curPiece.getMinp();
		Point curLoc = curPiece.getLocation();
		
		if (curPiece.getParent() instanceof FrameLayout) {
			FrameLayout.LayoutParams alp = (FrameLayout.LayoutParams) curPiece.getLayoutParams();
			alp.gravity = android.view.Gravity.TOP|android.view.Gravity.LEFT;
			alp.leftMargin = curLoc.x;
			alp.topMargin = curLoc.y;
			alp.width = curPiece.getWidth();
			alp.height = curPiece.getHeight();
			
			curPiece.setLayoutParams(alp);
		}
		
		curPiece.layout(curLoc.x, curLoc.y, curLoc.x + curPiece.getWidth(),
				curLoc.y + curPiece.getHeight());
		
		curPiece.postInvalidate();
		space.bringChildToFront(curPiece);   //把该视图置于其他所有子视图之上
		
		int id = curPiece.getId();
		int curRow = id / line;
		int curLine = id % line;
		
		//top
		if (curPiece.isHasTop()) {
			PieceView topPiece = (PieceView) allPieces.get((curRow - 1) * line + curLine);
			if (!topPiece.isTraverse()) {
				Point topMinp = topPiece.getMinp();
				topPiece.setLocation(new Point(curLoc.x + (topMinp.x - curMinp.x),
						curLoc.y + (topMinp.y - curMinp.y)));
				topPiece.setTraverse(true);
				absorb(topPiece);
			}
			
		}
		
		//right
		if (curPiece.isHasRight()) {
			PieceView rightPiece = (PieceView) allPieces.get(id + 1);
			if (!rightPiece.isTraverse()) {
				Point rightMinp = rightPiece.getMinp();
				rightPiece.setLocation(new Point(curLoc.x + (rightMinp.x - curMinp.x),
						curLoc.y + (rightMinp.y - curMinp.y)));
				rightPiece.setTraverse(true);
				absorb(rightPiece);
			}
			
		}
		
		//feet
		if (curPiece.isHasFeet()) {
			PieceView feetPiece = (PieceView) allPieces.get((curRow + 1) * line + curLine);
			if (!feetPiece.isTraverse()) {
				Point feetMinp = feetPiece.getMinp();
				feetPiece.setLocation(new Point(curLoc.x + (feetMinp.x - curMinp.x),
						curLoc.y + (feetMinp.y - curMinp.y)));
				feetPiece.setTraverse(true);
				absorb(feetPiece);
			}
			
		}
		
		//left
		if (curPiece.isHasLeft()) {
			PieceView leftPiece = (PieceView) allPieces.get(id - 1);
			if (!leftPiece.isTraverse()) {
				Point leftMinp = leftPiece.getMinp();
				leftPiece.setLocation(new Point(curLoc.x + (leftMinp.x - curMinp.x),
						curLoc.y + (leftMinp.y - curMinp.y)));
				leftPiece.setTraverse(true);
				absorb(leftPiece);
			}
			
		}
		
	}

	/**
	 * 检查可以拼合的碎片
	 * @param v 当前的碎片
	 * @return 首个可以拼合的碎片
	 */
	private PieceView checkAbsorb(PieceView v) {
		PieceView firstPiece = null;
		
		PieceView curPiece = (PieceView) v;
		curPiece.setTraverse(true);
		
		int curId = curPiece.getId();
		int curRow = curId / line;
		int curLine = curId % line;
		Point curMinp = curPiece.getMinp();
		Point curLoc = curPiece.getLocation();
		
		//从top，right，feet，left开始遍历，设置吸附标志

		//top
		if (curRow > 0) {   //当前碎片存在上面的碎片
			int topPieceId = (curRow - 1) * line + curLine;
			if (!curPiece.isHasTop()) {  //如果上面的碎片还未吸附
				//如果存在上面的碎片，还没有碰撞，则得到上面碎片的位置判断是否吸附
				PieceView topPiece = (PieceView) allPieces.get(topPieceId);
				Point topLoc = topPiece.getLocation();
				Point topMinp = topPiece.getMinp();
				
				//如果吸附条件成立，则吸附
				if (distance(curMinp, topMinp, curLoc, topLoc, INACCURACY)) {
					curPiece.setHasTop(true);
					topPiece.setHasFeet(true);
					if (firstPiece == null) {
						firstPiece = topPiece;
					}

				}
			} else {  //如果上面的碎片已经吸附,且不是搜索的来源（避免死循环）,则继续上面的碎片查找
				PieceView topPiece = (PieceView) allPieces.get(topPieceId);
				if (!topPiece.isTraverse()) {
					checkAbsorb(topPiece);
				}

			}
		}
		
		//right
		if (curLine < (line -1)) {  //当前碎片存在右面的碎片
			int rightPieceId = curId + 1;
			if (!curPiece.isHasRight()) {  //如果右面的碎片还为吸附
				//如果存在右面的碎片，还没有碰撞，则得到右面碎片的位置判断是否吸附
				PieceView rightPiece = (PieceView) allPieces.get(rightPieceId);
				Point rightLoc = rightPiece.getLocation();
				Point rightMinp = rightPiece.getMinp();

				//如果吸附条件成立，则吸附
				if (distance(curMinp, rightMinp, curLoc, rightLoc, INACCURACY)) {
					curPiece.setHasRight(true);
					rightPiece.setHasLeft(true);
					if (firstPiece == null) {
						firstPiece = rightPiece;
					}
					
				}
			} else {
				PieceView rightPiece = (PieceView) allPieces.get(rightPieceId);
				if (!rightPiece.isTraverse()) {
					checkAbsorb(rightPiece);
				}

			}
		}
		
		//feet
		if (curRow < (row - 1)) {
			int feetPieceId = (curRow + 1) * line + curLine;
			if (!curPiece.isHasFeet()) {
				//如果存在右面的碎片，还没有碰撞，则得到右面碎片的位置判断是否吸附
				PieceView feetPiece = (PieceView) allPieces.get(feetPieceId);
				Point feetLoc = feetPiece.getLocation();
				Point feetMinp = feetPiece.getMinp();
				
				//如果吸附条件成立，则吸附
				if (distance(curMinp, feetMinp, curLoc, feetLoc, INACCURACY)) {
					curPiece.setHasFeet(true);
					feetPiece.setHasTop(true);
					if (firstPiece == null) {
						firstPiece = feetPiece;
					}
					
				}
			} else {
				PieceView feetPiece = (PieceView) allPieces.get(feetPieceId);
				if (!feetPiece.isTraverse()) {
					checkAbsorb(feetPiece);
				}
			}

		}

		//left
		if (curLine > 0) {
			int leftPieceId = curId - 1;
			if (!curPiece.isHasLeft()) {
				//如果存在右面的碎片，还没有碰撞，则得到右面碎片的位置判断是否吸附
				PieceView leftPiece = (PieceView) allPieces.get(leftPieceId);
				Point leftLoc = leftPiece.getLocation();
				Point leftMinp = leftPiece.getMinp();
				
				//如果吸附条件成立，则吸附
				if (distance(curMinp, leftMinp, curLoc, leftLoc, INACCURACY)) {
					curPiece.setHasLeft(true);
					leftPiece.setHasRight(true);
					if (firstPiece == null) {
						firstPiece = leftPiece;
					}
					
				}
			} else {
				PieceView leftPiece = (PieceView) allPieces.get(leftPieceId);
				if (!leftPiece.isTraverse()) {
					checkAbsorb(leftPiece);
				}
			}

		}
		
		if (firstPiece == null) {
			firstPiece = v;
		}
		
		return firstPiece;
		
	}

	/**
	 * 前置显示未拼合的碎片
	 * @param num 未拼合的碎片数量
	 */
	private void displayLast(int num) {
		int last = row * line;
		for (int i = 0; i < allPieces.size(); i++) {
			PieceView piece = (PieceView) allPieces.get(i);
			if (piece.isTraverse()) {
				
				last--;
			} else {
				
			}
		}
		if (last <= Math.round(row * line * 1/5)) {
			for (int i = 0; i < allPieces.size(); i++) {
				PieceView piece = (PieceView) allPieces.get(i);
				if (!piece.isTraverse()) {
					space.bringChildToFront(piece);   //把该视图置于其他所有子视图之上
					piece.postInvalidate();
				}
				
			}
		
		}
	}

	@SuppressLint("NewApi")
	private void setOpacity(ArrayList<PieceView> pieces) {
		
		for (int i=0; i<allPieces.size(); i++) {
			PieceView piece = (PieceView) allPieces.get(i);
			if (pieces.contains(piece) || piece.isTraverse()) {
				if (android.os.Build.VERSION.SDK_INT < 11)
					piece.setPieceAlpha(255); //piece.setImageAlpha(255);
				else piece.setAlpha(1f);
			} else {
				if (android.os.Build.VERSION.SDK_INT < 11)
					piece.setPieceAlpha(200); //piece.setImageAlpha(200);
				else piece.setAlpha(0.92f);
			}
			
		}
	}


}

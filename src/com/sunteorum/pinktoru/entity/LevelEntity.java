package com.sunteorum.pinktoru.entity;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 游戏关卡数据包装类
 * @author KYO
 *
 */
public class LevelEntity implements Parcelable {

	private int levelId;//游戏关卡ID
	private int gameMode = 1;//游戏模式，0.连图，1.拼图，2.填图，3.组图。
	private int targetValue = 0;//游戏关卡目标分值
	private int giftPoints = 0;//游戏关卡赠送点数
	private int pieceRow;//列
	private int pieceLine;//行
	
	private int cutFlag = 0;//切图方式
	private int cutAlt = 2;//切图参数
	private int renderFlag = 1;//碎片渲染方式
	private int edgeWidth = 16;//碎片边缘宽度
	private int shadowOffset = 3;//碎片阴影
	
	private boolean withQuad = true;//曲线绘图
	private boolean absInMove = true;//移动拼接
	
	private int imageId = 0;//游戏图片的ID
	private String imageUrl;//游戏图片的URL
	private String addData;//游戏关卡附加数据
	private String levelDesc = "该游戏关卡暂无介绍";//游戏关卡介绍
	
	public LevelEntity() {
		super();
		
	}
	
	public LevelEntity(int id, int row, int line, String uri) {
		//游戏ID分类：0为预置，1～100为自定义，1000以上为下载内容
		this.levelId = 0;
		this.imageId = 0;
		this.imageUrl = uri;
		this.levelId = id;
		this.pieceRow = row;
		this.pieceLine = line;
		this.gameMode = 0;
		this.levelDesc = "";
		
	}
	
	/**
	 * 自定义游戏关卡重载
	 * @param id
	 * @param mode
	 * @param row
	 * @param line
	 * @param path
	 * @param desc
	 */
	public LevelEntity(int id, int mode, int row, int line, String path, String desc) {
		if (id < 1 || id > 100) id = 1;
		this.levelId = id;
		this.imageId = 0;
		this.imageUrl = path;
		this.pieceRow = row;
		this.pieceLine = line;
		this.gameMode = mode;
		this.levelDesc = desc;
		
	}
	
	public LevelEntity(String lvJson) {
		try {
			JSONObject jso = new JSONObject(lvJson);
			int id = jso.getInt("id");
			if (id < 101) id = 101;
			this.levelId = id;
			this.imageId = jso.has("image_id")?jso.getInt("image_id"):0;
			this.imageUrl = jso.getString("image_url");
			this.pieceRow = jso.has("row")?jso.getInt("row"):3;
			this.pieceLine = jso.has("line")?jso.getInt("line"):3;
			this.gameMode =  jso.has("mode")?jso.getInt("mode"):1;
			if (jso.has("desc")) this.levelDesc = jso.getString("desc");
			if (jso.has("add_data")) this.addData = jso.getString("add_data");
			if (jso.has("target")) this.targetValue = jso.getInt("target");
			if (jso.has("gift_pts")) this.giftPoints = jso.getInt("gift_pts");
			
			if (jso.has("cut_alt")) this.cutAlt = jso.getInt("cut_alt");
			if (jso.has("cut_flag")) this.cutFlag = jso.getInt("cut_flag");
			if (jso.has("render_flag")) this.renderFlag = jso.getInt("render_flag");
			if (jso.has("edge_width")) this.edgeWidth = jso.getInt("edge_width");
			if (jso.has("shadow_offset")) this.shadowOffset = jso.getInt("shadow_offset");
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}

	public String toJSONString() {
		try {
			JSONObject jso = new JSONObject();
			jso.put("id", this.levelId);
			jso.put("image_id", this.imageId);
			jso.put("image_url", this.imageUrl);
			jso.put("target", this.targetValue);
			jso.put("gift_pts", this.giftPoints);
			jso.put("row", this.pieceRow);
			jso.put("line", this.pieceLine);
			jso.put("mode", this.gameMode);
			
			jso.put("cut_alt", this.cutAlt);
			jso.put("cut_flag", this.cutFlag);
			jso.put("render_flag", this.renderFlag);
			jso.put("edge_width", this.edgeWidth);
			jso.put("shadow_offset", this.shadowOffset);
			jso.put("add_data", this.addData);
			
			jso.put("desc", this.levelDesc);
			
			return jso.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return this.toString();
	}
	
	public void setExtParams(String extParams) {
		
	}
	
	public String getExtParams() {
		String extParams;
		
		extParams = "";
		
		return extParams;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel p, int flag) {
		p.writeInt(levelId);
		p.writeInt(gameMode);
		p.writeInt(targetValue);
		p.writeInt(giftPoints);
		p.writeInt(pieceRow);
		p.writeInt(pieceLine);
		p.writeInt(imageId);
		
		p.writeInt(cutAlt);
		p.writeInt(cutFlag);
		p.writeInt(renderFlag);
		p.writeInt(edgeWidth);
		p.writeInt(shadowOffset);
		p.writeInt(withQuad ? 1 : 0);
		p.writeInt(absInMove ? 1 : 0);
		
		p.writeString(imageUrl);
		p.writeString(addData);
		p.writeString(levelDesc);
		
	}
	
	public static final Parcelable.Creator<LevelEntity> CREATOR = new Parcelable.Creator<LevelEntity>() {

		@Override
		public LevelEntity createFromParcel(Parcel p) {
			LevelEntity pe = new LevelEntity();
			
			pe.levelId = p.readInt();
			pe.gameMode = p.readInt();
			pe.targetValue = p.readInt();
			pe.giftPoints = p.readInt();
			pe.pieceRow = p.readInt();
			pe.pieceLine = p.readInt();
			pe.imageId = p.readInt();
			
			pe.cutAlt = p.readInt();
			pe.cutFlag = p.readInt();
			pe.renderFlag = p.readInt();
			pe.edgeWidth = p.readInt();
			pe.shadowOffset = p.readInt();
			
			pe.withQuad = (p.readInt() == 0)? false : true;
			pe.absInMove = (p.readInt() == 0)? false : true;
			
			pe.imageUrl = p.readString();
			pe.addData = p.readString();
			pe.levelDesc = p.readString();
			
			return pe;
		}

		@Override
		public LevelEntity[] newArray(int n) {
			// TODO Auto-generated method stub
			return new LevelEntity[n];
		}
		
	};

	public int getLevelId() {
		return levelId;
	}

	public void setLevelId(int levelId) {
		this.levelId = levelId;
	}

	public int getGameMode() {
		return gameMode;
	}

	public void setGameMode(int gameMode) {
		this.gameMode = gameMode;
	}

	public int getTargetValue() {
		return targetValue;
	}

	public void setTargetValue(int targetValue) {
		this.targetValue = targetValue;
	}

	public int getGiftPoints() {
		return giftPoints;
	}

	public void setGiftPoints(int giftPoints) {
		this.giftPoints = giftPoints;
	}

	public int getPieceRow() {
		return pieceRow;
	}

	public void setPieceRow(int pieceRow) {
		this.pieceRow = pieceRow;
	}

	public int getPieceLine() {
		return pieceLine;
	}

	public void setPieceLine(int pieceLine) {
		this.pieceLine = pieceLine;
	}

	public int getCutFlag() {
		return cutFlag;
	}

	public void setCutFlag(int cutFlag) {
		this.cutFlag = cutFlag;
	}

	public int getCutAlt() {
		return cutAlt;
	}

	public void setCutAlt(int cutAlt) {
		this.cutAlt = cutAlt;
	}

	public int getRenderFlag() {
		return renderFlag;
	}

	public void setRenderFlag(int renderFlag) {
		this.renderFlag = renderFlag;
	}

	public int getEdgeWidth() {
		return edgeWidth;
	}

	public void setEdgeWidth(int edgeWidth) {
		this.edgeWidth = edgeWidth;
	}

	public int getShadowOffset() {
		return shadowOffset;
	}

	public boolean isWithQuad() {
		return withQuad;
	}

	public void setWithQuad(boolean withQuad) {
		this.withQuad = withQuad;
	}

	public boolean isAbsInMove() {
		return absInMove;
	}

	public void setAbsInMove(boolean absInMove) {
		this.absInMove = absInMove;
	}

	public void setShadowOffset(int shadowOffset) {
		this.shadowOffset = shadowOffset;
	}

	public int getImageId() {
		return imageId;
	}

	public void setImageId(int imageId) {
		this.imageId = imageId;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getAddData() {
		return addData;
	}

	public void setAddData(String addData) {
		this.addData = addData;
	}

	public String getLevelDesc() {
		return levelDesc;
	}

	public void setLevelDesc(String levelDesc) {
		this.levelDesc = levelDesc;
	}

	

}

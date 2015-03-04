package com.sunteorum.pinktoru.entity;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 游戏数据包装类
 * @author KYO
 *
 */
public class GameEntity implements Parcelable {

	private int gameId;//游戏ID
	private int gameMode;//游戏模式，0.连图，1.拼图，2.填图，3.组图。
	private int gameLevel;//游戏级别，难易度
	private int gameRow;//行
	private int gameLine;//列
	private int gameImageId;//游戏图片的ID
	private String gameImageUrl;//游戏图片的URL
	private String gameDesc = "该游戏关卡暂无介绍";//游戏介绍
	
	public GameEntity() {
		super();
		
	}
	
	public GameEntity(int level, int row, int line, String desc) {
		//游戏ID分类：0为预置，1～100为自定义，100以上为下载内容
		this.gameId = 0;
		this.gameImageId = 0;
		this.gameImageUrl = "";
		this.gameLevel = level;
		this.gameRow = row;
		this.gameLine = line;
		this.gameMode = 1;
		this.gameDesc = desc;
		
	}
	
	/**
	 * 自定义游戏重载
	 * @param id
	 * @param level
	 * @param mode
	 * @param row
	 * @param line
	 * @param path
	 * @param desc
	 */
	public GameEntity(int id, int level, int mode, int row, int line, String path, String desc) {
		if (id < 1 || id > 100) id = 1;
		this.gameId = id;
		this.gameImageId = 0;
		this.gameImageUrl = path;
		this.gameLevel = level;
		this.gameRow = row;
		this.gameLine = line;
		this.gameMode = mode;
		this.gameDesc = desc;
		
	}
	
	public GameEntity(String lvJson) {
		try {
			JSONObject jso = new JSONObject(lvJson);
			int id = jso.getInt("id");
			if (id < 101) id = 101;
			this.gameId = id;
			this.gameImageId = jso.has("img_id")?jso.getInt("img_id"):0;
			this.gameImageUrl = jso.getString("img");
			this.gameLevel = jso.getInt("level");
			this.gameRow = jso.has("row")?jso.getInt("row"):3;
			this.gameLine = jso.has("line")?jso.getInt("line"):3;
			this.gameMode = jso.getInt("mode");
			if (jso.has("desc")) this.gameDesc = jso.getString("desc");
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}

	public String getGameJSON() {
		try {
			JSONObject jso = new JSONObject();
			jso.put("id", this.gameId);
			jso.put("img_id", this.gameImageId);
			jso.put("img", this.gameImageUrl);
			jso.put("level", this.gameLevel);
			jso.put("row", this.gameRow);
			jso.put("line", this.gameLine);
			jso.put("mode", this.gameMode);
			jso.put("desc", this.gameDesc);
			
			return jso.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return this.toString();
	}
	
	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public int getGameMode() {
		return gameMode;
	}

	public void setGameMode(int gameMode) {
		this.gameMode = gameMode;
	}

	public int getGameLevel() {
		return gameLevel;
	}

	public void setGameLevel(int gameLevel) {
		this.gameLevel = gameLevel;
	}

	public int getGameRow() {
		return gameRow;
	}

	public void setGameRow(int gameRow) {
		this.gameRow = gameRow;
	}

	public int getGameLine() {
		return gameLine;
	}

	public void setGameLine(int gameLine) {
		this.gameLine = gameLine;
	}

	public int getGameImageId() {
		return gameImageId;
	}

	public void setGameImageId(int gameImageId) {
		this.gameImageId = gameImageId;
	}

	public String getGameImageUrl() {
		return gameImageUrl;
	}

	public void setGameImageUrl(String gameImageUrl) {
		this.gameImageUrl = gameImageUrl;
	}

	public String getGameDesc() {
		return gameDesc;
	}

	public void setGameDesc(String gameDesc) {
		this.gameDesc = gameDesc;
	}


	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel p, int flag) {
		p.writeString(gameImageUrl);
		p.writeString(gameDesc);
		p.writeInt(gameId);
		p.writeInt(gameMode);
		p.writeInt(gameLevel);
		p.writeInt(gameRow);
		p.writeInt(gameLine);
		p.writeInt(gameImageId);
	}
	
	public static final Parcelable.Creator<GameEntity> CREATOR = new Parcelable.Creator<GameEntity>() {

		@Override
		public GameEntity createFromParcel(Parcel p) {
			GameEntity pe = new GameEntity();
			
			pe.gameImageUrl = p.readString();
			pe.gameDesc = p.readString();
			pe.gameId = p.readInt();
			pe.gameMode = p.readInt();
			pe.gameLevel = p.readInt();
			pe.gameRow = p.readInt();
			pe.gameLine = p.readInt();
			pe.gameImageId = p.readInt();
			
			return pe;
		}

		@Override
		public GameEntity[] newArray(int n) {
			// TODO Auto-generated method stub
			return new GameEntity[n];
		}
		
	};

	

}

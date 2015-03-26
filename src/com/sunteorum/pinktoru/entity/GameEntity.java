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

	private int gameId;				//游戏ID
	private int gameMode = 1;			//游戏通用模式
	private int gameReward = 2;		//游戏奖励点数
	private int gamePrice = 0;		//游戏价值点数
	private int gameImageId = 0;		//游戏图片的ID
	private String gameName;		//游戏名称
	private String gameLevel = "{}";		//游戏关卡JSON数据
	private String gameImageUrl;	//游戏图片的URL
	private String gameIconUrl;		//游戏图标的URL
	private String gameDesc = "该游戏暂无介绍";	//游戏介绍
	
	public GameEntity() {
		super();
		
	}
	
	public GameEntity(int id, int mode, String name, String imgUrl) {
		this.gameId = id;
		this.gameMode = mode;
		this.gameIconUrl = null;
		this.gameDesc = "";
		this.gameLevel = "{}";
		this.gameName = name;
		this.gameImageUrl = imgUrl;
		this.gameImageId = 0;
		this.gameReward = 2;
		this.gamePrice = 0;
		
	}
	
	public GameEntity(String lvJson) {
		try {
			JSONObject jso = new JSONObject(lvJson);
			int id = jso.getInt("id");
			if (id < 101) id = 101;
			this.gameId = id;
			this.gameName = jso.getString("name");
			this.gameMode = jso.getInt("mode");
			this.gameReward = jso.has("reward_pts")?jso.getInt("reward_pts"):0;
			this.gamePrice = jso.has("price_pts")?jso.getInt("price_pts"):0;
			this.gameImageId = jso.has("image_id")?jso.getInt("img_id"):0;
			this.gameImageUrl = jso.getString("image_url");
			this.gameLevel = jso.getString("level");
			
			if (jso.has("icon_url")) this.gameIconUrl = jso.getString("icon_url");
			if (jso.has("desc")) this.gameDesc = jso.getString("desc");
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}

	public String toJSONString() {
		try {
			JSONObject jso = new JSONObject();
			jso.put("id", this.gameId);
			jso.put("name", this.gameName);
			jso.put("mode", this.gameMode);
			jso.put("reward_pts", this.gameReward);
			jso.put("price_pts", this.gamePrice);
			jso.put("image_id", this.gameImageId);
			jso.put("image_url", this.gameImageUrl);
			jso.put("icon_url", this.gameIconUrl);
			jso.put("level", this.gameLevel);
			jso.put("desc", this.gameDesc);
			
			return jso.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return this.toString();
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel p, int flag) {
		p.writeString(gameName);
		p.writeString(gameImageUrl);
		p.writeString(gameIconUrl);
		p.writeString(gameLevel);
		p.writeString(gameDesc);
		p.writeInt(gameId);
		p.writeInt(gameMode);
		p.writeInt(gameImageId);
		p.writeInt(gameReward);
		p.writeInt(gamePrice);
	}
	
	public static final Parcelable.Creator<GameEntity> CREATOR = new Parcelable.Creator<GameEntity>() {

		@Override
		public GameEntity createFromParcel(Parcel p) {
			GameEntity pe = new GameEntity();
			pe.gameName = p.readString();
			pe.gameImageUrl = p.readString();
			pe.gameIconUrl = p.readString();
			pe.gameLevel = p.readString();
			pe.gameDesc = p.readString();
			pe.gameId = p.readInt();
			pe.gameMode = p.readInt();
			pe.gameImageId = p.readInt();
			pe.gameReward = p.readInt();
			pe.gamePrice = p.readInt();
			
			return pe;
		}

		@Override
		public GameEntity[] newArray(int n) {
			// TODO Auto-generated method stub
			return new GameEntity[n];
		}
		
	};

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

	public int getGameImageId() {
		return gameImageId;
	}

	public void setGameImageId(int gameImageId) {
		this.gameImageId = gameImageId;
	}

	public String getGameName() {
		return gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	public String getGameLevel() {
		return gameLevel;
	}

	public void setGameLevel(String gameLevel) {
		this.gameLevel = gameLevel;
	}

	public String getGameImageUrl() {
		return gameImageUrl;
	}

	public void setGameImageUrl(String gameImageUrl) {
		this.gameImageUrl = gameImageUrl;
	}

	public String getGameIconUrl() {
		return gameIconUrl;
	}

	public void setGameIconUrl(String gameIconUrl) {
		this.gameIconUrl = gameIconUrl;
	}

	public String getGameDesc() {
		return gameDesc;
	}

	public void setGameDesc(String gameDesc) {
		this.gameDesc = gameDesc;
	}

	public int getGameReward() {
		return gameReward;
	}

	public void setGameReward(int gameReward) {
		this.gameReward = gameReward;
	}

	public int getGamePrice() {
		return gamePrice;
	}

	public void setGamePrice(int gamePrice) {
		this.gamePrice = gamePrice;
	}

	

}

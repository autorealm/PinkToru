package com.sunteorum.pinktoru.entity;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * ��Ϸ���ݰ�װ��
 * @author KYO
 *
 */
public class GameEntity implements Parcelable {

	private int gameId;//��ϷID
	private int gameMode;//��Ϸģʽ��0.��ͼ��1.ƴͼ��2.��ͼ��3.��ͼ��
	private int gameLevel;//��Ϸ�������׶�
	private int gameRow;//��
	private int gameLine;//��
	private int gameImageId;//��ϷͼƬ��ID
	private String gameImageUrl;//��ϷͼƬ��URL
	private String gameDesc = "����Ϸ�ؿ����޽���";//��Ϸ����
	
	public GameEntity() {
		super();
		
	}
	
	public GameEntity(int level, int row, int line, String desc) {
		//��ϷID���ࣺ0ΪԤ�ã�1��100Ϊ�Զ��壬100����Ϊ��������
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
	 * �Զ�����Ϸ����
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

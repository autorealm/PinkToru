package com.sunteorum.pinktoru.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 用户信息数据包装类
 *
 */
public class UserEntity implements Parcelable {

	private int userId;
	private int points;
	private int grade;
	private String username;
	private String password;
	private String email;
	private String phone;
	private String avatar;
	private String nickname;
	private String signature;
	private String locale;
	private String uuid;
	private String _info;
	
	public UserEntity() {
		super();
		
	}
	
	public UserEntity(String username, String password) {
		this.username = username;
		this.password = password;
		
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getPoints() {
		return points;
	}

	public int getGrade() {
		return grade;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getUUID() {
		return uuid;
	}

	public void setUUID(String uuid) {
		this.uuid = uuid;
	}

	public String get_info() {
		return _info;
	}

	public void set_info(String _info) {
		this._info = _info;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel desc, int flag) {
		desc.writeInt(userId);
		desc.writeInt(points);
		desc.writeInt(grade);
		desc.writeString(username);
		desc.writeString(password);
		desc.writeString(email);
		desc.writeString(phone);
		desc.writeString(avatar);
		desc.writeString(nickname);
		desc.writeString(signature);
		desc.writeString(locale);
		desc.writeString(uuid);
		desc.writeString(_info);
		
	}
	
	public static final Parcelable.Creator<UserEntity> CREATOR = new Parcelable.Creator<UserEntity>() {

		@Override
		public UserEntity createFromParcel(Parcel p) {
			UserEntity ue = new UserEntity();
			
			ue.userId = p.readInt();
			ue.points = p.readInt();
			ue.grade = p.readInt();
			ue.username = p.readString();
			ue.password = p.readString();
			ue.email = p.readString();
			ue.phone = p.readString();
			ue.avatar = p.readString();
			ue.nickname = p.readString();
			ue.signature = p.readString();
			ue.locale = p.readString();
			ue.uuid = p.readString();
			ue._info = p.readString();
			
			return ue;
		}

		@Override
		public UserEntity[] newArray(int n) {
			// TODO Auto-generated method stub
			return new UserEntity[n];
		}
		
	};

}

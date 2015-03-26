package com.sunteorum.pinktoru.db;

import java.util.ArrayList;

import com.sunteorum.pinktoru.entity.LevelEntity;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	private static SQLiteDatabase mDatabase = null;
	private static DBHelper mDBHelper = null;
	
	protected final static byte[] _writeLock = new byte[0];
	
	private static final String DATABASE_NAME = "pinktoru.db";
	public static final String TABLE_LEVEL = "pt_level";
	public static final String TABLE_GAME = "pt_game";
	public static final String TABLE_RECORD = "pt_record";
	private static final int DATABASE_VERSION = 1;
	
	private static final String KEY_ID = "_id";
	
	public DBHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		mDBHelper = this;
		mDatabase = this.getWritableDatabase();
		if (version < 1) throw new IllegalArgumentException("Version must be >= 1, was " + version);
		
	}
	
	protected DBHelper (Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		mDatabase = this.getReadableDatabase();
		
	}
	
	public static DBHelper getInstance(Context context) {
		if (mDBHelper == null) {
			mDBHelper = new DBHelper(context);
		}
		
		return mDBHelper;
	}
	
	@Override
	public synchronized void close() {
		// TODO Auto-generated method stub
		super.close();
		if (mDatabase != null && mDatabase.isOpen()) {
			mDatabase.close();
			mDatabase = null;
		}
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String GAME_TABLE_CREATE_SQL = "create table if not exists " + TABLE_GAME + " (" +
				KEY_ID + " integer primary key autoincrement, " +
				"game_id integer not null, " +
				"game_name tinytext not null, " +
				"level text not null, " +
				"reward_pts integer default 2, " +
				"price_pts integer default 0, " +
				"image_id integer default 0, " +
				"image_uri tinytext default null, " +
				"icon_uri tinytext default null, " +
				"desc text ); ";
		String LEVEL_TABLE_CREATE_SQL = "create table if not exists " + TABLE_LEVEL + " (" + 
				KEY_ID + " integer primary key autoincrement, " +
				"level_id integer not null, " +
				"piece_row tinyint not null, " +
				"piece_line tinyint not null, " +
				"game_mode tinyint default null, " +
				"target_value integer default null, " +
				"gift_pts integer default 1, " +
				"image_id integer default 0, " +
				"image_uri tinytext default null, " +
				"cut_params tinytext default null, " +	//cut_flag,cut_alt,render_flag,edge_width,shadow_offset
				"add_data text default null, " +
				"desc text ); ";
		String RECORD_TABLE_CREATE_SQL = "create table if not exists " + TABLE_RECORD + " (" + 
				KEY_ID + " integer primary key autoincrement, " +
				"game_id integer not null, " +
				"level_id integer not null, " +
				"user_id integer not null, " +
				"record_time integer not null, " +
				"steps integer not null, " +
				"score integer not null, " +
				"desc text ); ";
		
		db.execSQL(GAME_TABLE_CREATE_SQL);
		db.execSQL(LEVEL_TABLE_CREATE_SQL);
		db.execSQL(RECORD_TABLE_CREATE_SQL);
		
		ContentValues values = new ContentValues();
		values.clear();
		values.put("game_id", 1);
		values.put("game_name", "д╛хо");
		values.put("image_uri", "assets://game/images/default.jpg");
		values.put("level", "{}");
		values.put("desc", "");
		db.insertOrThrow(TABLE_GAME, null, values);
		
		ArrayList<LevelEntity> lvlist = new ArrayList<LevelEntity>();
		lvlist.add(new LevelEntity(1, 3, 2, "3*2"));
		lvlist.add(new LevelEntity(2, 5, 3, "5*3"));
		lvlist.add(new LevelEntity(3, 6, 4, "6*4"));
		lvlist.add(new LevelEntity(4, 7, 5, "7*5"));
		lvlist.add(new LevelEntity(5, 8, 6, "8*6"));
		lvlist.add(new LevelEntity(6, 9, 7, "9*7"));
		lvlist.add(new LevelEntity(7, 10, 8, "10*8"));
		lvlist.add(new LevelEntity(8, 12, 9, "12*9"));
		lvlist.add(new LevelEntity(9, 14, 10, "14*10"));
		
		for (LevelEntity le:lvlist) {
			values.clear();
			values.put("level_id", le.getLevelId());
			values.put("piece_row", le.getPieceRow());
			values.put("piece_line", le.getPieceLine());
			values.put("desc", le.getLevelDesc());
			db.insertOrThrow(TABLE_LEVEL, null, values);
		}
		
		lvlist.clear();
		values.clear();
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAME);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LEVEL);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORD);
		onCreate(db);
		
	}

}

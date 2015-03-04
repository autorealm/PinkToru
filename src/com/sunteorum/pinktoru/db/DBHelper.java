package com.sunteorum.pinktoru.db;

import java.util.ArrayList;

import com.sunteorum.pinktoru.entity.GameEntity;

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
		String GAME_TABLE_CREATE_SQL = "create table " +
				TABLE_GAME + " (" + KEY_ID + " integer primary key autoincrement, " +
				"game_id integer, " +
				"game_name text, " +
				"level_id integer, " +
				"desc text ); ";
		String LEVEL_TABLE_CREATE_SQL = "create table " + TABLE_LEVEL + " (" + 
				KEY_ID + " integer primary key autoincrement, " +
				"level_id integer not null, " +
				"piece_row integer not null, " +
				"piece_line integer not null, " +
				"desc text ); ";
		String RECORD_TABLE_CREATE_SQL = "create table " + TABLE_RECORD + " (" + 
				KEY_ID + " integer primary key autoincrement, " +
				"level_id integer not null, " +
				"record_time integer not null, " +
				"steps integer not null, " +
				"score integer not null, " +
				"desc text ); ";
		db.execSQL(GAME_TABLE_CREATE_SQL);
		db.execSQL(LEVEL_TABLE_CREATE_SQL);
		db.execSQL(RECORD_TABLE_CREATE_SQL);
		
		ContentValues values = new ContentValues();
		values.clear();
		values.put("level_id", 1);
		db.insertOrThrow(TABLE_GAME, null, values);
		
		ArrayList<GameEntity> gamelist = new ArrayList<GameEntity>();
		gamelist.add(new GameEntity(1, 3, 2, "3*2"));
		gamelist.add(new GameEntity(2, 5, 3, "5*3"));
		gamelist.add(new GameEntity(3, 6, 4, "6*4"));
		gamelist.add(new GameEntity(4, 7, 5, "7*5"));
		gamelist.add(new GameEntity(5, 8, 6, "8*6"));
		gamelist.add(new GameEntity(6, 9, 7, "9*7"));
		gamelist.add(new GameEntity(7, 10, 8, "10*8"));
		gamelist.add(new GameEntity(8, 12, 9, "12*9"));
		
		for (GameEntity ge:gamelist) {
			values.clear();
			values.put("level_id", ge.getGameLevel());
			values.put("piece_row", ge.getGameRow());
			values.put("piece_line", ge.getGameLine());
			values.put("desc", ge.getGameDesc());
			db.insertOrThrow(TABLE_LEVEL, null, values);
		}
		
		gamelist.clear();
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

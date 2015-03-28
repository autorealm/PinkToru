package com.sunteorum.pinktoru.db;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.sunteorum.pinktoru.entity.GameEntity;
import com.sunteorum.pinktoru.entity.LevelEntity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DataBean {
	private static DataBean mInstance;
	private DBHelper mSQLHelper;
	private SQLiteDatabase mSQLiteDatabase;

	private DataBean(Context context) {
		//mContext = context;
		mSQLHelper = new DBHelper(context);
		mSQLiteDatabase = mSQLHelper.getWritableDatabase();
	}
	
	public static DataBean getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new DataBean(context);
		}
		
		return mInstance;
	}
	
	/**
	 * 关闭数据库
	 */
	public void close() {
		mSQLHelper.close();
		mSQLHelper = null;
		mSQLiteDatabase.close();
		mSQLiteDatabase = null;
		mInstance = null;
	}

	/**
	 * 添加数据
	 */
	public void insertData(String tableName, ContentValues values) {
		mSQLiteDatabase.insert(tableName, null, values);
	}

	/**
	 * 更新数据
	 * 
	 * @param values
	 * @param whereClause
	 * @param whereArgs
	 */
	public void updateData(String tableName, ContentValues values, String whereClause, String[] whereArgs) {
		mSQLiteDatabase.update(tableName, values, whereClause, whereArgs);
	}

	/**
	 * 删除数据
	 * 
	 * @param whereClause
	 * @param whereArgs
	 */
	public void deleteData(String tableName, String whereClause, String[] whereArgs) {
		mSQLiteDatabase.delete(tableName, whereClause, whereArgs);
	}

	/**
	 * 查询数据
	 * 
	 * @param columns
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @return
	 */
	public Cursor selectData(String tableName, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having, String orderBy) {
		Cursor cursor = mSQLiteDatabase.query(tableName,columns, selection, selectionArgs, groupBy, having, orderBy);
		return cursor;
	}
	
	/**
	 * 获取记录的最大值
	 * @param table 表名
	 * @param get_col 查询的字段
	 * @param order_col 排序的字段
	 * @return
	 */
	public int getMaxValue(String table, String get_col, String order_col) {
		String sql = " select " + get_col + " from " + table + " order by " + order_col + " desc; ";
		Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);
		if (cursor.moveToFirst()) {
			return cursor.getInt(cursor.getColumnIndexOrThrow(get_col));
		} else {
			return 0;
		}
		
	}
	
	public int getEntryCount(String table, String where) {
		String sql = " select * from " + table + " where " + where + " ; ";
		Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);
		int count = cursor.getCount();
		cursor.close();
		return count;
	}
	
	public Cursor getEntry(String table, String where) {
		String sql = " select * from " + table + " where " + where + " ; ";
		
		return mSQLiteDatabase.rawQuery(sql, null);
	}

	/** 插入成绩记录数据 */
	public void insertRecord(int gameId, int levelId, int score, int steps, int time, String desc) {
		ContentValues values = new ContentValues();
		values.put("game_id", gameId);
		values.put("level_id", levelId);
		values.put("record_time", time);
		values.put("steps", steps);
		values.put("score", score);
		values.put("desc", desc);
		
		mSQLiteDatabase.insert(DBHelper.TABLE_RECORD, "", values);
	}

	/** 更新关卡数据 */
	public void updateLevel(String mode, String level, String desc, int row, int line) {
		ContentValues values = new ContentValues();
		values.put("piece_row", row);
		values.put("piece_line", line);
		values.put("desc", desc);
		mSQLiteDatabase.update(DBHelper.TABLE_LEVEL, values, "game_mode=? and " + "level_id=?",
				new String[] { mode, level });
	}

	/** 删除游戏数据 */
	public void deleteGame(String mode, String level) {
		mSQLiteDatabase.delete(DBHelper.TABLE_GAME, "game_mode=? and " + "level_id=?",
				new String[] { mode, level });
	}
	
	/** 查询数据 */
	public LinkedList<LevelEntity> queryGame(String mode, String level) {
		
		LinkedList<LevelEntity> recordList = new LinkedList<LevelEntity>();
		Cursor cursor = null;
		try {
			cursor = mSQLiteDatabase.query(DBHelper.TABLE_LEVEL,
					new String[] { "", "", "", },
					"=? and " + "=?",
					new String[] { mode, level },
					null, null, " ASC");
			int nameIndex = cursor.getColumnIndex("");
			int stepsIndex = cursor.getColumnIndex("");
			int timeIndex = cursor.getColumnIndex("");
			// 获取总列数
			cursor.getColumnCount();
			// 获取总共有多少条数据
			cursor.getCount();
			
			LevelEntity record = null;
			while (cursor.moveToNext()) {// 要查询的列
				cursor.getString(nameIndex);
				cursor.getInt(stepsIndex);
				cursor.getInt(timeIndex);
				recordList.add(record);
			}
		} catch (Exception e) {
		} finally {
			if (cursor != null) {
				cursor.close();// 游标执行完判断是否为空，不为空即可关闭
			}
		}
		
		return recordList;
	}
	
	public boolean insertGame(GameEntity ge) {
		Cursor cursor = null;
		boolean success = false;
		try {
			ContentValues values = new ContentValues();
			values.put("game_name", ge.getGameName());
			values.put("image_uri", ge.getGameImageUrl());
			values.put("icon_uri", ge.getGameIconUrl());
			values.put("level", ge.getGameLevel());
			values.put("game_id", ge.getGameId());
			values.put("image_id", ge.getGameImageId());
			values.put("reward_pts", ge.getGameReward());
			values.put("Price_pts", ge.getGamePrice());
			values.put("desc", ge.getGameDesc());
			
			long id = -1;
			id = mSQLiteDatabase.insert(DBHelper.TABLE_GAME, null, values);
			success = (id != -1 ? true : false);
		} catch (Exception e) {
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return success;
	}
	
	public boolean insertLevel(LevelEntity le) {
		Cursor cursor = null;
		boolean success = false;
		try {
			ContentValues values = new ContentValues();
			
			values.put("level_id", le.getLevelId());
			values.put("piece_row", le.getPieceRow());
			values.put("piece_line", le.getPieceLine());
			values.put("game_mode", le.getGameMode());
			values.put("target_value", le.getTargetValue());
			values.put("gift_pts", le.getGiftPoints());
			values.put("ext_params", le.getExtParams());
			values.put("image_id", le.getImageId());
			values.put("image_uri", le.getImageUrl());
			values.put("add_data", le.getAddData());
			values.put("desc", le.getLevelDesc());
			
			long id = -1;
			id = mSQLiteDatabase.insert(DBHelper.TABLE_LEVEL, null, values);
			success = (id != -1 ? true : false);
		} catch (Exception e) {
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return success;
	}
	
	public boolean delectGame(String whereClause, String[] whereArgs) {
		boolean success = false;
		try {
			int count = 0;
			count = mSQLiteDatabase.delete(DBHelper.TABLE_GAME, whereClause, whereArgs);
			success = (count > 0 ? true : false);
		} catch (Exception e) {
		} finally {
		}
		
		return success;
	}
	
	public boolean delectLevel(String whereClause, String[] whereArgs) {
		boolean success = false;
		try {
			int count = 0;
			count = mSQLiteDatabase.delete(DBHelper.TABLE_LEVEL, whereClause, whereArgs);
			success = (count > 0 ? true : false);
		} catch (Exception e) {
		} finally {
		}
		
		return success;
	}
	
	public boolean updateGame(ContentValues values, String whereClause, String[] whereArgs) {
		boolean flag = false;
		int count = 0;
		try {
			count = mSQLiteDatabase.update(DBHelper.TABLE_GAME, values, whereClause, whereArgs);
			flag = (count > 0 ? true : false);
		} catch (Exception e) {
		} finally {
		}
		
		return flag;
	}
	
	public boolean updateLevel(ContentValues values, String whereClause, String[] whereArgs) {
		boolean flag = false;
		int count = 0;
		try {
			count = mSQLiteDatabase.update(DBHelper.TABLE_LEVEL, values, whereClause, whereArgs);
			flag = (count > 0 ? true : false);
		} catch (Exception e) {
		} finally {
		}
		
		return flag;
	}
	
	public GameEntity queryGame(int id) {
		GameEntity ge = new GameEntity();
		Cursor cursor = getEntry(DBHelper.TABLE_GAME, "game_id=" + id);
		if (cursor.getCount() != 1) return null;
		
		cursor.moveToFirst();
		int game_id = cursor.getInt(cursor.getColumnIndexOrThrow("game_id"));
		int image_id = cursor.getInt(cursor.getColumnIndexOrThrow("image_id"));
		int reward_pts = cursor.getInt(cursor.getColumnIndexOrThrow("reward_pts"));
		int price_pts = cursor.getInt(cursor.getColumnIndexOrThrow("price_pts"));
		String game_name = cursor.getString(cursor.getColumnIndexOrThrow("game_name"));
		String image_uri = cursor.getString(cursor.getColumnIndexOrThrow("image_uri"));
		String icon_uri = cursor.getString(cursor.getColumnIndexOrThrow("icon_uri"));
		String level = cursor.getString(cursor.getColumnIndexOrThrow("level"));
		String desc = cursor.getString(cursor.getColumnIndexOrThrow("desc"));
		
		ge.setGameId(game_id);
		ge.setGameImageId(image_id);
		ge.setGameReward(reward_pts);
		ge.setGamePrice(price_pts);
		ge.setGameName(game_name);
		ge.setGameImageUrl(image_uri);
		ge.setGameIconUrl(icon_uri);
		ge.setGameLevel(level);
		ge.setGameDesc(desc);
		
		cursor.close();
		
		return ge;
	}
	
	public LevelEntity queryLevel(int id) {
		LevelEntity le = new LevelEntity();
		Cursor cursor = getEntry(DBHelper.TABLE_LEVEL, "level_id=" + id);
		if (cursor.getCount() != 1) return null;
		
		cursor.moveToFirst();
		int level_id = cursor.getInt(cursor.getColumnIndexOrThrow("level_id"));
		int piece_row = cursor.getInt(cursor.getColumnIndexOrThrow("piece_row"));
		int piece_line = cursor.getInt(cursor.getColumnIndexOrThrow("piece_line"));
		int game_mode = cursor.getInt(cursor.getColumnIndexOrThrow("game_mode"));
		int target_value = cursor.getInt(cursor.getColumnIndexOrThrow("target_value"));
		int gift_pts = cursor.getInt(cursor.getColumnIndexOrThrow("gift_pts"));
		int image_id = cursor.getInt(cursor.getColumnIndexOrThrow("image_id"));
		String image_uri = cursor.getString(cursor.getColumnIndexOrThrow("image_uri"));
		String ext_params = cursor.getString(cursor.getColumnIndexOrThrow("ext_params"));
		String add_data = cursor.getString(cursor.getColumnIndexOrThrow("add_data"));
		String desc = cursor.getString(cursor.getColumnIndexOrThrow("desc"));

		le.setLevelId(level_id);
		le.setPieceRow(piece_row);
		le.setPieceLine(piece_line);
		le.setGameMode(game_mode);
		le.setTargetValue(target_value);
		le.setGiftPoints(gift_pts);
		le.setExtParams(ext_params);
		le.setImageId(image_id);
		le.setImageUrl(image_uri);
		le.setAddData(add_data);
		le.setLevelDesc(desc);
		
		cursor.close();
		
		return le;
	}
	
	public List<GameEntity> listGame(String selection, String[] selectionArgs) {
		List<GameEntity> list = new ArrayList<GameEntity>();
		Cursor cursor = null;
		try {
			cursor = mSQLiteDatabase.query(false, DBHelper.TABLE_GAME, null, selection, selectionArgs, null, null, null, null);
			int cols_len = cursor.getColumnCount();
			while (cursor.moveToNext()) {
				GameEntity map = new GameEntity();
				for (int i = 0; i < cols_len; i++) {
					String cols_name = cursor.getColumnName(i);
					int cols_index = cursor.getColumnIndex(cols_name);
					if (("game_name").equalsIgnoreCase(cols_name)) {
						map.setGameName(cursor.getString(cols_index));
					} else if (("image_uri").equalsIgnoreCase(cols_name)) {
						map.setGameImageUrl(cursor.getString(cols_index));
					} else if (("icon_uri").equalsIgnoreCase(cols_name)) {
						map.setGameIconUrl(cursor.getString(cols_index));
					} else if (("level").equalsIgnoreCase(cols_name)) {
						map.setGameLevel(cursor.getString(cols_index));
					} else if (("game_id").equalsIgnoreCase(cols_name)) {
						map.setGameId(cursor.getInt(cols_index));
					} else if (("image_id").equalsIgnoreCase(cols_name)) {
						map.setGameImageId(cursor.getInt(cols_index));
					} else if (("reward_pts").equalsIgnoreCase(cols_name)) {
						map.setGameReward(cursor.getInt(cols_index));
					} else if (("price_pts").equalsIgnoreCase(cols_name)) {
						map.setGamePrice(cursor.getInt(cols_index));
					} else if (("desc").equalsIgnoreCase(cols_name)) {
						map.setGameDesc(cursor.getString(cols_index));
					}
					
					String cols_values = cursor.getString(cursor.getColumnIndex(cols_name));
					if (cols_values == null) {
						cols_values = "";
					}
					
					//map.put(cols_name, cols_values);
				}
				
				list.add(map);
			}

		} catch (Exception e) {
			// TODO: handle exception
		} finally {
		}
		
		return list;
	}
	
	
}

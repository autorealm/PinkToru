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
	
	public Cursor getEntry(String table, String where){
		String sql = " select * from " + table + " " + where + " ; ";
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
	
	public boolean addGame(GameEntity ge) {
		Cursor cursor = null;
		boolean success = false;
		try {
			ContentValues values = new ContentValues();
			values.put("game_name", ge.getGameName());
			values.put("image_uri", ge.getGameImageUrl());
			values.put("level", ge.getGameLevel());
			values.put("game_id", ge.getGameId());
			values.put("image_id", ge.getGameImageId());
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
					} else if (("level").equalsIgnoreCase(cols_name)) {
						map.setGameLevel(cursor.getString(cols_index));
					} else if (("game_id").equalsIgnoreCase(cols_name)) {
						map.setGameId(cursor.getInt(cols_index));
					} else if (("image_id").equalsIgnoreCase(cols_name)) {
						map.setGameImageId(cursor.getInt(cols_index));
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

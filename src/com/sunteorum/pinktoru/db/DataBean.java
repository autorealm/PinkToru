package com.sunteorum.pinktoru.db;

import java.util.LinkedList;

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
	public void insertRecord(String mode, String level, String name, int steps, int time) {
		ContentValues values = new ContentValues();
		values.put("", mode);
		values.put("", level);
		values.put("", name);
		values.put("", steps);
		values.put("", time);
		
		mSQLiteDatabase.insert(DBHelper.TABLE_RECORD, "", values);
	}

	/** 更新关卡数据 */
	public void updateLevel(String mode, String level, String desc, int row, int line) {
		ContentValues values = new ContentValues();
		values.put("", row);
		values.put("", line);
		values.put("", desc);
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
	
}

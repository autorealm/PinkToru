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
	 * �ر����ݿ�
	 */
	public void close() {
		mSQLHelper.close();
		mSQLHelper = null;
		mSQLiteDatabase.close();
		mSQLiteDatabase = null;
		mInstance = null;
	}

	/**
	 * �������
	 */
	public void insertData(String tableName, ContentValues values) {
		mSQLiteDatabase.insert(tableName, null, values);
	}

	/**
	 * ��������
	 * 
	 * @param values
	 * @param whereClause
	 * @param whereArgs
	 */
	public void updateData(String tableName, ContentValues values, String whereClause, String[] whereArgs) {
		mSQLiteDatabase.update(tableName, values, whereClause, whereArgs);
	}

	/**
	 * ɾ������
	 * 
	 * @param whereClause
	 * @param whereArgs
	 */
	public void deleteData(String tableName, String whereClause, String[] whereArgs) {
		mSQLiteDatabase.delete(tableName, whereClause, whereArgs);
	}

	/**
	 * ��ѯ����
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
	 * ��ȡ��¼�����ֵ
	 * @param table ����
	 * @param get_col ��ѯ���ֶ�
	 * @param order_col ������ֶ�
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

	/** ����ɼ���¼���� */
	public void insertRecord(String mode, String level, String name, int steps, int time) {
		ContentValues values = new ContentValues();
		values.put("", mode);
		values.put("", level);
		values.put("", name);
		values.put("", steps);
		values.put("", time);
		
		mSQLiteDatabase.insert(DBHelper.TABLE_RECORD, "", values);
	}

	/** ���¹ؿ����� */
	public void updateLevel(String mode, String level, String desc, int row, int line) {
		ContentValues values = new ContentValues();
		values.put("", row);
		values.put("", line);
		values.put("", desc);
		mSQLiteDatabase.update(DBHelper.TABLE_LEVEL, values, "game_mode=? and " + "level_id=?",
				new String[] { mode, level });
	}

	/** ɾ����Ϸ���� */
	public void deleteGame(String mode, String level) {
		mSQLiteDatabase.delete(DBHelper.TABLE_GAME, "game_mode=? and " + "level_id=?",
				new String[] { mode, level });
	}
	
	/** ��ѯ���� */
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
			// ��ȡ������
			cursor.getColumnCount();
			// ��ȡ�ܹ��ж���������
			cursor.getCount();
			
			LevelEntity record = null;
			while (cursor.moveToNext()) {// Ҫ��ѯ����
				cursor.getString(nameIndex);
				cursor.getInt(stepsIndex);
				cursor.getInt(timeIndex);
				recordList.add(record);
			}
		} catch (Exception e) {
		} finally {
			if (cursor != null) {
				cursor.close();// �α�ִ�����ж��Ƿ�Ϊ�գ���Ϊ�ռ��ɹر�
			}
		}
		
		return recordList;
	}
	
}

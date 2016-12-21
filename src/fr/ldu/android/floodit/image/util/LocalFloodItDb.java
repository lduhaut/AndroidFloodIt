package fr.ldu.android.floodit.image.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.AndroidRuntimeException;

public class LocalFloodItDb {
	
    public static final String KEY_ID = "_id";
    public static final String KEY_SCORE = "score";
    public static final String KEY_NAME = "name";
    public static final String KEY_LEVEL = "level";

    public static final String KEY_NB_COL = "nb_col";
    public static final String KEY_NB_ROWS = "nb_rows";
    
    public static final String SCORE_TABLE_NAME = "scores";
    public static final String LEVEL_TABLE_NAME = "levels";

    private static final int NB_SCORES_CONSERVES = 10;
    
	private DataBaseHelper mDbHelper;
	private final Context mContext;
	
	public LocalFloodItDb(Context ct) {
		mContext = ct;
	}
	
	public LocalFloodItDb open() throws SQLException {
		mDbHelper = new DataBaseHelper(mContext);
        return this;
    }
	
	public void close() {
        mDbHelper.close();
    }
	
	public void createScore(int nbCol, int nbRows, int score, String name) {
        SQLiteDatabase mDb = mDbHelper.getWritableDatabase();
        
        long level = getLevel(nbCol, nbRows);
        if (level == -1) {
        	level = createLevel(nbCol, nbRows);
        }
        
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_LEVEL, level);
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_SCORE, score);
        mDb.insert(SCORE_TABLE_NAME, null, initialValues);
        
        Cursor c = getAllScoresAtLevel(nbCol, nbRows);
        if (c.getCount() > NB_SCORES_CONSERVES) {
        	c.moveToPosition(NB_SCORES_CONSERVES - 1);
        	deleteScore(c.getInt(0));
        	
        	while (c.moveToNext()) {
        		deleteScore(c.getInt(0));
        	}
        }
    }
	
	private void deleteScore(int id) {
		SQLiteDatabase mDb = mDbHelper.getReadableDatabase();
		mDb.execSQL("DELETE FROM scores where _id = " + id);
	}
	
	private long createLevel(int nbCol, int nbRows) {
        SQLiteDatabase mDb = mDbHelper.getWritableDatabase();
        
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NB_COL, nbCol);
        initialValues.put(KEY_NB_ROWS, nbRows);
        return mDb.insert(LEVEL_TABLE_NAME, null, initialValues);
    }
	
	private void clearDb() {
		SQLiteDatabase mDb = mDbHelper.getReadableDatabase();
		mDb.execSQL("DELETE FROM scores");
		mDb.execSQL("DELETE FROM levels");
	}
	
	public Cursor getAllScores() {
		SQLiteDatabase mDb = mDbHelper.getReadableDatabase();
		final String queryAll = "SELECT l.nb_col, l.nb_rows, s.score, s.name FROM " + SCORE_TABLE_NAME + " s INNER JOIN " + LEVEL_TABLE_NAME + " l ON s.level=l._id ORDER BY s.level, s.score";
		
        return mDb.rawQuery(queryAll, null);
	}
	
	public Cursor getAllScoresAtLevel(int nbCol, int nbRows) {
		int levelId = getLevel(nbCol, nbRows);
		
		SQLiteDatabase mDb = mDbHelper.getReadableDatabase();
        return mDb.query(SCORE_TABLE_NAME, new String[] {
                KEY_ID, KEY_LEVEL, KEY_SCORE, KEY_NAME
        }, KEY_LEVEL + " = ?", new String[]{Integer.toString(levelId)}, null, null, KEY_SCORE);
	}
	
	public int getLevel(int nbCol, int nbRows) {
		SQLiteDatabase mDb = mDbHelper.getReadableDatabase();
		Cursor cursor = mDb.query(LEVEL_TABLE_NAME,  
				new String[] {KEY_ID}, 
				KEY_NB_COL + " = ? AND " + KEY_NB_ROWS + " = ?", 
				new String[]{Integer.toString(nbCol), Integer.toString(nbRows)},
				null, null, null);
		
		if (cursor.getCount() == 0) {
			return -1;
		} else if (cursor.getCount() > 1) {
			throw new AndroidRuntimeException("LocalFloodItDb : Returned many levels for nbCol;nbRows = " + nbCol + ";" + nbRows);
		} else {
			cursor.moveToFirst();
			return cursor.getInt(0);
		}
	}
	
	private static class DataBaseHelper extends SQLiteOpenHelper {
		private final static String DB_NAME = "FloodIt";
		
		public DataBaseHelper(Context context) {
			super(context, DB_NAME, null, 1);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + SCORE_TABLE_NAME +" (" +
					KEY_ID + " integer primary key autoincrement, " +
					KEY_LEVEL + " integer not null," +
					KEY_SCORE + " integer not null," +
					KEY_NAME + " string not null)");
			
			db.execSQL("CREATE TABLE " + LEVEL_TABLE_NAME +" (" +
					KEY_ID + " integer primary key autoincrement, " +
					KEY_NB_COL + " integer not null," +
					KEY_NB_ROWS + " integer not null)");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// Do nothing.
		}
	}
	
}

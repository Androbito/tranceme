package com.gnb.sqlite;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.gnb.model.Hit;

public class DatabaseHandler extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "android_api";

	// Login table name
	private static final String TABLE_FAV = "fav_hits";

	// Login Table Columns names
	private static final String AUTEUR_ID = "idAut";
	private static final String TRACK_ID = "id";
	private static final String TRACK_TITLE = "title";
	private static final String TRACK_DURATION = "duration";
	private static final String TRACK_URL = "url";
	private static final String TRACK_INFO = "info";
	private static final String TRACK_IMG_URL = "img";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_FAV
				+ "( _id INTEGER PRIMARY KEY," + TRACK_ID + " INTEGER,"
				+ AUTEUR_ID + " INTEGER," + TRACK_TITLE + " TEXT,"
				+ TRACK_DURATION + " TEXT," + TRACK_URL + " TEXT UNIQUE,"
				+ TRACK_INFO + " TEXT," + TRACK_IMG_URL + " TEXT" + ")";
		db.execSQL(CREATE_LOGIN_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAV);

		// Create tables again
		onCreate(db);
	}

	/**
	 * add a hit to the favoris
	 * */
	public void addHitToFavoris(int id, int idAut, String title,
			String duration, String url, String info, String img) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(TRACK_ID, id); // Name
		values.put(AUTEUR_ID, idAut); // Email
		values.put(TRACK_TITLE, title); // Email
		values.put(TRACK_DURATION, duration);
		values.put(TRACK_URL, url);
		values.put(TRACK_INFO, info);
		values.put(TRACK_IMG_URL, img);
		db.insert(TABLE_FAV, null, values);
		db.close();

	}

	/**
	 * add a hit to the favoris
	 * */
	public void addHitToFavoris(Hit hit) {
		if (!isFav(hit)) {
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(TRACK_ID, hit.id); // Name
			values.put(AUTEUR_ID, hit.idAut); // Email
			values.put(TRACK_TITLE, hit.title); // Email
			values.put(TRACK_DURATION, hit.duration);
			values.put(TRACK_URL, hit.url);
			values.put(TRACK_INFO, hit.info);
			values.put(TRACK_IMG_URL, hit.img);
			db.insert(TABLE_FAV, null, values);
			db.close();
		}
	}

	/**
	 * Getting Favoris Hits from database
	 * */
	public List<Hit> getFavHits() {
		List<Hit> hitList = new ArrayList<Hit>(0);
		String selectQuery = "SELECT  * FROM " + TABLE_FAV;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// loop the cursor
		while (cursor.moveToNext()) {
			Hit hit = new Hit(cursor.getInt(2), cursor.getInt(1),
					cursor.getString(3), cursor.getString(4),
					cursor.getString(5), cursor.getString(6),
					cursor.getString(7));
			hitList.add(hit);
		}
		cursor.close();
		db.close();
		// return user
		return hitList;
	}

	/**
	 * check if a hit is already in the fav
	 */
	public boolean isFav(Hit hit) {
		Boolean b = false;
		for (int i = 0; i < getFavHits().size(); i++) {
			if (getFavHits().get(i).getUrl().equals(hit.getUrl()))
				b = true;
		}
		return b;
	}

	/**
	 * Re crate database Delete all tables and create them again
	 * */
	public void resetTables() {
		SQLiteDatabase db = this.getWritableDatabase();
		// Delete All Rows
		db.delete(TABLE_FAV, null, null);
		db.close();
	}

}
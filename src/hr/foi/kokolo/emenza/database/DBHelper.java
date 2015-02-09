package hr.foi.kokolo.emenza.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	public DBHelper(Context context, String name, CursorFactory factory,int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	
	// metoda za kreiranje tablica u bazi podataka
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub		
		String table_pois = "CREATE TABLE pois (id INTEGER PRIMARY KEY, name TEXT, longitude NUMERIC, latitude NUMERIC);";

		String table_menus = "CREATE TABLE menus(id INTEGER PRIMARY KEY, poiid INTEGER, type INTEGER, soup TEXT, main TEXT, side TEXT, salad TEXT, drink TEXT, dessert TEXT, FOREIGN KEY(type) REFERENCES menutypes(id));";
		

		db.execSQL(table_pois);

		db.execSQL(table_menus);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}

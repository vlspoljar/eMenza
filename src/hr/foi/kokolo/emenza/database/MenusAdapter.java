package hr.foi.kokolo.emenza.database;

import hr.foi.kokolo.emenza.types.PoiInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.android.gms.maps.model.LatLng;

public class MenusAdapter {

	private static String DATABASE_NAME = "menus.db";
	private static int DATABASE_VERSION = 1;
	private static String TABLE_POIS = "pois";
	private static String TABLE_MENUS = "menus";
	private static String KEY_POIS = "id";
	private static String KEY_MENUS = "id";
	
	private DBHelper dbHelper;
	private SQLiteDatabase db;
	private Context cont;
	
	public MenusAdapter(Context c){
		dbHelper= new DBHelper(c, DATABASE_NAME, null, DATABASE_VERSION);
		cont=c;
	}
	
	public void openToRead(){
		db = dbHelper.getReadableDatabase();
	}
	
	private void openToWrite(){
		db = dbHelper.getWritableDatabase();
	}
	
	private void close(){
		dbHelper.close();
	}
	
	/**
	* Popunjavanje pois tablice u bazi
	* @param PoiInfo poi => sadrži informacije o pojedinom poi-ju
	*/
	public long insertPois(PoiInfo poi){
		ContentValues values = new ContentValues();
		values.put("name", poi.getName());
		values.put("latitude", poi.getLocation().latitude);
		values.put("longitude", poi.getLocation().longitude);
		
		openToWrite();
		
		long result = db.insert(TABLE_POIS, null, values);
		close();
	    
		return result;
	}
	
	/**
	 * Dohvacanje svih {@link PoiInfo} PoiInfo objekata iz baze.
	 * @return Podatke strukturirane u {@link List} od {@link PoiInfo} objekata
	 */
	public List<PoiInfo> getAllPois(){
		List<PoiInfo> pois = new ArrayList<PoiInfo>();
		String[] columns = new String[]{KEY_POIS, "name", "latitude", "longitude"};
		openToRead();
		Cursor c = db.query(TABLE_POIS, columns, null, null, null, null, null);
		for(c.moveToFirst(); !(c.isAfterLast()); c.moveToNext()){
			String name =c.getString(c.getColumnIndex("name"));
			Double lat =c.getDouble(c.getColumnIndex("latitude"));
			Double lng =c.getDouble(c.getColumnIndex("longitude"));
			int id = c.getInt(c.getColumnIndex(KEY_POIS));
			
			PoiInfo poi = new PoiInfo(id, name, new LatLng(lat, lng));
			pois.add(poi);
		}
		close();
		return pois;
	}
	
	/**
	* Popunjavanje tablice menus u bazi
	* @param JSONObject jobrok => joson koji sadrži stavke menija
	*/
	public long insertMenu(int id,JSONObject jobrok){
		
		ContentValues values = new ContentValues();
		try {
			values.put("poiid", id);
			values.put("type", jobrok.getString("type"));
			values.put("soup", jobrok.getString("soup"));
			values.put("main", jobrok.getString("main"));
			values.put("side", jobrok.getString("side"));
			values.put("salad", jobrok.getString("salad"));
			values.put("drink", jobrok.getString("drink"));
			values.put("dessert", jobrok.getString("dessert"));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		openToWrite();
		
		long result = db.insert(TABLE_MENUS, null, values);
		close();
		
		return result;
	}
	
	/**
	 * Dohvacanje svih naslova iz baze podataka.
	 */
	public List<String> getMenuHeaders(int id){
		
		// definira se lista u koju se podaci spremaju
		List<String> headers = new ArrayList<String>();
		
		// brojaci za brojanje koliko ima ruèkova i veèera u meniju
		int brojac_rucak=0, brojac_vecera=0;
		String[] columns = new String[]{KEY_MENUS, "type"};
		openToRead();
		Cursor c = db.query(TABLE_MENUS, columns, "poiid" + "=" + id, null, null, null, null);
		for(c.moveToFirst(); !(c.isAfterLast()); c.moveToNext()){
			if(c.getInt(c.getColumnIndex("type")) != 0)	
				switch (c.getInt(c.getColumnIndex("type"))){
					case 1: 
						brojac_rucak++;
						headers.add("Ruèak - MENU "+brojac_rucak); 
						break;
					case 2: 
						headers.add("Ruèak - VEGE"); 
						break;
					case 3:
						brojac_vecera++;
						headers.add("Veèera - MENU "+brojac_vecera); 
						break;
					case 4: 
						headers.add("Veèera - VEGE"); 
						break;
				}
			
		}
		close();
		return headers;
	}
	
	/**
	 * Dohvacanje svih menija iz baze.
	 */
	public HashMap<String, List<String>> getMenu(int poiId){
		
		// hash mapa u koju se spremaju finalni podaci
		HashMap<String, List<String>> listDataChild = new HashMap<String, List<String>>();
		
		// lista u koju se spremaju naslovi da bi znali kuda koji meni pripada
		List<String> headers = new ArrayList<String>();
		
		int brojac_rucak=0, brojac_vecera=0,brojac=0;
		
		String[] columns = new String[]{KEY_MENUS, "type", "soup", "main", "side", "salad", "drink", "dessert"};
		openToRead();
		Cursor c = db.query(TABLE_MENUS, columns, "poiid" + "=" + poiId, null, null, null, null);
		for(c.moveToFirst(); !(c.isAfterLast()); c.moveToNext()){
			List<String> menus = new ArrayList<String>();
			if(c.getInt(c.getColumnIndex("type")) != 0){
				switch (c.getInt(c.getColumnIndex("type"))){
					case 1: 
						brojac_rucak++;
						headers.add("Ruèak - MENU "+brojac_rucak); 
						break;
					case 2: 
						headers.add("Ruèak - VEGE"); 
						break;
					case 3:
						brojac_vecera++;
						headers.add("Veèera - MENU "+brojac_vecera); 
						break;
					case 4: 
						headers.add("Veèera - VEGE"); 
						break;
				}
			}
			if(!c.getString(c.getColumnIndex("soup")).equals("null"))
				menus.add(c.getString(c.getColumnIndex("soup")));
			if(!c.getString(c.getColumnIndex("main")).equals("null"))
				menus.add(c.getString(c.getColumnIndex("main")));
			if(!c.getString(c.getColumnIndex("side")).equals("null"))
				menus.add(c.getString(c.getColumnIndex("side")));
			if(!c.getString(c.getColumnIndex("salad")).equals("null"))
				menus.add(c.getString(c.getColumnIndex("salad")));
			if(!c.getString(c.getColumnIndex("drink")).equals("null"))
				menus.add(c.getString(c.getColumnIndex("drink")));
			if(!c.getString(c.getColumnIndex("dessert")).equals("null"))
				menus.add(c.getString(c.getColumnIndex("dessert")));
			
			listDataChild.put(headers.get(brojac), menus);
			brojac++;
		}
		
		close();
		return listDataChild;
	}
	
	/**
	 * Provjera dal baza postoji, i ako postoji brise ju
	 */
	public void deleteDB() {
		File database= new File(cont.getDatabasePath(DATABASE_NAME).toString());
		if(database.exists()){
			database.delete();
		}
	  }

}


package hr.foi.kokolo.emenza.services;

import hr.foi.kokolo.emenza.database.MenusAdapter;
import hr.foi.kokolo.emenza.interfaces.IMenu;
import hr.foi.kokolo.emenza.interfaces.IPoiSource;
import hr.foi.kokolo.emenza.plugins.MenuWebService;
import hr.foi.kokolo.emenza.plugins.PoiSourceWebService;
import hr.foi.kokolo.emenza.types.PoiInfo;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

// service koja se pokrece iz MainActivit i služi za kreiranje i ažuriranje baze podataka
public class DatabaseUpdateService extends Service{

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	// metoda se poziva kada se service pokrene
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		
		// adapter za komunikaciju s bazom podataka
		MenusAdapter adapter = new MenusAdapter(getApplicationContext());
		
		// brisanje baze ako veæ postoji
		adapter.deleteDB();
		
		IPoiSource poiSource= new PoiSourceWebService();
		IMenu menuSource= new MenuWebService();
		
		// lista sa svim poijima dohvaæenim sa servera
		List<PoiInfo> pois = poiSource.getPois();
		int n=pois.size();
		for(int i=0;i<n;i++){
			PoiInfo poi= pois.get(i);
			
			// umetanje poija u bazu
			adapter.insertPois(poi);
			
			// dohvaæanje menija za svaki poi
			String menu = menuSource.getMenu(poi.getId());
			if(menu!=""){
				try {
					int length = new JSONArray(menu).length();
					for(int j=0;j<length; j++){
						JSONObject jobrok=new JSONArray(menu).getJSONObject(j);
						
						// spremanje menija u bazu
						adapter.insertMenu(poi.getId(), jobrok);
						
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		}
		
		// service se nakon izvršenog posla sama zaustavlja
		this.stopSelf();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		
		super.onDestroy();
	}
}

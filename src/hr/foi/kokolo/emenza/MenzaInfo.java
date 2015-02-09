package hr.foi.kokolo.emenza;

import hr.foi.kokolo.emenza.database.MenusAdapter;
import hr.foi.kokolo.emenza.types.MyLocationInfo;
import hr.foi.kokolo.emenza.types.PoiInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MenzaInfo extends Activity{

	SharedPreferences myPreference;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menza_info_activity);
		
		myPreference = PreferenceManager.getDefaultSharedPreferences(this);
		
		// poziv metode za prikaz podataka
		showData();
	}
	
	
	// metoda za prikaz podataka o menzama
	private void showData(){
		
		// adapter za komunikaciju s bazom
		MenusAdapter menusAdapter = new MenusAdapter(this);
		boolean showLocation=myPreference.getBoolean("chkShowMyLocation", false);
		
		// lista sa svim poi-jima
		List<PoiInfo> pois = menusAdapter.getAllPois();
		
		HashMap<String, String> map;
		
		// lista sa svim podacima za prikaz
		List<HashMap<String, String>> data= new ArrayList<HashMap<String,String>>();
		
		Iterator<PoiInfo> itr = pois.iterator();
		
		// prolazak kroz sve poi-je i dohva�anje naziva, adrese i udaljenosti
		while(itr.hasNext()){
			PoiInfo poi = itr.next();
			map = new HashMap<String, String>();
			map.put("title", "" + poi.getName());
			
			// preko metode reverseGeocoding se iz koordinata dobiva adresa
			map.put("adress", reverseGeocoding(poi.getLocation().latitude, poi.getLocation().longitude));
			if(showLocation){
				map.put("distance", "" + distanceCalc(poi) + " m");
			}
			else{
				//ako nije uklju�eno prikazivanje lokacije nije mogu�e izra�unati udaljenost do menze
				map.put("distance", "NaN");
			}
			
			// dodavanje podataka u listu
			data.add(map);
			
		}
		
		// definiranje gdje se podaci prikazuju i odakle se uzimaju
		String[] from = new String[]{"title","adress","distance"};
		int[] to = new int[] {R.id.poi_title, R.id.poi_adress, R.id.poi_distance};
		ListView lv = (ListView) findViewById(R.id.listMenze);
		
		// adapter preko kojeg se podaci prikazuju u listview-u
		SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.lista_redak, from, to);
		lv.invalidateViews();
		lv.setAdapter(adapter);
		
	}
	
	// metoda za ra�unanje udaljenosti do svih menzi od trenutne lokacije
	public Double distanceCalc(PoiInfo poi) {
		
		Location myLocation = new Location("myLocation");
		
		// dohva�anje na�e lokacije pomo�u gettera (spremljena je setterom u MapActivity)
		myLocation.setLatitude(MyLocationInfo.getMyLocation().latitude);
		myLocation.setLongitude(MyLocationInfo.getMyLocation().longitude);
		
		double distance=0;
		
		// pretvaranje koordinata poi-ja u lokaciju
		Location menze = new Location("menza");
		menze.setLatitude(poi.getLocation().latitude);
		menze.setLongitude(poi.getLocation().longitude);
		
		// ore�ivanje udaljenosti izme�u dvije lokacije
		distance = myLocation.distanceTo(menze);
		
		// formatiranje za prikaz da bude ljep�e
		return Double.parseDouble(String.format(Locale.getDefault(),"%.2f", distance));

	}
	
	// metoda kojom se iz koordinata odre�uje stvarna adresa
	public String reverseGeocoding(double latitude, double longitude) {
		
		// inicijalizacija novog geocoder objekta
		Geocoder geocoder = new Geocoder(this, Locale.getDefault());
		String adresa="";
		try {
			// dohva�anje svih adresa u listu, no mi postavljamo da ho�emo samo prvu po�to je to adresa koju tra�imo
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            
            // spajanje vi�e dijelova kako bi dobili �itavu adresu
            if (addresses.size() > 0) {
                for (int i = 0; i < addresses.get(0).getMaxAddressLineIndex(); i++){
                	adresa += addresses.get(0).getAddressLine(i) + " ";
                }
                    
            }
        }catch (IOException ex) {        
            ex.printStackTrace();
        }catch (Exception e2) {
            // TODO: handle exception

            e2.printStackTrace();
        }
		return adresa;
	}
}

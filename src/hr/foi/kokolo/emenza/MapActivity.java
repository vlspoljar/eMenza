package hr.foi.kokolo.emenza;

import hr.foi.kokolo.emenza.database.MenusAdapter;
import hr.foi.kokolo.emenza.plugins.CustomExpandableListAdapter;
import hr.foi.kokolo.emenza.types.MyLocationInfo;
import hr.foi.kokolo.emenza.types.PoiInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends FragmentActivity implements LocationListener, LocationSource{
	
	GoogleMap map;
	LocationManager locationManager;
	OnLocationChangedListener listener;
	static Marker myMarker;
	SharedPreferences myPreference;
	SharedPreferences.Editor prefsEditor;
	List<PoiInfo> distance_pois;
	LatLng najbliza;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		myPreference = PreferenceManager.getDefaultSharedPreferences(this);
		
		// ako je u postavkama uključeno prikazivanje trenutne lokacije te ovisno o tome da li je odabran GPS
		// zahtjeva se ažuriranje trenutne lokacije ili putem GPS-a ili preko mreže
		if (myPreference.getBoolean("chkShowMyLocation", true)) {
			if (myPreference.getBoolean("swcUseGPS", true)){
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, this);
			}
			else{
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 10, this);
			}
		}
	}
	
	// metoda u kojoj se na mapi prikazuju lokacije
	public void drawPois() {
		
		//inicijalizacija adaptera za komunikaciju s bazom podataka
		final MenusAdapter adapter= new MenusAdapter(this);
		
		// dohvaćanje svih poi-ja u listu
		List<PoiInfo> pois = adapter.getAllPois();
		distance_pois = pois;
		map.clear();
		int n=pois.size();
		
		// hash mapa u koju se spremaju tj. povezuju id markera i id poi-ja
		final HashMap<String, Integer> id_parovi = new HashMap<String, Integer>();
		
		for(int i=0;i<n;i++){
			final PoiInfo poi= pois.get(i);
			
			// postavljanje markera na mapu sa danim opcijama
			MarkerOptions markerOptions=new MarkerOptions();
			LatLng position= new LatLng(poi.getLocation().latitude, poi.getLocation().longitude);
			markerOptions.position(position);
			markerOptions.title(poi.getName());
			markerOptions.snippet("Kliknite na ovaj prozorcic za prikaz menija.");
			
			// ako je zatraženo prikazivanje najbliže menze, mijenja se boja tj. ikona markera 
			if(najbliza!=null && najbliza.latitude==position.latitude && najbliza.longitude==position.longitude){
				markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.menza_najbliza_pin));
			}
			else{
				markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.menza_pin));
			}
			
			Marker a = map.addMarker(markerOptions);
			id_parovi.put(a.getId(), poi.getId());
			
			// postavljanje listenera na info window markera <= prikazuje se kad se klikne na marker
			map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
				
				@Override
				public void onInfoWindowClick(Marker mark) {
					
					// provjera da li se radi o poi-ju ili markeru naše lokacije
					//if(!mark.getId().equals(myMarker.getId())){
						
						// klikom na info window korisniku se prikazuje dijalog sa menu-ima odabrane menze
						final Dialog dijalog = new Dialog(MapActivity.this);
						
						dijalog.setContentView(R.layout.dialog_meni_expandable);
						dijalog.setTitle(mark.getTitle());
						
					    ExpandableListView expListView = (ExpandableListView) dijalog.findViewById(R.id.expMenu);
					    
					    // lista u koju se spremaju naslovi za expendablelistview
					    List<String> listDataHeader = new ArrayList<String>();
					    
					    // dohvaćanje naslova iz baze s prosljeđivanjem id-ja poi-ja do kojeg se iz hash mape došlo
					    // preko id-ja kliknutog markera
					    listDataHeader = adapter.getMenuHeaders(id_parovi.get(mark.getId()));
					    
					    // hash lista u kojij su spremljeni podaci za expendablelistview
					    HashMap<String, List<String>> listDataChild = new HashMap<String, List<String>>();
					    
					    // dohvaćanje podataka iz baze preko id-ja poija
					    listDataChild = adapter.getMenu(id_parovi.get(mark.getId()));
					     
					    // inicijalizacija vlastitog adaptera preko kojeg se puni expendablelistview
					    CustomExpandableListAdapter listAdapter = new CustomExpandableListAdapter(MapActivity.this, listDataHeader, listDataChild);
					    
				        expListView.setAdapter(listAdapter);
						
						dijalog.show();
						
						// klikom na cancle gumb dijalog nestaje
						Button gumb = (Button)dijalog.findViewById(R.id.btnCancel);
						gumb.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								dijalog.dismiss();
							}
						});
					//}
				}
			});
		
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		// metoda za postavljanje mape
		setUpMapIfNeeded();
		
		// metoda za crtanje poi-ja
		drawPois();
		
		// uključuje se sloj za prikaz trenutne lokacije
		map.setMyLocationEnabled(true);
		
		myPreference = PreferenceManager.getDefaultSharedPreferences(this);
		
		if(locationManager!= null){
		
			// opet provjere vezane uz GPS i njegovu uključenost
			boolean useGPS=myPreference.getBoolean("swcUseGPS", false);
			boolean showLocation=myPreference.getBoolean("chkShowMyLocation", false);
			
			if(showLocation && useGPS){
				// provjera da li je GPS ukljuèen
				boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

				if (!enabled) {
					Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivityForResult(intent, 1);
				}
				else{
					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, this);
				}
				
			}
			else if(showLocation && !useGPS){
				checkIfEmulator();
			}
			else{
				if (myMarker != null) {
					myMarker.remove();
				}
				map.setMyLocationEnabled(false);
			}
			
		}
		
	}

	// kada se aktivnost pauzira, zaustavljaju se zahtjevi za ažuriranje lokacije
	@Override
	public void onPause() {
		super.onPause();
		if(locationManager!= null){
			locationManager.removeUpdates(this);
		}
	}
	
	// metoda u kojoj se postavlja i inicijalizira mapa
	private void setUpMapIfNeeded() {
		if (map == null) {
			map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment)).getMap();
			if (map != null) {
				
				// koordinate varaždina za početno zumiranje iznad grada
				LatLng varazdin = new LatLng(46.3000, 16.3333);
				map.animateCamera(CameraUpdateFactory.newLatLngZoom(varazdin, 13));
				map.setMyLocationEnabled(true);
			}
			map.setLocationSource(this);
		}
	}

	// metoda koja se poziva svaki puta kad se promjeni naša lokacija
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
		if (listener != null) {
			listener.onLocationChanged(location);
			
			// dohvaćanje granica unutar kojih je mapa vidljiva
			LatLngBounds bounds = this.map.getProjection().getVisibleRegion().latLngBounds;
			
			// ako se novo dohvaćena lokacija ne nalazu unutar definiranih granica, karta se zumira na poziciju lokacije
			if (!bounds.contains(new LatLng(location.getLatitude(), location.getLongitude()))) {
				map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16));
			}
			
			// micanje starog markera
			if (myMarker != null) {
				myMarker.remove();
			}
			
			// definiranje opcija za marker naše trenutne lokacije
			myMarker = map.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude()))
					.title("Vasa lokacija").icon(BitmapDescriptorFactory.fromResource(R.drawable.my_location)));
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16));
			
			LatLng mojaLokacija=new LatLng(location.getLatitude(), location.getLongitude());
			
			// spremanje lokacije za daljnju upotrebu preko settera
			MyLocationInfo.setMyLocation(mojaLokacija);
		}
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void activate(OnLocationChangedListener list) {
		// TODO Auto-generated method stub
		listener=list;
	}

	@Override
	public void deactivate() {
		// TODO Auto-generated method stub
		listener = null;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_map, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		
		// pozivanje aktivnosti s postavkama
		case R.id.opt_map_settings:
			Intent i = new Intent(this, SettingsActivity.class); 
			startActivity(i);
			break;
		
		// ažuriranje karte
		case R.id.opt_map_refresh:
			map=((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment)).getMap();
			if(locationManager!= null){
				boolean useGPS=myPreference.getBoolean("swcUseGPS", false);
				boolean showLocation=myPreference.getBoolean("chkShowMyLocation", false);
				
				if(showLocation && useGPS){
					// provjera da li je GPS ukljuèen
					boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

					if (!enabled) {
						Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						startActivityForResult(intent, 1);
					}
					else{
						locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, this);
					}
					
				}
				else if(showLocation && !useGPS){
					checkIfEmulator();
				}
			}
			najbliza=null;
			drawPois();
			Toast.makeText(this, "Map refreshed!", Toast.LENGTH_SHORT).show();
			break;
		
		// prikazivanje najbliže menze
		case R.id.opt_najbliza_menza:
			
			boolean showLocation=myPreference.getBoolean("chkShowMyLocation", false);
			boolean useGPS=myPreference.getBoolean("swcUseGPS", false);
			
			if(showLocation){
				int n=distance_pois.size();
				
				Location myLocation = new Location("myLocation");
				myLocation.setLatitude(myMarker.getPosition().latitude);
				myLocation.setLongitude(myMarker.getPosition().longitude);
				
				double distances[] = new double[n];
				double distance=0;
				
				// sljedeci komadic koda je za određivanje udaljenosti od naše trenutne lokacije do do najbliže menze
				HashMap<Double, Location> dist_poi_par = new HashMap<Double, Location>();
				for(int j=0;j<n;j++){
					Location menze = new Location("menza");
					PoiInfo poi= distance_pois.get(j);
					menze.setLatitude(poi.getLocation().latitude);
					menze.setLongitude(poi.getLocation().longitude);
					distance = myLocation.distanceTo(menze);
					distances[j]= Double.parseDouble(String.format("%.2f", distance));
					//spremanje udaljenosti do svih menzi
					dist_poi_par.put(distances[j], menze);
				}
				
				// najkraća udaljenost
				double key=getMinValue(distances);
				
				najbliza= new LatLng(dist_poi_par.get(key).getLatitude(), dist_poi_par.get(key).getLongitude());
				
				drawPois();
				if(showLocation && useGPS){
					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, this);
				}
				else if(showLocation && !useGPS){
					locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 10, this);
				}
			}
			else{
				Toast.makeText(this, "Nije uključeno prikazivanje trenutne lokacije!", Toast.LENGTH_SHORT).show();
			}
			
			break;
			
		// prikazivanje informacija o svim menzama
		case R.id.opt_menza_info:

			Intent info = new Intent(this, MenzaInfo.class); 
			startActivity(info);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	// metoda u kojoj se provjerava radi li se o emulatoru ili stvarnom uređaju
	public void checkIfEmulator() {
		if (android.os.Build.BRAND.toLowerCase(Locale.getDefault()).equals("generic")) {
			Toast.makeText(this, "Emulator running...Network Provider not available!", Toast.LENGTH_LONG).show();
			prefsEditor = myPreference.edit();
			prefsEditor.putBoolean("chkShowMyLocation", false);
			prefsEditor.commit();
			map.setMyLocationEnabled(false);
		} else {
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 10, this);
		}
	}
	
	// metoda koja provjerava da li je GPS uključen nakon povratka iz postavki telefona
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (!enabled) {
			Toast.makeText(this, "GPS nije uključen!",Toast.LENGTH_LONG).show();
			prefsEditor = myPreference.edit();
			prefsEditor.putBoolean("swcUseGPS", false);
			prefsEditor.commit();
			checkIfEmulator();
		} else {
			Toast.makeText(this, "GPS je uključen!",Toast.LENGTH_LONG).show();
		}
	}
	
	// metoda kojom iz polja koje se prosljeđuje kao argument izvlačimo najmanju vrijednost
	public static double getMinValue(double[] array){  
	     double minValue = array[0];  
	     for(int i=1;i<array.length;i++){  
	    	 if(array[i] < minValue){  
	    		 minValue = array[i];  
	    	 }  
	     }  
	    return minValue;  
	}  

}

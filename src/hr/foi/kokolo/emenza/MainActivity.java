package hr.foi.kokolo.emenza;

import hr.foi.kokolo.emenza.services.DatabaseUpdateService;

import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
/*
 * statasfsdgfsdhgfh
 * 
 */
public class MainActivity extends Activity {
	
	SharedPreferences myPreference;
	SharedPreferences.Editor prefsEditor;
	LocationManager locationManager;
	boolean firstRun;
	private PendingIntent pendingIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//pozivanje metode za postavljanje alarmmanagera koji okida pokretanje servisa za ažuriranje baze podataka
		startAlarm();
		
		firstRun=true;
		
		final Context context=this;
		
		myPreference = PreferenceManager.getDefaultSharedPreferences(this);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		//provjera dali je u postavkama uključeno dohvačanje lokacije
		boolean useGPS=myPreference.getBoolean("swcUseGPS", false);
		boolean showLocation=myPreference.getBoolean("chkShowMyLocation", false);
		if(showLocation && useGPS){
			// provjera da li je GPS uključen
			boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			
			//ako nije
			if (!enabled) {
				//prikazuje se dialog korisniku na kojem ga se pita želi li ga uključiti
				new AlertDialog.Builder(this)
		        .setTitle(R.string.alert_gps_title)
		        .setMessage(R.string.turn_on_gps)
		        //ako je ogovor pozitivan otvaraju se postavke telefona gdje korisnik može uključiti GPS
		        .setPositiveButton(R.string.turn_gps_yes, new DialogInterface.OnClickListener() {

		            @Override
		            public void onClick(DialogInterface dialog, int which) {

		            	Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						startActivityForResult(intent, 1);
		            	    
		            }

		        })
		        //ako je odgovor negativan postavke se postavljaju na dohvačanje lokacije preko mreže 
		        //te se provjerava radi li se o emulatoru
		        .setNegativeButton(R.string.turn_gps_no, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						prefsEditor = myPreference.edit();
						prefsEditor.putBoolean("swcUseGPS", false);
						prefsEditor.commit();
						checkIfEmulator();
						
					}
					
				})
		        .show();
				
			} 
			
		}
        
		//otvaranje mape klikom na gumb
        Button btnOpenMap=(Button) findViewById(R.id.btnMap);
        
        btnOpenMap.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// pokreće se aktivnost na kojoj se prikazuje mapa
				Intent i=new Intent(context ,MapActivity.class);
				startActivity(i);
			}
		});
        //otvaranje postavki klikom na gumb
        Button btnOpenSettings=(Button) findViewById(R.id.btnSettings);
        
        btnOpenSettings.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// pokreće se aktivnost na kojoj se prikazuju postavke
				Intent i=new Intent(context ,SettingsActivity.class);
				startActivity(i);
			}
		});
        
        Button btnStanjeXica=(Button) findViewById(R.id.btn_stanje_xica);
        
        btnStanjeXica.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i=new Intent(context ,XicaActivity.class);
				startActivity(i);
			}
		});
        
	}
	
	// metoda koja pokreće service za ažuriranje baze sa menu-ima
	private void startAlarm() {
		// provjera dal je već pokrenut alarm
		boolean alarmUp = (PendingIntent.getService(this, 0, new Intent(this, DatabaseUpdateService.class), PendingIntent.FLAG_NO_CREATE) != null);

		if (!alarmUp)
		{
			Calendar calendar = Calendar.getInstance();
			// postavljanje vremena u koje se baza automatski ažurira
			calendar.set(Calendar.HOUR_OF_DAY, 8);
			calendar.set(Calendar.MINUTE, 0);
	      	calendar.set(Calendar.SECOND, 0);
	     
	      	Intent myIntent = new Intent(MainActivity.this, DatabaseUpdateService.class);
	      	pendingIntent = PendingIntent.getService(MainActivity.this, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	     
	      	AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
	      	//alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 5*60*1000, pendingIntent);
	      	// postavljanje alarma koji ažurira bazu podataka u intervalu od jednog dana
	      	alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
		}
		    
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		// opet provjera da li je uključeno prikazivanje lokacije u postavkama
		boolean useGPS=myPreference.getBoolean("swcUseGPS", false);
		boolean showLocation=myPreference.getBoolean("chkShowMyLocation", false);
		
		if(showLocation && useGPS){
			// provjera da li je GPS ukljuèen
			boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			
			if (!enabled && !firstRun) {
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivityForResult(intent, 1);
			} 
			
		}
		else if(showLocation && !useGPS){
			// provjera radi li se o emulatoru
			checkIfEmulator();
		}
		
		firstRun=false;
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	// metoda koja provjerava da li je GPS uključen nakon povratka iz postavki telefona
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (!enabled) {
			Toast.makeText(this, "GPS nije uključen!",Toast.LENGTH_SHORT).show();
			prefsEditor = myPreference.edit();
			prefsEditor.putBoolean("swcUseGPS", false);
			prefsEditor.commit();
			checkIfEmulator();
		} else {
			Toast.makeText(this, "GPS je uključen!",Toast.LENGTH_SHORT).show();
		}
	}

	// metoda kojom se provjerava radi li se o emulatoru ili stvarnom uređaju
	public void checkIfEmulator() {
		if (android.os.Build.BRAND.toLowerCase(Locale.getDefault()).equals("generic")) {
			Toast.makeText(this, "Emulator running...Network Provider not available!", Toast.LENGTH_LONG).show();
			prefsEditor = myPreference.edit();
			prefsEditor.putBoolean("chkShowMyLocation", false);
			prefsEditor.commit();
			
		}
	}

}

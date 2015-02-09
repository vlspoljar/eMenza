package hr.foi.kokolo.emenza;

import hr.foi.kokolo.emenza.plugins.BalanceWebService;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class XicaActivity extends Activity implements OnCheckedChangeListener {
	
	SharedPreferences myPreference;
	SharedPreferences.Editor prefsEditor;
	String brojXice;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_xica);
		
		myPreference = PreferenceManager.getDefaultSharedPreferences(this);
		
		final EditText editBrojXice=(EditText) findViewById(R.id.edit_xicaBroj);
		final CheckBox zapamti = (CheckBox) findViewById(R.id.chk_zapamtiBrojXice);
		zapamti.setOnCheckedChangeListener(this);
		
		if(!myPreference.contains("chk_zapamtiBrojXice")){
			zapamti.setChecked(false);
			prefsEditor = myPreference.edit();
			prefsEditor.putBoolean("chk_zapamtiBrojXice", false);
			prefsEditor.commit();
		}
		if(!myPreference.getString("brojXice", "").equals("")){
			editBrojXice.setText(myPreference.getString("brojXice", ""));
			zapamti.setChecked(true);
		}
		
		Button provjeriStanje = (Button) findViewById(R.id.btn_provjeriStanjeXice);
		
		provjeriStanje.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				brojXice= editBrojXice.getText().toString().trim();
				int lng= String.valueOf(brojXice).length();
				
				if(brojXice.equals("")){
					
					Toast.makeText(XicaActivity.this, "Niste unijeli broj Xice", Toast.LENGTH_SHORT).show();
				}
				else if(lng<13){
					Toast.makeText(XicaActivity.this, "Unos mora imati 13 znamenaka!", Toast.LENGTH_SHORT).show();
				}
				else{
					
					LinearLayout layoutBalance=(LinearLayout) findViewById(R.id.lay_stanje);
					
					BalanceWebService balance= new BalanceWebService();
					String result = balance.getBalance(brojXice, XicaActivity.this);
					
					if(!result.equals("Error")){
						layoutBalance.setVisibility(View.VISIBLE);
						
						EditText editStanje=(EditText) findViewById(R.id.edit_stanje);
						editStanje.setEnabled(false);
						
						double stanje=Double.parseDouble(result.replace(",", "."));
						
						if(stanje<50){
							editStanje.setTextColor(getResources().getColor(R.color.red_balance));
						}
						else{
							editStanje.setTextColor(getResources().getColor(R.color.green_balance));
						}

						editStanje.setText(result+" kuna");
					}
					else{
						Toast.makeText(XicaActivity.this, "Unjeli ste neispravan broj X-ice!", Toast.LENGTH_SHORT).show();
					}
					
					
					
				}
			}
		});
		
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		EditText editBrojXice=(EditText) findViewById(R.id.edit_xicaBroj);
		String xica = editBrojXice.getText().toString().trim();
		if(isChecked){
			if(xica.equals("")){
				Toast.makeText(XicaActivity.this, "Niste unijeli broj Xice", Toast.LENGTH_SHORT).show();
				buttonView.setChecked(false);
			}
			else{
				prefsEditor = myPreference.edit();
				prefsEditor.putBoolean("chk_zapamtiBrojXice", true);
				prefsEditor.putString("brojXice", xica);
				prefsEditor.commit();
			}
			
			
		}
		else{
			prefsEditor = myPreference.edit();
			prefsEditor.putBoolean("chk_zapamtiBrojXice", false);
			prefsEditor.putString("brojXice", "");
			prefsEditor.commit();
		}
	}

}

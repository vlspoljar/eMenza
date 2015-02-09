package hr.foi.kokolo.emenza.core;

import hr.foi.kokolo.emenza.types.PoiInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

// asinkroni zadatak za dohvaæanje svih menzi sa servera
public class PoiSourceAsyncTask extends AsyncTask<Void, Void, List<PoiInfo>> {

	@Override
	protected List<PoiInfo> doInBackground(Void... params) {
		// TODO Auto-generated method stub
		
		List<PoiInfo> pois=new ArrayList<PoiInfo>();
		String result=null;
		HttpClient httpClient= new DefaultHttpClient();
		//HttpGet request= new HttpGet("http://andromeda.foi.hr:9080/Poi/poi/ws?REST=json&card="+ groupId);
		HttpGet request= new HttpGet("http://emenza.herokuapp.com/pois");
		ResponseHandler<String> handler= new BasicResponseHandler();
		
		try {
			result=httpClient.execute(request, handler);
			if(result!=""){
				int length=new JSONArray(result).length();
				for(int i=0;i<length; i++){
					// vraæeni format je u json obliku, pa se on parsira kako bi se dobili svi poi-ji
					JSONObject jpoi=new JSONArray(result).getJSONObject(i);
					PoiInfo poi= new PoiInfo(jpoi.getInt("id") ,jpoi.getString("name"), new LatLng(jpoi.getDouble("latitude"), jpoi.getDouble("longitude")));
					pois.add(poi);
				}
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		httpClient.getConnectionManager().shutdown();
		return pois;
		
	}

}

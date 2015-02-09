package hr.foi.kokolo.emenza.core;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;

// asinkroni zadatak za dohvaćanje menija za odabranu menzu
public class MenuAsyncTask extends AsyncTask<Integer, Void, String> {

	Integer id;
	@Override
	protected String doInBackground(Integer... params) {
		
		id=(Integer)params[0];
		String result=null;
		HttpClient httpClient= new DefaultHttpClient();
		HttpGet request= new HttpGet("http://emenza.herokuapp.com/menus/daily/" + id);
		ResponseHandler<String> handler= new BasicResponseHandler();
		
		try {
			result=httpClient.execute(request, handler);
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		httpClient.getConnectionManager().shutdown();
		return result;
		
	}

}



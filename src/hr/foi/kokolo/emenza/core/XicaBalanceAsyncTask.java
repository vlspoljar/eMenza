package hr.foi.kokolo.emenza.core;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class XicaBalanceAsyncTask extends AsyncTask<String, Void, String> {
	
	String cardNr;
	Context mContext;
	ProgressDialog progressDialog;
	
	public XicaBalanceAsyncTask(Context context){
		this.mContext=context;
	}
	
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		progressDialog= new ProgressDialog(mContext);
		progressDialog.setCancelable(false);
		progressDialog.setMessage("Gathering data...");
		progressDialog.setIndeterminate(true);
		progressDialog.show();
	}
	
	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		
		cardNr=(String) params[0];
		String balance=null;
		String result=null;
		HttpClient httpClient= new DefaultHttpClient();
		//HttpGet request= new HttpGet("http://andromeda.foi.hr:9080/Poi/poi/ws?REST=json&card="+ groupId);
		HttpGet request= new HttpGet("http://emenza.herokuapp.com/balance/"+cardNr);
		ResponseHandler<String> handler= new BasicResponseHandler();
		
		try {
			result=httpClient.execute(request, handler);
			if(result!=""){
				
				JSONObject jBalance = new JSONObject(result);
				balance= jBalance.getString("balance");
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
		
		return balance;
		
	}
	
	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		progressDialog.dismiss();
	}
}

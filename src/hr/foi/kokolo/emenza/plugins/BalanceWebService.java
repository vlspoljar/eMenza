package hr.foi.kokolo.emenza.plugins;

import hr.foi.kokolo.emenza.core.XicaBalanceAsyncTask;
import hr.foi.kokolo.emenza.interfaces.IBalance;

import java.util.concurrent.ExecutionException;

import android.content.Context;

public class BalanceWebService implements IBalance {

	@Override
	public String getBalance(String cardNr, Context context) {
		// TODO Auto-generated method stub
		XicaBalanceAsyncTask asyncTask= new XicaBalanceAsyncTask(context);
		
		String[] params = new String[] {cardNr};
		asyncTask.execute(params);
		
		
		String result= null;
		try {
			result=asyncTask.get();
			if(result.equals("")){
				result="Error";
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
		
		
	}

	

}

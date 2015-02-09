package hr.foi.kokolo.emenza.plugins;

import hr.foi.kokolo.emenza.core.MenuAsyncTask;
import hr.foi.kokolo.emenza.interfaces.IMenu;

import java.util.concurrent.ExecutionException;

public class MenuWebService implements IMenu {

	@Override
	public String getMenu(int id) {
		// TODO Auto-generated method stub

		MenuAsyncTask asyncTask=new MenuAsyncTask();
		
		Integer[] params = new Integer[] {id};
		asyncTask.execute(params);
		
		
		String result= null;
		try {
			result=asyncTask.get();
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

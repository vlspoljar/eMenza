package hr.foi.kokolo.emenza.plugins;

import hr.foi.kokolo.emenza.core.PoiSourceAsyncTask;
import hr.foi.kokolo.emenza.interfaces.IPoiSource;
import hr.foi.kokolo.emenza.types.PoiInfo;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class PoiSourceWebService implements IPoiSource {

	@Override
	public List<PoiInfo> getPois() {
		// TODO Auto-generated method stub

		PoiSourceAsyncTask asyncTask=new PoiSourceAsyncTask();
		
		asyncTask.execute();
		
		
		List<PoiInfo> resuslt= null;
		try {
			resuslt=asyncTask.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resuslt;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getVersion() {
		// TODO Auto-generated method stub
		return null;
	}

}

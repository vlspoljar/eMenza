package hr.foi.kokolo.emenza.types;

import com.google.android.gms.maps.model.LatLng;

// getteri i setteri za našu trenutnu lokaciju
public class MyLocationInfo {

	private static LatLng myLocation;

	public MyLocationInfo(){
		
	}
	
	public static LatLng getMyLocation() {
		return myLocation;
	}

	public static void setMyLocation(LatLng myLocation) {
		MyLocationInfo.myLocation = myLocation;
	}
}

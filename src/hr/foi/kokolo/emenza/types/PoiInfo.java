package hr.foi.kokolo.emenza.types;

import com.google.android.gms.maps.model.LatLng;

public class PoiInfo {

	private int id;
	
	private String name;
	//private String description;
	private LatLng location;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	/**public String getDescription() {
		return description;
	}*/
	public LatLng getLocation() {
		return location;
	}
	
	public PoiInfo(int id, String name, LatLng location){
		this.id=id;
		this.name=name;
		//this.description=description;
		this.location=new LatLng(location.latitude, location.longitude);
	}

}

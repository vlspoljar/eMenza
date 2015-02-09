package hr.foi.kokolo.emenza.interfaces;

import hr.foi.kokolo.emenza.types.PoiInfo;

import java.util.List;

public interface IPoiSource {

	public List<PoiInfo> getPois();
	public String getName();
	public String getVersion();
}

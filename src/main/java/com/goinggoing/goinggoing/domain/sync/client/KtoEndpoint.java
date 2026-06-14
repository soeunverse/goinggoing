package com.goinggoing.goinggoing.domain.sync.client;

public enum KtoEndpoint {
	REGIONAL_SERVICE_DEMAND("/areaTarSvcDemList"),
	REGIONAL_CULTURAL_DEMAND("/areaCulResDemList"),
	RELATED_PLACE_AREA_BASED("/areaBasedList1"),
	RELATED_PLACE_KEYWORD("/searchKeyword1");

	private final String path;

	KtoEndpoint(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}
}

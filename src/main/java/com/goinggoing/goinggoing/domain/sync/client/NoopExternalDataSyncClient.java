package com.goinggoing.goinggoing.domain.sync.client;

import org.springframework.stereotype.Component;

@Component
public class NoopExternalDataSyncClient implements ExternalDataSyncClient {

	@Override
	public ExternalSyncResult syncContents() {
		return new ExternalSyncResult(0, 0, "컨텐츠 동기화 client 준비 완료");
	}

	@Override
	public ExternalSyncResult syncRelatedPlaces() {
		return new ExternalSyncResult(0, 0, "연관 관광지 동기화 client 준비 완료");
	}

	@Override
	public ExternalSyncResult syncRegionalDemand() {
		return new ExternalSyncResult(0, 0, "지역수요 동기화 client 준비 완료");
	}
}

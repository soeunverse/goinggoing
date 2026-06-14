package com.goinggoing.goinggoing.domain.sync.client;

import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Component
@ConditionalOnProperty(name = "kto.sync.enabled", havingValue = "false", matchIfMissing = true)
public class NoopExternalDataSyncClient implements ExternalDataSyncClient {

	@Override
	public ExternalSyncResult syncRelatedPlaces() {
		return new ExternalSyncResult(0, 0, "연관 관광지 동기화 client 준비 완료");
	}

	@Override
	public ExternalSyncResult syncRegionalDemand() {
		return new ExternalSyncResult(0, 0, "지역수요 동기화 client 준비 완료");
	}
}

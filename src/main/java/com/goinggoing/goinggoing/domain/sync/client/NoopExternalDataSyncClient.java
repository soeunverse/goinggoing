package com.goinggoing.goinggoing.domain.sync.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Slf4j
@Component
@ConditionalOnProperty(name = "kto.sync.enabled", havingValue = "false", matchIfMissing = true)
public class NoopExternalDataSyncClient implements ExternalDataSyncClient {

	@Override
	public ExternalSyncResult syncRelatedPlaces() {
		log.warn("[KTO 비활성] kto.sync.enabled=false - 연관 관광지 실제 API를 호출하지 않습니다.");
		return new ExternalSyncResult(0, 0, "연관 관광지 동기화 client 준비 완료");
	}

	@Override
	public ExternalSyncResult syncRegionalDemand() {
		log.warn("[KTO 비활성] kto.sync.enabled=false - 지역 관광 수요 실제 API를 호출하지 않습니다.");
		return new ExternalSyncResult(0, 0, "지역수요 동기화 client 준비 완료");
	}
}

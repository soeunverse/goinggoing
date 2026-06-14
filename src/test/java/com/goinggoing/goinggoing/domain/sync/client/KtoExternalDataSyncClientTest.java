package com.goinggoing.goinggoing.domain.sync.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goinggoing.goinggoing.domain.category.entity.Region;
import com.goinggoing.goinggoing.domain.category.repository.RegionRepository;
import com.goinggoing.goinggoing.domain.content.entity.Content;
import com.goinggoing.goinggoing.domain.content.entity.ContentType;
import com.goinggoing.goinggoing.domain.content.repository.ContentRepository;
import com.goinggoing.goinggoing.domain.sync.entity.RegionalDemandMetric;
import com.goinggoing.goinggoing.domain.sync.entity.RelatedPlace;
import com.goinggoing.goinggoing.domain.sync.repository.RegionalDemandMetricRepository;
import com.goinggoing.goinggoing.domain.sync.repository.RelatedPlaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class KtoExternalDataSyncClientTest {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private KtoApiGateway ktoApiGateway;
	private RegionRepository regionRepository;
	private ContentRepository contentRepository;
	private RegionalDemandMetricRepository regionalDemandMetricRepository;
	private RelatedPlaceRepository relatedPlaceRepository;
	private KtoExternalDataSyncClient syncClient;

	@BeforeEach
	void setUp() {
		ktoApiGateway = mock(KtoApiGateway.class);
		regionRepository = mock(RegionRepository.class);
		contentRepository = mock(ContentRepository.class);
		regionalDemandMetricRepository = mock(RegionalDemandMetricRepository.class);
		relatedPlaceRepository = mock(RelatedPlaceRepository.class);
		syncClient = new KtoExternalDataSyncClient(
				ktoApiGateway,
				regionRepository,
				contentRepository,
				regionalDemandMetricRepository,
				relatedPlaceRepository,
				objectMapper,
				20,
				100
		);
	}

	@Test
	@DisplayName("지역별 관광 자원 수요 API 응답을 지역수요 지표로 저장한다")
	void syncRegionalDemandStoresMetrics() throws Exception {
		Region region = new Region(1L, "3", "대전", "대전광역시", 3);
		when(regionRepository.findAllByOrderByDisplayOrderAscIdAsc()).thenReturn(List.of(region));
		when(regionalDemandMetricRepository.findByRegionIdAndMetricMonth(1L, "2026-06")).thenReturn(Optional.empty());
		when(regionalDemandMetricRepository.save(any(RegionalDemandMetric.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));
		when(ktoApiGateway.fetchItems(eq(KtoEndpoint.REGIONAL_SERVICE_DEMAND), any()))
				.thenReturn(List.of(json("""
						{
						  "baseYm": "202606",
						  "tourSvcDemand": "87.5",
						  "navigationSearchScore": "71.2"
						}
						""")));
		when(ktoApiGateway.fetchItems(eq(KtoEndpoint.REGIONAL_CULTURAL_DEMAND), any()))
				.thenReturn(List.of(json("""
						{
						  "baseYm": "202606",
						  "culResDemand": "62.4",
						  "navigationSearchScore": "55.0"
						}
						""")));

		ExternalSyncResult result = syncClient.syncRegionalDemand();

		assertThat(result.importedCount()).isEqualTo(2);
		assertThat(result.failedCount()).isZero();
		verify(regionalDemandMetricRepository, times(2)).save(any(RegionalDemandMetric.class));
	}

	@Test
	@DisplayName("연관 관광지 API 응답을 기준 컨텐츠의 related_places로 저장한다")
	void syncRelatedPlacesStoresRelatedPlaces() throws Exception {
		Region region = new Region(6L, "6", "부산", "부산광역시", 6);
		Content content = new Content(
				10L,
				region,
				null,
				null,
				"광안리 해수욕장",
				ContentType.ATTRACTION,
				"부산 대표 바다",
				"광안대교 야경",
				"부산 수영구 광안해변로",
				new BigDecimal("35.1532000"),
				new BigDecimal("129.1186000"),
				null,
				0L,
				0L,
				BigDecimal.ZERO,
				true
		);
		when(contentRepository.findAllPublishedForSync()).thenReturn(List.of(content));
		when(relatedPlaceRepository.save(any(RelatedPlace.class))).thenAnswer(invocation -> invocation.getArgument(0));
		when(ktoApiGateway.fetchItems(eq(KtoEndpoint.RELATED_PLACE_KEYWORD), any()))
				.thenReturn(List.of(json("""
						{
						  "rlteTatsNm": "민락수변공원",
						  "rlteTatsType": "관광지",
						  "rlteRank": "1",
						  "rlteScore": "98.4",
						  "rlteAddr": "부산 수영구 민락동",
						  "rlteMapY": "35.1547000",
						  "rlteMapX": "129.1276000",
						  "baseYm": "202504"
						}
						""")));

		ExternalSyncResult result = syncClient.syncRelatedPlaces();

		assertThat(result.importedCount()).isEqualTo(1);
		assertThat(result.failedCount()).isZero();
		verify(relatedPlaceRepository).deleteByBaseContentId(10L);
		verify(relatedPlaceRepository).save(any(RelatedPlace.class));
	}

	private JsonNode json(String value) throws Exception {
		return objectMapper.readTree(value);
	}
}

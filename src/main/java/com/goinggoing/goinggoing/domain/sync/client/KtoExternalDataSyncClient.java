package com.goinggoing.goinggoing.domain.sync.client;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@ConditionalOnProperty(name = "kto.sync.enabled", havingValue = "true")
public class KtoExternalDataSyncClient implements ExternalDataSyncClient {

	private final KtoApiGateway ktoApiGateway;
	private final RegionRepository regionRepository;
	private final ContentRepository contentRepository;
	private final RegionalDemandMetricRepository regionalDemandMetricRepository;
	private final RelatedPlaceRepository relatedPlaceRepository;
	private final ObjectMapper objectMapper;
	private final int relatedPlaceRows;
	private final int demandRows;
	private final String baseYearMonth;

	public KtoExternalDataSyncClient(
			KtoApiGateway ktoApiGateway,
			RegionRepository regionRepository,
			ContentRepository contentRepository,
			RegionalDemandMetricRepository regionalDemandMetricRepository,
			RelatedPlaceRepository relatedPlaceRepository,
			ObjectMapper objectMapper,
			@Value("${kto.sync.related-place-rows:20}") int relatedPlaceRows,
			@Value("${kto.sync.demand-rows:100}") int demandRows,
			@Value("${kto.sync.base-ym:202504}") String baseYearMonth
	) {
		this.ktoApiGateway = ktoApiGateway;
		this.regionRepository = regionRepository;
		this.contentRepository = contentRepository;
		this.regionalDemandMetricRepository = regionalDemandMetricRepository;
		this.relatedPlaceRepository = relatedPlaceRepository;
		this.objectMapper = objectMapper;
		this.relatedPlaceRows = relatedPlaceRows;
		this.demandRows = demandRows;
		this.baseYearMonth = baseYearMonth;
	}

	@Override
	public ExternalSyncResult syncRelatedPlaces() {
		List<Content> contents = contentRepository.findAllPublishedForSync();
		int importedCount = 0;
		int failedCount = 0;

		for (Content content : contents) {
			Region region = content.getRegion();
			if (region.getAreaCode() == null || region.getAreaCode().isBlank()
					|| region.getSigunguCode() == null || region.getSigunguCode().isBlank()) {
				log.warn("[KTO 스킵] 연관 관광지 필수 지역 코드 누락 contentId={} title={} areaCd={} signguCd={}",
						content.getId(), content.getTitle(), region.getAreaCode(), region.getSigunguCode());
				continue;
			}
			try {
				List<JsonNode> items = ktoApiGateway.fetchItems(
						KtoEndpoint.RELATED_PLACE_KEYWORD,
						Map.of(
								"areaCd", region.getAreaCode(),
								"signguCd", region.getSigunguCode(),
								"baseYm", baseYearMonth,
								"keyword", content.getTitle(),
								"numOfRows", String.valueOf(relatedPlaceRows),
								"pageNo", "1"
						)
				);
				relatedPlaceRepository.deleteByBaseContentId(content.getId());
				for (int index = 0; index < items.size(); index++) {
					relatedPlaceRepository.save(toRelatedPlace(content, items.get(index), index + 1));
				}
				importedCount += items.size();
				log.info("[DB 저장] 연관 관광지 저장 baseContentId={} keyword={} count={}", content.getId(), content.getTitle(), items.size());
			} catch (RuntimeException exception) {
				failedCount++;
				log.warn("[DB 저장 실패] 연관 관광지 저장 실패 baseContentId={} keyword={} error={}", content.getId(), content.getTitle(), exception.getMessage());
			}
		}

		return new ExternalSyncResult(importedCount, failedCount, "연관 관광지 동기화 완료");
	}

	@Override
	public ExternalSyncResult syncRegionalDemand() {
		List<Region> regions = regionRepository.findAllByOrderByDisplayOrderAscIdAsc();
		int importedCount = 0;
		int failedCount = 0;

		for (Region region : regions) {
			if (region.getAreaCode() == null || region.getAreaCode().isBlank()) {
				continue;
			}
			try {
				importedCount += syncRegionalDemandEndpoint(region, KtoEndpoint.REGIONAL_SERVICE_DEMAND);
				importedCount += syncRegionalDemandEndpoint(region, KtoEndpoint.REGIONAL_CULTURAL_DEMAND);
			} catch (RuntimeException exception) {
				failedCount++;
				log.warn("[DB 저장 실패] 지역수요 저장 실패 regionId={} regionName={} error={}", region.getId(), region.getName(), exception.getMessage());
			}
		}

		return new ExternalSyncResult(importedCount, failedCount, "지역별 관광 자원 수요 동기화 완료");
	}

	private int syncRegionalDemandEndpoint(Region region, KtoEndpoint endpoint) {
		List<JsonNode> items = ktoApiGateway.fetchItems(
				endpoint,
				Map.of(
						"areaCd", region.getAreaCode(),
						"baseYm", baseYearMonth,
						"numOfRows", String.valueOf(demandRows),
						"pageNo", "1"
				)
		);

		for (JsonNode item : items) {
			String metricMonth = readText(item, "baseYm", "stdrYm", "yyyymm", "metricMonth", "baseYmd");
			if (metricMonth == null || metricMonth.isBlank()) {
				metricMonth = YearMonth.now().toString();
			}
			String normalizedMetricMonth = normalizeMetricMonth(metricMonth);
			RegionalDemandMetric metric = regionalDemandMetricRepository.findByRegionIdAndMetricMonth(region.getId(), normalizedMetricMonth)
					.orElseGet(() -> RegionalDemandMetric.create(region, normalizedMetricMonth));
			if (endpoint == KtoEndpoint.REGIONAL_SERVICE_DEMAND) {
				metric.updateServiceDemand(readDecimal(item, "tourSvcDemand", "svcDemand", "serviceDemandScore", "demandScore", "score"), readDecimal(item, "naverSrch", "navigationSearchScore", "navSearchScore"), toRawPayload(item));
			} else {
				metric.updateCulturalResource(readDecimal(item, "culResDemand", "culturalResourceScore", "demandScore", "score"), readDecimal(item, "naverSrch", "navigationSearchScore", "navSearchScore"), toRawPayload(item));
			}
			regionalDemandMetricRepository.save(metric);
		}
		log.info("[DB 저장] 지역수요 저장 regionId={} regionName={} endpoint={} count={}", region.getId(), region.getName(), endpoint, items.size());
		return items.size();
	}

	private RelatedPlace toRelatedPlace(Content baseContent, JsonNode item, int rank) {
		return new RelatedPlace(
				baseContent,
				readText(item, "rlteTatsNm", "relatedPlaceName", "rlteNm", "title", "name") == null ? "이름 미상" : readText(item, "rlteTatsNm", "relatedPlaceName", "rlteNm", "title", "name"),
				mapContentType(readText(item, "rlteTatsType", "rlteCtgryLclsNm", "contentType", "type")),
				readInteger(item, rank, "rlteRank", "rank", "rnum"),
				readDecimal(item, "rlteScore", "relationScore", "score"),
				readText(item, "rlteAddr", "addr", "address"),
				readDecimal(item, "rlteMapY", "mapY", "latitude", "lat"),
				readDecimal(item, "rlteMapX", "mapX", "longitude", "lng", "lon"),
				readText(item, "baseYm", "period", "sourcePeriod"),
				toRawPayload(item)
		);
	}

	private ContentType mapContentType(String value) {
		if (value == null) {
			return ContentType.ETC;
		}
		if (value.contains("음식") || value.contains("식당") || value.equalsIgnoreCase("RESTAURANT")) {
			return ContentType.RESTAURANT;
		}
		if (value.contains("숙박") || value.equalsIgnoreCase("ACCOMMODATION")) {
			return ContentType.ACCOMMODATION;
		}
		if (value.contains("쇼핑") || value.equalsIgnoreCase("SHOPPING")) {
			return ContentType.SHOPPING;
		}
		if (value.contains("카페") || value.equalsIgnoreCase("CAFE")) {
			return ContentType.CAFE;
		}
		if (value.contains("축제") || value.equalsIgnoreCase("FESTIVAL")) {
			return ContentType.FESTIVAL;
		}
		if (value.contains("관광") || value.equalsIgnoreCase("ATTRACTION")) {
			return ContentType.ATTRACTION;
		}
		return ContentType.ETC;
	}

	private String normalizeMetricMonth(String value) {
		String digits = value.replaceAll("[^0-9]", "");
		if (digits.length() >= 6) {
			return digits.substring(0, 4) + "-" + digits.substring(4, 6);
		}
		return value;
	}

	private String readText(JsonNode item, String... fieldNames) {
		for (String fieldName : fieldNames) {
			JsonNode value = item.get(fieldName);
			if (value != null && !value.isNull() && !value.asText().isBlank()) {
				return value.asText();
			}
		}
		return null;
	}

	private Integer readInteger(JsonNode item, int defaultValue, String... fieldNames) {
		for (String fieldName : fieldNames) {
			JsonNode value = item.get(fieldName);
			if (value != null && value.canConvertToInt()) {
				return value.asInt();
			}
		}
		return defaultValue;
	}

	private BigDecimal readDecimal(JsonNode item, String... fieldNames) {
		for (String fieldName : fieldNames) {
			JsonNode value = item.get(fieldName);
			if (value != null && !value.isNull() && !value.asText().isBlank()) {
				try {
					return new BigDecimal(value.asText());
				} catch (NumberFormatException ignored) {
					return null;
				}
			}
		}
		return null;
	}

	private String toRawPayload(JsonNode item) {
		try {
			return objectMapper.writeValueAsString(item);
		} catch (JsonProcessingException exception) {
			return "{}";
		}
	}
}

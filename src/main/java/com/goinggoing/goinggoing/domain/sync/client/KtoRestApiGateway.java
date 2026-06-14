package com.goinggoing.goinggoing.domain.sync.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@ConditionalOnProperty(name = "kto.sync.enabled", havingValue = "true")
public class KtoRestApiGateway implements KtoApiGateway {

	private final RestClient restClient;
	private final ObjectMapper objectMapper;
	private final String serviceKey;
	private final String regionalDemandBaseUrl;
	private final String relatedPlaceBaseUrl;

	public KtoRestApiGateway(
			RestClient.Builder restClientBuilder,
			ObjectMapper objectMapper,
			@Value("${kto.service-key}") String serviceKey,
			@Value("${kto.regional-demand.base-url:https://apis.data.go.kr/B551011/AreaTarResDemService}") String regionalDemandBaseUrl,
			@Value("${kto.related-place.base-url:https://apis.data.go.kr/B551011/TarRlteTarService1}") String relatedPlaceBaseUrl
	) {
		this.restClient = restClientBuilder.build();
		this.objectMapper = objectMapper;
		this.serviceKey = normalizeServiceKey(serviceKey);
		this.regionalDemandBaseUrl = regionalDemandBaseUrl;
		this.relatedPlaceBaseUrl = relatedPlaceBaseUrl;
	}

	@Override
	public List<JsonNode> fetchItems(KtoEndpoint endpoint, Map<String, String> queryParameters) {
		log.info("[KTO 요청] endpoint={} params={}", endpoint, sanitizeQueryParameters(queryParameters));
		String responseBody = restClient.get()
				.uri(uriBuilder -> buildUri(uriBuilder, endpoint, queryParameters))
				.retrieve()
				.body(String.class);
		log.info("[KTO 응답 원문] endpoint={} body={}", endpoint, abbreviate(responseBody));
		List<JsonNode> items = extractItems(responseBody);
		log.info("[KTO 응답] endpoint={} itemCount={}", endpoint, items.size());
		return items;
	}

	private URI buildUri(UriBuilder uriBuilder, KtoEndpoint endpoint, Map<String, String> queryParameters) {
		UriBuilder builder = uriBuilder
				.scheme("https")
				.host(resolveHost(endpoint))
				.path(resolvePathPrefix(endpoint))
				.path(endpoint.getPath())
				.queryParam("serviceKey", serviceKey)
				.queryParam("numOfRows", queryParameters.getOrDefault("numOfRows", "100"))
				.queryParam("pageNo", queryParameters.getOrDefault("pageNo", "1"))
				.queryParam("MobileOS", queryParameters.getOrDefault("MobileOS", "ETC"))
				.queryParam("MobileApp", queryParameters.getOrDefault("MobileApp", "goinggoing"))
				.queryParam("_type", "json");

		queryParameters.forEach((key, value) -> {
			if (!isDefaultQueryParameter(key) && value != null && !value.isBlank()) {
				builder.queryParam(key, value);
			}
		});
		return builder.build();
	}

	private boolean isDefaultQueryParameter(String key) {
		return "numOfRows".equals(key)
				|| "pageNo".equals(key)
				|| "MobileOS".equals(key)
				|| "MobileApp".equals(key);
	}

	private String resolveHost(KtoEndpoint endpoint) {
		String baseUrl = resolveBaseUrl(endpoint).replace("https://", "").replace("http://", "");
		int slashIndex = baseUrl.indexOf('/');
		if (slashIndex < 0) {
			return baseUrl;
		}
		return baseUrl.substring(0, slashIndex);
	}

	private String resolvePathPrefix(KtoEndpoint endpoint) {
		String baseUrl = resolveBaseUrl(endpoint).replace("https://", "").replace("http://", "");
		int slashIndex = baseUrl.indexOf('/');
		if (slashIndex < 0) {
			return "";
		}
		return "/" + baseUrl.substring(slashIndex + 1);
	}

	private String resolveBaseUrl(KtoEndpoint endpoint) {
		if (endpoint == KtoEndpoint.RELATED_PLACE_AREA_BASED || endpoint == KtoEndpoint.RELATED_PLACE_KEYWORD) {
			return relatedPlaceBaseUrl;
		}
		return regionalDemandBaseUrl;
	}

	private List<JsonNode> extractItems(String responseBody) {
		try {
			JsonNode root = objectMapper.readTree(responseBody);
			JsonNode header = root.path("response").path("header");
			if (!header.isMissingNode()) {
				String resultCode = header.path("resultCode").asText();
				String resultMessage = header.path("resultMsg").asText();
				if (!resultCode.isBlank() && !"0000".equals(resultCode)) {
					log.warn("[KTO 오류 응답] resultCode={} resultMessage={}", resultCode, resultMessage);
				}
			}
			JsonNode itemNode = root.path("response").path("body").path("items").path("item");
			if (itemNode.isMissingNode()) {
				itemNode = root.path("body").path("items").path("item");
			}
			if (itemNode.isArray()) {
				List<JsonNode> items = new ArrayList<>();
				Iterator<JsonNode> iterator = itemNode.elements();
				while (iterator.hasNext()) {
					items.add(iterator.next());
				}
				return items;
			}
			if (itemNode.isObject()) {
				return List.of(itemNode);
			}
			return List.of();
		} catch (Exception exception) {
			throw new IllegalStateException("한국관광공사 API 응답 파싱에 실패했습니다.", exception);
		}
	}

	private Map<String, String> sanitizeQueryParameters(Map<String, String> queryParameters) {
		return queryParameters;
	}

	private String normalizeServiceKey(String serviceKey) {
		if (serviceKey == null || serviceKey.isBlank()) {
			return serviceKey;
		}
		if (!serviceKey.contains("%")) {
			return serviceKey;
		}
		return URLDecoder.decode(serviceKey, StandardCharsets.UTF_8);
	}

	private String abbreviate(String responseBody) {
		if (responseBody == null) {
			return "";
		}
		int maxLength = 1000;
		if (responseBody.length() <= maxLength) {
			return responseBody;
		}
		return responseBody.substring(0, maxLength) + "...";
	}
}
